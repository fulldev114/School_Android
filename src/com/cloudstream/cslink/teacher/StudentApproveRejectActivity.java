package com.cloudstream.cslink.teacher;

import android.*;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.teacher.Childbeans;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.common.dialog.MainProgress;
import com.common.utils.AppUtils;
import com.common.utils.BitmapUtil;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.common.view.CircularImageView;
import com.common.view.RoundedImageView;
import com.xmpp.teacher.Constant;
import com.cloudstream.cslink.R;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by etech on 16/6/16.
 */
public class StudentApproveRejectActivity extends ActivityHeader {
    private StudentApproveRejectActivity mActivity;
    TextView txtTitle, txtClass;//txt_title
    EditText txtParent1Name, txtParent1Mobile, txtParent2Name, txtParent2Mobile;

    View lytParent1name, lytParent1mobile, lytParent2name, lytParent2mobile;


    TextView btnClose, btnApprove, btnReject;
    private boolean isChanged = false;
    RoundedImageView imgProfile;
    //CheckBox chkParent1, chkParent2, chkBoth, chkContact;
    public final static int GET_CAMERA = 1009;
    public final static int GET_GALLERY = 1010;
    private static final int RESULT_OK = -1;
    int imageclick = 0;
    private String photoPath;
    ArrayList<Childbeans> allStudentList = null;
    ArrayList<Childbeans> allArrayList = null;
    ArrayList<Childbeans> arrayGrade = null;
    ArrayList<Childbeans> arrayClass = null;
    private static final int REQUEST_CODE_PHOTO_CROP = 1011;
    private final static int REQUEST_CONTACTS_CODE = 100;
    Bitmap sbmp = null;
    private Childbeans data;
    private RelativeLayout rel_up_img;
    //ImageView imgback;
    private int index;
    private String photoPathParam;
    private RelativeLayout rel;
    private EditText txtContactName;
    private View lytContactName;
    private View lytContactNameSpace;
    private EditText txtContactMobile;
    private View lytContactMobile;
    private CheckBox chkBoth;
    private View lytBothSpace;
    private CheckBox chkContact;
    private TextView txt_bloc_2, txt_bloc_1;
    private MainProgress pDialog;
    private String school_id, _teacher_id, Response;
    private Dialog dlgConfirm;
    private LinearLayout ly_both;
    private View view_othercontact, view_both, view_parent3;
    private static File dirf;
    private static String SDCARD_PERMISSIONS[] = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    private LayoutInflater inflater;
    private RelativeLayout screenview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //  setContentView(R.layout.dlg_studentsetting_details);
        inflater = getLayoutInflater();
        screenview = (RelativeLayout) inflater.inflate(R.layout.adres_dlg_studentsetting_details, null);
        relwrapp.addView(screenview);

        mActivity = this;

        isChanged = false;

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        imgProfile = (RoundedImageView) findViewById(R.id.img_student_pic);
        txtClass = (TextView) findViewById(R.id.txtClass);

        txtParent1Name = (EditText) findViewById(R.id.txtParent1Name);
        lytParent1name = (View) findViewById(R.id.lytParent1name);
        txtParent1Mobile = (EditText) findViewById(R.id.txtParent1Mobile);
        lytParent1mobile = (View) findViewById(R.id.lytParent1mobile);
        txtParent2Name = (EditText) findViewById(R.id.txtParent2Name);
        lytParent2name = (View) findViewById(R.id.lytParent2name);
        txtParent2Mobile = (EditText) findViewById(R.id.txtParent2Mobile);
        lytParent2mobile = (View) findViewById(R.id.lytParent2mobile);
        rel_up_img = (RelativeLayout) findViewById(R.id.rel_up_img);
        rel = (RelativeLayout) findViewById(R.id.rel);
        btnApprove = (TextView) findViewById(R.id.btnApprove);
        btnReject = (TextView) findViewById(R.id.btnReject);
        //  txt_title = (TextView) findViewById(R.id.textView1);
        //   imgback = (ImageView) findViewById(R.id.imgback);
        btnClose = (TextView) findViewById(R.id.btnClose);
        txtContactName = (EditText) findViewById(R.id.txtContactName);
        lytContactName = (View) findViewById(R.id.lytContactName);
        lytContactNameSpace = (View) findViewById(R.id.lytContactNameSpace);
        txtContactMobile = (EditText) findViewById(R.id.txtContactMobile);
        lytContactMobile = (View) findViewById(R.id.lytContactMobile);
        chkBoth = (CheckBox) findViewById(R.id.chkBoth);
        lytBothSpace = (View) findViewById(R.id.lytBothSpace);
        chkContact = (CheckBox) findViewById(R.id.chkContact);
        txt_bloc_2 = (TextView) findViewById(R.id.txt_bloc_2);
        txt_bloc_1 = (TextView) findViewById(R.id.txt_bloc_1);
        ly_both = (LinearLayout) findViewById(R.id.ly_both);
        view_othercontact = (View) findViewById(R.id.view_othercontact);
        view_both = (View) findViewById(R.id.view_both);
        view_parent3 = (View) findViewById(R.id.view_parent3);

