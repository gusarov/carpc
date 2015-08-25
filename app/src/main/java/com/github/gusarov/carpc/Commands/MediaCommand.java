package com.github.gusarov.carpc.Commands;

import android.content.Context;
import android.media.AudioManager;

public abstract class MediaCommand extends Command {

	protected AudioManager AudioManager;

	@Override
	public void execute(Context ctx) {
		AudioManager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
	}
}
