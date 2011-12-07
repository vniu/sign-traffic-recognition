package org.uit.thesis.View;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class TestActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		TestView01 testview=new TestView01(this);
		//Sample1View testview=new Sample1View(this);
        setContentView(testview);	
	}
}
