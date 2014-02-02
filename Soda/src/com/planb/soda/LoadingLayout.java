package com.planb.soda;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class LoadingLayout extends LinearLayout {
	public GifMovieView imgLoading=null;
	public TextView txtLoadingStatus=null;
	public LoadingLayout(Context context) {
		super(context);
		this.setOrientation(LinearLayout.VERTICAL);
		this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
		this.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
		
		GifMovieView imgLoading=new GifMovieView(context);
		imgLoading.setMovieResource(R.drawable.loading);
		RelativeLayout.LayoutParams rlpForImg= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForImg.addRule(RelativeLayout.CENTER_IN_PARENT);
		imgLoading.setLayoutParams(rlpForImg);
		this.addView(imgLoading);
		
		txtLoadingStatus=new TextView(context);
		RelativeLayout.LayoutParams rlpForTxtLoadingStatus=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForTxtLoadingStatus.addRule(RelativeLayout.CENTER_IN_PARENT);
		txtLoadingStatus.setLayoutParams(rlpForTxtLoadingStatus);
		txtLoadingStatus.setPadding(0,(int) (ShareVariable.screenW*0.02727),0,0);
		txtLoadingStatus.setText("正在讀取資料中...");
		this.addView(txtLoadingStatus);
	}
	
	public void hide(){
		txtLoadingStatus.setVisibility(View.INVISIBLE);
		imgLoading.setVisibility(View.INVISIBLE);
	}
	
	public void show(){
		txtLoadingStatus.setVisibility(View.VISIBLE);
		imgLoading.setVisibility(View.VISIBLE);
	}

}
