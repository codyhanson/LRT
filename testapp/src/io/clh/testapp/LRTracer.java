package io.clh.testapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LRTracer {
	// Intents handled by the LRT service
	public static final String ACTION_LRT_TEST = "io.clh.lrt.action.test";
	private static final String TAG = "LRTracer";

	public static int LRTcounter = 0;
	
	public static void trace(Context ctx, String functionName) {
		String msg = functionName + " " + LRTcounter++;
    	Log.d(TAG, "Send broadcast: "+msg);
    	Intent connBroadcastIntent = new Intent(ACTION_LRT_TEST);
    	connBroadcastIntent.putExtra("message", msg);
    	connBroadcastIntent.setPackage("io.clh.lrt.androidservice");		// Limit who sees the broadcast
    	ctx.sendBroadcast(connBroadcastIntent);
    	Log.d(TAG, "Sent.");
	}

	
	    
}
