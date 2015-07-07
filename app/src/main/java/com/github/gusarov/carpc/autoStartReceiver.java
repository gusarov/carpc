package com.github.gusarov.carpc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStartReceiver extends BroadcastReceiver
{
    public void onReceive(Context ctx, Intent intent)
    {
        String action = intent.getAction();
        Log.i("autoStart", "received " + action);
        CarPcService.ensureServiceRunning(ctx);
        AudioService.ensureServiceRunning(ctx);
    }

}

