package com.github.gusarov.carpc;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class ProcessingService extends IntentService {
    private static final String TAG = ProcessingService.class.getSimpleName();

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_RESISTIVE_BUTTON = "com.github.gusarov.carpc.action.RESISTIVE_BUTTON";
    private static final String ACTION_BAZ = "com.github.gusarov.carpc.action.BAZ";

    private static final String RESISTANCE_LEVEL = "com.github.gusarov.carpc.extra.RESISTANCE_LEVEL";
    private static final String EXTRA_PARAM2 = "com.github.gusarov.carpc.extra.PARAM2";

    public ProcessingService()
    {
        super("ProcessingService");
        setIntentRedelivery(true);
    }

    /**
     * Starts this service to perform action for resistive button with the given level. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionResistiveButton(Context context, int level) {
        Intent intent = new Intent(context, ProcessingService.class);
        intent.setAction(ACTION_RESISTIVE_BUTTON);
        intent.putExtra(RESISTANCE_LEVEL, level);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ProcessingService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RESISTIVE_BUTTON.equals(action)) {
                final int level = intent.getIntExtra(RESISTANCE_LEVEL, -1);
                handleActionResistiveButton(level);
            } else if (ACTION_BAZ.equals(action)) {
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(null, param2);
            }
        }
    }

    /**
     * Handle action RESISTIVE_BUTTON in the provided background thread with the provided
     * parameters.
     */
    private void handleActionResistiveButton(int level) {
        Log.v(TAG, "handleActionResistiveButton: " + level);

        ResistiveButtonsManager.getInstance().Process(level, this);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
