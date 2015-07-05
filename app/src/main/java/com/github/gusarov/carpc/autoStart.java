package com.github.gusarov.carpc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class autoStart extends BroadcastReceiver
{
    public void onReceive(Context ctx, Intent intent)
    {
        Log.i("AutostartReceiver", "received " + intent.getAction());
        CarPcService.ensureServiceRunning(ctx, "A");
        CarPcService.ensureServiceRunning(ctx, "B");
    }

}