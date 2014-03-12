package com.example.trackme;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * 
 * The app works only one side and more features needed to be added.
 * One side here means that we can find a phone nearby by clicking the
 * tag. In future we can add features of detecting a location of the 
 * tag in a map if this tag is attached to a keychain we can find the location
 * of the keychain and also using background syncs we can create a 
 * network of people scanning for tags and updating it to the server
 * just like the those guys at http://www.thetrackr.com are doing. 
 * 
 * @author bharatyarlagadda
 *
 */
public class MainActivity extends BaseActivity {


	private BluetoothAdapter mBluetoothAdapter;
	BluetoothManager bluetoothManager;

	private Handler mHandler = new Handler();

	// This is the schedular we use to call the bluetooth every 60 seconds or whatever value is in
	// SCAN_REPETITITON_INMILLISECONDS.
	ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

	// Stops scanning after 50 seconds or 50000 milli seconds. 
	// Increase this value if you find that the devices we 
	// use are not detecting the scan period we gave them.
	private static final long SCAN_PERIOD_MILLISECS = 5000;

	// Scan repetitions every 60 seconds.
	private static final long SCAN_REPETITION_INMILLISECONDS = 10000;

	// Bluetooth scan callbacks variable. We get the callback here when we register this with the
	// bluetoothadapter. So whenever bluetooth adapter finds a device in a scan and calls the 
	// methods
	private BluetoothAdapter.LeScanCallback mLeScanCallback;
	private boolean isPaused = false;

	private List<String> deviceDetected = new ArrayList<String>();
	private MenuItem refresh;
	private MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();


		mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

			@Override
			public void onLeScan(final BluetoothDevice device,final int rssi,
					final byte[] scanRecord) {
				String str = "";
				for(byte i : scanRecord) {
					str = str+" "+i;
				}
				Log.d("SCANResponse",device.getName()+str);
				if(device.getName().equals("imSoFresh") && !deviceDetected.contains("imSoFresh")) {
					
					// Read the button press from the tag.	
					int buttonPress = (scanRecord[5] & 0x01 );

					if(buttonPress == 1) {
						deviceDetected.add("imSoFresh");
						Vibrator v = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
						// Vibrate for 3000 milliseconds
						v.vibrate(3000);

						// Play the sound when tag discovers the device.
						Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
						mp = MediaPlayer.create(getApplicationContext(), notification);
						mp.start();
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								mp.stop();
							}
						},3000);
					}
				}

			}
		};

		// Schedular we use to start the scan every 60 seconds or whatevers in 
		// SCAN_REPETITION_INMILLISECONDS
		scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
			public void run() {

				scanLeDevice();
			}
		}, 1000, SCAN_REPETITION_INMILLISECONDS, TimeUnit.MILLISECONDS);
	}

	/**
	 * The scanning/stop scanning for devices is called here. 
	 */
	private void scanLeDevice() {
		if(mBluetoothAdapter!= null) {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			if(!isPaused) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						deviceDetected.clear();
						mBluetoothAdapter.stopLeScan(mLeScanCallback);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								refresh.setActionView(null);
							}

						});
					}
				}, SCAN_PERIOD_MILLISECS);
				mBluetoothAdapter.startLeScan(mLeScanCallback);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						refresh.setActionView(R.layout.refresh_loading_spinner);
					}

				});
			}
		}

	}

	/**
	 * Destroys the activity.
	 */
	@Override
	protected void onDestroy() {

		super.onDestroy();
		if(mLeScanCallback != null && mBluetoothAdapter!= null ) {

			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
		scheduleTaskExecutor.shutdown();
	}

	/**
	 * An action bar icon that shows a loading icon when when ever the device is scanning for 
	 * devices.
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getSupportMenuInflater().inflate(R.menu.main, menu);
		
		// refresh menu item
		refresh = menu.findItem(R.id.action_refresh);

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * This gets called when the refresh button is clicked by the user.
	 * you can make the device force scan again or just wait for the 
	 * schedular to schedule a scan.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// when refresh item is clicked. scan happens
		case R.id.action_refresh: 
			scanLeDevice();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
