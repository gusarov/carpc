package com.github.gusarov.carpc.Commands;

import android.content.Context;

public abstract class Command {
	public abstract String getCode();
	public abstract String getDisplayName();
	public abstract void execute(Context ctx);

	@Override
	public String toString() {
		return getDisplayName();
	}
}

