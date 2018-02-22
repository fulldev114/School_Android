package com.cloudstream.cslink.parent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cloudstream.cslink.R;

public class Dialoglogout extends DialogFragment {

	String language, msg_title, msg_message,_cancel,_ok;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		SharedPreferences sharedpref = getActivity().getSharedPreferences(
				"absentapp", 0);
		// ........languagechnage............//
		language = sharedpref.getString("language", "");
		msg_title = "Logout";
		msg_message = "Are you sure you want to logout?";
		_cancel="Cancel";

		return new AlertDialog.Builder(getActivity())
		// Set Dialog Icon
				.setIcon(R.drawable.aboutus_icon)
				// Set Dialog Title
				.setTitle(msg_title)
				// Set Dialog Message
				.setMessage(msg_message)

				// Positive button
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						// Do something else

						// ........................sharedpreferences.......................//
						SharedPreferences myPrefs = getActivity()
								.getSharedPreferences("absentapp",
										Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = myPrefs.edit();

						editor.putString("parent_id", "");
						editor.putString("child_array", "");
						editor.putString("parent_emailid", "");
						editor.putString("parent_name", "");
						editor.putBoolean("is_login", false);

						editor.commit();

						Intent in = new Intent(getActivity(),
								ParentLoginActivity.class);
						in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						startActivity(in);
						getActivity().finish();
					}
				})

				// Negative Button
				.setNegativeButton(_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Do something else
							}
						}).create();
	}
}
