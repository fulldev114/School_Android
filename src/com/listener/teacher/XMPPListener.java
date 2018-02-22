package com.listener.teacher;


import android.content.Context;
import android.util.Log;

import com.xmpp.teacher.MessageReceivedListener;
import com.xmpp.teacher.XMPPMethod;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;

/**
 * Created by etech on 23/5/16.
 */
public class XMPPListener {
    private final MessageReceivedListener mMessageReceivedListener;
    private final DeliveryReportReceivedListener mDeliveryReportReceivedListener;
    private final InternalMessageReceivedListener mInternalMessageDelivery;
    private final Context context;
    private XMPPConnection xmppcon;

    public XMPPListener(Context context) {
        this.context = context;
        mMessageReceivedListener = new MessageReceivedListener(context);
        mDeliveryReportReceivedListener = new DeliveryReportReceivedListener(context);
        mInternalMessageDelivery = new InternalMessageReceivedListener(context);
    }

    public void initializeListeners() {
        xmppcon = XMPPMethod.getconnectivity();
        if (xmppcon != null)
            if (xmppcon.isConnected() && xmppcon.isAuthenticated()) {
                xmppcon.addPacketListener(mMessageReceivedListener, null);
                xmppcon.addPacketListener(mInternalMessageDelivery, null);
                DeliveryReceiptManager.getInstanceFor(xmppcon)
                        .autoAddDeliveryReceiptRequests();
                DeliveryReceiptManager
                        .getInstanceFor(xmppcon)
                        .addReceiptReceivedListener(mDeliveryReportReceivedListener);
            }
    }

    public void removeListeners() {
        xmppcon = XMPPMethod.getconnectivity();

        try {

            // Message Listener

            if (mMessageReceivedListener != null)
                xmppcon.removePacketListener(mMessageReceivedListener);

            if (mInternalMessageDelivery != null)
                xmppcon.removePacketListener(mInternalMessageDelivery);

            // delivery report listener
            if (mDeliveryReportReceivedListener != null) {
                DeliveryReceiptManager.getInstanceFor(xmppcon)
                        .removeReceiptReceivedListener(
                                mDeliveryReportReceivedListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
