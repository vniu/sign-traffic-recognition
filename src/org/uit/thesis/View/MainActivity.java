package org.uit.thesis.View;

import jakie.thesis.MLP;
import jakie.thesis.NeuralNetwork;
import jakie.thesis.SignData;
import jakie.thesis.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.uit.thesis.R;
import org.uit.thesis.View.CustomSlider.CustomSliderPositionListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class MainActivity extends Activity {
	public static final int     VIEW_MODE_MANUAL  = 0;
    public static final int     VIEW_MODE_AUTO = 1;
        
    private MenuItem            mItemManul;
    private MenuItem            mItemAuto;
    
    public static View mManualView;
    public static View mAutoView;
    private AffectView affect=null;
    
    public static int           viewMode        = VIEW_MODE_MANUAL;
    public static float			focus	= 1.0f;
    public static boolean		change_focus=true;
    public static boolean		taking_pic=false;
    public static boolean		inProgress=false;
    
    public static boolean disableMenu=false;
    private CustomSlider mZoomSlider;
    private FrameViewLayer cam;
    private int cout=0;
    QuickAction quickAction;
    
    NeuralNetwork<String> net;
    SignData data;
    
    //Initialize Main-activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        cam=new FrameViewLayer(this,handler);
        setContentView(cam);
        initView();
        
        initNetwork();
    }
    
    //Try to initialize View for Main-activity
    private void initView(){
    	//Create manual-view and auto-view
    	LayoutInflater  in=getLayoutInflater();
        mManualView=in.inflate(R.layout.manual_view,null);
        mAutoView=in.inflate(R.layout.auto_view,null);
        mAutoView.setVisibility(View.INVISIBLE);
        
        //Add View to Activity
        addContentView(mManualView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        addContentView(mAutoView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        
        //Create Affect View
        affect=new AffectView(this);
        affect.setVisibility(View.INVISIBLE);
    	addContentView(affect, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        
    	//Set the Button for camera
        ImageButton next= (ImageButton) findViewById(R.id.button1);
        next.setAlpha(175);
             
        next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				taking_pic=true;
			}
		});
        
        //Create Slider for zooming
        mZoomSlider = (CustomSlider) findViewById(R.id.slider_vertical);
        mZoomSlider.setPositionListener(new CustomSliderPositionListener() {
	      public void onPositionChange(final float newPosition) {
	    	  changeZoom();
	      }
	    });
    }
    
    private void initNetwork(){
    	try{
    		net=new NeuralNetwork<String>(new MLP<String>(63,80,5));
        	InputStream in=this.getResources().openRawResource(R.raw.net);
    		net.loadNetwork(in);
    		
    		InputStream is = this.getAssets().open("sign.xml");
    		data =new SignData(is);
    	}
    	catch(Exception e)
    	{
    		Log.i("MainActivity.Class",e.toString());
    	}
    }
    
    //Create option menu
    public boolean onCreateOptionsMenu(Menu menu) {
    	mItemManul = menu.add("Manual");
    	mItemAuto = menu.add("Auto");
        return true;
    }
    
    //Change option menu
    public boolean onPrepareOptionsMenu (Menu menu) {
        if (disableMenu){
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
        }else{
        	menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
        }
        return true;
    }
    
    //Change view
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == mItemManul&&viewMode == VIEW_MODE_MANUAL)
        	return true;
        if (item == mItemAuto&&viewMode == VIEW_MODE_AUTO)
        	return true;
        
	        if(item == mItemManul){
	            viewMode = VIEW_MODE_MANUAL;
	            addManualView();
	        }
	        else{
	        	viewMode = VIEW_MODE_AUTO;
	        	removeManualView();
	        }
        return true;
    }
    
    //Enable manual view
    private void addManualView(){
    	mManualView.setVisibility(View.VISIBLE);
    	mAutoView.setVisibility(View.INVISIBLE);
    }
    
    //Enable auto view
    private void removeManualView(){
    	mManualView.setVisibility(View.INVISIBLE);
    	mAutoView.setVisibility(View.VISIBLE);
    }
    
    //Get detected result from camera
    public void detectObject(){
    	mManualView.setVisibility(View.INVISIBLE);
    	mAutoView.setVisibility(View.INVISIBLE);
   	
    	if(cam.boxList.size()==0)
    		noResult();
    	else{
    		disableMenu=true;
    	}
    }
    

    //No detected sign
    private void noResult(){
    	new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_info)
        .setTitle("Result")
        .setMessage("There's no sign detected!")
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	taking_pic=false;
            	cam.setFlag(true);
            	finishDetect();
            }
        })
        .setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
				taking_pic=false;
				cam.setFlag(true);
            	finishDetect();

			}
		})
        .show();
    }
    
    //Touch on Screen
    public boolean onTouchEvent(MotionEvent event) {
	     float touched_x = event.getX();
	     float touched_y = event.getY();
	      
	     int action = event.getAction();
	     if(action==MotionEvent.ACTION_UP&&disableMenu){
	    	 //Try to test if touch on detected sign
	    	 findRect(touched_x,touched_y);
	     }
	     return true;
   }

    //Check if this is one of detected signs
    private void findRect(float x, float y){
    	affect.setVisibility(View.INVISIBLE);
    	
    	int n=cam.boxList.size();
      	for(int i=0;i<n;i++){
      		Rect r=cam.boxList.get(i);
      		
      		if((x>=r.tl().x && x<=r.br().x)&&(y>=r.tl().y && y<=r.br().y)){
      			//Set affect
      			affect.setRegion(r.tl().x, r.tl().y, r.br().x, r.br().y);
      			affect.setVisibility(View.VISIBLE);
      			
      			//Show Recognized result
				createPopupResult(cam.signList.get(i));
				quickAction.show(affect,r.x,r.y,r.width,r.height);
      			return;
      		}
      	}
  		affect.setVisibility(View.INVISIBLE);
    }

    //Create PopupMenu
    private void createPopupResult(Mat sign){
    	Bitmap temp=Bitmap.createBitmap(sign.cols(), sign.rows(), Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(sign, temp);
    	
		Bitmap blob=Bitmap.createScaledBitmap(temp, 30, 30,true);
		cout++;
		savePic(blob, "blob");
		
		//read again from sd card
		BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    	Bitmap blobnew = BitmapFactory.decodeFile("/sdcard/"+"blob.jpg", options);
		
	    	try{
	    		net.recognize(Util.toData(blobnew));
	    	}catch(Exception e)
	    	{
	    		Log.i("MainActivity.Class",e.toString());
	    	}
	    	
	    String matchHigh=net.getMatchedHigh();
	    String matchLow=net.getMatchedLow();
	    int high=(int)(net.getOutputValueHight()*100);
	    int low=(int)(net.getOutputValueLow()*100);
	    
	    quickAction = new QuickAction(this, QuickAction.HORIZONTAL);
	    
	    if(matchHigh!=null&&high>90){
	    	String title=data.getSignName(matchHigh);
	    	
	    	ActionItem sign1 	= new ActionItem(high+"%. "+title,
	    			getResources().getDrawable(
	    					getResources().getIdentifier(
	    					data.getImage(matchHigh), "drawable", "org.uit.thesis")));
	    	quickAction.addActionItem(sign1);
	    	
	    	if(matchLow!=null&&low>90){
		    	title=data.getSignName(matchLow);
		    	
		    	ActionItem sign2 	= new ActionItem(low+"%. "+title,
		    			getResources().getDrawable(
		    					getResources().getIdentifier(
		    					data.getImage(matchLow), "drawable", "org.uit.thesis")));
		    	quickAction.addActionItem(sign2);
		    }
	    }else if(matchHigh!=null){
	    	ActionItem sign3 	= new ActionItem("Don't have data about this sign",
	    			getResources().getDrawable(R.drawable.unknow));
	    	quickAction.addActionItem(sign3);
	    }
	    
	    
    }

    //End the detect process
    private void finishDetect(){
    	if(viewMode == VIEW_MODE_MANUAL){
            addManualView();
        }
        else{
        	removeManualView();
        }
    }
    
    //Process when press key
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if(keyCode == KeyEvent.KEYCODE_BACK) {
        	if(disableMenu){
        		disableMenu=false;
        		taking_pic=false;
        		cam.setFlag(true);
            	finishDetect();
            	affect.setVisibility(View.INVISIBLE);
            	return true;
        	}
        	        	
            //Ask the user if they want to quit
            new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Quit")
            .setMessage("Do you really want to quit ?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //Stop the activity
                	MainActivity.this.finish();    
                }

            })
            .setNegativeButton("No", null)
            .show();

            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }

    }
    
    //Change zoom-focus
    private void changeZoom(){
    	focus=mZoomSlider.getPosition();
    	change_focus=true;
    	Log.i("ZoomFocus","Focus: " + focus);
    }
    
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int f = msg.arg1;
            if(f==1){
            	mManualView.setVisibility(View.INVISIBLE);
            	mAutoView.setVisibility(View.INVISIBLE);
            	disableMenu=true;
            }else if(f==0){
            	detectObject();
            }
        }
    };
    
  private void savePic(Bitmap bmp,String name){
	try {
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File file = new File(path, name+".jpg");
        fOut = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        fOut.flush();
        fOut.close();
    }
    catch (Exception e) {
        e.printStackTrace();
    }
	
}
}