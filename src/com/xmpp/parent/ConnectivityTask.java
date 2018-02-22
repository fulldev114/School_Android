package com.xmpp.parent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;

import com.cloudstream.cslink.parent.ApplicationData;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by etech on 2/9/16.
 */
public class ConnectivityTask extends AsyncTask {


    private final Context context;
    private final XMPPConnection xmppcon;
    SharedPreferences myPrefs;

    public ConnectivityTask(Context context, XMPPConnection xmppcon) {
        this.context = context;
        this.xmppcon = xmppcon;
        myPrefs = context.getSharedPreferences("absentapp", Context.MODE_PRIVATE);
    }

    @Override
    protected Object doInBackground(Object[] params) {

        XMPPMethod.disconnection(context, myPrefs.getString("jid", ""));
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            XMPPMethod.connect(context, myPrefs.getString("jid", ""), "5222", myPrefs.getString("jid_pwd", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Handler handl = new Handler();
        handl.postDelayed(new Runnable() {
            @Override
            public void run() {
                ApplicationData.ignorbadge = false;
            }
        }, 5000);
    }
}
