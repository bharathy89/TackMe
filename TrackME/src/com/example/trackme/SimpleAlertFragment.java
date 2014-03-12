package com.example.trackme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * I have no idea for what this class is used. But this is being called from BaseActivity
 * which we use for other Activities.
 * 
 * @author bharatyarlagadda
 *
 */
public class SimpleAlertFragment extends DialogFragment
{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(this.getArguments().getString("title"))
				.setMessage(this.getArguments().getString("message"))
				.setNeutralButton("OK", new OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.dismiss();
					}
				});
		
		return builder.create();
	}
	
	@Override public void onSaveInstanceState(Bundle outState) { 
		//first saving my state, so the bundle wont be empty. 
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE"); 
		super.onSaveInstanceState(outState); 
	} 
}
