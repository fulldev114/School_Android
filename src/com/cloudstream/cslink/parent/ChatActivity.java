package com.cloudstream.cslink.parent;

import android.*;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.parent.Childbeans;
import com.adapter.parent.MyAdapter;
import com.common.SharedPreferFile;
import com.common.dialog.MainProgress;
import com.common.utils.AppUtils;
import com.common.utils.GlobalConstrants;
import com.common.view.CircularImageView;
import com.db.parent.DatabaseHelper;
import com.langsetting.apps.Change_lang;
import com.xmpp.parent.ConnectivityTask;
import com.xmpp.parent.Constant;
import com.xmpp.parent.XMPPMethod;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import com.cloudstream.cslink.R;

public class ChatActivity extends ActivityHeader {

    String message_id, sender_id, sendername, sender_image, sender_subject,
            getmessage_url, _parent_id, sendmessage_url, sender_school,
            window_id, child_id, child_image, phone, receiver_jid, sender_jid, parentno;

    CircularImageView profileimage;
    ListView messagelistview;
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

    ChatActivity mActivity;
    private boolean refreashFlag = true;

    SharedPreferences sharedpref;
    private int currSelection = 0;
    private String lastMessage = "";
    private SharedPreferFile shf;
    private XmppReceiver receiver;
    private int height, width;
    private boolean istyping = false;
    private int previouslengh = 0;
    Handler handler = null, handler1 = null;
    private Runnable myrunnable, myrunnable1;
    private LayoutInflater inflater;
    private RelativeLayout screenview;
    private final static int REQUEST_CONTACTS_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater = getLayoutInflater();
        screenview = (RelativeLayout) inflater.inflate(R.layout.chatactivity, null);
        relwrapp.addView(screenview);

        mActivity = this;
        ApplicationData.setChatActivity(mActivity);
        refreashFlag = true;

        sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        change_lang = new Change_lang(getApplicationContext());
        sender_jid = sharedpref.getString("jid", "");
        _parent_id = sharedpref.getString("parent_id", "");
        child_id = sharedpref.getString("childid", "");
        child_image = sharedpref.getString("image", "");
        phone = sharedpref.getString("phone", "");
        parentno = sharedpref.getString("parent_no", "");

        Intent intent = getIntent();
        // message_id = intent.getStringExtra("message_id");
        sender_id = intent.getStringExtra("sender_id");
        sendername = intent.getStringExtra("sendername");
        sender_image = intent.getStringExtra("sender_image");
        sender_subject = intent.getStringExtra("sender_subject");
        sender_school = intent.getStringExtra("sender_school");
        receiver_jid = intent.getStringExtra("receiver_jid");
        ApplicationData.receiver_jid = receiver_jid;

        firsttime = 1;
        shf = new SharedPreferFile(ChatActivity.this);
        school = (TextView) findViewById(R.id.textView1);
        school.setText(sender_school);
        // .....................ListView................//
        messagelistview = (ListView) findViewById(R.id.lstMessages);
        message = (EditText) findViewById(R.id.editText1);
        message_send = (RelativeLayout) findViewById(R.id.sendmessage);
        txt_load = (TextView) findViewById(R.id.txt_load);
        userprofile.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.textView1)).setVisibility(View.GONE);

        //get height and width
        DisplayMetrics matric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(matric);
        height = matric.heightPixels;
        width = matric.widthPixels;
        //set user image
        ApplicationData.setProfileImg(this, ApplicationData.web_server_url + "uploads/" + sender_image, imageView2);

