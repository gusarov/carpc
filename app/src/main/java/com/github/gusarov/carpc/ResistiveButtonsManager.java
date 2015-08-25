package com.github.gusarov.carpc;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.gusarov.carpc.Commands.Command;
import com.github.gusarov.carpc.Commands.CommandManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ResistiveButtonsManager {
	private static final String TAG = ResistiveButtonsManager.class.getSimpleName();
	private static final String ResistiveButtons = "ResistiveButtons";

	private static final ResistiveButtonsManager _instance = new ResistiveButtonsManager();

	public static ResistiveButtonsManager getInstance() { return _instance; }

	List<ResistiveButtonInfo> _configuredValues = new ArrayList<>();

	public void loadConfig(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(ResistiveButtons, Context.MODE_WORLD_WRITEABLE);
		_configuredValues.clear();
		Set<String> knownIds = prefs.getStringSet("KnownIds", new HashSet<String>());
		for (String knownId : knownIds) {
			UUID id = UUID.fromString(knownId);
			String name = prefs.getString("Name" + knownId, "New Button");
			int mainValue = prefs.getInt("Value2" + knownId, 0);
			String cmd = prefs.getString("Command" + knownId, "");
			Command command = null;
			if (cmd != null && cmd != ""){
				command = CommandManager.getInstance().getCommand(cmd);
			}
			if (cmd != null) {
				_configuredValues.add(new ResistiveButtonInfo(id, name, mainValue, command));
			}
		}
		Collections.sort(_configuredValues, ResistiveButtonInfoComparator.Instance);
	}

	public void save(Context ctx) {
		SharedPreferences.Editor prefsEditable = ctx.getSharedPreferences(ResistiveButtons, Context.MODE_WORLD_WRITEABLE).edit();
		Set<String> knownValues = new HashSet<>();
		for (ResistiveButtonInfo button: _configuredValues) {
			knownValues.add(button.Id.toString());

			prefsEditable.putString("Name" + button.Id, button.Name);
			Log.d(TAG, "save " + "Name" + button.Id + " = " + button.Name);

			prefsEditable.putInt("Value2" + button.Id, button.MainLevel);
			Log.d(TAG, "save " + "Value2" + button.Id + " = " + Integer.toString(button.MainLevel));

			if (button.Command == null) {
				prefsEditable.putString("Command" + button.Id, "");
				Log.d(TAG, "save " + "Command" + button.Id + " = (empty)");
			} else {
				prefsEditable.putString("Command" + button.Id, button.Command.getCode());
				Log.d(TAG, "save " + "Command" + button.Id + " = " + button.Command.getCode());
			}
		}
		prefsEditable.putStringSet("KnownIds", knownValues);
		Log.d(TAG, "save " + "KnownIds " + knownValues.size() + " items");
		for (String  value: knownValues) {
			Log.d(TAG, value);
		}
		Log.d(TAG, "-end-");
		prefsEditable.apply();
		prefsEditable.commit();
	}

	public void save(Context ctx, ResistiveButtonInfo btn) {
		for (ResistiveButtonInfo button: _configuredValues) {
			if (button == btn) {
				Log.d(TAG, "save: ResistiveButtonInfo exists, just save all");
				save(ctx);
				return;
			}
		}
		Log.d(TAG, "save: ResistiveButtonInfo is new, add and save");
		_configuredValues.add(btn);
		save(ctx);
	}

	/*
	public void loadConfig(int value, String command) {
		Command cmd = CommandManager.getInstance().getCommand(command);
		_configuredValues.add(new ResistiveButtonInfo(value, cmd));
	}
	*/

	public List<ResistiveButtonInfo> getButtons() {
		return _configuredValues;
	}

	public ResistiveButtonInfo getCommand(int level) {
		int index = Collections.binarySearch(_configuredValues, level);
		if (index < 0) {
			index = ~index;
		}
		ResistiveButtonInfo cmd1 = null;
		ResistiveButtonInfo cmd2 = null;
		if (index > 0) {
			cmd1 = _configuredValues.get(index-1);
		}
		if (index < _configuredValues.size()) {
			cmd2 = _configuredValues.get(index);
		}
		if (cmd1 != null && cmd2 != null) {
			if (Math.abs(level - cmd1.MainLevel) <  Math.abs(level - cmd2.MainLevel)){
				return cmd1;
			} else {
				return cmd2;
			}
		}
		if (cmd1 != null) {
			return cmd1;
		}
		if (cmd2 != null) {
			return cmd2;
		}
		return null;
	}

	public void Process(int level, Context ctx) {
		ResistiveButtonInfo info = getCommand(level);
		Log.v(TAG, "process: " + level + " Info: " + info);
		if (info != null && info.Command != null) {
			info.Command.execute(ctx);
		}
	}

	public void reset(Context ctx) {
		SharedPreferences.Editor prefsEditable = ctx.getSharedPreferences(ResistiveButtons, Context.MODE_WORLD_WRITEABLE).edit();
		prefsEditable.clear();
		prefsEditable.apply();
		prefsEditable.commit();
		loadConfig(ctx);
	}
}

