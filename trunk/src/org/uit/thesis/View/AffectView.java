package org.uit.thesis.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.view.View;

public class AffectView extends View{
	private final Paint mPaint = new Paint();
    private final Rect  mRect1 = new Rect();
    private final Rect  mRect2 = new Rect();
    
	public AffectView(Context context) {
		super(context);
	}
	
	public void setRegion(double left, double top,  double right, double bottom){
		mRect2.set((int)left, (int)top, (int)right, (int)bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        mPaint.setAlpha(200);
        mRect1.set(0, 0, canvas.getWidth(), canvas.getHeight());
                
        Region rgn = new Region();
        rgn.set(mRect1);
        rgn.op(mRect2, Region.Op.XOR);
        
        RegionIterator iter = new RegionIterator(rgn);
        Rect r = new Rect();
        
        while (iter.next(r)) {
            canvas.drawRect(r, mPaint);
        }
	}
}
