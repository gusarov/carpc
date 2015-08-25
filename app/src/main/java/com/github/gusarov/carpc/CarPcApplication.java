package com.github.gusarov.carpc;

import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

public class CarPcApplication extends Application {
	static final String TAG = CarPcApplication.class.getSimpleName();

	Handler h = new Handler() {
		public void handleMessage(Message m) {
			synchronized (_callbacks) {
				Log.i(TAG, "Broadcast message to " + _callbacks.size() + " subscribers");
				for (int i =0; i< _callbacks.size(); i++){
					_callbacks.get(i).handleMessage(m);
				}
			}
		}
	};

	ArrayList<Handler.Callback> _callbacks = new ArrayList<>();

	public Handler getHandler() {
		return h;
	}

	public void subscribe(Handler.Callback c) {
		Log.i(TAG, "someone subscribed");
		synchronized (_callbacks) {
			_callbacks.add(c);
		}
	}

	public void unsubscribe(Handler.Callback c) {
		Log.i(TAG, "someone unsubscribed");
		synchronized (_callbacks) {
			_callbacks.remove(c);
		}
	}

	public boolean ControllerConnected;


}
