package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.adapter.teacher.Childbeans;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudstream.cslink.R;
import com.common.dialog.MainProgress;
import com.common.utils.AppUtils;
import com.common.utils.BitmapUtil;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.common.view.CircularImageView;
import com.common.view.RoundedImageView;
import com.itextpdf.text.pdf.parser.Line;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FramentProfile extends Fragment {

    public final static int GET_CAMERA = 1009;
    public final static int GET_GALLERY = 1010;
    private static final int RESULT_OK = -1;

    private CircularImageView imgPlus;
    private RoundedImageView profile_pic;

    MainProgress pDialog;
    private View txtUpdate, lytEdit;

    private String teacher_id, teacher_name, mobile, email, image;
    private TextView txtSchool, txtClass, txtsub;
    private EditText txtName, txtMobile, txtEmail;
    boolean editable = false;

    String Response;
    String language;

    int imageclick = 2;
    Integer angle = 0;
    private String photoPath;
    String path = GlobalConstrants.path;
    private Bitmap bitmap;
    Bitmap sbmp = null;
    private int REQUEST_CODE_PHOTO_CROP = 1008;
    private final static int REQUEST_CONTACTS_CODE = 100;
    private LinearLayout lin_update;
    private Childbeans data;
    private LinearLayout ly_cancel;
    private static File filepath;
    private static String SDCARD_PERMISSIONS[] = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    //private Bitmap croppedPhotoBmp;

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
        View rootView = inflater.inflate(R.layout.adres_profile, container, false);

        SharedPreferences sharedpref = getActivity().getSharedPreferences(Constant.USER_FILENAME, 0);
        teacher_id = sharedpref.getString("teacher_id", "");

		/*lytEdit = (View) rootView.findViewById(R.id.lytEdit);
        lytEdit.setVisibility(View.VISIBLE);
		lytEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});*/

        txtUpdate = (View) rootView.findViewById(R.id.txtUpdate);
        imgPlus = (CircularImageView) rootView.findViewById(R.id.imgPlus);
        profile_pic = (RoundedImageView) rootView.findViewById(R.id.imgProfile);
        lin_update = (LinearLayout) rootView.findViewById(R.id.lin_update);
        txtName = (EditText) rootView.findViewById(R.id.txtName);
        txtEmail = (EditText) rootView.findViewById(R.id.txtEmail);
        txtMobile = (EditText) rootView.findViewById(R.id.txtMobile);
        txtClass = (TextView) rootView.findViewById(R.id.txtClass);
        ly_cancel = (LinearLayout) rootView.findViewById(R.id.ly_cancel);
        txtsub = (TextView) rootView.findViewById(R.id.txtsub);

        ((MainActivity) getActivity()).btnUpdateProfile.setEnabled(true);

        lin_update.setVisibility(View.GONE);

        if (data != null) {
            if (data.teacher_id != null && data.teacher_id.length() > 0)
                teacher_id = data.teacher_id;
            if (data.name != null && data.name.length() > 0 && !data.name.equalsIgnoreCase("null"))
                txtName.setText(data.name);
            if (data.emailaddress != null && data.emailaddress.length() > 0 && !data.emailaddress.equalsIgnoreCase("null"))
                txtEmail.setText(data.emailaddress);
            if (data.mobile1 != null && data.mobile1.length() > 0 && !data.mobile1.equalsIgnoreCase("null"))
                txtMobile.setText(data.mobile1);
            if (data.class_name != null && data.class_name.length() > 0 && !data.class_name.equalsIgnoreCase("null"))
                txtClass.setText(data.class_name);
            else
                txtClass.setText("-");

            if (data.image != null && data.image.length() > 0 && !data.image.equalsIgnoreCase("null")) {
                image = data.image;
                loadimage(data.image);
            }
            if (data.subject_name != null && data.subject_name.length() > 0 && !data.subject_name.equals("null")) {
                txtsub.setVisibility(View.VISIBLE);
                txtsub.setText(data.subject_name);
            } else
                txtsub.setVisibility(View.GONE);
        }

        lin_update.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                try {

                    if (txtName.getText().length() == 0) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.adres_msg_input_name), false);
                        txtName.setFocusable(true);
                    } else if (txtEmail.getText().length() == 0) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_input_email), false);
                        txtEmail.setFocusable(true);
                    } else if (!ApplicationData.isValidEmail(txtEmail.getText().toString())) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_invalid_email), false);
                        txtEmail.setFocusable(true);
                    } else if (txtMobile.getText().length() > 0 && !ApplicationData.isValidPhone(txtMobile.getText())) {
                        ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_invalid_phone), false);
                        txtMobile.setFocusable(true);
                    } else {
                        teacher_name = URLEncoder.encode(txtName.getText().toString(), "UTF-8");
                        email = URLEncoder.encode(txtEmail.getText().toString(), "UTF-8");
                        mobile = URLEncoder.encode(txtMobile.getText().toString(), "UTF-8");

                        new register_user_runnner().execute();
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
        imgPlus.setVisibility(View.GONE);
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
        // .......TextView.........//


        String url = ApplicationData.getlanguageAndApi(getActivity(), ConstantApi.GET_PROFILE) + "teacher_id=" + teacher_id;

        //getTeacherDetails(url);

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
                    Log.d("FramProfile", "Directory Not exists");
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

                            Intent intent = new Intent(mActivity, PhotoCropActivity.class);
                            intent.putExtra("photoPath", photoPathParam);
                            startActivityForResult(intent, REQUEST_CODE_PHOTO_CROP);
                        }
                    }
                }
            } else if (requestCode == GET_GALLERY) {
                if (resultCode == RESULT_OK) {
                    Uri selectedPhoto = data.getData();
                    if (selectedPhoto == null)
                        return;

                    String[] filePath_gallery = {MediaStore.Images.Media.DATA};
                    Cursor c = null;
                    try {
                        c = mActivity.getContentResolver().query(selectedPhoto, filePath_gallery, null, null, null);
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

                    if (photoPath == null)
                        return;

                    String photoPathParam = null;
                    String externalStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                    if (photoPath.startsWith(externalStorageDir)) {
                        File fileDest = new File(GlobalConstrants.LOCAL_PATH + "/profile_img");

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
                    startActivityForResult(intent, REQUEST_CODE_PHOTO_CROP);
                }

            } else if (requestCode == REQUEST_CODE_PHOTO_CROP) {
                if (resultCode == RESULT_OK) {
                    if (data == null)
                        return;

                    profile_pic.setImageDrawable(null);

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
                        profile_pic.setImageBitmap(sbmp);
                    }
                }
                ApplicationData.isphoto = false;
            }

        } else {
            ApplicationData.isphoto = false;
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
        profile_pic.setImageBitmap(sbmp);

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

                String url = ApplicationData.getlanguageAndApi(getActivity(), ConstantApi.UPDATE_TEACHER_DETAIL) +
                        "userid="
                        + teacher_id
                        + "&name="
                        + teacher_name
                        + "&email="
                        + email
                        + "&mobile="
                        + mobile
                        + "&removeimage="
                        + val;

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost postRequest = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);

                if (imageclick == 0) {
                    photoPath = "";
                    SharedPreferences shf = getActivity().getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = shf.edit();
                    edit.putString("image", "");
                    edit.commit();

                } else if (imageclick == 1) {
                    //File file = new File(photoPath);
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
                        JSONObject teacher = response.getJSONObject("Teacher");

                        JSONArray teachers = teacher.getJSONArray("teachers");
                        JSONObject c = teachers.getJSONObject(0);
                        SharedPreferences myPrefs = mActivity.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = myPrefs.edit();
                        editor.putString("image", c.getString("image"));
                        editor.putString("email", txtEmail.getText().toString());
                        editor.putString("teacher_name", txtName.getText().toString());
                        editor.commit();

                        ApplicationData.showToast(mActivity, getResources().getString(R.string.success_update_profile), true);

                        MainActivity mainActivity = (MainActivity) ApplicationData.getMainActivity();

                        if (mainActivity != null && sbmp != null) {
                            mainActivity.getImageProfileView().setImageBitmap(sbmp);
                            sbmp = null;
                        }

                        updatedone();
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

    private void updatedone() {
        lin_update.setVisibility(View.GONE);
        imgPlus.setVisibility(View.GONE);
        ((MainActivity) getActivity()).btnUpdateProfile.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).btnUpdateProfile.setEnabled(true);
        editable = false;

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            txtName.setBackgroundDrawable(null);
            txtMobile.setBackgroundDrawable(null);
            //  txtEmail.setBackgroundDrawable(null);
        } else {
            txtName.setBackground(null);
            txtMobile.setBackground(null);
            //  txtEmail.setBackground(null);
        }
        txtName.setEnabled(false);
        txtMobile.setEnabled(false);
        //   txtEmail.setEnabled(false);
    }

    protected void loadimage(String path) {
        // TODO Auto-generated method stub
        if (path != null && path.length() > 0) {
            ApplicationData.setProfileRounded(profile_pic, ApplicationData.web_server_url + "uploads/" + path, mActivity);
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
    }

    public void editprofile() {
        editable = true;
        imgPlus.setVisibility(View.VISIBLE);
        lin_update.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).btnUpdateProfile.setVisibility(View.GONE);
        // lytEdit.setVisibility(View.GONE);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            txtName.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
            txtMobile.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
            //    txtEmail.setBackgroundDrawable(getResources().getDrawable(R.drawable.liner_white_border));
        } else {
            txtName.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
            txtMobile.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
            //    txtEmail.setBackground(getResources().getDrawable(R.drawable.liner_white_border));
        }
        txtName.setEnabled(true);
        txtMobile.setEnabled(true);
        // txtEmail.setEnabled(true);
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
