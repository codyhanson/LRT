package io.clh.testapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	LRTracer.startTrace(this,14,"MainActivity.java","MainActivity.onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	LRTracer.trace(this,21,"MainActivity.java","MainActivity.onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    public void goToOtherActivity(View view) 
    {
    	LRTracer.trace(this,27,"MainActivity.java","MainActivity.goToOtherActivity");
        Intent intent = new Intent(getApplicationContext(), OtherActivity.class);
        startActivity(intent);
    }
    
    //Exit button click
    public void clickToExit(View view){
    	LRTracer.stopTrace(this,35,"MainActivity.java","clickToExit");
    	LRTracer.startTrace(this,38,"MainActivity.java","clickToExit");

    	//shut down and exit the app.
    }
    /*    
    public void onPause() {
    	
    }
    
    public void onResume() {
    	
    }
    
    public void onStop() {
    	
    }
    
    public void onSaveInstanceState(){
    
    }
    */
    
}
