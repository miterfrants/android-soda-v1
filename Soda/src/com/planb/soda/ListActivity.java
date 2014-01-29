package com.planb.soda;


import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;

import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.loopj.android.http.*;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

@SuppressLint("InlinedApi")
public class ListActivity extends FragmentActivity {
	public String token="";
	private int screenW=0;
	private Button btnGetMore;
	private String urlGet;
	private JSONArray arrRes;
	private String keyword;
	private String type;
	private String otherSource;
	private RelativeLayout rlForContent=null;
	private ScrollViewForPlaceItem scForPI =null;
	public Location currentLocation=null;
	private GoogleMap map=null;
	public boolean isShowingGetMore=false;
	public int selectedMarkerIndex=-1;
	private Button _btnNext=null;
	private Button _btnPreviouse=null;
	private Button _btnTakeMeThere =null;
	public GifMovieView ldImg=null;
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
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		setContentView(com.planb.soda.R.layout.activity_list);
		rlForContent= new RelativeLayout(this);
		
		//loading bar 
		GifMovieView ldImg=new GifMovieView(this);
		ldImg.setMovieResource(R.drawable.loading);
		RelativeLayout.LayoutParams rlpForImg= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForImg.addRule(RelativeLayout.CENTER_IN_PARENT);
		ldImg.setLayoutParams(rlpForImg);
		rlForContent.addView(ldImg);

		
		LocationManager lm=(LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);
		currentLocation=ShareVariable.getLocation(lm);
		if(currentLocation==null){
			Toast toast = Toast.makeText(this, "請先開啟 GPS 定位功能。", 1000);
    		toast.show();
			return;
		}
		SlidingMenu slideMenu =(SlidingMenu) this.findViewById(com.planb.soda.R.id.rl_for_activity_list);
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
		rlpForBtnGetMore.width=((int) (screenW*0.125));
		rlpForBtnGetMore.height=((int) (screenW*0.125));
		rlpForBtnGetMore.bottomMargin=10;
		rlpForBtnGetMore.leftMargin=10;
		rlpForBtnGetMore.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		//peter modify
		if(ShareVariable.screenW==1080){
			btnGetMore.setTextSize(10);
		}else if(ShareVariable.screenW==720 || ShareVariable.screenW==768){
			btnGetMore.setTextSize(10);	
		}else{
			btnGetMore.setTextSize((int) (screenW * 0.01688888));
		}
		
