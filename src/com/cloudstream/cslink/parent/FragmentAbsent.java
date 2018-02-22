package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.adapter.parent.Childbeans;
import com.adapter.parent.ChildrenListAdapter;
import com.adapter.parent.SpinnerListAdapter;
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
import com.xmpp.parent.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class FragmentAbsent extends ActivityHeader implements AsyncTaskCompleteListener<String> {

    private Spinner templetedropdown;
    private ListView subject_list;
    private String getsubject_url, send_data_url, send_subject, _parent_id,
            school_id, child_id, class_id, template, child_img;
    MainProgress pDialog;
    private CheckBox check_all;
    private LinearLayout single_selection, _send;

    ArrayList<Childbeans> arrayList_subject = null;

    ArrayList<Childbeans> arrayList_templete = null;

    String[] subject_list_arr;

    String[] templete_list_arr;
    ArrayList arrList;
    String strText = "";
    TextView text, txtCurDate, lang_selectall, lang_absent, _send_text;
    View lytReason;
    //private AlertDialog
    // = null;
    private Dialog myalertDialog = null;
    String date;
    private RelativeLayout _done;
    private LinearLayout select_all;
    private int current_yr = 1997, startingyr = 1997;

    Activity mActivity;
    private ImageView img_date;
    private int yy, mon, dd;
    private DatePickerDialog.OnDateSetListener setdate;
    private DatePickerDialog datepic;
    private LayoutInflater inflater;
    private View rootView;

    public FragmentAbsent() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = FragmentAbsent.this;
        hideKeyboardForFocusedView(this);
        ApplicationData.setMainActivity(mActivity);

        inflater = getLayoutInflater();
        rootView = inflater.inflate(R.layout.absent_fragment, null);
        relwrapp.addView(rootView);

        check_all = (CheckBox) rootView.findViewById(R.id.checkBox1);
        single_selection = (LinearLayout) rootView.findViewById(R.id.select_subject);
        select_all = (LinearLayout) rootView.findViewById(R.id.all_subject);
        text = (TextView) rootView.findViewById(R.id.textView2);
        txtCurDate = (TextView) rootView.findViewById(R.id.txtCurDate);
        _send = (LinearLayout) rootView.findViewById(R.id.send);
        subject_list = (ListView) rootView.findViewById(R.id.listView1);
        templetedropdown = (Spinner) rootView.findViewById(R.id.spinner1);
        img_date = (ImageView) rootView.findViewById(R.id.img_date);


        SharedPreferences sharedpref = FragmentAbsent.this.getSharedPreferences(Constant.USER_FILENAME, 0);// childid

        _parent_id = sharedpref.getString("parent_id", "");
        school_id = sharedpref.getString("school_id", "");
        child_id = sharedpref.getString("childid", "");//school_class_id
        class_id = sharedpref.getString("school_class_id", "");
        child_img = sharedpref.getString("image", "");


        arrList = new ArrayList();
        final Calendar cal = Calendar.getInstance();
        yy = cal.get(Calendar.YEAR);
        mon = cal.get(Calendar.MONTH);
        dd = cal.get(Calendar.DAY_OF_MONTH);

        //new code --- get current year
        final Date date = new Date();
        int year = date.getYear();
        Date dateInit = new Date(year, 7, 15);
        if (dateInit.before(date)) {
            current_yr = Calendar.getInstance().get(Calendar.YEAR) + 1;
            startingyr = Calendar.getInstance().get(Calendar.YEAR);
        } else {
            current_yr = Calendar.getInstance().get(Calendar.YEAR);
            startingyr = Calendar.getInstance().get(Calendar.YEAR) - 1;
        }
        //  cal.add(Calendar.DAY_OF_MONTH, -2);

        //set current date
        String curr_date = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", this.getResources().getConfiguration().locale);
        curr_date = dateFormat.format(date);
        txtCurDate.setText(curr_date);

        //set title
        showheadermenu(FragmentAbsent.this, getString(R.string.send_abs_single_line), R.color.yellow_home, true, child_img);

        check_all.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox1);
                if (subject_list_arr != null && subject_list_arr.length > 0) {
                    checkBox.setEnabled(true);
                    check_all.setEnabled(true);
                    strText = "";
                    if (checkBox.isChecked()) {
                        if (arrList.size() > 0) {
                            arrList.clear();
                        }
                        for (int i = 0; i < subject_list.getAdapter().getCount(); i++) {
                            subject_list.setItemChecked(i, true);
                            arrList.add(i);
                        }

                        for (int i = 0; i < arrList.size(); i++) {
                            strText += arrayList_subject.get(i).child_period_id + ",";
                        }

                    } else {
                        if (arrList.size() > 0) {
                            arrList.clear();
                        }
                        for (int i = 0; i < subject_list.getAdapter().getCount(); i++) {
                            subject_list.setItemChecked(i, false);
                        }
                    }
                } else {
                    checkBox.setChecked(false);
                    checkBox.setClickable(false);
                    check_all.setChecked(false);
                    check_all.setEnabled(false);
                }
            }

        });

        single_selection.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (arrList.size() > 0) {
                    if (check_all.isChecked()) {
                        check_all.setChecked(false);
                    }
                    arrList.clear();
                    for (int i = 0; i < subject_list.getAdapter().getCount(); i++) {
                        subject_list.setItemChecked(i, false);
                    }
                    strText = "";
                }
            }
        });

        img_date.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                datepic = new DatePickerDialog(FragmentAbsent.this, setdate, yy, mon, dd);
                Calendar c = Calendar.getInstance();
                c.set(startingyr, 7, 15);
                datepic.getDatePicker().setMinDate(c.getTimeInMillis() - 10000);
                c.set(current_yr, 07, 14);
                datepic.getDatePicker().setMaxDate(c.getTimeInMillis());
                datepic.setButton(DatePickerDialog.BUTTON_POSITIVE, getResources().getString(R.string.str_done), datepic);
                datepic.setButton(DatePickerDialog.BUTTON_NEGATIVE, getResources().getString(R.string.str_cancel), datepic);
                datepic.show();
                // new DatePickerDialog(FragmentAbsent.this,setdate,yy,mon,dd).show();
            }
        });

        select_all.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });


        _send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!GlobalConstrants.isWifiConnected(FragmentAbsent.this)) {
                    return;
                }

                if (strText.equalsIgnoreCase("")) {
                    ApplicationData.showToast(mActivity, R.string.msg_no_period, false);
                } else {
                    //String temp_lete = text.getText().toString();
                    if (template.equalsIgnoreCase(getString(R.string.str_select_reason))) {
                        ApplicationData.showToast(mActivity, R.string.msg_no_reason, false);
                    } else if (!template.equalsIgnoreCase(getString(R.string.str_select_reason)) && !strText.equalsIgnoreCase("")) {

                        try {
                            template = URLEncoder.encode(template, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if (strText.endsWith(","))
                            strText = strText.substring(0, strText.lastIndexOf(","));

                        send_data_url = ApplicationData.getlanguageAndApi(FragmentAbsent.this, ConstantApi.SET_CHILD_NOTICE)
                                + "child_id="
                                + child_id + "&period_ids=" + strText
                                + "&reason=" + template
                                + "&date=" + ApplicationData.convertTodbDate(txtCurDate.getText().toString(), FragmentAbsent.this);

                        try {
                            HashMap<String, Object> params = new HashMap<String, Object>();
                            params.put("child_id", child_id);
                            params.put("os", "android");
                            params.put("period_ids", strText);
                            params.put("reason", template);
                            params.put("date", ApplicationData.convertTodbDate(txtCurDate.getText().toString(), FragmentAbsent.this));
                            ETechAsyncTask task = new ETechAsyncTask(FragmentAbsent.this, FragmentAbsent.this, ConstantApi.SET_CHILD_NOTICE, params);
                            task.execute(ApplicationData.main_url + ConstantApi.SET_CHILD_NOTICE + ".php?");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//						send_data_url = send_data_url.replaceAll(" ", "%20");
                        System.out.println(send_data_url + " milliseconds since midnight");
                        //  send_data(send_data_url);
                    } else {
                        ApplicationData.showToast(mActivity, R.string.msg_no_period, false);
                    }
                }
            }
        });

        // .............ListView................//

        subject_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        subject_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                    long arg3) {
                if (arrList.contains(arg2)) {
                    arrList.remove((Integer) arg2);
                    if (check_all.isChecked())
                        check_all.setChecked(false);
                } else {
                    arrList.add(arg2);
                }

                Collections.sort(arrList);
                strText = "";
                for (int i = 0; i < arrList.size(); i++) {
                    int inx = Integer.valueOf(arrList.get(i).toString());
                    strText += arrayList_subject.get(inx).child_period_id + ",";
                }
            }

        });

        // ..........dropdown spinner...........//

        templetedropdown
                .setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {

                        if (arrayList_templete == null) {
                            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_value_template), false);
                        } else {
                            if (arrayList_templete.size() > 0) {
                                template = arrayList_templete.get(position).child_template_title;
                            } else {
                                ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_value_template), false);
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

