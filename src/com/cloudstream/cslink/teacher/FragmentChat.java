package com.cloudstream.cslink.teacher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.adapter.teacher.Childbeans;
import com.adapter.teacher.ChildrenListAdapter_message;
import com.adapter.teacher.SpinnerListAdapter;
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
import com.db.teacher.DatabaseHelper;
import com.xmpp.teacher.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentChat extends ActivityHeader {

    private ListView _lstStudent;
    private MainProgress pDialog;

    String _teacher_id;
    private String noti_from_id = "";
    private String noti_kid_id = "";
    UpdaterBroadcastReceiver updateBroadcaseReceiver = null;
    TextView txtGrade;
    Spinner txtClass;
    RelativeLayout lytGrade, lytClass;

    String[] grades, classes;
    String school_id = "";
    String grade_id = "";
    String class_id = "";

    ArrayList<Childbeans> allStudentList = new ArrayList<Childbeans>();
    ArrayList<Childbeans> allStudentListOrg = null;
    ArrayList<Childbeans> allArrayList = null;
    ArrayList<Childbeans> arrayGrade = null;
    ArrayList<Childbeans> arrayClass = null;
    ChildrenListAdapter_message cAdapter;

    private Dialog myalertDialog = null;

    Activity mActivity;
    TextView txtSearch;
    View lytSearch;
    private boolean isLoading = false;
    private String jid;
    private LayoutInflater inflater;
    private View rootView;
    private SharedPreferences sharedpref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;

        inflater = getLayoutInflater();
        rootView = inflater.inflate(R.layout.adres_message_fragment, null);
        relwrapp.addView(rootView);

        lytSearch = (View) findViewById(R.id.lytSearch);
        txtSearch = (EditText) findViewById(R.id.txtSearch);
        txtClass = (Spinner) findViewById(R.id.txtClass);
        //txtGrade = (TextView) rootView.findViewById(R.id.txtGrade);
        //lytGrade = (RelativeLayout) rootView.findViewById(R.id.lytGrade);
        lytClass = (RelativeLayout) findViewById(R.id.lytClass);
        _lstStudent = (ListView) findViewById(R.id.lstStudent);

        sharedpref = this.getSharedPreferences(Constant.USER_FILENAME, 0);
        school_id = sharedpref.getString("school_id", "");
        _teacher_id = sharedpref.getString("teacher_id", "");

        if (getIntent().hasExtra("noti_kid_id")) {
            noti_kid_id = getIntent().getExtras().getString("noti_kid_id", "");
        }

        //set Title
        showheadermenu(FragmentChat.this, getString(R.string.str_message), R.color.color_blue_p, false);

        //  new chatclass().execute();

        //get device height and width
        DisplayMetrics display = new DisplayMetrics();
        FragmentChat.this.getWindowManager().getDefaultDisplay().getMetrics(display);
        int deviceWidth = (display.widthPixels / 2) - 35;
        int deviceHeight = display.heightPixels;
        //  txtClass.setDropDownWidth(deviceWidth);

        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        txtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                setlist_toadapter_without_badge();
            }
        });


        ApplicationData.hideKeyboardForFocusedView(mActivity);

        InputMethodManager inputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);

        txtClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (classes == null) {
                    ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_class), false);
                } else {
                    if (classes.length > 0) {
                        // showListDlg("class");
                        class_id = arrayClass.get(position).class_id;
                        initStudent();
                    } else {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_class), false);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        _lstStudent.setOnItemClickListener(new OnItemClickListener() {

            @SuppressLint("LongLogTag")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                //if (!isLoading)
                showChatActivity(position);
            }
        });

        IntentFilter filter = new IntentFilter(ApplicationData.BROADCAST_CHAT);
        updateBroadcaseReceiver = new UpdaterBroadcastReceiver();
        registerReceiver(updateBroadcaseReceiver, filter);

        loadClassList();

        cAdapter = new ChildrenListAdapter_message(FragmentChat.this, allStudentList);
        _lstStudent.setAdapter(cAdapter);
        cAdapter.updateReceiptsList(allStudentList);

    }

    private void setlist_toadapter_without_badge() {
        String searchStr = txtSearch.getText().toString();
        if (allStudentListOrg == null || allStudentListOrg.size() == 0) {
            return;
        }
        if (allStudentList != null)
            allStudentList.clear();
        else
            allStudentList = new ArrayList<Childbeans>();
        for (int i = 0; i < allStudentListOrg.size(); i++) {
            if (allStudentListOrg.get(i).child_name.toLowerCase().contains(searchStr.toLowerCase())) {
                allStudentList.add(allStudentListOrg.get(i));
            }
        }

        if (allStudentList != null && allStudentList.size() > 0) {
            cAdapter.updateReceiptsList(allStudentList);
        }
    }

    private void showChatActivity(int position) {
        Intent in = new Intent(FragmentChat.this, ChatActivity.class);
        in.putExtra("data", allStudentList.get(position));
        in.putExtra("teacher_id", _teacher_id);
        noti_kid_id = "";
        if (allStudentList.get(position).parent_id != null && !allStudentList.get(position).parent_id.isEmpty() && !allStudentList.get(position).parent_id.equals("null")) {
            isLoading = true;
            //ApplicationData.showAppBadgeDec(mActivity, allStudentList.get(position).badge);
            DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(FragmentChat.this);
            db.clearchatbadge(_teacher_id, Constant.getUserName(allStudentList.get(position).jid).toUpperCase());
            startActivity(in);
        } else {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_parent_atmoment), false);
        }
    }


    public void setlist_toadapter() {
        isLoading = true;
        // TODO Auto-generated method stub

        final String searchStr = txtSearch.getText().toString();

		/*if (allStudentListOrg == null || allStudentListOrg.size() ==0)
			return;*/

        if (allStudentListOrg != null && allStudentListOrg.size() > 0) {

            String url = ApplicationData.getlanguageAndApi(FragmentChat.this, ConstantApi.GET_UNREADMSG) + "id=" + _teacher_id;
            RequestQueue queue = Volley.newRequestQueue(FragmentChat.this);

            // Log.e("URl", "" + login_url2);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            if (allStudentList != null)
                                allStudentList.clear();
                            else
                                allStudentList = new ArrayList<Childbeans>();

                            for (int i = 0; allStudentListOrg != null && i < allStudentListOrg.size(); i++) {
                                if (allStudentListOrg.get(i).child_name.toLowerCase().contains(searchStr.toLowerCase())) {
                                    allStudentList.add(allStudentListOrg.get(i));
                                }
                            }


							/*try {
								ArrayList<Childbeans> badgeList = new ArrayList<Childbeans>();
								String flag = response.getString("flag");
								if (Integer.parseInt(flag) == 1) {
									JSONArray result = response.getJSONArray("Msg");
									for (int i = 0; result.length() > i; i++) {
										JSONObject c = result.getJSONObject(i);
										Childbeans childbeans = new Childbeans();
										childbeans.user_id = c.getString("user_id");
										childbeans.badge = Integer.valueOf(c.getString("msg"));
										badgeList.add(childbeans);
									}
								} else {

								}

								int noti_position = 0;
								for (int i = 0; allStudentList.size() > i; i++) {
									Childbeans childbeans = allStudentList.get(i);
									//childbeans.user_id = "1";
									childbeans.badge = 0;

									for (int j = 0; j < badgeList.size(); j++) {
										if (badgeList.get(j).user_id.equals(childbeans.sender_id)) {
											childbeans.badge = badgeList.get(j).badge;
											break;
										}
									}

									if (noti_kid_id != null && !noti_kid_id.isEmpty() && !noti_kid_id.equals("0") && noti_kid_id.equals(childbeans.sender_id)) {
										noti_position = i;
										allStudentList.set(i, childbeans);
									} else {
										if (childbeans.badge == 0) {
											allStudentList.set(i, childbeans);
										} else {
											allStudentList.remove(i);
											allStudentList.add(0, childbeans);
										}
									}
								}
//								cAdapter = new ChildrenListAdapter(FragmentChat.this, allStudentList);
//								_lstStudent.setAdapter(cAdapter);
								if (noti_kid_id != null && !noti_kid_id.isEmpty() && !noti_kid_id.equals("0")) {
									showChatActivity(noti_position);
									noti_kid_id = "";
								}
							} catch (Exception e) {
								e.printStackTrace();
								for (int i = 0; allStudentList.size() > i; i++) {
									Childbeans childbeans = allStudentList.get(i);
									childbeans.badge = 0;
									allStudentList.set(i, childbeans);
								}
//								cAdapter = new ChildrenListAdapter(FragmentChat.this, allStudentList);
							}

							_lstStudent.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
								@Override
								public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
									_lstStudent.removeOnLayoutChangeListener(this);
									isLoading = false;
								}
							});*/

//							cAdapter.notifyDataSetChanged();
//							_lstStudent.setAdapter(cAdapter);
                            txtSearch.setVisibility(View.VISIBLE);
                            ApplicationData.hideKeyboardForFocusedView(mActivity);

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    ApplicationData.showToast(FragmentChat.this, getResources().getString(R.string.str_network_error),
                            false);
                    isLoading = false;
                }
            });

            queue.add(jsObjRequest);

        } else {
            allStudentList.clear();
            cAdapter.updateReceiptsList(allStudentList);
//			cAdapter.notifyDataSetChanged();
			/*cAdapter = new ChildrenListAdapter(FragmentChat.this, allStudentList);
			_lstStudent.setAdapter(cAdapter);*/
            isLoading = false;
        }
    }

    private void loadClassList() {
        String url = ApplicationData.getlanguageAndApi(FragmentChat.this, ConstantApi.GET_CLASS_TEACHER)
                + "teacher_id=" + _teacher_id;
        if (!GlobalConstrants.isWifiConnected(mActivity)) {
            return;
        }
        if (pDialog == null)
            pDialog = new MainProgress(mActivity);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(mActivity);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 1) {
                                JSONObject classes = response.getJSONObject("classes");
                                JSONArray allClasses = classes.getJSONArray("classes");
                                if (allArrayList != null)
                                    allArrayList.clear();
                                allArrayList = new ArrayList<Childbeans>();

                                for (int i = 0; allClasses.length() > i; i++) {
                                    JSONObject c = allClasses.getJSONObject(i);
                                    Childbeans childbeans = new Childbeans();
                                    childbeans.school_id = c.getString("school_id");
                                    childbeans.class_id = c.getString("class_id");
                                    childbeans.class_name = c.getString("class_name");
                                    childbeans.name = c.getString("grade");
                                    allArrayList.add(childbeans);
                                }
                                initGrade();
                            } else {
                                pDialog.dismiss();
                                if (response.has("msg")) {
                                    String msg = response.getString("msg");
                                    ApplicationData.showToast(mActivity, msg, false);
                                } else {
                                    ApplicationData.showToast(mActivity, R.string.msg_no_class, false);
                                }
                            }
                        } catch (Exception e) {
                            pDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
            }
        });
        queue.add(jsObjRequest);
    }

    private void initGrade() {
        if (arrayGrade != null)
            arrayGrade.clear();
        arrayGrade = new ArrayList<Childbeans>();
        //txtGrade.setText(getResources().getString(R.string.str_all));
        grade_id = "";
        grades = null;
        //txtClass.setText(getResources().getString(R.string.select_class));
        class_id = "";
        classes = null;

        if (school_id.length() == 0) {
            initStudent();
            return;
        } else {
            Childbeans blankBean = new Childbeans();
            blankBean.name = getResources().getString(R.string.select_all);

            arrayGrade.add(blankBean);
            String tmpGrade = "";
            for (int i = 0; i < allArrayList.size(); i++) {
                if (school_id.equals(allArrayList.get(i).school_id) && !tmpGrade.equals(allArrayList.get(i).name)) {
                    tmpGrade = allArrayList.get(i).name;
                    arrayGrade.add(allArrayList.get(i));
                }
            }

            grades = new String[arrayGrade.size()];
            for (int j = 0; arrayGrade.size() > j; j++) {
                grades[j] = new String(arrayGrade.get(j).name);
            }


            initClass();
        }
    }

    private void initClass() {
        if (arrayClass != null)
            arrayClass.clear();
        arrayClass = new ArrayList<Childbeans>();
        //	txtClass.setText(getResources().getString(R.string.select_class));
        class_id = "";
        classes = null;

        if (school_id.length() == 0) {
            initStudent();
            return;
        } else {
            Childbeans blankBean = new Childbeans();
            blankBean.class_id = "";
            blankBean.class_name = getResources().getString(R.string.select_class);
            arrayClass.add(blankBean);
            for (int i = 0; i < allArrayList.size(); i++) {
                if (school_id.equals(allArrayList.get(i).school_id)) {
                    if (grade_id.length() == 0) {
                        arrayClass.add(allArrayList.get(i));
                    } else if (grade_id.equals(allArrayList.get(i).name)) {
                        arrayClass.add(allArrayList.get(i));
                    }
                }
            }
            classes = new String[arrayClass.size()];

            for (int j = 0; arrayClass.size() > j; j++) {
                classes[j] = new String(arrayClass.get(j).class_name);
            }

            /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(FragmentChat.this,R.layout.spinner_childlist_item,
                    R.id.text1,classes);*/
            SpinnerListAdapter adapter = new SpinnerListAdapter(FragmentChat.this, classes);
            txtClass.setAdapter(adapter);

            initStudent();
        }
    }

    private void initStudent() {
        if (allStudentListOrg != null)
            allStudentListOrg.clear();
        if (allStudentList != null)
            allStudentList.clear();
        allStudentListOrg = new ArrayList<Childbeans>();
        allStudentList = new ArrayList<Childbeans>();

       /* if (grade_id.length() != 0 || class_id.length() != 0) {
            loadStudentList();
        } else */{
            loadStudentList();
        }

    }

    private void loadStudentList() {
        String url = ApplicationData.getlanguageAndApi(FragmentChat.this, ConstantApi.GET_STUDENT) +
                "school_id=" + school_id + "&grade_id=" + grade_id + "&class_id=" + class_id + "&teacher_id=" + _teacher_id;


        if (!GlobalConstrants.isWifiConnected(mActivity)) {
            return;
        }
        if (pDialog == null)
            pDialog = new MainProgress(mActivity);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        if (!pDialog.isShowing())
            pDialog.show();

        if (allStudentListOrg != null)
            allStudentListOrg.clear();
        if (allStudentList != null)
            allStudentList.clear();
        allStudentListOrg = new ArrayList<Childbeans>();
        allStudentList = new ArrayList<Childbeans>();

        RequestQueue queue = Volley.newRequestQueue(mActivity);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String flag = response.getString("flag");

                            if (Integer.parseInt(flag) == 1) {
                                JSONArray allStudents = response.getJSONArray("allStudents");
                                allStudentListOrg = new ArrayList<>();
                                allStudentList = new ArrayList<>();

                                for (int i = 0; i < allStudents.length(); i++) {
                                    Childbeans childbeans = new Childbeans();
                                    Childbeans childbeansOrg = new Childbeans();
                                    JSONObject c = allStudents.getJSONObject(i);
                                    if (c.getString("parent_id") == null || c.getString("parent_id").equals("null") || c.getString("parent_id").isEmpty())
                                        continue;
                                    childbeans.sender_id = c.getString("user_id");
                                    childbeans.child_name = c.getString("name");
                                    childbeans.child_image = c.getString("image");
                                    childbeans.parent_id = c.getString("parent_id");
                                    childbeans.parent_name = c.getString("parent_name");
                                    childbeans.class_name = c.getString("class_name");
                                    childbeans.child_age = ApplicationData.convertToNorweiDateyeartime(c.getString("birthday"), mActivity);
                                    childbeans.school_name = c.getString("school_name");
                                    childbeans.jid = c.getString("jid");
                                    childbeans.mobile1 = c.getString("parent_phone");
                                    childbeans.parent2name = c.getString("parent2name");
                                    childbeans.parent2mobile = c.getString("parent2mobile");
                                    childbeans.parent3name = c.getString("contactname");
                                    childbeans.parent3mobile = c.getString("contactmobilem");

                                    childbeansOrg.sender_id = c.getString("user_id");
                                    childbeansOrg.child_name = c.getString("name");
                                    childbeansOrg.child_image = c.getString("image");
                                    childbeansOrg.parent_id = c.getString("parent_id");
                                    childbeansOrg.parent_name = c.getString("parent_name");
                                    childbeansOrg.class_name = c.getString("class_name");
                                    childbeansOrg.child_age = ApplicationData.convertToNorweiDateyeartime(c.getString("birthday"), mActivity);
                                    childbeansOrg.school_name = c.getString("school_name");
                                    childbeansOrg.jid = c.getString("jid");
                                    childbeansOrg.mobile1 = c.getString("parent_phone");
                                    childbeansOrg.parent2name = c.getString("parent2name");
                                    childbeansOrg.parent2mobile = c.getString("parent2mobile");
                                    childbeansOrg.parent3name = c.getString("contactname");
                                    childbeansOrg.parent3mobile = c.getString("contactmobilem");

                                    //  childbeansOrg.jid_pwd = c.getString("jid_key");
                                    allStudentList.add(childbeans);
                                    allStudentListOrg.add(childbeansOrg);
                                }
                                setmessagebadge();
                                cAdapter.updateReceiptsList(allStudentList);

                              //  setlist_toadapter();
                            } else {
                                if (response.has("msg")) {
                                    String msg = response.getString("msg");
                                    ApplicationData.showToast(mActivity, msg, false);
                                } else
                                    ApplicationData.showToast(mActivity, R.string.msg_no_students, false);
                            }
                            pDialog.dismiss();
                        } catch (Exception e) {
                            pDialog.dismiss();
                            e.printStackTrace();
                        }
						/*cAdapter = new ChildrenListAdapter(mActivity, allStudentList);
						_lstStudent.setAdapter(cAdapter);*/
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
                cAdapter.updateReceiptsList(allStudentList);
				/*cAdapter = new ChildrenListAdapter(mActivity, allStudentList);
				_lstStudent.setAdapter(cAdapter);*/
            }
        });
        queue.add(jsObjRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        isLoading = false;
        ApplicationData.setMainActivity(mActivity);
        ApplicationData.setChatActivity(null);
        setmessagebadge();
        View view = getCurrentFocus();
        if (view!=null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        ApplicationData.ispincode = true;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updateBroadcaseReceiver != null) {
            FragmentChat.this.unregisterReceiver(updateBroadcaseReceiver);
        }

         _lstStudent=null;
        pDialog=null;
        _teacher_id=null;
        noti_from_id = null;
        noti_kid_id = null;
        updateBroadcaseReceiver = null;
        txtGrade=null;
        txtClass=null;
        lytGrade=null; lytClass=null;
        grades=null; classes=null;
        school_id =null;
        grade_id =null;
        class_id = null;
        allStudentList = null;
        allStudentListOrg = null;
        allArrayList = null;
        arrayGrade = null;
        arrayClass = null;
        cAdapter=null;
        myalertDialog = null;
        mActivity=null;
        txtSearch=null;
        lytSearch=null;
        jid=null;
        inflater=null;
        rootView=null;
        sharedpref=null;

        System.gc();
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

    public void onLowMemory() {
        super.onLowMemory();
    }

    private void setmessagebadge() {

        DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(FragmentChat.this);
        String query = "select * from chat_msg_badge where User_id= " + '"' + _teacher_id + '"'+" Order by Created ASC";// + "And Message_type='teacher-parent' Order by Created ASC";
        ArrayList<HashMap<String, String>> messagelist = db.selectRecordsFromDBList(query, null);
        if (messagelist != null) {

            for (HashMap<String, String> badgemap : messagelist) {
                if (allStudentList.size() > 0) {
                    for (int i = 0; i < allStudentList.size(); i++) {
                        if (Constant.getUserName(allStudentList.get(i).jid).equalsIgnoreCase(badgemap.get("Receiver_jid"))) {
                            allStudentList.get(i).badge = Integer.parseInt(badgemap.get("Badge"));
                            Childbeans chbean = allStudentList.get(i);
                            allStudentList.remove(i);
                            allStudentList.add(0, chbean);
                            break;
                        }
                    }
                }
            }
            if (allStudentList.size() > 0)
                cAdapter.updateReceiptsList(allStudentList);
        }

        db.clearallchatbadge(_teacher_id, "teacher-parent");
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
    protected void onPause() {
        super.onPause();
        ApplicationData.ispincode = false;
    }


}

