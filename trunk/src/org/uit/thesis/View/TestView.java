package org.uit.thesis.View;

import jakie.thesis.MLP;
import jakie.thesis.NeuralNetwork;
import jakie.thesis.SignData;
import jakie.thesis.Util;

import java.io.InputStream;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.uit.thesis.R;
import org.uit.thesis.Object.DetectObjectLayer;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class TestView extends View{
	Bitmap src;
    
	public TestView(Context context) {
    	super(context);
    	    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    	Bitmap temp = BitmapFactory.decodeResource(this.getResources(), R.drawable.nest, options);
	  
    	NeuralNetwork<String> net=new NeuralNetwork<String>(new MLP<String>(63,80,5));
    	InputStream in=this.getResources().openRawResource(R.raw.net);
    	
    	
    	
    	try{
    		net.loadNetwork(in);
    		Bitmap bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.testsign, options);
        	net.recognize(Util.toData(bm));
        	
    		InputStream is = context.getAssets().open("sign.xml");
    		//InputStream is = this.getResources().openRawResource(R.xml.sign);
    		SignData data =new SignData(is);
    		Log.i("Ketqua",data.getImage(net.getMatchedHigh()));
    		String myResourceId=data.getImage(net.getMatchedHigh());
    		
    		Log.i("Ketqua",getResources().getIdentifier(myResourceId, "drawable", "org.uit.thesis")+"");
    		Log.i("Ketqua", R.drawable.sign_102+"");
    	}
    	catch(Exception e)
    	{
    		Log.i("TestView.Class",e.toString());
    	}
   	
    	
    	DetectObjectLayer detect=new DetectObjectLayer(temp);
        //detect.detectRedTriangleSign();
        //detect.detectRedCircleSign();
        //detect.detectBlueCircleSign();
        detect.detectAllSign();
    	
    	//List<Mat> signList=detect.getSignList();
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

