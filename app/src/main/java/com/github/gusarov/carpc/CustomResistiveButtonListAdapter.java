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
		final ViewHolder holder;
		if (convertView == null) {
			Log.d(TAG, "RLA - Inflate new item for position " + position);
			convertView = _layoutInflater.inflate(R.layout.list_row_layout, null);
			holder = new ViewHolder();
			holder.idView = (TextView) convertView.findViewById(R.id.id);
			holder.nameView = (TextView) convertView.findViewById(R.id.name);
			holder.mainValueView = (TextView) convertView.findViewById(R.id.mainValue);
			holder.commandCodeSpinner = (Spinner) convertView.findViewById(R.id.command);

			if(holder.commandCodeSpinner.getTag() != null) {
				Log.d(TAG, "WTF???");
			}

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

			Log.d(TAG, "RLA Inflate new item - holder " + holder.hashCode() + " adapter " + holder.commandAdapter.hashCode() + " tag item " + item.hashCode());

			holder.commandCodeSpinner.setAdapter(holder.commandAdapter);
			holder.commandCodeSpinner.setTag(item);

			int spinPos = holder.commandAdapter.getPosition(item.getButtonInfo().Command);
			holder.commandCodeSpinner.setSelection(spinPos);

			holder.commandCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				{
					this.holder1 = holder;
					this.item1 = item;
				}
				ViewHolder holder1;
				ResistiveButtonViewModel item1;
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
					if (holder1.muteSelection) return;
					int realPos = holder1.commandCodeSpinner.getSelectedItemPosition();
					if(realPos != position){
						Log.d(TAG, "wow");
					}
					ResistiveButtonViewModel model = item1;//(ResistiveButtonViewModel)adapterView.getTag();
					Command cmd = (Command)adapterView.getItemAtPosition(position);
					if (model.getCommandCode() != cmd.getCode()) {
						Log.d(TAG, "ItemSelected: position " + position + " " + cmd.getCode() + " set it to model " + model.getName() + " avg " + model.getAvg());
						model.setCommand(cmd);
						ResistiveButtonsManager.getInstance().save(adapterView.getContext(), model.getButtonInfo());
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					if (holder1.muteSelection) return;
					Log.d(TAG, "NothingSelected");
					/*
					ResistiveButtonViewModel model = (ResistiveButtonViewModel)parent.getTag();
					Command cmd = _commands.get(0);
					if (model.getCommandCode() != cmd.getCode()) {
						model.setCommand(cmd);
						ResistiveButtonsManager.getInstance().save(parent.getContext(), model.getButtonInfo());
					}
					*/
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

		Log.d(TAG, "RLA position " + position + " - set item's spin");

		boolean set = false;
		for (int i = 0; i < _commands.size(); i++) {
			Command cmd = _commands.get(i);
			if (cmd.getCode().equals(item.getCommandCode())) {
				final int ii = holder.commandAdapter.getPosition(cmd);
				Log.d(TAG, "found! " + i +" "+ ii + " code = " + item.getCommandCode());
				if (holder.commandCodeSpinner.getSelectedItemPosition() != ii) {
					Log.d(TAG, "post " + ii + " because current is " + holder.commandCodeSpinner.getSelectedItemPosition());
					holder.commandCodeSpinner.post(new Runnable() {
						@Override
						public void run() {
							if (holder.commandCodeSpinner.getSelectedItemPosition() != ii) {
								Log.d(TAG, "Inflate position " + position + " set selection to " + ii);
								holder.muteSelection = true;
								holder.commandCodeSpinner.setSelection(ii, false);
								holder.muteSelection = false;
							} else {
								Log.d(TAG, "already set to " + ii + " after post");
							}
						}
					});
				} else {
					Log.d(TAG, "already set to " + ii);
				}
				set = true;
				break;
			}
		}
		if (!set) {
			Log.d(TAG, "not found, reset to nothing (0)");
			if (holder.commandCodeSpinner.getSelectedItemPosition() != 0) {
				Log.d(TAG, "post 0 because current is " + holder.commandCodeSpinner.getSelectedItemPosition());
				holder.commandCodeSpinner.post(new Runnable() {
					@Override
					public void run() {
						if (holder.commandCodeSpinner.getSelectedItemPosition() != 0) {
							Log.d(TAG, "RLA posted - set selection to 0");
							holder.muteSelection = true;
							holder.commandCodeSpinner.setSelection(0, false);
							holder.muteSelection = false;
						} else {
							Log.d(TAG, "already reset to 0 after post");
						}
					}
				});
			} else {
				Log.d(TAG, "already reset to 0");
			}
		}

		return convertView;
	}

	class ViewHolder {
		TextView idView;
		TextView mainValueView;
		TextView nameView;
		Spinner commandCodeSpinner;
		ArrayAdapter<Command> commandAdapter;
		boolean muteSelection;
	}
}
