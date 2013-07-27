package io.clh.testapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

public class OtherActivity extends Activity {
	
	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
		  LRTracer.trace(this,11,"OtherActivity.java","OtherActivity.onCreate");

		  Log.v("OtherActivity","Activity Created");
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_other);
	    }


	  //on rating bar click
	  public void onRatingBarChange(View view){
		  LRTracer.trace(this,21,"OtherActivity.java","OtherActivity.onRatingBarChange");
		  Log.v("OtherActivity","Ratingbar Changed");
	  }
	  
	  
	  //on checkbox click
	  public void onCheckboxClicked(View view) {
		  LRTracer.trace(this,28,"OtherActivity.java","OtherActivity.onCheckboxClicked");
		  Log.v("OtherActivity","Checkbox Changed");
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
