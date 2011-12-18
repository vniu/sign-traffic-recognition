package org.uit.thesis.View;

import org.uit.thesis.R;
import org.uit.thesis.View.CustomSlider.CustomSliderPositionListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
    
    private static View mManualView;
    private static View mAutoView;
    public static int           viewMode        = VIEW_MODE_MANUAL;
    public static float			focus	= 1.0f;
    public static boolean		change_focus=true;
    public static boolean		taking_pic=false;
    public static boolean		inProgress=false;
    
    private CustomSlider mZoomSlider;
    private FrameViewLayer cam;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //CameraViewLayer cam=new CameraViewLayer(this);
        cam=new FrameViewLayer(this);
        setContentView(cam);
        //setContentView(cam);
        initView();
    }
    
    private void changeZoom(){
    	focus=mZoomSlider.getPosition();
    	change_focus=true;
    	Log.i("ZoomFocus","Focus: " + focus);
    }
    
    private void initView(){
    	LayoutInflater  in=getLayoutInflater();
        mManualView=in.inflate(R.layout.manual_view,null);
        mAutoView=in.inflate(R.layout.auto_view,null);
        mAutoView.setVisibility(View.INVISIBLE);
        
        addContentView(mManualView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        addContentView(mAutoView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        
        ImageButton next= (ImageButton) findViewById(R.id.button1);
        next.setAlpha(175);
             
        next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				taking_pic=true;
				try{
					//wait for camera
				Thread.sleep(1200);
				}
				catch(Exception e)
				{
					//Can't sleep
				}
				detectObject();
			}
		});
        
        mZoomSlider = (CustomSlider) findViewById(R.id.slider_vertical);
        mZoomSlider.setPositionListener(new CustomSliderPositionListener() {
	      public void onPositionChange(final float newPosition) {
	    	  changeZoom();
	      }
	    });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	mItemManul = menu.add("Manual");
    	mItemAuto = menu.add("Auto");
        return true;
    }
    
    @Override
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
    
    private void addManualView(){
    	mManualView.setVisibility(View.VISIBLE);
    	mAutoView.setVisibility(View.INVISIBLE);
    }
    
    private void removeManualView(){
    	mManualView.setVisibility(View.INVISIBLE);
    	mAutoView.setVisibility(View.VISIBLE);
    }
    
    public void detectObject(){
    	mManualView.setVisibility(View.INVISIBLE);
    	mAutoView.setVisibility(View.INVISIBLE);
   	
    	if(cam.boxList.size()==0)
    		noResult();
    	else foundResult();
    }
    
    private void foundResult(){
    	new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_info)
        .setTitle("Result")
        .setMessage("There's "+cam.boxList.size()+" sign detected!")
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	taking_pic=false;
            	cam.flag=true;
            	finishDetect();
            }
        })
        .setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
				taking_pic=false;
				cam.flag=true;
            	finishDetect();
			
			}
		})
        .show();
    }
    
    private void noResult(){
    	new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_info)
        .setTitle("Result")
        .setMessage("There's no sign detected!")
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	taking_pic=false;
            	cam.flag=true;
            	finishDetect();
            }
        })
        .setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
				taking_pic=false;
				cam.flag=true;
            	finishDetect();

			}
		})
        .show();
    }
    
    private void finishDetect(){
    	if(viewMode == VIEW_MODE_MANUAL){
            addManualView();
        }
        else{
        	removeManualView();
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if(keyCode == KeyEvent.KEYCODE_BACK) {
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
}