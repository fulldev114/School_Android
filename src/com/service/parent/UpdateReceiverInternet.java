package com.service.parent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.R;
import com.xmpp.parent.XMPPMethod;

/**
 * Created by etech on 15/9/16.
 */
public class UpdateReceiverInternet extends BroadcastReceiver {

    String TAG = "UpdateReceiverInternet";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ApplicationData.getMainActivity() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetInfo != null && activeNetInfo.isConnected();
            if (isConnected) {
                Toast.makeText(context, context.getString(R.string.connection), Toast.LENGTH_SHORT).show();
                try {
                    XMPPMethod.isconnected(context);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, context.getString(R.string.msg_operation_error), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
