package com.github.gusarov.carpc.Commands;

import android.content.Context;

public class NothingCommand extends Command {
	@Override
	public String getCode() {
		return "nothing";
	}

	@Override
	public String getDisplayName() {
		return "Do Nothing";
	}

	@Override
	public void execute(Context ctx) {
	}
}
