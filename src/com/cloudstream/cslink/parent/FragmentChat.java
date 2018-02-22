package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.adapter.parent.Childbeans;
import com.adapter.parent.TeacherListAdapter;
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
import com.db.parent.DatabaseHelper;
import com.xmpp.parent.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentChat extends ActivityHeader {


    private ListView _teacherlist;
    private MainProgress pDialog;
    TeacherListAdapter adapter;
    private ArrayList<String> s_message;

    ArrayList<Childbeans> messageList = new ArrayList<Childbeans>();
    String getmessage_url, _parent_id, class_id, _child_id, child_img;
    private String noti_teacher_id = "";
    UpdaterBroadcastReceiver updateBroadcaseReceiver = null;
    Activity mActivity;
    private boolean isLoading = false;
    private View rootView;
    private LayoutInflater inflater;
    static ArrayList<Childbeans> arrchildbadge = new ArrayList<Childbeans>();
    private SharedPreferences sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = FragmentChat.this;
        ApplicationData.setMainActivity(mActivity);
        inflater = getLayoutInflater();
        rootView = inflater.inflate(R.layout.message_fragment, null);
        relwrapp.addView(rootView);

        _teacherlist = (ListView) findViewById(R.id.listView_message);

        sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);

        _parent_id = sharedpref.getString("parent_id", "");
        _child_id = sharedpref.getString("childid", "");
        class_id = sharedpref.getString("school_class_id", "");
        child_img = sharedpref.getString("image", "");

        //set title
        showheadermenu(FragmentChat.this, getString(R.string.message_hint), R.color.white_light, true, child_img);

        if (getIntent().hasExtra("noti_teacher_id")) {
            noti_teacher_id = getIntent().getExtras().getString("noti_teacher_id", "");
        }
        //get badge list and set array of listing

        _teacherlist.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                Intent in = new Intent(mActivity, ChatActivity.class);
                in.putExtra("sender_id", messageList.get(position).user_id);
                in.putExtra("sendername", messageList.get(position).sendername);
                in.putExtra("sender_image", messageList.get(position).senderimage);
                in.putExtra("sender_subject", messageList.get(position).subject_name);
                in.putExtra("sender_school", messageList.get(position).senderschool);
                in.putExtra("parent_id", _parent_id);
                in.putExtra("receiver_jid", messageList.get(position).jid);
//				ApplicationData.showAppBadgeDec(mActivity, messageList.get(position).badge);
                startActivity(in);

                DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(FragmentChat.this);
                db.clearchatbadge(_child_id, Constant.getUserName(messageList.get(position).jid).toUpperCase());
