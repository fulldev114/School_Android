package com.cloudstream.cslink.parent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.xmpp.parent.Constant;

import org.json.JSONObject;

import java.util.Locale;

public class ParentLoginActivity extends Activity {

    private EditText edtPhone;
    private String login_url;
    MainProgress pDialog;
    // int register = 0;
    String _parent_id, child_array, email_id, password;
    String regid, language;
    //private RelativeLayout lytRegister;
    private String parent_name;
    private TextView textRegister, _tv_english, _tv_nowrgian;//, txtDesc;
    private RelativeLayout textlogin;
    LinearLayout _english, _nowrgian;
    int sdk = android.os.Build.VERSION.SDK_INT;

    SharedPreferences myPrefs;
    SharedPreferences.Editor editor;

    Activity mActivity;
    private TextView txt_next;
    private TextView txt_anaccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_activity);

        mActivity = this;

        myPrefs = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        editor = myPrefs.edit();

        //txtDesc = (TextView) findViewById(R.id.txtDesc);
        _tv_english = (TextView) findViewById(R.id.textView_english);
        _tv_nowrgian = (TextView) findViewById(R.id.textView_nowrgian);
        _english = (LinearLayout) findViewById(R.id.english);
        _nowrgian = (LinearLayout) findViewById(R.id.norwgian);
        txt_anaccount = (TextView) findViewById(R.id.txt_anaccount);
        findview();

        editor.putString("language", "nowrgian");
        editor.commit();
        change_lang();

        txt_anaccount.setText(getString(R.string.account));

        _english.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                // ........................sharedpreferences.......................//
                //SharedPreferences myPrefs = getActivity().getSharedPreferences(
                //	"absentapp", Context.MODE_PRIVATE);
                //SharedPreferences.Editor editor = myPrefs.edit();

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
        // lytRegister = (RelativeLayout) findViewById(R.id.lytRegister);
        textRegister = (TextView) findViewById(R.id.textView1);
        edtPhone = (EditText) findViewById(R.id.et_emailid);


        textRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ParentLoginActivity.this, RegisterMainActivity.class);
                startActivity(in);
            }
        });
    }

    public void login(View v) {

        if (edtPhone.getText().toString().length() > 7) {

            login_url = ApplicationData.getlanguageAndApi(ParentLoginActivity.this, ConstantApi.GET_VERIFY)
                    + "phone="
                    + edtPhone.getText().toString();
            edtPhone.setEnabled(false);
            getverify(login_url);

//            } else {
//                Toast.makeText(this, getResources().getString(R.string.msg_no_password), Toast.LENGTH_LONG).show();
//            }
        } else if (edtPhone.getText().toString().length() > 0) {
            try {
                ApplicationData.showMessage(this,
                        getResources().getString(R.string.str_phone_number),
                        getResources().getString(R.string.msg_invalidphone), getResources().getString(R.string.str_ok));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                ApplicationData.showMessage(this,
                        getResources().getString(R.string.str_phone_number),
                        getResources().getString(R.string.msg_no_phone), getResources().getString(R.string.str_ok));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getverify(String verify_url) {
        if (!GlobalConstrants.isWifiConnected(ParentLoginActivity.this)) {
            return;
        }

        if (pDialog == null)
            pDialog = new MainProgress(this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.show();
        edtPhone.setEnabled(true);
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, verify_url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String flag = response.getString("flag");

                            if (Integer.parseInt(flag) == 1) {
                                String parent_no = response.getString("parent_no");

                                editor.putString("parent_no", parent_no);
                                editor.putString("phone", edtPhone.getText().toString());

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
                                        Intent intent = new Intent(ParentLoginActivity.this, UpdateAvailableActivity.class);
                                        startActivity(intent);

                                    }
                                    else
                                    {
                                        Intent in = new Intent(ParentLoginActivity.this, VerifyActivity.class);
                                        in.putExtra("childArray", child_array);
                                        startActivity(in);

                                        if(isVersionDifferent.equalsIgnoreCase("Yes")) {
                                            Intent in1 = new Intent(ParentLoginActivity.this, UpdateAvailableActivity.class);
                                            startActivity(in1);
                                        }
                                    }
                                    finish();
                                } else {
                                    if (pDialog != null && pDialog.isShowing()) {
                                        pDialog.dismiss();
                                    }
                                    Intent in = new Intent(ParentLoginActivity.this, VerifyActivity.class);
                                    in.putExtra("childArray", child_array);
                                    startActivity(in);
                                    finish();
                                }
                                editor.commit();


                            } else {

                                pDialog.dismiss();
                                String errcode = response.getString("errcode");

                                if (response.has("msg")) {
                                    String msg = response.getString("msg");
                                    ApplicationData.showMessage(mActivity, getResources().getString(R.string.str_title_error), msg, getResources().getString(R.string.str_ok));
                                }

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

                Toast.makeText(ParentLoginActivity.this, getResources().getString(R.string.msg_operation_error),
                        Toast.LENGTH_LONG).show();
            }
        });

        queue.add(jsObjRequest);
    }

    // ....on click on forgot password..//



//        finish();



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

        //  txtDesc.setText(getResources().getString(R.string.login_desc));
        txt_next.setText(getResources().getString(R.string.str_login));
        textRegister.setText(getResources().getString(R.string.register_child_button));

        // edtPhone.setHint(getResources().getString(R.string.str_phone_number));

        _tv_english.setText(getResources().getString(R.string.str_english));
        _tv_nowrgian.setText(getResources().getString(R.string.str_norwegian));
        txt_anaccount.setText(getString(R.string.account));

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
            /*_tv_english.setTextColor(getResources().getColor(
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
    public void onLowMemory() {
        super.onLowMemory();
    }
}
