package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.adapter.parent.Childbeans;
import com.adapter.parent.GradeListAdapter;
import com.adapter.parent.SpinnerListAdapter;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by etech on 3/6/16.
 */
public class SchoolGradeActivity extends Activity {

    ImageView _back;
    ArrayList<Childbeans> allStudentList = null;
    String child_array, Response = "";
    String parent_id, registrationid, language;
    public boolean b = false;
    ListView lstStudent;
    String[] schools, grades, classes;
    String school_id = "";
    String grade_id = "";
    String class_id = "";
    ArrayList<Childbeans> arraySchool = new ArrayList<Childbeans>();
    ArrayList<Childbeans> arrayGrade = null;
    ArrayList<Childbeans> arrayClass = null;
    ArrayList<Childbeans> selectedlist = new ArrayList<Childbeans>();
    ArrayList<Childbeans> allArrayList = new ArrayList<Childbeans>();
    private Dialog myalertDialog = null;
    GradeListAdapter cAdapter;
    TextView txtSchool, txtGrade, txtClass;
    Activity mActivity;
    private MainProgress pDialog;
    private int Request_Load = 100;
    private RelativeLayout rel_search;
    private RelativeLayout rel_auto_srch;
    private Childbeans data;
    private Spinner auto_grad;
    private TextView textView1;

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
        textView1 = (TextView) findViewById(R.id.textView1);
        lstStudent = (ListView) findViewById(R.id.lstStudent);

        rel_auto_srch.setVisibility(View.VISIBLE);
        rel_search.setVisibility(View.GONE);

        _back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        SharedPreferences sharedpref = getSharedPreferences("absentapp", 0);

        parent_id = sharedpref.getString("parent_id", "");
        child_array = sharedpref.getString("child_array", "");
        registrationid = sharedpref.getString("registrationId", "");

        textView1.setText(getString(R.string.select_cls_nm));

        cAdapter = new GradeListAdapter(this, selectedlist, R.drawable.grade);
        lstStudent.setAdapter(cAdapter);
        //get intent data
        if (getIntent().hasExtra("array")) {
            allArrayList = (ArrayList<Childbeans>) getIntent().getSerializableExtra("array");
        }

        if (getIntent().hasExtra("data")) {
            data = (Childbeans) getIntent().getSerializableExtra("data");
            if (data.school_id != null && data.school_id.length() > 0) {
                school_id = data.school_id;
                initGrade(data.school_id);
            }
        }


        //set auto completed text
        SpinnerListAdapter adapter = new SpinnerListAdapter(getApplicationContext(), grades);
        auto_grad.setAdapter(adapter);

