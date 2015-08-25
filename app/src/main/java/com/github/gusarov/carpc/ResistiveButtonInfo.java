package com.github.gusarov.carpc;

import com.github.gusarov.carpc.Commands.Command;

import java.util.Comparator;
import java.util.UUID;

public class ResistiveButtonInfo implements Comparable<Integer> {
	public ResistiveButtonInfo(UUID id, String name, int mainLevel, Command command) {
		Id = id;
		Name = name;
		MainLevel = mainLevel;
		Command = command;
	}

	public UUID Id;
	public String Name;
	public int MainLevel;
	public Command Command;

	@Override
	public int compareTo(Integer another)
	{
		return compare(MainLevel, another);
	}

	static int compare(int lhs, int rhs) {
		return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
	}

}

