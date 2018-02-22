package com.cloudstream.cslink.parent;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.parent.Childbeans;
import com.common.dialog.MainProgress;
import com.common.utils.AppUtils;
import com.common.utils.BitmapUtil;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.common.view.CircularImageView;
import com.common.view.RoundedImageView;
import com.xmpp.parent.Constant;

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
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.cloudstream.cslink.R;

public class FramentProfile extends Fragment {

    public final static int GET_CAMERA = 1009;
    public final static int GET_GALLERY = 1010;
    private static final int RESULT_OK = -1;

    private RoundedImageView profile_pic;

    private String photoPath;

    private View update;
    private View lyt_class, lytContact;
    //private View edit_profile;

    MainProgress pDialog;
    private String login_url, child_id, schoolname, parentno;

    private TextView _childname, _schoolname, child_parent1, child_parent2, child_phone1,
            child_phone2, child_class, child_classincharge, child_mobile,
            lang_edit, lang_class, lang_classing, lang_parent1, lang_parent2,
            lang_phone1, lang_phone2, txtContactName, txtContactMobile, txtStatus1, txtStatus2, txtStatus3;

    private String image;

    boolean editable = false;

    //=================
    int imageclick = 0;
    Integer angle = 0;

    String Response;

    String path = Environment.getExternalStorageDirectory().toString() + File.separator + "absent"
            + File.separator + "yay.jpg";

    String extStorageDirectory = Environment.getExternalStorageDirectory()
            .toString();
    private Bitmap bitmap;
    String parent1name = "", parent1phone = "", parent2name = "", parent2phone = "", childname = "",
            userid, schoolid = "", mobile = "", child_array, phoneparent1 = "", phoneparent2 = "",
            child_school_id = "", language = "", parent3name = "", parent3phone = "";


    Bitmap sbmp = null;
    private int REQUEST_CODE_PHOTO_CROP = 1008;
    private final static int REQUEST_CONTACTS_CODE = 100;
    private static String photoPathParam = null;
    private LinearLayout lin_update;
    private RelativeLayout rel;
    private CircularImageView imgPlus;
    private Childbeans data;
    private LinearLayout ly_cancel;
    private SharedPreferences sharedpref;
    private static File dirf;