        auto_grad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                grade_id = grades[position];
                if (grade_id.equalsIgnoreCase(getString(R.string.search_class_grad))) {
                    //ApplicationData.showToast(mActivity, R.string.msg_no_grade_or_class, false);
                } else
                    initClass(school_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        lstStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // showConfirmRegister(position);
                Childbeans data = (Childbeans) parent.getItemAtPosition(position);
                Intent i = new Intent(SchoolGradeActivity.this, SchoolActivity.class);
                i.putExtra("data", data);
                //startActivityForResult(i, Request_Load);
                startActivity(i);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Request_Load) {
            if (resultCode == 1) {
                if (data.hasExtra("user_id")) {
                    Intent i = new Intent();
                    i.putExtra("user_id", data.getStringExtra("user_id"));
                    setResult(1, i);
                    finish();
                }
            }
        }
    }


    public void back() {
        finish();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        /*Background_work.set_background_time();
        Log.e("onPause calling", "onPause");
        b = false;*/
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        Log.e("onresume calling", "Resume calling");
        super.onResume();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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


    private void initGrade(String school_id) {
        arrayGrade = new ArrayList<Childbeans>();
        grade_id = "";
        grades = null;
        class_id = "";
        classes = null;

        if (school_id.length() == 0) {
            Intent i = new Intent();
            setResult(0, i);
            finish();

        } else {
            Childbeans blankBean = new Childbeans();
            blankBean.name = getString(R.string.search_class_grad);
            arrayGrade.add(blankBean);

            String tmpGrade = "";
            for (int i = 0; i < allArrayList.size(); i++) {
                if (school_id.equals(allArrayList.get(i).school_id) && !tmpGrade.equals(allArrayList.get(i).name)) {
                    tmpGrade = allArrayList.get(i).name;
                    arrayGrade.add(allArrayList.get(i));
                }
            }
           // selectedlist.addAll(arrayGrade);

            grades = new String[arrayGrade.size()];
            for (int j = 0; arrayGrade.size() > j; j++) {
                grades[j] = new String(arrayGrade.get(j).name);
            }

            initClass(school_id);
        }
    }

    private void initClass(String school_id) {
        if (arrayClass != null)
            arrayClass.clear();
        arrayClass = new ArrayList<Childbeans>();

        if(selectedlist!=null)
            selectedlist.clear();
        selectedlist = new ArrayList<Childbeans>();
        class_id = "";
        classes = null;

        if (school_id.length() == 0) {
            // initStudent();
            finish();
            return;
        } else {
           /* Childbeans blankBean = new Childbeans();
            if (grade_id.equals(getString(R.string.search_class_grad)) || grade_id.length() == 0) {
                blankBean.class_id = "";
                blankBean.class_name = getResources().getString(R.string.str_all);
                arrayClass.add(blankBean);
            }*/

            for (int i = 0; i < allArrayList.size(); i++) {
                if (school_id.equals(allArrayList.get(i).school_id)) {
                    if (grade_id.length() == 0) {
                        arrayClass.add(allArrayList.get(i));
                    } else if (grade_id.equals(allArrayList.get(i).name)) {
                        arrayClass.add(allArrayList.get(i));
                    }
                }
            }
            selectedlist.addAll(arrayClass);
            classes = new String[arrayClass.size()];

            for (int j = 0; arrayClass.size() > j; j++) {
                classes[j] = new String(arrayClass.get(j).class_name);
            }
            cAdapter = new GradeListAdapter(this, selectedlist, R.drawable.grade);
            lstStudent.setAdapter(cAdapter);
            cAdapter.notifyDataSetChanged();

        }
    }

    private void initStudent() {
        if (allStudentList != null)
            allStudentList.clear();
        allStudentList = new ArrayList<Childbeans>();

        if (grade_id.length() != 0 || class_id.length() != 0) {
            loadStudentList();
        } else {
            ApplicationData.showToast(mActivity, R.string.msg_no_grade_or_class, false);
            cAdapter.notifyDataSetChanged();
        }

    }

    private void loadStudentList() {
        String url = ApplicationData.getlanguageAndApi(SchoolGradeActivity.this, ConstantApi.GET_NEW_STUDENT_PHONE)
        +"school_id=" + school_id + "&grade_id=" + grade_id + "&class_id=" + class_id;
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
                                JSONArray allStudents = response.getJSONArray("allStudents");


                                int adtuallength = allStudents.length();

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
                                }
                            } else {
                                if(response.has("msg"))
                                {
                                    String msg = response.getString("msg");
                                    ApplicationData.showToast(mActivity, msg, false);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /*cAdapter = new ChildrenListAdapter(mActivity, allStudentList);
						lstStudent.setAdapter(cAdapter);*/
                        cAdapter.notifyDataSetChanged();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        _back=null;
        allStudentList = null;
        child_array=null; Response =null;
        parent_id=null; registrationid=null; language=null;
        lstStudent=null;
        schools=null; grades=null; classes=null;
        school_id =null;
        grade_id =null;
        class_id =null;
        arraySchool =null;
        arrayGrade = null;
        arrayClass = null;
        selectedlist =null;
        allArrayList =null;
        myalertDialog = null;
        cAdapter=null;
        txtSchool=null; txtGrade=null; txtClass=null;
        mActivity=null;
        pDialog=null;
        rel_search=null;
        rel_auto_srch=null;
        data=null;
        auto_grad=null;
        textView1=null;

        System.gc();
    }
}
