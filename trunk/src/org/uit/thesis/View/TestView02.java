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
import org.uit.thesis.Object.VideoObjectLayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceHolder;

public class TestView02 extends VideoObjectLayer{
	DetectObjectLayer detect;
	List<Rect> boxList;
	Mat mRgba;
		
	public TestView02(Context context) {
        super(context);
        detect=new DetectObjectLayer();
        boxList=new ArrayList<Rect>();
    }

    @Override
    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) {
        super.surfaceChanged(_holder, format, width, height);

        synchronized (this) {
        	mRgba = new Mat();
        }
    }

    @Override
    protected Bitmap processFrame(VideoCapture capture) {
    	if(counter>10) counter=0;
    	else counter++;
    	
    	Log.i("Counter", "Counter is "+counter);
    	//get new data
    	capture.retrieve(mRgba, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
    	detect.setData(mRgba);
    	    	    	
    	if(counter==0){
    	   	detect.detectAllSign();
	    	boxList.clear();
	    	boxList=detect.getBoxList();
    	  	    
    	//draw 
	      	int n=boxList.size();
	      	if(n>0) flag=false;
	      	
	      	for(int i=0;i<n;i++){
	      		Rect r=boxList.get(i);
	      		Core.rectangle(mRgba, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 3);
	      	}
    	}
        Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
        
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
                       
            mRgba = null;
           
        }
    }
}
