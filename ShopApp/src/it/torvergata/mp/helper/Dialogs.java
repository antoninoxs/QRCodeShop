package it.torvergata.mp.helper;

import it.torvergata.mp.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Dialogs {

	public Dialogs(){
		
	}
	public AlertDialog ConnectionTimeout(Context context) {
		AlertDialog alertDialog = new AlertDialog.Builder(context)
				.setTitle(R.string.tWarning)
				.setMessage(R.string.tTimeout)
				.setIcon(R.drawable.timeout)
				.setPositiveButton(R.string.tOk,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss(); 
								
							}
						})
				.create();
		return alertDialog;
	}
	
}
