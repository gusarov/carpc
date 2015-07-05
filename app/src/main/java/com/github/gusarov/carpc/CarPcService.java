package com.github.gusarov.carpc;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

public class CarPcService extends IntentService {
    private static final String TAG = "CarPcService";

    public static void ensureServiceRunning(Context context) {
        Intent intent = new Intent(context, CarPcService.class);
        context.startService(intent);
    }

    public CarPcService() {
        super("CarPcService");
        setIntentRedelivery(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.setAction("com.github.gusarov.carpc.action.YouWillNeverKillMe");
        sendBroadcast(intent);
        Log.w(TAG, "YouWillNeverKillMe intent sent...");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        workLoad();
    }

    private void workLoad() {
        for (int i = 0; i <= 1000000; i++) {
            SystemClock.sleep(1000);
            Log.i(TAG, "tick");
        }
    }


}
