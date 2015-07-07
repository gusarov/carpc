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
        while (true) {
            try {
                Log.i(TAG, "workLoad - Scanning for drivers...");
                UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

                Log.v(TAG, availableDrivers.size() + " usb drivers");
                for (int i = 0; i < availableDrivers.size(); i++) {
                    Log.v(TAG, "tryDriver " + i);
                    tryDriver(manager, availableDrivers.get(i));
                }
                Log.i(TAG, "USB Controller not found. Waiting.");
            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
            }
            SystemClock.sleep(5000);
        }
        // terminatedProperly = true;
    }

    void tryDriver(UsbManager manager, UsbSerialDriver driver) {
        UsbDevice device = driver.getDevice();
        if (!manager.hasPermission(device)) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            manager.requestPermission(device, pendingIntent);
            return; // try other and sleep
        }
        UsbDeviceConnection connection = manager.openDevice(device);
        if (connection == null) {
            Log.e(TAG, "connection == null. You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)");
            return;
        }

        List<UsbSerialPort> ports = driver.getPorts();
        Log.v(TAG, ports.size() + " serial ports");
        for(int i=0; i < ports.size(); i++) {
            Log.v(TAG, "tryPort #" + i);
            tryPort(connection, ports.get(i));
        }
    }

    private void tryPort(UsbDeviceConnection connection, UsbSerialPort port) {
        Log.v(TAG, "tryPort " + Integer.toString(port.getPortNumber()));
        try {
            port.setDTR(true);
            port.setParameters(115200, 8, 1, 0);
            port.open(connection);
            byte buffer[] = new byte[5];
            int numBytesRead = port.read(buffer, 3000);
            Log.d(TAG, "Read " + numBytesRead + " bytes.");
            if (numBytesRead == 5) {
                String pre = new String(buffer, "UTF-8");
                if (pre == "CarPC") {
                    Log.i(TAG, "This is CarPC!");
                    workWithCarPcArduino(port);
                } else {
                    Log.i(TAG, "This is not CarPC: " + pre);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "tryPort", e);
        } finally {
            try {
                port.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "close", e);
            }
        }
    }

    private void workWithCarPcArduino(UsbSerialPort port) {

    }
}

