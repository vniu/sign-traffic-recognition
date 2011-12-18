package org.uit.thesis.View;

import org.uit.thesis.Object.*;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceHolder;

class FrameViewLayer extends FrameCaptureLayer {
    private Mat mRgba;
    private Mat mZoomWindow;
    private Mat mTemp;
    
    public FrameViewLayer(Context context) {
        super(context);
    }

    @Override
    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) {
        super.surfaceChanged(_holder, format, width, height);

        synchronized (this) {
            // initialize Mats before usage
            mRgba = new Mat();
            mTemp = new Mat();
        }
    }

    private void CreateAuxiliaryMats(float rate) {
        if (mRgba.empty())
            return;
               
        int rows = mRgba.rows();
        int cols = mRgba.cols();
        
        int x=(int)((cols- cols / rate)/2);
        int y=(int)((rows- rows / rate)/2);
       
        //if (mZoomWindow == null)
        mZoomWindow = mTemp.submat(y, y + (int)(rows/rate), x, x + (int)(cols/rate));
                	
        MainActivity.change_focus=false; 	
    }

    
    @Override
    protected Bitmap processFrame(VideoCapture capture) {
        capture.retrieve(mRgba, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
        Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);

        mRgba.copyTo(mTemp);
        if (MainActivity.change_focus)
            CreateAuxiliaryMats(MainActivity.focus);
        Log.i("ZoomInFrame","ZoomFrame_Focus: " + MainActivity.focus);
        
        //Imgproc.resize(mZoomWindow, mRgba, mRgba.size());
        
        if (Utils.matToBitmap(mRgba, bmp))
            return bmp;

        bmp.recycle();
        return null;
    }

    @Override
    public void run() {
        super.run();

        synchronized (this) {
            // Explicitly deallocate Mats
            if (mRgba != null)
                mRgba.release();
            if (mZoomWindow != null)
                mZoomWindow.release();
            if(mTemp != null)
            	mTemp.release();
            
            mRgba = null;
            mZoomWindow=null;
            mTemp=null;
        }
    }
}
