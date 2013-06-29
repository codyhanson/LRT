package io.clh.testapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class OtherActivity extends Activity {
	
	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
		  Log.v("OtherActivity","Activity Created");
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_other);
	    }


	  //on rating bar click
	  public void onRatingBarChange(){
		  Log.v("OtherActivity","Ratingbar Changed");

	  }
	  
	  //on checkbox click
	  public void onCheckboxChange() {
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
