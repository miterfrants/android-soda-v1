package com.planb.soda;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import org.json.*;
import com.dmobile.pulltorefresh.*;

import com.loopj.android.http.*;

public class ListActivity extends Activity {
	private String token="";
	private int screenW=0;
	private Button btnGetMore;
	private String urlGet;
	private JSONArray arrRes;
	private String keyword;
	private String type;
	private String otherSource;
	public boolean isShowingGetMore=false;
	@Override 
	protected void onStart(){
		super.onStart();
		hideButtonGetMore();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("test","on create");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		keyword=getIntent().getStringExtra("keyword");
		type=getIntent().getStringExtra("type");
		otherSource=getIntent().getStringExtra("otherSource");
		urlGet="https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
				+ "location={lat},{lng}&radius=500&keyword="+keyword+"&sensor=false&"
				+ "key="+ShareVariable.GOOGLE_KEY+"&rankBy=prominence&types="+ type;
		screenW=getWindowManager().getDefaultDisplay().getWidth();
		//screenH=getWindowManager().getDefaultDisplay().getHeight();
		btnGetMore=(Button) this.findViewById(R.id.btn_get_more);
		RelativeLayout.LayoutParams rlpForBtnGetMore= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForBtnGetMore.width=((int) (screenW*0.125));
		rlpForBtnGetMore.height=((int) (screenW*0.125));
		rlpForBtnGetMore.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		//peter modify
		btnGetMore.setTextSize(14);
		btnGetMore.setVisibility(0);
		btnGetMore.setLayoutParams(rlpForBtnGetMore);
		btnGetMore.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	getData();
		    }
		});
		ActionBar bar=getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("Soda | "+getIntent().getStringExtra("title"));
		getData();
		
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
		
		Log.d("test","test hideButtonGetMore");
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
	public void getData(){
		LocationManager lm=(LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);
		Location currentLocation=ShareVariable.getLocation(lm);
		if(currentLocation ==  null){
			//peter modify pop up
			Log.d("test","test: location is null");
			return;
			//Location is null
		};
		String urlTempGet =urlGet.replace("{lat}",String.valueOf( currentLocation.getLatitude())).replace("{lng}",String.valueOf( currentLocation.getLongitude()));
		if(this.token.length()>0){
			Log.d("test","test next token:");
			urlTempGet+="&pagetoken="+this.token;
		}
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(urlTempGet, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		    	try{
		    		Log.d("test","test response:"+response);
		    		JSONObject res=new JSONObject(response);
		    		generateList(res);
		    	}catch(Exception ex){
		    		Log.d("test","test exception occur:"+ex.getMessage());
		    		ex.printStackTrace();
		    	}
		    }
		    @Override
		    public void onFailure(Throwable e, String response){
		    	Log.d("test","test async get google api error:"+ e.getMessage());
		    }
		});
	}

	public void generateList(JSONObject res){
	   RelativeLayout rlList =(RelativeLayout) findViewById(R.id.rl_list);
	   try{
		   String status =res.getString("status");
		   if(res.has("next_page_token")){
			   this.token=res.getString("next_page_token");
			   btnGetMore.setVisibility(1);
		   }else{
			   btnGetMore.setVisibility(0);
		   }
		   if(status.equals("OK")){
			   if(arrRes !=null && arrRes.length()>0){
				   JSONArray tempArr=res.getJSONArray("results");
				   for(int i=0;i< tempArr.length();i++){
					   arrRes.put(tempArr.get(i));   
				   }
				   
			   }else{
				   arrRes=res.getJSONArray("results");   
			   }
			   Log.d("test","test:arrResult length:"+arrRes.length());
			   for(int i=0;i<arrRes.length();i++){
				   JSONObject item= arrRes.getJSONObject(i);
				   PlaceItem btn=new PlaceItem(this.getApplicationContext(),this.getWindow().getWindowManager().getDefaultDisplay().getWidth());
				   RelativeLayout.LayoutParams lpForButton= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
					lpForButton.height=screenW/2;
					lpForButton.setMargins(0,i*lpForButton.height, 0, 0);
					btn.setLayoutParams(lpForButton);
					btn.bottomLayout.title.setText(item.getString("name"));
					JSONObject location=item.getJSONObject("geometry").getJSONObject("location");
					btn.lat=Double.parseDouble(location.getString("lat"));
					btn.lng=Double.parseDouble(location.getString("lng"));
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
				   rlList.addView(btn);
				   
			   }
			   //last child is button
			   for(int i=0;i<rlList.getChildCount();i++){
				   PlaceItem child=(PlaceItem) rlList.getChildAt(i);
			       child.getDist();   
			  }
			  this.findViewById(R.id.btn_get_more).bringToFront();
		   }else{
			   Log.d("test","test:"+ status);
		   }
	   }catch(Exception ex){
		   Log.d("test","test:exception occur:" + ex.getMessage());
		   ex.printStackTrace();
	   }
	   
	}
	

}
