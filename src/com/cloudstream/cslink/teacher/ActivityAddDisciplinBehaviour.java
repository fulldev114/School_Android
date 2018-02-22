package com.cloudstream.cslink.teacher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.adapter.teacher.Childbeans;
import com.adapter.teacher.DisciplineListAdapter;
import com.adapter.teacher.SubSpinnerListAdapter;
import com.cloudstream.cslink.R;
import com.common.Bean.MarkBean;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.langsetting.apps.Change_lang;
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;
import com.request.FileObject;
import com.xmpp.teacher.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by etech on 19/10/16.
 */
public class ActivityAddDisciplinBehaviour extends ActivityHeader implements AsyncTaskCompleteListener<String> {

    private LayoutInflater inflater;
    private RelativeLayout screenview;
    private Activity mActivity;
    private SharedPreferences sharedpref;
    private Change_lang change_lang;
    private String _teacher_id, _teacher_image, school_id, class_id, user_id;
    private RelativeLayout rel_date;
    private TextView txt_date;
    private Spinner spn_category, spin_remark;
    private EditText edt_comment;
    private LinearLayout lyt_save;
    private int current_yr = 1997, startingyr = 1997;
    int year, month, day;
    private Childbeans data;
    private String selected_year = "", selected_disid = "0", selected_remarkid = "0", selected_class = "0", selected_exam = "0", selected_exam_no = "0";
    private ArrayList<Childbeans> disciplinecatarray, remarkarray, characterarray, studentmarkarray;
    private DisciplineListAdapter subtper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        screenview = (RelativeLayout) inflater.inflate(R.layout.adres_add_disciplineandbehav, null);
        relwrapp.addView(screenview);

        init();

        showheaderusermenu(ActivityAddDisciplinBehaviour.this, "", 0, true);

        mActivity = ActivityAddDisciplinBehaviour.this;
        ApplicationData.setMainActivity(mActivity);

        sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        change_lang = new Change_lang(this);
        _teacher_id = sharedpref.getString("teacher_id", "");
        _teacher_image = sharedpref.getString("image", "");
        school_id = sharedpref.getString("school_id", "");

        //get current date
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


        //fetch data from privious activity
        if (getIntent().hasExtra("child_detail")) {
            data = (Childbeans) getIntent().getSerializableExtra("child_detail");
            name.setText((data.child_name != null && data.child_name.length() > 0) ? data.child_name : "");
            if (data.child_image != null && data.child_image.length() > 0) {
                String url = ApplicationData.web_server_url + "uploads/" + data.child_image;
                ApplicationData.setProfileImg(profileimage, url, ActivityAddDisciplinBehaviour.this);
            }

            subject.setText((data.class_name != null && data.class_name.length() > 0) ? data.class_name : "");
            class_id = (data.class_id != null && data.class_id.length() > 0) ? data.class_id : "";
            user_id = (data.sender_id != null && data.sender_id.length() > 0) ? data.sender_id : "";
        }

