package com.github.gusarov.carpc;

import android.util.Log;

import com.github.gusarov.carpc.Commands.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResistiveButtonViewModel {
	private List<Integer> _points = new ArrayList<Integer>();
	ResistiveButtonInfo _resistiveButtonInfo;
	boolean _last;

	public ResistiveButtonViewModel(ResistiveButtonInfo resistiveButtonInfo)
	{
		this._resistiveButtonInfo = resistiveButtonInfo;
		addPoint(resistiveButtonInfo.MainLevel);
	}

	public ResistiveButtonInfo getButtonInfo() {
		return _resistiveButtonInfo;
	}

	public int getAvg() {
		return (int) calculateAverage(_points);
	}

	private double calculateAverage(List<Integer> values) {
		Integer sum = 0;
		if (!values.isEmpty()) {
			for (Integer mark : values) {
				sum += mark;
			}
			double r = sum.doubleValue() / values.size();
			return r;
		}
		return sum;
	}

	public int getStart() {
		return getAvg() - 7;
	}

	public int getEnd() {
		return getAvg() + 7;
	}

	public void addPoint(int value) {
		_points.add(value);
		/*
		if (value > _end) {
			_end = value;
		}
		if (value < _start) {
			_start = value;
		}
		*/
		getButtonInfo().MainLevel = getAvg();
	}

	public String getName() {
		return _resistiveButtonInfo.Name;
	}

	public void setName(String value) {
		_resistiveButtonInfo.Name = value;
	}

	public String getCommandCode()
	{
		if (getButtonInfo().Command == null) {
			return "";
		}
		return getButtonInfo().Command.getCode();
	}

	public void setCommand(Command command)
	{
		Log.i("RB", "Change command of " + getAvg() + " to " + (command == null ? "<null>" : command.getCode()));
		getButtonInfo().Command = command;
	}

	public void setLast(boolean value) {
		_last = value;
	}

	public boolean getLast() {
		return _last;
	}

}
