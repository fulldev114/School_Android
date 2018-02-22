package com.service.teacher;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.adapter.teacher.Childbeans;
import com.listener.teacher.XMPPListener;
import com.listener.XmppDeliveryReportListener;
import com.xmpp.teacher.XMPPMethod;
import com.xmpp.teacher.XmppCall;
import com.xmpp.teacher.XmppMessageListener;
import com.xmpp.teacher.XmppServiceBinder;
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
    public static final String BroadcastKeyStatusCode = "StatusCode";
    public static final String BroadcastKeyMessageRowId = "MessageRowId";
    public static final String BroadcastKeyGroupId = "GroupId";
    /*public static ArrayList<Childbeans> getList() {
        if(al!=null && al.size()>0)
            return al;
        else
            return null;
    }*/

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
            return xmp;
        }

        return null;
    }

    /*public void addXmppMessageListener(XmppMessageListener listener) {
        if ((listener != null)) {
            XmppCall.addXmppMessageListener(listener);
        }
    }

    public static void addXmppDeliveryReportListener(XmppDeliveryReportListener listener) {
        if ((listener != null)) {
            XmppCall.addXmppDeliveryReportListener(listener);
        }

    }*/
    public void removelisterner() {
        listener.removeListeners();
    }

    public Context getContext() {
        return MessageService.this;
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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onCreate();
        return new XmppServiceBinder<MessageService>(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        DeliveryReceiptManager.getInstanceFor(xmp);
     //   al=XMPPMethod.getrequest(MessageService.this);
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

        }

        @Override
        public synchronized void onServiceDisconnected(final ComponentName name) {
            mXmppService = null;
            mBounded = false;
            running = false;
        }

    };

    private void init() {
        listener = new XMPPListener(this);
        listener.initializeListeners();
    }

   /* protected void sendXmppBroadcast(Intent data) {
        String action = Constant.CUSTOM_INTENT_XMPP;
        data.setAction(action);
        sendBroadcast(data);
    }

    public void setTyping(String to_username, boolean isTyping) {
        try {
            //if (state == STATE.CONNECTED)
            {
                XmppCall.sendTyping(MessageService.this, to_username, isTyping,xmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
*/


}
