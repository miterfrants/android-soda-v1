package com.planb.soda;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Debug;

import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class ScrollViewForPlaceItem extends PullToRefreshScrollView{
	public ScrollView internalScrollView=null;
	public ScrollViewForPlaceItem(Context context) {
		super(context);
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
            internalScrollView=scrollView;
            return scrollView;
    }
    final class InternalScrollViewSDK9 extends ScrollView {
    	public InternalScrollViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
    	}
    	@Override
        protected void onScrollChanged(int x, int y, int oldx, int oldy) {
            super.onScrollChanged(x, y, oldx, oldy);
            int _screenW = ShareVariable.screenW;
            final ListActivity la = (ListActivity) this.getContext();
            if(y<_screenW*0.4){
            	ShareVariable.selectedMarkerIndex=0;
            }else{
            	int currentIndex=(int) Math.floor((y +_screenW*0.4)/(_screenW/2));
    			ShareVariable.selectedMarkerIndex=currentIndex;
            }
        	if(ShareVariable.arrMarker.size()>0){
        		ShareVariable.arrMarker.get(ShareVariable.selectedMarkerIndex).showInfoWindow();	
        	}            
            if(y-oldy>0){
            	if(!la.isShowingGetMore && la.token.length()>0){
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
		
		String ip = Util.getIPAddress(true);
		String url="http://"+ShareVariable.domain+ShareVariable.reportController+"?action=add-pull-down&cate="+la.title+"&creator_ip="+ip;
		AsyncHttpClient client = new AsyncHttpClient();
 		client.get(url, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		    }
		    @Override
		    public void onFailure(Throwable e, String response){
		    }
		});
    }	
}