            /*imgProfile.setBorderWidth(5);
            imgProfile.setBorderColor(R.color.color_blue);*/

        // txt_title.setText(getString(R.string.student_set));

        //set Title
        showheadermenu(StudentApproveRejectActivity.this, getString(R.string.student_set), R.color.white_light, false);

        SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);

        school_id = sharedpref.getString("school_id", "");
        _teacher_id = sharedpref.getString("teacher_id", "");
        if (getIntent().hasExtra("data")) {
            data = (Childbeans) getIntent().getSerializableExtra("data");
            if (data != null && data.image != null && data.image.length() > 0)
                ApplicationData.setProfileRoundedwithoutdefault(imgProfile, ApplicationData.web_server_url + "uploads/" + data.image, mActivity);
        }
        if (getIntent().hasExtra("index"))
            index = getIntent().getIntExtra("index", 0);

        txtTitle.setText((data.child_name.equals("null") || data.child_name.equals("")) ? "" : data.child_name);
        txtClass.setText(data.class_name.equals("null") ? "" : data.class_name);

        if (data.nc_parent_name != null && data.nc_parent_name.length() > 0 && !data.nc_parent_name.equalsIgnoreCase("null")) {
            txtParent1Name.setText(data.nc_parent_name);
            if (data.nc_mobile != null && data.nc_mobile.length() > 0)
                txtParent1Mobile.setText(data.nc_mobile);
        } else {
            txtParent1Name.setText(data.parent_name.equals("null") ? "" : data.parent_name);
            txtParent1Mobile.setText(data.mobile1.equals("null") ? "" : data.mobile1);
        }


        if (data.nc_parent_name2 != null && data.nc_parent_name2.length() > 0 && !data.nc_parent_name2.equalsIgnoreCase("null")) {
            txtParent2Name.setText(data.nc_parent_name2);
            if (data.nc_mobile2 != null && data.nc_mobile2.length() > 0)
                txtParent2Mobile.setText(data.nc_mobile2);
        } else {
            if (data.nc_parent_name == null || data.nc_parent_name.length() == 0 || data.nc_parent_name.isEmpty() ||
                    data.nc_parent_name.equalsIgnoreCase("null")) {
                txtParent2Name.setText(data.parent2_name.equals("null") ? "" : data.parent2_name);
                txtParent2Mobile.setText(data.mobile2.equals("null") ? "" : data.mobile2);
            }
        }

        if (txtParent1Name.getText().toString() != null && txtParent1Name.getText().toString().length() > 0
                && !txtParent1Name.getText().toString().equalsIgnoreCase("null")) {
            txt_bloc_1.setVisibility(View.VISIBLE);
            txt_bloc_1.setText(data.status1.equals("0") ? getString(R.string.unblock_user) : getString(R.string.block_user));
            if (data.status1.equalsIgnoreCase("0"))
                txt_bloc_1.setTextColor(getResources().getColor(R.color.green_approve));
            else
                txt_bloc_1.setTextColor(getResources().getColor(R.color.orange_pending));
        } else
            txt_bloc_1.setVisibility(View.GONE);


        if (txtParent2Name.getText().toString() != null && txtParent2Name.getText().toString().length() > 0
                && !txtParent2Name.getText().toString().equalsIgnoreCase("null")) {
            txt_bloc_2.setVisibility(View.VISIBLE);
            txt_bloc_2.setText(data.status2.equals("0") ? getString(R.string.unblock_user) : getString(R.string.block_user));
            if (data.status2.equalsIgnoreCase("0"))
                txt_bloc_2.setTextColor(getResources().getColor(R.color.green_approve));
            else
                txt_bloc_2.setTextColor(getResources().getColor(R.color.orange_pending));
        } else {
            txt_bloc_2.setVisibility(View.GONE);
        }


        if (data.mobile2 == null || data.mobile2.isEmpty() || data.mobile2.equals("null")) {
            txt_bloc_2.setVisibility(View.GONE);
        }
        if ((data.nc_parent_name != null && !data.nc_parent_name.equals("null") && data.nc_parent_name.length() > 0) ||
                data.nc_mobile2 != null && data.nc_mobile2.length() > 0 && !data.nc_mobile2.equalsIgnoreCase("null")) {

            btnClose.setVisibility(View.GONE);
            lytParent1name.setVisibility(View.VISIBLE);
            lytParent1mobile.setVisibility(View.VISIBLE);
            lytParent2name.setVisibility(View.VISIBLE);
            lytParent2mobile.setVisibility(View.VISIBLE);
            ly_both.setVisibility(View.GONE);
            lytBothSpace.setVisibility(View.GONE);
            chkBoth.setVisibility(View.GONE);
            chkContact.setVisibility(View.GONE);
            txt_bloc_1.setVisibility(View.GONE);
            txt_bloc_2.setVisibility(View.GONE);
            btnApprove.setVisibility(View.VISIBLE);
            btnReject.setVisibility(View.VISIBLE);
            lytContactMobile.setVisibility(View.GONE);
            lytBothSpace.setVisibility(View.GONE);
            lytContactName.setVisibility(View.GONE);
            view_both.setVisibility(View.GONE);
            view_othercontact.setVisibility(View.GONE);
            view_parent3.setVisibility(View.GONE);

        } else if ((data.parent2_name != null && !data.parent2_name.equalsIgnoreCase("null") && data.parent2_name.length() > 0) ||
                data.parent_name != null && !data.parent_name.equalsIgnoreCase("null") && data.parent_name.length() > 0) {
            btnClose.setVisibility(View.VISIBLE);
            lytParent1name.setVisibility(View.VISIBLE);
            lytParent1mobile.setVisibility(View.VISIBLE);
            lytParent2name.setVisibility(View.VISIBLE);
            lytParent2mobile.setVisibility(View.VISIBLE);
            ly_both.setVisibility(View.VISIBLE);
            lytBothSpace.setVisibility(View.VISIBLE);
            chkBoth.setVisibility(View.VISIBLE);
            chkContact.setVisibility(View.VISIBLE);

            // if(data.parent_name != null && !data.parent_name.equals("null") && data.parent_name.length() > 0)
            txt_bloc_1.setVisibility(View.VISIBLE);
            //  if(data.parent2_name != null && !data.parent2_name.equals("null") && data.parent2_name.length() > 0)
            //txt_bloc_2.setVisibility(View.VISIBLE);

            btnApprove.setVisibility(View.GONE);
            btnReject.setVisibility(View.GONE);
            lytContactMobile.setVisibility(View.VISIBLE);
            lytBothSpace.setVisibility(View.VISIBLE);
            lytContactName.setVisibility(View.VISIBLE);
        } else if (data.mobile1 == null || data.mobile1.equals("null") || data.mobile1.length() == 0) {

            txt_bloc_1.setVisibility(View.GONE);
            //  txt_bloc_2.setVisibility(View.GONE);
            chkBoth.setVisibility(View.VISIBLE);
            chkContact.setVisibility(View.VISIBLE);
            ly_both.setVisibility(View.VISIBLE);
            lytBothSpace.setVisibility(View.VISIBLE);
            //   btnClose.setText(mActivity.getResources().getString(R.string.str_close));
            btnClose.setVisibility(View.GONE);
            btnApprove.setVisibility(View.GONE);
            btnReject.setVisibility(View.GONE);
            lytContactMobile.setVisibility(View.VISIBLE);
            lytContactName.setVisibility(View.VISIBLE);
            lytParent1name.setVisibility(View.VISIBLE);
            lytParent1mobile.setVisibility(View.VISIBLE);
            lytParent2name.setVisibility(View.VISIBLE);
            lytParent2mobile.setVisibility(View.VISIBLE);
        } else {
            txt_bloc_1.setVisibility(View.GONE);
            txt_bloc_2.setVisibility(View.GONE);
            chkBoth.setVisibility(View.GONE);
            lytBothSpace.setVisibility(View.GONE);
            chkContact.setVisibility(View.GONE);
            btnClose.setVisibility(View.GONE);
            btnApprove.setVisibility(View.VISIBLE);
            btnReject.setVisibility(View.VISIBLE);
            lytContactName.setVisibility(View.GONE);
            lytContactMobile.setVisibility(View.GONE);
            ly_both.setVisibility(View.GONE);
            lytBothSpace.setVisibility(View.GONE);
            view_both.setVisibility(View.GONE);
            view_othercontact.setVisibility(View.GONE);
            view_parent3.setVisibility(View.GONE);
        }


        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (AppUtils.hasSelfPermission(mActivity, SDCARD_PERMISSIONS)) {
                        selectImage();
                    } else {
                        ApplicationData.isphoto = true;
                        requestPermissions(SDCARD_PERMISSIONS, REQUEST_CONTACTS_CODE);
                    }
                } else {
                    selectImage();
                }
            }
        });

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentApproveRejectActivity.this, FragmentStudentSetting.class);
                setResult(2, i);
                finish();
            }
        });

        txtContactName.setText(data.parent3_name.equals("null") ? "" : data.parent3_name);
        txtContactMobile.setText(data.mobile3.equals("null") ? "" : data.mobile3);

        txtContactName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChanged = true;
            }
        });

        txtContactName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isChanged = true;
            }
        });

        txtContactMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isChanged = true;
            }
        });

        txtContactMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChanged = true;
            }
        });

        if (data.status1.equals("1") && data.status2.equals("1")) {
            chkBoth.setChecked(true);
        } else {
            chkBoth.setChecked(false);
        }
