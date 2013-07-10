package io.clh.lrt.androidservice;

import java.util.LinkedList;
import java.util.Queue;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	// Debug verbosity
	private static final String TAG = "lrt_main";
	private static int LOGLEVEL = 3; // 0 = None, 1 = Warn, 2 = Debug, 3 =
										// Verbose
	private static boolean WARN = LOGLEVEL > 0;
	private static boolean DEBUG = LOGLEVEL > 1;
	private static boolean VERBOSE = LOGLEVEL > 2;

	private TextView dbgView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.i(TAG, "MainActivity#Create");
		startService(new Intent(MainActivity.this, TraceListenerService.class));

		sendBroadcast("Started the main LRT activity");
		sendBroadcast("Second broadcast");

		Button button = (Button) findViewById(R.id.btnRefresh);

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dumpQueue();
			}
		});

		dbgView = (TextView) findViewById(R.id.dbg);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void sendBroadcast(String msg) {
		Log.d(TAG, "Send broadcast: " + msg);
		Intent connBroadcastIntent = new Intent(
				TraceListenerService.ACTION_LRT_TEST);
		connBroadcastIntent.putExtra("message", msg);
		connBroadcastIntent.setPackage("io.clh.lrt.androidservice"); // Limit
																		// who
																		// sees
																		// the
																		// broadcast
		sendBroadcast(connBroadcastIntent);
		Log.d(TAG, "Sent.");
	}

	public void teddyBear() {
		Log.i(TAG, "I'm safe!");
	}

	public void dumpQueue() {
		Log.w(TAG, "DUMP DAT QUEUE, but not really");
//		LinkedList<String> dataBuffer = TraceListenerService.getQueue();
//		String stringBuffer = "";
//		if (dataBuffer != null) {
//			for (String string : dataBuffer) {
//				stringBuffer += string + "\n";
//			}
//		}
//		dbgView.setText(stringBuffer);
	}
}
