package com.common.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.parent.ParentLoginActivity;
import com.cloudstream.cslink.parent.PasswordActivity;
import com.cloudstream.cslink.parent.Pincode_activity;
import com.cloudstream.cslink.parent.RegisterMainActivity;
import com.cloudstream.cslink.parent.Splash_Activity;
import com.cloudstream.cslink.parent.UpdateAvailableActivity;
import com.cloudstream.cslink.parent.VerifyActivity;
import com.xmpp.parent.Constant;
import com.xmpp.parent.XMPPMethod;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by etech on 3/8/16.
 */
public class Foreground implements Application.ActivityLifecycleCallbacks {

    private static Foreground instance;
    private String TAG = "Foreground";
    private boolean foreground = false, paused = true;
    private Handler handler = new Handler();
    private Runnable check;
    public static final long CHECK_DELAY = 500;
    private static Context appCtx;

    private List<Listener> listeners = new CopyOnWriteArrayList();

    public boolean isForeground() {
        return foreground;
    }

    public boolean isBackground() {
        return !foreground;
    }

    public static Foreground init(Application application) {
        if (instance == null) {
            instance = new Foreground();

            application.registerActivityLifecycleCallbacks(instance);
        }
        return instance;
    }

    public static Foreground get(Context ctx) {
        if (instance == null) {
            appCtx = ctx.getApplicationContext();
            if (appCtx instanceof Application) {
                init((Application) appCtx);
            }
            throw new IllegalStateException("IllegalException");
        }
        return instance;
    }

    public static Foreground get() {
        return instance;
    }

    private Foreground() {
    }


    @Override
    public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
        ApplicationData.isrunning = true;


        /*HomeWatcher mHomeWatcher = new HomeWatcher(activity);
       mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                ApplicationData.isrunning = false;
                SharedPreferences sharedpref = activity.getSharedPreferences(Constant.USER_FILENAME, 0);
                String sender_jid = sharedpref.getString("jid", "");
                ApplicationData.setMainActivity(null);
                XMPPMethod.disconnection(activity, sender_jid);
                //   activity.stopService(new Intent(activity, ReconnectionService.class));
                activity.stopService(new Intent(activity, MessageService.class));
            }

            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();*/
        // activity.startService(new Intent(activity, MessageService.class));
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ApplicationData.isrunning = true;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;
        ApplicationData.isrunning = true;
        ApplicationData.setMainActivity(activity);


        if (check != null)
            handler.removeCallbacks(check);

        if (wasBackground && !ApplicationData.isphoto) {
            if (!activity.getClass().getSimpleName().equalsIgnoreCase(Pincode_activity.class.getSimpleName())
                    && !activity.getClass().getSimpleName().equalsIgnoreCase(ParentLoginActivity.class.getSimpleName())
                    && !activity.getClass().getSimpleName().equalsIgnoreCase(Splash_Activity.class.getSimpleName())
                    && !activity.getClass().getSimpleName().equalsIgnoreCase(VerifyActivity.class.getSimpleName())
                    && !activity.getClass().getSimpleName().equalsIgnoreCase(PasswordActivity.class.getSimpleName())
                    && !activity.getClass().getSimpleName().equalsIgnoreCase(RegisterMainActivity.class.getSimpleName())
                    && !activity.getClass().getSimpleName().equalsIgnoreCase(UpdateAvailableActivity.class.getSimpleName())) {

                Intent i = new Intent(activity, PasswordActivity.class);
                activity.startActivity(i);
            }

            for (Listener l : listeners) {
                try {
                    l.onBecameForeground();
                } catch (Exception exc) {
                    Log.e(TAG, "Listener threw exception!", exc);
                }
            }


        } else {
            Log.i(TAG, "still foreground");
        }

    }

    @Override
    public void onActivityPaused(final Activity activity) {
        // foreground = false;
        paused = true;

        if (check != null)
            handler.removeCallbacks(check);

        handler.postDelayed(check = new Runnable() {
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;
                    ApplicationData.isrunning = false;
                    SharedPreferences sharedpref = activity.getSharedPreferences(Constant.USER_FILENAME, 0);
                    String sender_jid = sharedpref.getString("jid", "");
                    ApplicationData.setMainActivity(null);
                    XMPPMethod.disconnection(activity, sender_jid);
                    //  activity.stopService(new Intent(activity, MessageService.class));

                    for (Listener l : listeners) {
                        try {
                            l.onBecameBackground();
                        } catch (Exception exc) {
                            //	Log.e(TAG, "Listener threw exception!", exc);
                        }
                    }
                } else {
                    //	Log.i(TAG, "still foreground");
                }
            }
        }, CHECK_DELAY);

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

        Log.d(TAG, "app is running : " + ApplicationData.isrunning);
        ApplicationData.isrunning = false;


    }

    public interface Listener {
        public void onBecameForeground();

        public void onBecameBackground();
    }


    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }
}
