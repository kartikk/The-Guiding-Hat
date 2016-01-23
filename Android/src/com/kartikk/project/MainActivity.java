package com.kartikk.project;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity implements LocationListener{

	private LocationManager locationManager;
	private String provider;

	private static final String TAG = "btStatus";
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;
	public static InputStream inStream = null;

	public static String destination="Adyar";
	public static Double lat=0.00,lng=0.00;
	ToggleButton tBtnFront,tBtnLeft,tBtnRight,tBtnBack;
	public static TextView tvlat,tvlong,text,tvDirection;
	public static String address="http://date.jsontest.com";
	public static int gpsFix=0;

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_ENABLE_BT = 3;

	// Well known SPP UUID
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	//bt Intent
	public static Intent serverIntent=null;

	// Insert your bluetooth devices MAC address
	// private static String address = "00:00:00:00:00:00";
	ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
	ListView lv1,lv2;
	Button b1,btnPreset1,btnPreset2,btnPreset3;
	int startFnCall=0;
	Thread btInputThread;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		b1= (Button) findViewById(R.id.button1);
		btnPreset1=(Button) findViewById(R.id.button2);
		btnPreset2=(Button) findViewById(R.id.button3);
		btnPreset3=(Button) findViewById(R.id.button4);
		tvlat = (TextView) findViewById(R.id.textView3);
		tvlong = (TextView) findViewById(R.id.textView4);
		text= (TextView) findViewById(R.id.textView5);
		tvDirection=(TextView) findViewById(R.id.textView6);
		tBtnFront=(ToggleButton) findViewById(R.id.toggleButton1);
		tBtnLeft=(ToggleButton) findViewById(R.id.toggleButton2);
		tBtnRight=(ToggleButton) findViewById(R.id.toggleButton3);
		tBtnBack=(ToggleButton) findViewById(R.id.toggleButton4);
		tBtnFront.setEnabled(false);
		tBtnLeft.setEnabled(false);
		tBtnRight.setEnabled(false);
		tBtnBack.setEnabled(false);
		

		btAdapter = BluetoothAdapter.getDefaultAdapter();

		checkBTState();

		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();
		/* Request updates at startup */

		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		// Initialize the location fields
		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			onLocationChanged(location);
		} else {
			tvlat.setText("Location not available");
			tvlong.setText("Location not available");
		}	

		b1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub


				startFnCall=1;

			}

		});
		tBtnFront.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					sendData("F");
				}
				else
				{
					sendData("f");
				}

			}
		});
		tBtnLeft.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					sendData("L");
				}
				else
				{
					sendData("l");
				}

			}
		});
		tBtnRight.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					sendData("R");
				}
				else
				{
					sendData("r");
				}

			}
		});
		tBtnBack.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					sendData("B");
				}
				else
				{
					sendData("b");
				}

			}
		});
		btnPreset1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				destination="Easwari Engineering College";
			}
		});
		btnPreset2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				destination="sp robotic works";
			}
		});
		btnPreset3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				destination="federn fabrik";
			}
		});
		exec.scheduleAtFixedRate(new Runnable() {
			public void run() {
				// code to execute repeatedly
				if(startFnCall==1 && gpsFix==1)
				{
					new HttpReq().execute(address);  
				}
			}
		}, 0, 10, TimeUnit.SECONDS); // execute every 60 seconds
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.secure_connect_scan:
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);

			return true;
		}
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		lat = (double) (location.getLatitude());
		lng = (double) (location.getLongitude());
		gpsFix=1;
		tvlat.setText(String.valueOf(lat));
		tvlong.setText(String.valueOf(lng));
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Enabled new provider " + provider,Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disabled provider " + provider,Toast.LENGTH_SHORT).show();
	}
	public void onStop(){
		// cancel executing after exit
		exec.shutdown();
		btInputThread.interrupt();
		//flush bluetooth output stream
		if (outStream != null) {
			try {
				outStream.flush();
			} catch (IOException e) {
				errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
			}
		}
		//flush bluetooth input stream
		if (inStream != null) {
			try {
				inStream.close();
			} catch (IOException e) {
				errorExit("Fatal Error", "In onPause() and failed to flush input stream: " + e.getMessage() + ".");
			}
		}

		// close bluetooth socket if open
		if(btSocket!=null){
			try     {
				btSocket.close();
			} catch (IOException e2) {
				errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
			}}
		super.onStop(); 
	}


	public void connectToDevice(String adr) {
		super.onResume();

		//enable buttons once connection established.
		tBtnFront.setEnabled(true);
		tBtnLeft.setEnabled(true);
		tBtnRight.setEnabled(true);
		tBtnBack.setEnabled(true);

		// Set up a pointer to the remote node using it's address.
		BluetoothDevice device = btAdapter.getRemoteDevice(adr);

		// Two things are needed to make a connection:
		//   A MAC address, which we got above.
		//   A Service ID or UUID.  In this case we are using the
		//     UUID for SPP.
		try {
			btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
		}

		// Discovery is resource intensive.  Make sure it isn't going on
		// when you attempt to connect and pass your message.
		btAdapter.cancelDiscovery();

		// Establish the connection.  This will block until it connects.
		try {
			btSocket.connect();
		} catch (IOException e) {
			try {
				btSocket.close();
			} catch (IOException e2) {
				errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
			}
		}

		// Create a data stream so we can talk to server.
		try {
			outStream = btSocket.getOutputStream();
			inStream = btSocket.getInputStream();
		} catch (IOException e) {
			errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}
	@Override
	public void onPause() {
		super.onPause();

		/* Remove the locationlistener updates when Activity is paused */
		locationManager.removeUpdates(this);

	}

	private void checkBTState() {
		// Check for Bluetooth support and then check to make sure it is turned on

		// Emulator doesn't support Bluetooth and will return null
		if(btAdapter==null) { 
			errorExit("Fatal Error", "Bluetooth Not supported. Aborting.");
		} else {
			if (btAdapter.isEnabled()) {
				Log.d(TAG, "...Bluetooth is enabled...");
			} else {
				//Prompt user to turn on Bluetooth
				Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}

	private void errorExit(String title, String message){
		Toast msg = Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_SHORT);
		msg.show();
		finish();
	}

	private void sendData(String message) {
		byte[] msgBuffer = message.getBytes();
		try {
			outStream.write(msgBuffer);
		} catch (IOException e) {
			String msg = "In onResume() and an exception occurred during write: " + e.getMessage();      
			errorExit("Fatal Error", msg);       
		}
	}
	private void receiveData()
	{       
		// Keep looping to listen for received messages
		btInputThread = new Thread() {
			@Override
			public void run() {
				byte[] buffer = new byte[256];
				int bytes = 0;
				Double dblFacing=0.00; 
				try {
					while(true) {
						sleep(500);
						String tempString="",newString="";		//for extracting only facing values
						bytes = inStream.read(buffer);            //read bytes from input buffer
						String readMessage = new String(buffer, 0, bytes);
						if( readMessage.contains("<"))
						{
							if(readMessage.indexOf("<")+1<readMessage.length())
							{
							tempString=readMessage.substring(readMessage.indexOf("<")+1);
							if(tempString.contains(">"))
							newString=tempString.substring(0, tempString.indexOf(">"));
							}
							}
						Log.d("btfacing",newString);
						if(HttpReq.bearing!=0.00 && newString!="")
						{
							dblFacing=HttpReq.bearing -Double.valueOf(newString);
							String facingDiffStng= String.valueOf(dblFacing);
							Log.d("facing diff", facingDiffStng);
						}
						if(dblFacing>70 && dblFacing<180)
						{
							sendData("R");
							sendData("l");
							sendData("b");
						}
						else if(dblFacing<-70 && dblFacing>-180)
						{
							sendData("L");
							sendData("r");
							sendData("b");						
						}
						else if(dblFacing<-180 || dblFacing>180)
						{
							sendData("B");
							sendData("r");
							sendData("l");
						}
						else if(dblFacing<70)
						{
							sendData("r");
							sendData("l");
							sendData("b");
						}
						else if(dblFacing>-70)
						{
							sendData("l");
							sendData("r");
							sendData("b");
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
		};
		btInputThread.start();
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, true);
			}
			break;
		}
	}

	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		text.setText("Device Address: " + address);
		connectToDevice(address);
		// Get the BluetoothDevice object
		BluetoothDevice device = btAdapter.getRemoteDevice(address);
		receiveData();
	}
}

