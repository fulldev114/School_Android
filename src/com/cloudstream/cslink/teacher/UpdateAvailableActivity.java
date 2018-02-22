package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cloudstream.cslink.R;
import com.xmpp.teacher.Constant;

public class UpdateAvailableActivity extends Activity {
    Context context = UpdateAvailableActivity.this;
    TextView tvupdateMsg;
    Button btnSkip;
    Button btnUpdate;
    private SharedPreferences preference;
    private String forcefully, isdifferent, message, link, skiptext, updatetext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.adres_activity_update_available);
        getViews();

        preference = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        forcefully = preference.getString(Constant.ForceUpdate, "");
        isdifferent = preference.getString(Constant.IsVersionDifferent, "");
        message = preference.getString(Constant.Update_Message, "");
        link = preference.getString(Constant.URL, "");
        skiptext = preference.getString(Constant.Skipbuttontitle, "");
        updatetext = preference.getString(Constant.Updatebuttontitle, "");


        if (forcefully.equalsIgnoreCase("Yes")) {
            btnSkip.setVisibility(View.GONE);
        }
        if (message != null && !"".equalsIgnoreCase(message)) {
            tvupdateMsg.setText(message);
        }

        if (forcefully != null && forcefully.equalsIgnoreCase("Yes")) {
            btnSkip.setVisibility(View.GONE);
            ApplicationData.updateActivityCall = true;
        }
        if (skiptext != null && !"".equalsIgnoreCase(skiptext)) {
            btnSkip.setText(skiptext);
        }
        if (updatetext != null && !"".equalsIgnoreCase(updatetext)) {
            btnUpdate.setText(updatetext);
        }

        btnSkip.setOnClickListener(btnSkipClickListener);
        btnUpdate.setOnClickListener(btnUpdateClickListener);
    }


    View.OnClickListener btnUpdateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String appVersionName = "";

            SharedPreferences.Editor edit = preference.edit();
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            appVersionName = pInfo.versionName;

            if (appVersionName != null && !appVersionName.equalsIgnoreCase("")) {
                edit.putString(Constant.Current_version_app, appVersionName);
                edit.commit();
            }

            if (link != null && !link.equalsIgnoreCase("")) {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(link));
                startActivity(viewIntent);
                finish();
            }

        }
    };
    View.OnClickListener btnSkipClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void getViews() {

        tvupdateMsg = (TextView) findViewById(R.id.txtmessage);
        btnSkip = (Button) findViewById(R.id.btnskip);
        btnUpdate = (Button) findViewById(R.id.btnupdate1);

    }

}