//		adapter = new MyAdapter(mActivity, recmsg, sender_image, child_image);

        imgback.setOnClickListener(new OnClickListener() {
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

        profileimage = (CircularImageView) findViewById(R.id.imageView2);
        ApplicationData.setProfileImg(mActivity, ApplicationData.web_server_url + "uploads/" + sender_image, profileimage);

        subject.setText(sender_subject);
        name.setText(sendername);

        //fetch history from xmpp
        localarray = new ArrayList<Childbeans>();
        adapter = new MyAdapter(mActivity, recmsg, sender_image, child_image, phone, height, width);
        messagelistview.setAdapter(adapter);

      /*  if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (AppUtils.hasSelfPermission(mActivity, CALL_PERMISSIONS)) {
            } else {
                requestPermissions(CALL_PERMISSIONS, REQUEST_CONTACTS_CODE);
            }
        }
*/
        if (GlobalConstrants.isWifiConnected(ChatActivity.this)) {
            if (XMPPMethod.getconnectivity() != null && XMPPMethod.getconnectivity().isAuthenticated()) {
                LoadingHistoryTask task = new LoadingHistoryTask(ChatActivity.this);
                task.execute();
            } else {
                new chatclass().execute();
            }
        }

        if (recmsg != null) {
        }

        message.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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

                        Log.e("call handler remove", "first time");
                    }

                    if (handler == null) {
                        handler = new Handler();
                        handler.postDelayed(myrunnable, 1000);
                        Log.e("call handler crate", "first time");
                    }
                } else if (s.toString().trim().length() == 0 && istyping) {
                    //Log.i(TAG, "typing stopped event...");
                    istyping = false;
                    XMPPMethod.typingstatus(ChatActivity.this, receiver_jid, istyping);
                } else if (!message.hasFocus()) {
                    istyping = false;
                    XMPPMethod.typingstatus(ChatActivity.this, receiver_jid, istyping);
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
                    XMPPMethod.typingstatus(ChatActivity.this, receiver_jid, istyping);
                } else {
                    //change status on pause
                    if (previouslengh == message.getText().toString().length()) {
                        MyTimerclass timer = new MyTimerclass(previouslengh, message.getText().toString().length(), receiver_jid, istyping, 3000, 1000);
                        timer.start();
                    }
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
//                final PopupWindow popupWindow = new PopupWindow(ChatActivity.this);
//                getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View convertView;
//                convertView = inflater.inflate(R.layout.item_copystring, null);
//
//                convertView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//
//                // some other visual settings for popup window
//                popupWindow.setFocusable(true);
//
//                LinearLayout lin_spinner = (LinearLayout) convertView.findViewById(R.id.lin_spinner);
//                TextView txt_spin = (TextView) convertView.findViewById(R.id.txt_spin);
//
//                lin_spinner.setBackgroundResource(R.drawable.white_backgroud_corner_rec);
//                txt_spin.setTextColor(getResources().getColor(R.color.color_black));
//
//                txt_spin.setText(getString(R.string.paste));
//                popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
//                // popupWindow.setBackgroundDrawable(null);
//                popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//                // popupWindow.setOutsideTouchable(true);
//                // set the list view as pop up window content
//                popupWindow.setOutsideTouchable(true);
//                popupWindow.setContentView(convertView);
//
//                int[] loc_int = new int[2];
//
//                Rect location = new Rect();
//                location.left = loc_int[0];
//                location.top = loc_int[1];
//                location.right = location.left + v.getWidth();
//                location.bottom = location.top + v.getHeight();
//
//                popupWindow.showAsDropDown(v, 0, -message.getMeasuredHeight() - 100);
//
//                txt_spin.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        message.setText(adapter.getcopyText().length() > 0 ? adapter.getcopyText() : "");
//                        popupWindow.dismiss();
//                        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                            @Override
//                            public void onDismiss() {
//                            }
//                        });
//                    }
//
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
                    Childbeans data = xmt.sendmessage(receiver_jid, mess_age, ChatActivity.this, istyping);
                    if (data != null) {
                        if (data.flag) {
                            message.setText("");
                            addtoist(mess_age, data.message_id);
                        }
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

                        PagingHistory task = new PagingHistory(ChatActivity.this);
                        task.execute();
                    }

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        first_time = 1;
        registerXmppReceiver();

    }

    private void typingstastus(final int previouslengh) {

        if (previouslengh > 0) {
            istyping = true;
            XMPPMethod.typingstatus(ChatActivity.this, receiver_jid, istyping);

            myrunnable1 = new Runnable() {
                @Override
                public void run() {
                    if (message != null) {
                        if (message.getText().toString().trim() != null && !message.getText().toString().isEmpty()) {
                            if (previouslengh == message.getText().toString().length()) {
                                istyping = false;
                                XMPPMethod.typingstatus(ChatActivity.this, receiver_jid, istyping);
                            }
                        }
                    }
                }
            };


            if (handler1 != null) {
                handler1.removeCallbacks(myrunnable1);
            }

            if (handler1 == null) {
                handler1 = new Handler();
                handler1.postDelayed(myrunnable1, 2000);
            }

        }
    }

    protected void addtoist(String mess_age, String message_id) {
        // TODO Auto-generated method stub
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String fmt = sdf.format(cal.getTime());
        Childbeans childbeans = new Childbeans();
        childbeans.user_id = "0";
        childbeans.name = "0";
        childbeans.image = child_image;
        childbeans.message_body = mess_age;
        childbeans.created_at = fmt;
        childbeans.child_marked_id = "0";
        childbeans.child_moblie = phone;
        childbeans.sender = "me";
        childbeans.sender_id = _parent_id;
        childbeans.sender_id_jid = Constant.getUserName(sender_jid).toUpperCase();
        childbeans.receiver_id_jid = Constant.getUserName(receiver_jid).toUpperCase();
        childbeans.message_status = "Sent";
        childbeans.message_id = message_id;
        childbeans.iscarboncopy = false;
       /* else
            childbeans.message_status = getResources().getString(R.string.str_not_seen);*/
        childbeans.teacher_id = sender_id;
        childbeans.receiver_name = sendername;
        childbeans.receiver_image = sender_image;
        childbeans.parentno = parentno;
        //childbeans.chat_time=formattedDate;
        if (recmsg == null)
            recmsg = new ArrayList<>();

        recmsg.add(childbeans);
        Childbeans bean = childbeans;
        localarray.add(bean);

        adapter.updateListAdapter(recmsg, sender_image, child_image, phone);
        adapter.notifyDataSetChanged();

        if (recmsg.size() == 0)
            return;
        if (!recmsg.get(recmsg.size() - 1).message_body.equals(lastMessage)) {
            lastMessage = recmsg.get(recmsg.size() - 1).message_body;
            messagelistview.setSelection(recmsg.size());
        }    //compile "org.igniterealtime.smack:smack-bosh:4.1.3"


       /* DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(ChatActivity.this);
        db.inserthistory(childbeans);*/

        array_size = recmsg.size();
        firsttime = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTimerEnableNotifyTyping != null) {
            mTimerEnableNotifyTyping.cancel();
        }
        String msg = message.getText().toString();
        SharedPreferences.Editor editor = sharedpref.edit();
        if (msg.length() > 0) {
            editor.putString("" + _parent_id + "_" + child_id + "_" + sender_id, msg);
            editor.commit();
        }
        istyping = false;

        ApplicationData.setChatActivity(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTimerEnableNotifyTyping != null) {
            mTimerEnableNotifyTyping.cancel();
        }
