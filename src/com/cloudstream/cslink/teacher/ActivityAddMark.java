package com.cloudstream.cslink.teacher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ArrowKeyMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.teacher.Childbeans;
import com.adapter.teacher.SubSpinnerListAdapter;
import com.adapter.teacher.YearSpinnerListAdapter;
import com.bumptech.glide.Glide;
import com.cloudstream.cslink.R;
import com.common.Bean.MarkBean;
import com.common.FileDownloader;
import com.common.utils.AppUtils;
import com.common.utils.BitmapUtil;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.common.view.AttachmentMap;
import com.common.view.CircularImageView;
import com.langsetting.apps.Change_lang;
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;
import com.request.FileObject;
import com.xmpp.teacher.Constant;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by etech on 4/7/16.
 */
public class ActivityAddMark extends ActivityHeader implements AsyncTaskCompleteListener<String> {
    private ImageView img_delete, img_camera;
    private TextView txt_date, txt_year;
    private TextView text_semester, txt_save;
    private EditText edt_mark, edt_exam, edt_comment;
    private Spinner spn_subject, spn_yr, spn_sem, spn_exam;
    ArrayList<String> array_yr, array_exam_no;
    private int current_yr = 1997, startingyr = 1997;
    private SharedPreferences sharedpref;
    private Change_lang change_lang;
    private String _teacher_id, _teacher_image, school_id;
    private Childbeans data = new Childbeans();
    private String class_id, user_id;
    private LinearLayout userprofile;
    private String selected_year = "", selected_sem = "0", selected_sub = "0", selected_class = "0", selected_exam = "0", selected_exam_no = "0";
    private ArrayList<Childbeans> semesterarray, subjectarray, characterarray, studentmarkarray;
    private CircularImageView img_mark;
    private static String SDCARD_PERMISSIONS[] = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    Integer angle = 0;
    private static String photoPath = "";
    private String removeimage = "no";
    String path = GlobalConstrants.path;
    private Bitmap bitmap;
    private static File filepath;
    Bitmap sbmp = null;
    private int REQUEST_CODE_PHOTO_CROP = 1008;
    private final static int REQUEST_CONTACTS_CODE = 100;
    public final static int GET_CAMERA = 1009;
    public final static int GET_GALLERY = 1010;
    private static final int RESULT_OK = -1;
    private RelativeLayout rel_date, rel_image;
    int year, month, day;
    private boolean fromViewMark = false, fromdownload = false;
    private Childbeans userdetail;
    private MarkBean mark_detail;
    private int parentposition, childpos;
    private Activity mActivity;
    private static Uri imageuri;
    private LayoutInflater inflater;
    private RelativeLayout screenview;
    private YearSpinnerListAdapter examadapter;
    private LinearLayout lyt_save;
    private String TAG = "ActivityAddMark";
    private ProgressBar progressbar;
    private boolean isfirstsemester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        screenview = (RelativeLayout) inflater.inflate(R.layout.adres_add_mark, null);
        relwrapp.addView(screenview);

        init();

        showHeaderUserAndInfoMenu();

        mActivity = ActivityAddMark.this;
        ApplicationData.setMainActivity(mActivity);
        sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        change_lang = new Change_lang(getApplicationContext());
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

        GregorianCalendar secondhalfdate = new GregorianCalendar(current_yr, 1, 15);

        if (date.before(dateInit) && date.after(secondhalfdate.getTime())) {
            isfirstsemester = false;
        } else {
            isfirstsemester = true;
        }

        //create year array list
        selected_year = startingyr + "-" + current_yr;
        String yeartwodigit = String.valueOf(current_yr).substring(Math.max(String.valueOf(current_yr).length() - 2, 0));
        txt_year.setText(startingyr + "/" + yeartwodigit);
        array_yr = new ArrayList<String>();

        //create exam no list
        array_exam_no = new ArrayList<String>();
        array_exam_no.add(getString(R.string.test));
        for (int i = 1; i <= ApplicationData.examno; i++) {
            array_exam_no.add(getString(R.string.testno) + " " + String.valueOf(i));
        }

