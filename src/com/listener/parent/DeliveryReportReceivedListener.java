package com.listener.parent;

import android.content.Context;
import android.content.Intent;

import com.adapter.parent.Childbeans;
import com.xmpp.parent.Constant;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jxmpp.jid.Jid;

public class DeliveryReportReceivedListener implements ReceiptReceivedListener {

    private Context context;
    //MessageService service;
    XMPPConnection connection;

    public DeliveryReportReceivedListener(Context context) {
        this.context=context;
    }

  /*  public DeliveryReportReceivedListener(MessageService service) {
        this.service = service;
    }*/

    @Override
    public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {
        Childbeans bean = new Childbeans();
        bean.sender_id_jid = Constant.getUserName(fromJid.toString()).toUpperCase();
        bean.receiver_id_jid = Constant.getUserName(toJid.toString()).toUpperCase();
        bean.message_id = receiptId;
        broadcastMessageDelivered(receiptId, bean);
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
}
