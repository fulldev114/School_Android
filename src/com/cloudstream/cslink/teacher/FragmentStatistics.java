 package com.cloudstream.cslink.teacher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.adapter.teacher.Childbeans;
import com.adapter.teacher.ChildrenListAdapter;
import com.adapter.teacher.ReportListAdapter;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.xmpp.teacher.Constant;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FragmentStatistics extends Fragment {

    private int fragmentvalue = 1;
    private LinearLayout lyn_calendar;
    private ImageView img_search;

    public FragmentStatistics() {
    }

    private MainProgress pDialog;

    int _date;

    String _schoolname, _email, _phone, teacher_id, _teacher,
            _fromdate, _todate, _totaldays, _totalhours, _ndays, _nhours;

    TextView txtToDate, btnSend, txtFromDate;
    LinearLayout fromdate, todate;
    RelativeLayout lytGrade, lytClass;
    Spinner txtGrade, txtClass;
    String[] grades, classes;
    String school_id = "";
    String grade_id = "";
    String class_id = "";
    String Response;

    ArrayList<Childbeans> allStudentList = null;
    ArrayList<Childbeans> allArrayList = null;
    ArrayList<Childbeans> arrayGrade = null;
    ArrayList<Childbeans> arrayClass = null;
    ReportListAdapter cAdapter;

    private Dialog myalertDialog = null;
    private Dialog dlg;

    TextView txtTitle, txtDate, txtDay, txtHour, btnClose, txtDate_to;
    private ListView _lstStudent;
    private boolean islaunchedapp = false;
    private EditText edtSearch;
    private ArrayList<Childbeans> allStudentListOrg = null;

    Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences sharedpref = getActivity().getSharedPreferences(Constant.USER_FILENAME, 0);// childid
        mActivity = this.getActivity();

        teacher_id = sharedpref.getString("teacher_id", "");

        View rootView = inflater.inflate(R.layout.adres_report_fragment, container, false);

        btnSend = (TextView) rootView.findViewById(R.id.btnSend);
        fromdate = (LinearLayout) rootView.findViewById(R.id.fromdate);
        txtFromDate = (TextView) rootView.findViewById(R.id.txtFromDate);
        txtToDate = (TextView) rootView.findViewById(R.id.txtToDate);
        txtGrade = (Spinner) rootView.findViewById(R.id.txtGrade);
        txtClass = (Spinner) rootView.findViewById(R.id.txtClass);
        todate = (LinearLayout) rootView.findViewById(R.id.todate);
        lyn_calendar = (LinearLayout) rootView.findViewById(R.id.lyn_calendar);
        edtSearch = (EditText) rootView.findViewById(R.id.edtSearch);
        img_search = (ImageView) rootView.findViewById(R.id.img_search);
        lyn_calendar.setVisibility(View.GONE);
        btnSend.setVisibility(View.GONE);
        // lytGrade = (RelativeLayout) rootView.findViewById(R.id.lytGrade);
        //  lytClass = (RelativeLayout) rootView.findViewById(R.id.lytClass);
        _lstStudent = (ListView) rootView.findViewById(R.id.lstStudent);

        islaunchedapp = true; // to call loadstudentlist one time

        if (getArguments() != null) {
            fragmentvalue = getArguments().getInt("flag");
            ((MainActivity) getActivity()).title.setTextColor(getResources().getColor(R.color.white_light));
            if (fragmentvalue == 1) {
                ((MainActivity) getActivity()).title.setText(getActivity().getString(R.string.str_report));
            } else
                ((MainActivity) getActivity()).title.setText(getActivity().getString(R.string.char_heading).replace("(+)", ""));
            //str_add_chart
        }

        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*createPDF(pdf_name);
                } else {
					ApplicationData.showToast(getActivity(), getResources().getString(R.string.msg_send_error),true);
				}*/
                if (grade_id != null && grade_id.length() > 0) {
                    if (class_id != null && class_id.length() > 0) {
                        getStatisticByClass();
                    } else {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.select_class), false);
                    }
                } else {
                    ApplicationData.showToast(mActivity, getResources().getString(R.string.select_grade), false);
                }

            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {

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

        img_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setlist_toadapter_without_badge();
            }
        });

        // ..............................TextView......//
        String curr_date = "", init_date = "";
        Date date = new Date();
        int year = date.getYear();
        Date dateInit = new Date(year, 7, 15);
        if (dateInit.after(date))
            dateInit = new Date(year - 1, 7, 15);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", this.getResources().getConfiguration().locale);

        curr_date = dateFormat.format(date);
        init_date = dateFormat.format(dateInit);

        ((MainActivity) getActivity()).lin_information.setVisibility(View.INVISIBLE);
        //   ((MainActivity) getActivity()).title.setText(getString(R.string.str_report));

        txtFromDate.setText(init_date);
        fromdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _date = 1;
                datepicker(1);
            }
        });


        txtToDate.setText(curr_date);
        todate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _date = 2;
                datepicker(2);
            }
        });

        school_id = sharedpref.getString("school_id", "");
        teacher_id = sharedpref.getString("teacher_id", "");

        txtGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (grades == null) {
                    ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_grade), false);
                } else {
                    if (grades.length > 0) {
                        grade_id = arrayGrade.get(position).name;
                        if (grade_id.equals(getResources().getString(R.string.select_grade))) {
                            grade_id = "";
                            if (!islaunchedapp)
                                loadStudentList();
                        }
                        if (!grade_id.equalsIgnoreCase("")) {
                            grade_id = arrayGrade.get(position).name;
                            initClass();
                        }
                    } else {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_grade), false);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        txtClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (classes == null) {
                    ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_class), false);
                } else {
                    if (classes.length > 0) {

                        class_id = arrayClass.get(position).class_id;
                        if (class_id.equals("")) {
                            class_id = "";
                             if (!islaunchedapp)
                            loadStudentList();
                        }
                        if (!class_id.equalsIgnoreCase("")) {
                            //   if (!grade_id.equals(getResources().getString(R.string.select_grade)) && !grade_id.equalsIgnoreCase(""))
                            //  {
                            class_id = arrayClass.get(position).class_id;
                            edtSearch.setText("");
                            initStudent();
                            islaunchedapp=true;
//                            } else {
//                                ApplicationData.showToast(mActivity, getResources().getString(R.string.select_grade), false);
//                                txtClass.setSelection(0);
//                            }
                        }

                    } else {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_class), false);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        _lstStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                if (fragmentvalue == 1) {
                    Intent i = new Intent(getActivity(), ActivityAddMark.class);
                    i.putExtra("child_detail", allStudentListOrg.get(position));
                    startActivity(i);
                } else {
                    Intent i = new Intent(getActivity(), ActivityAddDisciplinBehaviour.class);
                    i.putExtra("child_detail", allStudentListOrg.get(position));
                    startActivity(i);
                }
                /*if (allStudentList.get(position).parent_id != null && !allStudentList.get(position).parent_id.isEmpty() && !allStudentList.get(position).parent_id.equals("null")) {
                    getStatisticByUser(allStudentList.get(position).sender_id, allStudentList.get(position).child_name);
				} else {
					//ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_parent), false);
					getStatisticByUser(allStudentList.get(position).sender_id, allStudentList.get(position).child_name);
				}*/
            }
        });

        loadClassList();

        allStudentList = new ArrayList<Childbeans>();
        cAdapter = new ReportListAdapter(getActivity(), allStudentList, fragmentvalue);
        _lstStudent.setAdapter(cAdapter);

        return rootView;
    }

    private void setlist_toadapter_without_badge() {
        String searchStr = edtSearch.getText().toString();

        if (allStudentListOrg != null)
            allStudentListOrg.clear();
        else
            allStudentListOrg = new ArrayList<Childbeans>();

        for (int i = 0; i < allStudentList.size(); i++) {
            if (allStudentList.get(i).child_name.toLowerCase().contains(searchStr.toLowerCase())) {
                allStudentListOrg.add(allStudentList.get(i));
            }
        }
        if (allStudentListOrg != null && allStudentListOrg.size() > 0) {
            cAdapter.updateReceiptsList(allStudentListOrg);
        }
    }


    protected void getStatisticByUser(final String child_id, final String child_name) {
        _fromdate = ApplicationData.convertToNorwei(txtFromDate.getText().toString(), mActivity);
        _todate = ApplicationData.convertToNorwei(txtToDate.getText().toString(), mActivity);

        String url = ApplicationData.getlanguageAndApi(getActivity(), ConstantApi.STATISTICS_BY_TEACHER)
                + "child_id=" + child_id + "&from_date="
                + _fromdate + "&to_date="
                + _todate;

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
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO Auto-generated method stub
                        try {
                            pDialog.dismiss();
                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 1) {
                                _totaldays = response.getString("tdays");
                                _totalhours = response.getString("thours");
                                _ndays = response.getString("ndays");
                                _nhours = response.getString("nhours");
                                if ((_totaldays != null && !_totaldays.isEmpty() && !_totaldays.equals("0") && !_totaldays.equals("null")) ||
                                        (_totalhours != null && !_totalhours.isEmpty() && !_totalhours.equals("0") && !_totalhours.equals("null"))) {
                                    showReportDlg(child_name);
                                } else {
                                    ApplicationData.showToast(getActivity(), R.string.msg_no_absent_recode, false);
                                }
                            } else {
                                String errcode = response.getString("errcode");
                                if (response.has("msg")) {
                                    String msg = response.getString("msg");
                                    ApplicationData.showToast(getActivity(), msg, false);
                                } else {
                                    if (errcode != null && errcode.equals("3")) {
                                        ApplicationData.showToast(getActivity(), R.string.msg_no_absent_recode, false);
                                    } else {
                                        ApplicationData.showToast(getActivity(), R.string.msg_operation_error, false);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            ApplicationData.showToast(getActivity(), R.string.msg_operation_error, false);
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                ApplicationData.showToast(getActivity(), R.string.msg_operation_error, false);
            }
        });
        queue.add(jsObjRequest);
    }

    private void showReportDlg(final String child_name) {
        if (dlg != null && dlg.isShowing())
            dlg.dismiss();
        dlg = new Dialog(mActivity);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.adres_dlg_report_by_user);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = dlg.getWindow();
        WindowManager.LayoutParams param = window.getAttributes();
        int[] locations = new int[2];

        param.gravity = Gravity.CENTER;// | Gravity.CENTER_HORIZONTAL;

        window.setAttributes(param);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        txtTitle = (TextView) dlg.findViewById(R.id.txtTitle);
        txtTitle.setText(child_name);
        txtDate = (TextView) dlg.findViewById(R.id.txtDate);
        txtDate.setText(txtFromDate.getText().toString());
        txtDay = (TextView) dlg.findViewById(R.id.txtDay);
        txtDay.setText(_totaldays);
        txtHour = (TextView) dlg.findViewById(R.id.txtHour);
        txtHour.setText(_totalhours);
        txtDate_to = (TextView) dlg.findViewById(R.id.txtDate_to);
        txtDate_to.setText(txtToDate.getText().toString());

        btnClose = (TextView) dlg.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
    }

    protected void getStatisticByClass() {
        _fromdate = ApplicationData.convertToNorwei(txtFromDate.getText().toString(), mActivity);
        _todate = ApplicationData.convertToNorwei(txtToDate.getText().toString(), mActivity);

        String url = ApplicationData.getlanguageAndApi(getActivity(), ConstantApi.STATISTICS_BY_CLASS)
                + "teacher_id=" + teacher_id
                + "&from_date=" + _fromdate
                + "&to_date=" + _todate
                + "&school_id=" + school_id
                + "&grade_id=" + grade_id
                + "&class_id=" + class_id;

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
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO Auto-generated method stub
                        try {
                            pDialog.dismiss();
                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 1) {
                                _totaldays = response.getString("tdays");
                                _totalhours = response.getString("thours");
                                _ndays = response.getString("ndays");
                                _nhours = response.getString("nhours");
                                JSONArray school_info = response.getJSONArray("school_info");
                                JSONObject school_job = school_info.getJSONObject(0);

                                _schoolname = school_job.getString("school_name");
                                _phone = school_job.getString("school_phone");
                                _email = school_job.getString("school_email");

                                JSONArray teacher_info = response.getJSONArray("teacher_info");
                                JSONObject teacher_job = teacher_info.getJSONObject(0);
                                _teacher = teacher_job.getString("teacher_name");

                                JSONArray absent_student_info = response.getJSONArray("absent_student_info"); //{user_id, parent_name, class_name, teacher_name, tdays, ndays, thours, nhours}

                              /*  if(fragmentvalue==1)
                                    createPDF(_teacher + "_" + _schoolname, absent_student_info);*/
                                if (fragmentvalue == 1)
                                    ApplicationData.showToast(getActivity(), R.string.success_send_marks, false);
                                else if (fragmentvalue == 2)
                                    ApplicationData.showToast(getActivity(), R.string.success_send_character, false);
                            } else {
                                String errcode = response.getString("errcode");
                                if (errcode != null && errcode.equals("2")) {
                                    ApplicationData.showToast(getActivity(), R.string.msg_send_error, false);
                                } else {
                                    ApplicationData.showToast(getActivity(), R.string.msg_operation_error, false);
                                }
                                /*_day.setText("0");
                                _hours.setText("0");*/
                            }
                        } catch (Exception e) {
                            ApplicationData.showToast(getActivity(), R.string.msg_operation_error, false);
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                ApplicationData.showToast(getActivity(), R.string.msg_operation_error, false);
            }
        });
        queue.add(jsObjRequest);
    }

    protected void createPDF(String pdf_name, JSONArray absent_info) {
        // TODO Auto-generated method stub
        _fromdate = ApplicationData.convertToNorwei(txtFromDate.getText().toString(), mActivity);
        _todate = ApplicationData.convertToNorwei(txtToDate.getText().toString(), mActivity);

        if (pDialog == null)
            pDialog = new MainProgress(getActivity());
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.show();

        try {
            File file2 = null;
            File sdCard = Environment.getExternalStorageDirectory();
            String filePath = sdCard.getAbsolutePath() + "/CSadminFolder/pdf";
            File file = new File(filePath);

            file.mkdirs();

            file2 = new File(file, pdf_name + ".pdf");
            if (!file2.exists()) {
                file2.createNewFile();
            }

            Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.NORMAL);
            Font grayFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.DARK_GRAY);

            Font blackFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL);
            Font blackFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file2.getAbsoluteFile()));
            document.open();
            Paragraph prefaceHeader = new Paragraph();
            prefaceHeader.setAlignment(Element.ALIGN_CENTER);
            String str = "";

            addEmptyLine(prefaceHeader, 2);
            str = makeSpace(17) + getResources().getString(R.string.str_absentreport);
            prefaceHeader.add(new Paragraph(str, catFont));

            addEmptyLine(prefaceHeader, 1);

            str = makeSpace(18) + txtFromDate.getText().toString() + " ~ " + txtToDate.getText().toString();
            prefaceHeader.add(new Paragraph(str, blackFont1));

            addEmptyLine(prefaceHeader, 1);
            prefaceHeader.setAlignment(Element.ALIGN_CENTER);

            document.add(prefaceHeader);

            Paragraph preface = new Paragraph();
