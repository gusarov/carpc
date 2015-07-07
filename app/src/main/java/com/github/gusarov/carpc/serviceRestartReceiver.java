package com.github.gusarov.carpc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceRestartReceiver extends BroadcastReceiver
{
    public void onReceive(Context ctx, Intent intent)
    {
        Log.i("serviceRestart", "received " + intent.getAction());
        CarPcService.restartService(ctx);
    }
}

