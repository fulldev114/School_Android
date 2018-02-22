package com.cloudstream.cslink.teacher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ParseException;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.adapter.teacher.AttendanceListAdapter;
import com.adapter.teacher.AttendanceSpinnerListAdapter;
import com.adapter.teacher.Childbeans;
import com.adapter.teacher.StudentAttendanceInfo;
import com.adapter.teacher.StudnetAttendanceAdpater;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudstream.cslink.R;
import com.common.Bean.ExpandableListDataPump;
import com.common.dialog.MainProgress;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.common.view.CircularImageView;
import com.xmpp.teacher.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FragmentAttendance extends ActivityHeader {


    MainProgress pDialog;

    private String teacher_id, school_id, grade_id, class_id = "", strAbsentDatas = "", strNoticeDatas = "",
            strAddedNoticeDatas = "", absentreason = "", noticereason = "";

    ArrayList<Childbeans> arrayClass = null;
    ArrayList<Childbeans> arrayAllClasses = null;

    ArrayList<Childbeans> arrayAllStudents = null, orgArrayAllPeriods = null,
            arrayAllPeriods = null, arrayAbsentStudents = null, arrayPnoticeStudents = null;

    ArrayList<StudentAttendanceInfo> arrayStudents = null, currArrayStudents, nochangeStudents = null;

    private ListView lstStudent;
    AttendanceListAdapter aAdapter = null;
    StudnetAttendanceAdpater adAdapter = null;

    String[] classes;

    ArrayList arrList;

    TextView txtDate, txtTitle;
    Spinner txtClass;
    View btnHelp, btnSave, btnSend, lytClass, lytFullDay;

    TextView imgFullDay;
    int fullDayStatus = 1;


    private Dialog myalertDialog = null;
    private Dialog dlg;
    int year, month, day;
    Activity mActivity;
    private ExpandableListView list_reason;
    private TextView txtreason, btndone, txt_Cancel;
    private ArrayList<Childbeans> arrayList_templete;
    private CircularImageView img_profile;
    private TextView text_class;
    private String reason1 = "", reason2 = "";
    private LayoutInflater inflater;
    private View rootView;
    private boolean isdatasaved = false;
    private int current_yr = 1997, startingyr = 1997;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;

        inflater = getLayoutInflater();
        rootView = inflater.inflate(R.layout.adres_attendance_fragment1, null);
        relwrapp.addView(rootView);
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        //get current year
        Date date = new Date();
        int year = date.getYear();
        Date dateInit = new Date(year, 7, 15);
        if (dateInit.before(date)) {
            current_yr = Calendar.getInstance().get(Calendar.YEAR) + 1;
            startingyr = Calendar.getInstance().get(Calendar.YEAR);
        } else {
            current_yr = Calendar.getInstance().get(Calendar.YEAR);
            startingyr = Calendar.getInstance().get(Calendar.YEAR) - 1;
        }

        //set Title
        showheadermenu(FragmentAttendance.this, getString(R.string.send_abs_without_newline), R.color.yellow_home, true);

        txtClass = (Spinner) rootView.findViewById(R.id.txtClass);
        lytClass = (View) rootView.findViewById(R.id.lytClass);
        lstStudent = (ListView) rootView.findViewById(R.id.lstStudent);
        txtDate = (TextView) rootView.findViewById(R.id.txtDate);
        btnSave = (View) rootView.findViewById(R.id.btnSave);
        btnSend = (View) rootView.findViewById(R.id.btnSend);

        lin_information.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpDlg();
            }
        });

        ApplicationData.hideKeyboardForFocusedView(this);
        SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        teacher_id = sharedpref.getString("teacher_id", "");
        school_id = sharedpref.getString("school_id", "");

        SharedPreferences.Editor editor = sharedpref.edit();
        int oldval = Integer.parseInt(sharedpref.getString("old_register_badge", "0"));
        oldval = oldval + Integer.parseInt(sharedpref.getString("register_badge", "0"));
        editor.putString("old_register_badge", String.valueOf(oldval));
        editor.putString("register_badge", "0");
        editor.commit();

        arrList = new ArrayList();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", this.getResources().getConfiguration().locale);
        txtDate.setText(dateFormat.format(new Date()));
        txtDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDatePicker();
            }
        });


        txtClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (classes == null) {
                    ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_class), false);
                } else {
                    if (classes.length > 0) {
                        if (position == 0)
                            class_id = "";
                        else
                            class_id = arrayClass.get(position).class_id;
                        // if (!class_id.equalsIgnoreCase("0"))
                        initStudentList();

                    } else if (classes.length <= 0) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_class), false);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        lstStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currArrayStudents.get(position).periodsInfo.size() > 0)
                    showStudentDetailsDlg(position);
                else {
                    try {
                        ApplicationData.showMessage(mActivity, getString(R.string.app_name),
                                getString(R.string.no_lecture), getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (class_id.equals("0") || class_id.equals("")) {
                    try {
                        ApplicationData.showMessage(mActivity, "", getString(R.string.select_class_before_reg), getString(R.string.str_ok));
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!isChanged()) {
                    isdatasaved = false;
                    ApplicationData.showToast(mActivity, R.string.msg_no_change, false);
                } else {

                    try {
                        if (absentreason != null && absentreason.length() > 0 && !absentreason.equalsIgnoreCase(""))
                            absentreason = URLEncoder.encode(absentreason, "utf-8");
                        if (noticereason != null && noticereason.length() > 0 && !noticereason.equalsIgnoreCase(""))
                            noticereason = URLEncoder.encode(noticereason, "utf-8");

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    String dt = ApplicationData.convertToNorwei(txtDate.getText().toString(), mActivity);
                    String url = ApplicationData.getlanguageAndApi(FragmentAttendance.this, ConstantApi.SET_ATTENDANCE_CLASS_TEACHER)
                            + "class_id=" + class_id
                            + "&teacher_id=" + teacher_id
                            + "&date=" + dt
                            + "&datas=" + strAbsentDatas
                            + "&notices=" + strNoticeDatas
                            + "&absentreason=" + absentreason
                            + "&noticereason=" + noticereason;
                    doServer(url, "save");

                }
            }
        });

        btnSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (class_id.equals("0") || class_id.equals("")) {
                    try {
                        ApplicationData.showMessage(mActivity, "", getString(R.string.error_absent_notice), getString(R.string.str_ok));
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // if (isdatasaved)
                {
                    String dt = ApplicationData.convertToNorwei(txtDate.getText().toString(), mActivity);
                    String url = ApplicationData.getlanguageAndApi(FragmentAttendance.this, ConstantApi.SEND_ABSENT_NOTICE)
                            + "teacher_id=" + teacher_id
                            + "&date=" + dt
                            + "&class_id=" + class_id;
                    doServer(url, "send");
                } /*else {
                    ApplicationData.showToast(mActivity, R.string.no_update, true);
                }*/
            }
        });

        loadClasses(ApplicationData.getlanguageAndApi(FragmentAttendance.this, ConstantApi.GET_CLASS_TEACHER) +
                "teacher_id=" + teacher_id);

        currArrayStudents = new ArrayList<StudentAttendanceInfo>();
        aAdapter = new AttendanceListAdapter(mActivity, currArrayStudents);
        lstStudent.setAdapter(aAdapter);

    }

    private void doServer(String url, final String str) {
        if (!GlobalConstrants.isWifiConnected(FragmentAttendance.this)) {
            return;
        }

        if (pDialog == null)
            pDialog = new MainProgress(FragmentAttendance.this);
        pDialog.setCancelable(false);
        if (str.equals("save")) {
            pDialog.setMessage(getResources().getString(R.string.str_saving));
        } else {
            pDialog.setMessage(getResources().getString(R.string.str_sending));
        }
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(FragmentAttendance.this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 1) {
                                if (str.equals("save")) {
                                    isdatasaved = true;
                                    ApplicationData.showMessage(mActivity, "", getString(R.string.success_save), getString(R.string.str_ok));
                                } else {
                                    ApplicationData.showMessage(mActivity, "", getString(R.string.success_send), getString(R.string.str_ok));
                                }
                            } else {

                                if (response.has("msg")) {
                                    String msg = response.getString("msg");
                                    ApplicationData.showMessage(mActivity, "", msg, getString(R.string.str_ok));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
                        }
                    }
                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
            }
        }

        );
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private boolean isChanged() {
        strAbsentDatas = "";
        strNoticeDatas = "";
        strAddedNoticeDatas = "";
        absentreason = "";
        noticereason = "";
        String userid = "";
        boolean changed = false;

        if (currArrayStudents.size() == 0)
            return changed;

        String lecture_no = "", period_id = "", absent_user_list = "", notice_user_list = "",
                addnotice_user_list = "", reason = "", reason1 = "";
        ArrayList<Childbeans> allPeriods = currArrayStudents.get(0).periodsInfo;
        for (int i = 0; i < allPeriods.size(); i++) {
            lecture_no = allPeriods.get(i).lecture_no;
            period_id = allPeriods.get(i).child_period_id;
            absent_user_list = "";
            reason1 = allPeriods.get(i).reason;
            for (int j = 0; j < currArrayStudents.size(); j++) {
                reason = "";
                if (currArrayStudents.get(j).periodsInfo.get(i).attend.equals("2")) {
                    continue;
                } else if (currArrayStudents.get(j).periodsInfo.get(i).attend.equals("1")) {
                    if (!currArrayStudents.get(j).periodsInfo.get(i).attend.equals(nochangeStudents.get(j).periodsInfo.get(i).attend)) {
                        changed = true;
                    }
                    continue;
                }/* else if (currArrayStudents.get(j).periodsInfo.get(i).attend.equals("0") && currArrayStudents.get(j).periodsInfo.get(i).attend.equals(nochangeStudents.get(j).periodsInfo.get(i).attend)
                        && reason1.equals(nochangeStudents.get(j).periodsInfo.get(i).reason)) {
                    continue;
                } */ else {
                    changed = true;
                    reason = currArrayStudents.get(j).periodsInfo.get(i).reason;
                }
                if (absent_user_list.length() > 0) {
                    absent_user_list = absent_user_list + ",";
                }
                absent_user_list = absent_user_list + currArrayStudents.get(j).user_id;
                userid = currArrayStudents.get(j).user_id;

                if (absentreason.length() > 0)
                    absentreason = absentreason + ":=:";

                absentreason = absentreason + userid + ":::" + reason;

            }

            if (absent_user_list.length() == 0)
                continue;


            if (strAbsentDatas.length() > 0)
                strAbsentDatas = strAbsentDatas + ":=:";

            strAbsentDatas = strAbsentDatas + lecture_no + ":::" + period_id + ":::" + absent_user_list;
        }

        for (int i = 0; i < allPeriods.size(); i++) {
            lecture_no = allPeriods.get(i).lecture_no;
            period_id = allPeriods.get(i).child_period_id;
            notice_user_list = "";
            reason1 = allPeriods.get(i).reason;
            for (int j = 0; j < currArrayStudents.size(); j++) {

                reason = "";
                if (currArrayStudents.get(j).periodsInfo.get(i).attend.equals("0")) {
                    continue;
                } else if (currArrayStudents.get(j).periodsInfo.get(i).attend.equals("1")) {
                    if (!currArrayStudents.get(j).periodsInfo.get(i).attend.equals(nochangeStudents.get(j).periodsInfo.get(i).attend)) {
                        changed = true;
                    }
                    continue;

                }/* else if (currArrayStudents.get(j).periodsInfo.get(i).attend.equals("2") && currArrayStudents.get(j).periodsInfo.get(i).attend.equals(nochangeStudents.get(j).periodsInfo.get(i).attend)
                        && reason1.equals(nochangeStudents.get(j).periodsInfo.get(i).reason)) {
                    continue;
                }*/
                if (notice_user_list.length() > 0) {
                    notice_user_list = notice_user_list + ",";
                }
                changed = true;
                notice_user_list = notice_user_list + currArrayStudents.get(j).user_id;
                reason = currArrayStudents.get(j).periodsInfo.get(i).reason;
                userid = currArrayStudents.get(j).user_id;

                if (noticereason.length() > 0)
                    noticereason = noticereason + ":=:";
                noticereason = noticereason + userid + ":::" + reason;

                if (arrayStudents.get(j).periodsInfo1.get(i).attend1.equals("2")) {
                    continue;
                }
                if (addnotice_user_list.length() > 0) {
                    addnotice_user_list = addnotice_user_list + ",";
                }
                addnotice_user_list = addnotice_user_list + currArrayStudents.get(j).user_id;
            }

            if (notice_user_list.length() == 0)
                continue;

            if (strNoticeDatas.length() > 0)
                strNoticeDatas = strNoticeDatas + ":=:";
            strNoticeDatas = strNoticeDatas + lecture_no + ":::" + period_id + ":::" + notice_user_list;

        }
        return changed;
    }

    private void showStudentDetailsDlg(final int pos) {


        if (dlg != null && dlg.isShowing())
            dlg.dismiss();
        dlg = new Dialog(mActivity);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.adres_dlg_attendance_details);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final int index = lstStudent.getFirstVisiblePosition();

        txtTitle = (TextView) dlg.findViewById(R.id.txtTitle);
        list_reason = (ExpandableListView) dlg.findViewById(R.id.list_reason);
        txtreason = (TextView) dlg.findViewById(R.id.txtreason);
        btndone = (TextView) dlg.findViewById(R.id.btndone);
        lytFullDay = (View) dlg.findViewById(R.id.lytFullDay);
        imgFullDay = (TextView) dlg.findViewById(R.id.imgFullDay);
        txt_Cancel = (TextView) dlg.findViewById(R.id.txt_Cancel);
        img_profile = (CircularImageView) dlg.findViewById(R.id.img_profile);
        text_class = (TextView) dlg.findViewById(R.id.text_class);
        //  lstStudentDetails = (ListView) dlg.findViewById(R.id.lstStudentDetails);

        txtTitle.setText(currArrayStudents.get(pos).name);

        String url = ApplicationData.web_server_url + ApplicationData.imagepath + currArrayStudents.get(pos).image;
        ApplicationData.setProfileImg(img_profile, url, FragmentAttendance.this);

        text_class.setText(currArrayStudents.get(pos).class_name);

        final ArrayList<Childbeans> Temparray = new ArrayList<Childbeans>();
        Childbeans bean = new Childbeans();
        bean.lecture_no = "0";
        bean.chat_time = getResources().getString(R.string.str_select_reason);
        Temparray.add(bean);
        reason1 = "";
        reason2 = "";
        int counter_yellow = 0, counter_red = 0, counter_green = 0;

        for (int i = 0; i < arrayStudents.get(pos).periodsInfo1.size(); i++) {

            if (arrayStudents.get(pos).periodsInfo1.get(i).attend1.equals("0"))
                counter_yellow++;
            if (arrayStudents.get(pos).periodsInfo1.get(i).attend1.equals("1"))
                counter_green++;
            if (arrayStudents.get(pos).periodsInfo1.get(i).attend1.equals("2"))
                counter_red++;

            Temparray.add(arrayStudents.get(pos).periodsInfo1.get(i));

            if (arrayStudents.get(pos).periodsInfo1.get(i).reason1 != null && arrayStudents.get(pos).periodsInfo1.get(i).reason1.length() > 0)
                reason1 = arrayStudents.get(pos).periodsInfo1.get(i).reason1;
            reason2 = arrayStudents.get(pos).periodsInfo1.get(i).reason1;

        }

        if (counter_yellow == Temparray.size() - 1) {
            fullDayStatus = 0;
        } else if (counter_green == Temparray.size() - 1) {
            fullDayStatus = 1;
        } else if (counter_red == Temparray.size() - 1) {
            fullDayStatus = 2;
        } else {
            fullDayStatus = 1;
        }

        lytFullDay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fullDayStatus--;
                if (fullDayStatus < 0)
                    fullDayStatus = 2;

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    if (fullDayStatus == 0) {
                        imgFullDay.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.adres_absent_yellow));
                    } else if (fullDayStatus == 2) {
                        imgFullDay.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.adres_absent_red));
                    } else {
                        imgFullDay.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.adres_present_green));
                    }
                } else {
                    if (fullDayStatus == 0) {
                        imgFullDay.setBackground(mActivity.getResources().getDrawable(R.drawable.adres_absent_red));
                    } else if (fullDayStatus == 2) {
                        imgFullDay.setBackground(mActivity.getResources().getDrawable(R.drawable.adres_absent_yellow));
                    } else {
                        imgFullDay.setBackground(mActivity.getResources().getDrawable(R.drawable.adres_present_green));
                    }
                }

                for (int loop = 1; loop < Temparray.size(); loop++) {
                    Childbeans periodInfo = Temparray.get(loop);
                    periodInfo.attend1 = fullDayStatus + "";
                    Temparray.set(loop, periodInfo);
                }
                adAdapter.updateAdapter(pos, Temparray);
                /*setAllStatus(pos, fullDayStatus);
                adAdapter.notifyDataSetChanged();
                aAdapter.notifyDataSetChanged();*/
            }
        });


        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            imgFullDay.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.adres_present_green));
        } else {
            imgFullDay.setBackground(mActivity.getResources().getDrawable(R.drawable.adres_present_green));
        }

        list_reason.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                if (groupPosition != 0) {
                    //  int status = getStatus(pos, groupPosition);
                    int status = Integer.parseInt(Temparray.get(groupPosition).attend1);
                    status--;
                    if (status < 0)
                        status = 2;

                    if (Temparray != null && Temparray.size() > 0) {

                        Childbeans periodInfo = Temparray.get(groupPosition);
                        periodInfo.attend1 = status + "";
                        Temparray.set(groupPosition, periodInfo);

                       /* boolean ispresent=true;
                        for(int ir=1;ir<Temparray.size();ir++)
                        {

                            if(!Temparray.get(ir).attend1.equalsIgnoreCase("0"))
                            {
                                ispresent=false;
                                break;
                            }
                        }
                        if(ispresent)
                            reason1="";
                        else
                            reason1=reason2;*/

                        adAdapter.updateAdapter(pos, Temparray);
                    }

                    if (fullDayStatus != 1) {
                        fullDayStatus = 1;
                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            imgFullDay.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.adres_present_green));
                        } else {
                            imgFullDay.setBackground(mActivity.getResources().getDrawable(R.drawable.adres_present_green));
                        }
                    }
                }

            }
        });

        list_reason.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });


        list_reason.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                if (groupPosition == 0) {
                    // txtnew.setText(arrayList_templete.get(childPosition).child_template_title);
                    adAdapter.setreason(groupPosition, arrayList_templete.get(childPosition).child_template_title,
                            Temparray);
                    reason1 = arrayList_templete.get(childPosition).child_template_title;
                    reason2 = arrayList_templete.get(childPosition).child_template_title;
                    ;
                    list_reason.collapseGroup(groupPosition);
                } /*else {
                    lay[0].removeView(txtnew);
                }*/

                return true;
            }
        });

       /* list_reason.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (groupPosition == 0) {
                    lay[0] = (LinearLayout) v;
                    lay[0].removeView(txtnew);
                    lay[0].addView(txtnew);
                }

                return false;
            }
        });*/


        txt_Cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StudentAttendanceInfo attendanceInfo = currArrayStudents.get(pos);
                    StudentAttendanceInfo orgattinfo = arrayStudents.get(pos);
                    // orgattinfo.reason=arrayStudents.get(pos).reason;
                    reason1 = "";
                    for (int position = 0; position < attendanceInfo.periodsInfo.size(); position++) {
                        Childbeans periodInfo = attendanceInfo.periodsInfo.get(position);
                        Childbeans originfo = orgattinfo.periodsInfo1.get(position);
                        originfo.attend1 = periodInfo.attend;
                        originfo.reason1 = periodInfo.reason;
                        orgattinfo.periodsInfo1.set(position, originfo);
                    }
                    arrayStudents.set(pos, orgattinfo);
                    //   arrayStudents.set(pos,currArrayStudents.get(pos));
                    dlg.dismiss();
                    Temparray.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btndone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (reason1 != null && reason1.length() > 0) {
                    StudentAttendanceInfo attendanceInfo = currArrayStudents.get(pos);
                    attendanceInfo.reason = reason1;
                    currArrayStudents.set(pos, attendanceInfo);

                    StudentAttendanceInfo orgattinfo = arrayStudents.get(pos);
                    orgattinfo.reason=reason1;
                    arrayStudents.set(pos, orgattinfo);
                }*/
                boolean ispresent = true;
                for (int ir = 1; ir < Temparray.size(); ir++) {

                    if (!Temparray.get(ir).attend1.equalsIgnoreCase("1")) {
                        ispresent = false;
                        break;
                    }
                }
                if (ispresent)
                    reason1 = "";

                StudentAttendanceInfo orgattinfo = arrayStudents.get(pos);
                // orgattinfo.reason=arrayStudents.get(pos).reason;
                if (reason1.equalsIgnoreCase("")) {
                    for (int position = 0; position < orgattinfo.periodsInfo1.size(); position++) {
                        Childbeans originfo = orgattinfo.periodsInfo1.get(position);
                        originfo.reason1 = "";
                        orgattinfo.periodsInfo1.set(position, originfo);
                    }
                } else {
                    for (int position = 0; position < orgattinfo.periodsInfo1.size(); position++) {
                        Childbeans originfo = orgattinfo.periodsInfo1.get(position);
                        originfo.reason1 = reason1;
                        orgattinfo.periodsInfo1.set(position, originfo);
                    }
                }
                try {
                    setAllStatusReturn(pos, Temparray, reason1);
                    adAdapter.notifyDataSetChanged();
                    aAdapter.notifyDataSetChanged();
                    dlg.dismiss();
                    //    lstStudent.setSelection(index);
                    Temparray.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dlg.setCanceledOnTouchOutside(false);
        dlg.show();

        HashMap<String, List<Childbeans>> expandableListDetail = ExpandableListDataPump.getData_reason(FragmentAttendance.this, arrayList_templete, arrayStudents.get(pos).periodsInfo1);
        List<Childbeans> expandableList = ExpandableListDataPump.getparentlist(FragmentAttendance.this, arrayStudents.get(pos).periodsInfo1);
        adAdapter = new StudnetAttendanceAdpater(FragmentAttendance.this, expandableListDetail, expandableList, reason1);
        list_reason.setAdapter(adAdapter);
    }

    private void setAllStatus(int pos, int fullDayStatus) {
        StudentAttendanceInfo attendanceInfo = currArrayStudents.get(pos);
        for (int position = 0; position < attendanceInfo.periodsInfo.size(); position++) {
            Childbeans periodInfo = attendanceInfo.periodsInfo.get(position);
            periodInfo.attend = fullDayStatus + "";
            attendanceInfo.periodsInfo.set(position, periodInfo);
        }
        currArrayStudents.set(pos, attendanceInfo);
    }

    private void setAllStatusReturn(int pos, ArrayList<Childbeans> temparray, String reason1) {
        StudentAttendanceInfo attendanceInfo = currArrayStudents.get(pos);
        int counter = 0;
        for (int position = 1; position < temparray.size(); position++) {
            Childbeans periodInfo = temparray.get(position);
            periodInfo.attend = temparray.get(position).attend1;
            periodInfo.reason = reason1;
            attendanceInfo.periodsInfo.set(counter, periodInfo);
            counter++;
        }
        currArrayStudents.set(pos, attendanceInfo);
    }

  /*  private void setStatus(int pos, int position, int status) {
        StudentAttendanceInfo attendanceInfo = currArrayStudents.get(pos);
        Childbeans periodInfo = attendanceInfo.periodsInfo.get(position);
        Log.e("add period", attendanceInfo.periodsInfo.get(position).attend);
        periodInfo.attend = status + "";
        Log.e("After add period", periodInfo.attend);
        attendanceInfo.periodsInfo.set(position, periodInfo);
        currArrayStudents.set(pos, attendanceInfo);

    }*/

    private int getStatus(int pos, int position) {
        int status = Integer.valueOf(currArrayStudents.get(pos).periodsInfo.get(position).attend);
        return status;
    }

    public void showHelpDlg() {
        if (myalertDialog != null && myalertDialog.isShowing())
            myalertDialog.dismiss();

        myalertDialog = new Dialog(mActivity);
        myalertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myalertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myalertDialog.setContentView(R.layout.adres_item_information);
        myalertDialog.setCanceledOnTouchOutside(true);
        myalertDialog.show();
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        private DatePickerDialog datepic;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            isdatasaved = false;
            datepic = new DatePickerDialog(FragmentAttendance.this, this, year, month, day);
            Calendar c1 = Calendar.getInstance();
            int currentyear = c1.get(Calendar.YEAR);
            c1.set(startingyr, 07, 15);
            datepic.getDatePicker().setMinDate(c1.getTimeInMillis() - 10000);
            c1.set(current_yr, 07, 14);
            datepic.getDatePicker().setMaxDate(c1.getTimeInMillis());
            datepic.setButton(DatePickerDialog.BUTTON_POSITIVE, getResources().getString(R.string.str_done), datepic);
            datepic.setButton(DatePickerDialog.BUTTON_NEGATIVE, getResources().getString(R.string.str_cancel), datepic);
            return datepic;
        }

        public void onDateChanged(DatePicker view, int year, int month, int day) {

        }

        @Override
        public void onDateSet(DatePicker view, int yy, int mon, int dy) {
            String finalDate = null;
            String datedummyy = String.valueOf(yy) + "-" + String.valueOf(mon + 1) + "-" + String.valueOf(dy);

            year = yy;
            month = mon;
            day = dy;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date myDate = null;
            try {
                myDate = dateFormat.parse(datedummyy);
                finalDate = dateFormat.format(myDate);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            txtDate.setText(ApplicationData.convertToNorweiDateyeartime(finalDate, mActivity));
            initStudentList();

        }

    }

    private void initStudentList() {
        String dt = ApplicationData.convertToNorwei(txtDate.getText().toString(), mActivity);
       /* if (class_id == null || class_id.isEmpty() || class_id.equals("null")) {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_class), false);
            return;
        }*/
        String url = ApplicationData.getlanguageAndApi(FragmentAttendance.this, ConstantApi.GET_ATTENDANCE_CLASS_TEACHER)
                + "class_id=" + class_id
                + "&date=" + dt
                + "&teacher_id=" + teacher_id;

        if (!GlobalConstrants.isWifiConnected(FragmentAttendance.this)) {
            return;
        }
        if (pDialog == null)
            pDialog = new MainProgress(FragmentAttendance.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_loading));
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(FragmentAttendance.this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String flag = response.getString("flag");
                            pDialog.dismiss();
                            if (Integer.parseInt(flag) == 1) {

                                JSONArray allStudents = response.getJSONArray("allStudents");
                                JSONArray allPeriods = response.getJSONArray("allPeriods");
                                JSONArray absentStudents = response.getJSONArray("absentStudents");
                                JSONArray pnoticeStudents = response.getJSONArray("pnoticeStudents");
                                JSONArray allReasons = response.getJSONArray("allReasons");

                                if (arrayAllStudents != null)
                                    arrayAllStudents.clear();
                                if (arrayAbsentStudents != null)
                                    arrayAbsentStudents.clear();
                                if (arrayPnoticeStudents != null)
                                    arrayPnoticeStudents.clear();
                                if (arrayStudents != null)
                                    arrayStudents.clear();

                                if (currArrayStudents != null)
                                    currArrayStudents.clear();

                                if (arrayList_templete != null)
                                    arrayList_templete.clear();
                                arrayAllStudents = new ArrayList<Childbeans>();

                                arrayAbsentStudents = new ArrayList<Childbeans>();
                                arrayPnoticeStudents = new ArrayList<Childbeans>();

                                arrayStudents = new ArrayList<StudentAttendanceInfo>();
                                currArrayStudents = new ArrayList<StudentAttendanceInfo>();
                                nochangeStudents = new ArrayList<StudentAttendanceInfo>();

                                arrayList_templete = new ArrayList<Childbeans>();

                                if (arrayAllPeriods != null)
                                    arrayAllPeriods.clear();


                                for (int i = 0; allStudents.length() > i; i++) {  //allStudents : (class_id, user_id, name, image, birthday, parent_id)
                                    JSONObject c = allStudents.getJSONObject(i);

                                    StudentAttendanceInfo attendanceInfo = new StudentAttendanceInfo();
                                    StudentAttendanceInfo orgattendanceInfo = new StudentAttendanceInfo();
                                    StudentAttendanceInfo nochangeinfo = new StudentAttendanceInfo();

                                    attendanceInfo.user_id = c.getString("user_id");
                                    attendanceInfo.class_id = c.getString("class_id");
                                    attendanceInfo.name = c.getString("name");
                                    attendanceInfo.image = c.getString("image");

                                    orgattendanceInfo.user_id = c.getString("user_id");
                                    orgattendanceInfo.class_id = c.getString("class_id");
                                    orgattendanceInfo.name = c.getString("name");
                                    orgattendanceInfo.image = c.getString("image");


                                    arrayAllPeriods = new ArrayList<Childbeans>();
                                    orgArrayAllPeriods = new ArrayList<Childbeans>();
                                    ArrayList<Childbeans> nochageallperiods = new ArrayList<Childbeans>();

                                    for (int iperiod = 0; allPeriods.length() > iperiod; iperiod++) {
                                        //allPeriods : (lecture_no, period_id, time, subject_name, teacher_id)  : ""1"" ""98"" ""09:00 - 10:00""  ""English"" ""334""

                                        JSONObject cperiod = allPeriods.getJSONObject(iperiod);
                                        if (!cperiod.get("class_id").equals(attendanceInfo.class_id)) {
                                            continue;
                                        }
                                        /*else*/
                                        {
                                            Childbeans periodbeans = new Childbeans();
                                            Childbeans orgperiodbeans = new Childbeans();
                                            Childbeans nochangebean = new Childbeans();
                                            periodbeans.lecture_no = cperiod.getString("lecture_no");
                                            periodbeans.child_period_id = cperiod.getString("period_id");
                                            periodbeans.chat_time = cperiod.getString("time");
                                            periodbeans.subject_name = cperiod.getString("subject_name");
                                            periodbeans.teacher_id = cperiod.getString("teacher_id");
                                            periodbeans.class_id = cperiod.getString("class_id");

                                            orgperiodbeans.lecture_no = cperiod.getString("lecture_no");
                                            orgperiodbeans.child_period_id = cperiod.getString("period_id");
                                            orgperiodbeans.chat_time = cperiod.getString("time");
                                            orgperiodbeans.subject_name = cperiod.getString("subject_name");
                                            orgperiodbeans.teacher_id = cperiod.getString("teacher_id");
                                            orgperiodbeans.class_id = cperiod.getString("class_id");

                                            periodbeans.attend = "1";  //attend
                                            orgperiodbeans.attend1 = "1";
                                            nochangebean.attend = "1";


                                            for (int inotice = 0; pnoticeStudents.length() > inotice; inotice++) {  //absentStudents : (date, user_id, name, image, lecture_no, attend, period_id, subject_id, teacher_id )
                                                JSONObject cnotice = pnoticeStudents.getJSONObject(inotice);

                                                if (!attendanceInfo.user_id.equals(cnotice.getString("user_id")) || !periodbeans.lecture_no.equals(cnotice.getString("lecture_no"))
                                                        || !periodbeans.child_period_id.equals(cnotice.getString("period_id"))) {
                                                    continue;
                                                }
                                                String tempreason = "";
                                               /* try {
                                                    tempreason= URLDecoder.decode(cnotice.getString("reason"), "utf-8");
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                }*/

                                                // String tempreason=URLEncoder.encode(cnotice.getString("reason"),"utf-8");
                                                periodbeans.attend = "2"; //notice
                                                orgperiodbeans.attend1 = "2";
                                                nochangebean.attend = "2";
                                                periodbeans.reason = cnotice.getString("reason");
                                                ;//cnotice.getString("reason");
                                                orgperiodbeans.reason1 = cnotice.getString("reason");
                                                ;//cnotice.getString("reason");
                                                nochangebean.reason = cnotice.getString("reason");
                                                ;//cnotice.getString("reason");
                                                break;
                                            }

                                            for (int iabsent = 0; absentStudents.length() > iabsent; iabsent++) {  //absentStudents : (date, user_id, name, image, lecture_no, attend, period_id, subject_id, teacher_id )
                                                JSONObject cabsent = absentStudents.getJSONObject(iabsent);

                                                if (!attendanceInfo.user_id.equals(cabsent.getString("user_id")) || !periodbeans.lecture_no.equals(cabsent.getString("lecture_no")) || !periodbeans.child_period_id.equals(cabsent.getString("period_id"))) {
                                                    continue;
                                                }

                                                if (cabsent.getString("attend").equals("0")) {
                                                    periodbeans.attend = "0"; //absent
                                                    orgperiodbeans.attend1 = "0";
                                                    nochangebean.attend = "0";
                                                }

                                                String tempreason = "";
                                                try {
                                                    tempreason = URLDecoder.decode(cabsent.getString("reason"), "utf-8");
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                }

                                                periodbeans.reason = cabsent.getString("reason");
                                                orgperiodbeans.reason1 = cabsent.getString("reason");
                                                nochangebean.reason = cabsent.getString("reason");
                                                break;
                                            }

                                            arrayAllPeriods.add(periodbeans);
                                            orgArrayAllPeriods.add(orgperiodbeans);
                                            nochageallperiods.add(nochangebean);
                                        }
                                    }
                                    attendanceInfo.periodsInfo = new ArrayList<>();
                                    orgattendanceInfo.periodsInfo1 = new ArrayList<>();
                                    nochangeinfo.periodsInfo = nochageallperiods;
                                    //attendanceInfo.periodsInfo= arrayAllPeriods;
                                    attendanceInfo.periodsInfo.addAll(arrayAllPeriods);
                                    orgattendanceInfo.periodsInfo1.addAll(orgArrayAllPeriods);
                                    arrayStudents.add(orgattendanceInfo);
                                    currArrayStudents.add(attendanceInfo);
                                    nochangeStudents.add(nochangeinfo);
                                }

                                for (int j = 0; allReasons.length() > j; j++) {
                                    JSONObject c = allReasons.getJSONObject(j);
                                    Childbeans childbeans = new Childbeans();
                                    childbeans.child_temlate_id = c.getString("template_id");
//									childbeans.child_school_id = c.getString("school_id");
                                    childbeans.child_template_title = c.getString("template_title");

                                    /*if (text.getText().toString().equals("") || text.getText().toString().equals("null"))
                                    {
                                        text.setText(templete_list_arr[0]);
                                    }*/
                                    arrayList_templete.add(childbeans);
                                }

                                aAdapter = new AttendanceListAdapter(mActivity, currArrayStudents);
                                lstStudent.setAdapter(aAdapter);
                                //aAdapter.updateReceiptsList(currArrayStudents);

                            } else {
                                if (response.has("msg")) {
                                    String msg = response.getString("msg");
                                    ApplicationData.showToast(mActivity, msg, false);
                                }
                                /*else
                                    ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);*/
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

    private void showDatePicker() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "DatePicker");
    }

    private void loadClasses(String url) {
        // TODO Auto-generated method stub
        if (!GlobalConstrants.isWifiConnected(FragmentAttendance.this)) {
            return;
        }
        if (pDialog == null)
            pDialog = new MainProgress(FragmentAttendance.this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(FragmentAttendance.this);

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
                                initClass();
                            } else {
                                pDialog.dismiss();
                                if (response.has("msg")) {
                                    String msg = response.getString("msg");
                                    ApplicationData.showToast(mActivity, msg, false);
                                } else
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

    private void initClass() {
        arrayClass = new ArrayList<Childbeans>();
        grade_id = "";
        classes = null;

        Childbeans blankBean = new Childbeans();
        blankBean.class_id = "0";
        blankBean.class_name = getResources().getString(R.string.select_class);
        arrayClass.add(blankBean);

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
            classes[j] = arrayClass.get(j).class_name;
        }

        AttendanceSpinnerListAdapter adapter = new AttendanceSpinnerListAdapter(mActivity, classes);
        txtClass.setAdapter(adapter);


        // class_id = arrayClass.get(1).class_id;

        initStudentList();
    }

    public void onLowMemory() {
        super.onLowMemory();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        pDialog=null;
        teacher_id=null; school_id=null; grade_id=null; class_id = null;
        strAbsentDatas = null; strNoticeDatas = null;
        strAddedNoticeDatas = null; absentreason = null; noticereason = null;
        arrayClass = null;
        arrayAllClasses = null;
        arrayAllStudents = null; orgArrayAllPeriods = null;
        arrayAllPeriods = null; arrayAbsentStudents = null; arrayPnoticeStudents = null;
        arrayStudents = null; currArrayStudents=null; nochangeStudents = null;
        lstStudent=null;
        aAdapter = null;
        adAdapter = null;
        classes=null;
        arrList=null;
        txtDate=null; txtTitle=null;
        txtClass=null;
        btnHelp=null; btnSave=null; btnSend=null; lytClass=null; lytFullDay=null;
        imgFullDay=null;
        myalertDialog = null;
        dlg=null;
         mActivity=null;
        list_reason=null;
        txtreason=null; btndone=null; txt_Cancel=null;
        arrayList_templete=null;
        img_profile=null;
        text_class=null;
        reason1 =null; reason2 = null;
        inflater=null;
        rootView=null;

        System.gc();
    }
}
