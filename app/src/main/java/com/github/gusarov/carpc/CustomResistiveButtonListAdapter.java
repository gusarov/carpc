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

	private List<ResistiveButtonViewModel> _resistiveButtonViewModels;
	private LayoutInflater _layoutInflater;
	List<Command> _commands;

	public CustomResistiveButtonListAdapter(Context context, int resource, List<ResistiveButtonViewModel> items)
	{
		super(context, resource, items);
		this._resistiveButtonViewModels = items;
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

	public View getView(int position, View convertView, ViewGroup parent) {
		ResistiveButtonViewModel item = _resistiveButtonViewModels.get(position);

		ViewHolder holder;
		if (convertView == null) {
			Log.d(TAG, "Inflate new item");
			convertView = _layoutInflater.inflate(R.layout.list_row_layout, null);
			holder = new ViewHolder();
			holder.idView = (TextView) convertView.findViewById(R.id.id);
			holder.nameView = (TextView) convertView.findViewById(R.id.name);
			holder.mainValueView = (TextView) convertView.findViewById(R.id.mainValue);
			holder.commandCodeSpinner = (Spinner) convertView.findViewById(R.id.command);
			holder.commandAdapter = new ArrayAdapter<Command>(convertView.getContext(), android.R.layout.simple_list_item_1/*, android.R.id.text1*/);
			/*{
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
				/ *
				TextView codeText = (TextView) view.findViewById(android.R.id.text2);
				Command item = getItem(position);
				codeText.setText("code: " + item.getCode());
				* /
					return view;
				}
			};*/
			holder.commandAdapter.addAll(_commands);

			holder.commandCodeSpinner.setAdapter(holder.commandAdapter);
			holder.commandCodeSpinner.setTag(item);
			holder.commandCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					/*
					if (initializedAdapter != parent.getAdapter()) {
						initializedAdapter = parent.getAdapter();
						return;
					}
					*/
					ResistiveButtonViewModel model = (ResistiveButtonViewModel)parent.getTag();
					Command cmd = _commands.get(position);
					Log.d(TAG, "selected item " + position + " " + cmd.getCode() + " set it to model " + model.getName() + " avg " + model.getAvg());
					if (model.getCommandCode() != cmd.getCode()) {
						model.setCommand(cmd);
						ResistiveButtonsManager.getInstance().save(parent.getContext(), model.getButtonInfo());
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					ResistiveButtonViewModel model = (ResistiveButtonViewModel)parent.getTag();
					Command cmd = _commands.get(0);
					if (model.getCommandCode() != cmd.getCode()) {
						model.setCommand(cmd);
						ResistiveButtonsManager.getInstance().save(parent.getContext(), model.getButtonInfo());
					}
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		//holder.idView.setText(item.getButtonInfo().Id.toString());
		holder.mainValueView.setText(Integer.toString(item.getButtonInfo().MainLevel));
		holder.nameView.setText(item.getName());

		if (item.getLast()) {
			convertView.setBackgroundColor(0x7700FF00);
		} else {
			convertView.setBackgroundColor(0);
		}

		Log.d(TAG, "set item's spin");

		boolean set = false;
		for (int i = 0; i < _commands.size(); i++) {
			Command cmd = _commands.get(i);
			if (cmd.getCode().equals(item.getCommandCode())) {
				if (holder.commandCodeSpinner.getSelectedItemId() != i) {
					holder.commandCodeSpinner.setSelection(i, true);
				}
				Log.d(TAG, "found! " + i + " code = " + item.getCommandCode());
				set = true;
				break;
			}
		}
		if (!set) {
			Log.d(TAG, "not found, set nothing (0)");
			holder.commandCodeSpinner.setSelection(0, true);
		}

		return convertView;
	}

	class ViewHolder {
		TextView idView;
		TextView mainValueView;
		TextView nameView;
		Spinner commandCodeSpinner;
		ArrayAdapter<Command> commandAdapter;

	}
}