//			preface.setAlignment(Element.ALIGN_LEFT);
            str = makeStr(getResources().getString(R.string.str_schoolname), 14) + " : " + _schoolname;
            preface.add(new Paragraph(str, grayFont));
            str = makeStr(getResources().getString(R.string.str_teacher_name), 14) + " : " + _teacher;
            preface.add(new Paragraph(str, grayFont));

            addEmptyLine(preface, 1);

            str = makeStr(getResources().getString(R.string.classs), 8);
            str = str + makeStr(getResources().getString(R.string.str_student_name), 30);
            str = str + makeStr(getResources().getString(R.string.str_days_ndays), 21);
            str = str + makeStr(getResources().getString(R.string.str_hours_nhours), 24);
            preface.add(new Paragraph(str, blackFont2));

            for (int i = 0; absent_info.length() > i; i++) {  //{user_id, user_name, parent_name, grade, class_name, teacher_name, tdays, ndays, thours, nhours}
                JSONObject c = absent_info.getJSONObject(i);
                str = makeStr(c.getString("class_name"), 8);
                str = str + makeStr(c.getString("user_name"), 30);
                str = str + makeStr(c.getString("tdays") + "(" + c.getString("ndays") + ")", 21);
                str = str + makeStr(c.getString("thours") + "(" + c.getString("nhours") + ")", 24);
                preface.add(new Paragraph(str, grayFont));
            }
