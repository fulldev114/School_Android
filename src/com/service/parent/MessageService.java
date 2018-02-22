package com.service.parent;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.adapter.parent.Childbeans;
import com.listener.parent.XMPPListener;
import com.listener.XmppDeliveryReportListener;
import com.xmpp.parent.Constant;
import com.xmpp.parent.XMPPMethod;
import com.xmpp.parent.XmppCall;
import com.xmpp.parent.XmppMessageListener;
import com.xmpp.parent.XmppServiceBinder;

import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;

/**
 * Created by etech on 24/5/16.
 */
public class MessageService extends Service{
    private Service service;
    private static MessageService mXmppService;
    private static boolean mBounded;
    static ArrayList<Childbeans> al =new ArrayList<Childbeans>();
    private static ArrayList<Childbeans> list;
    private static boolean running;
    private XMPPListener listener;
    private XMPPConnection xmp;

    public static ArrayList<Childbeans> getList() {
        if(al!=null && al.size()>0)
            return al;
        else
            return null;
    }

    public XMPPConnection getAuthentication() {
        xmp = XMPPMethod.getconnectivity();
        if(xmp!=null)
            if(xmp.isConnected())
                if(xmp.isAuthenticated())
                    return xmp;

        return null;
    }

    public XMPPConnection getActiveConnection() {

        if ((xmp != null) && xmp.isConnected()
                && xmp.isAuthenticated()) {
            Log.e("Active :: " ,"Active");
            return xmp;
        }

        return null;
    }


    public Context getContext() {
        return MessageService.this;
    }

    public void removelisterner() {
        listener.removeListeners();
    }

    public enum XmppStatus {
        NO_INTERNET, NOT_CONNECTED, MESSAGE_SENT, MESSAGE_DELIVERED, SENDING_ERROR, MESSAGE_RECEIVED, DOWNLOADING, DOWNLOAD_COMPLETE, DOWNLOAD_CANCELLED, DOWNLOAD_FAILED, UPLOAD_FAILED, UPLOADING, UPLOAD_COMPLETE, UPLOAD_CANCELLED, GROUP_UPDATE, GROUP_DELETE, GROUP_MESSAGE_SENT, GROUP_MESSAGE_RECEIVED
    }
    public static MessageService getServiceInstance() {
        if (running && mXmppService != null) {
            return mXmppService;
        }
        return null;
    }

    public static final String BroadcastKeyStatusCode = "StatusCode";
    public static final String BroadcastKeyMessageRowId = "MessageRowId";
    public static final String BroadcastKeyGroupId = "GroupId";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onCreate();
        return new XmppServiceBinder<MessageService>(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        doBindService();
        init();
        return Service.START_STICKY;
    }

    private void doBindService() {
        bindService(new Intent(this, MessageService.class), mConnection,
                getApplicationContext().BIND_AUTO_CREATE);
        //   startService(new Intent(this, FriendList.class));
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static ServiceConnection mConnection = new ServiceConnection() {

        @Override
        @SuppressWarnings("unchecked")
        public synchronized void onServiceConnected(final ComponentName name,
                                                    final IBinder service) {
            mXmppService = ((XmppServiceBinder<MessageService>) service).getService();
            mBounded = true;
            running = true;
            Log.d("Friendlist", "onServiceConnected");

        }

        @Override
        public synchronized void onServiceDisconnected(final ComponentName name) {
            mXmppService = null;
            mBounded = false;
            running = false;
            Log.d("service friend ", "onServiceDisconnected");
        }

    };

    private void init() {
        listener = new XMPPListener(this);
        listener.initializeListeners();
    }

    protected void sendXmppBroadcast(Intent data) {
        String action = Constant.CUSTOM_INTENT_XMPP;
        data.setAction(action);
        sendBroadcast(data);
    }



}
