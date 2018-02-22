package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.adapter.teacher.Childbeans;
import com.adapter.teacher.SpinnerListAdapter;
import com.adapter.teacher.StudentSettingListAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudstream.cslink.R;
import com.common.dialog.MainProgress;
import com.common.utils.BitmapUtil;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.common.view.CircularImageView;
import com.xmpp.teacher.Constant;

import org.apache.commons.io.FileUtils;
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
import java.util.ArrayList;

public class FragmentStudentSetting extends ActivityHeader {

    public final static int GET_CAMERA = 1009;
    public final static int GET_GALLERY = 1010;
    private static final int RESULT_OK = -1;
    public static String BROADCAST_CHAT = "com.admin.apps.TEACHER_LIST_RELOAD";
    private static final int REQUEST_CODE_APPROVAL = 1011;
    private ListView _lstStudent;
    private MainProgress pDialog;

    static String _teacher_id;
    private String noti_teacher_id = "";
    UpdaterBroadcastReceiver updateBroadcaseReceiver = null;
    Spinner txtGrade, txtClass;
    RelativeLayout lytGrade, lytClass;

    String[] grades, classes;
    static String school_id = "", grade_id = "", class_id = "";

    static ArrayList<Childbeans> allStudentList = new ArrayList<Childbeans>();
    ArrayList<Childbeans> allArrayList = null;
    ArrayList<Childbeans> arrayGrade = null;
    ArrayList<Childbeans> arrayClass = null;
    StudentSettingListAdapter cAdapter;

    private Dialog myalertDialog = null;

    Activity mActivity;
    private Dialog dlg;
    String user_id = "";
    CheckBox chkParent1, chkParent2, chkBoth, chkContact;
    TextView txtContactName, txtContactMobile;
    CircularImageView imgProfile;
    int imageclick = 0;
    Integer angle = 0;
    private String photoPath;

    String path = GlobalConstrants.path;
    String Response;
    private Bitmap bitmap;
    Bitmap sbmp = null;
    private int index = 0;
    private int initPosition = -1;
    private boolean isChanged = false;
    private Dialog dlgConfirm;
    private TextView txt_sel_class;
    private String parent_id;
    private View rootView;
    private LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;

        inflater = getLayoutInflater();
        rootView = inflater.inflate(R.layout.adres_studentsetting_fragment1, null);
        relwrapp.addView(rootView);

        //set Title
        showheadermenu(FragmentStudentSetting.this, getString(R.string.students), R.color.white_light, false);

        txtGrade = (Spinner) findViewById(R.id.spnGrade);
        txtClass = (Spinner) findViewById(R.id.spnClass);
        _lstStudent = (ListView) findViewById(R.id.lstStudent);
        txt_sel_class = (TextView) findViewById(R.id.txt_sel_class);


        SharedPreferences sharedpref = getSharedPreferences("adminapp", 0);
        school_id = sharedpref.getString("school_id", "");
        _teacher_id = sharedpref.getString("teacher_id", "");

        Bundle bn = getIntent().getBundleExtra("noti_teacher_id");
        if (bn != null) {
            noti_teacher_id = bn.getString("noti_teacher_id", "");
        }

