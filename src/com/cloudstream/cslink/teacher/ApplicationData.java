package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.teacher.Childbeans;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cloudstream.cslink.R;
import com.common.Bean.MarkBean;
import com.common.FileDownloader;
import com.common.dialog.MainProgress;
import com.common.utils.ConstantApi;
import com.common.view.CircularImageView;
import com.common.view.RoundedImageView;
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
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xmpp.teacher.Constant;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

//import com.common.ImageLoader;


public class ApplicationData extends Application// implements Application.ActivityLifecycleCallbacks
{
    //  public static String web_server_url = "http://constore.no/cslink/";
 //   public static String web_server_url = "http://constore.no/";
    public static String web_server_url = "http://cslink.no/";
//	public static String web_server_url = "http://foodxpress.no/";

    public static String main_url = web_server_url + "api/ipad/";
    private static Activity mainActivity, chatActivity, internalActiivty, reportcardActivity;
    private static com.common.ImageLoader imageMainLoader;
    private static Dialog dlg;

    public static Handler handler;
    static private String alert = "", message = "";
    static private String type = "";
    static private int kidid = 0, from_id = 0;
    private static int badgeCount;
    private static boolean imageLoaderInited = false;
    public static final String MSG_DEFAULT = "Error occured.Could not proceed your request. Please try again later.";
    public static final String MIME_TYPE_PDF = "application/pdf";
    public static boolean ishomebuttonview = false;
    public static String imagepath = "uploads/";
    public static int examno = 20;
    public static final String BROADCAST_CHAT = "com.admin.apps.badge";
    public static final String BROADCAST_CHAT_INTERNAL = "com.admin.apps.badge.internal";
    public static boolean isrunning = false, ispincode = true;
    public static String receiver_jid = "";
    public static boolean ignorbadge = false;
    private static MainProgress pDialog;
    private String Tag = "ApplicationData";
    public static List<Childbeans> duplicatemessage = new ArrayList<Childbeans>();
    public static List<Childbeans> Internalduplicatemessage = new ArrayList<Childbeans>();
    public static boolean isphoto = false;
    public static boolean updateActivityCall = false;


    public static void showMessage(Context context, String title, String msg, String btn_txt) throws Exception {
        //custom dialog

        if (dlg != null && dlg.isShowing())
            dlg.dismiss();
        dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.msgdialog);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView tv_title = (TextView) dlg.findViewById(R.id.msgtitle);
        TextView tv_content = (TextView) dlg.findViewById(R.id.msgcontent);

        tv_title.setText(title);
        tv_title.setVisibility(View.GONE);

        tv_content.setText(msg);

        Button dlg_btn_ok = (Button) dlg.findViewById(R.id.btn_ok);
        dlg_btn_ok.setText(btn_txt);

