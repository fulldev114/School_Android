package com.request;

import android.app.Dialog;
import android.content.Context;

public class CustomDialog extends Dialog {

	public boolean dismissByBack = false;

	public CustomDialog(Context context) {
		super(context);

		// TODO Auto-generated constructor stub
	}

	/*@Override
	public void onBackPressed() {
		dismissByBack = true;
		this.dismiss();
	}
*/
	public boolean isDismissByBack() {
		return dismissByBack;
	}

	public void setDismissByBack(boolean dismissByBack) {
		this.dismissByBack = dismissByBack;
	}

}