//		if ( (allStudentList.get(position).mobile1==null || allStudentList.get(position).mobile1.isEmpty() || allStudentList.get(position).mobile1.equals("null")) ||
//				(allStudentList.get(position).mobile2==null || allStudentList.get(position).mobile2.isEmpty() || allStudentList.get(position).mobile2.equals("null")) ) {
//			chkBoth.setVisibility(View.GONE);
//		} else {
//			chkBoth.setVisibility(View.VISIBLE);
//		}

        if (data.status3.equals("1")) {
            chkContact.setChecked(true);
        } else {
            chkContact.setChecked(false);
        }


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    /*if (btnClose.getText().toString().equalsIgnoreCase(getString(R.string.str_save))) {
                        if (photoPath != null && !photoPath.equalsIgnoreCase("") && !photoPath.equalsIgnoreCase("null") && photoPath.length() > 0) {
                            new UploadImage().execute();
                        }
                    }*/
                    setChecked(0);
//					initPosition = _lstStudent.getFirstVisiblePosition();
//					loadStudentList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentApproveRejectActivity.this, FragmentStudentSetting.class);
                i.putExtra("falg_approve_reject", true);
                i.putExtra("index", index);
                i.putExtra("parent_id", data.nc_parent_id);
                i.putExtra("user_id", data.user_id);
                i.putExtra("image", photoPath);
                setResult(1, i);
                finish();
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationData.calldialog(StudentApproveRejectActivity.this, "", getString(R.string.str_request_reject),
                        getString(R.string.str_yes), getString(R.string.str_no), new ApplicationData.DialogListener() {
                            @Override
                            public void diaBtnClick(int diaID, int btnIndex) {
                                if (btnIndex == 2) {
                                    Intent i = new Intent(StudentApproveRejectActivity.this, FragmentStudentSetting.class);
                                    i.putExtra("falg_approve_reject", false);
                                    i.putExtra("index", index);
                                    i.putExtra("parent_id", data.nc_parent_id);
                                    i.putExtra("user_id", data.user_id);
                                    setResult(1, i);
                                    finish();
                                }
                            }
                        }, 1);

            }
        });

        txt_bloc_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txt_bloc_1.getText().toString().equals(getString(R.string.unblock_user))) {
                    String message = mActivity.getResources().getString(R.string.msg_confirm_block);
                    showConfirmBlock(mActivity, mActivity.getResources().getString(R.string.str_alert), message, 1);

                    if (!txt_bloc_2.getText().toString().equalsIgnoreCase(getString(R.string.block_user)))
                        chkBoth.setChecked(false);

                } else {
                    txt_bloc_1.setText(getString(R.string.block_user));
                    txt_bloc_1.setTextColor(getResources().getColor(R.color.orange_pending));
                }
                isChanged = true;
                //	setChecked(2);
            }
        });

        txt_bloc_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txt_bloc_2.getText().toString().equals(getString(R.string.unblock_user))) {
                    String message = mActivity.getResources().getString(R.string.msg_confirm_block);
                    showConfirmBlock(mActivity, mActivity.getResources().getString(R.string.str_alert), message, 2);

                    if (!txt_bloc_1.getText().toString().equalsIgnoreCase(getString(R.string.block_user)))
                        chkBoth.setChecked(false);
                }
                //	setChecked(2);
                else {
                    txt_bloc_2.setText(getString(R.string.block_user));
                    txt_bloc_2.setTextColor(getResources().getColor(R.color.orange_pending));
                }
                isChanged = true;
            }
        });

        chkBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((txt_bloc_1.getText().toString().equalsIgnoreCase(getString(R.string.unblock_user)) ||
                        txt_bloc_2.getText().toString().equalsIgnoreCase(getString(R.string.unblock_user)))
                        && chkBoth.isChecked()) {
                    chkBoth.setChecked(false);
                    try {
                        ApplicationData.showMessage(mActivity, getString(R.string.app_name), mActivity.getResources().getString(R.string.msg_blocked_parent),
                                getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }

                if (chkBoth.isChecked() && chkContact.isChecked()) {
                    try {
                        ApplicationData.showMessage(mActivity, getString(R.string.app_name), mActivity.getResources().getString(R.string.msg_checked_both_and_contact), getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    chkBoth.setChecked(false);
                    return;
                }
                if (chkBoth.isChecked()) {
                    ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_checked_both), false);
                    chkContact.setChecked(false);
                } else {
                    ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_unchecked_both), false);
                }
                isChanged = true;