        spn_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_disid = disciplinecatarray.get(position).descipline_id;
                selected_remarkid="0";
                if (!selected_disid.equalsIgnoreCase("0")) {
                    if (characterarray != null && characterarray.size() > 0)
                        characterarray.clear();
                    edt_comment.setText("");
                    spin_remark.setSelection(0);
                    Childbeans bean = new Childbeans();
                    bean.remarks_id = "0";
                    bean.remarks_name = getString(R.string.select_remark);
                    bean.descipline_id = "0";
                    characterarray.add(bean);
                    if (remarkarray != null && remarkarray.size() > 0) {
                        if (selected_remarkid != null && selected_remarkid.length() > 0 && !selected_remarkid.equalsIgnoreCase(getString(R.string.select_sub))) {
                            for (int i = 0; i < remarkarray.size(); i++) {
                                if (selected_disid.equalsIgnoreCase(remarkarray.get(i).descipline_id)) {

                                    bean = new Childbeans();
                                    bean.remarks_id = remarkarray.get(i).remarks_id;
                                    bean.remarks_name = remarkarray.get(i).remarks_name;
                                    bean.descipline_id = remarkarray.get(i).descipline_id;
                                    characterarray.add(bean);
                                }
                            }
                            subtper.updaterecord(ActivityAddDisciplinBehaviour.this, characterarray, true);
                            spin_remark.setAdapter(subtper);
                        }
                    }
                }
                else if(selected_disid.equalsIgnoreCase("0"))
                {
                    if(remarkarray!=null && remarkarray.size()>0)
                    {
                        subtper = new DisciplineListAdapter(ActivityAddDisciplinBehaviour.this, remarkarray, true);
                        spin_remark.setAdapter(subtper);
                    }
                }
               /* if (!selected_disid.equalsIgnoreCase("0") && !selected_remarkid.equalsIgnoreCase("0")) {
                    if (spn_category.isEnabled())
                        setmark();
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spin_remark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              //  selected_disid = remarkarray.get(position).descipline_id;
                selected_remarkid = characterarray.get(position).remarks_id;

               /* if (!selected_disid.equalsIgnoreCase("0") && !selected_remarkid.equalsIgnoreCase("0")) {
                    if (spin_remark.isEnabled())
                        setmark();
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        lyt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isvalid())
                {
                    String comment=edt_comment.getText().toString();
                    String exam_date = txt_date.getText().toString();
                    try {
                        comment= URLEncoder.encode(comment,"utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("user_id", user_id);
                    map.put("class_id", class_id);
                    map.put("teacher_id", _teacher_id);
                    map.put("date", ApplicationData.convertToNorwei(exam_date, ActivityAddDisciplinBehaviour.this));
                    map.put("descipline_id", selected_disid);
                    map.put("remarks_id", selected_remarkid);
                    map.put("comment",comment);
                    senddata(map);

                }
            }
        });
        GetDisciplinBehave(data);
    }

    private void senddata(HashMap<String, Object> map) {
        if (!GlobalConstrants.isWifiConnected(ActivityAddDisciplinBehaviour.this)) {
            return;
        } else {
            ETechAsyncTask task1 = new ETechAsyncTask(ActivityAddDisciplinBehaviour.this,this, ConstantApi.SET_DISCIPLINE_BEHAVIOUR, map);
            task1.execute(ApplicationData.main_url + ConstantApi.SET_DISCIPLINE_BEHAVIOUR + ".php?");
        }
    }

    private boolean isvalid() {
        if(selected_disid.equalsIgnoreCase("0")) {
            try {
                ApplicationData.showMessage(ActivityAddDisciplinBehaviour.this,"", getString(R.string.error_discipline),getString(R.string.str_ok));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
       /* else if(!selected_disid.equalsIgnoreCase("2")) {

            if (selected_remarkid.equalsIgnoreCase("0")) {
                try {
                    ApplicationData.showMessage(ActivityAddDisciplinBehaviour.this, "", getString(R.string.error_remark), getString(R.string.str_ok));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
            return false;
        }*/
       /* else if(edt_comment.getText().toString().length()==0){//selected_sub.equalsIgnoreCase("0")) {
            try {
                ApplicationData.showMessage(ActivityAddDisciplinBehaviour.this,"", getString(R.string.err_comment),getString(R.string.str_ok));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }*/
        else
            return true;
    }

    private void GetDisciplinBehave(Childbeans data) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("class_id", class_id);
        map.put("school_id", school_id);
        map.put("user_id", user_id);
        if (!GlobalConstrants.isWifiConnected(ActivityAddDisciplinBehaviour.this)) {
            return;
        } else {
            ETechAsyncTask task = new ETechAsyncTask(ActivityAddDisciplinBehaviour.this, ActivityAddDisciplinBehaviour.this, ConstantApi.GET_SUBJECT_SEMESTER, map);
            task.execute(ApplicationData.main_url + ConstantApi.GET_SUBJECT_SEMESTER + ".php?");
        }

    }

    private void init() {

        spn_category = (Spinner) findViewById(R.id.spn_category);
        spin_remark = (Spinner) findViewById(R.id.spin_remark);
        edt_comment = (EditText) findViewById(R.id.edt_comment);
        lyt_save = (LinearLayout) findViewById(R.id.lyt_save);
        rel_date = (RelativeLayout) findViewById(R.id.rel_date);
        txt_date = (TextView) findViewById(R.id.txtDate);

        rel_date.setOnClickListener(Clicklistener);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", getResources().getConfiguration().locale);
        txt_date.setText(dateFormat.format(new Date()));

      /*  edt_comment.setOnTouchListener(touchListener);
        edt_comment.setMovementMethod(new ScrollingMovementMethod());*/

        edt_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != edt_comment.getLayout() && edt_comment.getLayout().getLineCount() > 5) {
                    edt_comment.getText().delete(edt_comment.getText().length() - 1, edt_comment.getText().length());
                }
            }
        });
    }


    View.OnClickListener Clicklistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.rel_date) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        }
    };


    View.OnTouchListener touchListener = new View.OnTouchListener() {
        public boolean onTouch(final View v, final MotionEvent motionEvent) {
            if (v.getId() == R.id.edt_comment) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
            }
            return false;
        }
    };


    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        private DatePickerDialog datepic;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            datepic = new DatePickerDialog(ActivityAddDisciplinBehaviour.this, this, year, month, day);
            Calendar c1 = Calendar.getInstance();
            c1.set(startingyr, 07, 15);
            datepic.getDatePicker().setMinDate(c1.getTimeInMillis()-10000);
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
            } catch (android.net.ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            txt_date.setText(ApplicationData.convertToNorweiDateyeartime(finalDate, ActivityAddDisciplinBehaviour.this));
          //  setmark();
        }
    }

    private void setmark() {
        edt_comment.setText("");
        spin_remark.setSelection(0);
        spn_category.setSelection(0);

        subtper.updaterecord(ActivityAddDisciplinBehaviour.this,remarkarray,true);
        if (studentmarkarray != null) {
            for (int i = 0; i < studentmarkarray.size(); i++) {
                Childbeans bean = studentmarkarray.get(i);
                if (bean.user_id.equals(user_id)) {
                    if (bean.descipline_id.equals(selected_disid) &&
                            bean.remarks_id.equals(selected_remarkid)
                            && bean.date.equals(ApplicationData.convertToNorwei(txt_date.getText().toString(), ActivityAddDisciplinBehaviour.this))) {
                        edt_comment.setText(bean.comment);
                        break;
                    }

                }
            }
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

                        if (disciplinecatarray != null) disciplinecatarray.clear();
                        if (remarkarray != null) remarkarray.clear();
                        if (characterarray != null) characterarray.clear();
                        if (studentmarkarray != null) studentmarkarray.clear();

                        disciplinecatarray = new ArrayList<Childbeans>();
                        remarkarray = new ArrayList<Childbeans>();
                        characterarray = new ArrayList<Childbeans>();
                        studentmarkarray = new ArrayList<Childbeans>();

                        //JsonParsing of Semesterarray
                        if (jObject.has("semester_list")) {
                           /* Childbeans data = new Childbeans();
                            data.semester_id = "0";
                            data.semester_name = getString(R.string.select_sem);
                            semesterarray.add(data);
                            JSONArray jseme = jObject.getJSONArray("semester_list");
                            for (int sem = 0; sem < jseme.length(); sem++) {
                                JSONObject jobsem = jseme.getJSONObject(sem);
                                Childbeans bean = new Childbeans();
                                if (jobsem.has("semester_id"))
                                    bean.semester_id = jobsem.getString("semester_id");
                                if (jobsem.has("semester_name"))
                                    bean.semester_name = jobsem.getString("semester_name");

                                semesterarray.add(bean);
                            }*/
                        }

                        //JsonParsing of subject_list
                        if (jObject.has("subject_list")) {
                           /* data.subject_id = "0";
                            data.subject_name = getString(R.string.select_sub);
                            data.class_id = "0";
                            subjectarray.add(data);
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
                            }*/
                        }

                        //JsonParsing of character_list
                        if (jObject.has("character_list")) {
                            data.subject_id = "0";
                            data.subject_name = getString(R.string.select_sub);
                            data.class_id = "0";
                            characterarray.add(data);
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

                      /*  //JsonParsing of user_marks_details
                        if (jObject.has("user_marks_details")) {
                            JSONArray juser = jObject.getJSONArray("user_marks_details");
                            for (int i = 0; i < juser.length(); i++) {
                                JSONObject jobuser = juser.getJSONObject(i);
                                Childbeans bean = new Childbeans();
                                if (jobuser.has("user_id"))
                                    bean.user_id = jobuser.getString("user_id");
                                if (jobuser.has("year"))
                                    bean.year = jobuser.getString("year");
                                if (jobuser.has("semester_id"))
                                    bean.semester_id = jobuser.getString("semester_id");
                                if (jobuser.has("class_id"))
                                    bean.class_id = jobuser.getString("class_id");
                                if (jobuser.has("subject_id"))
                                    bean.subject_id = jobuser.getString("subject_id");
                                if (jobuser.has("marks"))
                                    bean.mark = jobuser.getString("marks");
                                if (jobuser.has("comment"))
                                    bean.comment = jobuser.getString("comment");
                                if (jobuser.has("created_at"))
                                    bean.created_at = jobuser.getString("created_at");
                                if (jobuser.has("semester_name"))
                                    bean.semester_name = jobuser.getString("semester_name");
                                if (jobuser.has("subject_name"))
                                    bean.subject_name = jobuser.getString("subject_name");
                                if (jobuser.has("exam_about"))
                                    bean.exam_about = jobuser.getString("exam_about");
                                if (jobuser.has("exam_no"))
                                    bean.exam_no = jobuser.getString("exam_no");
                                if (jobuser.has("image"))
                                    bean.image = jobuser.getString("image");
                                if (jobuser.has("exam_date"))
                                    bean.date = jobuser.getString("exam_date");
                                studentmarkarray.add(bean);
                            }
                        }*/

                        if (jObject.has("descipline_list")) {
                            Childbeans data = new Childbeans();
                            data.descipline_id = "0";
                            data.descipline_name = getString(R.string.select_dis_cate);
                            disciplinecatarray.add(data);
                            JSONArray jseme = jObject.getJSONArray("descipline_list");
                            for (int sem = 0; sem < jseme.length(); sem++) {
                                JSONObject jobsem = jseme.getJSONObject(sem);
                                Childbeans bean = new Childbeans();
                                if (jobsem.has("descipline_id"))
                                    bean.descipline_id = jobsem.getString("descipline_id");
                                if (jobsem.has("descipline_name"))
                                    bean.descipline_name = jobsem.getString("descipline_name");

                                disciplinecatarray.add(bean);
                            }
                        }
                        if (jObject.has("remarks_list")) {

                            data.remarks_id = "0";
                            data.remarks_name = getString(R.string.select_remark);
                            data.descipline_id = "0";
                            remarkarray.add(data);
                            characterarray.add(data);

                            JSONArray jsub = jObject.getJSONArray("remarks_list");
                            for (int sub = 0; sub < jsub.length(); sub++) {
                                JSONObject jobsub = jsub.getJSONObject(sub);

                                Childbeans bean = new Childbeans();
                                if (jobsub.has("remarks_id"))
                                    bean.remarks_id = jobsub.getString("remarks_id");
                                if (jobsub.has("remarks_name"))
                                    bean.remarks_name = jobsub.getString("remarks_name");
                                if (jobsub.has("descipline_id"))
                                    bean.descipline_id = jobsub.getString("descipline_id");
                                if(bean.remarks_id!=null && !bean.remarks_id.equalsIgnoreCase("0")) {
                                    remarkarray.add(bean);
                                    characterarray.add(bean);
                                }
                            }
                        }

                        if (jObject.has("user_descipline_details")) {
                            JSONArray juser = jObject.getJSONArray("user_descipline_details");
                            for (int i = 0; i < juser.length(); i++) {
                                JSONObject jobuser = juser.getJSONObject(i);
                                Childbeans bean = new Childbeans();
                                if (jobuser.has("user_id"))
                                    bean.user_id = jobuser.getString("user_id");
                                if (jobuser.has("class_id"))
                                    bean.class_id = jobuser.getString("class_id");
                                if (jobuser.has("char_date"))
                                    bean.date = jobuser.getString("char_date");
                                if (jobuser.has("descipline_id"))
                                    bean.descipline_id = jobuser.getString("descipline_id");
                                if (jobuser.has("remarks_id"))
                                    bean.remarks_id = jobuser.getString("remarks_id");
                                if (jobuser.has("teacher_id"))
                                    bean.teacher_id = jobuser.getString("teacher_id");
                                if (jobuser.has("comment"))
                                    bean.comment = jobuser.getString("comment");
                                if (jobuser.has("created_at"))
                                    bean.created_at = jobuser.getString("created_at");
                                if (jobuser.has("name"))
                                    bean.name = jobuser.getString("name");

                                studentmarkarray.add(bean);
                            }
                        }
                        setadapter();


                    } else if (webserviceCb.equalsIgnoreCase(ConstantApi.SET_DISCIPLINE_BEHAVIOUR)) {
                        String flag = jObject.getString("flag");
                        if (flag.equalsIgnoreCase("1")) {

                            ApplicationData.calldialog(ActivityAddDisciplinBehaviour.this, "", getString(R.string.save_character), getString(R.string.str_ok), "",
                                    new ApplicationData.DialogListener() {
                                        @Override
                                        public void diaBtnClick(int diaID, int btnIndex) {
                                            if(btnIndex==2) {
                                               /* if (fromViewMark) {
                                                    MarkBean mbean = new MarkBean();
                                                    mbean.exam_no = selected_exam;
                                                    mbean.mark = edt_mark.getText().toString();
                                                    mbean.exam_about = edt_exam.getText().toString();
                                                    mbean.comment = edt_comment.getText().toString();
                                                    mbean.exam_date = ApplicationData.convertToNorwei(txt_date.getText().toString(), ActivityAddMark.this);
                                                    mbean.image = photoPath;
                                                    //if (photoPath.startsWith("/storage"))
                                                    if (removeimage.equals("yes"))
                                                        mbean.image = "";

                                                    ((ReportCardActivity) ApplicationData.getReportActivity()).updatedetail(userprofile, mbean, parentposition, childpos, fromdownload);
                                                    finish();
                                                }*/
                                            }
                                        }
                                    }, 2);


                            edt_comment.setText("");
                            spn_category.setSelection(0);
                            spin_remark.setSelection(0);
                            subtper.updaterecord(ActivityAddDisciplinBehaviour.this,remarkarray,true);

                        } else {
                            if (jObject.has("msg")) {
                                String msg = jObject.getString("msg");
                                ApplicationData.showToast(ActivityAddDisciplinBehaviour.this, msg, false);
                            }
                            // ApplicationData.showToast(ActivityAddMark.this, R.string.error_save_mark, false);
                        }
                    } else {
                        ApplicationData.showToast(ActivityAddDisciplinBehaviour.this, R.string.server_error, false);
                    }

                    //setAdapter();
                } catch (Exception e) {
                    Log.e("OfferCategory", "onTaskComplete() " + e, e);
                }
            } else if (statusCode == ETechAsyncTask.ERROR_NETWORK) {
                try {
                    ApplicationData.showToast(ActivityAddDisciplinBehaviour.this, R.string.msg_operation_error, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setadapter() {
        if (disciplinecatarray != null && disciplinecatarray.size() > 0) {
            DisciplineListAdapter semtper = new DisciplineListAdapter(ActivityAddDisciplinBehaviour.this, disciplinecatarray, false);
            spn_category.setAdapter(semtper);

            if (selected_disid != null && selected_disid.length() > 0 && !selected_disid.equalsIgnoreCase(getString(R.string.select_sem))) {
                for (int i = 0; i < disciplinecatarray.size(); i++) {
                    if (selected_disid.equalsIgnoreCase(disciplinecatarray.get(i).descipline_id)) {
                        spn_category.setSelection(i);
                        break;
                    }
                }
            }
        }

        if (remarkarray != null && remarkarray.size() > 0) {
            subtper = new DisciplineListAdapter(ActivityAddDisciplinBehaviour.this, characterarray, true);
            spin_remark.setAdapter(subtper);

            if (selected_remarkid != null && selected_remarkid.length() > 0 && !selected_remarkid.equalsIgnoreCase(getString(R.string.select_sub))) {
                for (int i = 0; i < remarkarray.size(); i++) {
                    if (selected_remarkid.equalsIgnoreCase(remarkarray.get(i).remarks_id)) {
                        spin_remark.setSelection(i);
                        selected_remarkid = remarkarray.get(i).remarks_id;
                        break;
                    }
                }
            }
        }
    }
}
