package com.planb.soda;

import javax.xml.datatype.DatatypeConstants.Field;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import org.json.*;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		String jsonConfig ="{\"cate\":["
				+ "{\"name\":\"小吃\",\"keyword\":\"小吃\",\"type\":\"\",\"pic\":\"cate_food\",\"bg\":\"\",\"color\":\"#ffb7dd6c\"},"
				+ "{\"name\":\"景點\",\"keyword\":\"旅遊景點\",\"type\":\"\",\"pic\":\"cate_attraction\",\"bg\":\"tourist-attraction-gray-640x320\",\"color\":\"#ffabd156\"},"
				+ "{\"name\":\"餐廳\",\"keyword\":\"餐廳\",\"type\":\"\",\"pic\":\"cate_rest\",\"bg\":\"restaurants-gray-640x320.png\",\"color\":\"#ffb4da5f\"},"
				+ "{\"name\":\"咖啡\",\"keyword\":\"咖啡+茶+簡餐\",\"type\":\"\",\"pic\":\"cate_cafe\",\"bg\":\"coffee-gray-640x320.png\",\"color\":\"#ffabd156\"},"
				+ "{\"name\":\"ATM\",\"keyword\":\"提款機|郵局\",\"type\":\"\",\"pic\":\"cate_atm\",\"bg\":\"atm-gray-640x320.png\",\"color\":\"#ffbcda78\",\"other-source\":\"/controller/mobile/place.aspx?action=get-atm\"},"
				+ "{\"name\":\"旅館\",\"keyword\":\"\",\"type\":\"hotel\",\"pic\":\"cate_hotel\",\"bg\":\"hotel-gray-640x320.png\",\"color\":\"#ffb9dd57\"},"
				+ "{\"name\":\"加油站\",\"keyword\":\"\",\"type\":\"gas\",\"pic\":\"cate_gas\",\"bg\":\"hotel-gray-640x320.png\",\"color\":\"#ffb7dd6c\",\"other-source\":\"/controller/mobile/place.aspx?action=get-gas\"},"
				+ "{\"name\":\"租車\",\"keyword\":\"\",\"type\":\"gas\",\"pic\":\"cate_rental\",\"bg\":\"hotel-gray-640x320.png\",\"color\":\"#ffabd156\",\"other-source\":\"/controller/mobile/place.aspx?action=get-rental\"}"
				+ "]}";
		
		try{
			JSONObject config=new JSONObject(jsonConfig);
			int screenW=getWindowManager().getDefaultDisplay().getWidth();
			for(int i=0;i<config.getJSONArray("cate").length();i++){
				JSONObject item=(JSONObject) config.getJSONArray("cate").get(i);
				PlaceCateButton btn=new PlaceCateButton(this.getApplicationContext());
				RelativeLayout rl=(RelativeLayout) this.findViewById(R.id.rl);
			
				btn.setBackgroundColor(Color.parseColor(item.getString("color")));
				RelativeLayout.LayoutParams lpForButton= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
				lpForButton.width=screenW/2;
				lpForButton.height=screenW/2;
				lpForButton.setMargins(i%2*screenW/2,(int) Math.floor(i/2)*screenW/2, 0, 0);
				btn.setLayoutParams(lpForButton);
				btn.keyword=item.getString("keyword");
				btn.title=item.getString("name");
				btn.type=item.getString("type");
				int id =getApplicationContext().getResources().getIdentifier(item.getString("pic"), "drawable", getPackageName());
				Bitmap bm=BitmapFactory.decodeResource(getResources(),id);
				btn.setImageBitmap(Bitmap.createScaledBitmap(bm, 120, 120, false));
				btn.setOnClickListener(new View.OnClickListener() {
				    public void onClick(View v) {
				    	PlaceCateButton btn=(PlaceCateButton) v;
				    	Intent intentMain = new Intent(v.getContext(),com.planb.soda.ListActivity.class);
				    	intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    	intentMain.putExtra("title", btn.title);
				    	intentMain.putExtra("type", btn.type);
				    	intentMain.putExtra("keyword", btn.keyword);
				    	v.getContext().startActivity(intentMain);				    	
				    }
				});

				
				rl.addView(btn);
			}	
		}catch(Exception ex){
			Log.d("test","test exception:"+ex.getMessage()); 
		}
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	
}
