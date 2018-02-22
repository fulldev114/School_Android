package com.listener.parent;


import android.content.Context;

import com.xmpp.parent.MessageReceivedListener;
import com.xmpp.parent.XMPPMethod;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;

/**
 * Created by etech on 23/5/16.
 */
public class XMPPListener {

    private final MessageReceivedListener mMessageReceivedListener;
    private final DeliveryReportReceivedListener mDeliveryReportReceivedListener;
    private final Context context;
    private XMPPConnection xmppcon;

    public XMPPListener(Context context) {
        this.context = context;

        mMessageReceivedListener = new MessageReceivedListener(context);
        mDeliveryReportReceivedListener = new DeliveryReportReceivedListener(context);
    }


    public void initializeListeners() {
        xmppcon = XMPPMethod.getconnectivity();
        if (xmppcon != null)
            if (xmppcon.isConnected() && xmppcon.isAuthenticated()) {
                xmppcon.addPacketListener(mMessageReceivedListener, null);
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

            // delivery report listener
            if (mDeliveryReportReceivedListener != null) {
                DeliveryReceiptManager.getInstanceFor(xmppcon)
                        .removeReceiptReceivedListener(mDeliveryReportReceivedListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
