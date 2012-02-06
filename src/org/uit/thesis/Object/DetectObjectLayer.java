package org.uit.thesis.Object;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;


public class DetectObjectLayer {
	private Bitmap image;
	private List<Mat> signList;
	private List<Rect> boxList;
	private Mat hierarchy;
	
	public DetectObjectLayer(Bitmap image){
		this.image=image;
		this.reset();
	}
	
	public DetectObjectLayer(){
		this.image=null;
		this.reset();
	}
	
	public void setData(byte[] data,int width, int height){
		Mat mYuv=new Mat(height + height / 2, width, CvType.CV_8UC1);
		Mat mRgba=new Mat();
		mYuv.put(0, 0, data);
		
		Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
		this.image=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		Utils.matToBitmap(mRgba, this.image);
		
		mYuv.release();
		mRgba.release();
		this.reset();
	}
	
	public void setData(Mat mRgba){
		this.image=Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(mRgba, this.image);
		this.reset();
	}
	
	public Mat getData(){
		return Utils.bitmapToMat(this.image);
	}
	
	public Mat getTestAlgorithm(){
		//Init temp variable
		//Mat result=new Mat();
		
		//List<Mat> contours=new ArrayList<Mat>();
		Mat mRGBA=new Mat();
		Mat mRGB=new Mat();
		Mat mTemp=new Mat();
		List<Mat> lHSV=new ArrayList<Mat>();
		
		//Get data from Bitmap
		mRGBA=Utils.bitmapToMat(image);
		Imgproc.GaussianBlur(mRGBA,mRGBA,new Size(5, 5),1.5,1.5);
	    Imgproc.cvtColor(mRGBA,mRGB,Imgproc.COLOR_RGBA2RGB);
	    Imgproc.cvtColor(mRGB,mTemp,Imgproc.COLOR_RGB2HSV);
	    Core.split(mTemp,lHSV);
	    
	    //Filter the 3 channels HSV to get blue mask
	    
	    //Filter H channel
	    mTemp=new Mat();
	    Core.inRange(lHSV.get(0), new Scalar(90), new Scalar(130), mTemp);
	    lHSV.set(0, mTemp);
	    
	    //Filter S channel
	    mTemp=new Mat();
	    Imgproc.threshold(lHSV.get(1), mTemp, 10, 255, Imgproc.THRESH_BINARY);
	    lHSV.set(1, mTemp);
	    Core.bitwise_and(lHSV.get(0), lHSV.get(1), mTemp);
	    
	    //Filter V channel
	    lHSV.set(0, mTemp);
	    mTemp=new Mat();
	    Imgproc.threshold(lHSV.get(2), mTemp, 100, 255, Imgproc.THRESH_BINARY);
	    lHSV.set(2, mTemp);
	    Core.bitwise_and(lHSV.get(0), lHSV.get(2), mTemp);
//	    
//	    //Use Canny
//	    Imgproc.dilate(mTemp, mTemp, new Mat(), new Point(-1, -1), 1);
//	    Imgproc.erode(mTemp, mTemp, new Mat(), new Point(-1, -1), 1);
//	    Imgproc.Canny(mTemp, mTemp, 100, 50);
//	    
//	    //Find contour
//	    hierarchy = new Mat();
//	    Imgproc.findContours(mTemp, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
//	    
//		return contours;
	    //Imgproc.cvtColor(mTemp,result,Imgproc.COLOR_GRAY2RGBA);
		return mTemp;
	}
		
	public void reset(){
		signList=new ArrayList<Mat>();
		boxList=new ArrayList<Rect>();
	}
	
	public List<Mat> getSignList(){
		return signList;
	}
	
	public List<Rect> getBoxList(){
		return boxList;
	}
	
 	public void detectAllSign(){
 		detectRedCircleSign();
 		//detectBlueCircleSign();
 		//detectRedTriangleSign();
	}
	