//			text.setText("Reason");
//			lang_select.setText("Select Period");
//			lang_selectall.setText("Select All");
//			lang_absent.setText("Periods");
//			_send_text.setText("SEND");

        getsubject_url = ApplicationData.getlanguageAndApi(FragmentAbsent.this, ConstantApi.GET_CHILD_NOTICE_PHONE) +
                "child_id=" + child_id +
                "&curdate=" + ApplicationData.convertTodbDate(txtCurDate.getText().toString(), FragmentAbsent.this);
        get_subject(getsubject_url);

        setdate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                yy = year;
                mon = monthOfYear;
                dd = dayOfMonth;
                updateLabel(yy, monthOfYear + 1, dd);
            }
        };

    }

    private void updateLabel(int yyyy, int mm, int dd) {
        txtCurDate.setText(ApplicationData.convertToNorweiDate(yyyy + "-" + pad(mm) + "-" + pad(dd), FragmentAbsent.this) + "");

        getsubject_url = ApplicationData.getlanguageAndApi(FragmentAbsent.this, ConstantApi.GET_CHILD_NOTICE_PHONE) + "child_id=" + child_id +
                "&curdate=" + ApplicationData.convertTodbDate(txtCurDate.getText().toString(), FragmentAbsent.this);

        get_subject(getsubject_url);
        check_all.setChecked(false);
    }

    private String pad(int dd) {
        if (dd < 10)
            return String.valueOf("0" + dd);
        else
            return dd + "";
    }


    protected void send_data(String send_data_url2) {
        // TODO Auto-generated method stub
       /* if (!ApplicationData.checkRight(mActivity)) {
            return;
        }*/

        if (!GlobalConstrants.isWifiConnected(FragmentAbsent.this)) {
            return;
        }

        if (pDialog == null)
            pDialog = new MainProgress(FragmentAbsent.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.show();

        date = send_data_url2;
        RequestQueue queue = Volley.newRequestQueue(FragmentAbsent.this);
        Log.e("URl", "" + send_data_url2);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, send_data_url2, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        String msg = "";
                        try {
                            String flag = response.getString("flag");
                            if (response.has("msg"))
                                msg = response.getString("msg");

                            if (Integer.parseInt(flag) == 1) {
                                ApplicationData.showToast(mActivity, R.string.success_submit_report, true);
                                templetedropdown.setSelection(0);
                                check_all.setChecked(false);
                                for (int i = 0; i < subject_list.getAdapter().getCount(); i++) {
                                    subject_list.setItemChecked(i, false);
                                }
                                strText = "";
                                arrList.clear();
                            } else {
                                ApplicationData.showToast(mActivity, msg, false);
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
        queue.add(jsObjRequest);
    }

    protected void uncheck() {
        // TODO Auto-generated method stub
        if (arrList.size() > 0) {
            if (check_all.isChecked()) {
                check_all.setChecked(false);
            }
            arrList.clear();
            for (int i = 0; i < subject_list.getAdapter().getCount(); i++) {
                subject_list.setItemChecked(i, false);
            }
            strText = "";
        }
    }

    private void get_subject(String login_url2) {
        // TODO Auto-generated method stub
        if (!GlobalConstrants.isWifiConnected(FragmentAbsent.this)) {
            return;
        }
        if (pDialog == null)
            pDialog = new MainProgress(FragmentAbsent.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(FragmentAbsent.this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, login_url2, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String currentDate = response.getString("currentDate");
                           /* if (currentDate.length() >= 8) {
                                currentDate = ApplicationData.convertToNorweiDate(currentDate, mActivity);
                                txtCurDate.setText(currentDate);
                            } else {
                                txtCurDate.setText("");
                            }*/
                            String msg = "";
                            String flag = response.getString("flag");
                            if (response.has("msg"))
                                msg = response.getString("msg");

                            pDialog.dismiss();
                            if (Integer.parseInt(flag) == 1) {

                                JSONArray allPeriods = response.getJSONArray("allPeriods");
                                JSONArray allReasons = response.getJSONArray("allReasons");

                                arrayList_subject = new ArrayList<Childbeans>();
                                arrayList_templete = new ArrayList<Childbeans>();

                                subject_list_arr = new String[allPeriods.length()];

                                for (int i = 0; allPeriods.length() > i; i++) {
                                    JSONObject c = allPeriods.getJSONObject(i);
                                    Childbeans childbeans = new Childbeans();
                                    childbeans.child_subject_id = c.getString("subjecet_id");
                                    childbeans.child_teacher_id = c.getString("teacher_id");
                                    childbeans.child_subject_name = c.getString("subject_name");

                                    subject_list_arr[i] = new String(c.getString("lecture_no") + " - " + c.getString("subject_name") + "  " + c.getString("time"));
                                    childbeans.child_period_id = c.getString("period_id");
                                    childbeans.child_marked_id = c.getString("reason");
                                  /*  if (c.getString("reason").length() > 0 && !c.getString("reason").equals("null"))
                                        text.setText(c.getString("reason"));*/
                                    arrayList_subject.add(childbeans);
                                    strText = "";
                                    if (arrList.size() > 0)
                                        arrList.clear();
                                }

                                if (allPeriods.length() == 0) {
                                    _send.setVisibility(View.GONE);
                                    ApplicationData.showToast(mActivity, R.string.msg_no_period_subject, true);
                                } else {
                                    _send.setVisibility(View.VISIBLE);
                                }

                                templete_list_arr = new String[allReasons.length() + 1];
                                templete_list_arr[0] = new String(getString(R.string.str_select_reason));

                                Childbeans childbean = new Childbeans();
                                childbean.child_template_title = getString(R.string.str_select_reason);
                                childbean.child_temlate_id = "0";
                                arrayList_templete.add(childbean);


                                for (int j = 0; allReasons.length() > j; j++) {
                                    JSONObject c = allReasons.getJSONObject(j);
                                    Childbeans childbeans = new Childbeans();
                                    childbeans.child_temlate_id = c.getString("template_id");
                                    childbeans.child_template_title = c.getString("template_title");

                                    templete_list_arr[j + 1] = new String(c.getString("template_title"));
                                    arrayList_templete.add(childbeans);
                                }

                                setadapter();

                           /*     ArrayAdapter<String> adapter = new ArrayAdapter<String>(FragmentAbsent.this,
                                        R.layout.dropdown_childlist_item, templete_list_arr);*/
                                SpinnerListAdapter adapter = new SpinnerListAdapter(FragmentAbsent.this, templete_list_arr);
                                templetedropdown.setAdapter(adapter);

                                pDialog.dismiss();
                            } else {
                                ApplicationData.showToast(mActivity, msg, false);
                                subject_list.setVisibility(View.GONE);
                            }


                        } catch (Exception e) {
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

    protected void setadapter() {
        subject_list.setVisibility(View.VISIBLE);
        subject_list.setAdapter(new ArrayAdapter(FragmentAbsent.this, R.layout.multiple_selection, subject_list_arr));
        ApplicationData.setListViewHeightBasedOnChildren(subject_list);


        for (int i = 0; i < subject_list.getAdapter().getCount(); i++) {
            check_all.setEnabled(true);

            if (!arrayList_subject.get(i).child_marked_id.equals("null")) {
                Log.d("FragmentAbsent ", "reason : " + arrayList_subject.get(i).child_marked_id);
                subject_list.setItemChecked(i, true);
                strText += arrayList_subject.get(i).child_period_id + ",";
                arrList.add(i);
            } else {
                Log.d("FragmentAbsent ", "false : " + arrayList_subject.get(i).child_marked_id);
                subject_list.setItemChecked(i, false);
            }
        }
    }

    // ....on click on forgot password..//
    public void conti(View v) {

    }

    public static void hideKeyboardForFocusedView(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationData.setMainActivity(mActivity);
    }


    @Override
    public void onTaskComplete(int statusCode, String responseMsg, String webserviceCb, Object tag) {
        if (statusCode == ETechAsyncTask.COMPLETED) {
            try {
                JSONObject jObject = new JSONObject(responseMsg);
                String resMsg = jObject
                        .getString(ConstantApi.REQ_RESPONSE_MSG);


                if (webserviceCb.equalsIgnoreCase(ConstantApi.SET_CHILD_NOTICE)) {
                    String flag = jObject.getString("flag");

                    if (Integer.parseInt(flag) == 1) {
                        ApplicationData.showToast(mActivity, R.string.success_submit_report, true);
                        templetedropdown.setSelection(0);
                        check_all.setChecked(false);
                        for (int i = 0; i < subject_list.getAdapter().getCount(); i++) {
                            subject_list.setItemChecked(i, false);
                        }
                        strText = "";
                        arrList.clear();

                    } else {
                        if (jObject.has("msg")) {
                            String msg = jObject.getString("msg");
                            ApplicationData.showToast(FragmentAbsent.this, msg, false);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ApplicationData.showToast(FragmentAbsent.this, getResources().getString(R.string.msg_operation_error), true);
            }
        } else if (statusCode == ETechAsyncTask.ERROR_NETWORK) {
            try {
                ApplicationData.showToast(FragmentAbsent.this, R.string.msg_operation_error, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        templetedropdown = null;
        subject_list = null;
        getsubject_url = null;
        send_data_url = null;
        send_subject = null;
        _parent_id = null;
        school_id = null;
        child_id = null;
        class_id = null;
        template = null;
        child_img = null;
        pDialog = null;
        check_all = null;
        single_selection = null;
        _send = null;
        arrayList_subject = null;
        arrayList_templete = null;
        subject_list_arr = null;
        templete_list_arr = null;
        arrList = null;
        strText = null;
        text = null;
        txtCurDate = null;
        lang_selectall = null;
        lang_absent = null;
        _send_text = null;
        lytReason = null;
        myalertDialog = null;
        date = null;
        _done = null;
        select_all = null;
        mActivity = null;
        img_date = null;
        setdate = null;
        datepic = null;
        inflater = null;
        rootView = null;

        System.gc();
    }
}
