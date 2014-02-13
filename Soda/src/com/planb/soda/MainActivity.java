package com.planb.soda;

//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.*;

//import net.frakbot.imageviewex.ImageViewEx;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewServer.get(this).addWindow(this); 
		int screenW = getWindowManager().getDefaultDisplay().getWidth();
		int screenH = getWindowManager().getDefaultDisplay().getHeight();
		ShareVariable.screenW=screenW;
		ShareVariable.screenH=screenH;
		SlidingMenu slideMenu = new SlidingMenu(this);
		// setContentView(R.layout.activity_main);
		setContentView(slideMenu);
		ScrollView sc = new ScrollView(this);
		RelativeLayout rl = new RelativeLayout(this);
		sc.addView(rl);
		slideMenu.setContent(sc);
		String jsonConfig = "{\"cate\":["
				+ "{\"name\":\"�p�Y\",\"keyword\":\"�p�Y\",\"type\":\"\",\"pic\":\"cate_food\",\"bg\":\"\",\"color\":\"#ffb7dd6c\"},"
				+ "{\"name\":\"���I\",\"keyword\":\"�ȹC���I\",\"type\":\"\",\"pic\":\"cate_attraction\",\"bg\":\"tourist_attraction_gray_640x320\",\"color\":\"#ffabd156\"},"
				+ "{\"name\":\"�\�U\",\"keyword\":\"�\�U\",\"type\":\"\",\"pic\":\"cate_rest\",\"bg\":\"restaurants_gray_640x320\",\"color\":\"#ffb4da5f\"},"
				+ "{\"name\":\"�@��\",\"keyword\":\"�@��%2B��%2B²�\\",\"type\":\"\",\"pic\":\"cate_cafe\",\"bg\":\"coffee_gray_640x320\",\"color\":\"#ffabd156\"},"
				+ "{\"name\":\"ATM\",\"keyword\":\"���ھ�%7C�l��\",\"type\":\"\",\"pic\":\"cate_atm\",\"bg\":\"atm_gray_640x320\",\"color\":\"#ffbcda78\",\"other-source\":\"/controller/mobile/place.aspx?action=get-atm\"},"
				+ "{\"name\":\"���]\",\"keyword\":\"\",\"type\":\"hotel\",\"pic\":\"cate_hotel\",\"bg\":\"\",\"color\":\"#ffb9dd57\"},"
				+ "{\"name\":\"�[�o��\",\"keyword\":\"\",\"type\":\"gas\",\"pic\":\"cate_gas\",\"bg\":\"\",\"color\":\"#ffb7dd6c\",\"other-source\":\"/controller/mobile/place.aspx?action=get-gas\"},"
				+ "{\"name\":\"����\",\"keyword\":\"\",\"type\":\"gas\",\"pic\":\"cate_rental\",\"bg\":\"\",\"color\":\"#ffabd156\",\"other-source\":\"/controller/mobile/place.aspx?action=get-rental\"}"
				+ "]}";
		try {
			JSONObject config = new JSONObject(jsonConfig);

			for (int i = 0; i < config.getJSONArray("cate").length(); i++) {
				JSONObject item = (JSONObject) config.getJSONArray("cate").get(
						i);
				PlaceCateLayout btn = new PlaceCateLayout(
						this.getApplicationContext());
				btn.setBackgroundColor(Color.parseColor(item.getString("color")));
				RelativeLayout.LayoutParams lpForButton = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				lpForButton.width = screenW / 2;
				lpForButton.height = screenW / 2;
				lpForButton.setMargins(i % 2 * screenW / 2,
						(int) Math.floor(i / 2) * screenW / 2, 0, 0);
				btn.setLayoutParams(lpForButton);
				btn.keyword = item.getString("keyword");
				btn.title = item.getString("name");
				btn.type = item.getString("type");
				btn.bg=item.getString("bg");
				if (item.has("other-source")) {
					btn.otherSource = item.getString("other-source");
				}
				int id = getApplicationContext().getResources().getIdentifier(
						item.getString("pic"), "drawable", getPackageName());
				Bitmap bm = BitmapFactory.decodeResource(getResources(), id);
				btn.cateButton.setBackground(null);
				btn.cateButton.setImageBitmap(Bitmap.createScaledBitmap(bm, ShareVariable.screenW/6, ShareVariable.screenW/6,
						false));
				btn.txtTitle.setText(item.getString("name"));
				btn.txtTitle.setTextColor(0xFFFFFFFF);
				btn.setTxtTopMargin(screenW/2, ShareVariable.screenW/6);
				btn.cateButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						
						PlaceCateLayout btn = (PlaceCateLayout) v.getParent();
						Intent intentMain = new Intent(v.getContext(),
								com.planb.soda.ListActivity.class);
						intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intentMain.putExtra("title", btn.title);
						intentMain.putExtra("type", btn.type);
						intentMain.putExtra("keyword", btn.keyword);
						intentMain.putExtra("otherSource", btn.otherSource);
						intentMain.putExtra("bg", btn.bg);
						//sent user research;
						String ip = Util.getIPAddress(true);
						String url="http://"+ShareVariable.domain+ShareVariable.reportController+"?action=add-category-count&cate="+btn.title+"&creator_ip="+ip;
						AsyncHttpClient client = new AsyncHttpClient();
				 		client.get(url, new AsyncHttpResponseHandler() {
						    @Override
						    public void onSuccess(String response) {
						    	//Log.d("test","test success");
						    }
						    @Override
						    public void onFailure(Throwable e, String response){
						    	Log.d("test","test exception"+ e.getMessage());
						    	e.printStackTrace();
						    }
						});
						v.getContext().startActivity(intentMain);
						

					}
				});
				rl.addView(btn);
			}
		} catch (Exception ex) {
			Log.d("test", "test exception:" + ex.getMessage());
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.planb.soda.R.menu.main, menu);
		return true;
	}

	public static int getResId(String variableName, Class<?> c) {
		try {
			java.lang.reflect.Field idField = c.getDeclaredField(variableName);
			return idField.getInt(idField);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
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
