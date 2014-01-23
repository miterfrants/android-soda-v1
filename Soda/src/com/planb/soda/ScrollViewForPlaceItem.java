package com.planb.soda;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

public class ScrollViewForPlaceItem extends PullToRefreshScrollView{
	
	public ScrollViewForPlaceItem(Context context) {
		super(context);
		Log.d("test","test screll view show");
		// TODO Auto-generated constructor stub
	}
	public ScrollViewForPlaceItem(Context context,AttributeSet attr){
		super(context,attr);
	}
	
    @Override
    protected ScrollView createRefreshableView(Context context, AttributeSet attrs) {
            ScrollView scrollView;
            if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
                    scrollView = new InternalScrollViewSDK9(context, attrs);
            } else {
                    scrollView = new ScrollView(context, attrs);
            }

            scrollView.setId(com.planb.soda.R.id.scrollview);
            return scrollView;
    }
    final class InternalScrollViewSDK9 extends ScrollView {
    	public InternalScrollViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
            Log.d("test","test aaa");
    	}
    	@Override
        protected void onScrollChanged(int x, int y, int oldx, int oldy) {
            super.onScrollChanged(x, y, oldx, oldy);
            if(y-oldy>0){
            	final ListActivity la = (ListActivity) this.getContext();
            	if(!la.isShowingGetMore){
            		la.showButtonGetMore();
            	}
            }
        }
        
	}
    @Override
    public void onReleaseToRefresh() {
        // Do work to refresh the list here.
    	super.onReleaseToRefresh();
    	final ListActivity la = (ListActivity) this.getContext();
    	la.getData(true);
    }	
}
