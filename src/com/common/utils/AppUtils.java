package com.common.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by etech on 28/9/16.
 */
public class AppUtils {
    public static boolean verifyAllPermissions(int[] permissions) {
        for (int result : permissions) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasSelfPermission(Activity activity, String[] permissions) {
        /*if (!isMNCBuildVersion()) {
            return true;
        }*/

        // Verify that all the permissions.
        for (String permission : permissions) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasSelfPermission(Context activity, String[] permissions) {
        /*if (!isMNCBuildVersion()) {
            return true;
        }*/

        // Verify that all the permissions.
        for (String permission : permissions) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    public static boolean isMNCBuildVersion() {
        return "MNC".equals(Build.VERSION.CODENAME);
    }
}
