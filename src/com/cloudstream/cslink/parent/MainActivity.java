package com.cloudstream.cslink.parent;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.parent.Childbeans;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudstream.cslink.R;
import com.common.SharedPreferFile;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.common.view.CircularImageView;
import com.db.parent.DatabaseHelper;
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;
import com.service.parent.NotificationJobService;
import com.xmpp.parent.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends Activity implements AsyncTaskCompleteListener<String> {

    LinearLayout menuBar, btnAbsentNotice, btnUpdateProfile, lin_emergency;
    private DrawerLayout mDrawerLayout;
    ListView list;
    String[] Drawerlist_item;
    int fragment;
    Integer[] Drawerlist_image = {R.drawable.img_home, R.drawable.img_profile,
            R.drawable.img_message,
            /*R.drawable.register_ic,*/ R.drawable.img_student, R.drawable.img_report, R.drawable.img_card,
            R.drawable.img_statics, R.drawable.img_parent, R.drawable.img_contact, R.drawable.img_setting, R.drawable.img_logout};
//, R.drawable.img_report, R.drawable.img_card

    Integer[] Drawerlist_badge = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public TextView title;
    private ActionBarDrawerToggle mDrawerToggle;

    boolean b = false;

    String _child_name, _child_id, _child_array, _school_id, language, image, parent_no, parent_status;

    String noti_type = "";
    int noti_kidid = 0, noti_from_id = 0;

    CircularImageView imageView;
    Activity mActivity;

    private FramentProfile fragmentprofile = null;
    private LinearLayout lin_menu;
    private AdapterHomeScreen adap;
    private Childbeans data;
    private int index = 0;
    private static ParentHomeActivity fragmenthome;
    private String badge = "0";
    UpdaterBroadcastReceiver updateBroadcaseReceiver = null;
    private TextView textName;
    public static int kJobId = 1;
    private JobScheduler jobScheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivity);


        Intent intent = getIntent();
        mActivity = this;

        fragment = intent.getIntExtra("fragment", 0);

        SharedPreferences sharedpref = getSharedPreferences("absentapp", 0);
        _child_array = sharedpref.getString("child_array", "");
        _child_id = sharedpref.getString("childid", "");
        language = sharedpref.getString("language", "");
        _child_name = sharedpref.getString("childname", "");
        _school_id = sharedpref.getString("school_id", "");
        image = sharedpref.getString("image", "");
        parent_no = sharedpref.getString("parent_no", "");
        parent_status = sharedpref.getString("parent_status", "");

        b = true;

        //fetch badge from database
        getbadgedb();

        list = (ListView) findViewById(R.id.list_slidermenu);
        lin_menu = (LinearLayout) findViewById(R.id.lin_menu);

       /* LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.header, list, false);*/
        textName = (TextView) findViewById(R.id.textName);
        textName.setText(_child_name);
        imageView = (CircularImageView) findViewById(R.id.imgProfile);
        /*TextView textParent = (TextView) header.findViewById(R.id.txtParent);
        textParent.setText(getResources().getString(R.string.str_parent) + parent_no);*/
        ApplicationData.setProfileImg(mActivity, ApplicationData.web_server_url + ApplicationData.child_image_path + image, imageView);

        //  list.addHeaderView(header, null, false);
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            setBackgroundService();
        }


        //remove notification from notification bar
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();

        //connectivity of user with xmpp
        SharedPreferFile user = new SharedPreferFile(MainActivity.this);
        // XMPPMethod.connect(MainActivity.this,user.getJidUser(),"5222",user.getJidPassword());
        setActionBarStyle();
        menuBar = (LinearLayout) findViewById(R.id.btnbck);
        btnAbsentNotice = (LinearLayout) findViewById(R.id.btnAbsentNotice);
        btnUpdateProfile = (LinearLayout) findViewById(R.id.btnUpdateProfile);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        lin_emergency = (LinearLayout) findViewById(R.id.lin_emergency);


        int width1 = (int) ((float) (getResources().getDisplayMetrics().widthPixels / 1.2));
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) lin_menu.getLayoutParams();
        params.width = width1;
        lin_menu.setLayoutParams(params);


        Drawerlist_item = getResources().getStringArray(R.array.drawer_item);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        mDrawerLayout.requestDisallowInterceptTouchEvent(true);

        // .................get string array from sring.xml..........//
        //   loaddrawer();
        menuBar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawers();
                    btnUpdateProfile.setEnabled(true);
                } else if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {

                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
                    language = sharedpref.getString("language", "");
                    _child_name = sharedpref.getString("childname", "");
                    image = sharedpref.getString("image", "");
                    textName.setText(_child_name);
                    ApplicationData.setProfileImg(mActivity, ApplicationData.web_server_url + ApplicationData.child_image_path + image, imageView);

                    loaddrawer();
                    drawMenu();

                    btnUpdateProfile.setEnabled(false);
                    mDrawerLayout.openDrawer(lin_menu);
                    //btnUpdateProfile.setEnabled(false);
                }
            }
        });
        btnAbsentNotice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callAction(4);
            }
        });
        btnUpdateProfile.setVisibility(View.GONE);
        lin_emergency.setVisibility(View.GONE);
        btnUpdateProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentprofile != null)
                    fragmentprofile.editprofile();
            }
        });
        list.setOnItemClickListener(new OnItemClickListener() {
            public View row;

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                final int pos = position;

                if (row != null) {
                    // row.setBackgroundColor(Color.GRAY);
                }
                row = view;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        callAction(pos); // your fragment transactions go here
                    }
                }, 200);
                mDrawerLayout.closeDrawers();
                list.setSelected(true);
                index = position;
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        callAction(0);
        list.setItemChecked(0, true);

        IntentFilter filter = new IntentFilter(ApplicationData.BROADCAST_CHAT);
        updateBroadcaseReceiver = new UpdaterBroadcastReceiver();
        registerReceiver(updateBroadcaseReceiver, filter);
        ApplicationData.setMainActivity(mActivity);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setBackgroundService() {
        ComponentName mServiceComponent = new ComponentName(this, NotificationJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(kJobId, mServiceComponent);
        builder.setMinimumLatency(3 * 1000); // wait at least
        builder.setOverrideDeadline(30 * 1000); // maximum delay
        builder.setPersisted(true);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        builder.setRequiresDeviceIdle(true); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        jobScheduler = (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    private void getbadgedb() {
        DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(this);
        String query = "select * from badge_table where KidId=" + '"' + _child_id + '"';// + " AND MessgeType = 'abn'";
        HashMap<String, String> messagelist = db.selSingleRecordFromDB(query, null);
        if (messagelist != null) {
            // if (messagelist.get("MessgeType").equalsIgnoreCase("abn"))
            {
                badge = messagelist.get("Abn");
            }
            Drawerlist_badge[2] = Integer.parseInt(badge);
        }
    }


    public void setActionBarTitle(String tit) {
        title.setText(tit);
    }

    public CircularImageView getImageProfileView() {
        return imageView;
    }

    private void drawMenu() {
        // TODO Auto-generated method stub
        getbadgedb();

        SharedPreferences sharedpref = getSharedPreferences("absentapp", 0);
        image = sharedpref.getString("image", "");
        ApplicationData.setProfileImg(mActivity, ApplicationData.web_server_url + ApplicationData.child_image_path + image, imageView);

        language = sharedpref.getString("language", "");
        String localeString;
        if (language.equalsIgnoreCase("english")) {
            localeString = "en";
        } else {
            localeString = "no";
        }

        Configuration config = getResources().getConfiguration();
        Locale locale = new Locale(localeString);
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        Drawerlist_item = getResources().getStringArray(R.array.drawer_item);
        adap = new AdapterHomeScreen(getApplicationContext(),
                Drawerlist_item, Drawerlist_image, Drawerlist_badge);
        list.setAdapter(adap);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_launcher, R.string.app_name, R.string.app_name) {
            /** Called when drawer is closed */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle("TAG PHOTO");
                invalidateOptionsMenu();
            }

            /** Called when a drawer is opened */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("TAG PHOTO");
                invalidateOptionsMenu();
            }
        };

        list.setItemChecked(index, true);

    }

    private void loaddrawer() {
        if (!GlobalConstrants.isWifiConnected(mActivity)) {
            return;
        }

        String url = ApplicationData.getlanguageAndApi(MainActivity.this, ConstantApi.GET_CHILD_BADGE) + "user_id=" + _child_id;
        RequestQueue queue = Volley.newRequestQueue(mActivity);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 1) {
                                Drawerlist_badge[2] = Integer.valueOf(response.getString("abn_badge"));
                            } else {
                                Drawerlist_badge[1] = 0;
                                Drawerlist_badge[2] = 0;
                                Drawerlist_badge[3] = 0;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Drawerlist_badge[1] = 0;
                            Drawerlist_badge[2] = 0;
                            Drawerlist_badge[3] = 0;
                        }
                        drawMenu();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ApplicationData.showToast(mActivity, getResources().getString(R.string.str_network_error), false);
                Drawerlist_badge[1] = 0;
                Drawerlist_badge[2] = 0;
                Drawerlist_badge[3] = 0;
                drawMenu();
            }
        });

        queue.add(jsObjRequest);
    }

    public void setActionBarStyle() {

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        View cView = getLayoutInflater().inflate(R.layout.title, null);

        actionBar.setCustomView(cView);
        actionBar.setDisplayShowHomeEnabled(false);

        title = (TextView) cView.findViewById(R.id.textView1);
        title.setText(this.getResources().getString(R.string.str_message));

    }

    public void callAction(int pos) {
        btnUpdateProfile.setEnabled(true);

        Fragment fragment = null;
        fragmentprofile = null;
        fragmenthome = null;
        Bundle bundle = new Bundle();
        ApplicationData.ishomebuttonview = false;

        if (pos > -1 && pos < 10) {  // set title
            title.setText(getResources().getStringArray(R.array.drawer_item)[pos]);
        }

        btnUpdateProfile.setVisibility(View.GONE);
        lin_emergency.setVisibility(View.GONE);
        title.setTextColor(Color.parseColor("#FFFFFF"));

        switch (pos) {

            case 0:
                fragmenthome = new ParentHomeActivity();
                lin_emergency.setVisibility(View.VISIBLE);
                break;
            case 1:
                btnUpdateProfile.setVisibility(View.VISIBLE);
                break;

            case 2:
                fragment = new FragmentImportantMessage();
                ((FragmentImportantMessage) fragment).setAbsent(true);
                Drawerlist_badge[2] = 0;
                DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(this);
                db.clearbadge("abn", _child_id);

                break;
            case 3:
                fragment = new FragmentSelectKid();
                break;
           /* case 3:
                fragment = new FragmentImportantMessage();
                ApplicationData.showAppBadgeDec(mActivity, Drawerlist_badge[3]);
                break;*/
            case 4:
                fragment = new DisciplineBehaveRepotActivity();
                break;
           /* case 6:
                fragment = new FragmentAbsent();
                break;*/
            case 5:
                fragment = new ReportCardActivity();
                break;
            case 6:
                fragment = new FragmentStatistics();
                break;
            case 7:
                fragment = new FragmentOtherParent();
                break;
            case 8:
                fragment = new FragmentContactUs();
                break;
            case 9:
                fragment = new FragmentSetting();
                break;
            case 10:
                // logout();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            mDrawerLayout.closeDrawer(lin_menu);
        } else if (pos == 1) {
            btnUpdateProfile.setEnabled(true);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("userid", _child_id);
            map.put("os", "android");
            ETechAsyncTask task = new ETechAsyncTask(MainActivity.this, this, ConstantApi.GET_PROFILE, map);
            task.execute(ApplicationData.main_url + ConstantApi.GET_PROFILE + ".php?");
            /*ProfileTask task = new ProfileTask(MainActivity.this,fragmentprofile,_teacher_id,
                    _teacher_name,mDrawerLayout,lin_menu,fragmentManager);
            task.execute();*/

        } else if (pos == 0) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragmenthome).commit();

            mDrawerLayout.closeDrawer(lin_menu);
        }
        /*else {
            ApplicationData.doLogout(this);
        }*/
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //   ApplicationData.setMainActivity(mActivity);
      /*  Background_work.set_background_time();
        b = false;*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        //   ApplicationData.setMainActivity(null);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        ApplicationData.setMainActivity(mActivity);
        ApplicationData.isrunning = true;

        SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        language = sharedpref.getString("language", "");
        String localeString;
        if (language.equalsIgnoreCase("english")) {
            localeString = "en";
        } else {
            localeString = "no";
        }

        Configuration config = getResources().getConfiguration();
        Locale locale = new Locale(localeString);
        config.locale = locale;
        locale.setDefault(locale);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        if (fragmenthome != null) {
            fragmenthome.setbadge();
        }

        list.setAdapter(adap);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationData.setMainActivity(null);
        //  stopService(new Intent(MainActivity.this, MessageService.class));
        unregisterReceiver(updateBroadcaseReceiver);

        menuBar=null; btnAbsentNotice=null; btnUpdateProfile=null; lin_emergency=null;
        mDrawerLayout=null;
        list=null;
        Drawerlist_item=null;
        Drawerlist_image = null;

        Drawerlist_badge = null;
        title=null;
        mDrawerToggle=null;
        _child_name=null; _child_id=null; _child_array=null;
        _school_id=null; language=null; image=null; parent_no=null; parent_status=null;
        noti_type =null;
        imageView=null;
        mActivity=null;
        fragmentprofile = null;
        lin_menu=null;
        adap=null;
        data=null;
        badge = null;
        updateBroadcaseReceiver = null;
        textName=null;
        jobScheduler=null;

        System.gc();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {


        if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.EDGE_LEFT) {
            if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(e);
    }

    @Override
    public void onTaskComplete(int statusCode, String responseMsg, String webserviceCb, Object tag) {
        try {

            if (statusCode == ETechAsyncTask.COMPLETED) {
                JSONObject jObject = new JSONObject(responseMsg);

                try {

                    if (webserviceCb.equalsIgnoreCase(ConstantApi.GET_PROFILE)) {
                        String flag = jObject.getString("flag");

                        if (Integer.parseInt(flag) == 1) {
                            JSONObject allchilds = jObject.getJSONObject("Child");

                            JSONArray details = allchilds.getJSONArray("childs");
                            for (int i = 0; i < details.length(); i++) {
                                JSONObject c = details.getJSONObject(i);
                                data = new Childbeans();
                                data.user_id = c.getString("user_id");
                                data.username = c.getString("username");
                                data.parent_name = c.getString("parent1name");
                                data.parent2_name = c.getString("parent2name");
                                data.parent1_phone = c.getString("parent1phone");
                                data.parent2_phone = c.getString("parent2phone");
                                data.parent1email = c.getString("parent1email");
                                data.parent2email = c.getString("parent2email");
                                data.class_name = c.getString("class_name");
                                data.incharger = c.getString("teachername");
                                data.school_id = c.getString("school_id");
                                data.school_name = c.getString("school_name");
                                //data.nc_mobile = c.getString("mobile");
                                data.contactmobile = c.getString("parent3mobile");
                                data.contactname = c.getString("parent3name");
                                data.status1 = c.getString("status1");
                                data.status2 = c.getString("status2");
                                data.status3 = c.getString("status3");
                                data.image = c.getString("image");

                                if (_child_id.equalsIgnoreCase(data.user_id)) {
                                    SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
                                    SharedPreferences.Editor edit = sharedpref.edit();
                                    edit.putString("image", data.image);
                                    ApplicationData.setProfileImg(mActivity, ApplicationData.web_server_url + ApplicationData.child_image_path + image, imageView);
                                    edit.commit();
                                }
                            }

                        } else if (flag.equalsIgnoreCase("0")) {
                            String msg = jObject.getString("msg");
                            ApplicationData.showToast(MainActivity.this, msg, false);
                        } else {
                            ApplicationData.showToast(MainActivity.this, getResources().getString(R.string.msg_operation_error), false);
                        }
                    }
                } catch (Exception e) {
                    Log.e("OfferCategory", "onTaskComplete() " + e, e);
                }

                Bundle bundle = new Bundle();
                bundle.putString("childArray", _child_array);
                bundle.putString("childid", _child_id);
                bundle.putString("childname", _child_name);
                bundle.putSerializable("data", data);
                fragmentprofile = new FramentProfile();
                fragmentprofile.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragmentprofile).commit();

                mDrawerLayout.closeDrawer(lin_menu);

            } else if (statusCode == ETechAsyncTask.ERROR_NETWORK) {
                try {
                    ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SharedPreferences sharedpref = getSharedPreferences("absentapp", 0);

        language = sharedpref.getString("language", "");
        String localeString;
        if (language.equalsIgnoreCase("english")) {
            localeString = "en";
        } else {
            localeString = "no";
        }

        Configuration config = getResources().getConfiguration();
        Locale locale = new Locale(localeString);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        Drawerlist_item = getResources().getStringArray(R.array.drawer_item);
        adap.updatedata(MainActivity.this, Drawerlist_item);

    }

    public static void sendbadgetoMainActivity(String alert, String message, String type, int kidid,
                                               int from_id, Integer badge, int abi, int abn, int chat_msg) {
        if (fragmenthome != null) {
            fragmenthome.setbadgemessage(alert, message, type, kidid, from_id, badge, abi, abn, chat_msg);
        }
    }

    public static void sendchatbadge() {
        if (fragmenthome != null) {
            fragmenthome.setbadge();//setmessagebadge();
        }
    }

    public class UpdaterBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            if (action.equals(ApplicationData.BROADCAST_CHAT)) {
                Childbeans newmessg = (Childbeans) intent.getSerializableExtra("newMessage");
                String childid = intent.getStringExtra("childid");

                sendchatbadge();

            }
        }
    }

}