//			preface.setAlignment(Element.ALIGN_LEFT);
            addEmptyLine(preface, 1);

            str = makeStr(getResources().getString(R.string.str_istotalabsent) + " : ", 18);
            str = str + makeStr(_totaldays + "(" + _ndays + ")" + getResources().getString(R.string.str_days), 25);
            str = str + makeStr(_totalhours + "(" + _nhours + ")" + getResources().getString(R.string.str_hours), 25);
            preface.add(new Paragraph(str, blackFont1));

            document.add(preface);
            document.close();

            openPdfFile(pdf_name);
            new UploadPdf().execute();
            //ApplicationData.showMessage(getActivity(), getResources().getString(R.string.str_success), getResources().getString(R.string.success_create_pdf));
            pDialog.dismiss();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class UploadPdf extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            if (pDialog == null)
                pDialog = new MainProgress(getActivity());
            pDialog.setCancelable(false);
            pDialog.setMessage(getResources().getString(R.string.str_wait));
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {

            try {

                String url = ApplicationData.getlanguageAndApi(getActivity(), ConstantApi.UPLOAD_PDF) + "teacher_id=" + teacher_id;

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost postRequest = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);


                String pdf_name = _teacher + "_" + _schoolname;
                File file = new File(Environment.getExternalStorageDirectory() + "/CSadminFolder/pdf", pdf_name + ".pdf");

                FileBody bin = new FileBody(file);
                reqEntity.addPart("pdf", bin);
                postRequest.setEntity(reqEntity);

                HttpResponse response = httpClient.execute(postRequest);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent(), "UTF-8"));
                String sResponse;
                StringBuilder s = new StringBuilder();

                while ((sResponse = reader.readLine()) != null) {
                    s = s.append(sResponse);
                }
                Response = s.toString();
            } catch (Exception e) {
                Log.e(e.getClass().getName(), e.getMessage());
            }
            pDialog.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (Response != null) {

                try {
                    JSONObject response = new JSONObject(Response);

                    String flag = response.getString("flag");

                    if (Integer.parseInt(flag) == 1) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_success_reported), false);
                    } else {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_operation_error), false);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_operation_error), false);
                }
            } else {
                ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_operation_error), false);
            }
        }
    }

    private void openPdfFile(String pdf_name) {
        boolean isavailable = ApplicationData.isPdfavailable(getActivity());
        if (isavailable) {
            File file = new File(Environment.getExternalStorageDirectory() + "/CSadminFolder/pdf", pdf_name + ".pdf");
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

    private String makeStr(String string, int cnt) {
        String str = "";
        if (string == null) {
            str = makeSpace(cnt);
            return str;
        }

        if (string.length() >= cnt - 1) {
            str = string.substring(0, cnt - 4 - 1) + "... ";
            return str;
        }
        str = string + makeSpace(cnt - string.length());
        return str;
    }

    private String makeSpace(int cnt) {
        String str = "";
        for (int i = 0; i < cnt; i++) {
            str = str + " ";
        }
        return str;
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
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            if (_date == 1) {
                txtFromDate.setText(ApplicationData.convertToNorwei(finalDate, a));
            } else if (_date == 2) {
                txtToDate.setText(ApplicationData.convertToNorwei(finalDate, a));
            }
        }
    }

    public void setlist_toadapter() {
        // TODO Auto-generated method stub
        if (allStudentList != null && allStudentList.size() > 0) {
            cAdapter.updateReceiptsList(allStudentList);
        }
    }

    private void loadClassList() {
        String url = ApplicationData.getlanguageAndApi(getActivity(), ConstantApi.GET_CLASS_TEACHER) +
                "teacher_id=" + teacher_id;

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
                                ApplicationData.showToast(mActivity, R.string.msg_no_class, false);
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
        grade_id = "";
        grades = null;
        class_id = "";
        classes = null;

        if (school_id.length() == 0) {
            initStudent();
            return;
        } else {
            Childbeans blankBean = new Childbeans();
            blankBean.name = getResources().getString(R.string.select_grade);

            arrayGrade.add(blankBean);
            String tmpGrade = "";
            for (int i = 0; i < allArrayList.size(); i++) {
                if (school_id.equals(allArrayList.get(i).school_id) && !tmpGrade.equals(allArrayList.get(i).name)) {
                    tmpGrade = allArrayList.get(i).name;
                    arrayGrade.add(allArrayList.get(i));
                }
                /*if (arrayGrade.size() == 2) {
                    grade_id = allArrayList.get(i).name;
                    //	txtGrade.setText(allArrayList.get(i).name);
                }*/
            }

            grades = new String[arrayGrade.size()];
            for (int j = 0; arrayGrade.size() > j; j++) {
                grades[j] = new String(arrayGrade.get(j).name);
            }


            SpinnerListAdapter adapter = new SpinnerListAdapter(mActivity, grades);
            txtGrade.setAdapter(adapter);

            initClass();
        }
    }

    private void initClass() {
        if (arrayClass != null)
            arrayClass.clear();
        arrayClass = new ArrayList<Childbeans>();
        //	txtClass.setText(getResources().getString(R.string.str_all));
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


            SpinnerListAdapter adapter = new SpinnerListAdapter(mActivity, classes);
            txtClass.setAdapter(adapter);

            initStudent();
        }
    }

    private void initStudent() {
        if (allStudentList != null)
            allStudentList.clear();
        allStudentList = new ArrayList<Childbeans>();

        if (grade_id.length() != 0 || class_id.length() != 0) {
            loadStudentList();
        } else {
            loadStudentList();
        }

    }

    private void loadStudentList() {
        String url = ApplicationData.getlanguageAndApi(getActivity(), ConstantApi.GET_STUDENT) +
                "school_id=" + school_id + "&grade_id=" + grade_id + "&class_id=" + class_id + "&teacher_id=" + teacher_id;
        if (!GlobalConstrants.isWifiConnected(mActivity)) {
            return;
        }
        if (pDialog == null)
            pDialog = new MainProgress(mActivity);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        if (!pDialog.isShowing())
            pDialog.show();

        if (allStudentList != null)
            allStudentList.clear();
        allStudentList = new ArrayList<Childbeans>();

        if (allStudentListOrg != null)
            allStudentListOrg.clear();

        /*if (grade_id.length() == 0 && class_id.length() == 0) {
//			cAdapter = new ChildrenListAdapter(getActivity(), allStudentList);
//			_lstStudent.setAdapter(cAdapter);
            cAdapter.updateReceiptsList(allStudentList);
            pDialog.dismiss();
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_grade_or_class), false);
            return;
        }*/
        RequestQueue queue = Volley.newRequestQueue(mActivity);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String flag = response.getString("flag");
                            pDialog.dismiss();
                            if (Integer.parseInt(flag) == 1) {
                                JSONArray allStudents = response.getJSONArray("allStudents");
                                int noti_position = 0;
                                for (int i = 0; i < allStudents.length(); i++) {
                                    Childbeans childbeans = new Childbeans();
                                    JSONObject c = allStudents.getJSONObject(i);
                                    childbeans.sender_id = c.getString("user_id");
                                    childbeans.class_id = c.getString("class_id");
                                    childbeans.child_name = c.getString("name");
                                    childbeans.child_image = c.getString("image");
                                    childbeans.parent_id = c.getString("parent_id");
                                    childbeans.parent_name = c.getString("parent_name");
                                    childbeans.class_name = c.getString("class_name");
                                    childbeans.child_age = ApplicationData.convertToNorweiDateyeartime(c.getString("birthday"), mActivity);
                                    childbeans.school_name = c.getString("school_name");
                                    childbeans.badge = 0;
                                    allStudentList.add(childbeans);
                                    allStudentListOrg.add(childbeans);
                                }
                                islaunchedapp = false;
                                setlist_toadapter();

                            } else {
                                if (response.has("msg")) {
                                    String msg = response.getString("msg");
                                    ApplicationData.showToast(mActivity, msg, false);
                                } else
                                    ApplicationData.showToast(mActivity, R.string.msg_no_students, false);

                            }
                        } catch (Exception e) {
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
                /*cAdapter = new ChildrenListAdapter(mActivity, allStudentList);
                _lstStudent.setAdapter(cAdapter);*/
                cAdapter.updateReceiptsList(allStudentList);
            }
        });
        queue.add(jsObjRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        // setlist_toadapter();
        setlist_toadapter_without_badge();
    }

    public void onLowMemory() {
        super.onLowMemory();
    }
}
