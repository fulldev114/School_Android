package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudstream.cslink.R;
import com.common.dialog.MainProgress;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.xmpp.teacher.Constant;

import org.json.JSONObject;

public class ChangePincode extends ActivityHeader {
	EditText _current_pincode, _new_pincode, _confirm_pincode;
	//ImageView _back;
	LinearLayout send;

	private String login_url;
	MainProgress pDialog;

	//TextView lang_header;

	SharedPreferences sharedpref;
	
	String _parent_id, language;

	Activity mActivity;
    private TextView txt_cancel;
    private SharedPreferences.Editor editor;
    private LayoutInflater inflater;
    private LinearLayout rootView;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        rootView = (LinearLayout)inflater.inflate(R.layout.adres_changepin_code, null);
        relwrapp.addView(rootView);

		mActivity = this;

        hideKeyboardForFocusedView(this);

        sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
		_parent_id = sharedpref.getString("parent_id", "");

		findview();

        //showheader
        showheadermenu(ChangePincode.this, getString(R.string.str_change_pincode),R.color.white_light,false);
		//lang_header.setText(getResources().getString(R.string.str_change_pincode));


        txt_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _current_pincode.setText("");
                _new_pincode.setText("");
                _confirm_pincode.setText("");
                finish();
            }
        });
	}

	private void findview() {
	//	lang_header = (TextView) findViewById(R.id.textView1);
		_current_pincode = (EditText) findViewById(R.id.editText_currentpincode);
		_new_pincode = (EditText) findViewById(R.id.editText_newpincode);
		_confirm_pincode = (EditText) findViewById(R.id.editText_confirmpincode);

        txt_cancel = (TextView) findViewById(R.id.txt_cancel);
		send = (LinearLayout) findViewById(R.id.send);
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				send_password();
			}
		});
	}

	protected void send_password() {
		if (_current_pincode.getText().length() == 0) {
			ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_currentpincode), false);
			_current_pincode.setFocusable(true);
		} else if (_new_pincode.getText().length() == 0) {
			ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_newpincode), false);
			_new_pincode.setFocusable(true);
		} else if (_confirm_pincode.getText().length() == 0){
			ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_confirmpincode), false);
		} else if (! _new_pincode.getText().toString().equalsIgnoreCase(_confirm_pincode.getText().toString())) {
			ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_pincode_notmatched), false);
		} else {
			login_url = ApplicationData.getlanguageAndApi(ChangePincode.this, ConstantApi.CHANGE_PINCODE_TEACHER)
					+ "teacher_id="+sharedpref.getString("teacher_id", "")
					+ "&newcode=" + _new_pincode.getText().toString()
					+ "&oldcode=" + _current_pincode.getText().toString();
			changepassword(login_url);
		}
	}

	private void changepassword(String login_url2) {
		// TODO Auto-generated method stub
		if (!GlobalConstrants.isWifiConnected(this)) {
			return;
		}
		if (pDialog == null)
			pDialog = new MainProgress(this);
		pDialog.setCancelable(false);
		pDialog.setMessage(getResources().getString(R.string.str_wait));
		pDialog.show();

		RequestQueue queue = Volley.newRequestQueue(this);

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.GET, login_url2, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						pDialog.dismiss();
						try {
							String flag = response.getString("flag");
							if (Integer.parseInt(flag) == 1) {
								ApplicationData.showMessage(mActivity, getResources().getString(R.string.str_success), getResources().getString(R.string.msg_success_change_pincode),getResources().getString(R.string.str_ok));
                                editor = sharedpref.edit();
                                editor.remove("pincode");
                                editor.putString("pincode", _new_pincode.getText().toString());
                                editor.commit();

                                _current_pincode.setText("");
                                _new_pincode.setText("");
                                _confirm_pincode.setText("");

                            } else {
                                if(response.has("msg"))
                                {
                                    String msg = response.getString("msg");
                                    ApplicationData.showMessage(mActivity, getResources().getString(R.string.str_failed), msg,getResources().getString(R.string.str_ok));
                                }
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						pDialog.dismiss();
						ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
					}
				});
		queue.add(jsObjRequest);
	}
	public void onLowMemory() {
		super.onLowMemory();
	}

    public static void hideKeyboardForFocusedView(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();

        _current_pincode=null; _new_pincode=null; _confirm_pincode=null;
        send=null;
        login_url=null;
        pDialog=null;
        sharedpref=null;
        _parent_id=null; language=null;
        mActivity=null;
        txt_cancel=null;
        editor=null;
        inflater=null;
        rootView=null;

    }
}
