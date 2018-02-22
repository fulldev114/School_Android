package com.cloudstream.cslink.teacher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.adapter.teacher.Childbeans;
import com.adapter.teacher.TeacherListAdapter_message;
import com.cloudstream.cslink.R;
import com.common.utils.ConstantApi;
import com.db.teacher.DatabaseHelper;
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;
import com.xmpp.teacher.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by etech on 10/6/16.
 */
public class FragmentTeacher extends ActivityHeader implements AsyncTaskCompleteListener<String> {

    private Activity mActivity;
    private ListView lstStudent;
    private LinearLayout lin_class_select;
    private String school_id;
    private String teacher_id;
    private String noti_kid_id;
    private boolean isLoading = false;
    ArrayList<Childbeans> allStudentList = new ArrayList<Childbeans>();
    private TeacherListAdapter_message cAdapter;
    private LayoutInflater inflater;
    private View rootView;
    UpdaterBroadcastReceiver updateBroadcaseReceiver = null;
    private SharedPreferences sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;

        inflater=getLayoutInflater();
        rootView = inflater.inflate(R.layout.adres_message_fragment_teacher, null);
        relwrapp.addView(rootView);

        lstStudent = (ListView) findViewById(R.id.lstStudent);

        //setTitle
        showheadermenu(FragmentTeacher.this, getString(R.string.teacher_msg).replace("\n", ""), R.color.white_light, false);

        sharedpref = getSharedPreferences("adminapp", 0);
        school_id = sharedpref.getString("school_id", "");
        teacher_id = sharedpref.getString("teacher_id", "");

        ApplicationData.hideKeyboardForFocusedView(mActivity);

        lstStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @SuppressLint("LongLogTag")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                //if (!isLoading)
                    showChatActivity(position);
            }
        });

        cAdapter = new TeacherListAdapter_message(FragmentTeacher.this, allStudentList);
        lstStudent.setAdapter(cAdapter);
        cAdapter.updateReceiptsList(allStudentList);

        IntentFilter filter = new IntentFilter(ApplicationData.BROADCAST_CHAT_INTERNAL);
        updateBroadcaseReceiver = new UpdaterBroadcastReceiver();
        registerReceiver(updateBroadcaseReceiver, filter);


        callapi();

    }


    public void callapi() {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("teacher_id", teacher_id);

            ETechAsyncTask task = new ETechAsyncTask(FragmentTeacher.this, this, ConstantApi.GET_TEACHER_DETAIL, params);
            task.execute(ApplicationData.main_url + ConstantApi.GET_TEACHER_DETAIL + ".php?");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showChatActivity(int position) {
        Intent in = new Intent(FragmentTeacher.this, ChatActivity_Teacher.class);
        in.putExtra("sender_id", allStudentList.get(position).sender_id);
        in.putExtra("sender_name", allStudentList.get(position).name);
        in.putExtra("sender_image", allStudentList.get(position).child_image);
        in.putExtra("data",allStudentList.get(position));
        in.putExtra("teacher_id", teacher_id);
        in.putExtra("receiver_jid", allStudentList.get(position).jid);
        noti_kid_id = "";
        startActivity(in);
        DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(FragmentTeacher.this);
        db.clearchatbadge(teacher_id, Constant.getUserName(allStudentList.get(position).jid).toUpperCase());

    }

    @Override
    public void onTaskComplete(int statusCode, String responseMsg, String webserviceCb, Object tag) {
        {
            if (statusCode == ETechAsyncTask.COMPLETED) {
                try {
                    JSONObject jObject = new JSONObject(responseMsg);
                    String resMsg = jObject
                            .getString(ConstantApi.REQ_RESPONSE_MSG);

                        if (webserviceCb.equalsIgnoreCase(ConstantApi.GET_TEACHER_DETAIL)) {
                            String flag = jObject.getString("flag");

                            if (Integer.parseInt(flag) == 1) {
                                if (allStudentList != null)
                                    allStudentList.clear();
                                else
                                    allStudentList = new ArrayList<Childbeans>();

                                JSONArray allStudents = jObject.getJSONArray("teacherDetails");

                                for (int i = 0; i < allStudents.length(); i++) {
                                    isLoading=true;
                                    Childbeans childbeans = new Childbeans();
                                    JSONObject c = allStudents.getJSONObject(i);
                                    /*if (c.getString("parent_id") == null || c.getString("parent_id").equals("null") || c.getString("parent_id").isEmpty())
                                        continue;*/
                                    childbeans.sender_id =(c.has("user_id")? c.getString("user_id"):"");
                                    childbeans.role_id=(c.has("role_id")? c.getString("role_id"):"");
                                    childbeans.name = (c.has("name")?c.getString("name"):"");
                                    childbeans.lastname = (c.has("lastname")?c.getString("lastname"):"");
                                    childbeans.school_id=(c.has("school_id")? c.getString("school_id"):"");
                                    childbeans.child_gender=(c.has("gender")? c.getString("gender"):"");
                                    childbeans.child_age=(c.has("age")? c.getString("age"):"");
                                    childbeans.status1=(c.has("marital_status_id")? c.getString("marital_status_id"):"");
                                    childbeans.address=(c.has("address")? c.getString("address"):"");
                                    childbeans.emailaddress=(c.has("emailaddress")? c.getString("emailaddress"):"");
                                    childbeans.mobile1=(c.has("mobile")? c.getString("mobile"):"");
                                    childbeans.child_image = (c.has("image")? c.getString("image"):"");
                                    childbeans.imagepath=(c.has("imagepath")? c.getString("imagepath"):"");
                                    childbeans.username =(c.has("username")? c.getString("username"):"");
                                    childbeans.dob = (c.has("date_of_birth")? c.getString("date_of_birth"):"");
                                    childbeans.joining = (c.has("date_of_joining")? c.getString("date_of_joining"):"");
                                    childbeans.releaving = (c.has("date_of_releaving")? c.getString("date_of_releaving"):"");
                                    childbeans.status_id=(c.has("status_id")? c.getString("status_id"):"");
                                    //childbeans.child_age = ApplicationData.convertToNorweiDate(c.getString("birthday"), mActivity);
                                    childbeans.access_token =(c.has("access_token")? c.getString("access_token"):"");
                                    childbeans.jid=(c.has("jid")?c.getString("jid"):"");
                                    allStudentList.add(childbeans);
                                }

                                cAdapter = new TeacherListAdapter_message(FragmentTeacher.this, allStudentList);
                                lstStudent.setAdapter(cAdapter);

                                setmessagebadge();
                           //     cAdapter.updateReceiptsList(allStudentList);
                                isLoading=false;
                            }
                            else {
                                if(jObject.has("msg"))
                                {
                                    String msg = jObject.getString("msg");
                                    ApplicationData.showToast(mActivity,msg, false);
                                }
                                else
                                ApplicationData.showToast(mActivity,R.string.msg_no_teacher, false);
                            }

                        }
                    /* else {
                        if (jObject.has("other_object")) {
                            JSONObject otherObject = jObject.getJSONObject("other_object");
                            if (otherObject.has("ErrorMessageNumber")) {
                                resMsg = otherObject.getString("ErrorMessageNumber") + " - " + resMsg;
                            }
                        }
                        ApplicationData.showMessage(FragmentTeacher.this,
                                getString(R.string.app_name), resMsg, getString(R.string.str_ok));
                    }*/
                } catch (Exception e) {
                    Log.e("OfferCategory", "onTaskComplete() " + e, e);
                }
            }

            else if (statusCode == ETechAsyncTask.ERROR_NETWORK) {
                try {
                    ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setmessagebadge() {

        DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(FragmentTeacher.this);
        String query = "select * from chat_msg_badge where User_id= "+'"'+teacher_id+'"'+" Order by Created ASC";
        ArrayList<HashMap<String, String>> messagelist = db.selectRecordsFromDBList(query, null);
        if (messagelist != null) {

            for (HashMap<String, String> badgemap : messagelist) {
                if(allStudentList.size()>0) {
                    for (int i = 0; i < allStudentList.size(); i++) {
                        if (Constant.getUserName(allStudentList.get(i).jid).equalsIgnoreCase(badgemap.get("Receiver_jid"))) {
                            allStudentList.get(i).badge = Integer.parseInt(badgemap.get("Badge"));
                            Childbeans chbean=allStudentList.get(i);
                            allStudentList.remove(i);
                            allStudentList.add(0, chbean);
                            break;
                        }
                    }
                }
            }
            if(allStudentList.size()>0)
                cAdapter.updateReceiptsList(allStudentList);
        }
        db.clearallchatbadge(teacher_id, "teacher-teacher");
        SharedPreferences.Editor edit = sharedpref.edit();
        edit.putString("chat_badge", "0");
        edit.commit();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ApplicationData.setMainActivity(mActivity);
            }
        }, 1500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setmessagebadge();
        ApplicationData.setMainActivity(mActivity);
        //ApplicationData.setInternalActivity(null);
        View view = getCurrentFocus();
        if (view!=null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateBroadcaseReceiver != null) {
            FragmentTeacher.this.unregisterReceiver(updateBroadcaseReceiver);
        }

        mActivity=null;
        lstStudent=null;
        lin_class_select=null;
        school_id=null;
        teacher_id=null;
        noti_kid_id=null;
        allStudentList = null;
        cAdapter=null;
        inflater=null;
        rootView=null;
        updateBroadcaseReceiver = null;
        sharedpref=null;

        System.gc();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public class UpdaterBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            if (action.equals(ApplicationData.BROADCAST_CHAT_INTERNAL)) {
                Childbeans newmessg = (Childbeans) intent.getSerializableExtra("newMessage");
                String childid = intent.getStringExtra("childid");

                setmessagebadge();

            }
        }
    }
}