//				mActivity.finish();
            }
        });

        // http://skywaltzlabs.in/abscentapp/get_child_teachers.php?classid=1

        IntentFilter filter = new IntentFilter(ApplicationData.BROADCAST_CHAT);
        updateBroadcaseReceiver = new UpdaterBroadcastReceiver();
        mActivity.registerReceiver(updateBroadcaseReceiver, filter);

        adapter = new TeacherListAdapter(mActivity, messageList, true, arrchildbadge);
        _teacherlist.setAdapter(adapter);

        getmessage(ApplicationData.getlanguageAndApi(FragmentChat.this, ConstantApi.GET_CHILD_TEACHER) + "classid=" + class_id +
                "&user_id=" + _child_id);

    }

    private void setmessagebadge() {

        if (arrchildbadge != null && arrchildbadge.size() > 0)
            arrchildbadge.clear();

        DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(FragmentChat.this);
        String query = "select * from chat_msg_badge where User_id= " + '"' + _child_id + '"' + " Order by Created ASC";
        ArrayList<HashMap<String, String>> messagelist = db.selectRecordsFromDBList(query, null);
        if (messagelist != null) {
            for (HashMap<String, String> badgemap : messagelist) {
                Childbeans bean = new Childbeans();
                if (messageList.size() > 0) {
                    for (int i = 0; i < messageList.size(); i++) {
                        if (Constant.getUserName(messageList.get(i).jid).equalsIgnoreCase(badgemap.get("Receiver_jid"))) {
                            messageList.get(i).badge = Integer.parseInt(badgemap.get("Badge"));
                            // if(!badgemap.get("Badge").equalsIgnoreCase("0"))
                            Childbeans chbean = messageList.get(i);
                            messageList.remove(i);
                            messageList.add(0, chbean);
                            break;
                        }
                    }
                }
            }
            if (messageList.size() > 0)
                adapter.updateListAdapter(messageList, arrchildbadge);
        }
        db.clearallchatbadge(_child_id);
        db.clearbadge("chb", _child_id);
        SharedPreferences.Editor edit = sharedpref.edit();
        edit.putInt("chb", 0);
        edit.commit();
    }

    private void getmessage(String login_url2) {
        // TODO Auto-generated method stub
        isLoading = true;
        if (!GlobalConstrants.isWifiConnected(mActivity)) {
            return;
        }

        if (pDialog == null)
            pDialog = new MainProgress(mActivity);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.dismiss();
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(mActivity);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, login_url2, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            pDialog.dismiss();
                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 1) {
                                JSONObject All_Teachers = response.getJSONObject("All Teachers");
                                JSONArray result = All_Teachers.getJSONArray("teachers");
                                if (messageList != null)
                                    messageList.clear();
                                messageList = new ArrayList<Childbeans>();
                                int noti_position = 0;
                                for (int i = 0; result.length() > i; i++) {
                                    JSONObject c = result.getJSONObject(i);
                                    // int checkdate = cheking(c
                                    // .getString("created_at"));
                                    // if (checkdate == 1) {

                                    Childbeans childbeans = new Childbeans();
                                    childbeans.user_id = c.getString("user_id");
                                    if (noti_teacher_id != null && noti_teacher_id.equals(childbeans.user_id)) {
                                        noti_position = i;
                                    }
                                    childbeans.sendername = c.getString("name");
                                    childbeans.senderimage = c.getString("image");
                                    childbeans.class_id = c.getString("class_id");
//									childbeans.subject_id = c.getString("subject_id");
                                    childbeans.subject_name = c.getString("subject_name");
                                    childbeans.emailaddress = c.getString("emailaddress");
                                    childbeans.senderschool = c.getString("school_name");
                                    childbeans.jid = c.getString("jid");
                                    childbeans.jid_pwd = c.getString("key");

                                    messageList.add(childbeans);

                                    // }
                                }

                                //setlist_toadapter();

                                setmessagebadge();
                                adapter.updateListAdapter(messageList, arrchildbadge);

                                pDialog.dismiss();
                            } else {

                                if (response.has("msg")) {
                                    String msg = response.getString("msg");
                                    ApplicationData.showToast(FragmentChat.this, msg, true);
                                }
                            }
                        } catch (Exception e) {
                            pDialog.dismiss();
                            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_operation_error), true);
                            e.printStackTrace();
                            isLoading = false;
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                ApplicationData.showToast(mActivity, getResources().getString(R.string.str_network_error), true);
                isLoading = false;
            }
        });

        queue.add(jsObjRequest);
    }

    public void setlist_toadapter() {
        isLoading = true;
        // TODO Auto-generated method stub
        /*if (!GlobalConstrants.isWifiConnected(mActivity)) {
			return;
		}*/

        if (messageList != null && messageList.size() > 0) {

            String url = ApplicationData.getlanguageAndApi(FragmentChat.this, ConstantApi.GET_UNREAD_MSG) + "id=" + _parent_id + "&kid_id=" + _child_id;
            RequestQueue queue = Volley.newRequestQueue(mActivity);

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {


                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    Toast.makeText(mActivity, getResources().getString(R.string.str_network_error),
                            Toast.LENGTH_SHORT).show();
                    isLoading = false;
                }
            });
            queue.add(jsObjRequest);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationData.setMainActivity(mActivity);
        setmessagebadge();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updateBroadcaseReceiver != null) {
            mActivity.unregisterReceiver(updateBroadcaseReceiver);
        }

        _teacherlist = null;
        pDialog = null;
        adapter = null;
        s_message = null;
        messageList = null;
        getmessage_url = null;
        _parent_id = null;
        class_id = null;
        _child_id = null;
        child_img = null;
        noti_teacher_id = null;
        updateBroadcaseReceiver = null;
        mActivity = null;
        rootView = null;
        inflater = null;
        sharedpref = null;

        System.gc();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
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
                setmessagebadge();

            }
        }
    }


}
