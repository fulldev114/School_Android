package com.listener.teacher;

import android.content.Context;
import android.content.Intent;

import com.adapter.teacher.Childbeans;
import com.cloudstream.cslink.teacher.ApplicationData;
import com.xmpp.teacher.Constant;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jxmpp.jid.Jid;

public class DeliveryReportReceivedListener implements ReceiptReceivedListener {

    private final Context context;

    public DeliveryReportReceivedListener(Context context) {
        this.context=context;
    }
    @Override
    public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {
        Childbeans bean = new Childbeans();
        bean.sender_id_jid = Constant.getUserName(fromJid.toString()).toUpperCase();
        bean.receiver_id_jid = Constant.getUserName(toJid.toString()).toUpperCase();
        bean.message_id = receiptId;

        if (ApplicationData.getChatActivity() != null)
            broadcastMessageDelivered(receiptId, bean);
        else if (ApplicationData.getInternalActivity() != null)
            broadcastInternalMessageDelivered(receiptId, bean);
    }


    protected void broadcastMessageDelivered(String packetId, Childbeans bean) {
        try {
            Intent data = new Intent();
            data.putExtra("messageId", packetId);
            data.putExtra("newMessage", bean);
            String action = Constant.CUSTOM_INTENT_XMPP_MESSAGE_DELIVERED;
            data.setAction(action);
            context.sendBroadcast(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void broadcastInternalMessageDelivered(String packetId, Childbeans bean) {
        try {
            Intent data = new Intent();
            data.putExtra("messageId", packetId);
            data.putExtra("newMessage", bean);
            String action = Constant.CUSTOM_INTENT_XMPP_MESSAGE_DELIVERED_INTERNAL;
            data.setAction(action);
            context.sendBroadcast(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
