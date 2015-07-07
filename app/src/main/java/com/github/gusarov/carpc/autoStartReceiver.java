package com.github.gusarov.carpc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class autoStartReceiver extends BroadcastReceiver
{
    public void onReceive(Context ctx, Intent intent)
    {
        String action = intent.getAction();
        Log.i("autoStart", "received " + action);
        switch (action) {
            case "com.github.gusarov.carpc.action.YouWillNeverKillMe.CarPcService":
                CarPcService.ensureServiceRunning(ctx);
                break;
            case "com.github.gusarov.carpc.action.YouWillNeverKillMe.AudioService":
                AudioService.ensureServiceRunning(ctx);
                break;
            default:
                CarPcService.ensureServiceRunning(ctx);
                AudioService.ensureServiceRunning(ctx);
                break;
        }
    }

}

