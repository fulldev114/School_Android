package com.listener.teacher;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.adapter.teacher.Childbeans;
import com.cloudstream.cslink.R;
import com.cloudstream.cslink.teacher.ApplicationData;
import com.cloudstream.cslink.teacher.MainActivity;

import com.cloudstream.cslink.Splash_Activity;
import com.db.teacher.DatabaseHelper;
import com.xmpp.teacher.Constant;
import com.xmpp.teacher.TypingExtension;
import com.xmpp.teacher.XMPPMethod;
import com.xmpp.teacher.XmppCall;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by etech on 23/5/16.
 */
public class InternalMessageReceivedListener implements PacketListener {
    private final Context context;
    private XMPPConnection xmppcon;
    String TAG = "InternalMessageReceivedListener";
    String parnetno = "", teachername = "";
    private Activity mactiivty = null;
    private String type;
    private boolean isactive = false;
    String messageFrom = "";

    public InternalMessageReceivedListener(Context context) {
        this.context = context;
    }

    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        xmppcon = XMPPMethod.getconnectivity();

        if (packet instanceof Message) {
            Message msgreceived = (Message) packet;

            String from_user = Constant.getUserName(msgreceived.getFrom().toString());
            String to_user = Constant.getUserName(msgreceived.getTo().toString());

            if ((msgreceived.getBody() != null) && !msgreceived.getBody().equalsIgnoreCase("")) {
                boolean messageavailable = false;
                try {

                    if (DeliveryReceiptManager.hasDeliveryReceiptRequest(msgreceived)) {
                        XmppCall.sendDeliveryReport(xmppcon, msgreceived);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Childbeans newMessage = getmessagefromstanza(msgreceived, packet, true, false);
                //prevent duplicate message
                if (!teachername.equalsIgnoreCase("1") && !teachername.equalsIgnoreCase("2") && !teachername.equalsIgnoreCase("3")) {
                    type = "teacher-teacher";
                    if (ApplicationData.Internalduplicatemessage != null && ApplicationData.Internalduplicatemessage.size() > 0) {
                        for (int msgid = 0; msgid < ApplicationData.Internalduplicatemessage.size(); msgid++) {
                            if (ApplicationData.Internalduplicatemessage.get(msgid).message_id.equalsIgnoreCase(newMessage.message_id)) {
                                messageavailable = true;
                                break;
                            }
                        }
                    } else if (ApplicationData.Internalduplicatemessage != null) {
                        messageavailable = false;
                    }

                    if (!messageavailable) {
                        ApplicationData.Internalduplicatemessage.add(newMessage);
                        if (!ApplicationData.ignorbadge) {
                            handlemessage(msgreceived, from_user, newMessage);
                        }
                    }
                }

            } else if (msgreceived.getType().equals(Message.Type.chat)) {

                ChatStateExtension extension = XmppCall.getExtension(msgreceived, TypingExtension.NAMESPACE);
                if (ApplicationData.receiver_jid.equalsIgnoreCase(msgreceived.getFrom().toString().contains("/") ? ApplicationData.getjid(msgreceived.getFrom().toString().toUpperCase()) : msgreceived.getFrom().toString())) {
                    if (extension != null) {
                        String element = extension.getElementName();
                        if (element != null) {

                            boolean isTyping = false;
                            String username = msgreceived.getFrom().toString().split("@")[0];

                            if (element.equalsIgnoreCase(TypingExtension.ELEMENT_GONE)) {
                                isTyping = false;
                            } else if (element
                                    .equalsIgnoreCase(TypingExtension.ELEMENT_COMPOSING)) {
                                isTyping = true;
                            } else if (element.equalsIgnoreCase(TypingExtension.ElEMENT_PAUSE)) {
                                isTyping = false;
                            }

                            Childbeans newMessage = new Childbeans();
                            newMessage.message = msgreceived.getBody();
                            newMessage.messageTimeMilliseconds = 00;
                            newMessage.messageType = Constant.MESSAGE_TYPE_RECEIVED;
                            newMessage.message_id = packet.getPacketID();
                            newMessage.username = Constant.getUserName(msgreceived.getFrom().toString()).toUpperCase();
                            newMessage.receiver_name = Constant.getUserName(msgreceived.getTo().toString()).toUpperCase();
                            newMessage.receiver_id_jid = Constant.getUserName(msgreceived.getTo().toString()).toUpperCase();
                            newMessage.sender_id_jid = Constant.getUserName(msgreceived.getFrom().toString()).toUpperCase();
                            newMessage.typing = isTyping;

                            broadcastMessageReceived(from_user, newMessage);
                        }
                    }
                }
            }
        } else {

        }
    }

    private void handlemessage(Message msgreceived, String from_user, Childbeans newMessage) {
        ApplicationData.getMainActivity();
        mactiivty = null;
        if (ApplicationData.getChatActivity() != null)
            isactive = true;

        if (ApplicationData.getInternalActivity() != null) {
            mactiivty = ApplicationData.getInternalActivity();
            isactive = false;
        }
        if (msgreceived.getBody() != null && msgreceived.getBody().length() > 0 && !msgreceived.getBody().equalsIgnoreCase("")) {
            if (messageFrom.equals("username")) {
                type = "teacher-teacher";
                if (mactiivty != null && (!isactive)) {
                    SharedPreferences sharedpref = mactiivty.getSharedPreferences(Constant.USER_FILENAME, 0);
                    String sender_jid = sharedpref.getString("jid", "");
                    String teacher_id = sharedpref.getString("teacher_id", "");
                    if (ApplicationData.receiver_jid.equalsIgnoreCase(msgreceived.getFrom().toString().contains("/") ? ApplicationData.getjid(msgreceived.getFrom().toString()) : msgreceived.getFrom().toString()))
                        broadcastMessageReceived(from_user, newMessage);
                    else {
                        DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(mactiivty);
                        db.insertchatbadge(newMessage, teacher_id, type);
                        MainActivity.sendchatbadge();
                        broadcastbadgereceive(teacher_id, newMessage);
                        localnotification(mactiivty, newMessage);
                    }
                } else {
                    Activity mactive = null;
                    if (ApplicationData.getChatActivity() != null)
                        mactive = ApplicationData.getChatActivity();
                    else if (ApplicationData.getMainActivity() != null)
                        mactive = ApplicationData.getMainActivity();
                    else
                        mactive = ApplicationData.getMainActivity();

                    if (mactive != null) {
                        SharedPreferences sharedpref = mactive.getSharedPreferences(Constant.USER_FILENAME, 0);
                        String teacher_id = sharedpref.getString("teacher_id", "");
                        String sender_jid = sharedpref.getString("jid", "");
                        DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(mactive);
                        db.insertchatbadge(newMessage, teacher_id, type);
                        MainActivity.sendchatbadge();
                        if (sender_jid.equalsIgnoreCase(msgreceived.getTo().toString().contains("/") ? ApplicationData.getjid(msgreceived.getTo().toString()) : msgreceived.getTo().toString()))
                            broadcastbadgereceive(teacher_id, newMessage);

                        Childbeans childbeans = getdatafromservice(newMessage);
                        DatabaseHelper db1 = DatabaseHelper.getDBAdapterInstance(mactive);
                        db1.inserthistory(childbeans);
                        localnotification(mactive, newMessage);
                    }
                }
                messageFrom="";
            }
        }
    }

    private Childbeans getmessagefromstanza(Message msgreceived, Stanza packet, boolean b, boolean isTyping) {
        if (b) {
            StandardExtensionElement extTimestamp = (StandardExtensionElement) msgreceived.getExtension("urn:xmpp:extra");
            List<StandardExtensionElement> element = extTimestamp.getElements();
            for (int i = 0; i < element.size(); i++) {
                if (element.get(i).getElementName().equals("username")) {
                    teachername = element.get(i).getText() != null ? element.get(i).getText() : "";
                    messageFrom = element.get(i).getElementName();
                }
            }

        }
        Childbeans newMessage = new Childbeans();
        newMessage.message_body = msgreceived.getBody();
        newMessage.messageTimeMilliseconds = 00;
        newMessage.messageType = Constant.MESSAGE_TYPE_RECEIVED;
        newMessage.message_id = packet.getPacketID();
        newMessage.username = Constant.getUserName(msgreceived.getFrom().toString()).toUpperCase();
        newMessage.receiver_name = Constant.getUserName(msgreceived.getTo().toString()).toUpperCase();
        newMessage.receiver_id_jid = Constant.getUserName(msgreceived.getTo().toString()).toUpperCase();
        newMessage.sender_id_jid = Constant.getUserName(msgreceived.getFrom().toString()).toUpperCase();
        newMessage.typing = isTyping;

        return newMessage;
    }

    private void localnotification(Activity activity, Childbeans newMessage) {
        Intent notificationIntent = new Intent(activity, Splash_Activity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
        stackBuilder.addParentStack(Splash_Activity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);

        Notification notification = builder.setContentTitle("Demo App Notification")
                .setSmallIcon(R.drawable.icon_silhouette)
                .setContentTitle(teachername)
                .setContentText(newMessage.message_body)
                        // .setContent(contentView)
                .setAutoCancel(true)
                .setTicker("Message")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent).build();

        notification.defaults |= Notification.DEFAULT_SOUND;

        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    private void broadcastMessageReceived(String from_user, Childbeans newMessage) {
        try {
            Intent data = new Intent();
            data.putExtra("from_username", from_user);
            data.putExtra("newMessage", newMessage);
            String action = Constant.CUSTOM_INTENT_XMPP_INTERNAL;
            data.setAction(action);
            context.sendBroadcast(data);
        } catch (Exception e) {
            Log.e(InternalMessageReceivedListener.class
                    + "broadcastMsgReceivedExc", e + "");
            e.printStackTrace();
        }

    }

    private void broadcastbadgereceive(String teacher_id, Childbeans newMessage) {
        try {
            Intent data = new Intent();
            data.putExtra("childid", teacher_id);
            data.putExtra("newMessage", newMessage);
            String action = ApplicationData.BROADCAST_CHAT_INTERNAL;
            data.setAction(action);
            context.sendBroadcast(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Childbeans getdatafromservice(Childbeans newmesg) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String fmt = sdf.format(cal.getTime());
        Childbeans childbeans = new Childbeans();
        childbeans.message_id = newmesg.message_id;
        childbeans.user_id = "0";
        childbeans.name = "0";
        childbeans.image = "";
        childbeans.message_body = newmesg.message;
        childbeans.child_marked_id = "0";
        childbeans.child_moblie = ApplicationData.getMainActivity().getResources().getString(R.string.str_tmp_phone);
        childbeans.created_at = fmt;
        childbeans.sender = "from";
        childbeans.sender_id_jid = newmesg.sender_id_jid.toUpperCase();
        childbeans.receiver_id_jid = newmesg.receiver_id_jid.toUpperCase();
        childbeans.message_status = "Received";
        childbeans.receiver_image = "";
        childbeans.sender_id = "";
        childbeans.parent_id = "";
        childbeans.receiver_name = newmesg.receiver_name;
        childbeans.parenttype = "";

        return childbeans;
    }
}
