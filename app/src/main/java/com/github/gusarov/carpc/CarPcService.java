package com.github.gusarov.carpc;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.List;


public class CarPcService extends IntentService {
	private static final String TAG = CarPcService.class.getSimpleName();
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

	public static void ensureServiceRunning(Context ctx) {
		Log.v(TAG, "start service...");
		Intent intent = new Intent(ctx, CarPcService.class);
		ctx.startService(intent);
	}

	public static void restartService(Context ctx) {
		Log.v(TAG, "restarting service...");
		Intent intent = new Intent(ctx, CarPcService.class);
		try {
			Log.v(TAG, "stopping...");
			ctx.stopService(intent);
		} catch (Exception ex) {
			Log.e(TAG, "Cannot stop service", ex);
		}
		Log.v(TAG, "starting...");
		ctx.startService(intent);
	}

	public class LocalBinder extends Binder {
		CarPcService getService() {
			return CarPcService.this;
		}
	}

	private final IBinder _binder = new LocalBinder();

	public CarPcService() {
		super("CarPcService");
		Log.i(TAG, "ctor()");
		setIntentRedelivery(true);
	}

	NotificationManager _nm;
	CarPcApplication _appState;
	private int NOTIFICATION = R.string.local_service_started;

	@Override
	public IBinder onBind(Intent intent) {
		return _binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		_nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		_appState = (CarPcApplication)getApplication();
		showNotification();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
		if (!terminatedProperly) {
			Intent intent = new Intent();
			intent.setAction("com.github.gusarov.carpc.action.YouWillNeverKillMe.CarPcService");
			sendBroadcast(intent);
			Log.w(TAG, "YouWillNeverKillMe.CarPcService intent sent...");
		}
		set(false);
		_nm.cancel(NOTIFICATION);
		Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
	}

	private void showNotification() {
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

		Notification notification = new Notification.Builder(this)
				.setContentText(getText(R.string.local_service_label))
				.setSmallIcon(R.drawable.car_144)
				.setContentIntent(contentIntent)
				.setOngoing(true)
				.build();

		_nm.notify(NOTIFICATION, notification);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		workLoad();
	}

	boolean terminatedProperly;

	private void workLoad() {
		try {
			Log.i(TAG, "workLoad - Scanning for drivers...");
			UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
			List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

			Log.i(TAG, availableDrivers.size() + " usb drivers");
			for (int i = 0; i < availableDrivers.size(); i++) {
				Log.i(TAG, "tryDriver " + i);
				tryDriver(manager, availableDrivers.get(i));
			}
			Log.i(TAG, "USB Controller not found. Service Shutdown.");
			terminatedProperly = true;
		} catch (Exception e) {
			Log.e(TAG, "Exception", e);
			SystemClock.sleep(5000);
		}
	}

	void tryDriver(UsbManager manager, UsbSerialDriver driver) {
		UsbDevice device = driver.getDevice();
		if (!manager.hasPermission(device)) {
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
			manager.requestPermission(device, pendingIntent);
			return; // try other and shutdown until permission manager broadcast an approve
		}
		UsbDeviceConnection connection = manager.openDevice(device);
		if (connection == null) {
			Log.e(TAG, "connection == null. You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)");
			return;
		}

		List<UsbSerialPort> ports = driver.getPorts();
		Log.i(TAG, ports.size() + " serial ports");
		for (int i = 0; i < ports.size(); i++) {
			Log.i(TAG, "tryPort #" + i);
			tryPort(connection, ports.get(i));
		}
	}

	int timeout = 1000;

	public void set(boolean state) {
		/*
		SharedPreferences prefs = this.getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
		prefs.edit().putBoolean(getString(R.string.preference_controller_connected), state);
		*/
		_appState.ControllerConnected = state;
		Log.i(TAG, "Set state " + state);
		Message msg = Message.obtain();
		msg.what = Const.UsbConnectionChanged;
		_appState.getHandler().sendMessage(msg);
	}

	private void tryPort(UsbDeviceConnection connection, final UsbSerialPort port) {
		Log.i(TAG, "tryPort " + Integer.toString(port.getPortNumber()));
		try {
			port.open(connection);
		} catch (Exception e) {
			Log.e(TAG, "tryPort - cannot open", e);
			return;
		}
		try {
			port.setParameters(115200, 8, 1, 0);

			StreamReader streamReader = StreamReader.create(new BlockReader() {
				@Override
				@TargetApi(18)
				public int read(byte[] buf, int offset, int maxLen) throws IOException {
					int r = port.read(buf, offset, maxLen, timeout);
					if (r > maxLen) {
						throw new IllegalStateException("Returned more than allowed");
					}
					return r;
				}

				@Override
				@TargetApi(12)
				public int read(byte[] buf, int maxLen) throws IOException {
					int r = port.read(buf, timeout);
					if (r > maxLen) {
						throw new IllegalStateException("Returned more than allowed");
					}
					return r;
				}
			});

			port.setDTR(true); // This restarts arduino. Proper firmware should say something like 'CarPC v0.1' in response to be identified

			String pre = streamReader.readLine();
			Log.d(TAG, "Read " + pre.length() + " chars: " + pre);
			if (pre.contains("CarPC")) {
				Log.i(TAG, "This is CarPC!");
				set(true);
				workWithCarPcArduino(port, streamReader);
			} else {
				Log.i(TAG, "This is not CarPC: " + pre);
			}
		} catch (IOException e) {
			Log.e(TAG, "tryPort", e);
		} finally {
			try {
				port.close();
			} catch (IOException e) {
				Log.d(TAG, "close", e);
			}
		}
	}

	private void workWithCarPcArduino(UsbSerialPort port, StreamReader streamReader) throws IOException {
		while (true) {
            String cmd = streamReader.readLine();
			String args;
			int i = cmd.indexOf(' ');
			if (i > 0) {
				args = cmd.substring(i).trim();
				cmd = cmd.substring(0, i).trim();
			} else {
				cmd = cmd.trim();
				args = "";
			}
			Log.v(TAG, "Received command: " + cmd + " with arguments: " + args);
			try {
				switch (cmd) {
					case "!RB": {
						int level = Integer.parseInt(args);
						Log.v(TAG, "Launching ActionResistiveButton: " + level);
						Message msg = Message.obtain();
						msg.what = Const.ResistiveButtonNotification;
						msg.arg1 = level;
						_appState.getHandler().sendMessage(msg);
						ProcessingService.startActionResistiveButton(this, level);
					}
					default: {
						Log.v(TAG, "Command unknown: " + cmd);
					}
				}
			} catch (Exception e){
				Log.e(TAG, "command handler", e);
			}
		}
	}
}

