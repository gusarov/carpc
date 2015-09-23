package com.github.gusarov.carpc;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.gusarov.carpc.Commands.Command;
import com.github.gusarov.carpc.Commands.CommandManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class CustomResistiveButtonListAdapter extends ArrayAdapter<ResistiveButtonViewModel> {
	private static final String TAG = CustomResistiveButtonListAdapter.class.getSimpleName();

	//private List<ResistiveButtonViewModel> _resistiveButtonViewModels;
	private LayoutInflater _layoutInflater;
	private List<Command> _commands;

	public CustomResistiveButtonListAdapter(Context context, int resource, List<ResistiveButtonViewModel> items)
	{
		super(context, resource, items);
		//this._resistiveButtonViewModels = items;
		_layoutInflater = LayoutInflater.from(context);
		try {
			_commands = CommandManager.getInstance().getAllCommands();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return;
		}
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ResistiveButtonViewModel item = this.getItem(position);
		//final ViewHolder holder;
		if (convertView == null) {
			Log.d(TAG, "RLA - Inflate new item for position " + position);
			convertView = _layoutInflater.inflate(R.layout.list_row_layout, null);

			TextView idView = (TextView) convertView.findViewById(R.id.id);
			TextView nameView = (TextView) convertView.findViewById(R.id.name);
			TextView mainValueView = (TextView) convertView.findViewById(R.id.mainValue);
			final Spinner commandCodeSpinner = (Spinner) convertView.findViewById(R.id.command);

			ArrayAdapter<Command> commandAdapter = new ArrayAdapter<Command>(convertView.getContext(), android.R.layout.simple_list_item_1/*, android.R.id.text1*/);
			commandAdapter.addAll(_commands);

			Log.d(TAG, "RLA Inflate new item - adapter " + commandAdapter.hashCode() + " tag item " + item.hashCode());

			commandCodeSpinner.setAdapter(commandAdapter);

			int spinPos = commandAdapter.getPosition(item.getButtonInfo().Command);
			commandCodeSpinner.setSelection(spinPos);

			commandCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				{
					this.item1 = item;
				}

				ResistiveButtonViewModel item1;

				@Override
				public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
					int realPos = commandCodeSpinner.getSelectedItemPosition();
					if (realPos != position) {
						Log.d(TAG, "wow");
					}
					ResistiveButtonViewModel model = item1;//(ResistiveButtonViewModel)adapterView.getTag();
					Command cmd = (Command) adapterView.getItemAtPosition(position);
					for (int i = 0; i < adapterView.getCount(); i++) {
						Log.d(TAG, "item at position " + i + " "+ ((Command)adapterView.getItemAtPosition(i)).getCode());
					}
					if (model.getCommandCode() != cmd.getCode()) {
						Log.d(TAG, "ItemSelected: position " + position + " " + cmd.getCode() + " set it to model " + model.getName() + " avg " + model.getAvg());
						model.setCommand(cmd);
						ResistiveButtonsManager.getInstance().save(adapterView.getContext(), model.getButtonInfo());
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					Log.d(TAG, "NothingSelected");
				}
			});
		}

		TextView nameView = (TextView) convertView.findViewById(R.id.name);
		TextView mainValueView = (TextView) convertView.findViewById(R.id.mainValue);
		final Spinner commandCodeSpinner = (Spinner) convertView.findViewById(R.id.command);
		ArrayAdapter<Command> commandAdapter = (ArrayAdapter<Command>)commandCodeSpinner.getAdapter();

		//idView.setText(item.getButtonInfo().Id.toString());
		mainValueView.setText(Integer.toString(item.getButtonInfo().MainLevel));
		nameView.setText(item.getName());

		if (item.getLast()) {
			convertView.setBackgroundColor(0x7700FF00);
		} else {
			convertView.setBackgroundColor(0);
		}

		Log.d(TAG, "RLA position " + position + " - set item's spin");

		boolean set = false;
		final int ii = commandAdapter.getPosition(item.getButtonInfo().Command);
		//Log.d(TAG, "found! " + i +" "+ ii + " code = " + item.getCommandCode());
		if (commandCodeSpinner.getSelectedItemPosition() != ii) {
			Log.d(TAG, "post " + ii + " because current is " + commandCodeSpinner.getSelectedItemPosition());
			commandCodeSpinner.post(new Runnable() {
				@Override
				public void run() {
					if (commandCodeSpinner.getSelectedItemPosition() != ii) {
						Log.d(TAG, "Inflate position " + position + " set selection to " + ii);
						commandCodeSpinner.setSelection(ii, false);
					} else {
						Log.d(TAG, "already set to " + ii + " after post");
					}
				}
			});
		} else {
			Log.d(TAG, "already set to " + ii);
		}
		/*
		set = true;
		if (!set) {
			Log.d(TAG, "not found, reset to nothing (0)");
			if (commandCodeSpinner.getSelectedItemPosition() != 0) {
				Log.d(TAG, "post 0 because current is " + commandCodeSpinner.getSelectedItemPosition());
				commandCodeSpinner.post(new Runnable() {
					@Override
					public void run() {
						if (commandCodeSpinner.getSelectedItemPosition() != 0) {
							Log.d(TAG, "RLA posted - set selection to 0");
							commandCodeSpinner.setSelection(0, false);
						} else {
							Log.d(TAG, "already reset to 0 after post");
						}
					}
				});
			} else {
				Log.d(TAG, "already reset to 0");
			}
		}
		*/

		return convertView;
	}
/*
	class ViewHolder {
		TextView idView;
		TextView mainValueView;
		TextView nameView;
		Spinner commandCodeSpinner;
		ArrayAdapter<Command> commandAdapter;
		boolean muteSelection;
	}
*/
}
