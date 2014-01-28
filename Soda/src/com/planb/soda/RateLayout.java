package com.planb.soda;

import android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class RateLayout extends RelativeLayout {
	public TextView txtRate=null;
	public RatingBar rateBar=null;
	public RateLayout(Context context,int screenW) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    inflater.inflate(com.planb.soda.R.layout.rate_layout, this, true);
		    
		rateBar =(RatingBar) ((RelativeLayout) this.getChildAt(0)).getChildAt(1);
		rateBar.setRating((float) 0.00);
		RelativeLayout.LayoutParams rlpForRateBar= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForRateBar.addRule(RelativeLayout.CENTER_VERTICAL);
		//rlpForRateBar.setMargins((int) (screenW*0.01641666),(int) (screenW*0.00501666),0,0);
		rlpForRateBar.leftMargin=(int) (ShareVariable.screenW * 0.022416666);
		if(ShareVariable.screenW==1080){
			rlpForRateBar.height=80;
			rlpForRateBar.width=80;
		}else{
			//no config adapter container
		}
		
		rateBar.setLayoutParams(rlpForRateBar);
		
		txtRate =(TextView) ((RelativeLayout) this.getChildAt(0)).getChildAt(0);
		txtRate.setTextColor(0xFFFFFFFF);
		txtRate.setText(String.valueOf(rateBar.getRating()));
		RelativeLayout.LayoutParams rlpForTxtRate= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForTxtRate.addRule(RelativeLayout.CENTER_VERTICAL);
		rlpForTxtRate.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		if(ShareVariable.screenW==1080){
			txtRate.setTextSize(26);
			rlpForTxtRate.rightMargin=(int) (screenW*0.01041666);
		}else if(ShareVariable.screenW==720 || ShareVariable.screenW==768){
			txtRate.setTextSize(26);
			rlpForTxtRate.rightMargin=(int) (screenW*0.01041666);
		}else{
			txtRate.setTextSize((int) (screenW*0.036041666));
			rlpForTxtRate.rightMargin=(int) (screenW*0.01041666);
		}
		txtRate.setLayoutParams(rlpForTxtRate);
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
		//Log.d("test","test width:" + String.valueOf(rateBar.getVerticalScrollbarWidth()));
	}

}
