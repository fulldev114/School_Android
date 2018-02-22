package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapter.teacher.Childbeans;
import com.adapter.teacher.MyAdapter;
import com.cloudstream.cslink.R;
import com.common.SharedPreferFile;
import com.common.dialog.MainProgress;
import com.common.utils.GlobalConstrants;
import com.db.teacher.DatabaseHelper;
import com.langsetting.apps.Change_lang;
import com.xmpp.teacher.Constant;
import com.xmpp.teacher.XMPPMethod;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;

public class ChatActivity_Teacher extends ActivityHeader {

    String sender_id, sender_name, sender_image, parent_name, class_name,
            getmessage_url, _teacher_id, sendmessage_url, _teacher_image,
            window_id, receiver_jid, sender_id_jid;

    ListView messagelistview;
    private Handler myHandler;
    // Runnable runnable;
    private MainProgress pDialog;
    MyAdapter adapter;
    EditText message;
    RelativeLayout message_send;
    int first_time = 0;
    private TextView school, txt_load;
    int array_size, firsttime;
    private Change_lang change_lang;

    Timer mTimerEnableNotifyTyping = null;

    List<Childbeans> recmsg = new ArrayList<Childbeans>();
    List<Childbeans> localarray;
    List<Childbeans> duplicatemessage = new ArrayList<Childbeans>();

    ChatActivity_Teacher mActivity;
    private boolean refreashFlag = true;
    SharedPreferences sharedpref;
    private int currSelection = 0;
    String lastMessage = "";
    private XmppReceiver receiver;
    private SharedPreferFile shf;
    private Childbeans data;
    private String school_id;
    private LinearLayout information;
    private int height, width;
    private boolean istyping = false;
    Handler handler = null, handler1 = null;
    private Runnable myrunnable, myrunnable1;
    private int previouslengh = 0;
    private LayoutInflater inflater;
    private RelativeLayout screenview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater = getLayoutInflater();
        screenview = (RelativeLayout) inflater.inflate(R.layout.adres_chatactivity, null);
        relwrapp.addView(screenview);

        mActivity = this;
        ApplicationData.setInternalActivity(mActivity);

        refreashFlag = true;
        sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        shf = new SharedPreferFile(ChatActivity_Teacher.this);
        change_lang = new Change_lang(getApplicationContext());

        _teacher_id = sharedpref.getString("teacher_id", "");
        _teacher_image = sharedpref.getString("image", "");
        sender_id_jid = sharedpref.getString("jid", "");

        message = (EditText) findViewById(R.id.editText1);
        school = (TextView) findViewById(R.id.textView1);
        messagelistview = (ListView) findViewById(R.id.lstMessages);
        message_send = (RelativeLayout) findViewById(R.id.sendmessage);
        information = (LinearLayout) findViewById(R.id.information);
        txt_load = (TextView) findViewById(R.id.txt_load);
        information.setVisibility(View.GONE);

        if (getIntent().hasExtra("data")) {
            data = (Childbeans) getIntent().getSerializableExtra("data");
            if (data != null && data.sender_id.length() > 0) {
                sender_id = data.sender_id;
                sender_image = data.child_image;
                sender_name = data.name;
                school_id = data.school_id;
                receiver_jid = data.jid;
                ApplicationData.receiver_jid = receiver_jid;
            }
        }
        // message_id = intent.getStringExtra("message_id");

        //get height and width
        DisplayMetrics matric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(matric);
        height = matric.heightPixels;
        width = matric.widthPixels;

        firsttime = 1;
        school.setText(sender_name);
        shf = new SharedPreferFile(ChatActivity_Teacher.this);
        // .....................ListView................//

//		adapter = new MyAdapter(mActivity, recmsg, sender_image, _teacher_image);

        userprofile.setVisibility(View.VISIBLE);
        school.setVisibility(View.GONE);

        //set user image
        ApplicationData.setProfileImg(profileimage, ApplicationData.web_server_url + ApplicationData.imagepath + sender_image, this);
        name.setText(sender_name);
        subject.setText("");//data.emailaddress
        //  message.setHint(change_lang.get_name(4));

        adapter = new MyAdapter(mActivity, recmsg, sender_image, _teacher_image, width, height, null,true);
        messagelistview.setAdapter(adapter);

        localarray = new ArrayList<Childbeans>();
        if (GlobalConstrants.isWifiConnected(ChatActivity_Teacher.this)) {

            if (XMPPMethod.getconnectivity() != null && XMPPMethod.getconnectivity().isAuthenticated()) {
                LoadingHistoryTask task = new LoadingHistoryTask(ChatActivity_Teacher.this);
                task.execute();
            } else {
                new chatclass().execute();
            }
        }

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                finish();

            }
        });

        message.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

