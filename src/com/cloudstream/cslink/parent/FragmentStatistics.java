package com.cloudstream.cslink.parent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudstream.cslink.R;
import com.common.dialog.MainProgress;
import com.common.utils.AppUtils;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.common.view.CircularImageView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.natasa.progressviews.CircleProgressBar;
import com.xmpp.parent.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FragmentStatistics extends Fragment {

    private CircleProgressBar circle_progress_dt, circle_progress_hr;
    private TextView txt_usenm;
    private CircularImageView profile_pic;
    private String child_image;
    private String TAG = "FragmentStatistics";
    private final static int REQUEST_CONTACTS_CODE = 100;
    private static String SDCARD_PERMISSIONS[] = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    public FragmentStatistics() {
    }

    private String getsubject_url, child_id, language, child_name;
    private MainProgress pDialog;

    int _date, create_pdf = 0;

    private TextView fromdate, todate, _day, _hours, lang_day, lang_hour,
            get_report;

    private LinearLayout _senddata, _create_pdf, from_date, to_date;

    String pdf_schoolname, pdf_email, pdf_phone, pdf_studentname, pdf_teacher,
            pdf_parentname, pdf_fromdate, pdf_todate, pdf_totaldays,
            pdf_totalhours, pdf_totalabsent, pdf_title, pdf_reason,
            _schoolname, _email, _phone, _studentname, _teacher, _parentname,
            _fromdate, _todate, _totaldays, _totalhours, _ndays, _nhours, _totalabsent, _reason = "";

    Activity mActivity;
    private int current_yr = 1997, startingyr = 1997;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences sharedpref = getActivity().getSharedPreferences(Constant.USER_FILENAME, 0);// childid
        mActivity = this.getActivity();

        child_id = sharedpref.getString("childid", "");
        language = sharedpref.getString("language", "");
        child_name = sharedpref.getString("childname", "");
        child_image = sharedpref.getString("image", "");

        View rootView = inflater.inflate(R.layout.statistics_fragment, container, false);

        change_language();

        _senddata = (LinearLayout) rootView.findViewById(R.id.getdata);
        from_date = (LinearLayout) rootView.findViewById(R.id.fromdate);
        to_date = (LinearLayout) rootView.findViewById(R.id.todate);
        _create_pdf = (LinearLayout) rootView.findViewById(R.id.pdf);
        fromdate = (TextView) rootView.findViewById(R.id.textView_fromdate);
        todate = (TextView) rootView.findViewById(R.id.textView_todate);
        //  lang_absent = (TextView) rootView.findViewById(R.id.textView1);
        get_report = (TextView) rootView.findViewById(R.id.textView2);
        _day = (TextView) rootView.findViewById(R.id.txt_dy1);
        _hours = (TextView) rootView.findViewById(R.id.txt_hr1);
        circle_progress_dt = (CircleProgressBar) rootView.findViewById(R.id.circle_progress);
        circle_progress_hr = (CircleProgressBar) rootView.findViewById(R.id.circle_progress_hr);
        txt_usenm = (TextView) rootView.findViewById(R.id.txt_usenm);
        profile_pic = (CircularImageView) rootView.findViewById(R.id.profile_pic);

        txt_usenm.setText(child_name);
        ApplicationData.setProfileImg(mActivity, ApplicationData.web_server_url + ApplicationData.child_image_path + child_image, profile_pic);
        circle_progress_dt.setStartPositionInDegrees(270);
        circle_progress_hr.setStartPositionInDegrees(270);

        ((MainActivity) getActivity()).title.setText(getString(R.string.absent_statistic));

        _senddata.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                send_data();
            }
        });

        from_date.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                _date = 1;
                datepicker(_date);
            }
        });


        to_date.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                _date = 2;
                datepicker(_date);
            }
        });


        _create_pdf.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (AppUtils.hasSelfPermission(mActivity, SDCARD_PERMISSIONS)) {
                        if (create_pdf == 1) {
                            String pdf_name = child_id + _fromdate + language;
                            createPDF(pdf_name);
                        } else {
                            ApplicationData.showToast(getActivity(), getResources().getString(R.string.msg_send_error), true);
                        }
                    } else {
                        ApplicationData.isphoto = true;
                        requestPermissions(SDCARD_PERMISSIONS, REQUEST_CONTACTS_CODE);
                    }
                } else {
                    if (create_pdf == 1) {
                        String pdf_name = child_id + _fromdate + language;
                        createPDF(pdf_name);
                    } else {
                        ApplicationData.showToast(getActivity(), getResources().getString(R.string.msg_send_error), true);
                    }
                }

            }
        });

        // ..............................TextView......//
        String curr_date = "", init_date = "";
        Date date = new Date();

        int year = date.getYear();

        Date dateInit = new Date(year, 7, 15);
        /*else {
            year=year-1;
            dateInit = new Date(year,7,15);
        }*/
        if (dateInit.before(date)) {
            current_yr = Calendar.getInstance().get(Calendar.YEAR) + 1;
            startingyr = Calendar.getInstance().get(Calendar.YEAR);
        } else {
            current_yr = Calendar.getInstance().get(Calendar.YEAR);
            startingyr = Calendar.getInstance().get(Calendar.YEAR) - 1;
        }

        if (dateInit.after(date))
            dateInit = new Date(year - 1, 7, 15);



        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", this.getResources().getConfiguration().locale);

        curr_date = dateFormat.format(date);
        init_date = dateFormat.format(dateInit);


        fromdate.setText(init_date);
        fromdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _date = 1;
                datepicker(1);
            }
        });


        todate.setText(curr_date);
        todate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _date = 2;
                datepicker(2);
            }
        });

