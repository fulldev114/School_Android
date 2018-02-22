package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudstream.cslink.R;
import com.common.ImageLoader;
import com.common.dialog.MainProgress;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.github.orangegangsters.lollipin.lib.managers.AppLockActivity;
import com.xmpp.teacher.Constant;
import com.xmpp.teacher.XMPPMethod;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

public class Pincode_activity extends AppLockActivity {

    EditText _current_pincode, _new_pincode, _confirm_pincode;

    ImageView _back;

    //LinearLayout send;
    //Button forgotpassword;

    private String url;

    MainProgress pDialog;

    TextView lang_header;//,send_text;

    SharedPreferences sharedpref;

    String _email, language;

    private static int SUCCESS_SEND_PINCODE = 1;
    private static int FAILED_SEND_SMS = 2;
    private static int WRONG_PINCODE = 3;
    ImageLoader imageLoader;

    String noti_type = "";
    int noti_kid_id = 0, noti_from_id = 0;

    Activity mActivity;
    private LayoutInflater inflater;
    private LinearLayout screen;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater = getLayoutInflater();
        screen = (LinearLayout) inflater.inflate(R.layout.adres_enterpin_code, null);
        relwrraper.addView(screen);
        mActivity = this;
        imageLoader = new ImageLoader(this);

