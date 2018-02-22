package com.cloudstream.cslink.teacher;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.teacher.Childbeans;
import com.adapter.teacher.DisciplineBehaveViewAdapter;
import com.cloudstream.cslink.R;
import com.common.Bean.MarkBean;
import com.common.dialog.MainProgress;
import com.common.utils.AppUtils;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.langsetting.apps.Change_lang;
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;
import com.xmpp.teacher.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by etech on 19/10/16.
 */
public class DisciplineBehaveRepotActivity extends ActivityHeader implements AsyncTaskCompleteListener<String> {
    private static MainProgress pDialog;
    private LayoutInflater inflater;
    private LinearLayout screenview;
    private Change_lang change_lang;
    private String _teacher_id, _teacher_image, school_id;
    private LinearLayout lin_head;
    private ListView lst_ds;
    private RelativeLayout rel_markview;
    private LinearLayout lyt_save, lin_note;
    private LinearLayout lin_status;
    private TextView txt_pdf;
    private LinearLayout fromdate, todate;
    private TextView txtFromDate;
    private TextView txtToDate;
    int _date;
    private TextView txt_custom, txt_system;
    private RelativeLayout rel_system, rel_custom;
    int year, month, day;
    private int current_yr = 1997, startingyr = 1997;
    private Childbeans data;
    private String class_id, child_id, disciplin_id = "0", child_name;
    private ArrayList<Childbeans> disciplinecatarray, remarkarray, characterarray, studentmarkarray, detailmarkarray;
    ArrayList<String> semester_name = new ArrayList<String>();
    private DisciplineBehaveViewAdapter characteradapter;
    private static String SDCARD_PERMISSIONS[] = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private final static int REQUEST_CONTACTS_CODE = 100;
    private ArrayList<Childbeans> studentlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        screenview = (LinearLayout) inflater.inflate(R.layout.adres_disciplinebehaviour_report, null);
        relwrapp.addView(screenview);

        SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        change_lang = new Change_lang(getApplicationContext());
        _teacher_id = sharedpref.getString("teacher_id", "");
        _teacher_image = sharedpref.getString("image", "");
        school_id = sharedpref.getString("school_id", "");

        init();
        //show header
        //showheadermenu(DisciplineBehaveRepotActivity.this, child_name, R.color.color_blue_p, false);// getString(R.string.char_heading)

        showheaderusermenu(DisciplineBehaveRepotActivity.this, "", R.color.white_light, true);

        if (getIntent().hasExtra("child_detail")) {
            data = (Childbeans) getIntent().getSerializableExtra("child_detail");
            class_id = (data.class_id != null && data.class_id.length() > 0) ? data.class_id : "";
            child_id = (data.sender_id != null && data.sender_id.length() > 0) ? data.sender_id : "";
            child_name = (data.child_name != null && data.child_name.length() > 0) ? data.child_name : "";

            subject.setText((data.class_name != null && data.class_name.length() > 0) ? data.class_name : "");
            if (data.child_image != null && data.child_image.length() > 0) {
                String url = ApplicationData.web_server_url + "uploads/" + data.child_image;
                ApplicationData.setProfileImg(profileimage, url, DisciplineBehaveRepotActivity.this);
            }
            name.setText(child_name);
        }

        if (getIntent().hasExtra("studentarray")) {
            studentlist = (ArrayList<Childbeans>) getIntent().getSerializableExtra("studentarray");
            studentlist.clear();
            studentlist.add(data);
        }


