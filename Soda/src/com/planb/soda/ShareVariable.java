package com.planb.soda;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

public class ShareVariable {
	 public static String GOOGLE_KEY="AIzaSyCYM1UUnXbgP3eD__x2EjIugNOy-vE3McY";
	 public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	 public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
 	 public static String domain="www.planb-on.com";
 	 public static String reportController="/controller/mobile/report.aspx";
	 public static Location currentLocation=null;
	 public static boolean isListActivity=false;
	 public static SupportMapFragment mapFr=null;
	 public static List<Marker> arrMarker=new ArrayList<Marker>();
	 public static int screenW=0;
	 public static int screenH=0;
	 public static int selectedMarkerIndex=0;
	 public static boolean isChangeMarkerIndex = true;
}