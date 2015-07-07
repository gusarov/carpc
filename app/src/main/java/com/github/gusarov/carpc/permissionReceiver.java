package com.github.gusarov.carpc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class permissionReceiver extends BroadcastReceiver
{
    private static final String TAG = permissionReceiver.class.getSimpleName();
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    public void onReceive(Context ctx, Intent intent)
    {
        String action = intent.getAction();
        Log.i(TAG, "received " + action);
        if (ACTION_USB_PERMISSION.equals(action)) {
            synchronized (this) {
                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if (device != null){
                        Log.i(TAG, "permission granted for device " + device);
                        CarPcService.ensureServiceRunning(ctx);
                    }
                }
                else {
                    Log.i(TAG, "permission denied for device " + device);
                }
            }
        }
    }
}
