package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudstream.cslink.R;
import com.xmpp.teacher.Constant;

/**
 * Created by etech on 27/7/16.
 */
public class EmeregencyPopupActivity extends Activity implements View.OnClickListener {

    private TextView txt_emgncy_title, txt_summary, txt_msg;
    private LinearLayout lin_cancel;
    private String message;
    private boolean flag = false;
    private SharedPreferences mpreference;
    private Button btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.adres_emergency_popup_screen);

        mpreference = getSharedPreferences(Constant.EMERGENCY_FILENAME, Context.MODE_PRIVATE);
        message = mpreference.getString("eme_message", "");
        flag = mpreference.getBoolean("emeregency_popup", false);

        initcomponent();

    }

    private void initcomponent() {
        txt_emgncy_title = (TextView) findViewById(R.id.txt_emgncy_title);
        txt_summary = (TextView) findViewById(R.id.txt_summary);
        txt_msg = (TextView) findViewById(R.id.txt_msg);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(EmeregencyPopupActivity.this);

        if (flag)
            txt_summary.setText(message);
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = mpreference.edit();
        editor.putBoolean("emeregency_popup",false);
        editor.putString("eme_message", "");
        editor.commit();

        SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        int Activityflag = sharedpref.getInt("from_pincode", 0);

        if (Activityflag == 2)
        {
            Intent i = new Intent(EmeregencyPopupActivity.this,PasswordActivity.class);
            startActivity(i);
        }

       finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        txt_emgncy_title=null;
        txt_summary=null; txt_msg=null;
        lin_cancel=null;
        message=null;
        mpreference=null;
        btn_cancel=null;

        System.gc();
    }
}
