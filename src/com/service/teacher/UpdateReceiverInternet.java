package com.service.teacher;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudstream.cslink.R;
import com.cloudstream.cslink.teacher.ApplicationData;
import com.cloudstream.cslink.teacher.Global;
import com.cloudstream.cslink.teacher.PasswordActivity;

import com.common.dialog.MainProgress;
import com.common.utils.GlobalConstrants;
import com.xmpp.teacher.Constant;
import com.xmpp.teacher.XMPPMethod;


/**
 * Created by etech on 15/9/16.
 */
public class UpdateReceiverInternet extends BroadcastReceiver {

    String TAG = "UpdateReceiverInternet";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ApplicationData.getMainActivity() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            SharedPreferences preference = context.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);

            boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
            if (isConnected) {
                Toast.makeText(context, context.getString(R.string.connection), Toast.LENGTH_SHORT).show();
                 try {
                    XMPPMethod.isconnected(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, context.getString(R.string.msg_operation_error), Toast.LENGTH_SHORT).show();
            }
        }

    }
}
