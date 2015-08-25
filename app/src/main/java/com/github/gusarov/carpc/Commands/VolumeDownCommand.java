package com.github.gusarov.carpc.Commands;

import android.content.Context;
import android.media.AudioManager;

public class VolumeDownCommand extends MediaCommand {
	@Override
	public String getCode() {
		return "VolumeDown";
	}

	@Override
	public String getDisplayName() {
		return "Volume Down";
	}

	@Override
	public void execute(Context ctx) {
		super.execute(ctx);
		AudioManager.adjustStreamVolume(android.media.AudioManager.STREAM_MUSIC, android.media.AudioManager.ADJUST_LOWER, android.media.AudioManager.FLAG_SHOW_UI);
	}

}
