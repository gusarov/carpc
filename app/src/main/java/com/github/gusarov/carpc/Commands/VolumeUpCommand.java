package com.github.gusarov.carpc.Commands;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class VolumeUpCommand extends MediaCommand {
	@Override
	public String getCode() {
		return "VolumeUp";
	}

	@Override
	public String getDisplayName() {
		return "Volume Up";
	}

	@Override
	public void execute(Context ctx) {
		super.execute(ctx);
		AudioManager.adjustStreamVolume(android.media.AudioManager.STREAM_MUSIC, android.media.AudioManager.ADJUST_RAISE, android.media.AudioManager.FLAG_SHOW_UI);
	}
}

