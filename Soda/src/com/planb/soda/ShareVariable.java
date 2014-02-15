package com.planb.soda;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

public class ShareVariable {
	 public static String GOOGLE_KEY="AIzaSyCYM1UUnXbgP3eD__x2EjIugNOy-vE3McY";
	 private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
 	 private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
 	 public static String domain="www.planb-on.com";
 	 public static String reportController="/controller/mobile/report.aspx";
	 public static Location currentLocation=null;
	 public static LocationListener listener=null;
	 public static SupportMapFragment mapFr=null;
	 public static List<Marker> arrMarker=new ArrayList<Marker>();
	 public static int screenW=0;
	 public static int screenH=0;
	 public static Location getLocation(LocationManager lm){
			boolean isGPSEnable= lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if(isGPSEnable){
				if(listener ==null){
					listener=new LocationListener(){
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
					};
				}
				lm.requestLocationUpdates(
	                    LocationManager.GPS_PROVIDER,
	                    MIN_TIME_BW_UPDATES,
	                    MIN_DISTANCE_CHANGE_FOR_UPDATES, listener);
				
				if (lm != null) {
					currentLocation= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	            }
			}
			return currentLocation;
		}	
}