		/*lytGrade = (RelativeLayout) findViewById(R.id.lytGrade);
        lytGrade.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (grades == null) {
					ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_grade), false);
				} else {
					if (grades.length > 0) {
						showListDlg("grade");
					} else {
						ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_grade), false);
					}
				}
			}
		});
		lytClass = (RelativeLayout) findViewById(R.id.lytClass);
		lytClass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (classes == null) {
					ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_class), false);
				} else {
					if (classes.length > 0) {
						showListDlg("class");
					} else {
						ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_class), false);
					}
				}
			}
		});*/

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
                        }
                        initClass();
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

                        if (!arrayClass.get(position).class_name.equalsIgnoreCase(getString(R.string.select_class))){
                                if (!grade_id.equals(getResources().getString(R.string.select_grade)) && !grade_id.equalsIgnoreCase("")) {
                                    class_id = arrayClass.get(position).class_id;
                                    txt_sel_class.setText(getResources().getString(R.string.classs) + "-" + arrayClass.get(position).class_name);
                                    initStudent();
                                } else {
                                    ApplicationData.showToast(mActivity, getResources().getString(R.string.select_grade), false);
                                    txtClass.setSelection(0);
                                }
                            }
                        else
                            txt_sel_class.setText(getString(R.string.student_classs));
                    } else {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_no_class), false);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        _lstStudent.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                //if(allStudentList.get(position).joining.)
                Intent i = new Intent(FragmentStudentSetting.this, StudentApproveRejectActivity.class);
                i.putExtra("data", allStudentList.get(position));
                i.putExtra("index", position);
                startActivityForResult(i, REQUEST_CODE_APPROVAL);
                //user_id = allStudentList.get(position).user_id;
                //index = position;
                //showStudentDetailsDlg(position);
            }
        });

        IntentFilter filter = new IntentFilter(BROADCAST_CHAT);
        updateBroadcaseReceiver = new UpdaterBroadcastReceiver();
        registerReceiver(updateBroadcaseReceiver, filter);

        loadClassList();

        cAdapter = new StudentSettingListAdapter(FragmentStudentSetting.this, allStudentList);
        _lstStudent.setAdapter(cAdapter);
        cAdapter.updateReceiptsList(allStudentList);

    }

    private void setApprove(final boolean b) {
        if (!GlobalConstrants.isWifiConnected(mActivity)) {
            dlg.dismiss();
            return;
        }

        if (pDialog == null)
            pDialog = new MainProgress(mActivity);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.show();

        if (allStudentList.size() < 1) {
            pDialog.dismiss();
            return;
        }
        String url = ApplicationData.getlanguageAndApi(FragmentStudentSetting.this, ConstantApi.SET_STUDENT_PARENT_TEACHER)
                + "teacher_id=" + _teacher_id
                + "&user_id=" + user_id
                + "&nc_parent_id=" + parent_id
                + "&approve=" + (b ? "Y" : "");


        final RequestQueue queue = Volley.newRequestQueue(mActivity);

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            pDialog.dismiss();
                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 1) {

//                                dlg.dismiss();
                                if (b) { // approved
                                    /*Childbeans childbeans = allStudentList.get(index);
                                    childbeans.parent_name = allStudentList.get(index).nc_parent_name;
									childbeans.mobile1 = allStudentList.get(index).nc_mobile;
									childbeans.parent2_name = "";
									childbeans.mobile2 = "";
									childbeans.nc_parent_name = "";
									childbeans.nc_mobile = "";
									childbeans.status1 = "1";
									childbeans.status2 = "2";
									childbeans.status3 = "0";
									childbeans.parent3_name = "";
									childbeans.mobile3 = "";
									allStudentList.set(index, childbeans);
									setlist_toadapter();*/
                                    ApplicationData.showMessage(mActivity,"" ,mActivity.getResources().getString(R.string.msg_success_approve),getString(R.string.str_ok));
                                    //initPosition = _lstStudent.getFirstVisiblePosition();
                                    loadStudentList();
                                } else {
                                    //initPosition = _lstStudent.getFirstVisiblePosition();
                                    ApplicationData.showMessage(mActivity, "", mActivity.getResources().getString(R.string.msg_success_reject), getString(R.string.str_ok));
                                    loadStudentList();
                                }
                                SharedPreferences shrf = getSharedPreferences(Constant.USER_FILENAME,Context.MODE_PRIVATE);
                                int value=Integer.parseInt(shrf.getString("psbadge", "0"));
                                value=value-1;
                                SharedPreferences.Editor editor = shrf.edit();
                                editor.putString("psbadge",String.valueOf(value));
                                editor.commit();
//								setlist_toadapter();
                            } else {
                                //	dlg.dismiss();
                                if(response.has("msg"))
                                {
                                    String msg = response.getString("msg");
                                    ApplicationData.showToast(mActivity,msg, true);
                                }
                                else
                                ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_operation_error), true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                dlg.dismiss();
                ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_operation_error), true);

                if (photoPath != null && photoPath.length() > 0) {
                    new UploadImage().execute();
                }
            }
        });

        queue.add(jsObjRequest);
    }


    public void setlist_toadapter() {
        // TODO Auto-generated method stub
        if (allStudentList != null && allStudentList.size() > 0) {
            cAdapter.notifyDataSetChanged();
        }
        if (initPosition != -1) {
            _lstStudent.setSelection(initPosition);
            initPosition = -1;
        }
    }

    private void loadClassList() {
        String url = ApplicationData.getlanguageAndApi(FragmentStudentSetting.this,ConstantApi.GET_CLASS_TEACHER)
                + "teacher_id=" + _teacher_id;
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
                new Response.Listener<JSONObject>() {

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
                                if(response.has("msg"))
                                {
                                    String msg = response.getString("msg");
                                    ApplicationData.showToast(mActivity,msg, false);
                                }
                                else
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
        //txtGrade.setText(getResources().getString(R.string.str_all));
        grade_id = "";
        grades = null;
        //txtClass.setText(getResources().getString(R.string.str_all));
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
        //txtClass.setText(getResources().getString(R.string.str_all));
        class_id = "";
        classes = null;

        if (school_id.length() == 0) {
            initStudent();
            return;
        } else {
            Childbeans blankBean = new Childbeans();
            blankBean.class_id = "0";
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
        String url = ApplicationData.getlanguageAndApi(FragmentStudentSetting.this,ConstantApi.GET_STUDENT) +
                "school_id=" + school_id + "&grade_id=" + grade_id + "&class_id=" + class_id + "&teacher_id=" + _teacher_id;

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

        RequestQueue queue = Volley.newRequestQueue(mActivity);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String flag = response.getString("flag");
                            pDialog.dismiss();
                            if (Integer.parseInt(flag) == 1) {
                                JSONArray allStudents = response.getJSONArray("allStudents");
                                allStudentList = new ArrayList<Childbeans>();

                                for (int i = 0; i < allStudents.length(); i++) {
                                    Childbeans childbeans = new Childbeans();
                                    JSONObject c = allStudents.getJSONObject(i);

                                    childbeans.user_id = c.getString("user_id");
                                    childbeans.child_name = c.getString("name");
                                    childbeans.name = c.getString("lastname");
                                    childbeans.image = c.getString("image");
                                    childbeans.child_age = ApplicationData.convertToNorweiDateyeartime(c.getString("birthday"), mActivity);
                                    childbeans.class_name = c.getString("class_name");
                                    childbeans.parent_name = c.getString("parent_name");
                                    childbeans.mobile1 = c.getString("parent_phone");
                                    childbeans.parent2_name = c.getString("parent2name");
                                    childbeans.mobile2 = c.getString("parent2mobile");
                                    childbeans.parent3_name = c.getString("contactname");
                                    childbeans.mobile3 = c.getString("contactmobilem");
                                    childbeans.nc_parent_id = c.getString("nc_parent_id");
                                    childbeans.nc_parent_name = c.getString("nc_parent_name");
                                    childbeans.nc_mobile = c.getString("nc_phone");
                                    childbeans.nc_parent_name2 = c.getString("nc_parent_name_2");
                                    childbeans.nc_mobile2 = c.getString("nc_phone_2");
                                    childbeans.status1 = c.getString("status1");
                                    childbeans.status2 = c.getString("status2");
                                    childbeans.status3 = c.getString("status3");
                                    allStudentList.add(childbeans);
                                }
                                // setlist_toadapter();
                                cAdapter = new StudentSettingListAdapter(FragmentStudentSetting.this, allStudentList);
                                _lstStudent.setAdapter(cAdapter);
                            } else {

                                if(response.has("msg"))
                                {
                                    String msg = response.getString("msg");
                                    ApplicationData.showToast(mActivity, msg, false);
                                }
                                else
                                ApplicationData.showToast(mActivity, R.string.msg_no_students, false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
                            setlist_toadapter();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                ApplicationData.showToast(mActivity, R.string.msg_operation_error, false);
				/*cAdapter = new StudentSettingListAdapter(mActivity, allStudentList);
				_lstStudent.setAdapter(cAdapter);*/
                cAdapter.updateReceiptsList(allStudentList);

            }
        });
        queue.add(jsObjRequest);
    }

    private void selectImage() {

        final Dialog dlg = new Dialog(mActivity);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.photo_picker_dlg);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        RelativeLayout lyt_album = (RelativeLayout) dlg.findViewById(R.id.lyt_album);
        RelativeLayout lyt_camera = (RelativeLayout) dlg.findViewById(R.id.lyt_camera);
        RelativeLayout lyt_remove = (RelativeLayout) dlg.findViewById(R.id.lyt_remove);

        lyt_album.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GET_GALLERY);
            }
        });

        lyt_camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(GlobalConstrants.LOCAL_PATH, "profile_img.png");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, GET_CAMERA);
            }
        });

        lyt_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgProfile.setImageResource(R.drawable.cslink_avatar_unknown);
                imageclick = 1;
                try {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (requestCode == REQUEST_CODE_APPROVAL) {

                boolean b = false;
                if (data.hasExtra("falg_approve_reject"))
                    b = data.getBooleanExtra("falg_approve_reject", false);
                if (data.hasExtra("index"))
                    index = data.getIntExtra("index", 0);
                if (data.hasExtra("parent_id"))
                    parent_id = data.getStringExtra("parent_id");
                if (data.hasExtra("user_id"))
                    user_id = data.getStringExtra("user_id");
                if (data.hasExtra("image"))
                    photoPath = data.getStringExtra("image");
                if (data != null && data.hasExtra("falg_approve_reject"))
                    setApprove(b);

            } else if (requestCode == GET_GALLERY) {
                if (resultCode == RESULT_OK) {
                    Uri selectedPhoto = data.getData();
                    if (selectedPhoto == null)
                        return;

                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = null;
                    try {
                        c = mActivity.getContentResolver().query(selectedPhoto, filePath, null, null, null);
                        if (c.moveToFirst()) {
                            try {
                                int columnIndex = c.getColumnIndex(filePath[0]);
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

                    if (photoPath == null)
                        return;

                    String photoPathParam = null;
                    String externalStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                    if (photoPath.startsWith(externalStorageDir)) {
                        File fileDest = new File(GlobalConstrants.LOCAL_PATH + "/profile_img.png");
                        if (fileDest.exists())
                            fileDest.delete();
                        File fileSrc = new File(photoPath);

                        try {
                            FileUtils.copyFile(fileSrc, fileDest);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }

                        photoPath = fileDest.getAbsolutePath();
                        photoPathParam = "file:///mnt/sdcard/" + photoPath.substring(externalStorageDir.length());
                    } else
                        photoPathParam = photoPath;

                    Intent intent = new Intent(mActivity, PhotoCropActivity.class);
                    intent.putExtra("photoPath", photoPathParam);
                    startActivityForResult(intent, 1011);
                }

            } else if (requestCode == 1011) {
                if (resultCode == RESULT_OK) {
                    if (data == null)
                        return;

                    imgProfile.setImageDrawable(null);

                    if (sbmp != null && !sbmp.isRecycled()) {
                        sbmp.recycle();
                        sbmp = null;
                    }

                    photoPath = data.getStringExtra("croppedPhotoPath");
                    if (photoPath == null)
                        return;

                    sbmp = BitmapUtil.getBitmapFromFile(photoPath);
                    if (sbmp != null) {
                        imageclick = 1;
                        imgProfile.setImageBitmap(sbmp);
                    }
                    if (photoPath == null) {
                        ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_no_saved_profile_img), true);
                        return;
                    }

                    new UploadImage().execute();
                }
            }
        } else if (resultCode == 2) {
            _lstStudent.setVisibility(View.VISIBLE);
            loadStudentList();
            /*cAdapter = new StudentSettingListAdapter(FragmentStudentSetting.this, allStudentList);
            _lstStudent.setAdapter(cAdapter);*/
        }
    }

    private void setimage(String absolutePath) {
        // TODO Auto-generated method stub

        if (absolutePath != null) {

            ExifInterface ei = null;
            try {

                ei = new ExifInterface(absolutePath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:

                    angle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:

                    angle = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:

                    angle = 270;
                    break;

                case 0:

                    angle = 0;
                    break;
            }

            try {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inPurgeable = true;
                options.inInputShareable = true;
                Bitmap myBitmap = null;
                try {
                    myBitmap = BitmapFactory.decodeFile(absolutePath,
                            options);
                } catch (Exception e) {
                    ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_bad_image), true);
                    return;
                }


                Matrix mat = new Matrix();
                mat.postRotate(angle);

                Bitmap captureBmp = Bitmap.createBitmap(myBitmap, 0, 0,
                        myBitmap.getWidth(), myBitmap.getHeight(), mat, true);

                File file = new File(path);
                file.createNewFile();
                FileOutputStream ostream = new FileOutputStream(file);
                captureBmp.compress(Bitmap.CompressFormat.JPEG, 60, ostream);
                ostream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            decodeFile(path);
        } else {
            // bitmap = null;
        }

    }

    public void decodeFile(String filePath) {
        // Decode image size

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 512;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        sbmp = Bitmap.createScaledBitmap(bitmap, w, h, false);

        imageclick = 1;
        imgProfile.setImageBitmap(sbmp);
        if (photoPath == null) {
            ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_no_saved_profile_img), true);
            return;
        }

        new UploadImage().execute();
    }

    public class UploadImage extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            if (pDialog == null)
                pDialog = new MainProgress(FragmentStudentSetting.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getResources().getString(R.string.str_wait));
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {

            try {


                String url = ApplicationData.getlanguageAndApi(FragmentStudentSetting.this,ConstantApi.UPLOAD_PROFILE_TEACHER)
                        + "user_id=" + user_id +"&removeimage=no";


                HttpClient httpClient = new DefaultHttpClient();
                HttpPost postRequest = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                if (imageclick == 0) {

                } else if (imageclick == 1) {

                    File file = new File(photoPath);
                    FileBody bin = new FileBody(file);
                    reqEntity.addPart("image", bin);
                    postRequest.setEntity(reqEntity);
                }

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
                        imageclick = 0;
                        String image = response.getString("filename");
                        Childbeans childbeans = allStudentList.get(index);
                        childbeans.image = image;
                        allStudentList.set(index, childbeans);
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

    @Override
    public void onResume() {
        super.onResume();
        setlist_toadapter();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updateBroadcaseReceiver != null) {
            FragmentStudentSetting.this.unregisterReceiver(updateBroadcaseReceiver);
        }

        _lstStudent=null;
        pDialog=null;
        noti_teacher_id = null;
        updateBroadcaseReceiver = null;
        txtGrade=null; txtClass=null;
        lytGrade=null; lytClass=null;
        grades=null;classes=null;
        allArrayList = null;
        arrayGrade = null;
        arrayClass = null;
        cAdapter=null;
        myalertDialog = null;
        mActivity=null;
        dlg=null;
        user_id = null;
        chkParent1=null; chkParent2=null; chkBoth=null; chkContact=null;
        txtContactName=null; txtContactMobile=null;
        imgProfile=null;
        photoPath=null;
        path = null;
        Response=null;
        bitmap=null;
        sbmp = null;
        dlgConfirm=null;
        txt_sel_class=null;
        parent_id=null;
        rootView=null;
        inflater=null;

        System.gc();
    }

    public class UpdaterBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            if (action.equals(BROADCAST_CHAT)) {
                setlist_toadapter();
            }
        }
    }
}
