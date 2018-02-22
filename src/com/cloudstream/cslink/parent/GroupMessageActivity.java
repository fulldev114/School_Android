package com.cloudstream.cslink.parent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.parent.ChildAdapter;
import com.adapter.parent.Childbeans;
import com.adapter.parent.GroupMessageAdapter;
import com.adapter.parent.MyAdapter;
import com.cloudstream.cslink.R;
import com.common.Bean.BadgeBean;
import com.common.SharedPreferFile;
import com.common.utils.ConstantApi;
import com.db.parent.DatabaseHelper;
import com.langsetting.apps.Change_lang;
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;
import com.xmpp.parent.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by etech on 24/6/16.
 */
public class GroupMessageActivity extends ActivityHeader implements AsyncTaskCompleteListener<String> {

    String message_id, sender_id, sendername, sender_image, sender_subject,
            getmessage_url, _parent_id, sendmessage_url, sender_school,
            window_id, child_id, child_image, phone, receiver_jid;

    TextView name, subject;
    // Runnable runnable;
    MyAdapter adapter;
    int first_time = 0;
    private Change_lang change_lang;
    ArrayList<Childbeans> recmsg = new ArrayList<Childbeans>();
    GroupMessageActivity mActivity;
    private boolean refreashFlag = true;
    SharedPreferences sharedpref;
    private SharedPreferFile shf;
    private LinearLayout userprofile;
    private TextView textView1;
    private ImageView imgback;
    private ListView lstMessages;
    private GroupMessageAdapter cAdapter;
    private LayoutInflater inflater;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = GroupMessageActivity.this;
        ApplicationData.setImportantMainActivity(mActivity);

        refreashFlag = true;
        inflater = getLayoutInflater();
        rootView = inflater.inflate(R.layout.groupchat_listview, null);
        relwrapp.addView(rootView);
        sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        change_lang = new Change_lang(getApplicationContext());
        _parent_id = sharedpref.getString("parent_id", "");
        child_id = sharedpref.getString("childid", "");
        child_image = sharedpref.getString("image", "");
        phone = sharedpref.getString("phone", "");

        lstMessages = (ListView) findViewById(R.id.lstMessages);

        //set title
        showheadermenu(this, getString(R.string.gp).replace("\n", "").replace("-", ""), R.color.light_green, true, child_image);

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userid", child_id);
        map.put("os", "android");
        ETechAsyncTask task = new ETechAsyncTask(GroupMessageActivity.this, this, ConstantApi.GROUPMESSAGE, map);
        task.execute(ApplicationData.main_url + ConstantApi.GROUPMESSAGE + ".php?");
    }

    @Override
    public void onTaskComplete(int statusCode, String responseMsg, String webserviceCb, Object tag) {
        if (statusCode == ETechAsyncTask.COMPLETED) {
            try {
                JSONObject jObject = new JSONObject(responseMsg);
                String resMsg = jObject
                        .getString(ConstantApi.REQ_RESPONSE_MSG);


                if (webserviceCb.equalsIgnoreCase(ConstantApi.GROUPMESSAGE)) {
                    String flag = jObject.getString("flag");

                    if (Integer.parseInt(flag) == 1) {

                        if (recmsg != null)
                            recmsg.clear();
                        recmsg = new ArrayList<Childbeans>();
                        int noti_position = 0;

                        try {
                            JSONObject job = jObject.getJSONObject("All Messages");
                            JSONArray jsonObj = job.getJSONArray("received");
                            //  child_array = jsonObj.toString();
                            int adtuallength = jsonObj.length();
                            //  videoNames = new String[adtuallength];
                            for (int i = 0; i < jsonObj.length(); i++) {
                                Childbeans childbeans = new Childbeans();
                                JSONObject c = jsonObj.getJSONObject(i);
                                childbeans.message_id = c.getString("message_id");
                                childbeans.isread = c.getString("isread");
                                childbeans.teacher_id = new String(c.getString("teacher_id"));
                                childbeans.isab = c.getString("isab");
                                childbeans.pri_message_id = c.getString("pri_message_id");
                                childbeans.message_subject = c.getString("message_subject");
                                childbeans.message_desc = c.getString("mm.message_desc");
                                childbeans.created_at = c.getString("created_at");
                                childbeans.fromname = c.getString("fromname");
                                childbeans.rolename = c.getString("rolename");
                                childbeans.toname = c.getString("toname");
                                childbeans.image = c.getString("image");
                                childbeans.class_name = c.getString("class");
                                recmsg.add(childbeans);
                            }
                            // looping through All Contacts
                        } catch (JSONException e) {
                            System.out.println("Andy Error " + e);
                            e.printStackTrace();
                        }

                        cAdapter = new GroupMessageAdapter(GroupMessageActivity.this, recmsg, 0);
                        lstMessages.setAdapter(cAdapter);


                    } else {
                        if (jObject.has("msg")) {
                            String msg = jObject.getString("msg");
                            ApplicationData.showToast(GroupMessageActivity.this, msg, true);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ApplicationData.showToast(GroupMessageActivity.this, getResources().getString(R.string.msg_operation_error), true);
            }
        } else if (statusCode == ETechAsyncTask.ERROR_NETWORK) {
            try {
                ApplicationData.showToast(GroupMessageActivity.this, R.string.msg_operation_error, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void setadapter(String alert, String message, String type, int kidid, int from_id, String kidname,
                           String teachername, String teacherimage) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createdate = sdf.format(Calendar.getInstance().getTime());
        Childbeans childbeans = new Childbeans();
        childbeans.teacher_id = String.valueOf(from_id);
        childbeans.message_desc = message;
        childbeans.created_at = createdate;
        childbeans.fromname = teachername;
        childbeans.toname = kidname;
        childbeans.image = teacherimage;
        recmsg.add(0, childbeans);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cAdapter = new GroupMessageAdapter(GroupMessageActivity.this, recmsg, 0);
                lstMessages.setAdapter(cAdapter);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationData.setImportantMainActivity(null);

        message_id = null;
        sender_id = null;
        sendername = null;
        sender_image = null;
        sender_subject = null;
        getmessage_url = null;
        _parent_id = null;
        sendmessage_url = null;
        sender_school = null;
        window_id = null;
        child_id = null;
        child_image = null;
        phone = null;
        receiver_jid = null;
        name = null;
        subject = null;
        adapter = null;
        change_lang = null;
        recmsg = null;
        mActivity = null;
        sharedpref = null;
        shf = null;
        userprofile = null;
        textView1 = null;
        imgback = null;
        lstMessages = null;
        cAdapter = null;
        inflater = null;
        rootView = null;

        System.gc();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationData.isrunning = true;
        ApplicationData.setImportantMainActivity(mActivity);
        ApplicationData.setMainActivity(mActivity);
    }


}
