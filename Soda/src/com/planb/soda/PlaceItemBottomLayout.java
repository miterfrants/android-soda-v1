package com.planb.soda;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ViewConstructor")
public class PlaceItemBottomLayout extends RelativeLayout {
	public TextView title=null;
	public ArrowButton btnDirection=null;
	public TextView dist =null;
	public PlaceItemBottomLayout(Context context, int screenW) {
		super(context);
		this.setBackgroundColor(0xCCFFFFFF);
		
		title=new TextView(context);
		if(ShareVariable.screenW==1080){
			title.setTextSize(24);
		}else if(ShareVariable.screenW==720){
			title.setTextSize(24);	
		}else{
			title.setTextSize((int) (ShareVariable.screenW*0.0347222));
		}
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
		if(ShareVariable.screenW==1080){
			dist.setTextSize(18);
		}else if(ShareVariable.screenW==720){
			dist.setTextSize(18);	
		}else{
			dist.setTextSize((int) (ShareVariable.screenW*0.025));
		}
		
		dist.setGravity(Gravity.LEFT);
		dist.setTextColor(0xFF000000);
    	//title set position and view
		RelativeLayout.LayoutParams rlpForDist=  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForDist.leftMargin=(int) (ShareVariable.screenW*0.078125*1.2);
		rlpForDist.bottomMargin=(int) (ShareVariable.screenW*0.015625);
		rlpForDist.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		dist.setLayoutParams(rlpForDist);
		this.addView(dist);
		
		
		btnDirection=new ArrowButton(context,ShareVariable.screenW);
		RelativeLayout.LayoutParams rlpForBtnDirection=  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForBtnDirection.setMargins((int) (screenW*0.015625), (int) (screenW*0.015625), 0, 0);
		btnDirection.setLayoutParams(rlpForBtnDirection);
		btnDirection.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	try{
            		PlaceItem pi= (PlaceItem) v.getParent().getParent();
                	Intent navigation = new Intent(Intent.ACTION_VIEW, Uri
                	        .parse("http://maps.google.com/maps?saddr="
                	                + pi.lat+ ","
                	                + pi.lng + "&daddr="
                	                + pi.lat+ "," + pi.lng));
                	navigation.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    navigation.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    v.getContext().startActivity(navigation);	
            	}catch(Exception ex){
            		Toast toast = Toast.makeText(v.getContext(), "請安裝Google Map，導航功能方能使用。", 1500);
            		toast.show();
            	}
            	
            	
            }
        });
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