/* if(message.hasFocus()) {
                    istyping = true;
                    XMPPMethod.typingstatus(ChatActivity.this, receiver_jid, messageListener, istyping);
                }*/

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
                if (count == 0) {
                    //	myHandler.removeCallbacksAndMessages(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                previouslengh = message.getText().toString().length();
                myrunnable = new Runnable() {
                    @Override
                    public void run() {
                        typingstastus(previouslengh);
                    }
                };
                if (!TextUtils.isEmpty(s.toString()) && s.toString().trim().length() >= 1) {
                    //Log.i(TAG, "typing started event...");
                    if (handler != null) {
                        handler.removeCallbacks(myrunnable);
                        handler = null;

                        if (handler1 != null) {
                            handler1.removeCallbacks(myrunnable1);
                            handler1 = null;
                        }
                    }

                    if (handler == null) {
                        handler = new Handler();
                        handler.postDelayed(myrunnable, 1000);
                    }
                } else if (s.toString().trim().length() == 0 && istyping) {
                    //Log.i(TAG, "typing stopped event...");
                    istyping = false;
                    XMPPMethod.typingstatus(ChatActivity_Teacher.this, receiver_jid, istyping);
                } else if (!message.hasFocus()) {
                    istyping = false;
                    XMPPMethod.typingstatus(ChatActivity_Teacher.this, receiver_jid, istyping);
                }
            }
        });


        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String input;
                EditText editText;

                if (!hasFocus) {
                    istyping = false;
                    XMPPMethod.typingstatus(ChatActivity_Teacher.this, receiver_jid, istyping);
                }
            }
        });


        message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
