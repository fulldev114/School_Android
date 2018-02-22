package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.teacher.Childbeans;
import com.adapter.teacher.GeneratePdfAdapter;
import com.adapter.teacher.ReportAdapter;
import com.cloudstream.cslink.R;
import com.common.Bean.MarkBean;
import com.common.FileDownloader;
import com.common.dialog.MainProgress;
import com.common.utils.AppUtils;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.langsetting.apps.Change_lang;
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;
import com.xmpp.teacher.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by etech on 5/7/16.
 */
public class ReportCardActivity extends ActivityHeader implements View.OnClickListener, AsyncTaskCompleteListener<String> {

    String url, _parent_id, child_id, child_name;
    private ListView expand_report, expand_sub;
    private ListView lin_sem;
    ArrayList<String> array_yr = new ArrayList<String>();
    private int current_yr;
    private int startingyr = 1997;
    private Spinner spn_yr;
    private String selected_year = "", school_id, class_id;
    private ArrayList<Childbeans> semesterarray, subjectarray, characterarray, detailmarkarray;
    private HashMap<String, ArrayList<Childbeans>> studentmarkarray;
    ArrayList<String> semester_name = new ArrayList<String>();
    private SemesterlistAdapter listadapter;
    private String semester_ids = "0", _teacher_id, _teacher_image;
    // private MarkListAdapter markadapter;
    private ReportAdapter markadapter;
    private Childbeans data;
    private Change_lang change_lang;
    private String[] arraysemid;
    private ImageView img_dropdown;
    private View view_system;
    private TextView txt_custom, txt_system;
    private RelativeLayout rel_system, rel_custom, rel_markview, rel_download;
    private Animation animation, animleft;
    private LinearLayout lyt_save, lyt_download, lin_note, lyt_cancel;
    private GeneratePdfAdapter selectsubadapter;
    private ArrayList<Childbeans> studentlist = new ArrayList<Childbeans>();
    private static String SDCARD_PERMISSIONS[] = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private final static int REQUEST_CONTACTS_CODE = 100;
    private LayoutInflater inflater;
    private LinearLayout screenview, lin_chk;
    private CheckBox chkbox;
    private String including_image = "no";

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    /*    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.report_card);*/
        inflater = getLayoutInflater();
        screenview = (LinearLayout) inflater.inflate(R.layout.adres_report_card, null);
        relwrapp.addView(screenview);

        ApplicationData.setReportActivity(ReportCardActivity.this);
        SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        change_lang = new Change_lang(getApplicationContext());
        _teacher_id = sharedpref.getString("teacher_id", "");
        _teacher_image = sharedpref.getString("image", "");
        school_id = sharedpref.getString("school_id", "");

        init();

       /* txt_title.setTextColor(getResources().getColor(R.color.color_blue_p));
        txt_title.setText(getString(R.string.marks));*/
        //show header
        showheaderusermenu(ReportCardActivity.this, "", R.color.white_light, true);//marks//headermark_title


        if (getIntent().hasExtra("child_detail")) {
            data = (Childbeans) getIntent().getSerializableExtra("child_detail");
            class_id = (data.class_id != null && data.class_id.length() > 0) ? data.class_id : "";
            child_id = (data.sender_id != null && data.sender_id.length() > 0) ? data.sender_id : "";
            child_name = (data.child_name != null && data.child_name.length() > 0) ? data.child_name : "";

            subject.setText((data.class_name != null && data.class_name.length() > 0) ? data.class_name : "");
            if (data.child_image != null && data.child_image.length() > 0) {
                String url = ApplicationData.web_server_url + "uploads/" + data.child_image;
                ApplicationData.setProfileImg(profileimage, url, ReportCardActivity.this);
            }
            name.setText(child_name);
        }

        if (getIntent().hasExtra("studentarray")) {
            studentlist = (ArrayList<Childbeans>) getIntent().getSerializableExtra("studentarray");
            studentlist.clear();
            studentlist.add(data);
        }


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

