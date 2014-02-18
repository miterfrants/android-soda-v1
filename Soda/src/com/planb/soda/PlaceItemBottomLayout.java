package com.planb.soda;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

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
		}else if(ShareVariable.screenW==720 || ShareVariable.screenW==768){
			title.setTextSize(24);	
		}else{
			title.setTextSize((int) (ShareVariable.screenW*0.0347222));
		}
    	
    	title.setTextColor(0xFF000000);
    	//title set position and view
    	double titleW=screenW*0.625;
		RelativeLayout.LayoutParams rlpForTitle=  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForTitle.addRule(RelativeLayout.CENTER_IN_PARENT);
		rlpForTitle.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rlpForTitle.rightMargin=(int) (screenW*0.015625);
		rlpForTitle.width=(int) titleW;
		title.setGravity(Gravity.RIGHT);
		if(title.getText().length()>=9){
			title.setGravity(Gravity.LEFT);
		}
		title.setLayoutParams(rlpForTitle);
		title.setSingleLine(true);
		
		this.addView(title);
		
		
		dist=new TextView(context);
		if(ShareVariable.screenW==1080){
			dist.setTextSize(18);
		}else if(ShareVariable.screenW==720 || ShareVariable.screenW==768){
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
		
		btnDirection=new ArrowButton(context);
		RelativeLayout.LayoutParams rlpForBtnDirection=  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForBtnDirection.setMargins((int) (screenW*0.015625), (int) (screenW*0.015625), 0, 0);
		rlpForBtnDirection.height=(int) (ShareVariable.screenW*0.0625);
		rlpForBtnDirection.width=(int) (ShareVariable.screenW*0.0625);
				
		btnDirection.setLayoutParams(rlpForBtnDirection);
		btnDirection.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	try{
            		PlaceItem pi= (PlaceItem) v.getParent().getParent();
            		String daddr="";
            		if(pi.address.length()==0){
            			daddr= pi.lat+ "," + pi.lng;
            		}else{
            			daddr=pi.address;
            		}
            		String url="http://maps.google.com/maps?language=zh-TW&saddr="
        	                + ShareVariable.currentLocation.getLatitude()+ ","
        	                + ShareVariable.currentLocation.getLongitude() + "&daddr="+daddr;
                	Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
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
		
		RelativeLayout.LayoutParams rlpForThis= new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForThis.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rlpForThis.height=(int) (ShareVariable.screenW*0.09375);
		this.setLayoutParams(rlpForThis);
		// TODO Auto-generated constructor stub
		//border
		ShapeDrawable rect = new ShapeDrawable(new RectShape());
		Paint paint = rect.getPaint();
		paint.setColor(0xFF999999);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(1);
		ImageView border=new ImageView(this.getContext());
		border.setBackground(rect);
		RelativeLayout.LayoutParams rlpForBorder= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForBorder.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rlpForBorder.height=1;
		border.setLayoutParams(rlpForBorder);
		this.addView(border);
	}
}
