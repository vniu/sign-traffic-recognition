package org.uit.thesis.View;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.uit.thesis.Object.CaptureObjectLayer;
import org.uit.thesis.Object.DetectObjectLayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceHolder;

public class CameraViewLayer extends CaptureObjectLayer{
	DetectObjectLayer detect;
	List<Rect> boxList;
	Mat mRgba;
	Mat mZoom;
	Mat mTemp;
	Mat mYuv;
	
	public CameraViewLayer(Context context) {
        super(context);
        detect=new DetectObjectLayer();
        boxList=new ArrayList<Rect>();
    }

    @Override
    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) {
        super.surfaceChanged(_holder, format, width, height);

        synchronized (this) {
        	mRgba = new Mat();
        	mZoom=new Mat();
        	mTemp=new Mat();
        	mYuv = new Mat(getFrameHeight() + getFrameHeight() / 2, getFrameWidth(), CvType.CV_8UC1);
        }
    }

    private void CreateAuxiliaryMats(float rate) {
        if (mRgba.empty())
            return;

        int rows = mRgba.rows();
        int cols = mRgba.cols();

        int x=(int)((cols- cols / rate)/2);
        int y=(int)((rows- rows / rate)/2);
       
        if (mZoom == null)
        	//mZoom = mTemp.submat(y, y + (int)(rows/rate), x, x + (int)(cols/rate));
            mZoom = mTemp.submat(rows / 2 - rows / 5, rows / 2 + rows / 5, cols / 2 - cols / 5, cols / 2 + cols / 5);
    }
    
    @Override
    protected Bitmap processFrame(byte[] data) {
    	if(counter>10) counter=0;
    	else counter++;
    	
    	Log.i("Counter", "Counter is "+counter);
    	//get new data
//    	detect.setData(data, getFrameWidth(), getFrameHeight());
//    	mRgba=detect.getData();
    	
    	mYuv.put(0, 0, data);
    	Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
    	mRgba.copyTo(mTemp);
    	
    	if (mZoom == null)
           	CreateAuxiliaryMats(2);
    	//Imgproc.resize(mZoom, mRgba, mRgba.size());
    	
    	//mRgba=detect.getTestAlgorithm();
    	    	
//    	if(counter==0){
//    	   	detect.detectAllSign();
//	    	boxList.clear();
//	    	boxList=detect.getBoxList();
//    	  	    
//    	//draw 
//	      	int n=boxList.size();
//	      	if(n>0) flag=false;
//	      	
//	      	for(int i=0;i<n;i++){
//	      		Rect r=boxList.get(i);
//	      		Core.rectangle(mRgba, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 3);
//	      	}
//    	}
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
            if (mRgba != null)
                mRgba.release();
            if (mYuv != null)
                mYuv.release();
            if(mZoom!=null)
            	mZoom.release();
            if(mTemp!=null)
            	mTemp.release();
            
            mYuv = null;
            mRgba = null;
            mZoom=null;
            mTemp=null;
        }
    }
}
