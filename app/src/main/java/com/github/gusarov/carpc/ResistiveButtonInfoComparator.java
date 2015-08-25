package com.github.gusarov.carpc;

import java.util.Comparator;

public class ResistiveButtonInfoComparator implements Comparator<ResistiveButtonInfo>
{
	public static ResistiveButtonInfoComparator Instance = new ResistiveButtonInfoComparator();

	@Override
	public int compare(ResistiveButtonInfo lhs, ResistiveButtonInfo rhs) {
		return Integer.compare(lhs.MainLevel, rhs.MainLevel);
	}
}

