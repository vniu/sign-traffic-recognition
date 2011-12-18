package org.uit.thesis.View;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.uit.thesis.Object.DetectObjectLayer;
import org.uit.thesis.Object.FrameCaptureLayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceHolder;

class FrameViewLayer extends FrameCaptureLayer {
    private Mat mRgba;
    private Mat mZoomWindow;
    private Mat mTemp;
    DetectObjectLayer detect;
	public List<Rect> boxList;
	public List<Mat>  signList;
	    
    public FrameViewLayer(Context context) {
        super(context);
        detect=new DetectObjectLayer();
        boxList=new ArrayList<Rect>();
        signList=new ArrayList<Mat>();
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
        //Zoom is too slow, so try to find a new approach method 
        //Imgproc.resize(mZoomWindow, mRgba, mRgba.size());
        
        if(MainActivity.viewMode==MainActivity.VIEW_MODE_AUTO){
        	//Try to detect in real-time mode
        	
        }else{
        	if(MainActivity.taking_pic){
        		flag=false;
        		detect.setData(mRgba);
        		
        		//Detect
        	   	detect.detectAllSign();
    	    	boxList.clear();
    	    	boxList=detect.getBoxList();
    	    	signList.clear();
    	    	signList=detect.getSignList();
    	    	
    	    	//draw
    	    	int n=boxList.size();
	    	      	for(int i=0;i<n;i++){
			      		Rect r=boxList.get(i);
			      		Core.rectangle(mRgba, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 3);
	    	      	}
        	}
        }
        
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
