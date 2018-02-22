package com.cloudstream.cslink.teacher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.util.Locale;

public class LoginActivity extends Activity {

    private EditText edtEmail;
    private String login_url;
    MainProgress pDialog;
    String language;
    private TextView _tv_english, _tv_nowrgian;//textRegister,
    RelativeLayout textlogin;
    LinearLayout _english, _nowrgian;
    int sdk = android.os.Build.VERSION.SDK_INT;

    SharedPreferences myPrefs;
    SharedPreferences.Editor editor;

    Activity mActivity;
    private TextView txt_next;
    // TextView txtDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.adres_login_activity);

        mActivity = this;

        myPrefs = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        editor = myPrefs.edit();


        //   txtDesc = (TextView) findViewById(R.id.txtDesc);
        _tv_english = (TextView) findViewById(R.id.textView_english);
        _tv_nowrgian = (TextView) findViewById(R.id.textView_nowrgian);
        _english = (LinearLayout) findViewById(R.id.english);
        _nowrgian = (LinearLayout) findViewById(R.id.norwgian);
        findview();

        editor.putString("language", "nowrgian");
        editor.commit();
        change_lang();


        _english.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                editor.putString("language", "english");

                editor.commit();

                change_lang();
            }
        });

        _nowrgian.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                editor.putString("language", "nowrgian");

                editor.commit();

                change_lang();
            }
        });



    }

    private void findview() {
        // TODO Auto-generated method stub

        textlogin = (RelativeLayout) findViewById(R.id.textView2);
        textlogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                login(textlogin);
            }
        });
        txt_next = (TextView) findViewById(R.id.txt_next);
        //   textRegister = (TextView) findViewById(R.id.textView1);
        edtEmail = (EditText) findViewById(R.id.et_emailid);
    }

    public void login(View v) {

        if (edtEmail.getText().toString().length() > 0) {
            if (ApplicationData.isValidEmail(edtEmail.getText().toString())) {
                login_url = ApplicationData.getlanguageAndApi(LoginActivity.this, ConstantApi.VERIFY_BY_EMAIl)
                        + "email="
                        + edtEmail.getText().toString();
                edtEmail.setEnabled(false);
                getverify(login_url);
            } else {
                ApplicationData.showToast(this, getResources().getString(R.string.msg_invalid_email), true);
            }
        } else {
            ApplicationData.showToast(this, getResources().getString(R.string.msg_no_email), true);
        }
    }

    private void getverify(String verify_url) {
        if (!GlobalConstrants.isWifiConnected(LoginActivity.this)) {
            return;
        }

        if (pDialog == null)
            pDialog = new MainProgress(this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.show();
        edtEmail.setEnabled(true);
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, verify_url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 1) {

                                editor.putString("email", edtEmail.getText().toString());

                                if (pDialog != null && pDialog.isShowing()) {
                                    pDialog.dismiss();
                                }

                                if(response.has("update_info")) {
                                    JSONObject job = response.getJSONObject("update_info");
                                    if (job.has("Android")) {
                                        JSONObject jandroid = job.getJSONObject("Android");

                                        String forceUpdateApp = jandroid.getString("forceUpdateApp");
                                        String isVersionDifferent = jandroid.getString("isVersionDifferent");
                                        String MessageType = jandroid.getString("MessageType");
                                        String URL = jandroid.getString("URL");
                                        String skipbuttontitle = jandroid.getString("skipbuttontitle");
                                        String updatebuttontitle = jandroid.getString("updatebuttontitle");

                                        editor.putString(Constant.ForceUpdate, forceUpdateApp);
                                        editor.putString(Constant.IsVersionDifferent, isVersionDifferent);
                                        editor.putString(Constant.Update_Message, MessageType);
                                        editor.putString(Constant.URL, URL);
                                        editor.putString(Constant.Skipbuttontitle, skipbuttontitle);
                                        editor.putString(Constant.Updatebuttontitle, updatebuttontitle);
                                        editor.commit();

                                        if (pDialog != null && pDialog.isShowing()) {
                                            pDialog.dismiss();
                                        }

                                        if (forceUpdateApp.equalsIgnoreCase("Yes")) {
                                            Intent intent = new Intent(LoginActivity.this, UpdateAvailableActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Intent in = new Intent(LoginActivity.this, VerifyActivity.class);
                                            startActivity(in);

                                            if (isVersionDifferent.equalsIgnoreCase("Yes")) {
                                                Intent in1 = new Intent(LoginActivity.this, UpdateAvailableActivity.class);
                                                startActivity(in1);
                                            }
                                        }
                                        finish();
                                    }
                                    else {
                                        if (pDialog != null && pDialog.isShowing()) {
                                            pDialog.dismiss();
                                        }
                                        Intent in = new Intent(LoginActivity.this, VerifyActivity.class);
                                        startActivity(in);
                                        finish();
                                    }
                                }else {
                                    if (pDialog != null && pDialog.isShowing()) {
                                        pDialog.dismiss();
                                    }
                                    Intent in = new Intent(LoginActivity.this, VerifyActivity.class);
                                    startActivity(in);
                                    finish();
                                }
                                editor.commit();


                            } else {
                                pDialog.dismiss();

                                String errcode = response.getString("errcode");
                                if (response.has("msg")) {
                                    String msg = response.getString("msg");
                                    ApplicationData.showMessage(mActivity,
                                            getResources().getString(R.string.str_title_error),
                                            msg, getResources().getString(R.string.str_ok));
                                    edtEmail.setText("");
                                } /*else {
                                    if (errcode.equals("3")) {
                                        ApplicationData.showMessage(mActivity,
                                                getResources().getString(R.string.str_title_error),
                                                getResources().getString(R.string.msg_content_user_overflowed), getResources().getString(R.string.str_ok));
                                    } else {
                                        ApplicationData.showMessage(mActivity,
                                                getResources().getString(R.string.str_title_error),
                                                getResources().getString(R.string.msg_invalid_email), getResources().getString(R.string.str_ok));

                                        edtEmail.setText("");
                                    }
                                }*/
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_operation_error), true);
            }
        });

        queue.add(jsObjRequest);
    }

    // ....on click on forgot password..//
    public void register(View v) {

//         Intent in = new Intent(LoginActivity.this, NewRegister.class);
//         startActivity(in);
//        finish();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void change_lang() {

        String localeString = "";

        if (myPrefs.getString("language", "nowrgian").equalsIgnoreCase("english")) {
            localeString = "en";
        } else {
            localeString = "no";
        }

        Configuration config = getResources().getConfiguration();

        Locale locale = new Locale(localeString);
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        //    txtDesc.setText(mActivity.getResources().getString(R.string.login_desc));
        txt_next.setText(getResources().getString(R.string.str_login));
        //      textRegister.setText(getResources().getString(R.string.login_newuser_button));

        edtEmail.setHint(getResources().getString(R.string.str_email_address));

        _tv_english.setText(getResources().getString(R.string.str_english));
        _tv_nowrgian.setText(getResources().getString(R.string.str_norwegian));

        if (myPrefs.getString("language", "nowrgian").equalsIgnoreCase("english")) {
           /* _tv_english.setTextColor(getResources().getColor(
                    R.color.color_white));
            _tv_nowrgian.setTextColor(getResources().getColor(
                    R.color.color_blue));*/

            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                _english.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.btn_cmd_p));
                _nowrgian.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.liner_white_border));
            } else {
                _english.setBackground(getResources().getDrawable(
                        R.drawable.btn_cmd_p));
                _nowrgian.setBackground(getResources().getDrawable(
                        R.drawable.liner_white_border));
            }
        } else {
           /* _tv_english.setTextColor(getResources().getColor(
                    R.color.color_blue));
            _tv_nowrgian.setTextColor(getResources().getColor(
                    R.color.color_white));*/

            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                _english.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.liner_white_border));
                _nowrgian.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.btn_cmd_p));
            } else {
                _english.setBackground(getResources().getDrawable(
                        R.drawable.liner_white_border));
                _nowrgian.setBackground(getResources().getDrawable(
                        R.drawable.btn_cmd_p));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        edtEmail=null;
        login_url=null;
        pDialog=null;
        language=null;
        _tv_english=null; _tv_nowrgian=null;
        textlogin=null;
        _english=null;_nowrgian=null;
        myPrefs=null;
        editor=null;
        mActivity=null;
        txt_next=null;

        System.gc();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