//		myHandler.removeCallbacksAndMessages(null);
//		ApplicationData.setChatActivity(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationData.setChatActivity(mActivity);
        String msg = sharedpref.getString("" + _parent_id + "_" + child_id + "_" + sender_id, "");
    }

    @Override
    public void onBackPressed() {

        ApplicationData.setChatActivity(null);
        finish();
    }

    private void registerXmppReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.CUSTOM_INTENT_XMPP);
        filter.addAction(Constant.CUSTOM_INTENT_XMPP_MESSAGE_DELIVERED);
        receiver = new XmppReceiver();
        registerReceiver(receiver, filter);
    }

    private class XmppReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent data) {
            if (data.getAction().equals(Constant.CUSTOM_INTENT_XMPP)) {

                Childbeans newmesg = (Childbeans) data.getSerializableExtra("newMessage");

                if ((newmesg.receiver_id_jid.equals(Constant.getUserName(sender_jid)) || newmesg.sender_id_jid.equalsIgnoreCase(Constant.getUserName(sender_jid)))
                        && !newmesg.typing
                        && newmesg.message != null && newmesg.message.length() > 0) {

                    subject.setText(sender_subject);
                    Childbeans childbeans = getdatafromservice(newmesg);

                } else if (newmesg.receiver_id_jid.equalsIgnoreCase(Constant.getUserName(shf.getJidUser()))) {
                    if (newmesg.typing)
                        subject.setText(getString(R.string.typing));
                    else
                        subject.setText(sender_subject);
                }

            } else if (data.getAction().equals(Constant.CUSTOM_INTENT_XMPP_MESSAGE_DELIVERED)) {
                Childbeans newmesg = (Childbeans) data.getSerializableExtra("newMessage");
                newmesg.receiver_id_jid = Constant.getUserName(newmesg.receiver_id_jid).toUpperCase();
                newmesg.sender_id_jid = Constant.getUserName(newmesg.sender_id_jid).toUpperCase();
               /* if (newmesg.receiver_id_jid.contains("/"))
                    newmesg.receiver_id_jid = ApplicationData.getjid(newmesg.receiver_id_jid);

                if (newmesg.sender_id_jid.contains("/"))
                    newmesg.sender_id_jid = ApplicationData.getjid(newmesg.sender_id_jid);
*/
                if (newmesg.receiver_id_jid.equals(Constant.getUserName(sender_jid))) {
                    Childbeans childbeans = new Childbeans();
                    childbeans.message_id = newmesg.message_id;
                    childbeans.receiver_id_jid = newmesg.sender_id_jid;
                    childbeans.sender_id_jid = newmesg.receiver_id_jid;
                    childbeans.sender_id = _parent_id;
                    childbeans.message_status = "Received";

                    DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(ChatActivity.this);
                    db.updatestatus(ChatActivity.this, childbeans);
                    db.insertdeliverystatus(childbeans);

                    for (int i = 0; i < recmsg.size(); i++) {
                        if (recmsg.get(i).message_id != null && newmesg.message_id != null && newmesg.message_id.length() > 0) {
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
        Childbeans childbeans = new Childbeans();
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String fmt = sdf.format(cal.getTime());
        //String formattedDate = dateFormat.format(c.getTime());

        childbeans.message_id = newmesg.message_id;
        childbeans.user_id = "0";
        childbeans.name = "0";
        childbeans.image = child_image;
        childbeans.message_body = newmesg.message;
        childbeans.child_marked_id = "0";
        childbeans.child_moblie = phone;
        childbeans.created_at = fmt;
        childbeans.sender_id = _parent_id;
        childbeans.sender_id_jid = newmesg.sender_id_jid;
        childbeans.receiver_id_jid = newmesg.receiver_id_jid;
        childbeans.message_status = "Received";
        childbeans.teacher_id = sender_id;
        childbeans.receiver_name = sendername;
        childbeans.receiver_image = sender_image;//newmesg.receiver_name;
        childbeans.iscarboncopy = newmesg.iscarboncopy;
        childbeans.parentno = newmesg.parentno;
       /* if(newmesg.iscarboncopy){
            childbeans.message_status="";
        }*/

        if (newmesg.sender_id_jid.equalsIgnoreCase(Constant.getUserName(sender_jid)))
            childbeans.sender = "me";
        else
            childbeans.sender = "from";

        if (duplicatemessage != null && duplicatemessage.size() > 0) {
            for (int msgid = 0; msgid < duplicatemessage.size(); msgid++) {
                if (duplicatemessage.get(msgid).message_id.equalsIgnoreCase(newmesg.message_id)) {
                    messageavailable = true;
                    break;
                }
            }
        } else if (duplicatemessage != null && duplicatemessage.size() <= 0) {
            duplicatemessage.add(childbeans);
            messageavailable = false;
        }

        if (!messageavailable) {
            duplicatemessage.add(childbeans);
            setmessagehistory(childbeans);
        }

        return childbeans;
    }

    private void setmessagehistory(Childbeans messagelist) {
        recmsg.add(messagelist);
        Childbeans bean = messagelist;
        localarray.add(messagelist);

        adapter.updateListAdapter(recmsg, sender_image, child_image, phone);
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

   /* private void gethistory(ArrayList<HashMap<String, String>> messagelist) {

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
            categoryObj.receiver_id_jid = categoryMap.get("Receiver_jid");
            categoryObj.message_status = categoryMap.get("Message_status");
            categoryObj.receiver_image = categoryMap.get("Receiver_image");
            categoryObj.teacher_id = sender_id;
            recmsg.add(categoryObj);
        }


        recmsg.add(messagelist);
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
    }*/

    private void unregisterXmppReceiver() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationData.setChatActivity(null);
        unregisterXmppReceiver();


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
        sender_jid = null;
        parentno = null;

        profileimage = null;
        messagelistview = null;
        // Runnable runnable;
        pDialog = null;
        adapter = null;
        message = null;
        message_send = null;
        school = null;
        txt_load = null;
        change_lang = null;
        mTimerEnableNotifyTyping = null;
        recmsg = null;
        localarray = null;
        duplicatemessage = null;
        mActivity = null;
        sharedpref = null;
        lastMessage = null;
        shf = null;
        receiver = null;
        handler = null;
        handler1 = null;
        myrunnable = null;
        myrunnable1 = null;
        inflater = null;
        screenview = null;

        System.gc();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public String getChildId() {
        return child_id;
    }

    public String getSenderId() {
        return sender_id;
    }

    private class MyTimerclass extends CountDownTimer {


        private final int oldlength, newlength;
        private final String receiver_jid;
        private boolean istyping;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyTimerclass(int oldlength, int newlength, String receiver_jid, boolean istyping,
                            long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.oldlength = oldlength;
            this.newlength = newlength;
            this.receiver_jid = receiver_jid;
            this.istyping = istyping;
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (oldlength == newlength) {
                istyping = false;
                XMPPMethod.typingstatus(ChatActivity.this, receiver_jid, istyping);
            }
        }
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
            txt_load.setText(getString(R.string.str_loading));
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
                if (recmsg.size() > 0)
                    recmsg.clear();
                recmsg = XMPPMethod.loadHistory(context, receiver_jid);
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
                adapter.updateListAdapter(recmsg, sender_image, child_image, phone);
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
        private List<Childbeans> oldhistory = new ArrayList<Childbeans>();
        private List<Childbeans> nexthistory;

        public PagingHistory(Activity context) {

            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txt_load.setVisibility(View.VISIBLE);
            txt_load.setText(getString(R.string.str_loading));
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {

                nexthistory = XMPPMethod.fetchbeforehistory(ChatActivity.this, receiver_jid);
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
                int firstPosition = messagelistview.getFirstVisiblePosition();
                adapter.updateListAdapter(recmsg, sender_image, child_image, phone);
                Log.d("position : ", firstPosition + "");
                messagelistview.setSelection(firstPosition);
                adapter.notifyDataSetChanged();

            }
        }
    }


    private class chatclass extends AsyncTask<Void, Void, Void> {
        int position;
        SharedPreferences myPrefs = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        MainProgress pdialog = null;

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
                    XMPPMethod.connect(ChatActivity.this, myPrefs.getString("jid", ""), "5222", myPrefs.getString("jid_pwd", ""));
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

            LoadingHistoryTask task = new LoadingHistoryTask(ChatActivity.this);
            task.execute();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CONTACTS_CODE) {

            if (AppUtils.verifyAllPermissions(grantResults)) {
                Toast.makeText(ChatActivity.this, "Permission Granted.Contacts available", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(ChatActivity.this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
