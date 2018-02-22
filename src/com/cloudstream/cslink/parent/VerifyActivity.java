package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
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
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.xmpp.parent.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import static com.cloudstream.cslink.parent.CommonUtilities.SENDER_ID;

public class VerifyActivity extends Activity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String TAG = "MainActivity";

    private String login_url;
    MainProgress pDialog;
    private TextView txtPhone;
    private EditText txtVerifyCode;
    int register = 0;
    String phone, child_array, email_id, password;
    private String parent_name;

    SharedPreferences myPrefs;
    SharedPreferences.Editor editor;

    Activity mActivity;
    private String parent_no = "1";
    private LinearLayout lin_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.verify_activity);
        mActivity = this;

        myPrefs = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        editor = myPrefs.edit();
        phone = myPrefs.getString("phone", "");
        password = myPrefs.getString("password", "");
        parent_no = myPrefs.getString("parent_no", "1");

        txtPhone = (TextView) findViewById(R.id.txtPhone);
        txtPhone.setText(phone);
        txtVerifyCode = (EditText) findViewById(R.id.txtVerifyCode);
        txtVerifyCode.setText("");

        lin_next = (LinearLayout) findViewById(R.id.lin_next);
        lin_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    verify(txtVerifyCode.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
//                finish();
            }
            return false;
        }
        return true;
    }

    public void verify(String vericode) throws Exception {
        if (vericode.length() == 0) {
            ApplicationData.showMessage(mActivity,
                    getResources().getString(R.string.str_verificaton_code),
                    getResources().getString(R.string.verify_desc), getResources().getString(R.string.str_ok));
        } else if (vericode.length() < 6) {
            ApplicationData.showMessage(mActivity,
                    getResources().getString(R.string.title_invalid_vericod),
                    getResources().getString(R.string.msg_invalidvericod), getResources().getString(R.string.str_ok));
        } else {
            login_url = ApplicationData.getlanguageAndApi(VerifyActivity.this, ConstantApi.VERIFYCODE)
                    + "phone="
                    + phone + "&vericode="
                    + vericode;

            loginapp(login_url);
        }
    }

    private void loginapp(String login_url2) {

        if (!GlobalConstrants.isWifiConnected(VerifyActivity.this)) {
            return;
        }

        if (pDialog == null)
            pDialog = new MainProgress(this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        // Log.e("URl", "" + login_url2);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, login_url2, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 0) {  //failed
                                String errcode = response.getString("errcode");
                                if (response.has("msg")) {
                                    String msg = response.getString("msg");
                                    ApplicationData.showToast(mActivity, msg, false);
                                }

                               /* if (errcode.equals("2")) {
                                    ApplicationData.showMessage(mActivity,
                                            getResources().getString(R.string.title_invalid_vericod),
                                            getResources().getString(R.string.msg_invalidvericod),getResources().getString(R.string.str_ok));
                                    txtVerifyCode.setText("");
                                } else {
                                    ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_operation_error), false);
                                }*/

                                lin_next.setEnabled(true);
                            } else {
//								register = 1;
//								registerdevice();


                                if (response.isNull("All Childs")) {
                                    child_array = "";
                                } else {
                                    JSONObject allchilds = response.getJSONObject("All Childs");
                                    JSONArray childs = allchilds.getJSONArray("childs");
                                    child_array = childs.toString();
                                }

                                SharedPreferences mprefer = getSharedPreferences(Constant.EMERGENCY_FILENAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = mprefer.edit();

                                String parent_id = response.getString("parentid");
                                email_id = response.getString("parent email");
                                parent_name = response.getString("parent name");
                                editor.putString("user_password", "");
                                editor.putBoolean("is_login", true);
                                edit.putBoolean("emeregency_popup", false);
                                editor.commit();
                                edit.commit();

                                addprefrences(parent_id, child_array);

                                lin_next.setEnabled(true);

                                if (checkPlayServices()) {
                                    // Start IntentService to register this application with GCM.
                                    Intent intent = new Intent(mActivity, RegistrationIntentService.class);
                                    intent.putExtra("parent_id", parent_id);
                                    intent.putExtra("parent_no", parent_no);
                                    startService(intent);


                                    Intent in = new Intent(VerifyActivity.this, Pincode_activity.class);
                                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    in.putExtra("childArray", child_array);
                                    startActivity(in);
                                    finish();

                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_operation_error), false);
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_operation_error), false);
            }
        });

        queue.add(jsObjRequest);
    }

    protected void addprefrences(String parent_id, String child_array2) {
        // TODO Auto-generated method stub
        // ........................sharedpreferences.......................//

        editor.putString("parent_id", parent_id);
        editor.putString("parent_emailid", email_id);
        editor.putString("user_password", password);
        editor.putString("child_array", child_array2);
        editor.putString("parent_name", parent_name);
        editor.putString("phone", phone);
        if (myPrefs.getString("language", "").equalsIgnoreCase("")) {
            editor.putString("language", "english");
        }

        editor.putString("notification", "0");
        editor.commit();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        Log.e("onresume calling", "Resume calling");
        super.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        TAG = null;
        login_url = null;
        pDialog = null;
        txtPhone = null;
        txtVerifyCode = null;
        phone = null;
        child_array = null;
        email_id = null;
        password = null;
        parent_name = null;
        myPrefs = null;
        editor = null;
        mActivity = null;
        parent_no = null;
        lin_next = null;

        System.gc();
    }
}
