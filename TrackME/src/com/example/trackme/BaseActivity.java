package com.example.trackme;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

/**
 * This activity is the parent of all the activities in the app.
 * This is just used for theming and action bar on the top
 * 
 */
public class BaseActivity extends SherlockFragmentActivity
{
	private ProgressDialog progress;
	private DialogFragment alert;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        
        alert = new SimpleAlertFragment();
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	  switch (item.getItemId()) {
	    case android.R.id.home: onBackPressed(); return true;
	  }
	    
	  return super.onOptionsItemSelected(item);
	}
    
    protected void showLoadingDialog(String title, String msg)
    {
    	progress = ProgressDialog.show(this, title, msg);
    }
    
    protected void hideLoadingDialog()
    {
    	progress.dismiss();
    }
    
    protected void showSimpleAlert(String title, String msg)
    {
    	Bundle args = new Bundle();
    	args.putString("title", title);
    	args.putString("message", msg);
    	alert.setArguments(args);
    	alert.show(this.getSupportFragmentManager(), title);
    }
    
    protected void hideSimpleAlert()
    {
    	alert.dismiss();
    }
}
