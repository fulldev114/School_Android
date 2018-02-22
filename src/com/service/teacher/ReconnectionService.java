package com.service.teacher;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.cloudstream.cslink.teacher.MainActivity;

/**
 * Created by etech on 13/7/16.
 */
public class ReconnectionService extends Service {
    private final IBinder mBinder = new MyBinder();
    private JobScheduler jobScheduler;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {

            ComponentName mServiceComponent = new ComponentName(ReconnectionService.this, NotificationJobService.class);
            JobInfo.Builder builder = new JobInfo.Builder(MainActivity.kJobId, mServiceComponent);
            builder.setMinimumLatency(3 * 1000); // wait at least
            builder.setOverrideDeadline(30 * 1000); // maximum delay
            builder.setPersisted(true);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
            builder.setRequiresDeviceIdle(true); // device should be idle
            builder.setRequiresCharging(false); // we don't care if the device is charging or not
            jobScheduler = (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        public ReconnectionService getService() {
            return ReconnectionService.this;
        }
    }

}
