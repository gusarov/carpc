package com.github.gusarov.carpc;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AudioService extends IntentService {
    private static final String TAG = CarPcService.class.getSimpleName();

    boolean terminatedProperly;

    public AudioService()
    {
        super("AudioService");
        Log.i(TAG, "ctor()");
        setIntentRedelivery(true);
    }

    public static void ensureServiceRunning(Context ctx) {
        Log.v(TAG, "start service...");
        Intent intent = new Intent(ctx, CarPcService.class);
        ctx.startService(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        if (!terminatedProperly) {
            Intent intent = new Intent();
            intent.setAction("com.github.gusarov.carpc.action.YouWillNeverKillMe.AudioService");
            sendBroadcast(intent);
            Log.w(TAG, "YouWillNeverKillMe.AudioService intent sent...");
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            workLoad();
            terminatedProperly = true;
        } catch (Exception e) {

        } finally {

        }
    }

    AudioTrack audioTrack;

    private void workLoad() {
        try {
            int intSize = android.media.AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_8BIT);
            AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_8BIT, intSize, AudioTrack.MODE_STATIC);
            byte byteData[] = new byte[intSize];
            audioTrack.setVolume(0);
            while (true) {
                audioTrack.write(byteData, 0, byteData.length);
            }
        } catch(Exception e) {
            Log.e(TAG, "Exception", e);
        }
        terminatedProperly = true;
    }
}