    private static String SDCARD_PERMISSIONS[] = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    public FramentProfile() {
    }

    Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //bundle data
        Bundle bn = getArguments();
        if (bn != null) {
            data = (Childbeans) bn.getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = getActivity();
        View rootView = inflater.inflate(R.layout.user_profile, container, false);

        sharedpref = getActivity().getSharedPreferences(Constant.USER_FILENAME, 0);
        child_id = sharedpref.getString("childid", "");
        parentno = sharedpref.getString("parent_no", "");

        imgPlus = (CircularImageView) rootView.findViewById(R.id.imgPlus);
        lin_update = (LinearLayout) rootView.findViewById(R.id.lin_update);
        imgPlus.setVisibility(View.GONE);
        profile_pic = (RoundedImageView) rootView.findViewById(R.id.img_profile);
        rel = (RelativeLayout) rootView.findViewById(R.id.rel);
        // .......TextView.........//

        _childname = (EditText) rootView.findViewById(R.id.textView_childname);
        _schoolname = (TextView) rootView.findViewById(R.id.textView_school);
        child_parent1 = (EditText) rootView.findViewById(R.id.editText_parent1);
        child_parent2 = (EditText) rootView.findViewById(R.id.editText_parent2);
        child_phone1 = (EditText) rootView.findViewById(R.id.editText_phone);
        child_phone1.setMovementMethod(new ScrollingMovementMethod());
        child_phone2 = (EditText) rootView.findViewById(R.id.editText_phone2);
        child_class = (EditText) rootView.findViewById(R.id.editText_class);
        lyt_class = (View) rootView.findViewById(R.id.lyt_class);
        child_classincharge = (EditText) rootView.findViewById(R.id.editText_classincharge);
        child_mobile = (EditText) rootView.findViewById(R.id.editText_mobile);
        txtContactName = (EditText) rootView.findViewById(R.id.txtContactName);
        txtContactMobile = (EditText) rootView.findViewById(R.id.txtContactMobile);
        lytContact = (View) rootView.findViewById(R.id.lytContact);
        txtStatus1 = (TextView) rootView.findViewById(R.id.txtStatus1);
        txtStatus2 = (TextView) rootView.findViewById(R.id.txtStatus2);
        txtStatus3 = (TextView) rootView.findViewById(R.id.txtStatus3);
        ly_cancel = (LinearLayout) rootView.findViewById(R.id.ly_cancel);
        // ........languagechnage............//
        language = sharedpref.getString("language", "");

        lin_update.setVisibility(View.GONE);

        lang_edit = (TextView) rootView.findViewById(R.id.textView2);
        lang_class = (TextView) rootView.findViewById(R.id.textView_class);
        lang_classing = (TextView) rootView.findViewById(R.id.textView_classincharge);
        lang_parent1 = (TextView) rootView.findViewById(R.id.textView_parent1);
        lang_parent2 = (TextView) rootView.findViewById(R.id.textView_parent2);
        lang_phone1 = (TextView) rootView.findViewById(R.id.textView_phone);
        lang_phone2 = (TextView) rootView.findViewById(R.id.textView_phone2);

        profile_pic.setImageResource(R.drawable.cslink_avatar_unknown);
        lang_edit.setText(getResources().getString(R.string.profile_edit));
        update = (View) rootView.findViewById(R.id.textView_update);
        /*edit_profile = (View) rootView.findViewById(R.id.edit_profile);
        edit_profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub


			}
		});*/
        ((MainActivity) getActivity()).btnUpdateProfile.setEnabled(true);
        ((MainActivity) getActivity()).title.setText(getString(R.string.str_profile));

        if (data != null) {

            if (data.username != null && data.username.length() > 0)
                _childname.setText(data.username);
            if (data.parent_name != null && data.parent_name.length() > 0)
                child_parent1.setText(data.parent_name);
            if (data.parent2_name != null && data.parent2_name.length() > 0)
                child_parent2.setText(data.parent2_name);
            if (data.parent1_phone != null && data.parent1_phone.length() > 0)
                child_phone1.setText(data.parent1_phone);
            if (data.parent2_phone != null && data.parent2_phone.length() > 0)
                child_phone2.setText(data.parent2_phone);
            if (data.class_name != null && data.class_name.length() > 0)
                child_class.setText(data.class_name);
            if (data.incharger != null && data.incharger.length() > 0)
                child_classincharge.setText(data.incharger);
            if (data.school_id != null && data.school_id.length() > 0)
                schoolid = data.school_id;

            if (data.school_name != null && data.school_name.length() > 0) {
                schoolname = data.school_name;
                _schoolname.setText(schoolname);
            }
            if (data.nc_mobile != null && data.nc_mobile.length() > 0) {
                mobile = data.nc_mobile;
                child_mobile.setText(data.nc_mobile);
            }

            /*if (data.contactmobile.isEmpty() || data.contactmobile.equals("null")) {
                lytContact.setVisibility(View.GONE);
            } else*/
            {
                lytContact.setVisibility(View.VISIBLE);
                if (data.contactname != null && data.contactname.length() > 0 && !data.contactname.equalsIgnoreCase("null"))
                    txtContactName.setText(data.contactname);
                if (data.contactmobile != null && data.contactmobile.length() > 0 && !data.contactmobile.equalsIgnoreCase("null"))
                    txtContactMobile.setText(data.contactmobile);
            }
            if (data.status1 != null && data.status1.length() > 0 && !data.status1.equalsIgnoreCase("null")) {
                txtStatus1.setVisibility(View.VISIBLE);
                setTextStatus(txtStatus1, data.status1);
            }
            if (data.status2 != null && data.status2.length() > 0 && !data.status2.equalsIgnoreCase("null")) {
                if (data.parent2_phone != null && data.parent2_phone.length() > 0) {
                    txtStatus2.setVisibility(View.VISIBLE);
                    setTextStatus(txtStatus2, data.status2);
                }
            }

            if (data.contactmobile == null || data.contactmobile.isEmpty() || data.contactmobile.equals("null")) {
                setTextStatus(txtStatus3, "2");
            } else {
                txtStatus3.setVisibility(View.VISIBLE);
                setTextStatus(txtStatus3, data.status3);
            }

            if (data.image != null && data.image.length() > 0) {
                //image = data.image;
                loadimage(data.image);
            }

        }
        lin_update.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                try {


                    if (_childname.getText().length() == 0) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_input_name), false);
                        _childname.setFocusable(true);
                    } else if (child_parent1.getText().length() == 0) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_input_parent1_name), false);
                        child_parent1.setFocusable(true);
                    } else if (child_phone1.getText().length() == 0) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_input_parent1_phone), false);
                        child_phone1.setFocusable(true);
                    } else if (child_parent2.getText().toString().length() != 0 && child_phone2.getText().toString().length() == 0) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_input_parent2_phone), false);
                        child_phone2.setFocusable(true);
                    } else if (child_phone2.getText().toString().length() != 0 && child_parent2.getText().toString().length() == 0) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_input_parent2_name), false);
                        child_parent2.setFocusable(true);
                    } else if (child_phone2.getText().toString().length() != 0 && child_phone2.getText().toString().length() < 8) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_invalidphone), false);
                        child_phone2.setFocusable(true);
                    }

                    /*else if (child_mobile.getText().length() == 0) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_input_mobile), false);
						child_mobile.setFocusable(true);
                    }*/
                    else if (txtContactName.getText().toString() != null && txtContactName.getText().toString().length() > 0
                            && txtContactMobile.getText().toString().length() == 0) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_input_mobile), false);
                        child_mobile.setFocusable(true);

                    } else if (txtContactMobile.getText().toString() != null && txtContactMobile.getText().toString().length() > 0 && txtContactName.getText().toString().length() == 0) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_input_parent3_name), false);
                        txtContactName.setFocusable(true);
                    } else if (txtContactMobile.getText().toString().length() != 0 && txtContactMobile.getText().toString().length() < 8) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_invalidphone), false);
                        txtContactMobile.setFocusable(true);
                    } else {
                        if (child_parent1.getText().toString().length() > 0)
                            parent1name = URLEncoder.encode(child_parent1.getText().toString(), "UTF-8");

                        if (child_phone1.getText().toString().length() > 0)
                            parent1phone = URLEncoder.encode(child_phone1.getText().toString(), "UTF-8");

                        if (child_parent2.getText().toString().length() > 0)
                            parent2name = URLEncoder.encode(child_parent2.getText().toString(), "UTF-8");

                        if (child_phone2.getText().toString().length() > 0)
                            parent2phone = URLEncoder.encode(child_phone2.getText().toString(), "UTF-8");

                        if (txtContactName.getText().toString().length() > 0)
                            parent3name = URLEncoder.encode(txtContactName.getText().toString(), "UTF-8");
                        if (txtContactMobile.getText().toString().length() > 0)
                            parent3phone = URLEncoder.encode(txtContactMobile.getText().toString(), "UTF-8");


                        childname = URLEncoder.encode(_childname.getText().toString(), "UTF-8");
                        schoolid = URLEncoder.encode(schoolid, "UTF-8");

						/*mobile = URLEncoder.encode(child_mobile.getText()
                                .toString(), "UTF-8");*/
                        userid = child_id;

						/*phoneparent1 = child_phone1.getText().toString();

						phoneparent2 = child_phone2.getText().toString();*/

                        if (ApplicationData.isValidPhone(parent1phone)) {

                            if (parent2phone.length() == 0 || ApplicationData.isValidPhone(parent2phone)) {
                               /* if (!ApplicationData.checkRight(mActivity)) {
                                    return;
                                }*/
                                new register_user_runnner().execute();
                            } else {
                                ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_invalidparent2phone), false);
                                child_phone2.setFocusable(true);
                            }
                        } else {
                            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_invalidparent1phone), false);
                            child_phone1.setFocusable(true);
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });


        ly_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedone();
            }
        });

     /*   profile_pic.setBorderWidth(5);
        profile_pic.setBorderColor(R.color.color_blue);*/
        profile_pic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (editable) {
                    if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (AppUtils.hasSelfPermission(mActivity, SDCARD_PERMISSIONS)) {
                            selectImage();
                        } else {
                            ApplicationData.isphoto = true;
                            requestPermissions(SDCARD_PERMISSIONS, REQUEST_CONTACTS_CODE);
                        }
                    }
                    else
                    {
                        selectImage();
                    }

                }
            }
        });

		/*lang_class.setText(getResources().getString(R.string.profile_hint_class));
        lang_classing.setText(getResources().getString(R.string.profile_hint_classincharge));
		lang_parent1.setText(getResources().getString(R.string.profile_hint_parent1));
		lang_parent2.setText(getResources().getString(R.string.profile_hint_parent2));
		lang_phone1.setText(getResources().getString(R.string.profile_hint_phone1));
		lang_phone2.setText(getResources().getString(R.string.profile_hint_phone2));*/


        //   int foo = Integer.parseInt(child_id);

        //    login_url = ApplicationData.main_url + "get_child_details_id_by_phone.php?userid=" + foo;

        //    loginapp(login_url);

        return rootView;
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
                dirf = new File(GlobalConstrants.LOCAL_PATH);
                if (!dirf.exists())
                    dirf.mkdirs();
                if (dirf.exists()) {
                    intent.setType("image/*");
                    startActivityForResult(intent, GET_GALLERY);
                }
                else {
                    Log.d("FramProfile", "Directory Not exists");
                }
            }
        });

        lyt_camera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                    ApplicationData.isphoto = true;
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    dirf = new File(GlobalConstrants.LOCAL_PATH);
                    if (!dirf.exists())
                        dirf.mkdirs();
                    if (dirf.exists()) {
                        File f = new File(GlobalConstrants.LOCAL_PATH, "profile_img.png");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(intent, GET_CAMERA);
                    } else {
                        Log.d("FramProfile", "Directory Not exists");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        lyt_remove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_pic.setImageResource(R.drawable.cslink_avatar_unknown);
                imageclick = 0;
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                photoPathParam = "";
                photoPath = "";
                data.image = "";
            }
        });

        lyt_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

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
        if (resultCode == RESULT_OK) {
            if (requestCode == GET_CAMERA) {
                if (resultCode == RESULT_OK) {
                    //  File f = new File(GlobalConstrants.LOCAL_PATH);
                    if (dirf != null && dirf.exists()) {
                        for (File temp : dirf.listFiles()) {
                            if (temp != null && temp.getName() != null && temp.getName().equals("profile_img.png")) {
                                dirf = temp;
                                break;
                            }
                        }

                        if (dirf.isFile()) {
                            photoPath = dirf.getAbsolutePath();

                            String photoPathParam = null;
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
                    File fileDest = new File(GlobalConstrants.LOCAL_PATH, "/profile_img");

                    if (photoPath.startsWith(externalStorageDir)) {

                        File fileSrc = new File(photoPath);

                        try {
                            FileUtils.copyFile(fileSrc, fileDest);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        photoPath = fileSrc.getAbsolutePath();
                        photoPathParam = "file:///mnt/sdcard" + photoPath.substring(externalStorageDir.length());
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

                    //    profile_pic.setImageDrawable(null);
                    photoPath = data.getStringExtra("croppedPhotoPath");

                    if (sbmp != null && !sbmp.isRecycled()) {
                        sbmp.recycle();
                        sbmp = null;
                    }


                    if (photoPath == null)
                        return;

                    sbmp = BitmapUtil.getBitmapFromFile(photoPath);
                    if (sbmp != null) {
                        imageclick = 1;
                        Log.d("ImagePath>>>", photoPath);
                        profile_pic.setImageBitmap(sbmp);

                    }

                    ApplicationData.isphoto = false;
                }
            }
            /*else
                ApplicationData.isphoto=false;*/
        } else {
            ApplicationData.isphoto = false;
        }
    }


    public class register_user_runnner extends AsyncTask<String, Void, Void> {

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
                String val = "no";
                if (imageclick == 0)
                    val = "yes";

                String url = ApplicationData.getlanguageAndApi(getActivity(), ConstantApi.UPDATE_CHILD_DETAIL_PHONE) +
                        "parent1name="
                        + parent1name
                        + "&parent1phone="
                        + parent1phone
                        + "&parent2name="
                        + parent2name
                        + "&parent2phone="
                        + parent2phone
                        + "&childname="
                        + childname
                        + "&userid="
                        + userid
                        + "&schoolid="
                        + schoolid
                        + "&parent3name="
                        + parent3name
                        + "&parent3phone="
                        + parent3phone
                        + "&removeimage="
                        + val;

						/*+ "&mobile="
                        + mobile*/

                Log.d("profile : ", url);

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost postRequest = new HttpPost(url);
                // ByteArrayBody bab = new ByteArrayBody(data, "forest.jpg");

                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                if (imageclick == 0) {
                    SharedPreferences shf = getActivity().getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = shf.edit();
                    edit.putString("image", "");
                    edit.commit();
                    //  Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.cslink_avatar_unknown);

                    // File file1 = new File("");
                    // FileBody bin1 = new FileBody(file1);
                    // reqEntity.addPart("image",bin1);
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
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pDialog.dismiss();
            if (Response != null) {

                try {
                    JSONObject response = new JSONObject(Response);

                    String flag = response.getString("flag");

                    if (Integer.parseInt(flag) == 1) {

                        if (child_phone1.getText().toString().length() == 0 && child_phone1.getText().toString().length() == 0) {
                            txtStatus1.setVisibility(View.GONE);
                        } else {
                            txtStatus1.setVisibility(View.VISIBLE);
                            setTextStatus(txtStatus1, data.status1);
                        }

                        if (child_parent2.getText().toString().length() == 0 && child_phone2.getText().toString().length() == 0) {
                            txtStatus2.setVisibility(View.GONE);
                        } else {
                            txtStatus2.setVisibility(View.VISIBLE);
                            setTextStatus(txtStatus2, data.status2);
                        }

                        if (txtContactMobile.getText().toString().length() == 0 && txtContactMobile.getText().toString().length() == 0) {
                            txtStatus3.setVisibility(View.GONE);
                            if (!data.status1.equalsIgnoreCase("1") && !data.status2.equalsIgnoreCase("1")) {
                                setTextStatus(txtStatus1, "1");
                                setTextStatus(txtStatus2, "1");
                            }
                        } else {
                            txtStatus3.setVisibility(View.VISIBLE);
                            setTextStatus(txtStatus3, data.status3);
                        }


                        imageclick = 0;
                        JSONObject All_childs = response.getJSONObject("All childs");
                        JSONObject selected_child = response.getJSONObject("Child");
                        JSONArray childs = All_childs.getJSONArray("allchilds");

                        child_array = childs.toString();
                        SharedPreferences myPrefs = mActivity.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = myPrefs.edit();
                        editor.putString("child_array", child_array);
                        editor.commit();

                        ApplicationData.showMessage(mActivity, "", getResources().getString(R.string.success_update_profile), getString(R.string.str_ok));
                        MainActivity mainActivity = (MainActivity) ApplicationData.getMainActivity();

                        if (mainActivity != null && sbmp != null) {
                            mainActivity.getImageProfileView().setImageBitmap(sbmp);
                            sbmp = null;
                        }

                        JSONArray child = selected_child.getJSONArray("childs");
                        if (child != null) {
                            JSONObject jbchild = child.getJSONObject(0);
                            SharedPreferences.Editor detailedit = myPrefs.edit();
                            if (jbchild.has("username")) {
                                detailedit.putString("childname", jbchild.getString("username"));
                            }
                            if (jbchild.has("image")) {
                                detailedit.putString("image", jbchild.getString("image"));
                            }
                            detailedit.commit();
                        }

                        updatedone();

                    } else {
                        if (response.has("msg")) {
                            String msg = response.getString("msg");
                            ApplicationData.showToast(mActivity, msg, false);
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_operation_error), false);
            }
        }
    }

    private void updatedone() {
        imgPlus.setVisibility(View.GONE);
        //update.setVisibility(View.GONE);
        lin_update.setVisibility(View.GONE);
        //	edit_profile.setVisibility(View.VISIBLE);
        lyt_class.setVisibility(View.VISIBLE);
        //child_mobile.setVisibility(View.GONE);
        ((MainActivity) getActivity()).btnUpdateProfile.setVisibility(View.VISIBLE);
        //((MainActivity)getActivity()).btnUpdateProfile.setEnabled(true);

        editable = false;

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            _childname.setBackgroundDrawable(null);
            child_parent1.setBackgroundDrawable(null);
            child_parent2.setBackgroundDrawable(null);
            child_phone1.setBackgroundDrawable(null);
            child_phone2.setBackgroundDrawable(null);
            child_mobile.setBackgroundDrawable(null);
            txtContactMobile.setBackgroundDrawable(null);
            txtContactName.setBackgroundDrawable(null);
        } else {
            _childname.setBackground(null);
            child_parent1.setBackground(null);
            child_parent2.setBackground(null);
            child_phone1.setBackground(null);
            child_phone2.setBackground(null);
            child_mobile.setBackground(null);
            txtContactMobile.setBackground(null);
            txtContactName.setBackground(null);
        }
        _childname.setEnabled(false);
        child_parent1.setEnabled(false);
        child_parent2.setEnabled(false);
        child_phone1.setEnabled(false);
        child_phone2.setEnabled(false);
        child_mobile.setEnabled(false);
        txtContactMobile.setEnabled(false);
        txtContactName.setEnabled(false);

    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private void setTextStatus(TextView txtStatus, String status) {
        if (status.equals("1")) {
            txtStatus.setText(mActivity.getResources().getString(R.string.str_actived));
        } else if (status.equals("2")) {
            txtStatus.setText(mActivity.getResources().getString(R.string.str_inactived));
        } else {
            txtStatus.setText(mActivity.getResources().getString(R.string.str_blocked));
        }
    }

    public void editprofile() {
        editable = true;

        imgPlus.setVisibility(View.VISIBLE);
        //       update.setVisibility(View.VISIBLE);
        lin_update.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).btnUpdateProfile.setVisibility(View.GONE);
//        edit_profile.setVisibility(View.GONE);
//				lyt_class.setVisibility(View.GONE);
        child_mobile.setVisibility(View.GONE);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            _childname.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
        }
        else {
            _childname.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
        }
        _childname.setEnabled(true);
      /*  if (parentno.equalsIgnoreCase("1")) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                child_parent1.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
                _childname.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
                child_parent2.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
                child_phone2.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
                child_mobile.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
                //txtContactName.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
                //txtContactMobile.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
            } else {
                child_parent1.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
                _childname.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
                child_parent2.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
                child_phone2.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
                child_mobile.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
               // txtContactName.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
               // txtContactMobile.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
            }
            child_parent1.setEnabled(true);
            child_parent2.setEnabled(true);
            child_phone2.setEnabled(true);
            child_mobile.setEnabled(true);
           // txtContactMobile.setEnabled(true);
           // txtContactName.setEnabled(true);
        } else if (parentno.equalsIgnoreCase("2")) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                _childname.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
                child_parent1.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
                child_phone1.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
                child_parent2.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
                child_mobile.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
               // txtContactName.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
               // txtContactMobile.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
            } else {
                _childname.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
                child_parent1.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
                child_phone1.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
                child_parent2.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
                child_mobile.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
               // txtContactName.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
              //  txtContactMobile.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
            }

            _childname.setEnabled(true);
            child_parent1.setEnabled(true);
            child_phone1.setEnabled(true);
            child_parent2.setEnabled(true);
            child_mobile.setEnabled(true);
           // txtContactMobile.setEnabled(true);
          //  txtContactName.setEnabled(true);
        } else {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                _childname.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
                child_parent1.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
                child_parent2.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
                child_phone1.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
                child_phone2.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
                child_mobile.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
           //     txtContactName.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
            } else {
                _childname.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
                child_parent1.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
                child_parent2.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
                child_phone1.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
                child_phone2.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
                child_mobile.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
           //     txtContactName.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
            }
            _childname.setEnabled(true);
            child_parent1.setEnabled(true);
            child_parent2.setEnabled(true);
            child_phone1.setEnabled(true);
            child_phone2.setEnabled(true);
            child_mobile.setEnabled(true);
         //   txtContactName.setEnabled(true);
        }*/

    }

    protected void loadimage(String string) {
        // TODO Auto-generated method stub
        if (string != null && string.length() > 0) {
            ApplicationData.setProfileRounded(mActivity, ApplicationData.web_server_url + "uploads/" + string, profile_pic);
        }
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CONTACTS_CODE){

            if (AppUtils.verifyAllPermissions(grantResults)) {
                ApplicationData.isphoto = false;
            } else {
                Toast.makeText(getActivity(), "Permission Not Granted", Toast.LENGTH_SHORT).show();
            }

        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


}

