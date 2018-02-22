package com.cloudstream.cslink;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.cloudstream.cslink.parent.ApplicationData;
import com.common.utils.Foreground;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.EmailIntentSender;
import org.acra.sender.HttpSender;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by etech on 11/8/16.
 */
@ReportsCrashes(
        formUri = "http://etech.cslink.no/",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        mode = ReportingInteractionMode.DIALOG,
        mailTo = "developer.etechmavens@gmail.com",
        formUriBasicAuthLogin = "GENERATED_USERNAME_WITH_WRITE_PERMISSIONS",
        formUriBasicAuthPassword = "GENERATED_PASSWORD",
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL,
                ReportField.CUSTOM_DATA,
                ReportField.STACK_TRACE,
                ReportField.LOGCAT,
                ReportField.USER_COMMENT
        },
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogText = R.string.app_name,
        formKey = ""
)
public class Global extends ApplicationData {

    private Foreground forground;
    public static Global context;
    public static ArrayList<String> logarr = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        forground = Foreground.init(this);
        context=this;

        boolean isDebuggable = 0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE);
        boolean isBeingDebugged = android.os.Debug.isDebuggerConnected();

        if (isDebuggable || isBeingDebugged) {
            logarr= new ArrayList<>();
        }
       /* try {
            ACRA.init(this);
            ACRA.getErrorReporter().addReportSender(new EmailIntentSender(this));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public static Context getContext()
    {
        return context;
    }

  /*  public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "File /data/data/APP_PACKAGE/" + s + " DELETED");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    @Override
    public boolean deleteFile(String name) {
        clearApplicationData();
        File dir = new File(name);
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }*/
}
