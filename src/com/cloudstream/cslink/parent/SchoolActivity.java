package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.adapter.parent.AddChildrenListAdapter;
import com.adapter.parent.Childbeans;
import com.adapter.parent.SchoolListAdapter;
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
import com.langsetting.apps.Background_work;
import com.xmpp.parent.Constant;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by etech on 3/6/16.
 */
public class SchoolActivity extends Activity {

    ImageView _back;
    ArrayList<Childbeans> allStudentList = new ArrayList<Childbeans>();
    String child_array, Response = "";
    String parent_id, registrationid, language;
    public boolean b = false;
    ListView lstStudent;
    AddChildrenListAdapter cAdapter;
    TextView txtSchool, txtGrade, txtClass;
    Activity mActivity;
    private MainProgress pDialog;
    private int Request_Load = 100;
    private RelativeLayout rel_search;
    private RelativeLayout rel_auto_srch;
    private Childbeans data;
    private Spinner auto_grad;
    private String user_id = "";
    private TextView textView1;
    private EditText edt_srch;
    private ArrayList<Childbeans> arraySchool = new ArrayList<Childbeans>();
    private TextView txt_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.selectschool_activity);
        mActivity = this;
        _back = (ImageView) findViewById(R.id.imgback);
        rel_search = (RelativeLayout) findViewById(R.id.rel_search);
        rel_auto_srch = (RelativeLayout) findViewById(R.id.rel_auto_srch);
        auto_grad = (Spinner) findViewById(R.id.auto_grad);
        edt_srch = (EditText) findViewById(R.id.edt_srch);
        txt_note = (TextView) findViewById(R.id.txt_note);

        rel_auto_srch.setVisibility(View.GONE);
        rel_search.setVisibility(View.VISIBLE);

        _back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        textView1 = (TextView) findViewById(R.id.textView1);
        parent_id = sharedpref.getString("parent_id", "");
        child_array = sharedpref.getString("child_array", "");
        registrationid = sharedpref.getString("registrationId", "");

        textView1.setText(getString(R.string.select_kid));

        lstStudent = (ListView) findViewById(R.id.lstStudent);


        if (getIntent().hasExtra("data")) {
            data = (Childbeans) getIntent().getSerializableExtra("data");
            if (data != null && data.class_id.length() > 0) {
                loadStudentList(data);

            }
        }

        cAdapter = new AddChildrenListAdapter(SchoolActivity.this, allStudentList);
        lstStudent.setAdapter(cAdapter);

        lstStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                showConfirmRegister(position);

                /*Childbeans data = (Childbeans) parent.getItemAtPosition(position);
                Intent i = new Intent(SchoolActivity.this, SchoolActivity.class);
                i.putExtra("data", data);
                startActivityForResult(i, Request_Load);*/
            }
        });

        edt_srch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    lstStudent.setVisibility(View.VISIBLE);
                }
                if (s.toString() != null && !s.toString().equalsIgnoreCase("")) {
                    if (s.toString().length() > 0) {
                        matchdata(s.toString());
                    }
                } else {
                    String tmpSchoolID = "";
                    arraySchool.clear();
                    arraySchool = new ArrayList<Childbeans>();
                    for (int i = 0; allStudentList.size() > i; i++)
                        if (!tmpSchoolID.equals(allStudentList.get(i).user_id)) {
                            tmpSchoolID = allStudentList.get(i).user_id;
                            arraySchool.add(allStudentList.get(i));
                            //cAdapter.notifyDataSetChanged();
                        }
                    cAdapter = new AddChildrenListAdapter(SchoolActivity.this, arraySchool);
                    lstStudent.setAdapter(cAdapter);
                    cAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edt_srch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String search_term = edt_srch.getText().toString();
                    if (search_term != null && search_term.length() > 0)
                        matchdata(search_term);
                    else
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.search_term), false);

                    return true;
                }
                return false;
            }
        });

    }

    private void matchdata(String s) {
        String tmpSchoolID = "";
        if (allStudentList != null && allStudentList.size() > 0) {
            arraySchool.clear();
            arraySchool = new ArrayList<Childbeans>();
            for (int i = 0; i < allStudentList.size(); i++) {
                if (allStudentList.get(i).child_name != null && allStudentList.get(i).child_name.length() > 0) {

                    if (allStudentList.get(i).child_name.toLowerCase().contains(s.toLowerCase())) {

                        if (!tmpSchoolID.equals(allStudentList.get(i).user_id)) {
                            tmpSchoolID = allStudentList.get(i).user_id;
                            arraySchool.add(allStudentList.get(i));
                        }
                    }
                }
            }

            if (arraySchool != null && arraySchool.size() > 0) {
                lstStudent.setVisibility(View.VISIBLE);
                cAdapter = new AddChildrenListAdapter(SchoolActivity.this, arraySchool);
                lstStudent.setAdapter(cAdapter);
                cAdapter.notifyDataSetChanged();
            } else {
                lstStudent.setVisibility(View.GONE);
                // ApplicationData.showToast(mActivity, getResources().getString(R.string.search_result), false);
            }
        }
        /*else
        {
            lstStudent.setVisibility(View.VISIBLE);
            selectedlist.addAll(allArrayList);
            cAdapter.notifyDataSetChanged();
        }*/
    }


    public void showConfirmRegister(final int position) {
//		final Activity activity = mActivity;

        String studentInfo = getResources().getString(R.string.str_name) + " " + allStudentList.get(position).child_name + "\n";
        studentInfo += getResources().getString(R.string.str_school) + " " + allStudentList.get(position).school_name + "\n";
        studentInfo += getResources().getString(R.string.str_class) + " " + allStudentList.get(position).class_name;
        // studentInfo += getResources().getString(R.string.str_birthday) + " " + allStudentList.get(position).child_age;

        final Dialog dlg = new Dialog(mActivity);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.msgdialog);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView txtTitle = (TextView) dlg.findViewById(R.id.msgtitle);
        txtTitle.setText(getResources().getString(R.string.msg_confirm_register_child));
        txtTitle.setVisibility(View.VISIBLE);

        TextView content = (TextView) dlg.findViewById(R.id.msgcontent);
        content.setText(studentInfo);

        Button dlg_btn_cancel = (Button) dlg.findViewById(R.id.btn_cancel);
        dlg_btn_cancel.setVisibility(View.VISIBLE);
        dlg_btn_cancel.setText(R.string.str_ok);
        dlg_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                    addChild(parent_id, allStudentList.get(position).user_id);
                  /*  Intent i = new Intent();
                    i.putExtra("user_id", allStudentList.get(position).user_id);
                    setResult(1, i);
                    finish();*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button dlg_btn_ok = (Button) dlg.findViewById(R.id.btn_ok);
        dlg_btn_ok.setText(R.string.str_cancel);
        dlg_btn_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//							dlg.setCanceledOnTouchOutside(false);
        try {
            dlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void back() {
        finish();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
      /*  Background_work.set_background_time();
        Log.e("onPause calling", "onPause");
        b = false;*/
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        /*if (b) {
            Log.e("onresume calling", "on Create");
        } else {
            Background_work.set_front_time();
            Log.e("onresume calling", "direct call");
            if (Background_work.check_layout_pincode()) {
                Intent i = new Intent(getApplicationContext(), PasswordActivity.class);
                startActivity(i);
            }
        }*/
    }


    private void loadStudentList(Childbeans data) {
        String url = ApplicationData.getlanguageAndApi(SchoolActivity.this, ConstantApi.GET_NEW_STUDENT_PHONE)
                + "school_id=" + data.school_id + "&grade_id=" + data.name + "&class_id=" + data.class_id;

        if (!GlobalConstrants.isWifiConnected(mActivity)) {
            return;
        }
        if (pDialog == null)
            pDialog = new MainProgress(mActivity);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.show();
        if (allStudentList != null)
            allStudentList.clear();

        allStudentList = new ArrayList<Childbeans>();

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
                                txt_note.setVisibility(View.GONE);
                                lstStudent.setVisibility(View.VISIBLE);
                                //         JSONObject job = response.getJSONObject("allStudents");
                                JSONArray allStudents = response.getJSONArray("allStudents");
                                for (int i = 0; i < allStudents.length(); i++) {
                                    Childbeans childbeans = new Childbeans();
                                    JSONObject c = allStudents.getJSONObject(i);
                                    childbeans.child_image = c.getString("image");
                                    childbeans.child_name = c.getString("name");
                                    childbeans.class_name = c.getString("class_name");
                                    childbeans.child_age = ApplicationData.convertToNorweiDate(c.getString("birthday"), mActivity);
                                    childbeans.user_id = c.getString("user_id");
                                    childbeans.school_name = c.getString("school_name");

                                    allStudentList.add(childbeans);
                                    cAdapter.notifyDataSetChanged();
                                }
                            } else {
                                if (response.has("msg")) {
                                    String msg = response.getString("msg");
                                    txt_note.setVisibility(View.VISIBLE);
                                    lstStudent.setVisibility(View.GONE);
                                    //ApplicationData.showToast(mActivity, msg, false);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /*cAdapter = new ChildrenListAdapter(mActivity, allStudentList);
                        lstStudent.setAdapter(cAdapter);*/


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
				/*cAdapter = new ChildrenListAdapter(mActivity, allStudentList);
				lstStudent.setAdapter(cAdapter);*/
                cAdapter.notifyDataSetChanged();
            }
        });
        queue.add(jsObjRequest);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    private void addChild(String parent_id, String child_id) {
        /*if ( !ApplicationData.checkRight(mActivity) ) {
            return;
        }*/
        String url = ApplicationData.getlanguageAndApi(SchoolActivity.this, ConstantApi.ADD_NEW_CHILD) + "parent_id=" + parent_id + "&child_id=" + child_id;

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
                            pDialog.dismiss();
                            if (Integer.parseInt(flag) == 1) {

                                ApplicationData.showMessage(mActivity, getResources().getString(R.string.str_success), getResources().getString(R.string.msg_register_new_child), getResources().getString(R.string.str_ok));
                                // finish();
                            } else {
                                String errcode = response.getString("errcode");
                                if (response.has("msg")) {
                                    String msg = response.getString("msg");
                                    ApplicationData.showMessage(mActivity, getResources().getString(R.string.str_failed), msg, getResources().getString(R.string.str_ok));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _back = null;
        allStudentList = null;
        child_array = null;
        Response = null;
        parent_id = null;
        registrationid = null;
        language = null;
        lstStudent = null;
        cAdapter = null;
        txtSchool = null;
        txtGrade = null;
        txtClass = null;
        mActivity = null;
        pDialog = null;
        rel_search = null;
        rel_auto_srch = null;
        data = null;
        auto_grad = null;
        user_id = null;
        textView1 = null;
        edt_srch = null;
        arraySchool = null;
        txt_note = null;

        System.gc();
    }
}
