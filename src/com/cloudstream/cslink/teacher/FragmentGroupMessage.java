package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.adapter.teacher.AttendanceSpinnerListAdapter;
import com.adapter.teacher.Childbeans;
import com.adapter.teacher.ClassListAdapter;
import com.adapter.teacher.GroupMessageHistoryAdapter;
import com.android.volley.DefaultRetryPolicy;
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
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;
import com.xmpp.teacher.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class FragmentGroupMessage extends ActivityHeader implements AsyncTaskCompleteListener<String> {

    private ListView lstHistory;
    private ExpandableListView lstClass;
    MainProgress pDialog;

    private String teacher_id, school_id, grade_id;
    private CheckBox chkAll;

    View lytHistory, lytGrade, btnSend;

    ArrayList<Childbeans> arrayHistory = null;
    ArrayList<Childbeans> arrayGrade = null;
    ArrayList<Childbeans> arrayClass = null;
    ArrayList<Childbeans> arrayAllClasses = null;

    String[] grades, classes;

    ArrayList arrList;
    String class_ids = "";

    TextView txtHistory;
    Spinner txtGrade;
    EditText txtMessage;

    private Dialog myalertDialog = null;

    Activity mActivity;
    private Dialog dlg;
    SharedPreferences sharedpref;
    private AttendanceSpinnerListAdapter adapter;
    private ClassListAdapter cls_adapter;
    private boolean isselected = false;
    private ArrayList<Childbeans> allStudentList;
    private int height, relsendheight;
    private LinearLayout relSendMessage;
    private LayoutInflater inflater;
    private View rootView;

    public FragmentGroupMessage() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;

        hideKeyboardForFocusedView(this);

        inflater = getLayoutInflater();
        rootView = inflater.inflate(R.layout.adres_groupmessage_fragment1, null);
        relwrapp.addView(rootView);

        sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        teacher_id = sharedpref.getString("teacher_id", "");
        school_id = sharedpref.getString("school_id", "");

        arrList = new ArrayList();
        txtHistory = (TextView) findViewById(R.id.txtHistory);
        lstClass = (ExpandableListView) findViewById(R.id.lstClass);
        txtGrade = (Spinner) findViewById(R.id.txtGrade);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        btnSend = (View) findViewById(R.id.btnSend);
        lytHistory = (View) findViewById(R.id.lytHistory);
        lytGrade = (View) findViewById(R.id.lytGrade);
        chkAll = (CheckBox) findViewById(R.id.chkAll);
        relSendMessage = (LinearLayout) findViewById(R.id.relSendMessage);

        //set Title
        showheadermenu(FragmentGroupMessage.this, getString(R.string.gp), R.color.light_green, false);

        //get height of screen
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;


        chkAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox chkbox = (CheckBox) v.findViewById(R.id.chkAll);
                if (chkbox.isChecked())
                    isselected = true;
                else
                    isselected = false;

                cls_adapter.update(isselected, null);
                cls_adapter.notifyDataSetChanged();

            }
        });
        chkAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


            }
        });

        txtGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (grades == null) {
                    ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_grade), false);
                } else {
                    if (grades.length > 0) {
                        grade_id = arrayGrade.get(position).name;
                        if (grade_id.equals(getResources().getString(R.string.str_all))) {
                            grade_id = "";
                        }
                        // if (!grade_id.equalsIgnoreCase(""))
                        initClass();

                    } else {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_grade), false);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lytHistory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (arrayHistory == null || arrayHistory.size() == 0) {
                    ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_history), false);
                } else {
                    showGroupMessageHistory();
                }
            }

        });


        btnSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String student_ids = "";

                if (txtMessage.getText().length() == 0) {
                    ApplicationData.showToast(mActivity, R.string.str_input_chatmessage, false);
                } else {
                    if (!chkAll.isChecked()) {
                        ArrayList<String> both = cls_adapter.getstudentid(FragmentGroupMessage.this, classes, arrayClass);
                        if (both.size() > 0) {
                            if (!both.get(0).equals("0"))
                                student_ids = both.get(0);

                            class_ids = both.get(1).startsWith(",") ? both.get(1).replace(",", "") : both.get(1);
                        }
                    } else {
                        for (int i = 0; i < arrayAllClasses.size(); i++)
                            if (i == 0)
                                class_ids = arrayAllClasses.get(i).class_id;
                            else
                                class_ids = class_ids + "," + arrayAllClasses.get(i).class_id;
                    }

                    if (class_ids.length() == 0) {
                        ApplicationData.showToast(mActivity, R.string.msg_no_selected_class, false);
                    } else if (student_ids.equals("") && class_ids.length() == 0) {
                        ApplicationData.showToast(mActivity, R.string.select_student, false);
                    } else {

                        String txt = "";
                        try {
                            txt = URLEncoder.encode(txtMessage.getText().toString().trim(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String send_url = ApplicationData.getlanguageAndApi(FragmentGroupMessage.this, ConstantApi.CREATE_NEW_MSG_TEACHER)
                                + "class_ids="
                                + class_ids + "&teacher_id=" + teacher_id
                                + "&message=" + txt + "&student_ids=" + student_ids;

                       // sendMessage(txt,student_ids);
                        sendMessage(send_url);
                    }
                }
            }
        });

        // .............ListView................//

        lstClass.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

       /* lstClass.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {


            *//*    if (arrList.contains(arg2)) {
                    arrList.remove((Integer) arg2);
                } else {
                    arrList.add(arg2);
                }

                Collections.sort(arrList);
                class_ids = "";
                for (int i = 0; i < arrList.size(); i++) {
                    int inx = Integer.valueOf(arrList.get(i).toString());
                    class_ids += arrayClass.get(inx).class_id + ",";
                }*//*
            }

        });
*/
        loadClasses(ApplicationData.getlanguageAndApi(FragmentGroupMessage.this, ConstantApi.GET_CLASS_TEACHER)
                + "teacher_id=" + teacher_id);

    }

    private void loadClasses(String url) {
        // TODO Auto-generated method stub
        if (!GlobalConstrants.isWifiConnected(FragmentGroupMessage.this)) {
            return;
        }
        if (pDialog == null)
            pDialog = new MainProgress(FragmentGroupMessage.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(FragmentGroupMessage.this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String flag = response.getString("flag");
                            //pDialog.dismiss();
                            if (Integer.parseInt(flag) == 1) {
                                JSONObject classes = response.getJSONObject("classes");
                                JSONArray allClasses = classes.getJSONArray("classes");
                                if (arrayAllClasses != null)
                                    arrayAllClasses.clear();
                                arrayAllClasses = new ArrayList<Childbeans>();

                                for (int i = 0; allClasses.length() > i; i++) {
                                    JSONObject c = allClasses.getJSONObject(i);
                                    Childbeans childbeans = new Childbeans();
                                    childbeans.school_id = c.getString("school_id");
                                    childbeans.class_id = c.getString("class_id");
                                    childbeans.class_name = c.getString("class_name");
                                    childbeans.name = c.getString("grade");
                                    arrayAllClasses.add(childbeans);
                                }
                                initGrade();
                                loadHistory(ApplicationData.getlanguageAndApi(FragmentGroupMessage.this, ConstantApi.GET_GROUP_MSG_TEACHER) + "teacher_id=" + teacher_id + "&is_incharge=" + "");
                            } else {
                                pDialog.dismiss();
                                ApplicationData.showToast(mActivity, R.string.msg_no_class, false);
                            }
                        } catch (Exception e) {
                            pDialog.dismiss();
                            ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
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

    private void loadHistory(String url) {
        // TODO Auto-generated method stub

        if (pDialog == null)
            pDialog = new MainProgress(FragmentGroupMessage.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));

        if (!pDialog.isShowing())
            pDialog.show();

        if (!GlobalConstrants.isWifiConnected(FragmentGroupMessage.this)) {
            pDialog.dismiss();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(FragmentGroupMessage.this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String flag = response.getString("flag");
                            pDialog.dismiss();
                            if (Integer.parseInt(flag) == 1) {
                                JSONObject messages = response.getJSONObject("messages");
                                JSONArray received = messages.getJSONArray("received");

                                //pri_message_id, created_at, message_subject, message_desc, class_ids, class_names
                                if (arrayHistory != null)
                                    arrayHistory.clear();
                                arrayHistory = new ArrayList<Childbeans>();

                                for (int i = 0; received.length() > i; i++) {
                                    JSONObject c = received.getJSONObject(i);
                                    Childbeans childbeans = new Childbeans();
                                    childbeans.message_id = c.getString("pri_message_id");
                                    childbeans.message_body = c.getString("message_subject");
                                    childbeans.message_desc = c.getString("message_desc");
                                    childbeans.class_id = c.getString("class_ids");
                                    childbeans.class_name = c.getString("class_names");
                                    childbeans.created_at = c.getString("created_at");
                                    arrayHistory.add(childbeans);
                                }
                                txtMessage.setText("");
                            } else {
                                ApplicationData.showToast(mActivity, R.string.msg_no_history, false);
                            }
                        } catch (Exception e) {
                            ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
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
        //   txtGrade.setText(getResources().getString(R.string.str_all));
        grade_id = "";
        grades = null;

        Childbeans blankBean = new Childbeans();
        blankBean.name = getResources().getString(R.string.str_all);
        arrayGrade.add(blankBean);

        String tmpGrade = "";
        for (int i = 0; i < arrayAllClasses.size(); i++) {
            if (school_id.equals(arrayAllClasses.get(i).school_id) && !tmpGrade.equals(arrayAllClasses.get(i).name)) {
                tmpGrade = arrayAllClasses.get(i).name;
                arrayGrade.add(arrayAllClasses.get(i));
            }
        }

        grades = new String[arrayGrade.size()];
        for (int j = 0; arrayGrade.size() > j; j++) {
            grades[j] = new String(arrayGrade.get(j).name);
        }
        adapter = new AttendanceSpinnerListAdapter(mActivity, grades);
        txtGrade.setAdapter(adapter);

        initClass();
    }

    private void initClass() {
        if (arrayClass != null)
            arrayClass.clear();
        arrayClass = new ArrayList<Childbeans>();
        classes = null;

        for (int i = 0; i < arrayAllClasses.size(); i++) {
            if (school_id.equals(arrayAllClasses.get(i).school_id)) {
                if (grade_id.length() == 0) {
                    arrayClass.add(arrayAllClasses.get(i));
                } else if (grade_id.equals(arrayAllClasses.get(i).name)) {
                    arrayClass.add(arrayAllClasses.get(i));
                }
            }
        }
        classes = new String[arrayClass.size()];
        for (int j = 0; arrayClass.size() > j; j++) {
            classes[j] = new String(arrayClass.get(j).class_name);
        }

        //  uncheckAll();
        callstudent();

    }

    private void callstudent() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("school_id", arrayAllClasses.get(0).school_id);

        ETechAsyncTask task = new ETechAsyncTask(FragmentGroupMessage.this, this, ConstantApi.GET_STUDENT, map);
        task.execute(ApplicationData.main_url + ConstantApi.GET_STUDENT + ".php?");
    }

    protected void setAdapter() {
        relsendheight = (int) relSendMessage.getY();

        cls_adapter = new ClassListAdapter(FragmentGroupMessage.this, classes, isselected, allStudentList,
                lstClass, height, relsendheight, chkAll);
        lstClass.setAdapter(cls_adapter);//new ArrayAdapter(FragmentGroupMessage.this, R.layout.multiple_selection, classes));

        /*for (int i = 0; i < lstClass.getAdapter().getCount(); i++) {

            if (arrayClass.get(i).child_marked_id != null && !arrayClass.get(i).child_marked_id.equals("null") && !arrayClass.get(i).child_marked_id.equals("")) {
                lstClass.setItemChecked(i, true);
                class_ids += arrayClass.get(i).class_id + ",";
                arrList.add(i);
            } else {
                lstClass.setItemChecked(i, false);
            }
        }*/
    }


    private void showGroupMessageHistory() {

        if (dlg != null && dlg.isShowing())
            dlg.dismiss();
        dlg = new Dialog(mActivity);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.adres_dlg_groupmessage_history);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        lstHistory = (ListView) dlg.findViewById(R.id.lstHistory);

        {

        }
        lstHistory.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                txtMessage.setText(arrayHistory.get(position).message_desc);
                grade_id = "";
                // initGrade();
                //  arrList.clear();
                //  class_ids = "";
                String cls_ids = arrayHistory.get(position).class_id;
                String class_name = arrayHistory.get(position).class_name;

               /* for (int i = 0; i < lstClass.getAdapter().getCount(); i++) {
                    if (class_name.contains(arrayClass.get(i).class_name) && cls_ids.contains(arrayClass.get(i).class_id)) {
                        lstClass.setItemChecked(i, true);
                        arrList.add(i);
                        class_ids += arrayClass.get(i).class_id + ",";
                    }
                }*/
                //txtHistory.setText(arrayHistory.get(position).message_desc);

                dlg.dismiss();
            }
        });

        GroupMessageHistoryAdapter gAdapter = new GroupMessageHistoryAdapter(FragmentGroupMessage.this, arrayHistory);
        lstHistory.setAdapter(gAdapter);

        TextView dlg_btn_ok = (TextView) dlg.findViewById(R.id.btn_ok);
        dlg_btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dlg.setCanceledOnTouchOutside(true);
        dlg.show();
    }

    protected void sendMessage(String send_data_url2)//, String student_ids)
     {
        // TODO Auto-generated method stub
        if (!GlobalConstrants.isWifiConnected(FragmentGroupMessage.this)) {
            return;
        }
       /* else {
            HashMap<String, Object> param = new HashMap<String, Object>();
            param.put("language", ApplicationData.getlanguage(FragmentGroupMessage.this));
            param.put("teacher_id", teacher_id);
            param.put("class_ids", class_ids);
            param.put("message", txt);
            param.put("student_ids", student_ids);
            ETechAsyncTask task = new ETechAsyncTask(FragmentGroupMessage.this, FragmentGroupMessage.this, ConstantApi.CREATE_NEW_MSG_TEACHER, param);
            task.execute(ApplicationData.main_url + ConstantApi.CREATE_NEW_MSG_TEACHER + ".php?");
        }
*/
        if (pDialog == null)
            pDialog = new MainProgress(FragmentGroupMessage.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(FragmentGroupMessage.this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, send_data_url2, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 1) {
                                ApplicationData.showToast(mActivity, R.string.success_send_groupmessage, true);
                                txtMessage.setText("");
                                cls_adapter.update(false, null);
                                cls_adapter.notifyDataSetChanged();
                                chkAll.setChecked(false);
                                loadHistory(ApplicationData.getlanguageAndApi(FragmentGroupMessage.this, ConstantApi.GET_GROUP_MSG_TEACHER)
                                        + "teacher_id=" + teacher_id + "&is_incharge=" + "");
                            } else {
                                if (response.has("msg")) {
                                    String msg = response.getString("msg");
                                    ApplicationData.showToast(mActivity, msg, false);
                                } else
                                    ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
            }
        });
         jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                 0,
                 DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                 DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsObjRequest);
    }

    protected void uncheckAll() {
        // TODO Auto-generated method stub
        if (arrList.size() > 0) {
            if (chkAll.isChecked()) {
                chkAll.setChecked(false);
            }
            arrList.clear();
            for (int i = 0; i < lstClass.getAdapter().getCount(); i++) {
                lstClass.setItemChecked(i, false);
            }
            class_ids = "";
        }
    }

    public static void hideKeyboardForFocusedView(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
    }

    public void onPause() {
        super.onPause();
        try {
            String msg = txtMessage.getText().toString();
            if (msg.length() > 0) {
                SharedPreferences.Editor editor = sharedpref.edit();
                editor.putString("g" + teacher_id, msg);  //URLEncoder.encode(msg, "UTF-8")
                editor.commit();
            }
            ApplicationData.ispincode = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String msg = sharedpref.getString("g" + teacher_id, "");
       /* if (msg.length() > 0)
            txtMessage.setText(msg);*/
        ApplicationData.ispincode = false;
    }

    @Override
    public void onTaskComplete(int statusCode, String responseMsg, String webserviceCb, Object tag) {
        try {
            if (statusCode == ETechAsyncTask.COMPLETED) {
                JSONObject jObject = new JSONObject(responseMsg);

                try {

                    if (webserviceCb.equalsIgnoreCase(ConstantApi.GET_STUDENT)) {
                        String flag = jObject.getString("flag");
                        if (allStudentList != null && allStudentList.size() > 0)
                            allStudentList.clear();

                        allStudentList = new ArrayList<Childbeans>();
                        if (Integer.parseInt(flag) == 1) {
                            JSONArray allStudents = jObject.getJSONArray("allStudents");

                            for (int i = 0; i < allStudents.length(); i++) {
                                Childbeans childbeans = new Childbeans();
                                JSONObject c = allStudents.getJSONObject(i);

                                childbeans.user_id = c.getString("user_id");
                                childbeans.child_name = c.getString("name");
                                childbeans.name = c.getString("lastname");
                                childbeans.image = c.getString("image");
                                childbeans.child_age = ApplicationData.convertToNorweiDateyeartime(c.getString("birthday"), mActivity);
                                childbeans.grade = c.getString("grade");
                                childbeans.class_id = c.getString("class_id");
                                childbeans.class_name = c.getString("class_name");
                                childbeans.school_name = c.getString("school_name");
                                childbeans.parent_id = c.getString("parent_id");
                                childbeans.parent_name = c.getString("parent_name");
                                childbeans.mobile1 = c.getString("parent_phone");
                                childbeans.parent2_name = c.getString("parent2name");
                                childbeans.mobile2 = c.getString("parent2mobile");
                                childbeans.parent3_name = c.getString("contactname");
                                childbeans.mobile3 = c.getString("contactmobilem");
                                childbeans.nc_parent_id = c.getString("nc_parent_id");
                                childbeans.nc_parent_name = c.getString("nc_parent_name");
                                childbeans.nc_mobile = c.getString("nc_phone");
                                childbeans.status1 = c.getString("status1");
                                childbeans.status2 = c.getString("status2");
                                childbeans.status3 = c.getString("status3");
                                allStudentList.add(childbeans);
                            }


                        } else {
                            if (jObject.has("msg")) {
                                String msg = jObject.getString("msg");
                                ApplicationData.showToast(mActivity, msg, false);
                            } else
                                ApplicationData.showToast(mActivity, R.string.msg_no_students, false);
                        }
                        setAdapter();
                    }

                    else if(webserviceCb.equalsIgnoreCase(ConstantApi.CREATE_NEW_MSG_TEACHER))
                    {
                        String flag = jObject.getString("flag");
                        if (Integer.parseInt(flag) == 1) {
                            ApplicationData.showToast(mActivity, R.string.success_send_groupmessage, true);
                            txtMessage.setText("");
                            cls_adapter.update(false, null);
                            cls_adapter.notifyDataSetChanged();
                            chkAll.setChecked(false);
                            loadHistory(ApplicationData.getlanguageAndApi(FragmentGroupMessage.this, ConstantApi.GET_GROUP_MSG_TEACHER)
                                    + "teacher_id=" + teacher_id + "&is_incharge=" + "");
                        }
                       else {
                            if (jObject.has("msg")) {
                                String msg = jObject.getString("msg");
                                ApplicationData.showToast(mActivity, msg, false);
                            } else
                                ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
                        }
                    }

                } catch (Exception e) {
                    Log.e("GroupmessageActivity", "onTaskComplete() " + e, e);
                }
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
    public void onDestroy() {
        super.onDestroy();
        txtMessage.setText("");

        lstHistory=null;
        lstClass=null;
        pDialog=null;
        teacher_id=null; school_id=null; grade_id=null;
        chkAll=null;
        lytHistory=null; lytGrade=null; btnSend=null;
        arrayHistory = null;
        arrayGrade = null;
        arrayClass = null;
        arrayAllClasses = null;
        grades=null; classes=null;
        arrList=null;
        class_ids = null;
        txtHistory=null;
        txtGrade=null;
        txtMessage=null;
        myalertDialog = null;
        mActivity=null;
        dlg=null;
        sharedpref=null;
        adapter=null;
        cls_adapter=null;
        allStudentList=null;
        relSendMessage=null;
        inflater=null;
        rootView=null;

        System.gc();
    }


}