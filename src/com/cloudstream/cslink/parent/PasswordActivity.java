package com.cloudstream.cslink.parent;

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
import com.common.dialog.MainProgress;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.github.orangegangsters.lollipin.lib.managers.AppLockActivity;
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;
import com.xmpp.parent.Constant;
import com.xmpp.parent.XMPPMethod;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

public class PasswordActivity extends AppLockActivity implements AsyncTaskCompleteListener<String> {

    EditText _confirm_pincode;

    LinearLayout send;
    Button forgotpassword;

    private String url, pincode;

    MainProgress pDialog;

    SharedPreferences sharedpref;

    private static int SUCCESS_SEND_PINCODE = 1;
    private static int FAILED_SEND_SMS = 2;
    private static int WRONG_PINCODE = 3;
    private static final int BLOCKED_PHONE = 4;

    private Activity mActivity;
    private View btnBack;
    private ImageView _back;
    private LayoutInflater inflater;
    private LinearLayout screen;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        inflater = getLayoutInflater();
        screen = (LinearLayout) inflater.inflate(R.layout.enterpin_code, null);
        relwrraper.addView(screen);
        mActivity = this;
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.setThreadPolicy(policy);
        }

        SharedPreferences myPrefs_emer = getSharedPreferences(Constant.EMERGENCY_FILENAME, Context.MODE_PRIVATE);
        sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);

        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putInt("from_pincode", 2);
        editor.commit();

        if(sharedpref.getString("childid", "")==null || sharedpref.getString("childid","").length()==0||sharedpref.getString("childid","").equalsIgnoreCase("null"))
        {
            ApplicationData.showAppBadge(PasswordActivity.this,0);
        }

        if (myPrefs_emer.getBoolean("emeregency_popup", false)) {

            Intent in = new Intent(PasswordActivity.this, EmeregencyPopupActivity.class);
            startActivity(in);
            finish();
        }


        pincode = sharedpref.getString("pincode", "");
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

        findview();

    }

    private void findview() {


        _back = (ImageView) findViewById(R.id.imgback);
        _back.setVisibility(View.GONE);

        //	forgotpassword = (Button) findViewById(R.id.btn_forgotpassword);
        mForgotTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                forgot_password();
            }
        });
    }


    @Override
    public void onPinFailure(int attempts) {

    }

    @Override
    public void onPinSuccess(int attempts) {

    }

    private void getChildList(String url2) {
        // TODO Auto-generated method stub
        if (!GlobalConstrants.isWifiConnected(this)) {
            return;
        }

        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("phone", sharedpref.getString("phone", ""));
            params.put("pincode", mPinCode);
            params.put("parent_no", sharedpref.getString("parent_no", ""));
            params.put("app_version", ApplicationData.getAppVersion(PasswordActivity.this));
            params.put("os", "android");
            params.put("os", "android");
            ETechAsyncTask task = new ETechAsyncTask(PasswordActivity.this, this, ConstantApi.GET_PINCODE_LOGIN, params);
            task.execute(ApplicationData.main_url + ConstantApi.GET_PINCODE_LOGIN + ".php?");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void forgot_password() {
        // TODO Auto-generated method stub

        url = ApplicationData.getlanguageAndApi(PasswordActivity.this, ConstantApi.FORGOT_PINCODE_SMS)
                + "phone=" + sharedpref.getString("phone", "")+"&parent_no="+sharedpref.getString("parent_no","");
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
        pDialog.setMessage(mActivity.getResources().getString(R.string.str_wait));
        pDialog.dismiss();
        pDialog.show();

        mForgotTextView.setEnabled(true);

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

                ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_operation_error), false);
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
            content.setText(mActivity.getResources().getString(R.string.success_send_pincode));
        } else if (res == FAILED_SEND_SMS) {
            title.setText(R.string.str_alert);
            content.setText(mActivity.getResources().getString(R.string.msg_not_register_phone));
        } else if (res == WRONG_PINCODE) {
            title.setText(R.string.str_alert);
            content.setText(mActivity.getResources().getString(R.string.msg_wrong_pincode));
        } else if (res == BLOCKED_PHONE) {
            title.setText(R.string.str_alert);
            content.setText(mActivity.getResources().getString(R.string.msg_blocked_phone, sharedpref.getString("phone", "")));
        }

        Button dlg_btn_ok = (Button) dlg.findViewById(R.id.btn_ok);
        dlg_btn_ok.setText(getString(R.string.str_ok));

        dlg_btn_ok.setOnClickListener(new OnClickListener() {

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

        if (!GlobalConstrants.isWifiConnected(PasswordActivity.this)) {
            return;
        }
        if (mPinCode.length() > 0) {
            url = ApplicationData.getlanguageAndApi(PasswordActivity.this, ConstantApi.GET_PINCODE_LOGIN)
                    + "phone=" + sharedpref.getString("phone", "")
                    + "&pincode=" + mPinCode + "&parent_no=" + sharedpref.getString("parent_no", "");

            getChildList(url);

        } /*else {
            ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_no_pincode), false);
        }*/
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        ApplicationData.setMainActivity(mActivity);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    @Override
    public void onTaskComplete(int statusCode, String responseMsg, String webserviceCb, Object tag) {
        if (statusCode == ETechAsyncTask.COMPLETED) {
            try {
                JSONObject jObject = new JSONObject(responseMsg);
                String resMsg = jObject.getString(ConstantApi.REQ_RESPONSE_MSG);


                if (webserviceCb.equalsIgnoreCase(ConstantApi.GET_PINCODE_LOGIN)) {
                    String flag = jObject.getString("flag");

                    if (Integer.parseInt(flag) == 1) {
                        String child_array = "";
                        String parent_id = jObject.getString("parentid");
                        String parent_no = jObject.getString("parent_no");
                        String parent_status = jObject.getString("parent_status");

                        JSONObject job = jObject.getJSONObject("update_info");
                        if (job.has("Android")) {
                            JSONObject jandroid = job.getJSONObject("Android");

                            String forceUpdateApp = jandroid.getString("forceUpdateApp");
                            String isVersionDifferent = jandroid.getString("isVersionDifferent");
                            String MessageType = jandroid.getString("MessageType");
                            String URL = jandroid.getString("URL");
                            String skipbuttontitle = jandroid.getString("skipbuttontitle");
                            String updatebuttontitle = jandroid.getString("updatebuttontitle");

                            SharedPreferences.Editor editor = sharedpref.edit();
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
                                Intent intent = new Intent(PasswordActivity.this, UpdateAvailableActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                if (parent_status != null && (parent_status.equals("1") || parent_status.equals("2"))) {
                                    onPinCodeSuccess();
                                    new ChatClass().execute();

                                } else {
                                    showMessage(BLOCKED_PHONE);
                                }
                            }
                        }
                    } else {
                        String msg = jObject.getString("msg");
                        onPinCodeError();
                        ApplicationData.showMessage(PasswordActivity.this, "", msg, getString(R.string.str_ok));
                        setPinCode("");
                        mType = AppLock.ENABLE_PINLOCK;
                        setStepText();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ApplicationData.showToast(PasswordActivity.this, getResources().getString(R.string.msg_operation_error), true);
            }
        } else if (statusCode == ETechAsyncTask.ERROR_NETWORK) {
            try {
                ApplicationData.showToast(PasswordActivity.this, R.string.msg_operation_error, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ChatClass extends AsyncTask<Void, Void, Void> {
        int position;
        SharedPreferences myPrefs = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        MainProgress pdialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pdialog == null) {
                pdialog = new MainProgress(PasswordActivity.this);
                pdialog.setCancelable(false);
                pdialog.setMessage(getResources().getString(R.string.str_wait));
                pdialog.dismiss();
                pdialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (myPrefs.getString("jid", "") != null && myPrefs.getString("jid", "").length() > 0) {
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.putString("offlinetag", "0");
                editor.commit();
                try {
                    XMPPMethod.connect(PasswordActivity.this, myPrefs.getString("jid", ""), "5222", myPrefs.getString("jid_pwd", ""));
                } catch (Exception e) {
                    e.printStackTrace();
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

            ApplicationData.showAppBadge(PasswordActivity.this, 0);
            // ShortcutBadger.removeCount(PasswordActivity.this);
            //   startService(new Intent(PasswordActivity.this, MessageService.class));
            if (myPrefs.getString(Constant.IsVersionDifferent, "").equalsIgnoreCase("Yes")) {
                Intent intent = new Intent(PasswordActivity.this, UpdateAvailableActivity.class);
                startActivity(intent);
            }
            finish();

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}


