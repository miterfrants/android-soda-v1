package com.planb.soda;

import android.content.Context;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlaceCateLayout extends RelativeLayout {
	public String title="";
	public String type="";
	public String keyword="";
	public String otherSource="";
	public String bg="";
	public ImageButton cateButton=null;
	public TextView txtTitle=null;
	public PlaceCateLayout(Context context) {
		super(context);
		cateButton=new ImageButton(context);
		txtTitle=new TextView(context);
		RelativeLayout.LayoutParams rlpForImageButton=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
		cateButton.setLayoutParams(rlpForImageButton);
		
		RelativeLayout.LayoutParams rlpForTxtView=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForTxtView.addRule(RelativeLayout.CENTER_HORIZONTAL);
		txtTitle.setLayoutParams(rlpForTxtView);
		this.addView(cateButton);
		this.addView(txtTitle);
	}
	public void setTxtTopMargin(int containHeight, int imgHeight){
		RelativeLayout.LayoutParams rlpForTxtView=(RelativeLayout.LayoutParams) txtTitle.getLayoutParams();
		rlpForTxtView.topMargin=(int) (containHeight+imgHeight)/2+25;
		txtTitle.setLayoutParams(rlpForTxtView);
	}
}
