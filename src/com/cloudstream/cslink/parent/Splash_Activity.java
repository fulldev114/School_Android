package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.cloudstream.cslink.R;
import com.cloudstream.cslink.StartActivity;
import com.xmpp.parent.Constant;

public class Splash_Activity extends Activity {
    public static final int SPLASHTIME = 1000; // 3000
    private static final int STOPSPLASH = 0;


    //private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private Handler uiHandler = null;

    String noti_type = "";
    int noti_kidid = 0, noti_from_id = 0;
    Context context;
    SharedPreferences myPrefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_screen);
        context = this;
        //pref = new Prefrence_sms_video(getApplicationContext());
        Message msg = new Message();
        //Shared = new Prefrence(getApplicationContext());
        //	shared = new Prefrence(context);
        myPrefs = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        editor = myPrefs.edit();

        msg.what = STOPSPLASH;

        Intent intent = getIntent();
        if (intent.hasExtra("noti_type")) {        // push noti getted.
            noti_type = intent.getStringExtra("noti_type");
        }
        if (intent.hasExtra("noti_kidid")) {
            noti_kidid = intent.getIntExtra("noti_kidid", 0);
        }
        if (intent.hasExtra("noti_from_id")) {
            noti_from_id = intent.getIntExtra("noti_from_id", 0);
        }

        if(myPrefs.getString("childid", "")==null || myPrefs.getString("childid","").length()==0||myPrefs.getString("childid","").equalsIgnoreCase("null"))
        {
            ApplicationData.showAppBadge(Splash_Activity.this,0);
        }
        splashHandler.sendMessageDelayed(msg, SPLASHTIME);
    }

    private final Handler splashHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            Intent i = new Intent();

            if (myPrefs.getBoolean("is_login", false)) {
                i.setClass(getApplicationContext(), Pincode_activity.class);
                i.putExtra("noti_kidid", noti_kidid);
                i.putExtra("noti_type", noti_type);
                i.putExtra("noti_from_id", noti_from_id);

            } else {
                i.setClass(getApplicationContext(), StartActivity.class);
            }
            startActivity(i);

            if (myPrefs.getString(Constant.Current_version_app, "").equalsIgnoreCase(pInfo.versionName)) {
                if (myPrefs.getString(Constant.ForceUpdate, "").equalsIgnoreCase("Yes") || myPrefs.getString(Constant.IsVersionDifferent, "").equalsIgnoreCase("Yes")) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), UpdateAvailableActivity.class);
                    startActivity(intent);
                }
            }

            finish();
            /*if (Shared.is_scdc())
            {
				if(pref.get_setting_onoff())
				{
					startActivity(new Intent(Splash_activity.this, Start_app.class));
					finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
				}
				else
				{
					startActivity(new Intent(Splash_activity.this, Main_activity.class));
					finish();
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
				}
			}
			else
			{
				startActivity(new Intent(Splash_activity.this, Swip_acdc.class));
				finish();
				overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			}*/


        }
    };

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