        if (getIntent().hasExtra("child_detail")) {
            data = (Childbeans) getIntent().getSerializableExtra("child_detail");
            name.setText((data.child_name != null && data.child_name.length() > 0) ? data.child_name : "");
            if (data.child_image != null && data.child_image.length() > 0) {
                String url = ApplicationData.web_server_url + "uploads/" + data.child_image;
                ApplicationData.setProfileImg(profileimage, url, ActivityAddMark.this);
            }

            subject.setText((data.class_name != null && data.class_name.length() > 0) ? data.class_name : "");

            class_id = (data.class_id != null && data.class_id.length() > 0) ? data.class_id : "";
            user_id = (data.sender_id != null && data.sender_id.length() > 0) ? data.sender_id : "";
        }

        if (getIntent().hasExtra("user_detail")) {
            userdetail = (Childbeans) getIntent().getSerializableExtra("user_detail");
            if (userdetail != null && userdetail.user_id.length() > 0) {
                user_id = userdetail.user_id;
                class_id = userdetail.class_id;
                subject.setText(userdetail.class_name);
                name.setText((userdetail.child_name != null && userdetail.child_name.length() > 0) ? userdetail.child_name : "");
                selected_sem = userdetail.semester_id;
                selected_sub = userdetail.subject_id;
                fromViewMark = true;
                if (userdetail.child_image != null && userdetail.child_image.length() > 0) {
                    String url = ApplicationData.web_server_url + "uploads/" + userdetail.child_image;
                    ApplicationData.setProfileImg(profileimage, url, ActivityAddMark.this);
                }
            }

            if (getIntent().hasExtra("parentposition")) {
                parentposition = getIntent().getIntExtra("parentposition", 0);
            }
            if (getIntent().hasExtra("position")) {
                childpos = getIntent().getIntExtra("position", 0);
            }

            if (getIntent().hasExtra("mark_detail")) {
                mark_detail = (MarkBean) getIntent().getSerializableExtra("mark_detail");
                selected_exam_no = (mark_detail.exam_no != null && mark_detail.exam_no.length() > 0) ? mark_detail.exam_no : "";
                edt_mark.setText((mark_detail.mark != null && mark_detail.mark.length() > 0) ? mark_detail.mark : "");
                edt_exam.setText((mark_detail.exam_about != null && mark_detail.exam_about.length() > 0) ? mark_detail.exam_about : "");
                edt_comment.setText((mark_detail.comment != null && mark_detail.comment.length() > 0) ? mark_detail.comment : "");
                txt_date.setText(ApplicationData.convertToNorweiDateyeartime(mark_detail.exam_date, ActivityAddMark.this));
                if (mark_detail.image != null && mark_detail.image.length() > 0) {
                    if (mark_detail.image.startsWith("/storage")) {

                        File filepath = new File(mark_detail.image);
                        Glide.with(ActivityAddMark.this).load(filepath)
                                .thumbnail(0.5f)
                                .crossFade()
                                .into(img_mark);
                    } else {
                        if (mark_detail.image.startsWith("http")) {
                            ApplicationData.setProfileImg(img_mark, mark_detail.image, ActivityAddMark.this);
                        } else {
                            ApplicationData.setProfileImg(img_mark, ApplicationData.web_server_url + "uploads/" + mark_detail.image, ActivityAddMark.this);
                        }
                    }
                    img_delete.setVisibility(View.VISIBLE);
                    img_camera.setVisibility(View.GONE);
                    photoPath = mark_detail.image;
                }
            }

            if (getIntent().hasExtra("fromdowload")) {
                fromdownload = getIntent().getBooleanExtra("fromdowload", false);
            }
            spn_sem.setEnabled(false);
            spn_exam.setEnabled(false);
            spn_subject.setEnabled(false);
            lin_information.setVisibility(View.GONE);
            // rel_date.setEnabled(false);
        }
        imgback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lin_information.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityAddMark.this, ReportCardActivity.class);
                i.putExtra("child_detail", data);
                startActivity(i);
            }
        });

        spn_sem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_sem = semesterarray.get(position).semester_id;
                if (!selected_year.equalsIgnoreCase(getString(R.string.select_yr)) && !selected_sem.equalsIgnoreCase("0")
                        && !selected_sub.equalsIgnoreCase("0")) {// && !selected_exam.equalsIgnoreCase("0")) {
                    if (spn_sem.isEnabled())
                        setmark();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_sub = subjectarray.get(position).subject_id;
                selected_class = subjectarray.get(position).class_id;

                if (!selected_year.equalsIgnoreCase(getString(R.string.select_yr)) && !selected_sem.equalsIgnoreCase("0")
                        && !selected_sub.equalsIgnoreCase("0")) {// && !selected_exam.equalsIgnoreCase("0")) {
                    if (spn_subject.isEnabled())
                        setmark();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spn_exam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    selected_exam = array_exam_no.get(position).replace(getString(R.string.testno) + " ", "");
                else
                    selected_exam = String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        examadapter = new YearSpinnerListAdapter(ActivityAddMark.this, array_exam_no);
        spn_exam.setAdapter(examadapter);
        if (selected_exam_no != null && selected_exam_no.length() > 0 && !selected_exam_no.equalsIgnoreCase("0")) {
            spn_exam.setSelection(Integer.parseInt(selected_exam_no));
            selected_exam = selected_exam_no;
        }

        lyt_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lyt_save.setEnabled(false);
                if (isvalid()) {
                    String exam_about = edt_exam.getText().toString();
                    String comment = edt_comment.getText().toString();
                    String marks = edt_mark.getText().toString();
                    String exam_date = txt_date.getText().toString();
                    try {
                        // exam_about = exam_about.replaceAll("'", "\\u0027");//replace("'", "\'");
                        exam_about = URLEncoder.encode(exam_about, "utf-8");
                        // comment = comment.replaceAll("'", "\\u0027");//replace("'", "\'");
                        //   Log.d(TAG," comment : "+comment);
                        comment = URLEncoder.encode(comment, "utf-8");
                        // marks = marks.replaceAll("'","\\u0027");//replace("'", "\'");
                        marks = URLEncoder.encode(marks, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("user_id", user_id);
                    map.put("year", selected_year);
                    map.put("semester_id", selected_sem);
                    map.put("class_id", selected_class);
                    map.put("subject_id", selected_sub);
                    map.put("exam_no", selected_exam);
                    map.put("marks", marks);
                    map.put("comment", comment);
                    map.put("exam_about", exam_about);
                    map.put("teacher_id", _teacher_id);
                    map.put("exam_date", ApplicationData.convertToNorwei(exam_date, ActivityAddMark.this));
                    map.put("language", ApplicationData.getlanguage(ActivityAddMark.this));
                    if (photoPath != null && photoPath.length() > 0 && !photoPath.startsWith("http")) {
                        File file = new File(photoPath);
                        FileObject fObj = null;
                        try {
                            if (file.exists()) {
                                fObj = new FileObject();
                                fObj.setContentType("image/*");
                                fObj.setByteData(ApplicationData.readFile(file));
                                fObj.setFileName(file.getName());
                                map.put("image", fObj);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else
                        map.put("image", photoPath);

                    map.put("removeimage", removeimage);
                    senddata(map);

                } else {
                    lyt_save.setEnabled(true);
                }
            }
        });

        rel_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (img_delete.getVisibility() == View.VISIBLE) {
                    ArrayList<AttachmentMap> imgList = new ArrayList<>();
                    AttachmentMap attach = new AttachmentMap();
                    attach.setAttachmentName(photoPath);
                    imgList.add(attach);


                    if (imgList != null && imgList.size() > 0) {
                        Intent intent = new Intent(ActivityAddMark.this, SlideShowActivity.class);
                        intent.putExtra("ImageList", imgList);
                        startActivity(intent);
                    }
                } else {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (AppUtils.hasSelfPermission(ActivityAddMark.this, SDCARD_PERMISSIONS)) {
                            selectImage();
                        } else {
                            ApplicationData.isphoto = true;
                            requestPermissions(SDCARD_PERMISSIONS, REQUEST_CONTACTS_CODE);
                        }
                    } else {
                        selectImage();
                    }
                }
            }
        });


        GetSubjectSemesterList(data);

    }

    private void senddata(HashMap<String, Object> map) {
        if (!GlobalConstrants.isWifiConnected(ActivityAddMark.this)) {
            lyt_save.setEnabled(true);
            return;
        } else {
            ETechAsyncTask task1 = new ETechAsyncTask(ActivityAddMark.this, this, ConstantApi.SET_MARK_BY_TEACHER, map, 2, true);
            task1.execute(ApplicationData.main_url + ConstantApi.SET_MARK_BY_TEACHER + ".php?");
            lyt_save.setEnabled(true);
        }
    }

    private boolean isvalid() {

        if (selected_sem.equalsIgnoreCase("0")) {
            ApplicationData.showToast(ActivityAddMark.this, getString(R.string.error_sem), true);
            return false;
        } else if (selected_sub.equalsIgnoreCase("0")) {
            ApplicationData.showToast(ActivityAddMark.this, getString(R.string.error_sub), true);
            return false;
        } else if (selected_exam.equalsIgnoreCase("0")) {
            ApplicationData.showToast(ActivityAddMark.this, getString(R.string.error_exam), true);
            return false;
        } else if (edt_exam.getText().toString().length() == 0) {
            ApplicationData.showToast(ActivityAddMark.this, getString(R.string.error_examabout), true);
            return false;
        } else
            return true;
    }

    private void setmark() {

        if (array_exam_no.size() > 0)
            array_exam_no.clear();

        array_exam_no.add(getString(R.string.test));
        for (int i = 1; i <= ApplicationData.examno; i++) {
            array_exam_no.add(getString(R.string.testno) + " " + String.valueOf(i));
        }

        if (studentmarkarray != null) {
            for (int i = 0; i < studentmarkarray.size(); i++) {
                Childbeans bean = studentmarkarray.get(i);
                if (bean.user_id.equals(user_id)) {
                    if (bean.year.equals(selected_year) && bean.semester_id.equals(selected_sem) && bean.subject_id.equals(selected_sub)) {
                        for (int no = 0; no < array_exam_no.size(); no++) {
                            if (bean.exam_no.equalsIgnoreCase(array_exam_no.get(no).replace(getString(R.string.testno) + " ", ""))) {
                                array_exam_no.remove(no);
                                break;
                            }
                        }
                    }
                }
            }
            examadapter.notifyDataSetChanged();
            spn_exam.setAdapter(examadapter);
            if (array_exam_no != null && array_exam_no.size() > 1)
                spn_exam.setSelection(1);
            else
                spn_exam.setSelection(0);
        }
    }

    private void GetSubjectSemesterList(Childbeans bean) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("class_id", class_id);
        map.put("school_id", school_id);
        map.put("user_id", user_id);
        if (!GlobalConstrants.isWifiConnected(ActivityAddMark.this)) {
            return;
        } else {
            ETechAsyncTask task = new ETechAsyncTask(ActivityAddMark.this, this, ConstantApi.GET_SUBJECT_SEMESTER, map);
            task.execute(ApplicationData.main_url + ConstantApi.GET_SUBJECT_SEMESTER + ".php?");
        }
    }

    private void init() {
        // imgback = (ImageView) findViewById(R.id.imgback);
        //txt_class = (TextView) findViewById(R.id.textView3);
        //   information = (LinearLayout) findViewById(R.id.information);
        //img_head = (ImageView) findViewById(R.id.img_head);
        //  textView1 = (TextView) findViewById(R.id.textView1);
        spn_yr = (Spinner) findViewById(R.id.spn_yr);
        spn_sem = (Spinner) findViewById(R.id.spn_sem);
        // text_semester = (TextView) findViewById(R.id.text_semester);
        edt_mark = (EditText) findViewById(R.id.edt_mark);
        edt_exam = (EditText) findViewById(R.id.edt_exam);
        edt_comment = (EditText) findViewById(R.id.edt_comment);
        spn_subject = (Spinner) findViewById(R.id.text_subject);
        txt_save = (TextView) findViewById(R.id.txt_save);
        lyt_save = (LinearLayout) findViewById(R.id.lyt_save);
        //   userprofile = (LinearLayout) findViewById(R.id.userprofile);
        spn_exam = (Spinner) findViewById(R.id.spn_exam);
        txt_year = (TextView) findViewById(R.id.txt_year);
        img_mark = (CircularImageView) findViewById(R.id.img_mark);
        img_delete = (ImageView) findViewById(R.id.img_delete);
        rel_date = (RelativeLayout) findViewById(R.id.rel_date);
        txt_date = (TextView) findViewById(R.id.txtDate);
        rel_image = (RelativeLayout) findViewById(R.id.rel_image);
        img_camera = (ImageView) findViewById(R.id.img_camera);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        //set edit icon on header
        //    textView1.setVisibility(View.GONE);
        //   userprofile.setVisibility(View.VISIBLE);
        //   information.setVisibility(View.GONE);

        rel_date.setOnClickListener(Clicklistener);
        img_delete.setOnClickListener(Clicklistener);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", getResources().getConfiguration().locale);
        txt_date.setText(dateFormat.format(new Date()));

        edt_comment.setOnTouchListener(touchListener);
        edt_comment.setMovementMethod(ArrowKeyMovementMethod.getInstance());//new ScrollingMovementMethod());

    }

    View.OnClickListener Clicklistener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.img_delete) {
                img_mark.setImageResource(R.drawable.circle_blue_full);
                img_delete.setVisibility(View.GONE);
                img_camera.setVisibility(View.VISIBLE);
                removeimage = "yes";
            } else if (v.getId() == R.id.rel_date) {
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

    private void selectImage() {

        final Dialog dlg = new Dialog(ActivityAddMark.this);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.photo_picker_dlg);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        RelativeLayout lyt_album = (RelativeLayout) dlg.findViewById(R.id.lyt_album);
        RelativeLayout lyt_camera = (RelativeLayout) dlg.findViewById(R.id.lyt_camera);
        RelativeLayout lyt_remove = (RelativeLayout) dlg.findViewById(R.id.lyt_remove);
        RelativeLayout lyt_cancel = (RelativeLayout) dlg.findViewById(R.id.lyt_cancel);

        lyt_album.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ApplicationData.isphoto = true;
                Intent intent = new Intent(Intent.ACTION_PICK);

                removeimage = "no";
                filepath = new File(GlobalConstrants.LOCAL_PATH);
                if (!filepath.exists())
                    filepath.mkdirs();
                if (filepath.exists()) {
                    intent.setType("image/*");
                    startActivityForResult(intent, GET_GALLERY);
                }
            }
        });

        lyt_camera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                removeimage = "no";
                ApplicationData.isphoto = true;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                filepath = new File(GlobalConstrants.LOCAL_PATH);
                if (!filepath.exists())
                    filepath.mkdirs();
                if (filepath.exists()) {
                    File f = new File(GlobalConstrants.LOCAL_PATH, "profile_img.png");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, GET_CAMERA);
                } else {
                }

            }
        });

        lyt_remove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                img_mark.setImageResource(R.drawable.circle_blue_full);
                removeimage = "yes";
                img_delete.setVisibility(View.GONE);
                img_camera.setVisibility(View.VISIBLE);
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        lyt_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    removeimage = "no";
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dlg.setCanceledOnTouchOutside(true);
        dlg.show();
    }


    @Override
    public void onTaskComplete(int statusCode, String result, String webserviceCb, Object tag) {
        try {
            if (statusCode == ETechAsyncTask.COMPLETED) {
                JSONObject jObject = new JSONObject(result);

                try {

                    if (webserviceCb.equalsIgnoreCase(ConstantApi.GET_SUBJECT_SEMESTER)) {
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
                            Childbeans data = new Childbeans();
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
                                if (jobuser.has("exam_no"))
                                    bean.exam_no = jobuser.getString("exam_no");
                                if (jobuser.has("image"))
                                    bean.image = jobuser.getString("image");
                                if (jobuser.has("exam_date"))
                                    bean.date = jobuser.getString("exam_date");
                                studentmarkarray.add(bean);
                            }
                        }

                        setadapter();

                    } else if (webserviceCb.equalsIgnoreCase(ConstantApi.SET_MARK_BY_TEACHER)) {
                        String flag = jObject.getString("flag");
                        if (flag.equalsIgnoreCase("1")) {
                            ApplicationData.calldialog(ActivityAddMark.this, "", getString(R.string.save_mark), getString(R.string.str_ok), "",
                                    new ApplicationData.DialogListener() {
                                        @Override
                                        public void diaBtnClick(int diaID, int btnIndex) {
                                            if (btnIndex == 2) {
                                                if (fromViewMark) {
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
                                                }
                                            }
                                        }
                                    }, 2);

                            if (!fromViewMark) {

                                Childbeans bean = new Childbeans();
                                bean.user_id = user_id;
                                bean.year = selected_year;
                                bean.semester_id = selected_sem;
                                bean.class_id = selected_class;
                                bean.subject_id = selected_sub;
                                bean.mark = edt_mark.getText().toString();
                                bean.comment = edt_comment.getText().toString();
                                bean.exam_no = selected_exam;
                                bean.exam_about = edt_exam.getText().toString();
                                bean.date = ApplicationData.convertToNorwei(txt_date.getText().toString(), ActivityAddMark.this);
                                bean.image = photoPath;
                                studentmarkarray.add(bean);

                                edt_mark.setText("");
                                edt_comment.setText("");
                                edt_exam.setText("");
                                photoPath = "";
                                img_delete.setVisibility(View.GONE);
                                img_camera.setVisibility(View.VISIBLE);
                                img_mark.setImageResource(R.drawable.circle_blue_full);
                                spn_yr.setSelection(array_yr.size() - 1);
                                // spn_sem.setSelection(0);
                                // spn_subject.setSelection(0);
                                // spn_exam.setSelection(0);
                                setmark();
                                if (isfirstsemester) {
                                    spn_sem.setSelection(1);
                                } else {
                                    spn_sem.setSelection(2);
                                }

                               /* for(int no=0;no<array_exam_no.size();no++)
                                {
                                    if(selected_exam.equalsIgnoreCase(array_exam_no.get(no).replace(getString(R.string.testno) + "-", "")))
                                    {
                                        array_exam_no.remove(no);
                                        break;
                                    }
                                }

                                examadapter.notifyDataSetChanged();
                                spn_exam.setAdapter(examadapter);*/
                            }

                        } else {
                            if (jObject.has("msg")) {
                                String msg = jObject.getString("msg");
                                ApplicationData.showToast(ActivityAddMark.this, msg, false);
                            }
                            // ApplicationData.showToast(ActivityAddMark.this, R.string.error_save_mark, false);
                        }
                    } else {
                        ApplicationData.showToast(ActivityAddMark.this, R.string.server_error, false);
                    }

                    //setAdapter();
                } catch (Exception e) {
                    Log.e("OfferCategory", "onTaskComplete() " + e, e);
                }
            } else if (statusCode == ETechAsyncTask.ERROR_NETWORK) {
                try {
                    ApplicationData.showToast(ActivityAddMark.this, R.string.msg_operation_error, false);
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
            SubSpinnerListAdapter semtper = new SubSpinnerListAdapter(ActivityAddMark.this, semesterarray, false);
            spn_sem.setAdapter(semtper);

            if (selected_sem != null && selected_sem.length() > 0 && !selected_sem.equalsIgnoreCase("0")) {
                for (int i = 0; i < semesterarray.size(); i++) {
                    if (selected_sem.equalsIgnoreCase(semesterarray.get(i).semester_id)) {
                        spn_sem.setSelection(i);
                        break;
                    }

                }
            } else if (isfirstsemester) {
                spn_sem.setSelection(1);
            } else {
                spn_sem.setSelection(2);
            }
        }
        if (subjectarray != null && subjectarray.size() > 0) {
            SubSpinnerListAdapter subtper = new SubSpinnerListAdapter(ActivityAddMark.this, subjectarray, true);
            spn_subject.setAdapter(subtper);

            if (selected_sub != null && selected_sub.length() > 0 && !selected_sub.equalsIgnoreCase(getString(R.string.select_sub))) {
                for (int i = 0; i < subjectarray.size(); i++) {
                    if (selected_sub.equalsIgnoreCase(subjectarray.get(i).subject_id)) {
                        spn_subject.setSelection(i);
                        selected_class = subjectarray.get(i).class_id;
                        break;
                    }
                }
            }
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GET_CAMERA) {
                if (resultCode == RESULT_OK) {
                    //File f = new File(GlobalConstrants.LOCAL_PATH);
                    if (filepath != null && filepath.exists()) {
                        for (File temp : filepath.listFiles()) {
                            if (temp != null && temp.getName() != null && temp.getName().equals("profile_img.png")) {
                                filepath = temp;
                                break;
                            }
                        }

                        if (filepath.isFile()) {
                            photoPath = filepath.getAbsolutePath();

                            String photoPathParam = null;
                            String externalStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                            if (photoPath.startsWith(externalStorageDir)) {
                                photoPathParam = "file:///mnt/sdcard/" + photoPath.substring(externalStorageDir.length());
                            } else
                                photoPathParam = photoPath;

                            if (sbmp != null && !sbmp.isRecycled()) {
                                sbmp.recycle();
                                sbmp = null;
                            }

                            try {
                                sbmp = BitmapUtil.landtoport(photoPath);
                                BitmapUtil.writeBmpToFile(sbmp, GlobalConstrants.LOCAL_PATH + "profile_img.png");
                                if (sbmp != null) {
                                    img_delete.setVisibility(View.VISIBLE);
                                    img_camera.setVisibility(View.GONE);
                                    img_mark.setImageBitmap(sbmp);
                                    removeimage = "no";
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //    performCropImage();
                           /* Intent intent = new Intent(ActivityAddMark.this, PhotoCropActivity.class);
                            intent.putExtra("photoPath", photoPathParam);
                            startActivityForResult(intent, REQUEST_CODE_PHOTO_CROP);*/
                        }
                    }
                }
            } else if (requestCode == GET_GALLERY) {
                if (resultCode == RESULT_OK) {
                    progressbar.setVisibility(View.VISIBLE);
                    Uri selectedPhoto = data.getData();
                    if (selectedPhoto == null)
                        return;

                    String[] filePath_gallery = {MediaStore.Images.Media.DATA};
                    Cursor c = null;
                    try {
                        c = ActivityAddMark.this.getContentResolver().query(selectedPhoto, filePath_gallery, null, null, null);
                        if (c.moveToFirst()) {
                            try {
                                int columnIndex = c.getColumnIndex(filePath_gallery[0]);
                                photoPath = c.getString(columnIndex);
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (c != null) {
                            c.close();
                            c = null;
                        }
                    }

                    if (photoPath == null && selectedPhoto.getHost().startsWith("com.")) {    //google.android.gallery3d.provider
                        photoPath = data.getData().toString();
                    }
                   /* else if(photoPath==null && data.getData().toString().startsWith("http"))
                    {
                        Log.d(TAG,"image path : "+data.getData().toString());
                    }*/

                    if (photoPath == null)
                        return;

                    if (photoPath.startsWith("http")) {
                        try {
                            File file = new File(GlobalConstrants.LOCAL_PATH);
                            file.mkdirs();

                            final File file2 = new File(file, "csprofile_img.png");

                            if (!file2.exists()) {
                                file2.createNewFile();
                            }

                            FileDownloader.downloadFile(photoPath, file2, new FileDownloader.DownloadListener() {
                                @Override
                                public void onDownloadSuccessful() {
                                    photoPath = file2.getAbsolutePath();

                                    if (sbmp != null && !sbmp.isRecycled()) {
                                        sbmp.recycle();
                                        sbmp = null;
                                    }

                                    try {

                                        sbmp = BitmapUtil.landtoport(photoPath);
                                        BitmapUtil.writeBmpToFile(sbmp, GlobalConstrants.LOCAL_PATH + "profile_img.png");
                                        if (sbmp != null) {
                                            img_delete.setVisibility(View.VISIBLE);
                                            img_camera.setVisibility(View.GONE);
                                            img_mark.setImageBitmap(sbmp);
                                            removeimage = "no";
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    progressbar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onDownloadFailed(String errorMessage) {
                                    progressbar.setVisibility(View.GONE);
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String photoPathParam = null;
                        String externalStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                        if (photoPath.startsWith(externalStorageDir)) {
                            File fileDest = new File(GlobalConstrants.LOCAL_PATH + "/profile_img");

                            File fileSrc = new File(photoPath);

                            try {
                                FileUtils.copyFile(fileSrc, fileDest);
                            } catch (Exception e) {
                                e.printStackTrace();
                                progressbar.setVisibility(View.GONE);
                                return;
                            }

                            photoPath = fileDest.getAbsolutePath();

                            photoPathParam = "file:///mnt/sdcard/" + photoPath.substring(externalStorageDir.length());
                        } else
                            photoPathParam = photoPath;

                        if (sbmp != null && !sbmp.isRecycled()) {
                            sbmp.recycle();
                            sbmp = null;
                        }

                        try {
                            sbmp = BitmapUtil.landtoport(photoPath);
                            BitmapUtil.writeBmpToFile(sbmp, GlobalConstrants.LOCAL_PATH + "profile_img.png");
                            if (sbmp != null) {
                                img_delete.setVisibility(View.VISIBLE);
                                img_camera.setVisibility(View.GONE);
                                img_mark.setImageBitmap(sbmp);
                                removeimage = "no";
                            }
                            progressbar.setVisibility(View.GONE);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }

            } else if (requestCode == REQUEST_CODE_PHOTO_CROP) {
                if (resultCode == RESULT_OK) {
                    if (data == null)
                        return;
                    // Uri imageuri = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                    try {
                        if (imageuri != null) {
                            sbmp = BitmapUtil.decodeUri(ActivityAddMark.this, imageuri);
                            img_delete.setVisibility(View.VISIBLE);
                            img_camera.setVisibility(View.GONE);
                            img_mark.setImageBitmap(sbmp);
                            removeimage = "no";
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                 /*   img_mark.setImageDrawable(null);

                    if (sbmp != null && !sbmp.isRecycled()) {
                        sbmp.recycle();
                        sbmp = null;
                    }

                    photoPath = data.getStringExtra("croppedPhotoPath");
                    if (photoPath == null)
                        return;

                    sbmp = BitmapUtil.getBitmapFromFile(photoPath);
                    if (sbmp != null) {
                        img_delete.setVisibility(View.VISIBLE);
                        img_camera.setVisibility(View.GONE);
                        img_mark.setImageBitmap(sbmp);
                        removeimage = "no";
                    }
                }*/
                    ApplicationData.isphoto = false;
                }
            }

        } else {
            ApplicationData.isphoto = false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CONTACTS_CODE) {

            if (AppUtils.verifyAllPermissions(grantResults)) {
                ApplicationData.isphoto = false;
            } else {
                Toast.makeText(ActivityAddMark.this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    //datepicker
    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        private DatePickerDialog datepic;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            datepic = new DatePickerDialog(ActivityAddMark.this, this, year, month, day);
            Calendar c1 = Calendar.getInstance();
            // int currentyear = c1.get(Calendar.YEAR);
            c1.set(startingyr, 07, 15);
            datepic.getDatePicker().setMinDate(c1.getTimeInMillis() - 10000);
            c1.set(current_yr, 07, 14);
            datepic.getDatePicker().setMaxDate(c1.getTimeInMillis());
            //datepic.getDatePicker().setMaxDate(c1.getTimeInMillis());
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

            txt_date.setText(ApplicationData.convertToNorweiDateyeartime(finalDate, ActivityAddMark.this));
           /* if(!fromViewMark) {
                setmark();
            }*/
        }
    }


//    private File createNewFile(String prefix) {
//        if (prefix == null || "".equalsIgnoreCase(prefix)) {
//            prefix = "IMG_";
//        }
//        File newDirectory = new File(Environment.getExternalStorageDirectory() + "/mypics/");
//        if (!newDirectory.exists()) {
//            if (newDirectory.mkdir()) {
//            }
//        }
//        File file = new File(newDirectory, (prefix + System.currentTimeMillis() + ".jpg"));
//        if (file.exists()) {
//            //this wont be executed
//            file.delete();
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return file;
//    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        // or = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        setRequestedOrientation(orientation);
    }

    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();

        img_delete = null;
        img_camera = null;
        txt_date = null;
        txt_year = null;
        text_semester = null;
        txt_save = null;
        edt_mark = null;
        edt_exam = null;
        edt_comment = null;
        spn_subject = null;
        spn_yr = null;
        spn_sem = null;
        spn_exam = null;
        array_yr = null;
        array_exam_no = null;
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
        selected_exam = null;
        selected_exam_no = null;
        semesterarray = null;
        subjectarray = null;
        characterarray = null;
        studentmarkarray = null;
        img_mark = null;
        removeimage = null;
        path = null;
        bitmap = null;
        sbmp = null;
        rel_date = null;
        rel_image = null;
        userdetail = null;
        mark_detail = null;
        mActivity = null;
        inflater = null;
        screenview = null;
        examadapter = null;
        lyt_save = null;
        TAG = null;
        progressbar = null;
    }
}
