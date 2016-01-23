package com.kartikk.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class HttpReq extends AsyncTask<String, Void , String> {
	public static String [] stringArray;
	double lat2=0.00,lon2=0.00;
	public static Double bearing=0.00;
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();

		HttpGet httpGet = new HttpGet("https://maps.googleapis.com/maps/api/directions/json?origin="+MainActivity.lat+","+MainActivity.lng+"&destination="+MainActivity.destination+"&mode=walking&key=AIzaSyBLQHBnivZ-Kd6nVCYliCyMssZYhJtQAzc");
		try{
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if(statusCode == 200){
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(content));
					String line;
					while((line = reader.readLine()) != null){
						builder.append(line);
					}
				}
				//System.out.println(builder.toString());
			} else {
				Log.e(MainActivity.class.toString(),"Failed to get JSON object");
			}
		}catch(ClientProtocolException e){
			e.printStackTrace();
			return null;
		} catch (IOException e){
			e.printStackTrace();
			return null;
		}
		return builder.toString();
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		String distance,directionfortv="",facingfortv="";
		try{
			JSONObject jsonObject = new JSONObject(result);

			JSONArray a1= new JSONArray(jsonObject.get("routes").toString());
			JSONObject a2=new JSONObject( a1.get(0).toString());
			JSONArray a3 = new JSONArray(a2.get("legs").toString());
			JSONObject a4 = new JSONObject(a3.get(0).toString());
			JSONArray a5 = new JSONArray(a4.get("steps").toString());
			//			JSONObject a8 = new JSONObject(a4.get("start_location").toString());
			//			MainActivity.tvlat.setText("Lat : "+ a8.get("lat").toString());
			//			MainActivity.tvlong.setText("Long :" +a8.get("lng").toString());

			for(int i=0;i<a5.length();i++)
			{
				JSONObject a6 = new JSONObject(a5.get(i).toString());
				JSONObject a7 = new JSONObject(a6.getString("distance"));
				distance=a7.get("text").toString();
				if(i==0)
				{
				directionfortv=distance+" : ";}
				System.out.println(distance);
				try{System.out.println(a6.getString("maneuver").toString());
				facingfortv=a6.getString("maneuver").toString();
				JSONObject a8 =new JSONObject(a6.getString("end_location"));
				lat2= Double.parseDouble(a8.get("lat").toString());
				lon2= Double.parseDouble(a8.get("lng").toString());
				}
				catch(Exception e){System.out.println("Straight");
				facingfortv="Straight";
				}
				finally{}	
				if(i==1)
				{
					directionfortv+=facingfortv;
					MainActivity.tvDirection.setText(directionfortv);}

			}
			if(MainActivity.lat!=0.00 && MainActivity.lng!=0.00 && lat2!=0.00 && lon2!=0.00)
			{
			bearing=Math.atan2(Math.cos(MainActivity.lat)*Math.sin(lat2)-Math.sin(MainActivity.lat)*Math.cos(lat2)*Math.cos(lon2-MainActivity.lng),Math.sin(lon2-MainActivity.lng)*Math.cos(lat2));
			bearing=bearing*180/Math.PI;
			System.out.println(bearing);
			}
			} catch(Exception e){e.printStackTrace();}
		finally{System.out.println("Success");

		}	}

}