//		lang_day = (TextView) rootView.findViewById(R.id.textView_fromday);
//		lang_hour = (TextView) rootView.findViewById(R.id.textView_tohour);


        _day.setText("0");

        _hours.setText("0");

        //	lang_day.setText(getResources().getString(R.string.str_days));
        //	lang_hour.setText(getResources().getString(R.string.str_hours));
        //lang_absent.setText(getResources().getString(R.string.str_result));
        get_report.setText(getResources().getString(R.string.str_get_report));

        send_data();
        return rootView;
    }

    protected void send_data() {

        _fromdate = ApplicationData.convertFromNorweiDate(fromdate.getText().toString(), mActivity);
        _todate = ApplicationData.convertFromNorweiDate(todate.getText().toString(), mActivity);
        getsubject_url = ApplicationData.getlanguageAndApi(getActivity(), ConstantApi.STATISTICS_BY_PHONE)
                + "child_id=" + child_id + "&from_date="
                + _fromdate + "&to_date="
                + _todate;

        get_subject(getsubject_url);
    }

    private void change_language() {
        // TODO Auto-generated method stub
        pdf_title = getResources().getString(R.string.str_absentreport);
        pdf_schoolname = getResources().getString(R.string.str_schoolname);
        pdf_email = getResources().getString(R.string.str_isemail);
        pdf_phone = getResources().getString(R.string.str_istelphone);
        pdf_studentname = getResources().getString(R.string.str_isstudentname) + "=";
        pdf_teacher = getResources().getString(R.string.str_isteacherincharge);
        pdf_parentname = getResources().getString(R.string.str_isparentname);
        pdf_fromdate = getResources().getString(R.string.str_isfromdate);
        pdf_todate = getResources().getString(R.string.str_istodate);
        pdf_totaldays = getResources().getString(R.string.str_istotaldays);
        pdf_totalhours = getResources().getString(R.string.str_istotalhours);
        pdf_totalabsent = getResources().getString(R.string.str_istotalabsent);
        pdf_reason = getResources().getString(R.string.str_isreason);
    }

    protected void createPDF(String pdf_name) {
        // TODO Auto-generated method stub

        try {
            File file2 = null;
            File sdCard = Environment.getExternalStorageDirectory();
            File file = new File(sdCard.getAbsolutePath() + "/CSlinkFolder/pdf");

            file.mkdirs();

            file2 = new File(file, pdf_name + ".pdf");
            if (!file2.exists()) {
                file2.createNewFile();
            }

            Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.UNDERLINE);
            Font grayFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL, BaseColor.DARK_GRAY);

            Font blackFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file2.getAbsoluteFile()));
            document.open();
            Paragraph preface = new Paragraph();
            addEmptyLine(preface, 1);

            preface.add(new Paragraph(pdf_schoolname + _schoolname, grayFont));
            preface.add(new Paragraph(pdf_email + _email, grayFont));
            preface.add(new Paragraph(pdf_phone + _phone, grayFont));

            addEmptyLine(preface, 1);

            preface.add(new Paragraph(pdf_studentname + _studentname, blackFont));
            preface.add(new Paragraph(pdf_teacher + _teacher, blackFont));
            preface.add(new Paragraph(pdf_parentname + _parentname, blackFont));

            addEmptyLine(preface, 2);

            preface.add(new Paragraph(pdf_title, catFont));
            preface.setAlignment(Element.ALIGN_MIDDLE);

            addEmptyLine(preface, 1);

            preface.add(new Paragraph(_fromdate + "              to              " + _todate, blackFont));

            addEmptyLine(preface, 1);
            preface.add(new Paragraph(pdf_totaldays + _totaldays + "(" + _ndays + ")", blackFont));
            addEmptyLine(preface, 1);

            preface.add(new Paragraph(pdf_totalhours + _totalhours + "(" + _nhours + ")", blackFont));
            addEmptyLine(preface, 1);

            preface.add(new Paragraph(pdf_reason + _reason, blackFont));
            addEmptyLine(preface, 1);

            addEmptyLine(preface, 2);
            preface.add(new Paragraph(pdf_totalabsent + _totalabsent, blackFont));

            document.add(preface);
            document.close();

            //	ApplicationData.showMessage(getActivity(), getResources().getString(R.string.str_success), getResources().getString(R.string.success_create_pdf), getResources().getString(R.string.str_ok));

            openPdfFile(pdf_name);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    protected void datepicker(int _date) {
        // TODO Auto-generated method stub
        DatePickerFragment newFragment = new DatePickerFragment();

        newFragment.show(getFragmentManager(), "DatePicker");
    }

    private void get_subject(String login_url2) {
        // TODO Auto-generated method stub
        if (!GlobalConstrants.isWifiConnected(getActivity())) {
            return;
        }
        if (pDialog == null)
            pDialog = new MainProgress(getActivity());
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, login_url2, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO Auto-generated method stub
                        try {
                            pDialog.dismiss();
                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 1) {
                                create_pdf = 1;

                                _totaldays = response.getString("tdays");
                                _totalhours = response.getString("thours");

                               /* if(Integer.parseInt(_totalhours)>24)
                                {
                                    while (Integer.parseInt(_totalhours)>24) {
                                        _totalhours = String.valueOf(Integer.parseInt(_totalhours) - 24);
                                        _totaldays = String.valueOf(Integer.parseInt(_totaldays)+1);
                                    }
                                }*/
                                _ndays = response.getString("ndays");
                                _nhours = response.getString("nhours");
                                _totalabsent = response.getString("totalabsent");
//								_day.setText(_totaldays + "(" + _ndays +")");
//								_hours.setText(_totalhours + "(" + _nhours +")");
                                _day.setText(_totaldays);
                                _hours.setText(_totalhours);

                                calculatetotaldaysdifferent(_totaldays, _totalhours);


                                if (response.isNull("absent_student_info")) {
                                    ApplicationData.showToast(getActivity(), R.string.msg_no_childinfo, false);
                                } else {
                                    JSONArray jarry = response.getJSONArray("absent_student_info");
                                    for (int i = 0; i < jarry.length(); i++) {
                                        JSONObject obj = jarry.getJSONObject(i);
                                        if ((_reason == null || _reason.length() == 0) && !obj.getString("reason").equalsIgnoreCase("-"))
                                            _reason = obj.getString("reason");
                                        else if (obj.has("reason") && !obj.getString("reason").equalsIgnoreCase("-"))
                                            _reason = _reason + "," + obj.getString("reason");
                                    }

                                    JSONObject parent_onfo = response.getJSONObject("parent_info");
                                    _parentname = parent_onfo.getString("parent_name");
                                    JSONArray user_info = response.getJSONArray("user_info");
                                    JSONObject user_job = user_info.getJSONObject(0);
                                    _studentname = user_job.getString("student_name");
                                    _teacher = user_job.getString("teacher_name");
                                    _email = user_job.getString("school_email");
                                    _schoolname = user_job.getString("school_name");
                                    _phone = user_job.getString("school_phone");

                                }
                            } else {
                                String errcode = response.getString("errcode");
                                if (errcode != null && errcode.equals("3")) {
                                    ApplicationData.showToast(getActivity(), R.string.msg_no_absent_recode, true);
                                } else {
                                    ApplicationData.showToast(getActivity(), R.string.msg_operation_error, true);
                                }
                                _day.setText("0");
                                _hours.setText("0");
                                circle_progress_dt.setProgress(0.0f, 30.0f);
                                circle_progress_hr.setProgress(0.0f, 24.0f);
                            }
                        } catch (Exception e) {
                            ApplicationData.showToast(getActivity(), R.string.msg_operation_error, true);
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                ApplicationData.showToast(getActivity(), R.string.msg_operation_error, true);
            }
        });
        queue.add(jsObjRequest);
    }

    private void calculatetotaldaysdifferent(String totaldays, String totalhours) {

        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        long diff = 0;
        Date startDate = ApplicationData.convertToDate(_fromdate, getActivity());
        Calendar c1 = Calendar.getInstance();
        //Change to Calendar Date
        c1.setTime(startDate);

        Date endDate = ApplicationData.convertToDate(_todate, getActivity());
        Calendar c2 = Calendar.getInstance();
        //Change to Calendar Date
        c2.setTime(endDate);

        //get Time in milli seconds
        long ms1 = c1.getTimeInMillis();
        long ms2 = c2.getTimeInMillis();
        //get difference in milli seconds
        diff = ms2 - ms1;

        //Find number of days by dividing the mili seconds
        int diffInDays = (int) (diff / (24 * 60 * 60 * 1000));
        System.out.println("Number of days difference is: " + diffInDays);


        //find total hours
        int hours = 24;
        circle_progress_dt.setProgress(Float.parseFloat(_totaldays), (float) diffInDays);

        circle_progress_hr.setProgress(Float.parseFloat(_totalhours), (float) hours);
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements
            OnDateSetListener {
        private DatePickerDialog datepic;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            datepic = new DatePickerDialog(getActivity(), this, year, month, day);
            c.set(startingyr,07, 15);
            datepic.getDatePicker().setMinDate(c.getTimeInMillis() - 10000);
            c.set(current_yr, 07, 14);
            datepic.getDatePicker().setMaxDate(c.getTimeInMillis());
            datepic.setButton(DatePickerDialog.BUTTON_POSITIVE, getResources().getString(R.string.str_done), datepic);
            datepic.setButton(DatePickerDialog.BUTTON_NEGATIVE, getResources().getString(R.string.str_cancel), datepic);
            return datepic;
        }

        public void onDateChanged(DatePicker view, int year, int month, int day) {

        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Activity a = getActivity();

            String finalDate = null;
            String datedummyy = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(day);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date myDate = null;
            try {
                myDate = dateFormat.parse(datedummyy);
                finalDate = dateFormat.format(myDate);

                if (_date == 1) {
                    Date toDate = dateFormat.parse(ApplicationData.convertFromNorweiDate(todate.getText().toString(), getActivity()));
                    if (myDate.after(toDate)) {
                        ApplicationData.showMessage(getActivity(), "", getActivity().getString(R.string.invalid_date), getActivity().getString(R.string.str_ok));
                    } else {
                        fromdate.setText(ApplicationData.convertToNorweiDate(finalDate, a));
                        send_data();
                    }
                } else if (_date == 2) {
                    Date fromDate = dateFormat.parse(ApplicationData.convertFromNorweiDate(fromdate.getText().toString(), getActivity()));
                    if (myDate.before(fromDate)) {
                        ApplicationData.showMessage(getActivity(), "", getActivity().getString(R.string.invalid_date), getActivity().getString(R.string.str_ok));
                    } else {
                        todate.setText(ApplicationData.convertToNorweiDate(finalDate, a));
                        send_data();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openPdfFile(String pdf_name) {

        boolean isavailable = ApplicationData.isPdfavailable(getActivity());
        if (isavailable) {
            File file = new File(Environment.getExternalStorageDirectory() + "/CSlinkFolder/pdf", pdf_name + ".pdf");
            Uri path = Uri.fromFile(file);
            Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
            pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfOpenintent.setDataAndType(path, "application/pdf");
            try {
                startActivity(pdfOpenintent);
            } catch (ActivityNotFoundException e) {
                ApplicationData.showToast(getActivity(), R.string.msg_operation_error, false);
            }
        } else {
            ApplicationData.showToast(getActivity(), R.string.pdf_notavailalbe, false);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CONTACTS_CODE) {

            if (AppUtils.verifyAllPermissions(grantResults)) {
                ApplicationData.isphoto = false;
            } else {
                Toast.makeText(getActivity(), "Permission Not Granted", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
