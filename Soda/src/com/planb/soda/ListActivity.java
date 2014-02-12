package com.planb.soda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("InlinedApi")
public class ListActivity extends FragmentActivity {
	public String token="";
	public Location currentLocation=null;
	public boolean isShowingGetMore=false;
	public int selectedMarkerIndex=-1;
	public LoadingLayout ldLayout=null;
	public String title="";
	public SlidingMenu slideMenu=null;
	public boolean isOpenedMap=false;
	private int screenW=0;
	private Button btnGetMore;
	private String urlGet;
	private JSONArray arrRes;
	private String keyword;
	private String type;
	private String otherSource;
	private RelativeLayout rlForContent=null;
	private ScrollViewForPlaceItem scForPI =null;
	private GoogleMap map=null;
	private Button _btnNext=null;
	private Button _btnPreviouse=null;
	private Button _btnTakeMeThere =null;
	
	public static List<PlaceItem> arrListResult=new ArrayList<PlaceItem>();
	@Override 
	protected void onStart(){
		super.onStart();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ShareVariable.arrMarker.clear();
		arrListResult.clear();
		super.onCreate(savedInstanceState);
		ViewServer.get(this).addWindow(this); 
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		setContentView(com.planb.soda.R.layout.activity_list);
		rlForContent= new RelativeLayout(this);
		
		ldLayout=new LoadingLayout(this);;
		ldLayout.setAlpha(1.0f);
		ldLayout.bringToFront();
		rlForContent.addView(ldLayout);

		LocationManager lm=(LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);
		currentLocation=ShareVariable.getLocation(lm);
		if(currentLocation==null){
			Toast toast = Toast.makeText(this, "請先開啟 GPS 定位功能。", 1000);
    		toast.show();
			return;
		}
		slideMenu =(SlidingMenu) this.findViewById(com.planb.soda.R.id.rl_for_activity_list);
		keyword=getIntent().getStringExtra("keyword");
		type=getIntent().getStringExtra("type");
		otherSource=getIntent().getStringExtra("otherSource");
		
		rlForContent.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
		urlGet="https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
				+ "location={lat},{lng}&radius=500&keyword="+keyword+"&sensor=false&"
				+ "key="+ShareVariable.GOOGLE_KEY+"&rankBy=prominence&types="+ type+"&language=zh-TW";
		
		screenW=getWindowManager().getDefaultDisplay().getWidth();
		btnGetMore=new Button(this);
		btnGetMore.setBackgroundResource(com.planb.soda.R.drawable.circle_button);
		RelativeLayout.LayoutParams rlpForBtnGetMore= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForBtnGetMore.width=((int) (screenW*0.145*0.8));
		rlpForBtnGetMore.height=((int) (screenW*0.145*0.8));
		rlpForBtnGetMore.bottomMargin=-rlpForBtnGetMore.width/2;
		rlpForBtnGetMore.leftMargin=-rlpForBtnGetMore.width/2;
		rlpForBtnGetMore.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		if(ShareVariable.screenW==1080){
			btnGetMore.setTextSize(9);
		}else if(ShareVariable.screenW==720 || ShareVariable.screenW==768){
			btnGetMore.setTextSize(9);	
		}else{
			btnGetMore.setTextSize((int) (screenW * 0.01688888));
		}
		btnGetMore.setTextColor(0xFFFFFFFF);
		btnGetMore.setText("更多");
		btnGetMore.setSingleLine(true);
		btnGetMore.setLayoutParams(rlpForBtnGetMore);
		btnGetMore.setAlpha(0.0f);
		btnGetMore.setClickable(false);
		btnGetMore.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	btnGetMore.setAlpha(0.0f);
		    	btnGetMore.setClickable(false);
		    	LinearLayout rlList =(LinearLayout) findViewById(com.planb.soda.R.id.ll_list);
		    	rlList.removeAllViews();
		    	String ip = Util.getIPAddress(true);
		    	ListActivity la=(ListActivity)	v.getContext();
				String url="http://"+ShareVariable.domain+ShareVariable.reportController+"?action=add-get-more&cate="+la.title+"&creator_ip="+ip;
				AsyncHttpClient client = new AsyncHttpClient();
		 		client.get(url, new AsyncHttpResponseHandler() {
				    @Override
				    public void onSuccess(String response) {
				    }
				    @Override
				    public void onFailure(Throwable e, String response){
				    }
				});
		 		getData(false);
		    }
		});
		rlForContent.addView(btnGetMore);
		//hideButtonGetMore(0);
		
		//list containerscForPI
		this.scForPI =(ScrollViewForPlaceItem) LayoutInflater.from(this).inflate(com.planb.soda.R.layout.scroll_view_for_place_item,null);
		rlForContent.addView(scForPI);
		scForPI.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
		scForPI.setPersistentDrawingCache(ViewGroup.PERSISTENT_SCROLLING_CACHE);
		scForPI.setAlwaysDrawnWithCacheEnabled(true);
		scForPI.setAlpha(0.0f);
		slideMenu.setContent(rlForContent);
		RelativeLayout rightView = (RelativeLayout)  LayoutInflater.from(this).inflate(com.planb.soda.R.layout.right_map,null);
		
		//map
		RelativeLayout.LayoutParams rlForMapPreviouseButton =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlForMapPreviouseButton.height=(int) (screenW*0.15);
		rlForMapPreviouseButton.width= (int) (screenW*0.15);
		rlForMapPreviouseButton.topMargin=(int) (screenW*0.0625);
		rlForMapPreviouseButton.leftMargin=(int) (screenW*0.0625);
		_btnPreviouse=new Button(this);
		_btnPreviouse.setLayoutParams(rlForMapPreviouseButton);
		_btnPreviouse.setBackgroundResource(R.drawable.pre_btn);
		_btnPreviouse.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	selectPreviouseMarker();
		    }
		});
		
		RelativeLayout.LayoutParams rlForMapNextButton =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		_btnNext=new Button(this);
		rlForMapNextButton =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlForMapNextButton.height=(int) (screenW*0.15);
		rlForMapNextButton.width= (int) (screenW*0.15);
		rlForMapNextButton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rlForMapNextButton.topMargin=(int) (screenW*0.0625);
		rlForMapNextButton.rightMargin=(int) (screenW*0.0625);
		_btnNext.setLayoutParams(rlForMapNextButton);
		_btnNext.setBackgroundResource(R.drawable.next_btn);
		_btnNext.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	selectNextMarker();
		    }
		});
		
		_btnTakeMeThere=new Button(this);
		RelativeLayout.LayoutParams rlForMapTakeMeThereButton =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlForMapTakeMeThereButton.width=(int) (screenW*0.50893);
		rlForMapTakeMeThereButton.height=(int) (screenW*0.15);
		rlForMapTakeMeThereButton.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rlForMapTakeMeThereButton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rlForMapTakeMeThereButton.addRule(RelativeLayout.ALIGN_LEFT);
		rlForMapTakeMeThereButton.bottomMargin=(int) (screenW*0.0625);
		rlForMapTakeMeThereButton.rightMargin=(int) (screenW*0.0625);
		_btnTakeMeThere.setLayoutParams(rlForMapTakeMeThereButton);
		_btnTakeMeThere.setBackgroundResource(R.drawable.nav_btn);
		_btnTakeMeThere.setText("導 航");
		//尺寸調整  16

		if(ShareVariable.screenW==1080){
			_btnTakeMeThere.setTextSize(20);
		}else if(ShareVariable.screenW==720 || ShareVariable.screenW==768){
			_btnTakeMeThere.setTextSize(38);
		}
		
		_btnTakeMeThere.setTextAlignment(View.TEXT_DIRECTION_LTR);
		_btnTakeMeThere.setTextColor(0xFFFFFFFF);
		_btnTakeMeThere.setPadding(0,0,(int) (screenW*0.339062*0.5),0);
		_btnTakeMeThere.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	try{
            		Marker marker= ShareVariable.arrMarker.get(selectedMarkerIndex);
                	Intent navigation = new Intent(Intent.ACTION_VIEW, Uri
                	        .parse("http://maps.google.com/maps?saddr="
                	                + String.valueOf(currentLocation.getLatitude())+ ","
                	                + String.valueOf(currentLocation.getLongitude()) + "&daddr="
                	                + marker.getSnippet()));
                	navigation.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    navigation.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    v.getContext().startActivity(navigation);	
            	}catch(Exception ex){
            		//com.google.android.apps.maps
            		Toast toast = Toast.makeText(v.getContext(), "請安裝Google Map，導航功能方能使用。", 1000);
            		toast.show();
            	}
            }
        });
		rightView.addView(_btnPreviouse);
		rightView.addView(_btnNext);
		rightView.addView(_btnTakeMeThere);
		
		//map view
		android.support.v4.app.FragmentManager myFM = this.getSupportFragmentManager();
		final SupportMapFragment myMAPF = (SupportMapFragment) myFM
		                .findFragmentById(R.id.map);
		map=myMAPF.getMap();
		if(map!=null){
			myMAPF.getMap().getUiSettings().setZoomControlsEnabled(false);
			setMapCenter(currentLocation.getLatitude(),currentLocation.getLongitude(),15);
		    map.setOnMarkerClickListener(getMarkerClickListener());
		    map.setOnMapClickListener(getMapClickListener());
		}else{
			Toast toast = Toast.makeText(this, "無法使用您的Google Map，麻煩您更新。", 1000);
    		toast.show();
		}
	    RelativeLayout.LayoutParams rlpForRightView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
	    rlpForRightView.width=(int) (ShareVariable.screenW*0.9);
	    rlpForRightView.height=ShareVariable.screenH;
	    rightView.setLayoutParams(rlpForRightView);
	    
	    slideMenu.setMenu(rightView);
	    slideMenu.setMode(SlidingMenu.RIGHT);
	    slideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
	    slideMenu.setBehindWidth((int) (ShareVariable.screenW*0.9));
	    slideMenu.setOnOpenedListener(new OnOpenedListener(){
			@Override
			public void onOpened() {
				isOpenedMap=true;
				String ip = Util.getIPAddress(true);
				String url="http://"+ShareVariable.domain+ShareVariable.reportController+"?action=add-slide-to-map&cate="+title+"&creator_ip="+ip;
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

	    });
	    slideMenu.setOnClosedListener(new OnClosedListener(){
			@Override
			public void onClosed() {
				isOpenedMap=false;
			}	    
	    });
	    
		ActionBar bar=getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		title=getIntent().getStringExtra("title");
		bar.setTitle("Soda | "+getIntent().getStringExtra("title"));
		
		getData(true);
	}
	
	
	//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{    
		
	   switch (item.getItemId()) 
	   {        
	      case android.R.id.home:
	    	  if(isOpenedMap){
	    		  slideMenu.showContent();
	    		  return true;  
	    	  }
	         Intent intent = new Intent(this, MainActivity.class);            
	         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
	         startActivity(intent);            
	         return true;
	      default:
	         return super.onOptionsItemSelected(item);    
	   }
	}
	//if map was expanded then click back button return list view
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if(isOpenedMap){
	    		slideMenu.showContent();
	    		return true;  
	    	}
            startActivity(new  Intent(this, MainActivity.class));
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    public void hideContent(){
    	Log.d("test","test hide content");
    	ldLayout.clearAnimation();
    	btnGetMore.clearAnimation();
    	scForPI.clearAnimation();
    	//這邊沒有work
		ldLayout.show();
		
		scForPI.setAlpha(0.0f);
		AlphaAnimation aanimForScForPI= new AlphaAnimation(1.0f,0.0f);
		aanimForScForPI.setDuration(250);
		aanimForScForPI.setFillAfter(true);
		aanimForScForPI.setAnimationListener(new Animation.AnimationListener(){
		    @Override
		    public void onAnimationStart(Animation arg0) {
		    }           
		    @Override
		    public void onAnimationRepeat(Animation arg0) {
		    }      
		    @Override
		    public void onAnimationEnd(Animation arg0) {
		    	scForPI.setAlpha(0.0f);
		    	scForPI.setVisibility(View.INVISIBLE);
		    	scForPI.clearAnimation();
		    }
		});
		scForPI.setAnimation(aanimForScForPI);
		
		if(token.length()>0){
			btnGetMore.setVisibility(View.VISIBLE);
			btnGetMore.setAlpha(0.0f);
			AlphaAnimation aanimForBtnGetMore= new AlphaAnimation((float) 1,(float) 0);
			aanimForBtnGetMore.setDuration(2000);
			aanimForBtnGetMore.setFillAfter(true);
			btnGetMore.setAnimation(aanimForBtnGetMore);
			aanimForBtnGetMore.setAnimationListener(new Animation.AnimationListener(){
			    @Override
			    public void onAnimationStart(Animation arg0) {
			    }           
			    @Override
			    public void onAnimationRepeat(Animation arg0) {
			    }      
			    @Override
			    public void onAnimationEnd(Animation arg0) {
			    	btnGetMore.setAlpha(0);
			    	btnGetMore.setVisibility(View.INVISIBLE);
			    	btnGetMore.clearAnimation();
			    }
			});
		}
    }
    
    public void showContent(boolean isShowGetMore){
    	ldLayout.clearAnimation();
    	btnGetMore.clearAnimation();
    	scForPI.clearAnimation();
		ldLayout.hide();
		
		scForPI.setVisibility(View.VISIBLE);
		scForPI.setAlpha(1.0f);
		AlphaAnimation aanimForScForPI= new AlphaAnimation((float) 0.0f,(float) 1.0f);
		aanimForScForPI.setDuration(500);
		aanimForScForPI.setFillAfter(true);
		aanimForScForPI.setAnimationListener(new Animation.AnimationListener(){
		    @Override
		    public void onAnimationStart(Animation arg0) {
		    }           
		    @Override
		    public void onAnimationRepeat(Animation arg0) {
		    }      
		    @Override
		    public void onAnimationEnd(Animation arg0) {
		    	scForPI.setAlpha(1.0f);
		    	scForPI.setVisibility(View.VISIBLE);
		    	scForPI.clearAnimation();
		    }
		});
		scForPI.setAnimation(aanimForScForPI);
		
	    btnGetMore.bringToFront();
	    if(isShowGetMore){
		    btnGetMore.setVisibility(View.VISIBLE);
		    btnGetMore.setAlpha(1.0f);
			AlphaAnimation aanimForBtnGetMore= new AlphaAnimation(0.0f, 1.0f);
			aanimForBtnGetMore.setDuration(500);
			aanimForBtnGetMore.setFillAfter(true);
			btnGetMore.setAnimation(aanimForBtnGetMore);
			aanimForBtnGetMore.setAnimationListener(new Animation.AnimationListener(){
			    @Override
			    public void onAnimationStart(Animation arg0) {
			    }           
			    @Override
			    public void onAnimationRepeat(Animation arg0) {
			    }      
			    @Override
			    public void onAnimationEnd(Animation arg0) {
			    	btnGetMore.setAlpha(1.0f);
			    	btnGetMore.setVisibility(View.VISIBLE);
			    	btnGetMore.clearAnimation();
			    	btnGetMore.setClickable(true);
			    }
			});
	   }else{
		   btnGetMore.setAlpha(0.0f);
		   btnGetMore.setClickable(false);
	   }
    }
	public void getData(final boolean isNew){
		if(!isNew){
			hideContent();
		}
		Thread thread = new Thread()
		{
		    @Override
		    public void run() {
		        try {
		            getDataMain(isNew);
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		};
		thread.start();
	}
	
	public OnMarkerClickListener getMarkerClickListener()
	{
	    return new OnMarkerClickListener() 
	    {       
	        @Override
	        public boolean onMarkerClick(Marker marker) 
	        {
	        	marker.showInfoWindow();
	        	for(int i=0;i<ShareVariable.arrMarker.size();i++){
	        		if(ShareVariable.arrMarker.get(i)==marker){
	        			selectedMarkerIndex=i;
	        		}
	        	}
	        	return false;
	        }
	    };      
	}
	
	public OnMapClickListener getMapClickListener()
	{
	    return new OnMapClickListener() 
	    {       
			@Override
			public void onMapClick(LatLng point) {
				selectedMarkerIndex=-1;
				_btnTakeMeThere.setVisibility(View.INVISIBLE);
			}
	    };      
	}
	
	public void selectNextMarker(){
		if(this.selectedMarkerIndex==-1){
			this.selectedMarkerIndex=0;
		}else if(this.selectedMarkerIndex==ShareVariable.arrMarker.size()-1){
			this.selectedMarkerIndex=0;
		}else{
			this.selectedMarkerIndex+=1;
		}
		Marker marker=ShareVariable.arrMarker.get(selectedMarkerIndex);
		setMapCenter(marker.getPosition().latitude,marker.getPosition().longitude,15);
		marker.showInfoWindow();
		((ScrollView) ((FrameLayout)scForPI.getChildAt(1)).getChildAt(0)).scrollTo(0,(int) ((screenW/2*selectedMarkerIndex)- screenW*0.2));
		_btnTakeMeThere.setVisibility(View.VISIBLE);
	}
	public void selectPreviouseMarker(){
		if(this.selectedMarkerIndex==-1){
			this.selectedMarkerIndex=ShareVariable.arrMarker.size()-1;
		}else if(this.selectedMarkerIndex==0){
			this.selectedMarkerIndex=ShareVariable.arrMarker.size()-1;
		}else{
			this.selectedMarkerIndex-=1;
		}
		Marker marker=ShareVariable.arrMarker.get(selectedMarkerIndex);
		setMapCenter(marker.getPosition().latitude,marker.getPosition().longitude,15);
		marker.showInfoWindow();
		((ScrollView) ((FrameLayout)scForPI.getChildAt(1)).getChildAt(0)).scrollTo(0,(int) ((screenW/2*selectedMarkerIndex) - screenW*0.2));
		_btnTakeMeThere.setVisibility(View.VISIBLE);
	}
	public void showButtonGetMore(){
		btnGetMore.clearAnimation();
		if(this.token.length()==0){
			return;
		}
		ScaleAnimation sanim= new ScaleAnimation((float) 1,(float) 1.2,(float) 1,(float) 1.2);
		sanim.setDuration(260);
		sanim.setFillAfter(true);
//		Log.d("test","test margin ori:"+-btnGetMore.getHeight());
		TranslateAnimation  tranAnim=new TranslateAnimation(0, btnGetMore.getHeight(),
				0,-btnGetMore.getHeight()
				);
		
		tranAnim.setDuration(260);
		tranAnim.setFillAfter(true);
		AnimationSet animSet=new AnimationSet(false);
		animSet.setFillAfter(true);
		animSet.addAnimation(sanim);
		animSet.addAnimation(tranAnim);
		isShowingGetMore=true;
		animSet.setAnimationListener(new Animation.AnimationListener(){
		    @Override
		    public void onAnimationStart(Animation arg0) {
		    	RelativeLayout.LayoutParams lp=(RelativeLayout.LayoutParams) btnGetMore.getLayoutParams();
		    }           
		    @Override
		    public void onAnimationRepeat(Animation arg0) {
		    }      

		    @Override
		    public void onAnimationEnd(Animation arg0) {
		    	RelativeLayout.LayoutParams lp=(RelativeLayout.LayoutParams) btnGetMore.getLayoutParams();
		    	btnGetMore.clearAnimation();
		    	lp=(RelativeLayout.LayoutParams) btnGetMore.getLayoutParams();
		    	lp=(LayoutParams) btnGetMore.getLayoutParams();
		    	lp.width=(int) (screenW*0.145*0.8*1.2);
    			lp.height=(int) (screenW*0.145*0.8*1.2);
    			lp.bottomMargin=(int) (screenW*0.145*0.8/2);
    			lp.leftMargin=(int) (screenW*0.145*0.8/2);
		    	btnGetMore.setLayoutParams(lp);
        		final Handler handler = new Handler();
        	    handler.postDelayed(new Runnable() {
        	      @Override
        	      public void run() {
        	    	  hideButtonGetMore(260);
        	      }
        	    }, 3500);
		    }
		});
		btnGetMore.setAnimation(animSet);
		animSet.startNow();
	}
	public void hideButtonGetMore(int duration){
    	RelativeLayout.LayoutParams lp=(LayoutParams) btnGetMore.getLayoutParams();
    	lp.width=((int) (screenW*0.145*0.8));
    	lp.height=((int) (screenW*0.145*0.8));
    	lp.bottomMargin=-lp.width/2;
    	lp.leftMargin=-lp.width/2;
    	btnGetMore.setLayoutParams(lp);
		ScaleAnimation sanim= new ScaleAnimation((float) 1.2,(float) 1,(float) 1.2,(float) 1);
		sanim.setDuration(duration);
		sanim.setFillAfter(true);
		TranslateAnimation  tranAnim=new TranslateAnimation(btnGetMore.getHeight(),0,
					-btnGetMore.getHeight(),0
				);
		tranAnim.setDuration(duration);
		tranAnim.setFillAfter(true);
		AnimationSet animSet=new AnimationSet(false);
		animSet.setFillAfter(true);
		animSet.addAnimation(sanim);
		animSet.addAnimation(tranAnim);
		animSet.setAnimationListener(new Animation.AnimationListener(){
		    @Override
		    public void onAnimationStart(Animation arg0) {
		    }           
		    @Override
		    public void onAnimationRepeat(Animation arg0) {
		    }           
		    @Override
		    public void onAnimationEnd(Animation arg0) {
		    	isShowingGetMore=false;
		    	btnGetMore.clearAnimation();
		    	if(token.length()==0){
		    		btnGetMore.setAlpha(0);
		    		btnGetMore.setClickable(false);
		    		btnGetMore.setVisibility(View.INVISIBLE);
		    	}
		    }
		});
		btnGetMore.startAnimation(animSet);
	}
	public void getDataMain(boolean isNew){
		if(isNew){
			this.token="";
			this.arrListResult.clear();
		}
		if(currentLocation ==  null){
			//peter modify pop up
			return;
		};
		String urlTempGet =urlGet.replace("{lat}",String.valueOf(currentLocation.getLatitude())).replace("{lng}",String.valueOf( currentLocation.getLongitude()));
		if(this.token.length()>0){
			urlTempGet+="&pagetoken="+this.token;
		}

		AsyncHttpClient client = new AsyncHttpClient();
 		client.get(urlTempGet, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		    	try{
		    		JSONObject res=new JSONObject(response);
		    		if(otherSource.length()>0){
		    			String urlOtherSource="http://"+ ShareVariable.domain+otherSource+"&lat="+String.valueOf(currentLocation.getLatitude())+"&lng="+String.valueOf(currentLocation.getLongitude());
		    			JSONObject jsOtherSource=new JSONObject(Util.getRemoteString(urlOtherSource));
		    			JSONArray jsArrayOtherSource=jsOtherSource.getJSONArray("results");
		    			if(res.getString("status")=="OK" && res.getJSONArray("results").length()>0){
		    				for(int i=0;i<jsArrayOtherSource.length();i++){
		    					res.getJSONArray("results").put(jsArrayOtherSource.get(i));
		    				}
		    			}else{
		    				res=jsOtherSource;
		    			}
		    		}
		    		generateList(res);
		    	}catch(Exception ex){
		    		ex.printStackTrace();
		    	}
		    }
		    @Override
		    public void onFailure(Throwable e, String response){
		    	Log.d("test","test async get google api error:"+ e.getMessage());
		    }
		});
	}
	public void setMapCenter(double lat,double lng,int zoomLevel){
		CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(lat,lng));
	    CameraUpdate zoom=CameraUpdateFactory.zoomTo(zoomLevel);
		map.moveCamera(zoom);
	    map.animateCamera(center,280,null);
	    
	}
	public void generateList(final JSONObject res){
	   try{
		   //data prepare
		   String status =res.getString("status");
		   if(status.equals("OK")){
			   
			   arrRes=res.getJSONArray("results");
			   for(int i=0;i<arrRes.length();i++){
				   	JSONObject item= arrRes.getJSONObject(i);
				   	JSONObject location=item.getJSONObject("geometry").getJSONObject("location");
				   	PlaceItem btn=new PlaceItem(this.getApplicationContext(),this.getWindow().getWindowManager().getDefaultDisplay().getWidth());
					btn.bottomLayout.title.setText(item.getString("name"));
					btn.name=item.getString("name");
					btn.lat=Double.parseDouble(location.getString("lat"));
					btn.lng=Double.parseDouble(location.getString("lng"));
					btn.address=item.getString("vicinity");
					if(item.has("rating")){
						btn.rateLayout.setRating((float) item.getDouble("rating")/5);
						btn.rateLayout.txtRate.setText(String.valueOf(item.getDouble("rating")));
					}
					if(item.has("photos")){
						String photoRef=item.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
						String url ="https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+photoRef+"&sensor=false&key="+ShareVariable.GOOGLE_KEY;
						btn.bg.setTag(url);
						new DownloadImagesTask().execute(btn.bg);
					}
					arrListResult.add(btn);
					btn.buildDist();
			   }

			   Collections.sort(arrListResult, new Comparator<PlaceItem>()  {
			        @Override
			        public int compare(PlaceItem s1, PlaceItem s2) {
			        	if(s1.dist<s2.dist){
			        		return -1;
			        		
			        	}else if(s1.dist>s2.dist){
			        		return 1;
			        	}else{
			        		return 0;
			        	}
			        }
			    });
			   
			   
			   //ui 
			   this.runOnUiThread(new Runnable(){
				   @Override
				   public void run(){
					   LinearLayout rlList =(LinearLayout) findViewById(com.planb.soda.R.id.ll_list);
					   rlList.setBackgroundColor(0xFFcccccc);
					   for(int i=0;i<arrListResult.size();i++){
						   RelativeLayout.LayoutParams lpForButton= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
							lpForButton.height=screenW/2;
							arrListResult.get(i).setLayoutParams(lpForButton);
							LatLng locate=new LatLng(arrListResult.get(i).lat,arrListResult.get(i).lng);
							if(map != null){
								Marker marker =map.addMarker(new MarkerOptions()
																	.position(locate)
																	.title(arrListResult.get(i).name)
																	.snippet(arrListResult.get(i).address)
															);
								ShareVariable.arrMarker.add(marker);
							}
							rlList.addView(arrListResult.get(i));
					   }
					   if(res.has("next_page_token")){
						   try {
								token=res.getString("next_page_token");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						   showContent(true);
					   }else{
						   token="";
						   showContent(false);
					   }
					   
					   if(ShareVariable.arrMarker.size()>0){
						   ShareVariable.arrMarker.get(0).showInfoWindow();
					   }
				   }
			   });
			   
		   }else{
			   Log.d("test","test:"+ status);
		   }
	   }catch(Exception ex){
		   Log.d("test","test:exception occur:" + ex.getMessage());
		   ex.printStackTrace();
	   }
	   
	}
    public void onDestroy() {  
        super.onDestroy();  
        ViewServer.get(this).removeWindow(this);  
   }  
  
    public void onResume() {  
        super.onResume();  
        ViewServer.get(this).setFocusedWindow(this);  
   }  
}
