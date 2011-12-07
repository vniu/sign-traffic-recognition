package org.uit.thesis.View;

import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.uit.thesis.R;
import org.uit.thesis.Object.DetectObjectLayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class TestView extends View{
	Bitmap src;
    
    public TestView(Context context) {
    	super(context);
    	    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    	Bitmap temp = BitmapFactory.decodeResource(this.getResources(), R.drawable.nest, options);
	  
    	DetectObjectLayer detect=new DetectObjectLayer(temp);
        //detect.detectRedTriangleSign();
        //detect.detectRedCircleSign();
        //detect.detectBlueCircleSign();
        detect.detectAllSign();
    	
    	List<Mat> signList=detect.getSignList();
    	List<Rect> boxList=detect.getBoxList();
	    
	    //Rect r=boxList.get(0);
	    //Mat m=signList.get(0);
	    Mat m=Utils.bitmapToMat(temp);
	    
    	int n=boxList.size();
    	for(int i=0;i<n;i++)
    	{
    		Rect r=boxList.get(i);
    		Core.rectangle(m, r.tl(), r.br(), new Scalar(0, 0, 255), 2);
    	}
	    
	    
	    src = Bitmap.createBitmap(temp.getWidth(), temp.getHeight(),Bitmap.Config.ARGB_8888);
	    //src = Bitmap.createBitmap(r.width, r.height,Bitmap.Config.ARGB_8888);
	    
	    Utils.matToBitmap(m, src);
    }

    @Override
    public void onDraw(Canvas c)
    {
    	c.drawBitmap(src, 0, 0, new Paint());
    }

}

