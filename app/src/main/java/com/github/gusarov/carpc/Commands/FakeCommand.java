package com.github.gusarov.carpc.Commands;

import android.content.Context;

/**
 * Created by xkip on 2015-08-09.
 */
public class FakeCommand extends Command {
	String _code;

	public FakeCommand(String code) {
		_code = code;
	}

	public FakeCommand() {
		_code = "FakeCommand";
	}

	@Override
	public String getCode() {
		return _code;
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public void execute(Context ctx) {

	}
}
