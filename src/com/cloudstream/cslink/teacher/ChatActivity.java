package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import com.adapter.teacher.Childbeans;
import com.adapter.teacher.MyAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudstream.cslink.R;
import com.common.SharedPreferFile;
import com.common.dialog.MainProgress;
import com.common.utils.GlobalConstrants;
import com.db.teacher.DatabaseHelper;
import com.langsetting.apps.Change_lang;
import com.xmpp.teacher.Constant;
import com.xmpp.teacher.XMPPMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;

public class ChatActivity extends ActivityHeader {

    String sender_id, sender_name, sender_image, parent_id, parent_name, class_name,
            getmessage_url, _teacher_id, sendmessage_url, _teacher_image,
            window_id, receiver_jid, phone;

    ListView messagelistview;
    private Handler myHandler;
    // Runnable runnable;
    private MainProgress pDialog;
    MyAdapter adapter;
    EditText message;
    RelativeLayout message_send;
    int first_time = 0;
    private TextView school;
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
    String lastMessage = "";
    private XmppReceiver receiver;
    private SharedPreferFile shf;
    private LinearLayout information;
    private int height, width;
    private String sender_jid;
    private boolean istyping = false;
    Handler handler = null, handler1 = null;
    private Runnable myrunnable, myrunnable1;
    private int previouslengh = 0;
    private Childbeans data;
    private LayoutInflater inflater;
    private RelativeLayout screenview;
    private TextView txt_load;
    private final static int REQUEST_CONTACTS_CODE = 100;
    private RelativeLayout rel_msg;

