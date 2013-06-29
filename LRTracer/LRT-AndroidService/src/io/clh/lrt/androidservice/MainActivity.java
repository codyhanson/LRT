package io.clh.lrt.androidservice;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	  
	// Debug verbosity
	private static final String TAG = "lrt_main";
	private static int LOGLEVEL 			= 3;	// 0 = None, 1 = Warn, 2 = Debug, 3 = Verbose
	private static boolean WARN 			= LOGLEVEL > 0;
	private static boolean DEBUG 			= LOGLEVEL > 1;
	private static boolean VERBOSE			= LOGLEVEL > 2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.i(TAG, "MainActivity#Create");
		startService(new Intent(MainActivity.this, TraceListenerService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
