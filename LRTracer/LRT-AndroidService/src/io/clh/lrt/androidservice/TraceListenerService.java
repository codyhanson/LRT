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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class TraceListenerService extends Service {
	  private Looper mServiceLooper;
	  private ServiceHandler mServiceHandler;
	  
	// Debug verbosity
	private static final String TAG = "lrt_androidservice";
	private static int LOGLEVEL 			= 3;	// 0 = None, 1 = Warn, 2 = Debug, 3 = Verbose
	private static boolean WARN 			= LOGLEVEL > 0;
	private static boolean DEBUG 			= LOGLEVEL > 1;
	private static boolean VERBOSE			= LOGLEVEL > 2;

	// Intents handled in this Service
	public static final String ACTION_LRT_TEST = "io.clh.lrt.action.test";

	// Broadcasts we make and receive
	public static final String BROADCAST_LRT_TEST = "io.clh.lrt.broadcast.test";
	
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
	          long endTime = System.currentTimeMillis() + 5*1000;
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
	          stopSelf(msg.arg1);
	      }
	  }

	  @Override
	  public void onCreate() {
          Log.i(TAG, "onCreate()");
	    // Start up the thread running the service.  Note that we create a
	    // separate thread because the service normally runs in the process's
	    // main thread, which we don't want to block.  We also make it
	    // background priority so CPU-intensive work will not disrupt our UI.
	    HandlerThread thread = new HandlerThread("ServiceStartArguments");
	    thread.start();
	    
	    // Get the HandlerThread's Looper and use it for our Handler 
	    mServiceLooper = thread.getLooper();
	    mServiceHandler = new ServiceHandler(mServiceLooper);
	  }

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
          Log.i(TAG, "onStartCommand()");
	      Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

	      // For each start request, send a message to start a job and deliver the
	      // start ID so we know which request we're stopping when we finish the job
	      Message msg = mServiceHandler.obtainMessage();
	      msg.arg1 = startId;
	      mServiceHandler.sendMessage(msg);
	      
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
	  }
	 

	    /**
	     * Listens for USB events, such as Disconnect, Permission to use, and data received
	     * 
	     * @category	Receiver
	     * @category	USB
	     */
		private final BroadcastReceiver mLrtReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
				if (DEBUG) Toast.makeText(context, "LRT broadcast received", Toast.LENGTH_SHORT).show();
		    	try {
			    	if(intent != null) {
			    		// Get the event that was broadcast
				    	String action = intent.getAction();
				    	if (DEBUG) Toast.makeText(context, "LRT broadcast action: "+action, Toast.LENGTH_SHORT).show();
				        if(action == null) {
				        	// Invalid action, exit
				        	if (DEBUG)	Toast.makeText(context, "LRT - No action given", Toast.LENGTH_SHORT).show();
				        	return;
				        }
			    	} else {
				    	if (DEBUG) Toast.makeText(context, "LRT - No intent given", Toast.LENGTH_SHORT).show();
			    	}
				} catch (Exception e) {
					Log.e(TAG, "Error: "+e);
				}
		    }
		};
	}
