package com.planb.soda;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
		this.setVisibility(View.VISIBLE);
		this.setAlpha(1);
		AlphaAnimation aanim= new AlphaAnimation((float) 1,(float) 0);
		aanim.setDuration(260);
		aanim.setFillAfter(true);
		this.setAnimation(aanim);
		final LoadingLayout self=this;
		aanim.setAnimationListener(new Animation.AnimationListener(){
		    @Override
		    public void onAnimationStart(Animation arg0) {
		    }           
		    @Override
		    public void onAnimationRepeat(Animation arg0) {
		    }      
		    @Override
		    public void onAnimationEnd(Animation arg0) {
		    	self.setAlpha(0);
		    	self.setVisibility(View.INVISIBLE);
		    	self.clearAnimation();
		    }
		});
	}
	
	public void show(){
		this.setVisibility(View.VISIBLE);
		this.setAlpha(0);
		AlphaAnimation aanim= new AlphaAnimation((float) 0,(float) 1);
		aanim.setDuration(260);
		aanim.setFillAfter(true);
		this.setAnimation(aanim);
		final LoadingLayout self=this;
		aanim.setAnimationListener(new Animation.AnimationListener(){
		    @Override
		    public void onAnimationStart(Animation arg0) {
		    }           
		    @Override
		    public void onAnimationRepeat(Animation arg0) {
		    }      
		    @Override
		    public void onAnimationEnd(Animation arg0) {
		    	self.setAlpha(1);
		    	self.clearAnimation();
		    }
		});
	}

}
