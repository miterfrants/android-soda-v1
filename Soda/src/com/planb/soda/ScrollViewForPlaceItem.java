package com.planb.soda;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;



public class ScrollViewForPlaceItem extends ScrollView {
	
	public ScrollViewForPlaceItem(Context context) {
		super(context);
		Log.d("test","test screll view show");
		// TODO Auto-generated constructor stub
	}
	public ScrollViewForPlaceItem(Context context,AttributeSet attr){
		super(context,attr);
	}
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if(y-oldy<0){
        	final ListActivity la = (ListActivity) this.getContext();
        	if(!la.isShowingGetMore){
        		la.showButtonGetMore();
        	}
        }
    }
}