		btnGetMore.setVisibility(View.INVISIBLE);
		btnGetMore.setTextColor(0xFFFFFFFF);
		btnGetMore.setText("更多");
		btnGetMore.setLayoutParams(rlpForBtnGetMore);
		btnGetMore.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	RelativeLayout rlList =(RelativeLayout) findViewById(com.planb.soda.R.id.rl_list);
		    	rlList.removeAllViews();
		    	getData(false);
		    }
		});
		rlForContent.addView(btnGetMore);

		//list containerscForPI
		this.scForPI =(ScrollViewForPlaceItem) LayoutInflater.from(this).inflate(com.planb.soda.R.layout.scroll_view_for_place_item,null);
		rlForContent.addView(scForPI);
		scForPI.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
		scForPI.setPersistentDrawingCache(ViewGroup.PERSISTENT_SCROLLING_CACHE);
		scForPI.setAlwaysDrawnWithCacheEnabled(true);
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
		//尺寸調整e
		
		
		
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
	    //Log.d("test","test class:"+ String.valueOf(slideMenu.getSecondaryMenu().getHeight()));
		//slideMenu.setSlideDirection(SlideMenu.FLAG_DIRECTION_LEFT);
	    slideMenu.setMode(SlidingMenu.RIGHT);
	    slideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
	    slideMenu.setBehindWidth((int) (ShareVariable.screenW*0.9));
	    
	    		
		
		ActionBar bar=getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("Soda | "+getIntent().getStringExtra("title"));
		Thread thread = new Thread()
		{
		    @Override
		    public void run() {
		        try {
		            getData(true);
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		};
		thread.start();
		//getData(true);
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
		isShowingGetMore=true;
		ScaleAnimation sanim= new ScaleAnimation((float) 0.8,(float) 1,(float) 0.8,(float) 1);
		sanim.setDuration(260);
		sanim.setFillAfter(true);
		TranslateAnimation  tranAnim=new TranslateAnimation((float) (-screenW*0.0625),10 ,
				(float) (screenW*0.0625),-10
				);
		tranAnim.setDuration(260);
		tranAnim.setFillAfter(true);
		AnimationSet animSet=new AnimationSet(false);
		animSet.setFillAfter(true);
		animSet.addAnimation(sanim);
		animSet.addAnimation(tranAnim);
		btnGetMore.setAnimation(animSet);
		animSet.setAnimationListener(new Animation.AnimationListener(){
		    @Override
		    public void onAnimationStart(Animation arg0) {
		    }           
		    @Override
		    public void onAnimationRepeat(Animation arg0) {
		    }           
		    @Override
		    public void onAnimationEnd(Animation arg0) {
        		final Handler handler = new Handler();
        	    handler.postDelayed(new Runnable() {
        	      @Override
        	      public void run() {
        	    	  hideButtonGetMore();
        	      }
        	    }, 3500);
		    }
		});
	}
	public void hideButtonGetMore(){
		
		//Log.d("test","test hideButtonGetMore");
		ScaleAnimation sanim= new ScaleAnimation((float) 1,(float) 0.8,(float) 1,(float) 0.8);
		sanim.setDuration(260);
		sanim.setFillAfter(true);
		TranslateAnimation  tranAnim=new TranslateAnimation(0, (float) (-screenW*0.0625),
					0,(float) (screenW*0.0625)
				);
		tranAnim.setDuration(260);
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
		    }
		});
		btnGetMore.startAnimation(animSet);
	}
	public void getData(boolean isNew){
		Log.d("test","test getData:true");
		if(isNew){
			this.token="";
		}
		if(currentLocation ==  null){
			//peter modify pop up
			//Log.d("test","test: location is null");
			//Location is null
			return;
		};
		String urlTempGet =urlGet.replace("{lat}",String.valueOf(currentLocation.getLatitude())).replace("{lng}",String.valueOf( currentLocation.getLongitude()));
		if(this.token.length()>0){
			//Log.d("test","test next token:");
			urlTempGet+="&pagetoken="+this.token;
		}
		AsyncHttpClient client = new AsyncHttpClient();
		Log.d("test","test get more:"+urlTempGet);
 		client.get(urlTempGet, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		    	try{
		    		//Log.d("test","test response:"+response);
		    		JSONObject res=new JSONObject(response);
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
			   if(arrRes !=null && arrRes.length()>0){
				   JSONArray tempArr=res.getJSONArray("results");
				   for(int i=0;i< tempArr.length();i++){
					   arrRes.put(tempArr.get(i));   
				   }
			   }else{
				   arrRes=res.getJSONArray("results");   
			   }
			   
			   
			   for(int i=0;i<arrRes.length();i++){
				   JSONObject item= arrRes.getJSONObject(i);
				   JSONObject location=item.getJSONObject("geometry").getJSONObject("location");
				   PlaceItem btn=new PlaceItem(this.getApplicationContext(),this.getWindow().getWindowManager().getDefaultDisplay().getWidth());
					btn.bottomLayout.title.setText(item.getString("name"));
					btn.name=item.getString("name");
					btn.lat=Double.parseDouble(location.getString("lat"));
					btn.lng=Double.parseDouble(location.getString("lng"));
					btn.address=item.getString("vicinity");

					//String urlDist="https://maps.googleapis.com/maps/api/distancematrix/json?origins=%.8F,%.8F&destinations=%.8F,%.8F&mode=walk&language=zh-TW&sensor=false";
					Log.d("test","test item name:"+item.getString("name"));
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
					   btnGetMore.bringToFront();
					   if(res.has("next_page_token")){
						   try {
								token=res.getString("next_page_token");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					   }else{
						   btnGetMore.setVisibility(View.INVISIBLE);
					   }
					   RelativeLayout rlList =(RelativeLayout) findViewById(com.planb.soda.R.id.rl_list);
					   rlList.setBackgroundColor(0xFFcccccc);
					   for(int i=0;i<arrListResult.size();i++){
						   RelativeLayout.LayoutParams lpForButton= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
						   
							lpForButton.height=screenW/2;
							lpForButton.setMargins(0,i*lpForButton.height, 0, 0);
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
	

}
