package com.planb.soda;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

@SuppressLint("NewApi")
public class RateLayout extends RelativeLayout {
	public TextView txtRate=null;
	public VerticalRatingBar rateBar=null;
	public RateLayout(Context context,int screenW) {
		super(context);
		//this.setBackgroundColor(0xCC999999);
		txtRate=new TextView(context);
		txtRate.setTextColor(0xFFFFFFFF);
		rateBar=new VerticalRatingBar(context);
		rateBar.setStepSize((float) 0.01);
		rateBar.setNumStars(1);
		rateBar.setRating(0);
		
		RelativeLayout.LayoutParams rlpForRateBar= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		
		rateBar.setLayoutParams(rlpForRateBar);
		
		txtRate.setText(String.valueOf(rateBar.getRating()));
		txtRate.setTextSize((int) (screenW*0.026041666));
		this.addView(rateBar);
		this.addView(txtRate);
		// TODO Auto-generated constructor stub
	}
	
    @Override
    public void dispatchDraw(Canvas canvas) {
    	Paint paint=new Paint();
    	paint.setColor(0xCC999999);
    	canvas.drawRoundRect(new RectF(0,0,this.getWidth(),this.getHeight()), 5, 5, paint);
    	super.dispatchDraw(canvas);
    }
	public void setRating(float rate){
		rateBar.setRating(rate);
	}

}