        // ..............................From Date and To Date ......//
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        //get current year
        String curr_date = "", init_date = "";
        Date date = new Date();
        int year = date.getYear();
        Date dateInit = new Date(year, 07, 15);
        if (dateInit.before(date)) {
            current_yr = Calendar.getInstance().get(Calendar.YEAR) + 1;
            startingyr = Calendar.getInstance().get(Calendar.YEAR);
            dateInit = new Date(year, 07, 15);
        } else {
            current_yr = Calendar.getInstance().get(Calendar.YEAR);
            startingyr = Calendar.getInstance().get(Calendar.YEAR) - 1;
            dateInit = new Date(year - 1, 07, 15);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", this.getResources().getConfiguration().locale);

        curr_date = dateFormat.format(date);
        init_date = dateFormat.format(dateInit);

        txtFromDate.setText(init_date);
        fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _date = 1;
                datepicker(1);
            }
        });


        txtToDate.setText(curr_date);
        todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _date = 2;
                datepicker(2);
            }
        });

        GetDisciplinBehave();
    }

    private void GetDisciplinBehave() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("class_id", class_id);
        map.put("school_id", school_id);
        map.put("user_id", child_id);
        if (!GlobalConstrants.isWifiConnected(DisciplineBehaveRepotActivity.this)) {
            return;
        } else {
            ETechAsyncTask task = new ETechAsyncTask(DisciplineBehaveRepotActivity.this, this, ConstantApi.GET_SUBJECT_SEMESTER, map);
            task.execute(ApplicationData.main_url + ConstantApi.GET_SUBJECT_SEMESTER + ".php?");
        }
    }

    private void init() {

        lst_ds = (ListView) findViewById(R.id.expand_report);
        rel_markview = (RelativeLayout) findViewById(R.id.rel_markview);
        lyt_save = (LinearLayout) findViewById(R.id.lyt_save);
        lin_note = (LinearLayout) findViewById(R.id.lin_note);
        txt_pdf = (TextView) findViewById(R.id.txt_pdf);
        fromdate = (LinearLayout) findViewById(R.id.fromdate);
        txtFromDate = (TextView) findViewById(R.id.txtFromDate);
        txtToDate = (TextView) findViewById(R.id.txtToDate);
        todate = (LinearLayout) findViewById(R.id.todate);
        txt_custom = (TextView) findViewById(R.id.txt_custom);
        txt_system = (TextView) findViewById(R.id.txt_system);
        rel_system = (RelativeLayout) findViewById(R.id.rel_system);
        rel_custom = (RelativeLayout) findViewById(R.id.rel_custom);
        txt_pdf.setText(getString(R.string.download_pdf));

        rel_custom.setOnClickListener(onclicklistener);
        rel_system.setOnClickListener(onclicklistener);
        lyt_save.setOnClickListener(onclicklistener);
    }

    View.OnClickListener onclicklistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int selectedval = 0;
            if (v.getId() == R.id.rel_system) {
                selectedval = 1;
                disciplin_id = disciplinecatarray.get(0).descipline_id;
                changelayout(selectedval);

            } else if (v.getId() == R.id.rel_custom) {
                selectedval = 2;
                disciplin_id = disciplinecatarray.get(1).descipline_id;
                changelayout(selectedval);
            } else if (v.getId() == R.id.lyt_save) {
                ApplicationData.calldialog(DisciplineBehaveRepotActivity.this, "", getString(R.string.send_mail), getString(R.string.str_yes), getString(R.string.str_no), new ApplicationData.DialogListener() {
                    @Override
                    public void diaBtnClick(int diaID, int btnIndex) {
                        String sendmail = "no";
                        if (btnIndex == 2) {
                            sendmail = "yes";
                        } else if (btnIndex == 1) {
                            sendmail = "no";
                        }
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                            if (AppUtils.hasSelfPermission(DisciplineBehaveRepotActivity.this, SDCARD_PERMISSIONS)) {
                                createMarkPdf(DisciplineBehaveRepotActivity.this, detailmarkarray, data, studentlist, sendmail, disciplin_id);
                            } else {
                                ApplicationData.isphoto = true;
                                requestPermissions(SDCARD_PERMISSIONS, REQUEST_CONTACTS_CODE);
                            }
                        } else {
                            createMarkPdf(DisciplineBehaveRepotActivity.this, detailmarkarray, data, studentlist, sendmail, disciplin_id);
                        }
                    }

                }, 2);

            }

        }
    };

    protected void datepicker(int _date) {
        // TODO Auto-generated method stub
        // if(_date==1) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "DatePicker");
        //  }

    }

    private void changelayout(int selectedval) {
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
        callapi();
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private DatePickerDialog datepic;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            if (_date == 1) {
                String fromdt = ApplicationData.convertToNorwei(txtFromDate.getText().toString(), DisciplineBehaveRepotActivity.this);
                year = Integer.parseInt(fromdt.split("-")[0]);
                month = Integer.parseInt(fromdt.split("-")[1]) - 1;
                day = Integer.parseInt(fromdt.split("-")[2]);
            } else if (_date == 2) {
                String todt = ApplicationData.convertToNorwei(txtToDate.getText().toString(), DisciplineBehaveRepotActivity.this);
                year = Integer.parseInt(todt.split("-")[0]);
                month = Integer.parseInt(todt.split("-")[1]) - 1;
                day = Integer.parseInt(todt.split("-")[2]);
            }
            datepic = new DatePickerDialog(DisciplineBehaveRepotActivity.this, this, year, month, day);
            Calendar c1 = Calendar.getInstance();
            c1.set(startingyr, 07, 15);
            datepic.getDatePicker().setMinDate(c1.getTimeInMillis() - 1000);
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

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date myDate = null;
            try {
                myDate = dateFormat.parse(datedummyy);
                if (_date == 2) {
                    if (myDate.before(dateFormat.parse(ApplicationData.convertToNorwei(txtFromDate.getText().toString(), DisciplineBehaveRepotActivity.this)))) {
                        ApplicationData.showMessage(DisciplineBehaveRepotActivity.this, "", getString(R.string.error_todate_msg), getString(R.string.str_ok));
                        return;
                    }
                } else if (_date == 1) {
                    if (myDate.after(dateFormat.parse(ApplicationData.convertToNorwei(txtToDate.getText().toString(), DisciplineBehaveRepotActivity.this)))) {
                        ApplicationData.showMessage(DisciplineBehaveRepotActivity.this, "", getString(R.string.error_fromdate_msg), getString(R.string.str_ok));
                        return;
                    }
                }
                finalDate = dateFormat.format(myDate);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            year = yy;
            month = mon;
            day = dy;
            if (_date == 1) {
                txtFromDate.setText(ApplicationData.convertToNorweiDateyeartime(finalDate, DisciplineBehaveRepotActivity.this));
            } else if (_date == 2) {
                txtToDate.setText(ApplicationData.convertToNorweiDateyeartime(finalDate, DisciplineBehaveRepotActivity.this));
            }

            callapi();
        }
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment1 extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private DatePickerDialog datepic2;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        /*    final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);*/

            String todt = ApplicationData.convertToNorwei(txtToDate.getText().toString(), DisciplineBehaveRepotActivity.this);
            year = Integer.parseInt(todt.split("-")[0]);
            month = Integer.parseInt(todt.split("-")[1]) - 1;
            day = Integer.parseInt(todt.split("-")[2]);
            datepic2 = new DatePickerDialog(DisciplineBehaveRepotActivity.this, this, year, month, day);

            Calendar c1 = Calendar.getInstance();
            c1.set(startingyr, 07, 15);
            datepic2.getDatePicker().setMinDate(c1.getTimeInMillis());
            c1.set(current_yr, 07, 14);
            datepic2.getDatePicker().setMaxDate(c1.getTimeInMillis());
            datepic2.setButton(DatePickerDialog.BUTTON_POSITIVE, getResources().getString(R.string.str_done), datepic2);
            datepic2.setButton(DatePickerDialog.BUTTON_NEGATIVE, getResources().getString(R.string.str_cancel), datepic2);
            return datepic2;
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

           /* if (_date == 1) {
                txtFromDate.setText(ApplicationData.convertToNorweiDateyeartime(finalDate, DisciplineBehaveRepotActivity.this));
            } else if (_date == 2) */
            {
                txtToDate.setText(ApplicationData.convertToNorweiDateyeartime(finalDate, DisciplineBehaveRepotActivity.this));
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

                        if (flag.equalsIgnoreCase("1")) {
                            if (disciplinecatarray != null) disciplinecatarray.clear();
                            if (remarkarray != null) remarkarray.clear();
                            if (characterarray != null) characterarray.clear();
                            if (studentmarkarray != null) studentmarkarray.clear();

                            disciplinecatarray = new ArrayList<Childbeans>();
                            remarkarray = new ArrayList<Childbeans>();
                            characterarray = new ArrayList<Childbeans>();
                            studentmarkarray = new ArrayList<Childbeans>();


                            if (jObject.has("descipline_list")) {
                                JSONArray jseme = jObject.getJSONArray("descipline_list");
                                for (int sem = 0; sem < jseme.length(); sem++) {
                                    JSONObject jobsem = jseme.getJSONObject(sem);
                                    Childbeans bean = new Childbeans();
                                    if (jobsem.has("descipline_id"))
                                        bean.descipline_id = jobsem.getString("descipline_id");
                                    if (jobsem.has("descipline_name"))
                                        bean.descipline_name = jobsem.getString("descipline_name");

                                    disciplinecatarray.add(bean);

                                    if (sem == 0) {
                                        txt_system.setText(jobsem.getString("descipline_name"));
                                        disciplin_id = jobsem.getString("descipline_id");
                                    } else if (sem == 1) {
                                        txt_custom.setText(jobsem.getString("descipline_name"));

                                    }
                                }
                            }
                            if (jObject.has("remarks_list")) {

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
                                    remarkarray.add(bean);
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
                                ApplicationData.showToast(DisciplineBehaveRepotActivity.this, msg, false);
                            } else {
                                ApplicationData.showToast(DisciplineBehaveRepotActivity.this, R.string.no_record, false);
                            }
                        }

                    } else if (webserviceCb.equalsIgnoreCase(ConstantApi.VIEW_DISCIPLINE_BEHAVIOUR_)) {
                        String flag = jObject.getString("flag");

                        if (flag.equalsIgnoreCase("1")) {
                            detailmarkarray = new ArrayList<Childbeans>();

                            if (jObject.has("descipline_details")) {
                                JSONArray jmark = jObject.getJSONArray("descipline_details");
                                if (jmark.length() > 0) {
                                    for (int i = 0; i < jmark.length(); i++) {
                                        JSONObject jobmark = jmark.getJSONObject(i);

                                        Childbeans bean = new Childbeans();
                                        if (jobmark.has("desc_id"))
                                            bean.id = jobmark.getString("desc_id");
                                        if (jobmark.has("user_id"))
                                            bean.user_id = jobmark.getString("user_id");
                                        if (jobmark.has("class_id"))
                                            bean.class_id = jobmark.getString("class_id");
                                        if (jobmark.has("char_date"))
                                            bean.date = jobmark.getString("char_date");
                                        if (jobmark.has("remarks_id"))
                                            bean.remarks_id = jobmark.getString("remarks_id");
                                        if (jobmark.has("descipline_id"))
                                            bean.descipline_id = jobmark.getString("descipline_id");
                                        if (jobmark.has("teacher_id"))
                                            bean.teacher_id = jobmark.getString("teacher_id");
                                        if (jobmark.has("comment"))
                                            bean.comment = jobmark.getString("comment");
                                        if (jobmark.has("created_date"))
                                            bean.created_at = jobmark.getString("created_date");
                                        if (jobmark.has("name"))
                                            bean.teacher_name = jobmark.getString("name");
                                        if (jobmark.has("image"))
                                            bean.image = jobmark.getString("image");
                                        if (jobmark.has("descipline_name"))
                                            bean.descipline_name = jobmark.getString("descipline_name");
                                        if (jobmark.has("remarks_name"))
                                            bean.remarks_name = jobmark.getString("remarks_name");

                                        detailmarkarray.add(bean);
                                    }
                                    rel_markview.setVisibility(View.VISIBLE);
                                    lin_note.setVisibility(View.GONE);
                                } else {
                                    rel_markview.setVisibility(View.GONE);
                                    lin_note.setVisibility(View.VISIBLE);
                                }
                                lst_ds.setVisibility(View.VISIBLE);
                                characteradapter = new DisciplineBehaveViewAdapter(DisciplineBehaveRepotActivity.this, detailmarkarray, lst_ds, data, rel_markview, lin_note);
                                lst_ds.setAdapter(characteradapter);
                            }
                        } else {
                            if (jObject.has("msg")) {
                                rel_markview.setVisibility(View.GONE);
                                lin_note.setVisibility(View.VISIBLE);
                            }
                        }

                    } else {
                        ApplicationData.showToast(DisciplineBehaveRepotActivity.this, R.string.server_error, false);
                    }

                    //setAdapter();
                } catch (Exception e) {
                    Log.e("DisciplineBehaveRepot", "onTaskComplete() " + e, e);
                }
            } else if (statusCode == ETechAsyncTask.ERROR_NETWORK) {
                try {
                    ApplicationData.showToast(DisciplineBehaveRepotActivity.this, R.string.msg_operation_error, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void callapi() {

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", child_id);
        map.put("class_id", class_id);
        map.put("from_date", ApplicationData.convertToNorwei(txtFromDate.getText().toString(), DisciplineBehaveRepotActivity.this));
        map.put("to_date", ApplicationData.convertToNorwei(txtToDate.getText().toString(), DisciplineBehaveRepotActivity.this));
        map.put("descipline_id", disciplin_id);
        if (!GlobalConstrants.isWifiConnected(DisciplineBehaveRepotActivity.this)) {
            return;
        } else {
            ETechAsyncTask task = new ETechAsyncTask(DisciplineBehaveRepotActivity.this, this, ConstantApi.VIEW_DISCIPLINE_BEHAVIOUR_, map);
            task.execute(ApplicationData.main_url + ConstantApi.VIEW_DISCIPLINE_BEHAVIOUR_ + ".php?");
        }
    }


    public void createMarkPdf(Context context, List<Childbeans> subtopdfarray, Childbeans data, ArrayList<Childbeans> studentlist,
                              String sendmail, String disciplin_id) {

        float actualheight = 0, newheight = 0;
        int pageno = -1;

        if (pDialog == null)
            pDialog = new MainProgress(context);
        pDialog.setCancelable(false);
        pDialog.setMessage(context.getString(R.string.str_wait));
        pDialog.show();

        ApplicationData.isphoto = true;

        SimpleDateFormat dateformt = new SimpleDateFormat("dd-MMM-yyyy_mmss");
        String date = dateformt.format(Calendar.getInstance().getTime());

        String pdfname = "Characterpdf_" + date;
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

            Font blackFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
            Font blackFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL);

            //start document to write
            Document document = new Document();
            PdfWriter write = PdfWriter.getInstance(document, new FileOutputStream(file2.getAbsoluteFile()));


            //margin to document
            document.top(4.5f);
            document.left(10f);
            document.right(10f);
            document.bottom(4.5f);

            actualheight = document.getPageSize().getHeight(); // PDF Page height

            String child_name = "", classname = "", schoolname = "";
            child_name = studentlist.get(0).child_name;
            classname = studentlist.get(0).class_name;
            schoolname = studentlist.get(0).school_name;

            // for (int stdloop = 0; stdloop < subtopdfarray.size(); stdloop++)
            {
                //if createpdf for single user

                Paragraph prefaceHeader = new Paragraph();
                prefaceHeader.setAlignment(Element.ALIGN_LEFT);

                //add two empty line
                addEmptyLine(prefaceHeader, 2);

                String header = context.getResources().getString(R.string.str_schoolname) + " : " + schoolname;
                prefaceHeader.add(new Paragraph(header, blackFont2));

                String header2 = Character.toUpperCase(context.getResources().getString(R.string.str_student_name).charAt(0)) + context.getResources().getString(R.string.str_student_name).substring(1).toLowerCase() + " : " + child_name;
                prefaceHeader.add(new Paragraph(header2, blackFont1));

                addEmptyLine(prefaceHeader, 1);

                header2 = context.getResources().getString(R.string.clsname) + " : " + classname;
                prefaceHeader.add(new Paragraph(header2, blackFont1));

                addEmptyLine(prefaceHeader, 1);
                PdfPTable table1 = new PdfPTable(1);
                PdfPCell cell = new PdfPCell();
                table1.getDefaultCell().setBorder(Rectangle.TOP);
                table1.setWidthPercentage(100);
                table1.addCell(cell);
                addEmptyLine(prefaceHeader, 1);

                String category = context.getString(R.string.category) + " " + subtopdfarray.get(0).descipline_name;

                Paragraph prefacecat = new Paragraph();
                prefacecat.add(new Paragraph(category, blackFont1));

                addEmptyLine(prefacecat, 1);


                Paragraph prefacemiddle = new Paragraph();

                float[] columnWidths = {1.5f, 2.5f, 4f, 2f};
                PdfPTable table = new PdfPTable(columnWidths);
                table.setWidthPercentage(100);
                insertcell(table, context.getString(R.string.date), Element.ALIGN_LEFT, 1, blackFont1);
                insertcell(table, context.getString(R.string.remarks), Element.ALIGN_LEFT, 1, blackFont1);
                insertcell(table, context.getString(R.string.comment).replace("s", ""), Element.ALIGN_LEFT, 1, blackFont1);
                insertcell(table, context.getString(R.string.teacher_head), Element.ALIGN_LEFT, 1, blackFont1);
                table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                // table.setHeaderRows(1);
                // prefacemiddle.add(table);


                for (int i = 0; i < subtopdfarray.size(); i++) {
                    //  for (int j = 0; j < subtopdfarray.get(i).markarray.size(); j++)
                    {
                        float noofline=0;
                        if(subtopdfarray.get(i).comment.length()<=1) {
                            if(subtopdfarray.get(i).teacher_name.contains("\n")){
                               String s[]= subtopdfarray.get(i).teacher_name.split("\n");
                                if(s.length>1)
                                    noofline=s.length+1;
                                else
                                    noofline = subtopdfarray.get(i).teacher_name.length() > 15 ? 2 : 1;
                            }
                            else
                                noofline = subtopdfarray.get(i).teacher_name.length() > 15 ? 2 : 1;

                        }
                        else if(subtopdfarray.get(i).comment.length()>=25) {
                            noofline = subtopdfarray.get(i).comment.length() / 25;
                            if(subtopdfarray.get(i).comment.contains("\n"))
                            {
                                String s[]=subtopdfarray.get(i).comment.split("\n");
                                noofline=s.length+1;
                            }
                        }
                        else
                            noofline=subtopdfarray.get(i).comment.length();

                        float commentheight = noofline * 10;

                        if (pageno == -1) {
                            float temp =commentheight + 140;
                            newheight = temp;
                            if (actualheight < temp) {
                                Rectangle rect = new Rectangle(document.getPageSize().getWidth(), newheight);
                                document.setPageSize(rect);
                            }

                        } else if (pageno == document.getPageNumber() && pageno!=-1) {
                            float remainingheight = newheight > document.getPageSize().getHeight() ? newheight - document.getPageSize().getHeight() : document.getPageSize().getHeight() - newheight;
                            if (write.getVerticalPosition(true)<110 || write.getVerticalPosition(true)<commentheight){//remainingheight < commentheight ||
                                newheight = commentheight;
                                if (actualheight < commentheight) {
                                    Rectangle rect = new Rectangle(document.getPageSize().getWidth(), newheight);
                                    document.setPageSize(rect);
                                }
                                else
                                {
                                    Rectangle rect1 = new Rectangle(document.getPageSize().getWidth(), actualheight);
                                    document.setPageSize(rect1);
                                }
                                pageno=2;
                            } else {
                                newheight += commentheight;
                            }
                        }

                        if (i == 0) {
                            document.open();
                            document.add(prefaceHeader);
                            document.add(table1);
                            document.add(prefacecat);
                            document.add(table);
                            pageno = document.getPageNumber();
                        }
                        float[] columnWidth = {1.5f, 2.5f, 4f, 2f};
                        PdfPTable tabledata = new PdfPTable(columnWidth);
                        insertcell(tabledata, ApplicationData.convertToreport(subtopdfarray.get(i).date, context), Element.ALIGN_LEFT, 1, blackFont2);
                        insertcell(tabledata, subtopdfarray.get(i).remarks_name, Element.ALIGN_LEFT, 1, blackFont2);
                        insertcell(tabledata, subtopdfarray.get(i).comment, Element.ALIGN_LEFT, 1, blackFont2);
                        insertcell(tabledata, subtopdfarray.get(i).teacher_name, Element.ALIGN_LEFT, 1, blackFont2);
                        tabledata.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                        tabledata.setSpacingAfter(5);
                        tabledata.setWidthPercentage(100);

                        if(pageno==2) {
                            document.newPage();
                            tabledata.setTotalWidth(document.getPageSize().getWidth());
                            tabledata.writeSelectedRows(i, -1, document.left(),
                                    tabledata.getTotalHeight() - document.top(), write.getDirectContent());
                            pageno=document.getPageNumber();
                        }

                        //prefacemiddle.add(tabledata);
                       // addEmptyLine(prefacemiddle, 1);


                        document.add(tabledata);
                        document.add(prefacemiddle);
                    }
                }
            //    document.add(prefacemiddle);

            }
            pDialog.dismiss();
            document.close();
            openPdfFile(context, pdfname);
            new ApplicationData.UploadPdf(context, pdfname, sendmail, "characterreport").execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void openPdfFile(Context context, String pdfname) {
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

    private static PdfPCell getCell(String data, int alignLeft) {
        PdfPCell pcell = new PdfPCell(new Phrase(data));
        pcell.setBorder(PdfPCell.NO_BORDER);
        return pcell;
    }

    private void insertcell(PdfPTable table, String text, int align, int colspan, Font font) {

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        cell.setBorder(-1);
        //in case there is no text and you wan to create an empty row
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        //add the call to the table
        table.addCell(cell);

    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CONTACTS_CODE) {

            if (AppUtils.verifyAllPermissions(grantResults)) {
                ApplicationData.isphoto = false;
            } else {
                Toast.makeText(DisciplineBehaveRepotActivity.this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _teacher_id=null; _teacher_image=""; school_id="";
        data=null;
        class_id=""; child_id=""; disciplin_id = ""; child_name="";
        disciplinecatarray=null; remarkarray=null; characterarray=null; studentmarkarray=null;
        detailmarkarray=null;
        semester_name = null;
        characteradapter=null;

        System.gc();
    }
}
