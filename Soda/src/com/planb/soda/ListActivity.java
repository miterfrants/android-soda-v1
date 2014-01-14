package com.planb.soda;

import android.app.ActionBar;
import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.json.*;

import com.loopj.android.http.*;

public class ListActivity extends Activity {
	
	private int screenW=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		screenW=getWindowManager().getDefaultDisplay().getWidth();
		ActionBar bar=getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("Soda | "+getIntent().getStringExtra("title"));
		setContentView(R.layout.activity_list);
		LocationManager lm=(LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);
		Location currentLocation=ShareVariable.getLocation(lm);
		if(currentLocation !=  null){
			AsyncHttpClient client = new AsyncHttpClient();
			Log.d("test","https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+String.valueOf(currentLocation.getLatitude())+","+String.valueOf(currentLocation.getLongitude())+"&radius=500&keyword=%E9%A4%90%E5%BB%B3&sensor=false&key="+ShareVariable.GOOGLE_KEY+"&rankBy=prominence&types=");
			client.get("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+String.valueOf(currentLocation.getLatitude())+","+String.valueOf(currentLocation.getLongitude())+"&radius=500&keyword=%E9%A4%90%E5%BB%B3&sensor=false&key="+ShareVariable.GOOGLE_KEY+"&rankBy=prominence&types=", new AsyncHttpResponseHandler() {
			    @Override
			    public void onSuccess(String response) {
			    	try{
			    		JSONObject res=new JSONObject(response);
			    		generateList(res);
			    	}catch(Exception ex){
			    		Log.d("test","test exception occur:"+ex.getMessage());
			    	}
			    }
			    
			    @Override
			    public void onFailure(Throwable e, String response){
			    	Log.d("test","test async get google api error:"+ e.getMessage());
			    }
			});
		}else{
			Log.d("test","test: location is null");
			//Location is null
		};
		
		//Log.d("test","test2:"+String.valueOf(this.getLoaction().getLatitude()));
		
	}
	public void generateList(JSONObject res){
	   RelativeLayout rlList =(RelativeLayout) findViewById(R.id.rl_list);
	   try{
		   String status =res.getString("status");
		   if(status.equals("OK")){
			   JSONArray arrResult=res.getJSONArray("results");
			   Log.d("test","test:arrResult length:"+arrResult.length());
			   for(int i=0;i<arrResult.length();i++){
				   JSONObject item= arrResult.getJSONObject(i);
				   PlaceItem btn=new PlaceItem(this.getApplicationContext(),this.getWindow().getWindowManager().getDefaultDisplay().getWidth());
				   RelativeLayout.LayoutParams lpForButton= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
					lpForButton.height=screenW/2;
					lpForButton.setMargins(0,i*lpForButton.height, 0, 0);
					btn.setLayoutParams(lpForButton);
					btn.bottomLayout.title.setText(item.getString("name"));
					//dist
					//JSONObject location=item.getJSONObject("geometry").getJSONObject("location");
					//btn.getDist(Double.parseDouble(location.getString("lat")), Double.parseDouble(location.getString("lng")));
					//String urlDist="https://maps.googleapis.com/maps/api/distancematrix/json?origins=%.8F,%.8F&destinations=%.8F,%.8F&mode=walk&language=zh-TW&sensor=false";
					//btn.bottomLayout.dist.setText("¶ZÂ÷");
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
		   }else{
			   Log.d("test","test:"+ status);
		   }
	   }catch(Exception ex){
		   Log.d("test","test:exception occur:" + ex.getMessage());
		   ex.printStackTrace();
	   }
	   
	}
	

}