//                final PopupMenu popupMenu = new PopupMenu(ChatActivity_Teacher.this, v);
//                popupMenu.inflate(R.menu.popup_menu);
//                popupMenu.show();
//
//                if (adapter.getcopyText().length() == 0)
//                    popupMenu.dismiss();
//
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        message.setText(adapter.getcopyText().length() > 0 ? adapter.getcopyText() : "");
//                        popupMenu.dismiss();
//                        return false;
//                    }
//                });

                return false;
            }
        });


        message_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String newString = message.getText().toString().trim();
                if (newString.length() > 0) {
                    String mess_age = newString;

                    istyping = false;
                    XMPPMethod xmt = new XMPPMethod();
                    Childbeans data = xmt.sendmessage(receiver_jid, mess_age, ChatActivity_Teacher.this, istyping);
                    if (data != null && data.flag) {
                        message.setText("");
                        addtoist(mess_age, data.message_id);
                    } else {
                        ApplicationData.showToast(mActivity, R.string.server_error, false);
                    }
                } else {
                    ApplicationData.showToast(mActivity, R.string.str_input_chatmessage, false);
                }
            }
        });

        messagelistview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    if (!Constant.iscomplete && messagelistview.getFirstVisiblePosition() == 0) {
                        if (GlobalConstrants.isWifiConnected(ChatActivity_Teacher.this)) {
                            PagingHistory task = new PagingHistory(ChatActivity_Teacher.this);
                            task.execute();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


        first_time = 1;
        //to receive message
        registerXmppReceiver();

    }


    private void typingstastus(final int previouslengh) {

        if (previouslengh > 0) {
            istyping = true;
            XMPPMethod.typingstatus(ChatActivity_Teacher.this, receiver_jid, istyping);

            myrunnable1 = new Runnable() {
                @Override
                public void run() {
                    if (previouslengh == message.getText().toString().length()) {
                        istyping = false;
                        XMPPMethod.typingstatus(ChatActivity_Teacher.this, receiver_jid, istyping);
                    }
                }
            };


            if (handler1 != null) {
                handler1.removeCallbacks(myrunnable1);
            }

            if (handler1 == null) {
                handler1 = new Handler();
                handler1.postDelayed(myrunnable1, 1000);
            }

        }
    }


    protected void addtoist(String mess_age, String message_id) {
        // TODO Auto-generated method stub
        Calendar c = Calendar.getInstance();
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String fmt = sdf.format(cal.getTime());
        Childbeans childbeans = new Childbeans();
        childbeans.message_id = message_id;
        childbeans.user_id = "0";
        childbeans.name = "0";
        childbeans.image = _teacher_image;
        childbeans.message_body = mess_age;
        childbeans.child_marked_id = "0";
        childbeans.child_moblie = getResources().getString(R.string.str_tmp_phone);
        childbeans.created_at = fmt;
        childbeans.sender = "me";
        childbeans.sender_id_jid = Constant.getUserName(sender_id_jid);
        childbeans.receiver_id_jid = Constant.getUserName(receiver_jid);
        childbeans.message_status = "Sent";
        childbeans.receiver_image = sender_image;
        childbeans.sender_id = _teacher_id;
        childbeans.parent_id = sender_id;
        //childbeans.chat_time=formattedDate;
        childbeans.receiver_name = sender_name;

        if (recmsg == null)
            recmsg = new ArrayList<>();

        recmsg.add(childbeans);
        Childbeans bean = childbeans;
        localarray.add(bean);

        adapter.updateReceiptsList(recmsg,true);
        adapter.notifyDataSetChanged();
        array_size = recmsg.size();

       /* DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(ChatActivity_Teacher.this);
        db.inserthistory(childbeans);*/
        firsttime = 0;

        if (recmsg.size() == 0)
            return;
        if (!recmsg.get(recmsg.size() - 1).message_body.equals(lastMessage)) {
            lastMessage = recmsg.get(recmsg.size() - 1).message_body;
            messagelistview.setSelection(recmsg.size());
        }
    }




    protected void checkdialog() {
        // TODO Auto-generated method stub
        if (pDialog != null && pDialog.isShowing()) {
            // is running
            pDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationData.setInternalActivity(null);
        SharedPreferences.Editor editor = sharedpref.edit();
        String msg = message.getText().toString();
        if (msg.length() > 0) {
            editor.putString("t" + _teacher_id + "_" + sender_id, msg);
            editor.commit();
        }

        // Background_work.set_background_time();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onStop() {
        super.onStop();
        ApplicationData.setInternalActivity(null);
        message.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        ApplicationData.setInternalActivity(mActivity);

        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


        String msg = sharedpref.getString("t" + _teacher_id + "_" + sender_id, "");

        //   XMPPMethod.Userisonline(ChatActivity_Teacher.this, sender_id_jid);

    }

    @Override
    public void onBackPressed() {

        ApplicationData.setInternalActivity(null);
        message.setText("");
        /*Intent in = new Intent(mActivity, MainActivity.class);
        startActivity(in);*/
        finish();
    }

    private void registerXmppReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.CUSTOM_INTENT_XMPP_INTERNAL);
        filter.addAction(Constant.CUSTOM_INTENT_XMPP_MESSAGE_DELIVERED_INTERNAL);
        receiver = new XmppReceiver();
        registerReceiver(receiver, filter);
    }

    private class XmppReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent data1) {
            if (data1.getAction().equals(Constant.CUSTOM_INTENT_XMPP_INTERNAL)) {
                Childbeans newmesg = (Childbeans) data1.getSerializableExtra("newMessage");

                if (newmesg.receiver_id_jid.equalsIgnoreCase(Constant.getUserName(shf.getJidUser()))
                        && newmesg.message_body != null && newmesg.message_body.length() > 0) {

                    subject.setText("");
                    Childbeans childbeans = getdatafromservice(newmesg);


                } else if (newmesg.receiver_id_jid.equalsIgnoreCase(Constant.getUserName(shf.getJidUser()))) {
                    if (newmesg.typing)
                        subject.setText(getString(R.string.typing));
                    else
                        subject.setText("");
                }
            } else if (data1.getAction().equals(Constant.CUSTOM_INTENT_XMPP_MESSAGE_DELIVERED_INTERNAL)) {
                Childbeans newmesg = (Childbeans) data1.getSerializableExtra("newMessage");

                if (newmesg.receiver_id_jid.equals(Constant.getUserName(sender_id_jid).toUpperCase())) {
                    Childbeans childbeans = new Childbeans();
                    childbeans.message_id = newmesg.message_id;
                    childbeans.receiver_id_jid = newmesg.sender_id_jid;
                    childbeans.sender_id_jid = newmesg.receiver_id_jid;
                    childbeans.sender_id = _teacher_id;
                    childbeans.message_status = "Received";

                    DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(ChatActivity_Teacher.this);
                    db.updatestatus(ChatActivity_Teacher.this, childbeans);
                    db.insertdeliverystatus(childbeans);

                    for (int i = 0; i < recmsg.size(); i++) {
                        if(recmsg.get(i).message_id!=null) {
                            if (recmsg.get(i).message_id.equalsIgnoreCase(newmesg.message_id)) {
                                recmsg.get(i).message_status = childbeans.message_status;
                                break;
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (recmsg.size() == 0)
                        return;
                    if (!recmsg.get(recmsg.size() - 1).message_body.equals(lastMessage)) {
                        lastMessage = recmsg.get(recmsg.size() - 1).message_body;
                        if (messagelistview.getLastVisiblePosition() == recmsg.size() - 2)
                            messagelistview.setSelection(recmsg.size());
                    }
                }
            }
        }
    }

    private Childbeans getdatafromservice(Childbeans newmesg) {
        boolean messageavailable = false;
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String fmt = sdf.format(cal.getTime());
        Childbeans childbeans = new Childbeans();
        childbeans.message_id = newmesg.message_id;
        childbeans.user_id = "0";
        childbeans.name = "0";
        childbeans.image = _teacher_image;
        childbeans.message_body = newmesg.message_body;
        childbeans.child_marked_id = "0";
        childbeans.child_moblie = getResources().getString(R.string.str_tmp_phone);
        childbeans.created_at = fmt;
        childbeans.sender = "from";
        childbeans.sender_id_jid = newmesg.sender_id_jid;
        childbeans.receiver_id_jid = newmesg.receiver_id_jid;
        childbeans.message_status = "Received";
        childbeans.receiver_image = sender_image;
        childbeans.sender_id = _teacher_id;
        childbeans.parent_id = sender_id;
        childbeans.receiver_name = newmesg.receiver_name;
        childbeans.parenttype = "";

        setmessagehistory(childbeans);
       /* if (duplicatemessage != null && duplicatemessage.size() > 0) {
            for (int msgid = 0; msgid < duplicatemessage.size(); msgid++) {
                if (duplicatemessage.get(msgid).message_id.equalsIgnoreCase(childbeans.message_id)) {
                    messageavailable = true;
                    break;
                }
            }
        } else if (duplicatemessage != null) {
            duplicatemessage.add(childbeans);
            messageavailable = false;
        }

        if (!messageavailable)
            */

        return childbeans;
    }

    private void setmessagehistory(Childbeans messagelist) {
        recmsg.add(messagelist);
        Childbeans bean = messagelist;
        localarray.add(messagelist);

        adapter.updateReceiptsList(recmsg,true);
        adapter.notifyDataSetChanged();

        //maitain postion of listview
        if (recmsg.size() == 0)
            return;
        if (!recmsg.get(recmsg.size() - 1).message_body.equals(lastMessage)) {
            lastMessage = recmsg.get(recmsg.size() - 1).message_body;
            if (messagelistview.getLastVisiblePosition() == recmsg.size() - 2)
                messagelistview.setSelection(recmsg.size());
        }
        array_size = recmsg.size();
        firsttime = 0;
    }

    private void gethistory(ArrayList<HashMap<String, String>> messagelist) {

        if (recmsg != null && recmsg.size() > 0)
            recmsg.clear();

        for (HashMap<String, String> categoryMap : messagelist) {
            Childbeans categoryObj = new Childbeans();

            categoryObj.message_id = categoryMap.get("Message_id");
            categoryObj.user_id = categoryMap.get("User_id");
            categoryObj.name = categoryMap.get("Username");
            categoryObj.image = categoryMap.get("Sender_image");
            categoryObj.message_body = categoryMap.get("Message");
            categoryObj.child_marked_id = categoryMap.get("Child_marked_id");
            categoryObj.child_moblie = categoryMap.get("Child_mobile");
            categoryObj.created_at = categoryMap.get("Created");
            categoryObj.sender = categoryMap.get("is_fromme");
            categoryObj.sender_id_jid = categoryMap.get("Sender_jid");
            categoryObj.parent_id_jid = categoryMap.get("Receiver_jid");
            categoryObj.message_status = categoryMap.get("Message_status");
            categoryObj.receiver_image = categoryMap.get("Receiver_image");
            categoryObj.parenttype = categoryMap.get("Parent_type");
            recmsg.add(categoryObj);
        }

        adapter.notifyDataSetChanged();

        //maitain postion of listview
        if (recmsg.size() == 0)
            return;
        else if (recmsg.get(recmsg.size() - 1).message_body != null && !recmsg.get(recmsg.size() - 1).message_body.equals(lastMessage)) {
            lastMessage = recmsg.get(recmsg.size() - 1).message_body;
            if (recmsg.size() > 3) {
                if (messagelistview.getLastVisiblePosition() == recmsg.size() - 2)
                    messagelistview.setSelection(recmsg.size());
            }
        }
        array_size = recmsg.size();
        firsttime = 0;
    }

    private void unregisterXmppReceiver() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationData.setInternalActivity(null);
        ApplicationData.setMainActivity(mActivity);
        unregisterXmppReceiver();
        message.setText("");


        System.gc();

        sender_id=null; sender_name=null; sender_image=null; parent_name=null;
        class_name=null;getmessage_url=null; _teacher_id=null; sendmessage_url=null;
        _teacher_image=null;window_id=null; receiver_jid=null; sender_id_jid=null;

        messagelistview=null;
        myHandler=null;
        pDialog=null;
        adapter=null;
        message=null;
        message_send=null;
        school=null; txt_load=null;
        change_lang=null;
        mTimerEnableNotifyTyping = null;
        recmsg = null;
        localarray=null;
        duplicatemessage = null;
        mActivity=null;
        sharedpref=null;
        lastMessage = null;
        receiver=null;
         shf=null;
        data=null;
        school_id=null;
        information=null;
        handler = null;handler1 = null;
        myrunnable=null; myrunnable1=null;
        inflater=null;
        screenview=null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /*public String getParentId() {
        return parent_id;
    }*/

    public String getSenderId() {
        return sender_id;
    }


    public class LoadingHistoryTask extends AsyncTask {

        private final Activity context;

        public LoadingHistoryTask(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txt_load.setVisibility(View.VISIBLE);
            message_send.setEnabled(false);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                message_send.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_cmd_gray));
            } else {
                message_send.setBackground(getResources().getDrawable(R.drawable.btn_cmd_gray));
            }
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                XMPPMethod.Userisonline(context, sender_id_jid);
                if (recmsg.size() > 0)
                    recmsg.clear();

                recmsg = XMPPMethod.loadHistory(context, receiver_jid, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            txt_load.setVisibility(View.GONE);
            message_send.setEnabled(true);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                message_send.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_cmd_chatbox_blue));
            } else {
                message_send.setBackground(getResources().getDrawable(R.drawable.btn_cmd_chatbox_blue));
            }
            if (recmsg != null) {
                adapter.updateReceiptsList(recmsg,true);
                adapter.notifyDataSetChanged();
                messagelistview.setSelection(recmsg.size() - 1);

                for (int loop = 0; loop < recmsg.size(); loop++) {
                    Childbeans bean = recmsg.get(loop);
                    localarray.add(bean);
                }
            }
        }
    }


    public class PagingHistory extends AsyncTask {

        private final Activity context;
        private List<Childbeans> nexthistory;

        public PagingHistory(Activity context) {

            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txt_load.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {

                 nexthistory = XMPPMethod.fetchbeforehistory(ChatActivity_Teacher.this, receiver_jid, false);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            txt_load.setVisibility(View.GONE);
            if (nexthistory != null && nexthistory.size() > 0) {
                //Collections.reverse(localarray);
                for (int j = 0; j < localarray.size(); j++) {
                }

                for (int loop = 0; loop < nexthistory.size(); loop++) {
                    Childbeans bean = nexthistory.get(loop);
                    localarray.add(0, bean);
                }
                recmsg.clear();
                for (int j = 0; j < localarray.size(); j++) {
                    Childbeans bean = localarray.get(j);
                    recmsg.add(bean);
                }
                //Collections.reverse(recmsg);
                adapter.updateReceiptsList(recmsg,true);
                messagelistview.setSelection(0);
                adapter.notifyDataSetChanged();

            }
        }
    }
    class chatclass extends AsyncTask<Void, Void, Void> {
        int position;
        SharedPreferences myPrefs = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txt_load.setVisibility(View.VISIBLE);
            txt_load.setText(getString(R.string.connect));
            message_send.setEnabled(false);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                message_send.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_cmd_gray));
            } else {
                message_send.setBackground(getResources().getDrawable(R.drawable.btn_cmd_gray));
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (myPrefs.getString("jid", "") != null && myPrefs.getString("jid", "").length() > 0) {
                try {
                    XMPPMethod.connect(ChatActivity_Teacher.this, myPrefs.getString("jid", ""), "5222", myPrefs.getString("jid_pwd", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            txt_load.setVisibility(View.GONE);
            message_send.setEnabled(true);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                message_send.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_cmd_chatbox_blue));
            } else {
                message_send.setBackground(getResources().getDrawable(R.drawable.btn_cmd_chatbox_blue));
            }

            LoadingHistoryTask task = new LoadingHistoryTask(ChatActivity_Teacher.this);
            task.execute();
        }
    }
}
