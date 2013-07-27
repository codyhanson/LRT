/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.clh.lrt.androidservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class TraceListenerService extends Service {
	private static final String TRACE_SERVICE_VERSION = "0.1.0";
	private static final String API_URL = "lrtserver.herokuapp.com";
	
	private HashMap<String, String> appLut = new HashMap<String, String>();
	
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private LinkedList<String> mDataBuffer = new LinkedList<String>();
	private HttpClient restClient;
	private HttpPost postMethod;

	// Debug verbosity
	private static final String TAG = "lrt_androidservice";
	private static int LOGLEVEL = 3; // 0 = None, 1 = Warn, 2 = Debug, 3 =
										// Verbose
	private static boolean WARN = LOGLEVEL > 0;
	private static boolean DEBUG = LOGLEVEL > 1;
	private static boolean VERBOSE = LOGLEVEL > 2;

	// Intents handled in this Service
	public static final String ACTION_LRT_TRACE = "io.clh.lrt.action.trace";
	public static final String ACTION_LRT_START = "io.clh.lrt.action.start";
	public static final String ACTION_LRT_STOP = "io.clh.lrt.action.stop";

	// Broadcasts we make and receive
	public static final String BROADCAST_LRT = "io.clh.lrt.broadcast";

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		private static final String TAG = "lrt_androidservice";

		public ServiceHandler(Looper looper) {
			super(looper);
			Log.i(TAG, "ServiceHandler()");
		}

		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "handleMessage()");
			// Normally we would do some work here, like download a file.
			// For our sample, we just sleep for 5 seconds.
			long endTime = System.currentTimeMillis() + 5 * 1000;
			while (System.currentTimeMillis() < endTime) {
				synchronized (this) {
					try {
						wait(endTime - System.currentTimeMillis());
					} catch (Exception e) {
					}
				}
			}
			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			// stopSelf(msg.arg1);
		}
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments");
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);

		restClient = new DefaultHttpClient();

		//network on main thread H@XXX
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		
		// Register the LRT receiver
		IntentFilter filter = new IntentFilter(ACTION_LRT_TRACE);
		registerReceiver(mLrtReceiver, filter);

		setNotification("LRT Tracing Service Running", true);

		infiLoop();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand()");
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind()");
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
		unregisterReceiver(mLrtReceiver);

	}

	private void infiLoop() {
//		Log.i(TAG, "Loop...");
//
//		dumpQueue();
//
//		Handler handler = new Handler();
//		handler.postDelayed(new Runnable() {
//			public void run() {
//				// runs a method every 30s
//				infiLoop();
//			}
//		}, 10000);
	}

	/**
	 * Listens for USB events, such as Disconnect, Permission to use, and data
	 * received
	 * 
	 * @category Receiver
	 * @category USB
	 */
	private final BroadcastReceiver mLrtReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			// if (DEBUG) Toast.makeText(context, "LRT broadcast received",
			// Toast.LENGTH_SHORT).show();
			try {
				if (intent != null) {
					// Get the event that was broadcast
					String action = intent.getAction();
					Log.e(TAG, "ACTION: "+action);
					if(action.equals(ACTION_LRT_START)) {
						Log.v(TAG, "START");
						
						traceEventStart(context, intent);
						
					} else if(action.equals(ACTION_LRT_STOP)) {
						Log.v(TAG, "STOP");
						
						traceEventStop(context, intent);
						
					} else if(action.equals(ACTION_LRT_TRACE)) {
						Log.v(TAG, "TRACE");
						
						traceEvent(context, intent);
						
					} else {
						Log.w(TAG, "Unknown action: "+action);
					}
				} else {
					if (DEBUG)
						Toast.makeText(context, "LRT - No intent given",
								Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				Log.e(TAG, "Error: " + e);
			}
		}
	};
	
	private void traceEventStart(Context context, Intent intent) {
		JSONObject jsonData = new JSONObject();
		try {
			String appName = intent.getStringExtra("appName");
			jsonData.put("appName", appName );
			jsonData.put("appVersion", intent.getStringExtra("appVersion") );
			jsonData.put("osType", "Android");
			jsonData.put("osVersion", android.os.Build.VERSION.SDK_INT);
			jsonData.put("traceServiceVersion", TRACE_SERVICE_VERSION);
			String userId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			
			Log.d(TAG, "start trace: "+jsonData.toString());
			
			HttpResponse response = postAPI("users/"+userId+"/traces", "trace", jsonData.toString());
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String json = reader.readLine();
			JSONTokener tokener = new JSONTokener(json);
			JSONObject finalResult = new JSONObject(tokener);
			Log.d(TAG, "start trace got response: "+finalResult.toString());
			
			String traceId = finalResult.getString("_id");
			
			appLut.put(appName, traceId);
			
			traceEvent(context, intent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void traceEventStop(Context context, Intent intent) {
		JSONObject jsonData = new JSONObject();
		String appName = intent.getStringExtra("appName");
		traceEvent(context, intent);
		Log.d(TAG, "trace done: "+intent.toString());
		appLut.remove(appName);
	}
	
	private void traceEvent(Context context, Intent intent) {		
		JSONObject jsonData = new JSONObject();
		try {
			String appName = intent.getStringExtra("appName");
			
			if( appLut.get(appName) == null ) {
				intent.putExtra("appVersion", "UNKNOWN");
				traceEventStart(context, intent);
				return;
			}
			
			jsonData.put("methodSig", intent.getStringExtra("methodSig") );
			jsonData.put("timestamp", intent.getStringExtra("timestamp") );
			jsonData.put("fileName", intent.getStringExtra("fileName") );
			jsonData.put("lineNumber", intent.getIntExtra("lineNumber", -1));
			jsonData.put("lineseq", intent.getIntExtra("seq", -1));
			
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(jsonData);
			
			Log.d(TAG, "trace event: "+jsonData.toString());
			
			HttpResponse response = postAPI("traces/"+appLut.get(appName)+"/tracepoints", "trace", jsonArray.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setNotification(String msg, boolean ongoing) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		CharSequence notiText = msg;
		long meow = System.currentTimeMillis();

		Notification notification = new Notification(icon, notiText, meow);

		Context context = getApplicationContext();
		CharSequence contentTitle = "LRT notification";
		CharSequence contentText = msg;
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		int SERVER_DATA_RECEIVED = 1;
		if (ongoing) {
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
			startForeground(1337, notification);
		} else {
			notificationManager.notify(SERVER_DATA_RECEIVED, notification);
		}
	}

	// public static LinkedList<String> getQueue() {
	// // Log.i(TAG, mDataBuffer.toString());
	// Log.v(TAG, "Dump queue | size = " + mDataBuffer.size());
	// return mDataBuffer;
	// }

//	public void dumpQueue() {
//		Log.v(TAG, "Dump queue | size = " + mDataBuffer.size());
//
//		while (mDataBuffer.size() > 0) {
//			String item = mDataBuffer.remove();
//			Log.v(TAG, " '--> " + item);
//			hitAPI("debug", item);
//		}
//		Log.v(TAG, "[====/dump====]");
//	}

	public HttpResponse postAPI(String path, String key, String jsonData) {
		try {
			jsonData = jsonData.replace("\\\"", "\"");
//			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//		      nameValuePairs.add(new BasicNameValuePair(key,
//		          jsonData));
		      

              JSONObject data = new JSONObject();
              try {
				data.put(key, jsonData);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      
		      StringEntity se = new StringEntity( jsonData.toString());
              se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
              
		      postMethod = new HttpPost("http://lrtserver.herokuapp.com/"+path);
		      postMethod.setEntity(se);
//		      postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		 	
			return restClient.execute(postMethod);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
