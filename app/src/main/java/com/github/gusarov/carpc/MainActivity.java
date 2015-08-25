package com.github.gusarov.carpc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements Handler.Callback {
    private static final int RESULT_RESISTIVE_SETTINGS = 2;

    CarPcApplication _appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _appState = ((CarPcApplication)getApplicationContext());

        setContentView(R.layout.activity_main);
        updateCheckbox();
        CarPcService.ensureServiceRunning(this);
    }

    @Override
    public void onStart() {
	    super.onStart();
        _appState.unsubscribe(this);
        _appState.subscribe(this);
    }

    @Override
    public void onPause() {
	    super.onPause();
        _appState.unsubscribe(this);
    }

    @Override
    public void onResume() {
	    super.onResume();
        _appState.unsubscribe(this);
        _appState.subscribe(this);
        updateCheckbox();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_resistive_settings) {
            Intent i = new Intent(this, ResistiveSettingsActivity.class);
            startActivityForResult(i, RESULT_RESISTIVE_SETTINGS);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_RESISTIVE_SETTINGS:
                break;
        }
    }

    public void testButtonCLick(View view) {
        toggle();
    }

    public void toggle() {
        CheckBox cb = (CheckBox) this.findViewById(R.id.cbUsbState);
        cb.setChecked(!cb.isChecked());
    }

    public void updateCheckbox(boolean b) {
        CheckBox cb = (CheckBox) this.findViewById(R.id.cbUsbState);
        cb.setChecked(b);
    }

    public void updateCheckbox() {
        // SharedPreferences prefs = this.getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        // boolean r = prefs.getBoolean(getString(R.string.preference_controller_connected), false);
        updateCheckbox(_appState.ControllerConnected);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == Const.UsbConnectionChanged){
            updateCheckbox();
            return true;
        }
        return false;
    }
}
