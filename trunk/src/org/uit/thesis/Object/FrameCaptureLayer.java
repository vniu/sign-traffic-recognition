package org.uit.thesis.Object;

import java.util.List;

import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.uit.thesis.View.MainActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class FrameCaptureLayer extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = "TSRMobileSystem::FrameCaptureLayer";

    private SurfaceHolder       mHolder;
    private VideoCapture        mCamera;
    private FpsMeter            mFps;
    private boolean             mThreadRun;
    protected static int		counter;
    public static boolean	flag;
    protected Handler mHandler;
    
    public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		FrameCaptureLayer.flag = flag;
	}

	public FrameCaptureLayer(Context context,Handler h) {
        super(context);
        mHandler=h;
        counter=0;
        flag=true;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mFps = new FpsMeter();
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) {
        Log.i(TAG, "surfaceCreated");
        synchronized (this) {
            if (mCamera != null && mCamera.isOpened()) {
                Log.i(TAG, "before mCamera.getSupportedPreviewSizes()");
                List<Size> sizes = mCamera.getSupportedPreviewSizes();
                Log.i(TAG, "after mCamera.getSupportedPreviewSizes()");
                int mFrameWidth = width;
                int mFrameHeight = height;

                // selecting optimal camera preview size
                {
                    double minDiff = Double.MAX_VALUE;
                    for (Size size : sizes) {
                        if (Math.abs(size.height - height) < minDiff) {
                            mFrameWidth = (int) size.width;
                            mFrameHeight = (int) size.height;
                            minDiff = Math.abs(size.height - height);
                        }
                    }
                }

                mCamera.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, mFrameWidth);
                mCamera.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, mFrameHeight);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        mCamera = new VideoCapture(Highgui.CV_CAP_ANDROID);
        if (mCamera.isOpened()) {
            (new Thread(this)).start();
        } else {
            mCamera.release();
            mCamera = null;
            Log.e(TAG, "Failed to open native camera");
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        mThreadRun = false;
        if (mCamera != null) {
            synchronized (this) {
                mCamera.release();
                mCamera = null;
            }
        }
    }

    protected abstract Bitmap processFrame(VideoCapture capture);

//    private void savePic(Bitmap bmp){
//    	try {
//	        String path = Environment.getExternalStorageDirectory().toString();
//	        OutputStream fOut = null;
//	        File file = new File(path, "camture.jpg");
//	        fOut = new FileOutputStream(file);
//	        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
//	        fOut.flush();
//	        fOut.close();
//	    }
//	    catch (Exception e) {
//	        e.printStackTrace();
//	    }
//   	
//    }
    
    public void run() {
        Log.i(TAG, "Starting processing thread");
        mFps.init();
        mThreadRun = true;
        while (mThreadRun) {
            Bitmap bmp = null;

            synchronized (this) {
                if (mCamera == null)
                    break;

                if (!mCamera.grab()) {
                    Log.e(TAG, "mCamera.grab() failed");
                    break;
                }
                if(flag)
                    bmp = processFrame(mCamera);

                mFps.measure();
            }

            if (bmp != null) {
               	    Canvas canvas = mHolder.lockCanvas();
	                if (canvas != null) {
	                    canvas.drawBitmap(bmp, (canvas.getWidth() - bmp.getWidth()) / 2, (canvas.getHeight() - bmp.getHeight()) / 2, null);
	                    
	                    if(MainActivity.viewMode==MainActivity.VIEW_MODE_AUTO)
	                    mFps.draw(canvas, (canvas.getWidth() - bmp.getWidth()) / 2, 0);
	                    
	                    mHolder.unlockCanvasAndPost(canvas);
	                }
            	bmp.recycle();
            }
        }

        Log.i(TAG, "Finishing processing thread");
    }
}