package com.xmpp.parent;

import android.content.Context;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.carbons.packet.CarbonExtension;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.mam.element.MamElements;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

import java.util.ArrayList;

/**
 * Created by etech on 23/5/16.
 */
public class XmppCall {

    public static ChatStateExtension getExtension(Message packet, String namespace) {
        ChatStateExtension extension = (ChatStateExtension) packet.getExtension(namespace);
       // boolean hasExtension = extension == null ? false : true;
        return extension;
    }

    public static MamElements.MamResultExtension getMAMExtension(Message packet, String namespace) {
        MamElements.MamResultExtension extension = (MamElements.MamResultExtension) packet.getExtension(namespace);
       // boolean hasExtension = extension == null ? false : true;
        return extension;
    }

    public static CarbonExtension getCarbonExtension(Message packet, String namespace) {
        CarbonExtension extension = (CarbonExtension) packet.getExtension(namespace);
       // boolean hasExtension = extension == null ? false : true;
        return extension;
    }


    public static void sendPresence(Context context, XMPPConnection connection,
                                    Presence.Type presenceType, String to_username) throws XMPPException {
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
                String jid = to_username;
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
