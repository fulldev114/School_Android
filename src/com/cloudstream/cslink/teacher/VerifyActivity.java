package com.cloudstream.cslink.teacher;

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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.xmpp.teacher.Constant;

import org.json.JSONObject;

public class VerifyActivity extends Activity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String TAG = "MainActivity";

    private String login_url;
    MainProgress pDialog;
    private TextView  txtEmail;
    private EditText txtVerifyCode;
    String email;

    SharedPreferences myPrefs;
    SharedPreferences.Editor editor;

    Activity mActivity;
    private LinearLayout lin_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.adres_verify_activity);
        mActivity = this;

        myPrefs = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        editor = myPrefs.edit();
        email = myPrefs.getString("email", "");

        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtEmail.setText(email);
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

    public void verify( String vericode) throws Exception {
        if(vericode!=null && vericode.length()==0)
        {
            ApplicationData.showMessage(mActivity,
                    getResources().getString(R.string.str_verificaton_code),
                    getResources().getString(R.string.msg_invalidvericod),getResources().getString(R.string.str_ok));
        }
        else if (vericode.length() < 6) {
            ApplicationData.showMessage(mActivity,
                    getResources().getString(R.string.title_invalid_vericod),
                    getResources().getString(R.string.msg_invalidvericod),getResources().getString(R.string.str_ok));
        } else {
            login_url = ApplicationData.getlanguageAndApi(VerifyActivity.this, ConstantApi.VERIFYCODE_TEACHER)
                    + "email="
                    + email + "&vericode="
                    + vericode;

                loginapp(login_url);

        }
    }

    private void loginapp(String login_url2) {

        if (!GlobalConstrants.isWifiConnected(VerifyActivity.this)) {
            return;
        }

        if ( pDialog == null )
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
                            String msg="";
                            String flag = response.getString("flag");
                            if(response.has("msg"))
                             msg = response.getString("msg");

                            if (Integer.parseInt(flag) == 0) {  //failed
                                String errcode = response.getString("errcode");
                                if(msg!=null && msg.length()>0) {
                                    ApplicationData.showMessage(mActivity,
                                            getResources().getString(R.string.title_invalid_vericod),
                                            msg, getResources().getString(R.string.str_ok));
                                    txtVerifyCode.setText("");
                                }
                                else {
                                    if (errcode.equals("2")) {
                                        ApplicationData.showMessage(mActivity,
                                                getResources().getString(R.string.title_invalid_vericod),
                                                getResources().getString(R.string.msg_invalidvericod), getResources().getString(R.string.str_ok));
                                        txtVerifyCode.setText("");
                                    } else {
                                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_operation_error), false);
                                    }
                                }
                                lin_next.setEnabled(true);
                            } else {

                                String school_id = response.getString("schoolid");
                                String teacher_id = response.getString("teacherid");
                                editor.putString("user_password", "");
                                editor.putString("school_id", school_id);
                                editor.putString("teacher_id", teacher_id);
                                editor.putString("email", email);
                                editor.putBoolean("is_login", true);
                                editor.putString("jid",response.getString("jid"));
                                editor.putString("jid_pwd",response.getString("key"));
                                //editor.putBoolean("jid",response.getString("isaccount"));
                                editor.commit();
                                Intent in = new Intent(VerifyActivity.this, Pincode_activity.class);
                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(in);

                                lin_next.setEnabled(true);

                                if (checkPlayServices()) {
                                    // Start IntentService to register this application with GCM.
                                    Intent intent = new Intent(mActivity, RegistrationIntentService.class);
                                    intent.putExtra("teacher_id", teacher_id);
                                    startService(intent);
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

    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
    }



    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
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
        login_url=null;
        pDialog=null;
        txtEmail=null;
        txtVerifyCode=null;
        email=null;
        myPrefs=null;
        editor=null;
        mActivity=null;
        lin_next=null;

        System.gc();
    }
}
