package com.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.Global;
import com.cloudstream.cslink.R;

import java.io.File;

/**
 * Created by Administrator on 11/12/2015.
 */
public class GlobalConstrants {

    private static String LOCAL_DIR_NAME = "CSlink_Image";
    public static String LOCAL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + LOCAL_DIR_NAME + "/";
    public static String path = Environment.getExternalStorageDirectory().toString() + File.separator + LOCAL_DIR_NAME + File.separator + "yay.jpg";
    public static String LOCAL_MEDIA_DIR_PATH = LOCAL_PATH + "Media/";
    public static String STICKER_DIR_PATH = LOCAL_PATH + ".Stickers/";
    public static String THEME_DIR_PATH = LOCAL_PATH + "Themes/";
    public static String AVATAR_DIR_PATH = LOCAL_PATH + "Avatars/";
    public static String CAMERA_TMP_DIR_PATH = LOCAL_PATH + "Cameratmp/";
    public static String CAMERA_TEMP_IMAGE_PATH = CAMERA_TMP_DIR_PATH + "camera.jpg";
    public static String CAMERA_TEMP_VIDEO_PATH = CAMERA_TMP_DIR_PATH + "camera.mp4";

    public final static int IMAGE_SIZE = 600;
    public final static int IMAGE_SMALL_SIZE = 200;
    public final static int PHOTO_COVER_SIZE = 864;


    public static final String DELIMIT = "$";
    public static final String RECV_DELIMIT = "\\$";
    public static final String ENDMARK = "\n";


    public static boolean isWifiConnected(Context context) {

        if (context == null) {
            context = Global.getContext();
        }
        ConnectivityManager connectivity=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity !=null)
        {
            boolean isconnected=false;
            NetworkInfo[] info=connectivity.getAllNetworkInfo();
            if(info != null)
            {
                for(int i=0;i<info.length;i++){
                    if(info[i].getState()== NetworkInfo.State.CONNECTED){
                        isconnected=true;
                        break;
                    }
                }

                if(isconnected)
                    return true;
                else{
                    ApplicationData.showToast(context, R.string.msg_operation_error, true);
                    return false;
                }
            }
        }
        return false;

      /*  ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();
        if (!activeNetInfo.isConnected()) {
            ApplicationData.showToast(context, R.string.msg_operation_error, true);
            return false;
        }
        return true;*/
    }


}
