package com.service.teacher;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * Created by etech on 4/2/17.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NotificationJobService extends JobService {
    private static final String TAG = "SyncService";

    @Override
    public boolean onStartJob(JobParameters params) {
        // fake work
        startService(new Intent(getApplicationContext(), ReconnectionService.class));
        Log.i(TAG, "on start job: " + params.getJobId());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
