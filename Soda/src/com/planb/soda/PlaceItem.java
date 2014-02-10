package com.planb.soda;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

@SuppressLint("ViewConstructor")
public class PlaceItem extends RelativeLayout {
	public PlaceItemBottomLayout bottomLayout = null;
	public RateLayout rateLayout = null;
	public ImageView bg = null;
	public String address = "";
	public String name = "";
	public double dist = 0;
	public double lat = 0;
	public double lng = 0;

	public PlaceItem(Context context, int screenW) {
		super(context);

		this.setClickable(true);
		this.setDrawingCacheEnabled(true);

		bg = new ImageView(context);
		bg.setScaleType(ScaleType.CENTER_CROP);
		LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		bg.setLayoutParams(rlp);
		
		bottomLayout = new PlaceItemBottomLayout(this.getContext(), screenW);
		rateLayout = new RateLayout(context, screenW);
		
		this.addView(bg);
		this.addView(rateLayout);
		this.addView(bottomLayout);
		this.setBackgroundColor(0xFFCCCCCC);
	}


	public void buildDist() {
		LocationManager lm = (LocationManager) this.getContext()
				.getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		Location loc = ShareVariable.getLocation(lm);
		Location distLoc = new Location("");
		distLoc.setLatitude(lat);
		distLoc.setLongitude(lng);
		final float defaultDistance = distLoc.distanceTo(loc);
		dist = distLoc.distanceTo(loc);
		bottomLayout.dist.setText(getDistText(defaultDistance));
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			URI website = new URI(
					"https://maps.googleapis.com/maps/api/distancematrix/json?origins="
							+ String.valueOf(loc.getLatitude()) + ","
							+ String.valueOf(loc.getLongitude())
							+ "&destinations=" + String.valueOf(lat) + ","
							+ String.valueOf(lng)
							+ "&mode=walk&sensor=false&language=zh-TW");
			request.setURI(website);
			HttpResponse response = httpclient.execute(request);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuilder builder = new StringBuilder();
			String aux = "";
			while ((aux = in.readLine()) != null) {
				builder.append(aux);
			}
			String result = builder.toString();

			JSONObject res = new JSONObject(result);
			address = res.getString("destination_addresses");
			JSONArray arrRows = res.getJSONArray("rows");
			if (arrRows.length() > 0) {
				JSONArray arrElements = arrRows.getJSONObject(0).getJSONArray(
						"elements");
				if (arrElements.length() > 0) {
					JSONObject element = arrElements.getJSONObject(0);
					dist = Float.parseFloat(element.getJSONObject("distance")
							.getString("value"));
					bottomLayout.dist.setText(getDistText(Float
							.parseFloat(element.getJSONObject("distance")
									.getString("value"))));
					// bottomLayout.dist.setText("a" + String.valueOf(dist));
				} else {
					bottomLayout.dist.setText(getDistText(defaultDistance));
					// bottomLayout.dist.setText(String.valueOf(dist));
				}
			} else {
				bottomLayout.dist.setText(getDistText(defaultDistance));
				// bottomLayout.dist.setText(String.valueOf(dist));
			}
		} catch (Exception ex) {
			bottomLayout.dist.setText(getDistText(defaultDistance));
			// bottomLayout.dist.setText(String.valueOf(dist));
		}
		// Log.d("test","test build finish");
	}

	public String getDistText(float dist) {
		if (dist > 1000) {
			float res = Math.round(Math.round(dist / 1000) * 100) / 100;
			return String.valueOf(res) + " ¤½¨½";
		} else {
			int res =(int) Math.round(dist);
			return String.valueOf(res) + " ¤½¤Ø";
		}
	}

}