        dlg_btn_ok.setOnClickListener(new View.OnClickListener() {

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

    public static void doLogout(Activity activity) {
        final Activity mActivity = activity;
        if (dlg != null && dlg.isShowing())
            dlg.dismiss();
        dlg = new Dialog(mActivity);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.adres_msgdialog);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView txtTitle = (TextView) dlg.findViewById(R.id.msgtitle);
        txtTitle.setText(mActivity.getResources().getString(R.string.str_logout));

        TextView content = (TextView) dlg.findViewById(R.id.msgcontent);
        content.setText(mActivity.getResources().getString(R.string.msg_confirm_logout));

        Button dlg_btn_cancel = (Button) dlg.findViewById(R.id.btn_cancel);
        dlg_btn_cancel.setVisibility(View.VISIBLE);

        dlg_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button dlg_btn_ok = (Button) dlg.findViewById(R.id.btn_ok);
        dlg_btn_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                    SharedPreferences myPrefs = mActivity.getSharedPreferences("adminapp",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = myPrefs.edit();

                    editor.putString("teacher_id", "");
                    editor.putString("teacher_name", "");
                    editor.putBoolean("is_login", false);
                    editor.putString("notification", "0");
                    editor.putString("image", "");
                    editor.putString("email", "");
                    editor.putString("school_id", "");
                    editor.putString("user_password", "");
                    editor.putString("jid", "");
                    editor.putString("jid_pwd", "");
                    editor.commit();

                    Intent in = new Intent(mActivity, TeacherLoginActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    mActivity.startActivity(in);
                    mActivity.finish();
                    mActivity.finish();
                    mActivity.finish();
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


    public static void calldialog(final Context mActivity, String title, final String msg, String btn_txt, String btn_txt2,
                                  final DialogListener diaBtnClick, final int diaID) {
        if (dlg != null && dlg.isShowing())
            dlg.dismiss();
        dlg = new Dialog(mActivity);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.msgdialog);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView txtTitle = (TextView) dlg.findViewById(R.id.msgtitle);
        txtTitle.setVisibility(View.GONE);

        TextView content = (TextView) dlg.findViewById(R.id.msgcontent);
        content.setText(title + " " + msg);

        Button dlg_btn_cancel = (Button) dlg.findViewById(R.id.btn_cancel);
        if (btn_txt2 != null && btn_txt2.length() > 0) {
            dlg_btn_cancel.setText(btn_txt2);
            dlg_btn_cancel.setVisibility(View.VISIBLE);
        }

        dlg_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                    if (diaBtnClick != null)
                        diaBtnClick.diaBtnClick(diaID, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button dlg_btn_ok = (Button) dlg.findViewById(R.id.btn_ok);
        dlg_btn_ok.setText(btn_txt);
        dlg_btn_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                    if (diaBtnClick != null)
                        diaBtnClick.diaBtnClick(diaID, 2);
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


    public static boolean URLIsReachable(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            int responseCode = urlConnection.getResponseCode();
            urlConnection.disconnect();
            return responseCode != 200;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Bitmap loadBitmap(String url) {
        if (ApplicationData.URLIsReachable(url)) {
            return null;
        }
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }

    public static void setProfileImg(final CircularImageView profile_pic, final String url, final Context myc) {
        Glide.with(myc)
                .load(url)
                .asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.cslink_avatar_unknown)
                .error(R.drawable.cslink_avatar_unknown)
                .into(new BitmapImageViewTarget(profile_pic) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(myc.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        profile_pic.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    public static void setProfileImg_noplaceholder(final CircularImageView profile_pic, final String url, final Context myc) {

        Glide.with(myc)
                .load(url)
                .asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new BitmapImageViewTarget(profile_pic) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(myc.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        profile_pic.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    public static void showToast(Context context, int stringID, boolean isLong) {
        // get your custom_toast.xml ayout
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) ((Activity) context).findViewById(R.id.custom_toast_layout_id));

        // set a message
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(stringID);

        // Toast...
        Toast toast = new Toast(context);
        //		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void showToast(Context context, String msg, boolean isLong) {
        // get your custom_toast.xml ayout
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) ((Activity) context).findViewById(R.id.custom_toast_layout_id));

        // set a message
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(msg);

        // Toast...
        Toast toast = new Toast(context);
        //		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public final static boolean isValidPhone(CharSequence target) {
        return !TextUtils.isEmpty(target) && target.length() == 8;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void setImageLoader(com.common.ImageLoader imageLoader) {
        imageMainLoader = imageLoader;
    }

    public static com.common.ImageLoader getImageLoader() {
        return imageMainLoader;
    }

    public static String convertToNorweiDate(String date, Context myc) {    //15 des. 2015
        Date convertedDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy,HH:mm", myc.getResources().getConfiguration().locale);

        String formattedFromDate = date;
        try {
            if (date != null) {
                formattedFromDate = targetFormat.format(dateFormat.parse(date));
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return formattedFromDate;
    }


    public static String convertToNorweiDateyeartime(String date, Context myc) {    //15 des. 2015
        Date convertedDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy", myc.getResources().getConfiguration().locale);

        String formattedFromDate = date;
        try {
            if (date != null) {
                formattedFromDate = targetFormat.format(dateFormat.parse(date));
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return formattedFromDate;
    }

    public static String convertToNorweiDatedash(String date, Context myc) {    //15 des. 2015
        Date convertedDate = new Date();
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", myc.getResources().getConfiguration().locale);

        String formattedFromDate = date;
        try {
            if (date != null) {
                formattedFromDate = targetFormat.format(dateFormat.parse(date));
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return formattedFromDate;
    }

    public static String convertToNorwei(String date, Context myc) {    //15 des. 2015
        Date convertedDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", myc.getResources().getConfiguration().locale);
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd", myc.getResources().getConfiguration().locale);

        String formattedFromDate = date;
        try {
            if (date != null) {
                formattedFromDate = targetFormat.format(dateFormat.parse(date));
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return formattedFromDate;
    }

    public static String convertToNorweiDateWithTime(String date, Context myc) {    //15 des. 2015
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat targetFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        TimeZone tzInAmerica = TimeZone.getTimeZone("UTC");
        dateFormat.setTimeZone(tzInAmerica);


        DateFormat formate = new SimpleDateFormat("MM/dd/yy", myc.getResources().getConfiguration().locale);
        DateFormat convertFormate = new SimpleDateFormat("dd MMM yyyy", myc.getResources().getConfiguration().locale);

//        int TimeZoneOffset = tzInAmerica.getRawOffset()/(60 * 60 * 1000);
        String formattedFromDate = date;
        try {
            if (date != null) {

                formattedFromDate = targetFormat.format(dateFormat.parse(date));
                String datePart = formattedFromDate.split(" ")[0];
                int index = formattedFromDate.indexOf(" ");
                formattedFromDate = convertFormate.format(formate.parse(datePart)) + "\n" + formattedFromDate.substring(index + 1, formattedFromDate.length());
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return formattedFromDate;
    }

    public static String convertFromNorweiDate(String date, Context myc) {   //return "2015-12-15"
        Date convertedDate = new Date();
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", myc.getResources().getConfiguration().locale);

        String formattedFromDate = date;
        try {
            if (date != null) {
                formattedFromDate = targetFormat.format(dateFormat.parse(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedFromDate;
    }

    public static String convertFromNorweiDateWithTime(String date, Context myc) {   //return "2015-12-15"
        Date convertedDate = new Date();
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", myc.getResources().getConfiguration().locale);

        String formattedFromDate = date;
        try {
            if (date != null) {
                formattedFromDate = targetFormat.format(dateFormat.parse(date));
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return formattedFromDate;
    }

    public static String convertToreportdate(String date, Context myc) {    //15 des. 2015
        Date convertedDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy", myc.getResources().getConfiguration().locale);

        String formattedFromDate = date;
        try {
            if (date != null) {
                formattedFromDate = targetFormat.format(dateFormat.parse(date));
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return formattedFromDate;
    }

    public static String convertToreport(String date, Context myc) {    //15 des. 2015
        Date convertedDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", myc.getResources().getConfiguration().locale);
        DateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy", myc.getResources().getConfiguration().locale);

        String formattedFromDate = date;
        try {
            if (date != null) {
                formattedFromDate = targetFormat.format(dateFormat.parse(date));
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return formattedFromDate;
    }


    public static void showNotifyAlert(final Context context, final String alert, final String message, final String noti_type, final int noti_kid_id, final int noti_from_id) {
        final Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (dlg != null && dlg.isShowing())
                        dlg.dismiss();
                    dlg = new Dialog(context);
                    dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dlg.setContentView(R.layout.msgdialog);
                    dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    TextView tv_title = (TextView) dlg.findViewById(R.id.msgtitle);
                    TextView tv_content = (TextView) dlg.findViewById(R.id.msgcontent);

                    tv_title.setText(alert);
                    tv_content.setText(message);

                    Button dlg_btn_ok = (Button) dlg.findViewById(R.id.btn_ok);
                    dlg_btn_ok.setText(R.string.str_view);
                    dlg_btn_ok.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                dlg.dismiss();
                                Intent in = new Intent(context, MainActivity.class);
                                in.putExtra("noti_kid_id", noti_kid_id);
                                in.putExtra("noti_type", noti_type);
                                in.putExtra("noti_from_id", noti_from_id);
                                context.startActivity(in);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    Button dlg_btn_cancel = (Button) dlg.findViewById(R.id.btn_cancel);
                    dlg_btn_cancel.setText(R.string.str_close);
                    dlg_btn_cancel.setVisibility(View.VISIBLE);
                    dlg_btn_cancel.setOnClickListener(new View.OnClickListener() {

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

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void setMainActivity(Activity mActivity) {
        mainActivity = mActivity;
    }

    public static Activity getMainActivity() {
        return mainActivity;
    }

    public static void setNotiMessage(String a, String m, String t, int k, int f) {
        alert = a;
        message = m;
        type = t;
        kidid = k;
        from_id = f;
    }

    public static void setChatActivity(Activity mActivity) {
        chatActivity = mActivity;
    }

    public static Activity getChatActivity() {
        return chatActivity;
    }

    public static void setReportActivity(Activity mActivity) {
        reportcardActivity = mActivity;
    }

    public static Activity getReportActivity() {
        return reportcardActivity;
    }

    public static String getLauncherClassName(Context context) {
        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

    public static void showAppBadge(Context context, Integer badge) {
        badgeCount = badge;
        Intent intentBadge = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intentBadge.putExtra("badge_count", badgeCount);
        intentBadge.putExtra("badge_count_package_name", context.getPackageName());
        String launcherClassName = ApplicationData.getLauncherClassName(context);
        if (launcherClassName != null) {
            intentBadge.putExtra("badge_count_class_name", launcherClassName);
            context.sendBroadcast(intentBadge);
        }
    }

    public static void showAppBadgeDec(Context context, Integer decBadge) {
        badgeCount = badgeCount - decBadge;
        Intent intentBadge = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        if (badgeCount < 0)
            badgeCount = 0;
        intentBadge.putExtra("badge_count", badgeCount);
        intentBadge.putExtra("badge_count_package_name", context.getPackageName());
        String launcherClassName = ApplicationData.getLauncherClassName(context);
        if (launcherClassName != null) {
            intentBadge.putExtra("badge_count_class_name", launcherClassName);
            context.sendBroadcast(intentBadge);
        }
    }

    public static void hideKeyboardForFocusedView(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void initImageLoader(Context context) {
        if (imageLoaderInited)
            return;

        imageLoaderInited = true;

        ImageLoaderConfiguration config = null;
        if (android.os.Build.VERSION.SDK_INT < 11) {
            config = new ImageLoaderConfiguration.Builder(context)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .threadPoolSize(1)
                    .memoryCache(new WeakMemoryCache())
                    .build();
        } else {
            config = new ImageLoaderConfiguration.Builder(context)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .threadPoolSize(3)
                    .build();
        }

        // Initialize ImageLoader with configuration.
        if (config != null)
            ImageLoader.getInstance().init(config);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
// pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static String getjid(String receiver_id_jid) {

        receiver_id_jid = receiver_id_jid.substring(0, receiver_id_jid.indexOf("/"));

        return receiver_id_jid;
    }

    public static void setProfileRounded(final RoundedImageView profile_pic, final String url, final Context myc) {
        Glide.with(myc)
                .load(url)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.cslink_avatar_unknown)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.cslink_avatar_unknown)
                .into(profile_pic);
    }

    public static void setProfileRoundedwithoutdefault(final RoundedImageView profile_pic, final String url, final Context myc) {
        Glide.with(myc)
                .load(url)
                .asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profile_pic);
    }

    public static void setexpandListViewHeightBasedOnChildren(ExpandableListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
// pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight - (listView.getDividerHeight() * (listAdapter.getCount() / 2));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static boolean isPdfavailable(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType(MIME_TYPE_PDF);
        if (packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String getlanguage(Context context) {
        SharedPreferences sharedpref = context.getSharedPreferences(Constant.USER_FILENAME, 0);
        String language = sharedpref.getString("language", "");
        String localeString;
        if (language.equalsIgnoreCase("english")) {
            localeString = "en";
        } else {
            localeString = "no";
        }
        return localeString;
    }

    public static String getlanguageAndApi(Context context, String apiname) {
        SharedPreferences sharedpref = context.getSharedPreferences(Constant.USER_FILENAME, 0);
        String language = sharedpref.getString("language", "");
        String localeString = "no";

        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            if (language.equalsIgnoreCase("english")) {
                localeString = "en";
            } else {
                localeString = "no";
            }
            localeString = ApplicationData.main_url + apiname + ".php?language=" + localeString + "&app_version=" + version + "&os=android&";

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return localeString;
    }


    public static long getDateDifference(String mDate) {

        Date d1 = null;
        Date d2 = null;
        long days = 0;

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy");
        String formattedDate = df.format(c.getTime());
        if (formattedDate.equalsIgnoreCase(mDate.split(",")[0])) {
            return 0;
        } else
            return 1;
    }

    public static String convertlocalize(String created_at) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DateFormat dateformate = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, Locale.getDefault());

        try {
            created_at = dateformate.format(sdf.parse(created_at));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return created_at;
    }

    public static void setInternalActivity(Activity mActivity) {
        internalActiivty = mActivity;
    }

    public static Activity getInternalActivity() {
        return internalActiivty;
    }

    public static String getUniqId(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static String getAppVersion(Context context) {
        String version = "";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static void downloadPDF(final Context context, final String url) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final MainProgress pDialog = new MainProgress(context);
                pDialog.setCancelable(false);
                pDialog.setMessage(context.getString(R.string.str_wait));
                pDialog.show();

                ApplicationData.isphoto = true;
                String fileUrl = url;   // -> http://maven.apache.org/maven-1.x/maven.pdf
                SimpleDateFormat dateformt = new SimpleDateFormat("dd-MMM-yyyy_mmss");
                String date = dateformt.format(Calendar.getInstance().getTime());

                final String pdfname = "Markpdf_" + date;
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

                    final File finalFile = file2;
                    FileDownloader.downloadFile(fileUrl, file2, new FileDownloader.DownloadListener() {
                        @Override
                        public void onDownloadSuccessful() {
                            openPdfFile(context, pdfname);
                            pDialog.dismiss();
                        }

                        @Override
                        public void onDownloadFailed(String errorMessage) {
                            Log.e("PdfReport ", errorMessage);
                            Toast.makeText(context, context.getString(R.string.pdf_load_issue), Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public interface DialogListener {
        public void diaBtnClick(int diaID, int btnIndex);
    }

    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static byte[] readFile(File file) {
        try {
            // Open file
            RandomAccessFile f = new RandomAccessFile(file, "r");
            try {
                // Get and check length
                long longlength = f.length();
                int length = (int) longlength;
                if (length != longlength) {
                    throw new IOException("File size >= 2 GB");
                }
                // Read file and return data
                byte[] data = new byte[length];
                f.readFully(data);
                return data;
            } finally {
                f.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void createMarkPdf(Context context, List<Childbeans> subtopdfarray, Childbeans data,
                                     ArrayList<Childbeans> studentlist, String sendmail) {

        float actualheight = 0, newheight = 0;
        int pageno = -1;
        if (pDialog == null)
            pDialog = new MainProgress(context);
        pDialog.setCancelable(false);
        pDialog.setMessage(context.getString(R.string.str_wait));
        pDialog.show();

        ApplicationData.isphoto = true;
        float usedheight = 0;
        SimpleDateFormat dateformt = new SimpleDateFormat("dd-MMM-yyyy_mmss");
        String date = dateformt.format(Calendar.getInstance().getTime());

        String pdfname = "Markpdf_" + date;
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

            Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.NORMAL);
            Font grayFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.DARK_GRAY);

            Font blackFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
            Font blackFont2 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL);

            //start document to write
            Document document = new Document();
            PdfWriter write = PdfWriter.getInstance(document, new FileOutputStream(file2.getAbsoluteFile()));
            //ColumnText ct  = null;

            //margin to document
            document.top(5f);
            document.left(10f);
            document.right(10f);
            document.bottom(5f);

            actualheight = document.getPageSize().getHeight();

            for (int stdloop = 0; stdloop < studentlist.size(); stdloop++) {
                //if createpdf for single user
                String child_name = "", classname = "", schoolname = "";
                child_name = studentlist.get(stdloop).child_name;
                classname = studentlist.get(stdloop).class_name;
                schoolname = studentlist.get(stdloop).school_name;

                if (stdloop != 0)
                    document.newPage();

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


                int k = 0;// create new page after even number
                for (int i = 0; i < subtopdfarray.size(); i++) {
                    for (int j = 0; j < subtopdfarray.get(i).markarray.size(); j++) {
                        MarkBean mbean = subtopdfarray.get(i).markarray.get(j);

                        if (pageno == -1) {
                            if ((int) document.getPageSize().getHeight() < ((mbean.comment.length() + mbean.exam_about.length() + mbean.mark.length()) * 5 + 15)) {
                                newheight = mbean.comment.length() > 1200 ? 1200 : mbean.comment.length();//+mbean.exam_about.length()+mbean.mark.length();
                                Rectangle rect = new Rectangle(document.getPageSize().getWidth(), newheight);
                                document.setPageSize(rect);
                            } else {
                                newheight = ((mbean.comment.length() + mbean.exam_about.length() + mbean.mark.length()) * 5) + 115;
                            }
                            if (k != 0) {
                                pageno = document.getPageNumber();
                            }
                        } else if (pageno == document.getPageNumber() && pageno != -1) {

                            float remainingheight = newheight > document.getPageSize().getHeight() ? newheight - document.getPageSize().getHeight() : document.getPageSize().getHeight() - newheight;

                            if (newheight != 1200 && remainingheight > 842) {
                                remainingheight = 842 / 2;
                            }
                            if (remainingheight < ((mbean.comment.length() + mbean.exam_about.length() + mbean.mark.length()) * 5) + 15 || remainingheight < 130) {

                                Rectangle rect;
                                if (mbean.comment.length() > 150) {
                                    newheight = (((mbean.comment.length() / 2) + mbean.exam_about.length() + mbean.mark.length()) * 5) + 15;
                                } else {
                                    newheight = ((mbean.comment.length() + mbean.exam_about.length() + mbean.mark.length()) * 5) + 15;
                                }

                                if (mbean.comment.length() > actualheight) {
                                    newheight = mbean.comment.length() > 1200 ? 1200 : mbean.comment.length();
                                    rect = new Rectangle(document.getPageSize().getWidth(), newheight);

                                } else {
                                    rect = new Rectangle(document.getPageSize().getWidth(), actualheight);
                                }
                                document.setPageSize(rect);
                                document.newPage();
                                pageno = -1;
                            } else {
                                newheight += ((mbean.comment.length() + mbean.exam_about.length() + mbean.mark.length()) * 5) + 15;
                            }
                        } else if (pageno == 2) {
                            if ((int) document.getPageSize().getHeight() < (mbean.comment.length() + mbean.exam_about.length() + mbean.mark.length() * 5)) {
                                newheight = mbean.comment.length() > 1200 ? 1200 : mbean.comment.length();//+mbean.exam_about.length()+mbean.mark.length();
                                Rectangle rect = new Rectangle(document.getPageSize().getWidth(), newheight);
                                document.setPageSize(rect);
                            } else {
                                newheight = ((mbean.comment.length() + mbean.exam_about.length() + mbean.mark.length()) * 5) + 115;
                            }
                            document.newPage();
                            pageno = document.getPageNumber();
                        } else {
                            newheight = ((mbean.comment.length() + mbean.exam_about.length() + mbean.mark.length()) * 5) + 15;
                            Rectangle rect1 = new Rectangle(document.getPageSize().getWidth(), actualheight);
                            document.setPageSize(rect1);
                        }
                        if (k == 0) {
                            document.open();

                            document.add(prefaceHeader);

                            document.add(table1);

                            pageno = document.getPageNumber();
                        }

                        if (k == -1) {
                            k = 0;
                        }

                        Paragraph prefermiddle = new Paragraph();
                        prefermiddle.setAlignment(Element.ALIGN_LEFT);

                        String examno = Character.toUpperCase(context.getResources().getString(R.string.test).charAt(0)) + context.getResources().getString(R.string.test).substring(1).toLowerCase() + " :" + mbean.exam_no;
                        String examdate = context.getString(R.string.date_test) + ":" + ApplicationData.convertToreportdate(mbean.exam_date, context);

                        //create left and right alignment
                        Chunk glue = new Chunk(new VerticalPositionMark());
                        Phrase p = new Phrase("", blackFont1);
                        p.add(examno);
                        p.add(glue);
                        p.add(examdate);
                        p.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD));

                        document.add(p);
                        // ct.addElement(p);
                        // ct.go();

                        String teacherpart = context.getResources().getString(R.string.teachernm) + " : " + subtopdfarray.get(i).teacher_name;
                        prefermiddle.add(new Paragraph(teacherpart, blackFont1));

                        String subjname = context.getString(R.string.subject_name).substring(0, 7) + " : " + subtopdfarray.get(i).subject_name;
                        prefermiddle.add(new Paragraph(subjname, blackFont1));

                        addEmptyLine(prefermiddle, 1);

                        String examabout = Character.toUpperCase(context.getResources().getString(R.string.exam_about).charAt(0)) + context.getResources().getString(R.string.exam_about).substring(1).toLowerCase() + ":";
                        prefermiddle.add(new Paragraph(examabout, blackFont1));
                        String examaboutval = mbean.exam_about;
                        prefermiddle.add(new Paragraph(examaboutval, blackFont2));

                        addEmptyLine(prefermiddle, 1);

                        String comment = context.getResources().getString(R.string.comment) + ":";
                        prefermiddle.add(new Paragraph(comment, blackFont1));
                        String commentval = mbean.comment;
                        prefermiddle.add(new Paragraph(commentval, blackFont2));
                        addEmptyLine(prefermiddle, 1);

                        String mark = context.getResources().getString(R.string.pdf_mark) + ":";
                        prefermiddle.add(new Paragraph(mark, blackFont1));
                        String markval = mbean.mark != null && mbean.mark.length() > 0 ? mbean.mark : context.getString(R.string.no_rate);
                        prefermiddle.add(new Paragraph(markval, blackFont2));

                        addEmptyLine(prefermiddle, 1);

                       /* String line2 = "---------------------------------------------------------------------------------------------------------------";
                        prefermiddle.add(new Paragraph(line2, blackFont1));
                        addEmptyLine(prefermiddle, 1);

                        document.add(prefermiddle);*/
                        PdfPTable table = new PdfPTable(1);
                        PdfPCell pcell = new PdfPCell();
                        table.getDefaultCell().setBorder(Rectangle.TOP);
                        table.setWidthPercentage(100);
                        table.addCell(pcell);

                        addEmptyLine(prefermiddle, 1);

                        document.add(prefermiddle);

                      /*  Image image = Image.getInstance(ApplicationData.web_server_url+"uploads/"+mbean.image);
                        image.setAbsolutePosition(220f, 550f);
                        document.add(image);*/

                        document.add(table);

                        k++;
                        if (k % 2 == 0 && j != i - 1 && pageno != -1) {
                            pageno = 2;
                        }
                    }
                    pageno = 2;
                    k = -1;
                }

                pDialog.dismiss();

            }
            document.close();
            openPdfFile(context, pdfname);
            new UploadPdf(context, pdfname, sendmail, "marksreport").execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PdfPCell getCell(String data, int alignLeft) {
        PdfPCell pcell = new PdfPCell(new Phrase(data));
        pcell.setBorder(PdfPCell.NO_BORDER);
        return pcell;
    }


    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private static String makeSpace(int cnt) {
        String str = "";
        for (int i = 0; i < cnt; i++) {
            str = str + " ";
        }
        return str;
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


    public static class UploadPdf extends AsyncTask<String, Void, Void> {

        private final Context context;
        private final String pdfname, sendmail, pdftype;
        private final String teacher_id;
        String Response = "";

        public UploadPdf(Context context, String pdfname, String sendmail, String pdftype) {
            this.context = context;
            this.pdfname = pdfname;
            this.sendmail = sendmail;
            this.pdftype = pdftype;
            SharedPreferences shf = context.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
            teacher_id = shf.getString("teacher_id", "");
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            if (pDialog == null)
                pDialog = new MainProgress(context);
            pDialog.setCancelable(false);
            pDialog.setMessage(context.getResources().getString(R.string.str_wait));
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {

            try {

                String url = ApplicationData.getlanguageAndApi(context, ConstantApi.UPLOAD_PDF) + "teacher_id=" + teacher_id
                        + "&report=" + pdftype + "&sendmail=" + sendmail;

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost postRequest = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);


                File file = new File(Environment.getExternalStorageDirectory() + "/CSadminFolder/pdf", pdfname + ".pdf");

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
                        //ApplicationData.showToast(context,context.getResources().getString(R.string.msg_success_reported), false);
                    } else {
                        String msg = response.getString("msg");
                        ApplicationData.showToast(context, msg, false);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    ApplicationData.showToast(context, context.getResources().getString(R.string.msg_operation_error), false);
                }
            } else {
                ApplicationData.showToast(context, context.getResources().getString(R.string.msg_operation_error), false);
            }
        }
    }

}


