package com.planb.soda;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("ViewConstructor")
public class PlaceItemBottomLayout extends RelativeLayout {
	public TextView title=null;
	public ArrowButton btnDirection=null;
	public TextView dist =null;
	public PlaceItemBottomLayout(Context context, int screenW) {
		super(context);
		this.setBackgroundColor(0xCCFFFFFF);
		
		title=new TextView(context);
    	title.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
    	title.setGravity(Gravity.RIGHT);
    	title.setTextColor(0xFF000000);
    	//title set position and view
    	double titleW=screenW*0.625;
		RelativeLayout.LayoutParams rlpForTitle=  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForTitle.setMargins((int) (screenW-titleW),0,(int) (screenW*0.015625),0);
		rlpForTitle.width=(int) titleW;
		title.setLayoutParams(rlpForTitle);
		this.addView(title);
		
		
		dist=new TextView(context);
		dist.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
		dist.setGravity(Gravity.LEFT);
		dist.setTextColor(0xFF000000);
    	//title set position and view
		RelativeLayout.LayoutParams rlpForDist=  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForDist.setMargins((int) (screenW*0.015825),0,0,0);
		dist.setLayoutParams(rlpForDist);
		this.addView(dist);
		
		
		btnDirection=new ArrowButton(context,screenW);
		RelativeLayout.LayoutParams rlpForBtnDirection=  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForBtnDirection.setMargins((int) (screenW*0.015625), (int) (screenW*0.015625), 0, 0);
		btnDirection.setLayoutParams(rlpForBtnDirection);
		
		this.addView(btnDirection);
		// TODO Auto-generated constructor stub
	}

	@Override
    public void dispatchDraw(Canvas canvas) {
		Paint paint = new Paint();

        // border
        paint.setColor(0xFF999999);
        canvas.drawRect(0,this.getHeight()-1,this.getWidth(),this.getHeight(), paint);
		super.dispatchDraw(canvas);
		
		

	}
}
