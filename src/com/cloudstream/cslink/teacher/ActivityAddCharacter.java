package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.adapter.teacher.CharacterSpinnerAdapter;
import com.adapter.teacher.Childbeans;
import com.adapter.teacher.SubSpinnerListAdapter;
import com.adapter.teacher.YearSpinnerListAdapter;
import com.cloudstream.cslink.R;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.common.view.CircularImageView;
import com.langsetting.apps.Change_lang;
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by etech on 4/7/16.
 */
public class ActivityAddCharacter extends Activity implements AsyncTaskCompleteListener<String> {
    private ImageView imgback;
    private CircularImageView img_pic;
    private TextView text_student_nm, txt_class, textView1;
    private LinearLayout information;
    private ImageView img_head;
    private TextView text_semester, txt_save;
    private EditText edt_mark, edt_exam, edt_comment;
    private Spinner spn_subject, spn_yr, spn_sem;
    ArrayList<String> array_yr = new ArrayList<>();
    private int current_yr = 1997, startingyr = 1997;
    private SharedPreferences sharedpref;
    private Change_lang change_lang;
    private String _teacher_id, _teacher_image, school_id;
    private Childbeans data = new Childbeans();
    private String class_id, user_id;
    private LinearLayout userprofile;
    private String selected_year = "", selected_sem = "0", selected_sub = "0", selected_class = "0";
    private ArrayList<Childbeans> semesterarray, subjectarray, characterarray, studentmarkarray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_character);
        init();

        sharedpref = getSharedPreferences("adminapp", 0);
        change_lang = new Change_lang(getApplicationContext());
        _teacher_id = sharedpref.getString("teacher_id", "");
        _teacher_image = sharedpref.getString("image", "");
        school_id = sharedpref.getString("school_id", "");

        //get current year
        Date date = new Date();
        int year = date.getYear();
        Date dateInit = new Date(year, 7, 15);
        if (dateInit.before(date))
            current_yr = Calendar.getInstance().get(Calendar.YEAR) + 1;
        else
            current_yr = Calendar.getInstance().get(Calendar.YEAR);

        //create year array list
        //  array_yr.add(getString(R.string.select_yr));

        if (current_yr == 1997)
            array_yr.add("1997-1997");
        else {
            for (int i = 0; i < (current_yr - 1997); i++) {
                array_yr.add(String.valueOf(startingyr) + "-" + String.valueOf(startingyr + 1));
                startingyr++;
            }
            Collections.reverse(array_yr);
        }

        if (getIntent().hasExtra("child_detail")) {
            data = (Childbeans) getIntent().getSerializableExtra("child_detail");
            text_student_nm.setText((data.child_name != null && data.child_name.length() > 0) ? data.child_name : "");
            if (data.child_image != null && data.child_image.length() > 0) {
                String url = ApplicationData.web_server_url + "uploads/" + data.child_image;
                ApplicationData.setProfileImg(img_pic, url, ActivityAddCharacter.this);
            }

            txt_class.setText((data.class_name != null && data.class_name.length() > 0) ? data.class_name : "");

            selected_class = (data.class_id != null && data.class_id.length() > 0) ? data.class_id : "";
            user_id = (data.sender_id != null && data.sender_id.length() > 0) ? data.sender_id : "";
        }

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GetSubjectSemesterList(data);

        YearSpinnerListAdapter adapter = new YearSpinnerListAdapter(ActivityAddCharacter.this, array_yr);
        spn_yr.setAdapter(adapter);
        spn_yr.setSelection(0);
        selected_year = array_yr.get(0);

        spn_yr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_year = array_yr.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_sem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_sem = semesterarray.get(position).semester_id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spn_subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_sub = characterarray.get(position).character_id;
                school_id = characterarray.get(position).school_id;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        txt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isvalid()) {
                    String comment = edt_comment.getText().toString();
                    try {
                        comment = URLEncoder.encode(comment, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("user_id", user_id);
                    map.put("year", selected_year);
                    map.put("semester_id", selected_sem);
                    map.put("class_id", selected_class);
                    map.put("character_id", selected_sub);
                    map.put("comment", comment);
                    senddata(map);

                }
            }
        });
    }

    private void senddata(HashMap<String, Object> map) {
        if (!GlobalConstrants.isWifiConnected(ActivityAddCharacter.this)) {
            return;
        } else {
            ETechAsyncTask task1 = new ETechAsyncTask(ActivityAddCharacter.this, this, ConstantApi.SET_CHARACTER_BY_TEACHER, map);
            task1.execute(ApplicationData.main_url + ConstantApi.SET_CHARACTER_BY_TEACHER + ".php?");
        }
    }

    private boolean isvalid() {
        if (selected_year.equalsIgnoreCase(getString(R.string.select_yr))) {
            ApplicationData.showToast(ActivityAddCharacter.this, getString(R.string.error_yr), true);
            return false;
        } else if (selected_sem.equalsIgnoreCase("0")) {
            ApplicationData.showToast(ActivityAddCharacter.this, getString(R.string.error_sem), true);
            return false;
        } else if (edt_comment.getText().toString().length() == 0) {//selected_sub.equalsIgnoreCase("0")) {
            ApplicationData.showToast(ActivityAddCharacter.this, getString(R.string.err_comment), true);//err_charcter
            return false;
        } else
            return true;
    }

    private void GetSubjectSemesterList(Childbeans bean) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("class_id", selected_class);
        map.put("school_id", school_id);
        map.put("user_id", user_id);
        map.put("language", ApplicationData.getlanguage(ActivityAddCharacter.this));
        if (!GlobalConstrants.isWifiConnected(ActivityAddCharacter.this)) {
            return;
        } else {
            ETechAsyncTask task = new ETechAsyncTask(ActivityAddCharacter.this, this, ConstantApi.GET_SUBJECT_SEMESTER, map);
            task.execute(ApplicationData.main_url + ConstantApi.GET_SUBJECT_SEMESTER + ".php?");
        }
    }

    private void init() {
        imgback = (ImageView) findViewById(R.id.imgback);
        img_pic = (CircularImageView) findViewById(R.id.imageView2);
        text_student_nm = (TextView) findViewById(R.id.textView2);
        txt_class = (TextView) findViewById(R.id.textView3);
        information = (LinearLayout) findViewById(R.id.information);
        img_head = (ImageView) findViewById(R.id.img_head);
        textView1 = (TextView) findViewById(R.id.textView1);
        spn_yr = (Spinner) findViewById(R.id.spn_yr);
        spn_sem = (Spinner) findViewById(R.id.spn_sem);
        // text_semester = (TextView) findViewById(R.id.text_semester);
        edt_comment = (EditText) findViewById(R.id.edt_comment);
        spn_subject = (Spinner) findViewById(R.id.text_subject);
        txt_save = (TextView) findViewById(R.id.txt_save);
        userprofile = (LinearLayout) findViewById(R.id.userprofile);

        //set edit icon on header
        textView1.setVisibility(View.GONE);
        userprofile.setVisibility(View.VISIBLE);
        information.setVisibility(View.GONE);

    }

    @Override
    protected void onResume() {
        super.onResume();
       /* Background_work.set_front_time();
        if (Background_work.check_layout_pincode()) {
            Intent i = new Intent(ActivityAddCharacter.this, PasswordActivity.class);
            startActivity(i);
        } else {
        }*/
    }

    @Override
    public void onTaskComplete(int statusCode, String result, String webserviceCb, Object tag) {
        try {
            if (statusCode == ETechAsyncTask.COMPLETED) {
                JSONObject jObject = new JSONObject(result);

                try {

                    if (webserviceCb.equalsIgnoreCase(ConstantApi.GET_SUBJECT_SEMESTER)) {
                        Log.e("json object :: ", jObject.toString());
                        String flag = jObject.getString("flag");

                        if (semesterarray != null) semesterarray.clear();
                        if (subjectarray != null) subjectarray.clear();
                        if (characterarray != null) characterarray.clear();
                        if (studentmarkarray != null) studentmarkarray.clear();

                        semesterarray = new ArrayList<Childbeans>();
                        subjectarray = new ArrayList<Childbeans>();
                        characterarray = new ArrayList<Childbeans>();
                        studentmarkarray = new ArrayList<Childbeans>();

                        //JsonParsing of Semesterarray
                        if (jObject.has("semester_list")) {
                            Childbeans data = new Childbeans();
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
                            }
                        }

                        //JsonParsing of subject_list
                        if (jObject.has("subject_list")) {
                            data.subject_id = "0";
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
                            }
                        }

                        //JsonParsing of character_list
                        if (jObject.has("character_list")) {
                            data.character_id = "0";
                            data.character_name = getString(R.string.select_rate);
                            data.school_id = "0";
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


                        //JsonParsing of user_marks_details
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

                                studentmarkarray.add(bean);
                            }
                        }

                        if (jObject.has("descipline_list")) {

                        }
                        if (jObject.has("remarks_list")) {

                        }
                        setadapter();

                    } else if (webserviceCb.equalsIgnoreCase(ConstantApi.SET_CHARACTER_BY_TEACHER)) {
                        String flag = jObject.getString("flag");
                        if (flag.equalsIgnoreCase("1")) {
                            ApplicationData.showMessage(ActivityAddCharacter.this, getString(R.string.app_name),
                                    getString(R.string.save_character), getString(R.string.str_ok));
                            edt_comment.setText("");
                            spn_yr.setSelection(array_yr.size() - 1);
                            spn_sem.setSelection(0);
                            spn_subject.setSelection(0);
                        } else {
                            if (jObject.has("msg")) {
                                String msg = jObject.getString("msg");
                                ApplicationData.showToast(ActivityAddCharacter.this, msg, false);
                            }

                        }
                    } else {
                        ApplicationData.showToast(ActivityAddCharacter.this, R.string.server_error, false);
                    }

                    //setAdapter();
                } catch (Exception e) {
                    Log.e("Character class", "onTaskComplete() " + e, e);
                }
            } else if (statusCode == ETechAsyncTask.ERROR_NETWORK) {
                try {
                    ApplicationData.showToast(ActivityAddCharacter.this, R.string.msg_operation_error, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setadapter() {
        if (semesterarray != null && semesterarray.size() > 0) {
            SubSpinnerListAdapter semtper = new SubSpinnerListAdapter(ActivityAddCharacter.this, semesterarray, false);
            spn_sem.setAdapter(semtper);
        }
        if (subjectarray != null && subjectarray.size() > 0) {
            CharacterSpinnerAdapter subtper = new CharacterSpinnerAdapter(ActivityAddCharacter.this, characterarray);
            spn_subject.setAdapter(subtper);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        imgback = null;
        img_pic = null;
        text_student_nm = null;
        txt_class = null;
        textView1 = null;
        information = null;
        img_head = null;
        text_semester = null;
        txt_save = null;
        edt_mark = null;
        edt_exam = null;
        edt_comment = null;
        spn_subject = null;
        spn_yr = null;
        spn_sem = null;
        array_yr = null;
        sharedpref = null;
        change_lang = null;
        _teacher_id = null;
        _teacher_image = null;
        school_id = null;
        data = null;
        class_id = null;
        user_id = null;
        userprofile = null;
        selected_year = null;
        selected_sem = null;
        selected_sub = null;
        selected_class = null;
        semesterarray = null;
        subjectarray = null;
        characterarray = null;
        studentmarkarray = null;
    }
}
