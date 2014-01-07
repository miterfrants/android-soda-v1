package com.planb.soda;

import android.app.ActionBar;
import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.*;

public class ListActivity extends Activity implements LocationListener {
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;	
	public static Location currentLocation=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar bar=getActionBar();
		bar.setTitle(getIntent().getStringExtra("title"));
		
		if(this.getLocation() !=  null){
			AsyncHttpClient client = new AsyncHttpClient();
			client.get("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+String.valueOf(currentLocation.getLatitude())+","+String.valueOf(currentLocation.getLongitude())+"&radius=500&keyword=%E9%A4%90%E5%BB%B3&sensor=false&key=AIzaSyCYM1UUnXbgP3eD__x2EjIugNOy-vE3McY&rankBy=prominence&types=", new AsyncHttpResponseHandler() {
			    @Override
			    public void onSuccess(String response) {
			        Log.d("test","test:"+response);
			    } 
			});
		}else{
			Log.d("test","test: location is null");
			//Location is null
		};
		
		//Log.d("test","test2:"+String.valueOf(this.getLoaction().getLatitude()));
		
	}
	
	public Location getLocation(){
		LocationManager lm=(LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);
		boolean isGPSEnable= lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if(isGPSEnable){
			
			lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
			
			if (lm != null) {
				currentLocation= lm
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
		}
		return currentLocation;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		currentLocation=location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
}