//				setChecked(3);
            }
        });

        chkContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkBoth.isChecked() && chkContact.isChecked()) {
                    try {
                        ApplicationData.showMessage(mActivity, getString(R.string.app_name), mActivity.getResources().getString(R.string.msg_checked_both_and_contact), getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    chkContact.setChecked(false);
                    return;
                }

                if (data.mobile1 == null || data.mobile1.equals("null")) {
                    ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_no_parent), false);
                    chkContact.setChecked(false);
                    return;
                }
                if (txtContactName.getText().length() < 1 && chkContact.isChecked()) {
                    try {
                        ApplicationData.showMessage(mActivity, getString(R.string.app_name), mActivity.getResources().getString(R.string.msg_no_contactname), getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    chkContact.setChecked(false);
                    txtContactName.setFocusable(true);
                    return;
                } else if (txtContactMobile.getText().length() < 1 && chkContact.isChecked()) {
                    try {
                        ApplicationData.showMessage(mActivity, getString(R.string.app_name), mActivity.getResources().getString(R.string.msg_no_contactmobile), getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    chkContact.setChecked(false);
                    txtContactMobile.setFocusable(true);
                    return;
                } else if (!ApplicationData.isValidPhone(txtContactMobile.getText().toString()) && chkContact.isChecked()) {
                    try {
                        ApplicationData.showMessage(mActivity, getString(R.string.app_name),
                                mActivity.getResources().getString(R.string.msg_invalid_contactmobile), getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    chkContact.setChecked(false);
                    txtContactMobile.setFocusable(true);
                    return;
                }

                if (chkContact.isChecked()) {
                    ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_checked_contact), false);
                    chkBoth.setChecked(false);
                } else {
                    ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_unchecked_contact), false);
                }
                isChanged = true;
//				setChecked(4);
            }
        });
    }


    private void setChecked(int i) {
        {
            if (!isChanged)
                return;

            String checked1 = "", checked2 = "", checkedBoth = "", checkedContact = "";
            if (txt_bloc_1.getText().toString().equalsIgnoreCase(getString(R.string.unblock_user))) {
                checked1 = "Y";
            } else {
                checked1 = "";
            }
            if (txt_bloc_2.getText().toString().equalsIgnoreCase(getString(R.string.unblock_user))) {
                checked2 = "Y";
            } else {
                checked2 = "";
            }
            if (chkBoth.isChecked()) {
                checkedBoth = "Y";
            } else {
                checkedBoth = "";
            }
            if (chkContact.isChecked()) {
                checkedContact = "Y";
                if (txtContactName.getText().length() < 1 && chkContact.isChecked()) {
                    try {
                        ApplicationData.showMessage(mActivity, getString(R.string.app_name),
                                mActivity.getResources().getString(R.string.msg_no_contactname), getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    txtContactName.setFocusable(true);
                    return;
                } else if (txtContactMobile.getText().length() < 1 && chkContact.isChecked()) {
                    try {
                        ApplicationData.showMessage(mActivity, getString(R.string.app_name),
                                mActivity.getResources().getString(R.string.msg_no_contactmobile), getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    txtContactMobile.setFocusable(true);
                    return;
                } else if (!ApplicationData.isValidPhone(txtContactMobile.getText().toString()) && chkContact.isChecked()) {
                    try {
                        ApplicationData.showMessage(mActivity, getString(R.string.app_name),
                                mActivity.getResources().getString(R.string.msg_invalid_contactmobile), getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    txtContactMobile.setFocusable(true);
                    return;
                }
            } else {
                checkedContact = "";

                if (txtContactName.getText().toString() != null && txtContactName.getText().toString().length() > 0) {
                    if (txtContactMobile.getText().toString().length() == 0) {
                        try {
                            ApplicationData.showMessage(mActivity, getString(R.string.app_name),
                                    mActivity.getResources().getString(R.string.msg_no_contactmobile), getString(R.string.str_ok));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return;
                    }
                } else if (txtContactMobile.getText().toString() != null && txtContactMobile.getText().toString().length() > 0) {
                    if (txtContactName.getText().toString().length() == 0) {
                        try {
                            ApplicationData.showMessage(mActivity, getString(R.string.app_name),
                                    mActivity.getResources().getString(R.string.msg_no_contactname), getString(R.string.str_ok));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
            }


            if (!GlobalConstrants.isWifiConnected(mActivity)) {
//			dlg.dismiss();
//			showStudentDetailsDlg(index);
                return;
            }

            if (pDialog == null)
                pDialog = new MainProgress(mActivity);
            pDialog.setCancelable(false);
            pDialog.setMessage(getResources().getString(R.string.str_wait));
            pDialog.show();
//child_id, teacher_id, checked1, checked2, bothchecked, contactchecked, contact_name, contact_mobile
            String url = ApplicationData.getlanguageAndApi(StudentApproveRejectActivity.this, ConstantApi.SET_SETTING_CHECKED)
                    + "teacher_id=" + _teacher_id
                    + "&child_id=" + data.user_id
                    + "&checked1=" + checked1
                    + "&checked2=" + checked2
                    + "&bothchecked=" + checkedBoth
                    + "&contactchecked=" + checkedContact
                    + "&contact_name=" + txtContactName.getText()
                    + "&contact_mobile=" + txtContactMobile.getText();

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
                                    ApplicationData.showMessage(mActivity, getString(R.string.app_name),
                                            mActivity.getResources().getString(R.string.msg_success_change_studentsetting),
                                            getString(R.string.str_ok));
                                } else {
                                    String errcode = response.getString("errcode");
                                    if (errcode.equals("2")) {
                                        ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_no_parent), true);
                                    } else if (errcode.equals("3")) {
                                        ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_duplicated_contact_mobile), true);
                                    } else {
                                        ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_operation_error), true);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_operation_error), true);
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    pDialog.dismiss();
                    ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_operation_error), true);
                }
            });

            queue.add(jsObjRequest);
        }
    }


    private void selectImage() {

        final Dialog dlg = new Dialog(mActivity);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.photo_picker_dlg);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        RelativeLayout lyt_album = (RelativeLayout) dlg.findViewById(R.id.lyt_album);
        RelativeLayout lyt_camera = (RelativeLayout) dlg.findViewById(R.id.lyt_camera);
        RelativeLayout lyt_remove = (RelativeLayout) dlg.findViewById(R.id.lyt_remove);
        RelativeLayout lyt_cancel = (RelativeLayout) dlg.findViewById(R.id.lyt_cancel);

        lyt_album.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ApplicationData.isphoto = true;

                Intent intent = new Intent(Intent.ACTION_PICK);
                dirf = new File(GlobalConstrants.LOCAL_PATH);
                if (!dirf.exists())
                    dirf.mkdirs();
                if (dirf.exists()) {
                    intent.setType("image/*");
                    startActivityForResult(intent, GET_GALLERY);
                }
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
                ApplicationData.isphoto = true;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                dirf = new File(GlobalConstrants.LOCAL_PATH);
                if (!dirf.exists())
                    dirf.mkdirs();
                if (dirf.exists()) {
                    File f = new File(GlobalConstrants.LOCAL_PATH, "student_img.png");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, GET_CAMERA);
                } else {
                    Log.d("FramProfile", "Directory Not exists");
                }

            }
        });

        lyt_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageclick = 0;
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                photoPath = "";
                photoPathParam = "";
            }
        });

        lyt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dlg.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GET_CAMERA) {
                if (resultCode == RESULT_OK) {
                    //   File f = new File(GlobalConstrants.LOCAL_PATH);
                    if (dirf != null && dirf.exists()) {
                        for (File temp : dirf.listFiles()) {
                            if (temp != null && temp.getName() != null && temp.getName().equals("student_img.png")) {
                                dirf = temp;
                                break;
                            }
                        }

                        if (dirf.isFile()) {
                            photoPath = dirf.getAbsolutePath();
                            String externalStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                            if (photoPath.startsWith(externalStorageDir)) {
                                photoPathParam = "file:///mnt/sdcard/" + photoPath.substring(externalStorageDir.length());
                            } else
                                photoPathParam = photoPath;

                            Intent intent = new Intent(mActivity, PhotoCropActivity.class);
                            intent.putExtra("photoPath", photoPathParam);
                            startActivityForResult(intent, REQUEST_CODE_PHOTO_CROP);
                            //   performCrop(photoPath);
                        }
                    }
                }
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


                    String externalStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();

                    if (photoPath.startsWith(externalStorageDir)) {
                        File fileDest = new File(GlobalConstrants.LOCAL_PATH + "/student_img.png");

                        File fileSrc = new File(photoPath);

                        try {
                            FileUtils.copyFile(fileSrc, fileDest);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        photoPath = fileDest.getAbsolutePath();
                        photoPathParam = "file:///mnt/sdcard/" + photoPath.substring(externalStorageDir.length());
                        //photoPathParam=photoPath+photoPath.substring(externalStorageDir.length());
                    } else
                        photoPathParam = photoPath;

                    Intent intent = new Intent(mActivity, PhotoCropActivity.class);
                    intent.putExtra("photoPath", photoPathParam);
                    startActivityForResult(intent, REQUEST_CODE_PHOTO_CROP);
                }

            } else if (requestCode == REQUEST_CODE_PHOTO_CROP) {
                if (resultCode == RESULT_OK) {

                  /*  Bundle extras = data.getExtras();
                    // get the cropped bitmap
                    Bitmap thePic = extras.getParcelable("data");
                    profile_pic.setImageBitmap(thePic);*/

                    imgProfile.setImageDrawable(null);
                    photoPath = data.getStringExtra("croppedPhotoPath");

                    if (sbmp != null && !sbmp.isRecycled()) {
                        sbmp.recycle();
                        sbmp = null;
                    }

                    if (photoPath == null) {
                        ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_no_saved_profile_img), true);
                        return;
                    }

                    sbmp = BitmapUtil.getBitmapFromFile(photoPath);
                    if (sbmp != null) {
                        imageclick = 1;
                        imgProfile.setImageBitmap(sbmp);

                        new UploadImage().execute();
                    }
                }
                ApplicationData.isphoto = false;
            }
        } else {
            ApplicationData.isphoto = false;
        }

    }


    public void showConfirmBlock(final Context context, final String title, final String message,
                                 final int checkBox) {
        final Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                /*if ((checkBox == 1 && !txt_bloc_1.getText().toString().equalsIgnoreCase(getString(R.string.unblock_user))) || (checkBox == 2 &&
                        !txt_bloc_2.getText().toString().equalsIgnoreCase(getString(R.string.unblock_user))) ||
                        (checkBox == 3 && !chkBoth.isChecked()) || (checkBox == 4 && !chkContact.isChecked())) {
                    doUnCheck(checkBox);
                    return;
                }*/
                try {
                    if (dlgConfirm != null && dlgConfirm.isShowing())
                        dlgConfirm.dismiss();
                    dlgConfirm = new Dialog(context);
                    dlgConfirm.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dlgConfirm.setContentView(R.layout.msgdialog);
                    dlgConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    TextView tv_title = (TextView) dlgConfirm.findViewById(R.id.msgtitle);
                    TextView tv_content = (TextView) dlgConfirm.findViewById(R.id.msgcontent);

                    tv_title.setText(title);
                    tv_content.setText(message);

                    Button dlg_btn_ok = (Button) dlgConfirm.findViewById(R.id.btn_ok);
                    dlg_btn_ok.setText(R.string.str_yes);
                    dlg_btn_ok.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                dlgConfirm.dismiss();
                                doCheck(checkBox);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    Button dlg_btn_cancel = (Button) dlgConfirm.findViewById(R.id.btn_cancel);
                    dlg_btn_cancel.setText(R.string.str_no);
                    dlg_btn_cancel.setVisibility(View.VISIBLE);
                    dlg_btn_cancel.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                dlgConfirm.dismiss();
                                if (checkBox == 1)
                                    txt_bloc_1.setText(getString(R.string.block_user));
                                else if (checkBox == 2)
                                    txt_bloc_2.setText(getString(R.string.block_user));
                                else if (checkBox == 3)
                                    chkBoth.setChecked(false);
                                else if (checkBox == 4)
                                    chkBoth.setChecked(false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    dlgConfirm.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doCheck(int checkBox) {
        if (checkBox == 1) {
            if (!txt_bloc_1.getText().toString().equalsIgnoreCase(getString(R.string.unblock_user)))
                chkBoth.setChecked(false);
            txt_bloc_1.setText(getString(R.string.unblock_user));
            txt_bloc_1.setTextColor(getResources().getColor(R.color.green_approve));
            isChanged = true;
        } else if (checkBox == 2) {
            if (!txt_bloc_2.getText().toString().equalsIgnoreCase(getString(R.string.unblock_user)))
                chkBoth.setChecked(false);
            // ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_parent_blocked, "2"), false);
            txt_bloc_2.setText(getString(R.string.unblock_user));
            txt_bloc_2.setTextColor(getResources().getColor(R.color.green_approve));
            isChanged = true;
        } else if (checkBox == 3) {
            if ((!txt_bloc_1.getText().toString().equalsIgnoreCase(getString(R.string.unblock_user)) ||
                    !txt_bloc_2.getText().toString().equalsIgnoreCase(getString(R.string.unblock_user)) &&
                            chkBoth.isChecked())) {
                chkBoth.setChecked(false);
                ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_blocked_parent), false);
                return;
            }
            isChanged = true;
        } else if (checkBox == 4) {
            if (txtContactName.getText().length() < 1 && chkContact.isChecked()) {
                ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_no_contactname), false);
                chkContact.setChecked(false);
                txtContactName.setFocusable(true);
                return;
            } else if (txtContactMobile.getText().length() < 1 && chkContact.isChecked()) {
                ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_no_contactmobile), false);
                chkContact.setChecked(false);
                txtContactMobile.setFocusable(true);
                return;
            } else if (!ApplicationData.isValidPhone(txtContactMobile.getText().toString()) && chkContact.isChecked()) {
                ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_invalid_contactmobile), false);
                chkContact.setChecked(false);
                txtContactMobile.setFocusable(true);
                return;
            }
            isChanged = true;
        }
    }

    private void doUnCheck(int checkBox) {
        if (checkBox == 1) {
            if (!txt_bloc_1.getText().toString().equalsIgnoreCase(getString(R.string.unblock_user)))
                chkBoth.setChecked(false);
            ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_parent_unblocked, "1"), false);
            isChanged = true;
        } else if (checkBox == 2) {
            if (!txt_bloc_2.getText().toString().equalsIgnoreCase(getString(R.string.unblock_user)))
                chkBoth.setChecked(false);

            ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_parent_unblocked, "2"), false);
            isChanged = true;
        } else if (checkBox == 3) {
            if ((!txt_bloc_1.getText().toString().equalsIgnoreCase(getString(R.string.unblock_user)) ||
                    !txt_bloc_2.getText().toString().equalsIgnoreCase(getString(R.string.unblock_user))) && chkBoth.isChecked()) {
                chkBoth.setChecked(false);
                ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_blocked_parent), true);
                return;
            }
            isChanged = true;
        } else if (checkBox == 4) {
            if (txtContactName.getText().length() < 1 && chkContact.isChecked()) {
                ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_no_contactname), true);
                chkContact.setChecked(false);
                txtContactName.setFocusable(true);
                return;
            } else if (txtContactMobile.getText().length() < 1 && chkContact.isChecked()) {
                ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_no_contactmobile), true);
                chkContact.setChecked(false);
                txtContactMobile.setFocusable(true);
                return;
            } else if (!ApplicationData.isValidPhone(txtContactMobile.getText().toString()) && chkContact.isChecked()) {
                ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.msg_invalid_contactmobile), true);
                chkContact.setChecked(false);
                txtContactMobile.setFocusable(true);
                return;
            }
            isChanged = true;
        }
    }

    public class UploadImage extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            if (pDialog == null)
                pDialog = new MainProgress(StudentApproveRejectActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getResources().getString(R.string.str_wait));
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                String val = "no";
                if (imageclick == 0)
                    val = "yes";

                String url = ApplicationData.getlanguageAndApi(StudentApproveRejectActivity.this, ConstantApi.UPLOAD_PROFILE_TEACHER)
                        + "user_id=" + data.user_id + "&removeimage=" + val;

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
                    // String msg = response.getString("msg");

                    if (Integer.parseInt(flag) == 1) {
                        imageclick = 0;
                        String image = response.getString("filename");
//                        Childbeans childbeans = allStudentList.get(index);
//                        childbeans.image = image;
//                        allStudentList.set(index, childbeans);
                        ApplicationData.showToast(mActivity, getString(R.string.upload_image_save), false);
                    } else {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.error_image), false);
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent();
        setResult(2, i);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CONTACTS_CODE) {

            if (AppUtils.verifyAllPermissions(grantResults)) {
                ApplicationData.isphoto = false;
            } else {
                Toast.makeText(StudentApproveRejectActivity.this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mActivity = null;
        txtTitle = null;
        txtClass = null;
        txtParent1Name = null;
        txtParent1Mobile = null;
        txtParent2Name = null;
        txtParent2Mobile = null;

        lytParent1name = null;
        lytParent1mobile = null;
        lytParent2name = null;
        lytParent2mobile = null;
        btnClose = null;
        btnApprove = null;
        btnReject = null;
        imgProfile = null;
        photoPath = null;
        allStudentList = null;
        allArrayList = null;
        arrayGrade = null;
        arrayClass = null;
        sbmp = null;
        data = null;
        rel_up_img = null;
        photoPathParam = null;
        rel = null;
        txtContactName = null;
        lytContactName = null;
        lytContactNameSpace = null;
        txtContactMobile = null;
        lytContactMobile = null;
        chkBoth = null;
        lytBothSpace = null;
        chkContact = null;
        txt_bloc_2 = null;
        txt_bloc_1 = null;
        pDialog = null;
        school_id = null;
        _teacher_id = null;
        Response = null;
        dlgConfirm = null;
        ly_both = null;
        view_othercontact = null;
        view_both = null;
        view_parent3 = null;
        inflater = null;
        screenview = null;

        System.gc();
    }
}
