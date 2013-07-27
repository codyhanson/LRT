package io.clh.testapp;

import java.sql.Timestamp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

public class LRTracer {
	// Intents handled by the LRT service
	public static final String ACTION_LRT_START = "io.clh.lrt.action.start";
	public static final String ACTION_LRT_TRACE = "io.clh.lrt.action.trace";
	public static final String ACTION_LRT_STOP = "io.clh.lrt.action.stop";

	private static final String TAG = "LRTracer";

	public static int LRTcounter = 0;
	
	//We would probably get these during the instrumentation insertion phase
	public static final String appName = "io.clh.testapp";
	public static final String appVersion = "0.1.0-alpha";

	
	public static void startTrace(Context ctx){
		Log.d(TAG, "Send broadcast: Starting new Trace");
    	Intent connBroadcastIntent = new Intent(ACTION_LRT_START);
    	connBroadcastIntent.putExtra("appName", appName);
    	connBroadcastIntent.putExtra("appVersion", appVersion);

    	connBroadcastIntent.setPackage("io.clh.lrt.androidservice");		// Limit who sees the broadcast
    	ctx.sendBroadcast(connBroadcastIntent);
    	Log.d(TAG, "Sent.");
	}
	
	public static void trace(Context ctx, int lineNumber, String functionName) {
    	Intent connBroadcastIntent = new Intent(ACTION_LRT_TRACE);

    	connBroadcastIntent.putExtra("seq", LRTcounter++);
    	connBroadcastIntent.putExtra("lineNumber", lineNumber);
    	connBroadcastIntent.putExtra("methodSig", functionName);
    	
    	java.util.Date date= new java.util.Date();
   	 	Timestamp ts = new Timestamp(date.getTime());
    	connBroadcastIntent.putExtra("timestamp", ts.toString());

    	connBroadcastIntent.setPackage("io.clh.lrt.androidservice");		// Limit who sees the broadcast
    	ctx.sendBroadcast(connBroadcastIntent);
    	Log.d(TAG, "Sent.");
	}

	public static void endTrace(Context ctx, String endType) {
		Log.d(TAG, "Send broadcast: ending Trace");
    	Intent connBroadcastIntent = new Intent(ACTION_LRT_STOP);
    	connBroadcastIntent.setPackage("io.clh.lrt.androidservice");		// Limit who sees the broadcast
    	ctx.sendBroadcast(connBroadcastIntent);
    	Log.d(TAG, "Sent.");
	}
	
	    
}
