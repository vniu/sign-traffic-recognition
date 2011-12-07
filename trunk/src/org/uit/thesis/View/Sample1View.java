package org.uit.thesis.View;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.uit.thesis.Object.CaptureObjectLayer;
import org.uit.thesis.Object.DetectObjectLayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceHolder;

class Sample1View extends CaptureObjectLayer {
    private Mat mYuv;
    private Mat mRgba;
    private Mat mGraySubmat;
    private Mat mIntermediateMat;
    DetectObjectLayer detect;
    
    public Sample1View(Context context) {
        super(context);
        detect=new DetectObjectLayer();
    }

    @Override
    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) {
        super.surfaceChanged(_holder, format, width, height);

        synchronized (this) {
            // initialize Mats before usage
            mYuv = new Mat(getFrameHeight() + getFrameHeight() / 2, getFrameWidth(), CvType.CV_8UC1);
            mGraySubmat = mYuv.submat(0, getFrameHeight(), 0, getFrameWidth());

            mRgba = new Mat();
            mIntermediateMat = new Mat();
        }
    }

    @Override
    protected Bitmap processFrame(byte[] data) {
        mYuv.put(0, 0, data);
        detect.setData(data, getFrameWidth(), getFrameHeight());
        mRgba=detect.getData();
        
            //Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
            //Core.putText(mRgba, "OpenCV + Android", new Point(10, 100), 3/* CV_FONT_HERSHEY_COMPLEX */, 1, new Scalar(255, 0, 0, 255), 3);
 
            Bitmap bmp = Bitmap.createBitmap(getFrameWidth(), getFrameHeight(), Bitmap.Config.ARGB_8888);

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
            if (mYuv != null)
                mYuv.release();
            if (mRgba != null)
                mRgba.release();
            if (mGraySubmat != null)
                mGraySubmat.release();
            if (mIntermediateMat != null)
                mIntermediateMat.release();

            mYuv = null;
            mRgba = null;
            mGraySubmat = null;
            mIntermediateMat = null;
        }
    }
}