   /* private static String CALL_PERMISSIONS[] = new String[]{
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.CALL_PHONE
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        screenview = (RelativeLayout) inflater.inflate(R.layout.adres_chatactivity, null);
        relwrapp.addView(screenview);

        mActivity = this;
        ApplicationData.setChatActivity(mActivity);

        refreashFlag = true;
        sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        shf = new SharedPreferFile(ChatActivity.this);
        change_lang = new Change_lang(getApplicationContext());

        _teacher_id = sharedpref.getString("teacher_id", "");
        _teacher_image = sharedpref.getString("image", "");
        sender_jid = sharedpref.getString("jid", "");

        message = (EditText) findViewById(R.id.editText1);
        school = (TextView) findViewById(R.id.textView1);
        messagelistview = (ListView) findViewById(R.id.lstMessages);
        message_send = (RelativeLayout) findViewById(R.id.sendmessage);
        information = (LinearLayout) findViewById(R.id.information);
        txt_load = (TextView) findViewById(R.id.txt_load);
        information.setVisibility(View.GONE);
        school = (TextView) findViewById(R.id.textView1);
        rel_msg = (RelativeLayout) findViewById(R.id.rel_msg);
        Intent intent = getIntent();
        // message_id = intent.getStringExtra("message_id");
        if (intent.hasExtra("data")) {
            data = (Childbeans) intent.getSerializableExtra("data");

            sender_id = data.sender_id;
            sender_name = data.child_name;
            sender_image = data.child_image;
            parent_id = data.parent_id;
            parent_name = data.parent_name;
            class_name = data.class_name;
            receiver_jid = data.jid;
            phone = data.mobile1;

            ApplicationData.receiver_jid = receiver_jid;
        }

        firsttime = 1;

        school.setText(sender_name);
        shf = new SharedPreferFile(ChatActivity.this);

        DisplayMetrics matric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(matric);
        height = matric.heightPixels;
        width = matric.widthPixels;

        localarray = new ArrayList<Childbeans>();
        adapter = new MyAdapter(mActivity, recmsg, sender_image, _teacher_image, height, width, data,false);
        messagelistview.setAdapter(adapter);

        userprofile.setVisibility(View.VISIBLE);
        school.setVisibility(View.GONE);

        //set user image

        ApplicationData.setProfileImg(profileimage, ApplicationData.web_server_url + "uploads/" + sender_image, mActivity);
        name.setText(sender_name);
        subject.setText(class_name);

        if (GlobalConstrants.isWifiConnected(ChatActivity.this)) {
            if (recmsg != null && recmsg.size() > 0)
                recmsg.clear();

            if (XMPPMethod.getconnectivity() != null && XMPPMethod.getconnectivity().isAuthenticated()) {
                LoadingHistoryTask task = new LoadingHistoryTask(ChatActivity.this);
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


 //               if (adapter.getcopyText().length() > 0 && adapter.getcopyText().toString().trim() != null) {
//
//                    final PopupWindow popupWindow = new PopupWindow(ChatActivity.this);
//                    getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    View convertView;
//                    convertView = inflater.inflate(R.layout.item_copystring, null);
//
//                    convertView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//
//                    // some other visual settings for popup window
//                    popupWindow.setFocusable(true);
//
//                    LinearLayout lin_spinner = (LinearLayout) convertView.findViewById(R.id.lin_spinner);
//                    TextView txt_spin = (TextView) convertView.findViewById(R.id.txt_spin);
//
//                    lin_spinner.setBackgroundResource(R.drawable.white_backgroud_corner_rec);
//                    txt_spin.setTextColor(getResources().getColor(R.color.color_black));
//
//                    txt_spin.setText(getString(R.string.paste));
//                    popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
//                    // popupWindow.setBackgroundDrawable(null);
//                    popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//                    // popupWindow.setOutsideTouchable(true);
//                    // set the list view as pop up window content
//                    popupWindow.setOutsideTouchable(true);
//                    popupWindow.setContentView(convertView);
//
//                    int[] loc_int = new int[2];
//
//                    Rect location = new Rect();
//                    location.left = loc_int[0];
//                    location.top = loc_int[1];
//                    location.right = location.left + v.getWidth();
//                    location.bottom = location.top + v.getHeight();
//
//                    popupWindow.showAsDropDown(v, 0, -message.getMeasuredHeight() - 100);
//
//                    txt_spin.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String txtmsg = message.getText().toString().trim() != null ? message.getText().toString() : "";
//                            message.setText(txtmsg + (adapter.getcopyText().length() > 0 ? adapter.getcopyText() : ""));
//                            message.setSelection(message.getText().toString().length());
//                            popupWindow.dismiss();
//                            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                                @Override
//                                public void onDismiss() {
//                                }
//                            });
//                        }
//
//                    });
//                }
                return false;
            }
        });


//        message.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
//
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                return false;
//            }
//
//            public void onDestroyActionMode(ActionMode mode) {
//            }
//
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                return false;
//            }
//
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                return false;
//            }
//        });

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
                        if (GlobalConstrants.isWifiConnected(ChatActivity.this)) {
                            PagingHistory task = new PagingHistory(ChatActivity.this);
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

    protected void addtoist(String mess_age, String message_id) {
        // TODO Auto-generated method stub
        // String formattedDate = dateFormat.format(Calendar.getInstance().getTime());
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String fmt = sdf.format(cal.getTime());

        Childbeans childbeans = new Childbeans();
        childbeans.message_id = message_id;
        childbeans.user_id = "0";
        childbeans.name = "0";
        childbeans.image = "0";
        childbeans.message_body = mess_age;
        childbeans.child_marked_id = "0";
        childbeans.child_moblie = "";
        childbeans.created_at = fmt;
        childbeans.sender = "me";
        childbeans.sender_id_jid = Constant.getUserName(shf.getJidUser());
        childbeans.receiver_id_jid = Constant.getUserName(receiver_jid);
        childbeans.message_status = "Sent";
        childbeans.receiver_image = sender_image;
        childbeans.sender_id = _teacher_id;
        childbeans.parent_id = sender_id;
        //childbeans.chat_time=formattedDate;
        childbeans.receiver_name = sender_name;
        childbeans.parenttype = "";

        if (recmsg == null)
            recmsg = new ArrayList<>();

        recmsg.add(childbeans);
        Childbeans bean = childbeans;
        localarray.add(bean);

        adapter.updateReceiptsList(recmsg,false);
        adapter.notifyDataSetChanged();
        array_size = recmsg.size();

       /* DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(ChatActivity.this);
        db.inserthistory(childbeans);*/
        firsttime = 0;

