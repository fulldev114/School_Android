package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
import com.common.dialog.MainProgress;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.xmpp.parent.Constant;
import com.cloudstream.cslink.R;
import org.json.JSONObject;

public class ChangePincode extends Activity {
    EditText _current_pincode, _new_pincode, _confirm_pincode;
    ImageView _back;
    LinearLayout send;

    private String login_url;
    MainProgress pDialog;

    TextView lang_header;

    SharedPreferences sharedpref;

    String _parent_id, _parent_no, language;

    Activity mActivity;
    private TextView txt_cancel;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.changepin_code);

        mActivity = this;

        sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        _parent_id = sharedpref.getString("parent_id", "");
        _parent_no = sharedpref.getString("parent_no", "");

        findview();

        lang_header.setText(getResources().getString(R.string.str_change_pincode));

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
        lang_header = (TextView) findViewById(R.id.textView1);
        _current_pincode = (EditText) findViewById(R.id.editText_currentpincode);
        _new_pincode = (EditText) findViewById(R.id.editText_newpincode);
        _confirm_pincode = (EditText) findViewById(R.id.editText_confirmpincode);
        txt_cancel = (TextView) findViewById(R.id.txt_cancel);
        _back = (ImageView) findViewById(R.id.imgback);
        _back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
        } else if (_confirm_pincode.getText().length() == 0) {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_confirmpincode), false);
        } else if (!_new_pincode.getText().toString().equalsIgnoreCase(_confirm_pincode.getText().toString())) {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_pincode_notmatched), false);
        }
        /*else if (_confirm_pincode.getText().toString().equalsIgnoreCase("1111")) {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_not_use_pincode111), false);
            _new_pincode.setText("");
            _confirm_pincode.setText("");
        }*/ else {
            login_url = ApplicationData.getlanguageAndApi(ChangePincode.this, ConstantApi.CHANGE_PIN_PHONE)+"phone=" + sharedpref.getString("phone", "")
                    + "&newcode=" + _new_pincode.getText().toString()
                    + "&oldcode=" + _current_pincode.getText().toString()
                    + "&parent_no=" + _parent_no;
            changepassword(login_url);
        }
    }

    private void changepassword(String login_url2) {
        // TODO Auto-generated method stub
        if (!ApplicationData.checkRight(mActivity)) {
            return;
        }
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
                                ApplicationData.showMessage(mActivity, getResources().getString(R.string.str_success), getResources().getString(R.string.msg_success_change_pincode), getResources().getString(R.string.str_ok));
                               //set in sharedpreference.
                                editor = sharedpref.edit();
                                editor.remove("pincode");
                                editor.putString("pincode",_new_pincode.getText().toString());
                                editor.commit();
                                _current_pincode.setText("");
                                _new_pincode.setText("");
                                _confirm_pincode.setText("");
                            } else {
                                String msg=response.getString("msg");
                                ApplicationData.showMessage(mActivity, getResources().getString(R.string.str_failed), msg, getResources().getString(R.string.str_ok));
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

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        _current_pincode=null;
        _new_pincode=null;
        _confirm_pincode=null;
        _back=null;
        send=null;
        login_url=null;
        pDialog=null;
        lang_header=null;
        sharedpref=null;
        _parent_id=null; _parent_no=null; language=null;
        mActivity=null;
        txt_cancel=null;
        editor=null;

        System.gc();
    }
}
