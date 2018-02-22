package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddChildActivity extends Activity {
    ImageView _back;

    ArrayList<Childbeans> allStudentList = null;

    String[] videoNames;

    String child_array, Response = "";

    String parent_id, registrationid, language;

    public boolean b = false;
    ListView lstStudent;

    String[] schools, grades, classes;
    String school_id = "";
    String grade_id = "";
    String class_id = "";

    ArrayList<Childbeans> arraySchool = new ArrayList<Childbeans>();

    ArrayList<Childbeans> allArrayList = new ArrayList<Childbeans>();
    ArrayList<Childbeans> selectedlist = new ArrayList<Childbeans>();

    SchoolListAdapter cAdapter;

    Activity mActivity;
    private MainProgress pDialog;
    private EditText edt_srch;
    private int Request_Load = 100;
    private TextView textView1;
    private ImageView img_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.selectschool_activity);
        mActivity = this;
        _back = (ImageView) findViewById(R.id.imgback);
        _back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        SharedPreferences sharedpref = getSharedPreferences("absentapp", 0);

        parent_id = sharedpref.getString("parent_id", "");
        child_array = sharedpref.getString("child_array", "");
        registrationid = sharedpref.getString("registrationId", "");
        textView1 = (TextView) findViewById(R.id.textView1);
        edt_srch = (EditText) findViewById(R.id.edt_srch);
        lstStudent = (ListView) findViewById(R.id.lstStudent);
        img_search = (ImageView) findViewById(R.id.img_search);

        textView1.setText(getString(R.string.str_select_school));

        loadClassList();

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
                    for (int i = 0; allArrayList.size() > i; i++)
                        if (!tmpSchoolID.equals(allArrayList.get(i).school_id)) {
                            tmpSchoolID = allArrayList.get(i).school_id;
                            arraySchool.add(allArrayList.get(i));
                            //cAdapter.notifyDataSetChanged();
                        }
                    if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                        cAdapter = new SchoolListAdapter(AddChildActivity.this, arraySchool, R.drawable.school_icon2);
                    } else
                        cAdapter = new SchoolListAdapter(AddChildActivity.this, arraySchool, R.drawable.school_icon);

                    lstStudent.setAdapter(cAdapter);
                    cAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        img_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String search_term = edt_srch.getText().toString();
                if (search_term != null && search_term.length() > 0)
                    matchdata(search_term);
                else
                    ApplicationData.showToast(mActivity, getResources().getString(R.string.search_term), false);

            }
        });


        lstStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // showConfirmRegister(position);
                Childbeans data = (Childbeans) parent.getItemAtPosition(position);
                Intent i = new Intent(AddChildActivity.this, SchoolGradeActivity.class);
                i.putExtra("data", data);
                i.putExtra("array", allArrayList);
                // startActivityForResult(i, Request_Load);
                startActivity(i);
            }
        });


        allStudentList = new ArrayList<Childbeans>();

        //	cAdapter = new ChildrenListAdapter(this, allStudentList);

    }

    //search list
    private void matchdata(String s) {
        String tmpSchoolID = "";
        if (allArrayList != null && allArrayList.size() > 0) {
            arraySchool.clear();
            arraySchool = new ArrayList<Childbeans>();
            for (int i = 0; i < allArrayList.size(); i++) {
                if (allArrayList.get(i).school_name != null && allArrayList.get(i).school_name.length() > 0) {
                    if (allArrayList.get(i).school_name.toLowerCase().contains(s.toLowerCase())) {

                        if (!tmpSchoolID.equals(allArrayList.get(i).school_id)) {
                            tmpSchoolID = allArrayList.get(i).school_id;
                            arraySchool.add(allArrayList.get(i));
                        }
                    }
                }
            }

            if (arraySchool != null && arraySchool.size() > 0) {
                lstStudent.setVisibility(View.VISIBLE);
                if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    cAdapter = new SchoolListAdapter(AddChildActivity.this, arraySchool, R.drawable.school_icon2);
                } else
                    cAdapter = new SchoolListAdapter(AddChildActivity.this, arraySchool, R.drawable.school_icon);

                lstStudent.setAdapter(cAdapter);
                cAdapter.notifyDataSetChanged();
            } else {
                lstStudent.setVisibility(View.GONE);
                ApplicationData.showToast(mActivity, getResources().getString(R.string.search_result), false);
            }
        }
        /*else
        {
            lstStudent.setVisibility(View.VISIBLE);
            selectedlist.addAll(allArrayList);
            cAdapter.notifyDataSetChanged();
        }*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Request_Load) {
            if (resultCode == 1) {
                if (data != null) {
                    if (getIntent().hasExtra("user_id")) {
                        String child_id = data.getStringExtra("user_id");
                        addChild(parent_id, child_id);
                    }
                }
            }
        }
    }

    private void addChild(String parent_id, String child_id) {
        if (!ApplicationData.checkRight(mActivity)) {
            return;
        }
        String url = ApplicationData.getlanguageAndApi(AddChildActivity.this, ConstantApi.ADD_NEW_CHILD) +
                "parent_id=" + parent_id + "&child_id=" + child_id;

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
                                finish();
                            } else {
                                String errcode = response.getString("errcode");
                                String msg = response.getString("msg");
                                ApplicationData.showMessage(mActivity, getResources().getString(R.string.str_failed), msg, getResources().getString(R.string.str_ok));
                                /*if (errcode.equals("2"))
									ApplicationData.showMessage(mActivity, getResources().getString(R.string.str_failed), getResources().getString(R.string.failed_registered_already),getResources().getString(R.string.str_ok));
								else
									ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);*/
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

    private void loadClassList() {
        String url = ApplicationData.getlanguageAndApi(AddChildActivity.this, ConstantApi.GET_ALL_CLASS_BY_PHONE);
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
                                //   JSONObject job = response.getJSONObject("allClasses");
                                JSONArray allClasses = response.getJSONArray("allClasses");

                                if (allArrayList != null)
                                    allArrayList.clear();
                                if (arraySchool != null)
                                    arraySchool.clear();
                                allArrayList = new ArrayList<Childbeans>();
                                arraySchool = new ArrayList<Childbeans>();

                                String tmpSchoolID = "";
                                Childbeans childbeans = null;
                                for (int i = 0; allClasses.length() > i; i++) {
                                    JSONObject c = allClasses.getJSONObject(i);
                                    childbeans = new Childbeans();
                                    childbeans.school_id = c.getString("school_id");
                                    childbeans.school_name = c.getString("school_name");
                                    childbeans.class_id = c.getString("class_id");
                                    childbeans.class_name = c.getString("class_name");
                                    childbeans.name = c.getString("grade");
                                    allArrayList.add(childbeans);
                                    if (!tmpSchoolID.equals(childbeans.school_id)) {
                                        tmpSchoolID = childbeans.school_id;
                                        arraySchool.add(childbeans);
                                    }
                                }

                                selectedlist.addAll(allArrayList);
                                if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                                    cAdapter = new SchoolListAdapter(AddChildActivity.this, arraySchool, R.drawable.school_icon2);
                                } else
                                    cAdapter = new SchoolListAdapter(AddChildActivity.this, arraySchool, R.drawable.school_icon);

                                lstStudent.setAdapter(cAdapter);
                                //   cAdapter.notifyDataSetChanged();
                            } else {
                                ApplicationData.showToast(mActivity, R.string.msg_child_not_exist, false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("error msg", e.getMessage());
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

    public void back() {
        finish();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //	Background_work.set_background_time();
        Log.e("onPause calling", "onPause");
        b = false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _back = null;
        allStudentList = null;
        videoNames = null;
        child_array = null;
        Response = null;
        parent_id = null;
        registrationid = null;
        language = null;
        lstStudent = null;
        schools = null;
        grades = null;
        classes = null;
        school_id = null;
        grade_id = null;
        class_id = null;
        arraySchool = null;
        allArrayList = null;
        selectedlist = null;
        cAdapter = null;
        mActivity = null;
        pDialog = null;
        edt_srch = null;
        textView1 = null;
        img_search = null;

        System.gc();
    }
}
