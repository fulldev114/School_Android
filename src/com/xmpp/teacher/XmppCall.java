package com.xmpp.teacher;

import android.content.Context;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

/**
 * Created by etech on 23/5/16.
 */
public class XmppCall {

    public static void sendDeliveryReport(XMPPConnection xmppcon, Message msgreceived) {
        DeliveryReceiptManager.getInstanceFor(xmppcon).addReceiptReceivedListener(new ReceiptReceivedListener() {
            @Override
            public void onReceiptReceived(org.jxmpp.jid.Jid fromJid, org.jxmpp.jid.Jid toJid, String receiptId, Stanza receipt) {

            }
        });
        Message ack = new Message(msgreceived.getFrom(), Message.Type.normal);
        ack.addExtension(new DeliveryReceipt(msgreceived.getPacketID()));
        try {
            xmppcon.sendStanza(ack);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static ChatStateExtension getExtension(Message packet, String namespace) {
        ChatStateExtension extension = (ChatStateExtension) packet.getExtension(namespace);
        boolean hasExtension = extension == null ? false : true;
        return extension;
    }

    public static void sendPresence(Context context, XMPPConnection connection, Presence.Type presenceType, String to_username) throws XMPPException {
        if (connection != null) {
            if (connection.isAuthenticated()) {
                Presence presence = new Presence(presenceType);
                presence.setTo(to_username);

                try {
                    connection.sendPacket(presence);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendTyping(Context context, String to_username, boolean isTyping, XMPPConnection connection) {
        try {
            if (connection != null) {
                EntityBareJid enji = JidCreate.entityBareFrom(to_username);
                Message msg = new Message(enji, Message.Type.chat);
                msg.addExtension(new TypingExtension(isTyping));
                connection.sendStanza(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