        if (recmsg.size() == 0)
            return;
        if (!recmsg.get(recmsg.size() - 1).message_body.equals(lastMessage)) {
            lastMessage = recmsg.get(recmsg.size() - 1).message_body;
            messagelistview.setSelection(recmsg.size());
        }
    }


    private void getmessage(String login_url2) {
        // TODO Auto-generated method stub
        if (!GlobalConstrants.isWifiConnected(mActivity)) {
            checkdialog();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(mActivity);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, login_url2, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (pDialog != null && pDialog.isShowing())
                                pDialog.dismiss();
                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 1) {
                                JSONObject result = response.getJSONObject("result");
                                if (recmsg != null)
                                    recmsg.clear();
                                recmsg = new ArrayList<Childbeans>();
                                window_id = result.getString("window_id");
                                JSONArray reply = result.getJSONArray("reply");

                                for (int i = 0; reply.length() > i; i++) {
                                    JSONObject c = reply.getJSONObject(i);
                                    Childbeans childbeans = new Childbeans();
                                    childbeans.user_id = c.getString("from_id");
                                    childbeans.name = c.getString("name");
                                    // childbeans.image = c.getString("image");
                                    childbeans.message_body = c.getString("msg");
//									SimpleDateFormat dateFormat = new SimpleDateFormat(
//											"dd MMM h:mm a");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy,HH:mm");
                                    // SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
                                    String formattedDate = "";
                                    try {
                                        formattedDate = dateFormat.format(new Date(Long.valueOf(c.getString("time")) * 1000));
                                    } catch (Exception e) {
                                        formattedDate = "";
                                    }

//									childbeans.created_at = c.getString("time");
                                    childbeans.created_at = formattedDate;
                                    childbeans.sender = c.getString("sender");
                                    childbeans.child_moblie = c.getString("mobile");
                                    childbeans.child_marked_id = c.getString("Tread");
                                    recmsg.add(childbeans);
                                }

                                currSelection = messagelistview.getFirstVisiblePosition();
                                adapter.updateReceiptsList(recmsg,false);
                                if (recmsg.size() == 0)
                                    return;
                                if (!recmsg.get(recmsg.size() - 1).message_body.equals(lastMessage)) {
                                    lastMessage = recmsg.get(recmsg.size() - 1).message_body;
                                    messagelistview.setSelection(recmsg.size());
                                } else {
//											messagelistview.setSelection(currSelection + 1);
                                }
                                firsttime = 1;

                                checkdialog();
                            } else {
                                JSONObject result = response
                                        .getJSONObject("result");
                                window_id = result.getString("window_id");
                                checkdialog();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        refreashFlag = true;
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                checkdialog();
                refreashFlag = true;

                ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
            }
        });
        queue.add(jsObjRequest);
    }

    protected void checkdialog() {
        // TODO Auto-generated method stub
        if (pDialog != null && pDialog.isShowing()) {
            // is running
            pDialog.dismiss();
        }
    }

	/*protected void send_message(String sendmessage_url2) {
        if (!GlobalConstrants.isWifiConnected(mActivity)) {
			return;
		}
		RequestQueue queue = Volley.newRequestQueue(mActivity);
		// Log.e("URl", "" + sendmessage_url2);
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.GET, sendmessage_url2, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub

						try {

							String flag = response.getString("flag");

							if (Integer.parseInt(flag) == 1) {
								scheduleSendLocation();
							} else {
								// pDialog.dismiss();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						// pDialog.dismiss();
						ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
					}
				});
		queue.add(jsObjRequest);
	}*/

    @Override
    protected void onPause() {
        super.onPause();
        /*if ( mTimerEnableNotifyTyping != null ) {
            mTimerEnableNotifyTyping.cancel();
		}*/
        ApplicationData.setChatActivity(null);
        SharedPreferences.Editor editor = sharedpref.edit();
        String msg = message.getText().toString();
        if (msg.length() > 0) {
            editor.putString("t" + _teacher_id + "_" + sender_id + "_" + parent_id, msg);
            editor.commit();
        }

        //	myHandler.removeCallbacksAndMessages(null);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onStop() {
        super.onStop();
        ApplicationData.setChatActivity(null);
        message.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* Background_work.set_front_time();
        if (Background_work.check_layout_pincode()) {
            Intent i = new Intent(mActivity, PasswordActivity.class);
            startActivity(i);
        } else {*/
        ApplicationData.setChatActivity(mActivity);
        //}
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        String msg = sharedpref.getString("t" + _teacher_id + "_" + sender_id + "_" + parent_id, "");
        //   startService(new Intent(this, ReconnectionService.class));

    }

    @Override
    public void onBackPressed() {
        ApplicationData.setChatActivity(null);
        message.setText("");
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

              /*  if (newmesg.receiver_id_jid.contains("/"))
                    newmesg.receiver_id_jid = ApplicationData.getjid(newmesg.receiver_id_jid);

                if (newmesg.sender_id_jid.contains("/"))
                    newmesg.sender_id_jid = ApplicationData.getjid(newmesg.sender_id_jid);*/
                if (newmesg.receiver_id_jid.equalsIgnoreCase(Constant.getUserName(shf.getJidUser())) && !newmesg.typing
                        && newmesg.message != null && newmesg.message.length() > 0) {

                    subject.setText(class_name);
                    Childbeans childbeans = getdatafromservice(newmesg);
                    /*DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(ChatActivity.this);
                    db.inserthistory(childbeans);

                    String query = "select * from chat_msg_history where (Sender_jid=" + "'" + Constant.getUserName(shf.getJidUser()) + "'" + " AND Receiver_jid='" + Constant.getUserName(receiver_jid) + "')" +
                            " OR (Sender_jid='" + Constant.getUserName(receiver_jid) + "' AND Receiver_jid='" + Constant.getUserName(shf.getJidUser()) + "')";

                    //String query = "select * from chat_msg_history where Message_id="+'"'+newmesg.message_id+'"';
                    ArrayList<HashMap<String, String>> maplist = db.selectRecordsFromDBList(query, null);

                    if (maplist != null && maplist.size() > 0)
                        gethistory(maplist);*/


                } else if (newmesg.receiver_id_jid.equalsIgnoreCase(Constant.getUserName(shf.getJidUser()))) {
                    if (newmesg.typing)
                        subject.setText(getString(R.string.typing));
                    else
                        subject.setText(class_name);
                }
            } else if (data.getAction().equals(Constant.CUSTOM_INTENT_XMPP_MESSAGE_DELIVERED)) {
                Childbeans newmesg = (Childbeans) data.getSerializableExtra("newMessage");

                if (newmesg.receiver_id_jid.equals(Constant.getUserName(sender_jid).toUpperCase())) {
                    Childbeans childbeans = new Childbeans();
                    childbeans.message_id = newmesg.message_id;
                    childbeans.receiver_id_jid = newmesg.sender_id_jid;
                    childbeans.sender_id_jid = newmesg.receiver_id_jid;
                    childbeans.sender_id = _teacher_id;
                    childbeans.message_status = "Received";

                    DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(ChatActivity.this);
                    db.updatestatus(ChatActivity.this, childbeans);
                    db.insertdeliverystatus(childbeans);

                    if (recmsg != null && recmsg.size() > 0) {
                        for (int i = 0; i < recmsg.size(); i++) {
                            if (newmesg.message_id != null && newmesg.message_id.length() > 0 && recmsg.get(i).message_id != null) {
                                if (recmsg.get(i).message_id.equalsIgnoreCase(newmesg.message_id)) {
                                    recmsg.get(i).message_status = childbeans.message_status;
                                    break;
                                }
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
        childbeans.image = "0";
        childbeans.message_body = newmesg.message;
        childbeans.child_marked_id = "0";
        childbeans.created_at = fmt;
        childbeans.sender = "from";
        childbeans.sender_id_jid = newmesg.sender_id_jid;
        childbeans.receiver_id_jid = newmesg.receiver_id_jid;
        childbeans.message_status = "Received";
        childbeans.receiver_image = sender_image;
        childbeans.sender_id = _teacher_id;
        childbeans.parent_id = sender_id;
        childbeans.receiver_name = newmesg.receiver_name;
        childbeans.child_moblie = "-------";
        childbeans.parenttype = newmesg.parenttype;

        setmessagehistory(childbeans);
        /*if (duplicatemessage != null && duplicatemessage.size() > 0) {
            for (int msgid = 0; msgid < duplicatemessage.size(); msgid++) {
                if (duplicatemessage.get(msgid).message_id.equalsIgnoreCase(newmesg.message_id)) {
                    messageavailable = true;
                    break;
                }
            }
        } else if (duplicatemessage != null) {
            duplicatemessage.add(childbeans);
            messageavailable = false;
        }

        if (!messageavailable) {
            duplicatemessage.add(childbeans);

        }*/

        return childbeans;
    }

    private void setmessagehistory(Childbeans messagelist) {
        recmsg.add(messagelist);
        Childbeans bean = messagelist;
        localarray.add(messagelist);

        adapter.updateReceiptsList(recmsg,false);
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
        if (!recmsg.get(recmsg.size() - 1).message_body.equals(lastMessage)) {
            lastMessage = recmsg.get(recmsg.size() - 1).message_body;
            if (messagelistview.getLastVisiblePosition() == recmsg.size() - 2)
                messagelistview.setSelection(recmsg.size());
        }

        array_size = recmsg.size();
        firsttime = 0;
    }

    private void typingstastus(final int previouslengh) {

        if (previouslengh > 0) {
            istyping = true;
            XMPPMethod.typingstatus(ChatActivity.this, receiver_jid, istyping);

            myrunnable1 = new Runnable() {
                @Override
                public void run() {
                    if (previouslengh == message.getText().toString().length()) {
                        istyping = false;
                        XMPPMethod.typingstatus(ChatActivity.this, receiver_jid, istyping);
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
        message.setText("");


        sender_id = null;
        sender_name = null;
        sender_image = null;
        parent_id = null;
        parent_name = null;
        class_name = null;
        getmessage_url = null;
        _teacher_id = null;
        sendmessage_url = null;
        _teacher_image = null;
        window_id = null;
        receiver_jid = null;
        phone = null;

        messagelistview = null;
        myHandler = null;
        pDialog = null;
        adapter = null;
        message = null;
        message_send = null;
        school = null;
        change_lang = null;
        mTimerEnableNotifyTyping = null;
        recmsg = null;
        localarray = null;
        duplicatemessage = null;
        mActivity = null;
        sharedpref = null;
        lastMessage = null;
        receiver = null;
        shf = null;
        information = null;
        sender_jid = null;
        handler = null;
        handler1 = null;
        myrunnable = null;
        myrunnable1 = null;
        data = null;
        inflater = null;
        screenview = null;
        txt_load = null;
        rel_msg = null;

        System.gc();

    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public String getParentId() {
        return parent_id;
    }

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
                if (recmsg.size() > 0)
                    recmsg.clear();
                recmsg = XMPPMethod.loadHistory(context, receiver_jid, true);
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
                adapter.updateReceiptsList(recmsg,false);
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
                nexthistory = XMPPMethod.fetchbeforehistory(ChatActivity.this, receiver_jid, true);
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
                adapter.updateReceiptsList(recmsg,false);
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
}
