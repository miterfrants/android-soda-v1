package com.planb.soda;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;

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

public class Util {
	public static String tag="com.planb.soda.Util";
	public static LocationListener lmListener=null;
	public static LocationManager lm=null;
    /**
     * Convert byte array to hex string
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for(int idx=0; idx < bytes.length; idx++) {
            int intVal = bytes[idx] & 0xff;
            if (intVal < 0x10) sbuf.append("0");
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    /**
     * Get utf8 byte array.
     * @param str
     * @return  array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String str) {
        try { return str.getBytes("UTF-8"); } catch (Exception ex) { return null; }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     * @param filename
     * @return  
     * @throws java.io.IOException
     */
    public static String loadFileAsString(String filename) throws java.io.IOException {
        final int BUFLEN=1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8=false;
            int read,count=0;           
            while((read=is.read(bytes)) != -1) {
                if (count==0 && bytes[0]==(byte)0xEF && bytes[1]==(byte)0xBB && bytes[2]==(byte)0xBF ) {
                    isUTF8=true;
                    baos.write(bytes, 3, read-3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count+=read;
            }
            return isUTF8 ? new String(baos.toByteArray(), "UTF-8") : new String(baos.toByteArray());
        } finally {
            try{ is.close(); } catch(Exception ex){} 
        }
    }

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface 
     * @return  mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));       
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    /**
     * Get IP address from first non-localhost interface
     * @param ipv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        if (useIPv4) {
                            if (isIPv4) 
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
    
    public static String getRemoteString(String url)
    {

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url); 
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                String result= convertStreamToString(instream);
                instream.close();
                return result;
            }
        } catch (Exception e) {e.printStackTrace();}
		return "";
    }

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    public static Location getLocation(LocationManager lm){
		return ShareVariable.currentLocation;
	}
	 
	 public static void checkLocationServices(final Activity context){
	     boolean gps_enabled,network_enabled;
     	 lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
     	 gps_enabled=false;
	     network_enabled =false;
	     try{
	        	gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	     }catch(Exception ex){}
	        
	     try{
	        	network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	      }catch(Exception ex){}
	       if(!gps_enabled && !network_enabled){
	    	   Builder dialog = new AlertDialog.Builder(context);
	            dialog.setMessage("GPS 未開啟");
	            dialog.setPositiveButton("開啟", new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
	                    // TODO Auto-generated method stub
	                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                    context.startActivity(myIntent);
	                    //get gps
	                }
	            });
	            dialog.setNegativeButton("關閉", new DialogInterface.OnClickListener() {

	                @Override
	                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
	                    // TODO Auto-generated method stub

	                }
	            });
	            dialog.show();

	        }
	 }
	 public static LocationListener getNewListener(){
	    	return new LocationListener(){

				@Override
				public void onLocationChanged(Location location) {
					// TODO Auto-generated method stub
					ShareVariable.currentLocation=location;
				}

				@Override
				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub
					//ShareVariable.currentLocation= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				}

				@Override
				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub
					//ShareVariable.currentLocation= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				}

				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// TODO Auto-generated method stub
					//ShareVariable.currentLocation= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				}
	    		
	    	};
	    }
	    public static void stopUpdateLocation(){
	    	if(lmListener!=null){
	    	lm.removeUpdates(lmListener);
	    	lmListener=null;
	    	}
	    }
	    
	    public static Location getLocation(Activity context) {
	    	Location location=null;
	    	lmListener=Util.getNewListener();
	        try {
	        	
	            lm= (LocationManager) context
	                    .getSystemService(Context.LOCATION_SERVICE);

	            // getting GPS status
	            boolean isGPSEnabled = lm
	                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

	            // getting network status
	            boolean isNetworkEnabled = lm
	                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

	            if (!isGPSEnabled && !isNetworkEnabled) {
	                // no network provider is enabled
	            } else {
	                if (isNetworkEnabled) {
	                	
	                    if (lm != null) {
	                    	lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,200,5,lmListener);
	                        location = lm
	                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	                    }
	                }
	                // if GPS Enabled get lat/long using GPS Services
	                if (isGPSEnabled) {
	                    if (location == null) {
	                        if (lm != null) {
	                        	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,200,5,lmListener);
	                            location = lm 
	                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                        }
	                    }
	                }
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return location;
	    }
}