	public void detectRedCircleSign(){
		for(int i=30;i<=145;i=i+10){
			List<Mat> contours=getContoursRedMask(i);
			if(contours.size()>0)
				FindCircle(contours, 0);
			if(signList.size()>0){
				Log.i("DetectObjectLayer_detectRedCircleSign", "Saturation value "+i);
				return;
			}
		}
	}
	
	public void detectBlueCircleSign(){
		List<Mat> contours=getContoursBlueMask();
		if(contours.size()>0)
			FindCircle(contours, 0);
	}
	
	public void detectRedTriangleSign(){
		List<Mat> contours=getContoursRedMask(60);
		if(contours.size()>0)
			FindTriangle(contours, 0);
	}
	
	public List<Mat> getContoursBlueMask(){
		//Init temp variable
		List<Mat> contours=new ArrayList<Mat>();
		Mat mRGBA=new Mat();
		Mat mRGB=new Mat();
		Mat mTemp=new Mat();
		List<Mat> lHSV=new ArrayList<Mat>();
		
		//Get data from Bitmap
		mRGBA=Utils.bitmapToMat(image);
		Imgproc.GaussianBlur(mRGBA,mRGBA,new Size(5, 5),1.5,1.5);
	    Imgproc.cvtColor(mRGBA,mRGB,Imgproc.COLOR_RGBA2RGB);
	    Imgproc.cvtColor(mRGB,mTemp,Imgproc.COLOR_RGB2HSV);
	    Core.split(mTemp,lHSV);
	    
	    //Filter the 3 channels HSV to get blue mask
	    
	    //Filter H channel
	    mTemp=new Mat();
	    Core.inRange(lHSV.get(0), new Scalar(90), new Scalar(130), mTemp);
	    lHSV.set(0, mTemp);
	    
	    //Filter S channel
	    mTemp=new Mat();
	    Imgproc.threshold(lHSV.get(1), mTemp, 10, 255, Imgproc.THRESH_BINARY);
	    lHSV.set(1, mTemp);
	    Core.bitwise_and(lHSV.get(0), lHSV.get(1), mTemp);
	    
	    //Filter V channel
	    lHSV.set(0, mTemp);
	    mTemp=new Mat();
	    Imgproc.threshold(lHSV.get(2), mTemp, 100, 255, Imgproc.THRESH_BINARY);
	    lHSV.set(2, mTemp);
	    Core.bitwise_and(lHSV.get(0), lHSV.get(2), mTemp);
	    
	    //Use Canny
	    Imgproc.dilate(mTemp, mTemp, new Mat(), new Point(-1, -1), 1);
	    Imgproc.erode(mTemp, mTemp, new Mat(), new Point(-1, -1), 1);
	    Imgproc.Canny(mTemp, mTemp, 100, 50);
	    
	    //Find contour
	    hierarchy = new Mat();
	    Imgproc.findContours(mTemp, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
	    
		return contours;
	}
	
	public List<Mat> getContoursRedMask(int i){
		//Init temp variable
		List<Mat> contours=new ArrayList<Mat>();
		Mat mRGBA=new Mat();
		Mat mRGB=new Mat();
		Mat mTemp=new Mat();
		List<Mat> lHSV=new ArrayList<Mat>();
		
		//Get data from Bitmap
		mRGBA=Utils.bitmapToMat(image);
		Imgproc.GaussianBlur(mRGBA,mRGBA,new Size(5, 5),3.5,3.5);
	    Imgproc.cvtColor(mRGBA,mRGB,Imgproc.COLOR_RGBA2RGB);
	    Imgproc.cvtColor(mRGB,mTemp,Imgproc.COLOR_RGB2HSV);
	    Core.split(mTemp,lHSV);
	    
	    //Filter the 3 channels HSV to get red mask
	    
	    //Filter H channel
	    mTemp=new Mat();
	    Core.inRange(lHSV.get(0), new Scalar(10), new Scalar(170), mTemp);
	    Core.bitwise_not(mTemp, mTemp);
	    lHSV.set(0, mTemp);
	    
	    //Filter S channel
	    mTemp=new Mat();
	    Imgproc.threshold(lHSV.get(1), mTemp, i, 255, Imgproc.THRESH_BINARY);
	    lHSV.set(1, mTemp);
	    Core.bitwise_and(lHSV.get(0), lHSV.get(1), mTemp);
	    
	    //Filter V channel
//	    lHSV.set(0, mTemp);
//	    mTemp=new Mat();
//	    Imgproc.threshold(lHSV.get(2), mTemp, 150, 255, Imgproc.THRESH_BINARY);
//	    lHSV.set(2, mTemp);
//	    Core.bitwise_and(lHSV.get(0), lHSV.get(2), mTemp);
	    
	    //Use Canny
	    Imgproc.dilate(mTemp, mTemp, new Mat(), new Point(-1, -1), 3);
	    Imgproc.erode(mTemp, mTemp, new Mat(), new Point(-1, -1), 3);
	    Imgproc.Canny(mTemp, mTemp, 100, 50);
	    
	    //Find contour
	    hierarchy = new Mat();
	    Imgproc.findContours(mTemp, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
	    
		return contours;
	}
	
	private void FindCircle(List<Mat> contours, int index){
		int i=index;
    	do
	    {
    		int buff[] = new int[4];
		    hierarchy.get(0, i, buff);
		    
		    //Get contour form list
		    Mat contour=contours.get(i);
		    int id=i;
		    //Get the next id contour
		    i=buff[0];
		    
		    //Check if this is a circle
		    if(Imgproc.contourArea(contour)>200)
		    {
		    	//Get all the point of this contour
		    	List<Point> points=new ArrayList<Point>();
		    	int num = (int) contour.total(); 
		    	int temp[] = new int[num*2]; 
		    	contour.get(0, 0, temp);
		    	
		    	for(int j=0;j<num*2;j=j+2)
		    		points.add(new Point(temp[j], temp[j+1]));
		    	
		    	//Get the bound of eclipse
		    	RotatedRect bound=Imgproc.fitEllipse(points);
		    	double pi = Imgproc.contourArea(contour) / ((bound.size.height / 2) * (bound.size.width / 2));
		    	
		    	//Check if pi ~ 3.14
                if (Math.abs(pi - 3.14) > 0.03)
                {
                	int k=buff[2];
                	if (k!=-1)
                        FindCircle(contours,k);
                    continue;
                }
                
                //Get the bound of contour
                Rect box=Imgproc.boundingRect(points);
                Mat candidate=((Utils.bitmapToMat(image)).submat(box)).clone();
                                  
        	    //Get mask of contour
        	    Mat mask=new Mat(box.size(),candidate.type(), new Scalar(0,0,0));
        	    //Draw contour        	    
                Imgproc.drawContours(mask, contours, id, new Scalar(255,255,255), -1 , 8, hierarchy, 0, new Point(-box.x,-box.y));
                Mat roi=new Mat(candidate.size(), candidate.type(), new Scalar(255,255,255));
        	    candidate.copyTo(roi, mask);
        	    
        	    //Get the 2 Axis of eclipse
		    	double longAxis;
                double shortAxis;

	                if (bound.size.height < bound.size.width)
	                {
	                    shortAxis = bound.size.height / 2;
	                    longAxis = bound.size.width / 2;
	                }
	                else
	                {
	                    shortAxis = bound.size.width / 2;
	                    longAxis = bound.size.height / 2;
	                }
	        	    
	                if ((longAxis / shortAxis) < 2.0)
	                {
	                	signList.add(roi);
	                	boxList.add(box);
	                }
            }
		    
	    }while(i!=-1);
	}
	
	private void FindTriangle(List<Mat> contours, int index){
		int i=index;
    	do
	    {
    		int buff[] = new int[4];
		    hierarchy.get(0, i, buff);
		    
		    //Get contour form list
		    Mat contour=contours.get(i);
		    int id=i;
		    
		    //Get all the point of this contour
	    	List<Point> points=new ArrayList<Point>();
	    	int num = (int) contour.total(); 
	    	int temp[] = new int[num*2]; 
	    	contour.get(0, 0, temp);
	    	
	    	for(int j=0;j<num*2;j=j+2)
	    		points.add(new Point(temp[j], temp[j+1]));
		    
	    	//Approximate the contour
	    	contour=new Mat();
		    Imgproc.approxPolyDP(contours.get(i), contour, Imgproc.arcLength(points, true)* 0.03, true);
		    
		    //Get the next id contour
		    i=buff[0];
		    
		    //Check if this is a triangle
		    if(Imgproc.contourArea(contour)>200){
		    	if(contour.total()==3){
		    		//Get the bound of contour
		    		points=new ArrayList<Point>();
			    	temp = new int[6]; 
			    	contour.get(0, 0, temp);
			    	
			    	for(int j=0;j<6;j=j+2)
			    		points.add(new Point(temp[j], temp[j+1]));
		    		
		    		Rect box=Imgproc.boundingRect(points);
	                Mat candidate=((Utils.bitmapToMat(image)).submat(box)).clone();
	                                  
	        	    //Get mask of contour
	        	    Mat mask=new Mat(box.size(),candidate.type(), new Scalar(0,0,0));
	        	    //Draw contour        	    
	                Imgproc.drawContours(mask, contours, id, new Scalar(255,255,255), -1 , 8, hierarchy, 0, new Point(-box.x,-box.y));
	                Mat roi=new Mat(candidate.size(), candidate.type(), new Scalar(255,255,255));
	        	    candidate.copyTo(roi, mask);
	        	    
	        	    signList.add(roi);
                	boxList.add(box);
		    	}
		    	else{
		    		int k=buff[2];
                	if (k!=-1)
                		FindTriangle(contours,k);
                }
		    }
		    
	    }while(i!=-1);
	}
	
	public double[] toData(Bitmap bm) throws Exception
	{
		if(bm.getHeight()!=30||bm.getWidth()!=30)
			throw new Exception("Image size must be 30*30");
		
		List<Double> Data = new ArrayList<Double>();
		
        double[][] temp=new double[30][30];
        int pixel;
        double total_R=0, total_G=0, total_B=0, total_temp=0;
		int r,g,b;
		
        for (int i = 0; i < 30; i++){
            for (int j = 0; j < 30; j++){
            	pixel= bm.getPixel(i, j);
            	
            	r=(int)Color.red(pixel);
            	g=(int)Color.green(pixel);
            	b=(int)Color.blue(pixel);
            	
                total_R += r;
                total_G += g;
                total_B += b;

                temp[i][j] = r * .3 + g * .59 + b * .11;
                total_temp += temp[i][j];
            }
        }
        
      //Calculate the average value;
        total_R = (total_R / 900) / 256; Data.add(total_R);
        total_G = (total_G / 900) / 256; Data.add(total_G);
        total_B = (total_B / 900) / 256; Data.add(total_B);

        double Threshold=(total_temp / 900);
        
      //Calculate the horizontal parameters
        for (int i = 0; i < 30; i++){
            total_temp = 0;
            for (int j = 0; j < 30; j++)
                if (temp[i][j] > Threshold) total_temp += temp[i][j];
            Data.add(total_temp / 30);
        }
        
      //Calculate the vertical parameters
        for (int j = 0; j < 30; j++){
            total_temp = 0;
            for (int i = 0; i < 30; i++)
                if (temp[i][j] > Threshold) total_temp += temp[i][j];
            Data.add(total_temp / 30);
        }
        
		double result[]=new double[63];
		for (int i = 0; i < 63; i++)
        {
            result[i] = (double)Data.get(i);
        }
		
		return result;
	}
}
