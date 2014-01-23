package com.planb.soda;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

@SuppressLint("ViewConstructor")
public class PlaceItem extends RelativeLayout {
	public PlaceItemBottomLayout bottomLayout=null;
	public RateLayout rateLayout=null;
	public ImageView bg=null;
	public String address="";
	public String name="";
	public double lat=0;
	public double lng=0;
	public PlaceItem(Context context,int screenW) {
		super(context);
		
		this.setClickable(true);
		rateLayout= new RateLayout(context,screenW);
		RelativeLayout.LayoutParams rlpForRateLayout=  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.MATCH_PARENT);
		rlpForRateLayout.height=(int) (screenW*0.09375);
		rlpForRateLayout.width=(int) (screenW*0.21875);
		rlpForRateLayout.setMargins((int) (screenW*0.765625),(int) (screenW*0.29375), 0, 0);
		rateLayout.setLayoutParams(rlpForRateLayout);
		
		bg= new ImageView(context);
		bg.setScaleType(ScaleType.CENTER_CROP);
		RelativeLayout.LayoutParams rlp=  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
		bg.setLayoutParams(rlp);
		
		this.addView(bg);
		this.addView(rateLayout);
		
		
		bottomLayout=new PlaceItemBottomLayout(this.getContext(),screenW);
		this.addView(bottomLayout);
		
		
		this.setBackgroundColor(0xFFCCCCCC);
		
		// TODO Auto-generated constructor stub
	}
	
    @Override
    public void dispatchDraw(Canvas canvas) {
    	super.dispatchDraw(canvas);
    	
		RelativeLayout.LayoutParams rlpForBottomLayout=  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlpForBottomLayout.height= (int) (this.getWidth()*0.09375);
		rlpForBottomLayout.setMargins(0, this.getHeight()-rlpForBottomLayout.height, 0, 0);
		bottomLayout.setLayoutParams(rlpForBottomLayout);
		
    }
    
    public void getDist(){
    	AsyncHttpClient client = new AsyncHttpClient();
    	
    	LocationManager lm=(LocationManager) this.getContext().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    	Location loc=ShareVariable.getLocation(lm);
    	Location distLoc=new Location("");
    	distLoc.setLatitude(lat);
    	distLoc.setLongitude(lng);
    	final float defaultDistance =distLoc.distanceTo(loc);
    	//Log.d("test","test dist url:"+ "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+String.valueOf(loc.getLatitude())+","+String.valueOf(loc.getLongitude())+"&destinations="+String.valueOf(lat)+","+String.valueOf(lng)+"&mode=walk&sensor=false&language=zh-TW");
    	client.get("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+String.valueOf(loc.getLatitude())+","+String.valueOf(loc.getLongitude())+"&destinations="+String.valueOf(lat)+","+String.valueOf(lng)+"&mode=walk&sensor=false&language=zh-TW", new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		    	try{
		    		//Log.d("test","test:"+response);
		    		JSONObject res=new JSONObject(response);
		    		address=res.getString("destination_addresses");
		    		JSONArray arrRows =res.getJSONArray("rows");
		    		if(arrRows.length()>0){
		    			JSONArray arrElements =arrRows.getJSONObject(0).getJSONArray("elements");
		    			if(arrElements.length()>0){
		    				JSONObject element=arrElements.getJSONObject(0);
		    				bottomLayout.dist.setText(element.getJSONObject("distance").getString("text"));
		    			}else{
		    				bottomLayout.dist.setText(getDistText(defaultDistance));
		    			}
		    		}else{
		    			bottomLayout.dist.setText(getDistText(defaultDistance));
		    		}
		    		//this.bottomLayout.dist.setText();
		    	}catch(Exception ex){
		    		try{
		    			bottomLayout.dist.setText(getDistText(defaultDistance));
		    		}catch(Exception ex1){
		    			Log.d("test","exception:"+ex1.getMessage());
		    			ex1.printStackTrace();
		    		}
		    		
		    	}
		    }
		    
		    @Override
		    public void onFailure(Throwable e, String response){
		    	bottomLayout.dist.setText(getDistText(defaultDistance));
		    	Log.d("test","test async get google api error:"+ e.getMessage());
		    }
		    
		    
		    
		    
		});
    	
    }
    public String getDistText(float dist){
    	if(dist>1000){
    		float res=Math.round(Math.round(dist/1000)*100)/100;
    		return String.valueOf(res) + " ����";
    	}else{
    		float res=Math.round(dist*100)/100;
    		return String.valueOf(res) + " ����";
    	}
    }

}
