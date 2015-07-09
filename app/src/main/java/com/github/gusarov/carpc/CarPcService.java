package com.github.gusarov.carpc;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
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

	public CarPcService() {
		super("CarPcService");
		Log.i(TAG, "ctor()");
		setIntentRedelivery(true);
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

	private void tryPort(UsbDeviceConnection connection, UsbSerialPort port) {
		Log.i(TAG, "tryPort " + Integer.toString(port.getPortNumber()));
		try {
			port.open(connection);
		} catch (Exception e) {
			Log.e(TAG, "tryPort - cannot open", e);
			return;
		}
		try {
			port.setParameters(115200, 8, 1, 0);
			port.setDTR(true); // This restarts arduino. Proper firmware should say something like 'CarPC v0.1' in response to be identified

			String pre = tryRead(port);
			Log.d(TAG, "Read " + pre.length() + " chars: " + pre);
			if (pre.contains("CarPC")) {
				Log.i(TAG, "This is CarPC!");
				workWithCarPcArduino(port);
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

	String tryRead(UsbSerialPort port) throws IOException {
		return tryRead(port, 3000);
	}

	String tryRead(UsbSerialPort port, int timeoutMilliseconds) throws IOException {
		stopWatch.start();
		int numBytesRead = 0;
		do {
			int prev = numBytesRead;
			numBytesRead += port.read(buffer, numBytesRead, timeoutMilliseconds);
			Log.d(TAG, "Received " + numBytesRead + " bytes");
			for (int i = prev; i < numBytesRead; i++) {
				if (buffer[i] == '\n' || buffer[i] == '\r') {
					break; // enter detected, exit quickly
				}
			}
		} while (numBytesRead < buffer.length && stopWatch.getElapsedMilliseconds() < timeoutMilliseconds);
		Log.d(TAG, "Default Android Charset: " + Charset.defaultCharset().toString());
		return new String(buffer, 0, numBytesRead, "UTF-8");
	}

	byte buffer[] = new byte[4 * 1024];

	StopWatch stopWatch = new StopWatch();

	private void workWithCarPcArduino(UsbSerialPort port) throws IOException {
		while (true) {
            String cmd = tryRead(port);
			Log.v(TAG, "Received command: " + cmd);
            switch (cmd) {

            }
		}
	}
}