        ApplicationData.ispincode = true;

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.setThreadPolicy(policy);
        }

        ApplicationData.initImageLoader(Pincode_activity.this);

        sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        SharedPreferences.Editor editor = sharedpref.edit();
        /*editor.putBoolean("from_pincode", true);
        editor.commit();*/

        SharedPreferences myPrefs_emer = getSharedPreferences(Constant.EMERGENCY_FILENAME, Context.MODE_PRIVATE);
        if (myPrefs_emer.getBoolean("emeregency_popup", false)) {
            Intent in = new Intent(Pincode_activity.this, EmeregencyPopupActivity.class);
            startActivity(in);
            finish();
        }

        String localeString = "no";
        if (sharedpref.getString("language", "nowrgian").equalsIgnoreCase("english")) {
            localeString = "en";
        } else {
            localeString = "no";
        }
        Configuration config = getResources().getConfiguration();
        Locale locale = new Locale(localeString);
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        _email = sharedpref.getString("email", "");

        Intent intent = getIntent();
        if (intent.hasExtra("noti_type")) {        // push noti getted.
            noti_type = intent.getStringExtra("noti_type");
        }
        if (intent.hasExtra("noti_kid_id")) {
            noti_kid_id = intent.getIntExtra("noti_kid_id", 0);
        }
        if (intent.hasExtra("noti_from_id")) {
            noti_from_id = intent.getIntExtra("noti_from_id", 0);
        }
        /*myPrefs = getSharedPreferences("adminapp",
                Context.MODE_PRIVATE);*/
        findview();

        lang_header.setText(getResources().getString(R.string.str_pincode));

      /*  _current_pincode.setHint(getResources().getString(R.string.str_pincode));
        _new_pincode.setHint(getResources().getString(R.string.new_pincode));
        _confirm_pincode.setHint(getResources().getString(R.string.str_pincode));*/
        //send_text.setText(getResources().getString(R.string.str_login));
        //forgotpassword.setText(getResources().getString(R.string.str_forgot_pincode));
    }

    private void findview() {

        lang_header = (TextView) findViewById(R.id.textView1);

        _current_pincode = (EditText) findViewById(R.id.editText_currentpincode);
        _new_pincode = (EditText) findViewById(R.id.editText_newpincode);
        _confirm_pincode = (EditText) findViewById(R.id.editText_confirmpincode);

        //	send_text = (TextView) findViewById(R.id.textView3);

        _back = (ImageView) findViewById(R.id.imgback);
        _back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean val = sharedpref.getBoolean("is_login", false);
                if (val) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else
                    finish();
            }
        });

        //send = (LinearLayout) findViewById(R.id.send);
      /*  send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                send_password(mPinCode);
            }
        });
*/
        //	forgotpassword = (Button) findViewById(R.id.btn_forgotpassword);
        mForgotTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                forgot_password();
            }
        });
    }


    private void getMainActivity(String url2) {
        // TODO Auto-generated method stub
        if (!GlobalConstrants.isWifiConnected(Pincode_activity.this)) {
            return;
        }

        if (pDialog == null)
            pDialog = new MainProgress(this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.dismiss();
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            String badge = "", chat_badge = "", internal_badge = "", register_badge = "";
                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 1) {

                                //_confirm_pincode.setText("");

                                String teacher_id = response.getString("teacherid");
                                String school_id = response.getString("schoolid");
                                String teacher_name = response.getString("teachername");
                                String image = response.getString("image");
                                if (response.has("psb"))
                                    badge = response.getString("psb");
                                if (response.has("chb"))
                                    chat_badge = response.getString("chb");
                                if (response.has("inb"))
                                    internal_badge = response.getString("inb");
                                if (response.has("rgb"))
                                    register_badge = response.getString("rgb");

                                SharedPreferences myPrefs = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = myPrefs.edit();
                                editor.putString("teacher_id", teacher_id);
                                editor.putString("school_id", school_id);
                                editor.putString("teacher_name", teacher_name);
                                editor.putString("image", image);
                                editor.putString("notification", "0");
                                editor.putBoolean("is_login", true);
                                editor.putString("psbadge", badge);
                                editor.putString("chat_badge", chat_badge);
                                editor.putString("internal_badge", internal_badge);

                                if (!myPrefs.getString("old_register_badge", "0").equalsIgnoreCase("0")) {
                                    register_badge = String.valueOf(Integer.parseInt(register_badge) - Integer.parseInt(myPrefs.getString("old_register_badge", "0")));
                                }
                                editor.putString("register_badge", register_badge);
                                editor.putString("noofrecord", response.getString("no of records"));
                                editor.commit();

                                if (response.has("update_info")) {
                                    JSONObject job = response.getJSONObject("update_info");
                                    if (job.has("Android")) {
                                        JSONObject jandroid = job.getJSONObject("Android");

                                        String forceUpdateApp = jandroid.getString("forceUpdateApp");
                                        String isVersionDifferent = jandroid.getString("isVersionDifferent");
                                        String MessageType = jandroid.getString("MessageType");
                                        String URL = jandroid.getString("URL");
                                        String skipbuttontitle = jandroid.getString("skipbuttontitle");
                                        String updatebuttontitle = jandroid.getString("updatebuttontitle");

                                        SharedPreferences.Editor edit = myPrefs.edit();
                                        edit.putString(Constant.ForceUpdate, forceUpdateApp);
                                        edit.putString(Constant.IsVersionDifferent, isVersionDifferent);
                                        edit.putString(Constant.Update_Message, MessageType);
                                        edit.putString(Constant.URL, URL);
                                        edit.putString(Constant.Skipbuttontitle, skipbuttontitle);
                                        edit.putString(Constant.Updatebuttontitle, updatebuttontitle);
                                        edit.commit();
                                        if (pDialog != null && pDialog.isShowing()) {
                                            pDialog.dismiss();
                                        }
                                        if (forceUpdateApp.equalsIgnoreCase("Yes")) {
                                            Intent intent = new Intent(Pincode_activity.this, UpdateAvailableActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            new chatclass().execute();
                                            onPinCodeSuccess();
                                        }
                                    }
                                }
                                else
                                {
                                    new chatclass().execute();
                                    onPinCodeSuccess();
                                }
                            } else {
                                pDialog.dismiss();
                                showMessage(WRONG_PINCODE);
                                setPinCode("");
                                mType = AppLock.ENABLE_PINLOCK;
                                setStepText();
                                onPinCodeError();
                            }
                        } catch (Exception e) {
                            ApplicationData.showToast(Pincode_activity.this, getResources().getString(R.string.msg_operation_error), true);
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                ApplicationData.showToast(Pincode_activity.this, getResources().getString(R.string.msg_operation_error), true);
            }
        });
        queue.add(jsObjRequest);
    }

    protected void forgot_password() {
        // TODO Auto-generated method stub
        url = ApplicationData.getlanguageAndApi(Pincode_activity.this, ConstantApi.FORGOT_PINCODE_EMAIL)
                + "email=" + _email;
        forgot_password_req(url);
    }

    private void forgot_password_req(String url2) {
        // TODO Auto-generated method stub
        if (!GlobalConstrants.isWifiConnected(this)) {
            mForgotTextView.setEnabled(true);
            return;
        }
        if (pDialog == null)
            pDialog = new MainProgress(this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.dismiss();
        pDialog.show();


        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url2, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO Auto-generated method stub

                        try {

                            String flag = response.getString("flag");

                            if (Integer.parseInt(flag) == 1) {
                                pDialog.dismiss();
                                showMessage(SUCCESS_SEND_PINCODE);
                            } else {
                                pDialog.dismiss();
                                showMessage(WRONG_PINCODE);
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

                Toast.makeText(Pincode_activity.this, getResources().getString(R.string.msg_operation_error),
                        Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);

    }

    private void showMessage(final int res) throws Exception {
        //custom dialog

        final Dialog dlg = new Dialog(this);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.msgdialog);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView title = (TextView) dlg.findViewById(R.id.msgtitle);
        TextView content = (TextView) dlg.findViewById(R.id.msgcontent);
        if (res == SUCCESS_SEND_PINCODE) {
            title.setText(R.string.str_success);
            content.setText(getResources().getString(R.string.success_send_pincode));
        } else if (res == FAILED_SEND_SMS) {
            title.setText(R.string.str_alert);
            content.setText(getResources().getString(R.string.msg_not_register_email));
        } else if (res == WRONG_PINCODE) {
            title.setText(R.string.str_alert);
            content.setText(getResources().getString(R.string.msg_wrong_pincode));
        }

        Button dlg_btn_ok = (Button) dlg.findViewById(R.id.btn_ok);
        dlg_btn_ok.setText(getString(R.string.str_ok));
        dlg_btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void showForgotDialog() {

    }

    @Override
    public void send_password() {
        if (!GlobalConstrants.isWifiConnected(Pincode_activity.this)) {
            return;
        }
        if ((mPinCode.length() > 0)) {
            url = ApplicationData.getlanguageAndApi(Pincode_activity.this, ConstantApi.TEACHER_LOGIN_EMAIL)
                    + "email=" + _email
                    + "&code=" + mPinCode.toString();


            getMainActivity(url);
        } /*else {
            ApplicationData.showToast(this, getResources().getString(R.string.msg_no_pincode), true);
        }*/
    }

    @Override
    public void onPinFailure(int attempts) {

    }

    @Override
    public void onPinSuccess(int attempts) {

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        setStepText();
        mForgotTextView.setText(getResources().getString(R.string.pin_code_forgot_text));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    private class chatclass extends AsyncTask<Void, Void, Void> {
        int position;
        SharedPreferences myPrefs = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        MainProgress pdialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!GlobalConstrants.isWifiConnected(Pincode_activity.this)) {
                return;
            }
            if (pdialog == null) {
                pdialog = new MainProgress(Pincode_activity.this);
                pdialog.setCancelable(false);
                pdialog.setMessage(getResources().getString(R.string.str_wait));
                pdialog.dismiss();
                pdialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (myPrefs.getString("jid", "") != null && myPrefs.getString("jid", "").length() > 0) {
                if (myPrefs.getString("jid", "").substring(0, myPrefs.getString("jid", "").lastIndexOf("@")).length() > 0) {
                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.putString("offlinetag", "0");
                    editor.commit();
                    try {
                        XMPPMethod.connect(Pincode_activity.this, myPrefs.getString("jid", ""), "5222", myPrefs.getString("jid_pwd", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pdialog != null) {
                pdialog.dismiss();
            }

            Intent in = new Intent(Pincode_activity.this, MainActivity.class);
            String count1 = myPrefs.getString("noofrecord", "");

            if ((Calendar.getInstance().get(Calendar.MONTH) > 1 || Calendar.getInstance().get(Calendar.DAY_OF_MONTH) > 20) && count1.equals("0")) {
                int allow = 1 / Integer.valueOf(count1);
                in.putExtra("allow", allow);
            }
            in.putExtra("noti_kid_id", noti_kid_id);
            in.putExtra("noti_type", noti_type);
            in.putExtra("noti_from_id", noti_from_id);
            startActivity(in);


            ApplicationData.showAppBadge(Pincode_activity.this, 0);
            //ShortcutBadger.removeCount(Pincode_activity.this);
            if (myPrefs.getString(Constant.IsVersionDifferent, "").equalsIgnoreCase("Yes")) {
                Intent intent = new Intent(Pincode_activity.this, UpdateAvailableActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}


