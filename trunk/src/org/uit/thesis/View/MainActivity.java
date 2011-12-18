package org.uit.thesis.View;

import org.uit.thesis.R;
import org.uit.thesis.View.CustomSlider.CustomSliderPositionListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class MainActivity extends Activity {
	public static final int     VIEW_MODE_MANUAL  = 0;
    public static final int     VIEW_MODE_AUTO = 1;
    
    private MenuItem            mItemManul;
    private MenuItem            mItemAuto;
    
    private View mManualView;
    private View mAutoView;
    public static int           viewMode        = VIEW_MODE_MANUAL;
    public static float			focus	= 1.0f;
    public static boolean		change_focus=true;
    
    private CustomSlider mZoomSlider;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //CameraViewLayer cam=new CameraViewLayer(this);
        setContentView(new FrameViewLayer(this));
        //setContentView(cam);
        initView();
        
        mZoomSlider = (CustomSlider) findViewById(R.id.slider_vertical);
        mZoomSlider.setPositionListener(new CustomSliderPositionListener() {
	      public void onPositionChange(final float newPosition) {
	    	  changeZoom();
	      }
	    });
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
}