        //  array_yr.add(getString(R.string.select_yr));
        //create year array list
        /*if (current_yr == 1997)
            array_yr.add("1997-1997");
        else {
            for (int i = 0; i < (current_yr - 1997); i++) {
                array_yr.add(String.valueOf(startingyr) + "-" + String.valueOf(startingyr + 1));
                startingyr++;
            }
            Collections.reverse(array_yr);
        }
*/
        //back from activity
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
      /*  ReportYearAdapter adapter = new ReportYearAdapter(ReportCardActivity.this, array_yr);
        spn_yr.setAdapter(adapter);
        spn_yr.setSelection(0);
        selected_year = array_yr.get(0);

        spn_yr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_year = array_yr.get(position);

                if (!selected_year.equalsIgnoreCase(getString(R.string.select_yr)) && !semester_ids.equalsIgnoreCase("0")) {
                    callapi();
                    img_dropdown.setVisibility(View.GONE);
                }
                *//*else if(!selected_year.equalsIgnoreCase(getString(R.string.select_yr)))
                {
                    img_dropdown.setVisibility(View.GONE);
                }
                else
                    img_dropdown.setVisibility(View.VISIBLE);*//*
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/

        //get semester list
        callsemesterapi();

    }

    private void init() {
        lin_sem = (ListView) findViewById(R.id.lin_sem);
        expand_report = (ListView) findViewById(R.id.expand_report);
        spn_yr = (Spinner) findViewById(R.id.spn_yr);
        //  txt_title = (TextView) findViewById(R.id.textView1);
        //  imgback = (ImageView) findViewById(R.id.imgback);
        img_dropdown = (ImageView) findViewById(R.id.img_dropdown);
        view_system = (View) findViewById(R.id.view_system);
        txt_custom = (TextView) findViewById(R.id.txt_custom);
        txt_system = (TextView) findViewById(R.id.txt_system);
        rel_system = (RelativeLayout) findViewById(R.id.rel_system);
        rel_custom = (RelativeLayout) findViewById(R.id.rel_custom);
        rel_markview = (RelativeLayout) findViewById(R.id.rel_markview);
        rel_download = (RelativeLayout) findViewById(R.id.rel_download);
        lyt_save = (LinearLayout) findViewById(R.id.lyt_save);
        lyt_download = (LinearLayout) findViewById(R.id.lyt_download);
        lin_note = (LinearLayout) findViewById(R.id.lin_note);
        expand_sub = (ListView) findViewById(R.id.expand_sub);
        lyt_cancel = (LinearLayout) findViewById(R.id.lyt_cancel);
        lin_chk = (LinearLayout) findViewById(R.id.lin_chk);
        chkbox = (CheckBox) findViewById(R.id.chkbox);
        rel_custom.setOnClickListener(ReportCardActivity.this);
        rel_system.setOnClickListener(ReportCardActivity.this);
        lyt_save.setOnClickListener(ReportCardActivity.this);
        lyt_download.setOnClickListener(ReportCardActivity.this);
        lyt_cancel.setOnClickListener(ReportCardActivity.this);

        animation = AnimationUtils.loadAnimation(ReportCardActivity.this, R.anim.right_to_left);
        animleft = AnimationUtils.loadAnimation(ReportCardActivity.this, R.anim.left_to_right);
        //ScreenAnimation.startAnimation(RightSwipe);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // lyt_save.setAnimation(animleft);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rel_download.setVisibility(View.VISIBLE);
                    }
                }, 500);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rel_markview.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animleft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rel_markview.setVisibility(View.VISIBLE);
                    }
                }, 500);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rel_download.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        lin_chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkbox.isChecked()) {
                    chkbox.setChecked(false);
                    including_image = "no";
                } else {
                    chkbox.setChecked(true);
                    including_image = "yes";
                }
            }
        });

        chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkbox.setChecked(true);
                    including_image = "yes";
                } else {
                    chkbox.setChecked(false);
                    including_image = "no";
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        int selectedval = 0;
        if (v.getId() == R.id.rel_system) {
            selectedval = 1;
            semester_ids = semesterarray.get(0).semester_id;
            changelayout(selectedval);
            callapi();
        } else if (v.getId() == R.id.rel_custom) {
            selectedval = 2;
            semester_ids = semesterarray.get(1).semester_id;
            changelayout(selectedval);
            callapi();
        } else if (v.getId() == R.id.lyt_save) {

            rel_download.setVisibility(View.VISIBLE);
            rel_markview.setVisibility(View.GONE);
            // rel_download.startAnimation(animation);
            selectsubadapter = new GeneratePdfAdapter(ReportCardActivity.this, detailmarkarray, semester_name, expand_report, data);
            expand_sub.setAdapter(selectsubadapter);

        } else if (v.getId() == R.id.lyt_download) {
            if (selectsubadapter != null && detailmarkarray != null) {
                //  List<Childbeans> subtopdfarray = new ArrayList<Childbeans>();
                String selected_subjects_id = "";
                if (detailmarkarray.size() > 0) {
                    selected_subjects_id = selectsubadapter.getSelectedSubId(ReportCardActivity.this);
                    if (selected_subjects_id != null && selected_subjects_id.length() > 0) {
                        //final List<Childbeans> finalSubtopdfarray = subtopdfarray;
                        final String finalSelected_subjects_id = selected_subjects_id;
                        ApplicationData.calldialog(ReportCardActivity.this, "", getString(R.string.send_mail), getString(R.string.str_yes), getString(R.string.str_no), new ApplicationData.DialogListener() {
                            @Override
                            public void diaBtnClick(int diaID, int btnIndex) {
                                String sendmail = "no";
                                if (btnIndex == 2) {
                                    sendmail = "yes";
                                } else if (btnIndex == 1) {
                                    sendmail = "no";
                                }
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                                    if (AppUtils.hasSelfPermission(ReportCardActivity.this, SDCARD_PERMISSIONS)) {
                                        generetePdfApi(finalSelected_subjects_id, data, "", sendmail, including_image);
                                        // ApplicationData.createMarkPdf(ReportCardActivity.this, finalSelected_subjects_id, data, studentlist, sendmail);
                                    } else {
                                        ApplicationData.isphoto = true;
                                        requestPermissions(SDCARD_PERMISSIONS, REQUEST_CONTACTS_CODE);
                                    }
                                } else {
                                    generetePdfApi(finalSelected_subjects_id, data, "", sendmail, including_image);
                                    // ApplicationData.createMarkPdf(ReportCardActivity.this, finalSubtopdfarray, data, studentlist, sendmail);
                                }

                            }

                        }, 2);

                    } else {
                        try {
                            ApplicationData.showMessage(ReportCardActivity.this, "", getString(R.string.error_sub), getString(R.string.str_ok));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            // rel_download.startAnimation(animleft);
        } else if (v.getId() == R.id.lyt_cancel) {
            including_image = "no";
            chkbox.setChecked(false);
            rel_download.setVisibility(View.GONE);
            rel_markview.setVisibility(View.VISIBLE);
            // rel_download.startAnimation(animleft);
        }

    }

    private void generetePdfApi(String selected_subjects_id, Childbeans data, String test_id, String sendmail, String including_image) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("teacher_id", _teacher_id);
        map.put("student_id", data.sender_id);
        map.put("selected_subjects_id", selected_subjects_id);
        map.put("subject_id", "");
        map.put("class_id", data.class_id);
        map.put("include_image", including_image);
        map.put("send_email", sendmail);
        map.put("test_id", "");
        map.put("semester_id", semester_ids);

        if (!GlobalConstrants.isWifiConnected(ReportCardActivity.this)) {
            return;
        } else {
            ETechAsyncTask task = new ETechAsyncTask(ReportCardActivity.this, this, ConstantApi.GET_MARK_PDF, map, 2);
            task.execute(ApplicationData.web_server_url + ConstantApi.GET_MARK_PDF);
        }
    }

    private void callsemesterapi() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("class_id", class_id);
        map.put("school_id", school_id);
        map.put("user_id", child_id);
        if (!GlobalConstrants.isWifiConnected(ReportCardActivity.this)) {
            return;
        } else {
            ETechAsyncTask task = new ETechAsyncTask(ReportCardActivity.this, this, ConstantApi.GET_SUBJECT_SEMESTER, map);
            task.execute(ApplicationData.main_url + ConstantApi.GET_SUBJECT_SEMESTER + ".php?");
        }
    }

    @Override
    public void onTaskComplete(int statusCode, String result, String webserviceCb, Object tag) {
        try {
            if (statusCode == ETechAsyncTask.COMPLETED) {
                JSONObject jObject = new JSONObject(result);
                try {
                    if (webserviceCb.equalsIgnoreCase(ConstantApi.GET_SUBJECT_SEMESTER)) {
                        String flag = jObject.getString("flag");

                        if (flag.equalsIgnoreCase("1")) {
                            if (semesterarray != null) semesterarray.clear();
                            if (subjectarray != null) subjectarray.clear();
                            if (characterarray != null) characterarray.clear();
                            if (studentmarkarray != null) studentmarkarray.clear();

                            semesterarray = new ArrayList<Childbeans>();
                            subjectarray = new ArrayList<Childbeans>();
                            characterarray = new ArrayList<Childbeans>();


                            //JsonParsing of Semesterarray
                            if (jObject.has("semester_list")) {

                                JSONArray jseme = jObject.getJSONArray("semester_list");
                                for (int sem = 0; sem < jseme.length(); sem++) {
                                    JSONObject jobsem = jseme.getJSONObject(sem);
                                    Childbeans bean = new Childbeans();
                                    if (jobsem.has("semester_id"))
                                        bean.semester_id = jobsem.getString("semester_id");
                                    if (jobsem.has("semester_name"))
                                        bean.semester_name = jobsem.getString("semester_name");

                                    if (sem == 0) {
                                        txt_system.setText(jobsem.getString("semester_name"));
                                        semester_ids = jobsem.getString("semester_id");
                                    } else if (sem == 1) {
                                        txt_custom.setText(jobsem.getString("semester_name"));
                                    }

                                    semesterarray.add(bean);
                                }
                            }

                            //JsonParsing of subject_list
                            if (jObject.has("subject_list")) {

                                JSONArray jsub = jObject.getJSONArray("subject_list");
                                for (int sub = 0; sub < jsub.length(); sub++) {
                                    JSONObject jobsub = jsub.getJSONObject(sub);
                                    Childbeans bean = new Childbeans();
                                    if (jobsub.has("subject_id"))
                                        bean.subject_id = jobsub.getString("subject_id");
                                    if (jobsub.has("subject_name"))
                                        bean.subject_name = jobsub.getString("subject_name");
                                    if (jobsub.has("class_id"))
                                        bean.class_id = jobsub.getString("class_id");

                                    subjectarray.add(bean);
                                }
                            }

                            //JsonParsing of character_list
                            if (jObject.has("character_list")) {

                                JSONArray jchar = jObject.getJSONArray("character_list");
                                for (int cha = 0; cha < jchar.length(); cha++) {
                                    JSONObject jobchar = jchar.getJSONObject(cha);
                                    Childbeans bean = new Childbeans();
                                    if (jobchar.has("character_id"))
                                        bean.character_id = jobchar.getString("character_id");
                                    if (jobchar.has("character_name"))
                                        bean.character_name = jobchar.getString("character_name");
                                    if (jobchar.has("school_id"))
                                        bean.school_id = jobchar.getString("school_id");

                                    characterarray.add(bean);
                                }
                            }
                            callapi();
                            // setadapter();
                        } else {
                            if (jObject.has("msg")) {
                                String msg = jObject.getString("msg");
                                ApplicationData.showToast(ReportCardActivity.this, msg, false);
                            } else {
                                ApplicationData.showToast(ReportCardActivity.this, R.string.no_record, false);
                            }
                        }

                    } else if (webserviceCb.equalsIgnoreCase(ConstantApi.GET_MARK_PARENT)) {
                        String flag = jObject.getString("flag");

                        if (flag.equalsIgnoreCase("1")) {
                            semester_name = new ArrayList<String>();
                            studentmarkarray = new HashMap<String, ArrayList<Childbeans>>();
                            detailmarkarray = new ArrayList<Childbeans>();

                            if (jObject.has("marks_details")) {
                                JSONArray jmark = jObject.getJSONArray("marks_details");
                                if (jmark.length() > 0) {
                                    for (int i = 0; i < jmark.length(); i++) {
                                        JSONObject jobmark = jmark.getJSONObject(i);

                                        int k = 0;
                                        //markarray = new ArrayList<Childbeans>();

                                       /* for (int j = 0; j < juser.length(); j++) {
                                            JSONObject jobmark = juser.getJSONObject(j);*/

