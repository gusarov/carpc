package com.github.gusarov.carpc;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class ResistiveSettingsActivity extends ActionBarActivity implements Handler.Callback {
	private static final String TAG = ResistiveSettingsActivity.class.getSimpleName();

	private CarPcApplication _appState;
	private List<ResistiveButtonViewModel> _buttons;
	private CustomResistiveButtonListAdapter _adapter;
	private ListView _listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_resistive_settings);

		_appState = ((CarPcApplication)getApplicationContext());
		_buttons = new ArrayList<>();
		_adapter = new CustomResistiveButtonListAdapter(this, R.layout.list_row_layout, _buttons);
		_listView = (ListView) this.findViewById(R.id._listView);
		_listView.setAdapter(_adapter);
	}

	@Override
	public void onStart() {
		super.onStart();
		_appState.unsubscribe(this);
		_appState.subscribe(this);
		fillFromSettings();
	}

	@Override
	public void onPause() {
		super.onPause();
		_appState.unsubscribe(this);
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == Const.ResistiveButtonNotification){
			int rbCode = msg.arg1;
			updateLastCodeLabel(rbCode);
			analyzeAndUpdateList(rbCode);
			return true;
		}
		return false;
	}

	private void analyzeAndUpdateList(int rbCode) {
		Log.d(TAG, "analyzeAndUpdateList rbCode="+rbCode);
		ResistiveButtonViewModel button = null;
		int index = 0;
		for (int i = 0; i < _buttons.size(); i++) {
			ResistiveButtonViewModel btn = _buttons.get(i);
			if (btn.getStart() < rbCode && rbCode < btn.getEnd()) {
				button = btn;
				index = i;
				break;
			}
		}
		for (int i = 0; i < _buttons.size(); i++) {
			ResistiveButtonViewModel btn = _buttons.get(i);
			btn.setLast(false);
		}
		if (button == null) {
			Log.d(TAG, "analyzeAndUpdateList this is new button! Add and save");
			button = new ResistiveButtonViewModel(new ResistiveButtonInfo(UUID.randomUUID(), "New Button", rbCode, null));
			_adapter.add(button);
			ResistiveButtonsManager.getInstance().save(this, button.getButtonInfo());
		} else {
			Log.d(TAG, "analyzeAndUpdateList add point");
			button.addPoint(rbCode);
		}
		Log.d(TAG, "analyzeAndUpdateList set last");
		button.setLast(true);
		_adapter.notifyDataSetChanged();
	}

	private void updateLastCodeLabel(int rbCode) {
		TextView txt = (TextView) this.findViewById(R.id._txtLastCode);
		txt.setText(Integer.toString(rbCode));
	}

	void fillFromSettings() {
		_adapter.clear();
		ResistiveButtonsManager.getInstance().loadConfig(this);
		List<ResistiveButtonInfo> buttons = ResistiveButtonsManager.getInstance().getButtons();
		for (int i = 0; i < buttons.size(); i++) {
			_adapter.add(new ResistiveButtonViewModel(buttons.get(i)));
		}
		_adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_resistive_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();


		Message msg = new Message();
		msg.what = Const.ResistiveButtonNotification;

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_resistive_test1) {
			msg.arg1 = 100;
			_appState.getHandler().sendMessage(msg);
			ProcessingService.startActionResistiveButton(this, msg.arg1);
			return true;
		}
		if (id == R.id.action_resistive_test2) {
			msg.arg1 = 200;
			_appState.getHandler().sendMessage(msg);
			ProcessingService.startActionResistiveButton(this, msg.arg1);
			return true;
		}
		if (id == R.id.action_resistive_testRandom) {
			msg.arg1 = new Random().nextInt();
			_appState.getHandler().sendMessage(msg);
			ProcessingService.startActionResistiveButton(this, msg.arg1);
			return true;
		}
		if (id == R.id.action_resistive_reset) {
			ResistiveButtonsManager.getInstance().reset(this);
			fillFromSettings();
			return true;
		}
		if (id == R.id.action_resistive_notify) {
			_adapter.notifyDataSetChanged();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


}