                                        Childbeans bean = new Childbeans();
                                        if (jobmark.has("user_id"))
                                            bean.user_id = jobmark.getString("user_id");
                                        if (jobmark.has("year"))
                                            bean.year = jobmark.getString("year");
                                        if (jobmark.has("semester_id"))
                                            bean.semester_id = jobmark.getString("semester_id");
                                        if (jobmark.has("class_id"))
                                            bean.class_id = jobmark.getString("class_id");
                                        if (jobmark.has("class_name"))
                                            bean.class_name = jobmark.getString("class_name");
                                        if (jobmark.has("subject_id"))
                                            bean.subject_id = jobmark.getString("subject_id");
                                        if (jobmark.has("created_at"))
                                            bean.created_at = jobmark.getString("created_at");
                                        if (jobmark.has("semester_name"))
                                            bean.semester_name = jobmark.getString("semester_name");
                                        if (jobmark.has("subject_name"))
                                            bean.subject_name = jobmark.getString("subject_name");
                                        if (jobmark.has("teacher_name"))
                                            bean.teacher_name = jobmark.getString("teacher_name");
                                        if(jobmark.has("user_name"))
                                            bean.child_name=jobmark.getString("user_name");
                                        if (jobmark.has("marks")) {
                                            JSONArray jmarkarray = jobmark.getJSONArray("marks");
                                            if (jmarkarray.length() > 0) {
                                                for (int l = 0; l < jmarkarray.length(); l++) {
                                                    JSONObject jmarkobj = jmarkarray.getJSONObject(l);
                                                    MarkBean jmarkbean = new MarkBean();
                                                    if (jmarkobj.has("exam_about"))
                                                        jmarkbean.exam_about = jmarkobj.getString("exam_about");
                                                    if (jmarkobj.has("exam_no"))
                                                        jmarkbean.exam_no = jmarkobj.getString("exam_no");
                                                    if (jmarkobj.has("marks")) {
                                                        jmarkbean.mark = jmarkobj.getString("marks");
                                                        if (jmarkobj.getString("marks") != null && jmarkobj.getString("marks").length() > 0)
                                                            jmarkbean.mark = jmarkobj.getString("marks");
                                                        else
                                                            jmarkbean.mark = getString(R.string.no_rate);
                                                    }
                                                    if (jmarkobj.has("comment")) {
                                                        jmarkbean.comment = jmarkobj.getString("comment");// URLDecoder.decode(, "utf-8");
                                                        if (jmarkobj.getString("comment") != null && jmarkobj.getString("comment").length() > 0)
                                                            jmarkbean.comment = jmarkobj.getString("comment");
                                                        else
                                                            jmarkbean.comment = getString(R.string.no_comment);
                                                    }
                                                    if (jmarkobj.has("image"))
                                                        jmarkbean.image = jmarkobj.getString("image");
                                                    if (jmarkobj.has("exam_date"))
                                                        jmarkbean.exam_date = jmarkobj.getString("exam_date");

                                                    bean.markarray.add(jmarkbean);
                                                }
                                            }
                                        }
                                        bean.child_image=data.child_image;
                                        detailmarkarray.add(bean);
                                        k++;
                                        semester_name.add(detailmarkarray.get(k - 1).semester_name);
                                        studentmarkarray.put(detailmarkarray.get(k - 1).semester_name, detailmarkarray);


                                    }
                                    rel_markview.setVisibility(View.VISIBLE);
                                    lin_note.setVisibility(View.GONE);
                                } else {
                                    arraysemid = semester_ids.split(",");
                                    if (arraysemid != null && arraysemid.length > 0) {
                                        for (int val = 0; val < semesterarray.size(); val++) {
                                            if (semester_ids.equalsIgnoreCase(semesterarray.get(val).semester_id)) {
                                                semester_name.add(semesterarray.get(val).semester_name);
                                                break;
                                            }
                                        }
                                        detailmarkarray = new ArrayList<Childbeans>();
                                        studentmarkarray.put(semester_name.get(0), detailmarkarray);
                                    }
                                    rel_markview.setVisibility(View.GONE);
                                    lin_note.setVisibility(View.VISIBLE);
                                }
                                expand_report.setVisibility(View.VISIBLE);
                                markadapter = new ReportAdapter(ReportCardActivity.this, detailmarkarray, semester_name, expand_report, data);
                                expand_report.setAdapter(markadapter);
                                // ApplicationData.setexpandListViewHeightBasedOnChildren(expand_report);
                            }
                        } else {
                            if (jObject.has("msg")) {
                                String msg = jObject.getString("msg");
                                ApplicationData.showToast(ReportCardActivity.this, msg, false);
                            } else
                                ApplicationData.showToast(ReportCardActivity.this, R.string.no_record, false);

                            expand_report.setVisibility(View.GONE);
                        }

                    } else if (webserviceCb.equalsIgnoreCase(ConstantApi.GET_MARK_PDF)) {
                        String flag = jObject.getString("flag");
                        if (flag.equalsIgnoreCase("1")) {
                            String url = jObject.has("url") ? jObject.getString("url") : "";
                            new DownloadPDF(ReportCardActivity.this, url).execute();
                        } else {
                            if (jObject.has("msg")) {
                                String msg = jObject.getString("msg");
                                ApplicationData.showToast(ReportCardActivity.this, msg, false);
                            }
                        }
                    } else {
                        ApplicationData.showToast(ReportCardActivity.this, R.string.server_error, false);
                    }

                    //setAdapter();
                } catch (Exception e) {
                    Log.e("ReportCardActivity", "onTaskComplete() " + e, e);
                }
            } else if (statusCode == ETechAsyncTask.ERROR_NETWORK) {
                try {
                    ApplicationData.showToast(ReportCardActivity.this, R.string.msg_operation_error, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setadapter() {

       /* listadapter = new SemesterlistAdapter(ReportCardActivity.this, semesterarray, expand_report);
        lin_sem.setAdapter(listadapter);
        ApplicationData.setListViewHeightBasedOnChildren(lin_sem);*/
    }

    private void callapi() {
        selected_year = startingyr + "-" + current_yr;
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", child_id);
        map.put("year", selected_year);
        map.put("semester_ids", semester_ids);
        map.put("class_id", class_id);
        map.put("subject_id", "");
        if (!GlobalConstrants.isWifiConnected(ReportCardActivity.this)) {
            return;
        } else {
            ETechAsyncTask task = new ETechAsyncTask(ReportCardActivity.this, this, ConstantApi.GET_MARK_PARENT, map);
            task.execute(ApplicationData.main_url + ConstantApi.GET_MARK_PARENT + ".php?");
        }
    }


    private void changelayout(int selectedval) {
        rel_download.setVisibility(View.GONE);
        rel_markview.setVisibility(View.VISIBLE);
        including_image = "no";
        chkbox.setChecked(false);
        if (selectedval == 1) {
            rel_system.setBackgroundResource(R.drawable.view_line_top_background);
            rel_custom.setBackgroundResource(R.drawable.view_line_bottom_background);

            txt_system.setTextColor(getResources().getColor(R.color.color_blue_p));
            txt_custom.setTextColor(getResources().getColor(R.color.white_light));

        } else if (selectedval == 2) {
            rel_system.setBackgroundResource(R.drawable.view_line_bottom_background);
            rel_custom.setBackgroundResource(R.drawable.view_line_top_background);

            txt_system.setTextColor(getResources().getColor(R.color.white_light));
            txt_custom.setTextColor(getResources().getColor(R.color.color_blue_p));
        }
    }

    public void updatedetail(LinearLayout userprofile, MarkBean mark_detail, int parentposition, int childpos, boolean fromdownload) {
        detailmarkarray.get(parentposition).markarray.remove(childpos);
        MarkBean mbean = mark_detail;
        detailmarkarray.get(parentposition).markarray.add(childpos, mbean);
        if (fromdownload) {
            selectsubadapter.updatenotify(ReportCardActivity.this, detailmarkarray, semester_name, expand_report, data);
            selectsubadapter.showDetailDialog(detailmarkarray.get(parentposition).markarray.get(childpos), childpos, parentposition);
        } else {
            markadapter.updatenotify(ReportCardActivity.this, detailmarkarray, semester_name, expand_report, data);
            markadapter.showDetailDialog(detailmarkarray.get(parentposition).markarray.get(childpos), childpos, parentposition);
        }
    }

    public class SemesterlistAdapter extends BaseAdapter {
        private final Context context;
        private final ArrayList<Boolean> flag = new ArrayList<Boolean>();
        private ArrayList<Childbeans> list = new ArrayList<Childbeans>();
        private ExpandableListView expand_list;

        public SemesterlistAdapter(Context context, ArrayList<Childbeans> list, ExpandableListView expand_list) {
            this.context = context;
            this.list = list;
            this.expand_list = expand_list;

            for (int i = 0; i < list.size(); i++) {
                flag.add(false);
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_semester, parent, false);
                holder = new ViewHolder();
                holder.rel_sem = (RelativeLayout) convertView.findViewById(R.id.rel_sem);
                holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
                holder.chk_item = (CheckBox) convertView.findViewById(R.id.chk_item);
                holder.view_line = (View) convertView.findViewById(R.id.view_line);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txt_name.setText(list.get(position).semester_name);

            if (position == list.size() - 1)
                holder.view_line.setVisibility(View.GONE);
            else
                holder.view_line.setVisibility(View.VISIBLE);

            holder.rel_sem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v.findViewById(R.id.chk_item);

                    if (checkBox.isChecked()) {
                        flag.set(position, false);
                        checkBox.setChecked(false);

                        if (semester_name != null && semester_name.size() > 0) {
                            for (int loop = 0; loop < semester_name.size(); loop++) {
                                if (semester_name.get(loop).equalsIgnoreCase(list.get(position).semester_name)) {
                                    studentmarkarray.remove(semester_name.get(loop));
                                    semester_name.remove(loop);
                                    break;
                                }
                            }

                            //add semester id which is selected
                            semester_ids = "0";
                            for (int i = 0; i < flag.size(); i++) {
                                if (flag.get(i)) {
                                    if (semester_ids.equalsIgnoreCase("0"))
                                        semester_ids = list.get(i).semester_id;
                                    else
                                        semester_ids = semester_ids + "," + list.get(i).semester_id;
                                }
                            }

                            markadapter.updatenotify(context, detailmarkarray, semester_name, expand_report, data);
                            markadapter.notifyDataSetChanged();
                            //  ApplicationData.setexpandListViewHeightBasedOnChildren(expand_report);
                        }

                    } else {

                        boolean isselected = false;
                        if (!selected_year.equalsIgnoreCase(getString(R.string.select_yr))) {
                            flag.set(position, true);
                            checkBox.setChecked(true);
                            semester_ids = "0";
                            for (int i = 0; i < flag.size(); i++) {
                                if (flag.get(i)) {
                                    if (semester_ids.equalsIgnoreCase("0"))
                                        semester_ids = list.get(i).semester_id;
                                    else
                                        semester_ids = semester_ids + "," + list.get(i).semester_id;

                                    isselected = true;
                                }
                            }

                            if (isselected) {
                                callapi();
                            }
                        } else {

                            ApplicationData.showToast(ReportCardActivity.this, R.string.error_yr, false);
                        }
                    }
                }
            });
           /* holder.chk_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        flag.set(position, true);
                    } else {
                        flag.set(position, false);

                        semesterarray.remove(position);
                        studentmarkarray.remove(position);
                        markadapter.notifyDataSetChanged();
                    }

                }
            });
*/
            holder.chk_item.setChecked(flag.get(position));

           /* boolean isselected = false;
            if (!selected_year.equalsIgnoreCase(getString(R.string.study_yr))) {
                semester_ids="0";
                for (int i = 0; i < flag.size(); i++) {
                    if (flag.get(i)) {
                        if (semester_ids.equalsIgnoreCase("0"))
                            semester_ids = list.get(position).semester_id;
                        else
                            semester_ids = semester_ids + "," + list.get(position).semester_id;

                        isselected = true;
                    }
                }

                if (isselected) {
                    callapi();
                }
            } else {
                ApplicationData.showToast(ReportCardActivity.this, R.string.error_yr, false);
            }*/

            return convertView;
        }

        class ViewHolder {

            public RelativeLayout rel_sem;
            public TextView txt_name;
            public CheckBox chk_item;
            public View view_line;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationData.isphoto = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CONTACTS_CODE) {

            if (AppUtils.verifyAllPermissions(grantResults)) {
                ApplicationData.isphoto = false;
            } else {
                Toast.makeText(ReportCardActivity.this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void calluploadpdf(final Context context, final List<Childbeans> subtopdfarray, final Childbeans child_data, final String including_image, final String test_id) {

        ApplicationData.calldialog(ReportCardActivity.this, "", getString(R.string.send_mail), getString(R.string.str_yes), getString(R.string.str_no), new ApplicationData.DialogListener() {
            @Override
            public void diaBtnClick(int diaID, int btnIndex) {
                String sendmail = "no";
                if (btnIndex == 2) {
                    sendmail = "yes";
                } else if (btnIndex == 1) {
                    sendmail = "no";
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (AppUtils.hasSelfPermission(context, SDCARD_PERMISSIONS)) {
                        try {
                            //ApplicationData.createMarkPdf(context, subtopdfarray, child_data, studentlist, sendmail);
                            generetePdfOfTest(subtopdfarray.get(0).subject_id, data, test_id, sendmail, including_image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        ApplicationData.isphoto = true;
                        requestPermissions(SDCARD_PERMISSIONS, REQUEST_CONTACTS_CODE);
                    }
                } else {
                    //ApplicationData.createMarkPdf(context, subtopdfarray, child_data, studentlist, sendmail);
                    generetePdfOfTest(subtopdfarray.get(0).subject_id, data, test_id, sendmail, including_image);
                }
            }
        }, 2);
    }

    private void generetePdfOfTest(String subject_id, Childbeans data, String test_id, String sendmail, String including_image) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("teacher_id", _teacher_id);
        map.put("student_id", data.sender_id);
        map.put("selected_subjects_id", "");
        map.put("subject_id", subject_id);
        map.put("class_id", data.class_id);
        map.put("include_image", including_image);
        map.put("send_email", sendmail);
        map.put("test_id", test_id);
        map.put("semester_id", semester_ids);

        if (!GlobalConstrants.isWifiConnected(ReportCardActivity.this)) {
            return;
        } else {
            ETechAsyncTask task = new ETechAsyncTask(ReportCardActivity.this, this, ConstantApi.GET_MARK_PDF, map, 2);
            task.execute(ApplicationData.web_server_url + ConstantApi.GET_MARK_PDF);
        }
    }


    public class DownloadPDF extends AsyncTask {


        private Context context;
        private String url;
        private MainProgress pDialog;
        private String pdfname;

        public DownloadPDF(final Context context, final String url) {
            this.context = context;
            this.url = url;
            ApplicationData.isphoto = true;
            SimpleDateFormat dateformt = new SimpleDateFormat("dd-MMM-yyyy_mmss");
            String date = dateformt.format(Calendar.getInstance().getTime());

             pdfname = "Markpdf_" + date;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog == null)
                pDialog = new MainProgress(context);
            pDialog.setCancelable(false);
            pDialog.setMessage(context.getString(R.string.str_wait));
            pDialog.show();
        }


        /*  FileDownloader.downloadFile(fileUrl, file2, new FileDownloader.DownloadListener() {
              @Override
              public void onDownloadSuccessful() {
                  Uri path = Uri.fromFile(finalFile);
                  Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                  pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  pdfOpenintent.setDataAndType(path, "application/pdf");
                  try {
                      context.startActivity(pdfOpenintent);

                      pDialog.dismiss();
                  } catch (ActivityNotFoundException e) {
                      ApplicationData.showToast(context, R.string.msg_operation_error, false);
                  }
              }

              @Override
              public void onDownloadFailed(String errorMessage) {
                  Log.e("PdfReport ", errorMessage);
                  Toast.makeText(context, context.getString(R.string.pdf_load_issue), Toast.LENGTH_LONG).show();
                  pDialog.dismiss();
              }
          });

      } catch (IOException e) {
          e.printStackTrace();

      }
*/
        @Override
        protected Object doInBackground(Object[] params) {

            try {
                File file2 = null;
                File sdCard = Environment.getExternalStorageDirectory();
                String filePath = sdCard.getAbsolutePath() + "/CSadminFolder/pdf";
                File file = new File(filePath);

                file.mkdirs();

                file2 = new File(file, pdfname + ".pdf");
                if (!file2.exists()) {
                    file2.createNewFile();
                }

                final File finalFile = file2;
                FileDownloader.downloadFile(url, file2, null);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(pDialog!=null && pDialog.isShowing())
                pDialog.dismiss();

            File file = new File(Environment.getExternalStorageDirectory() + "/CSadminFolder/pdf", pdfname + ".pdf");
            Uri path = Uri.fromFile(file);
            Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
            pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfOpenintent.setDataAndType(path, "application/pdf");
            try {
                context.startActivity(pdfOpenintent);
            } catch (ActivityNotFoundException e) {
                ApplicationData.showToast(context, R.string.msg_operation_error, false);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        url=null;_parent_id=null;child_id=null;child_name=null;
        expand_report=null;expand_sub=null;
        lin_sem=null;
        array_yr = null;
        spn_yr=null;
        selected_year = null;school_id=null;class_id=null;
        semesterarray=null;subjectarray=null; characterarray=null; detailmarkarray=null;
        studentmarkarray=null;
        semester_name = null;
        listadapter=null;
        semester_ids = null;_teacher_id=null;_teacher_image=null;
        markadapter=null;
        data=null;
        change_lang=null;
        arraysemid=null;
        img_dropdown=null;
        view_system=null;
        txt_custom=null; txt_system=null;
        rel_system=null; rel_custom=null;rel_markview=null; rel_download=null;
        animation=null; animleft=null;
        lyt_save=null; lyt_download=null; lin_note=null; lyt_cancel=null;
        selectsubadapter=null;
        studentlist =null;
        inflater=null;
        screenview=null; lin_chk=null;
        chkbox=null;
        including_image =null;

        System.gc();
    }
}
