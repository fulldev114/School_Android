package com.xmpp.teacher;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.adapter.teacher.Childbeans;
import com.cloudstream.cslink.R;
import com.cloudstream.cslink.teacher.ApplicationData;
import com.cloudstream.cslink.teacher.Global;
import com.cloudstream.cslink.teacher.MainActivity;

import com.cloudstream.cslink.Splash_Activity;
import com.db.teacher.DatabaseHelper;

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
public class MessageReceivedListener implements PacketListener {
    private final Context context;
    private XMPPConnection xmppcon;
    String TAG = "MessageReceivedListener";
    String parnetno = "", phoneno = "";
    private Activity mactiivty = null;
    private String type;
    private boolean isactive = false;
    private String parentname;
    private boolean isonline = false;
    private String messageFrom="";

    public MessageReceivedListener(Context context) {
        this.context = context;
    }

    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        xmppcon = XMPPMethod.getconnectivity();

        try {

            if (packet instanceof Message) {

                Message msgreceived = (Message) packet;

                String from_user = Constant.getUserName(msgreceived.getFrom().toString());
                String to_user = Constant.getUserName(msgreceived.getTo().toString());
                if ((msgreceived.getBody() != null) && !msgreceived.getBody().equalsIgnoreCase("")) {

                    try {

                        if (DeliveryReceiptManager.hasDeliveryReceiptRequest(msgreceived)) {
                            XmppCall.sendDeliveryReport(xmppcon, msgreceived);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Childbeans newMessage = getmessagefromstanza(msgreceived, packet, true, false);
                    boolean messageavailable = false;

                    type = "teacher-parent";

                    //  Log.d(TAG,newMessage.message_id+">> body >> "+newMessage.message);

                    if (parnetno != null && (parnetno.equalsIgnoreCase("1") || parnetno.equalsIgnoreCase("2") || parnetno.equalsIgnoreCase("3"))) {
                        if (ApplicationData.duplicatemessage != null && ApplicationData.duplicatemessage.size() > 0) {
                            for (int msgid = 0; msgid < ApplicationData.duplicatemessage.size(); msgid++) {
                                if (ApplicationData.duplicatemessage.get(msgid).message_id.equalsIgnoreCase(newMessage.message_id)) {
                                    messageavailable = true;
                                    break;
                                }
                            }
                        } else if (ApplicationData.duplicatemessage != null) {
                            messageavailable = false;
                        }

                        if (!messageavailable) {
                            ApplicationData.duplicatemessage.add(newMessage);
                            if (!ApplicationData.ignorbadge) {
                                handlemessage(msgreceived, Constant.getUserName(msgreceived.getFrom().toString()), newMessage);
                            }
                        }
                    }
                } else if (msgreceived.getType().equals(Message.Type.chat)) {

                    ChatStateExtension extension = XmppCall.getExtension(msgreceived, TypingExtension.NAMESPACE);
                    if (ApplicationData.receiver_jid.equalsIgnoreCase(msgreceived.getFrom().toString().contains("/") ? ApplicationData.getjid(msgreceived.getFrom().toString().toUpperCase()) : msgreceived.getFrom().toString().toUpperCase())) {
                        if (extension != null) {
                            String element = extension.getElementName();
                            if (element != null) {
                                boolean isTyping = false;

                                if (element.equalsIgnoreCase(TypingExtension.ELEMENT_GONE)) {
                                    isTyping = false;
                                } else if (element
                                        .equalsIgnoreCase(TypingExtension.ELEMENT_COMPOSING)) {
                                    isTyping = true;
                                } else if (element.equalsIgnoreCase(TypingExtension.ElEMENT_PAUSE)) {
                                    isTyping = false;
                                }

                                Childbeans newMessage = new Childbeans();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlemessage(Message msgreceived, String from_user, Childbeans newMessage) {
        isonline = false;
        if (ApplicationData.getInternalActivity() != null) {
            isactive = true;
            isonline = true;
        }
        if (ApplicationData.getChatActivity() != null) {
            mactiivty = ApplicationData.getChatActivity();
            isactive = false;
            isonline = true;
        }
        if (msgreceived.getBody() != null && msgreceived.getBody().length() > 0 && !msgreceived.getBody().equalsIgnoreCase("")) {

            if (parnetno != null && parnetno.length() > 0 && !parnetno.equalsIgnoreCase("") && messageFrom.equals("parent")) {
                if (mactiivty != null && (!isactive)) {
                    type = "teacher-parent";
                    SharedPreferences sharedpref = mactiivty.getSharedPreferences(Constant.USER_FILENAME, 0);
                    String sender_jid = sharedpref.getString("jid", "");
                    String childid = sharedpref.getString("teacher_id", "");
                    if (ApplicationData.receiver_jid.equalsIgnoreCase(msgreceived.getFrom().toString().contains("/") ? ApplicationData.getjid(msgreceived.getFrom().toString()) : msgreceived.getFrom().toString())
                            && isonline)
                        broadcastMessageReceived(from_user, newMessage);
                    else {
                        DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(mactiivty);
                        db.insertchatbadge(newMessage, childid, type);
                        MainActivity.sendchatbadge();
                        broadcastbadgereceive(childid, newMessage);
                        localnotification(mactiivty, newMessage);
                    }
                } else {
                    type = "teacher-parent";
                    Activity mactive = null;
                //    if (!isactive)
                        if (ApplicationData.getInternalActivity() != null)
                            mactive = ApplicationData.getInternalActivity();
                        else if (ApplicationData.getMainActivity() != null)
                            mactive = ApplicationData.getMainActivity();
                        else
                            mactive = ApplicationData.getMainActivity();

                    if (mactive != null) {
                        SharedPreferences sharedpref = mactive.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
                        String childid = sharedpref.getString("teacher_id", "");
                        String sender_jid = sharedpref.getString("jid", "");
                        DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(mactive);
                        db.insertchatbadge(newMessage, childid, type);
                        if (sender_jid.equalsIgnoreCase(msgreceived.getTo().toString().contains("/") ? ApplicationData.getjid(msgreceived.getTo().toString()) : msgreceived.getTo().toString()))
                            broadcastbadgereceive(childid, newMessage);

                        Childbeans childbeans = getdatafromservice(newMessage);
                        DatabaseHelper db1 = DatabaseHelper.getDBAdapterInstance(mactive);
                        db1.inserthistory(childbeans);
                        MainActivity.sendchatbadge();
                        localnotification(mactive, newMessage);
                    }
                }
                messageFrom="";
            }
        }

    }

    private void broadcastMessageReceived(String from_user, Childbeans newMessage) {
        try {
            Intent data = new Intent();
            data.putExtra("from_username", from_user);
            data.putExtra("newMessage", newMessage);
            String action = Constant.CUSTOM_INTENT_XMPP;
            data.setAction(action);
            context.sendBroadcast(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastbadgereceive(String childid, Childbeans newMessage) {
        try {
            Intent data = new Intent();
            data.putExtra("childid", childid);
            data.putExtra("newMessage", newMessage);
            String action = ApplicationData.BROADCAST_CHAT;
            data.setAction(action);
            context.sendBroadcast(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Childbeans getmessagefromstanza(Message msgreceived, Stanza packet, boolean b, boolean isTyping) {

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

        if (b) {
            StandardExtensionElement extTimestamp = (StandardExtensionElement) msgreceived.getExtension("urn:xmpp:extra");
            List<StandardExtensionElement> element = extTimestamp.getElements();

            if (element.size() > 1) {
                parnetno = element.get(0).getText() != null ? element.get(0).getText() : "";
                newMessage.parenttype = parnetno;
                parentname = element.get(1).getText() != null ? element.get(1).getText() : "";
                messageFrom = element.get(0).getElementName();
            }
        }
        return newMessage;
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
        childbeans.created_at = fmt;
        childbeans.sender = "from";
        childbeans.sender_id_jid = newmesg.sender_id_jid.toUpperCase();
        childbeans.receiver_id_jid = newmesg.receiver_id_jid.toUpperCase();
        childbeans.message_status = "Received";
        childbeans.receiver_image = "";
        childbeans.sender_id = "";
        childbeans.parent_id = "";
        childbeans.receiver_name = newmesg.receiver_name;
        childbeans.child_moblie = "-------";
        childbeans.parenttype = newmesg.parenttype;

        return childbeans;
    }

    private void localnotification(Activity activity, Childbeans newMessage) {

        if(Global.logarr!=null)
        Global.logarr.add("local Push Message => "+"message : "+newMessage.message+",parentname : "+parentname);

        Intent notificationIntent = new Intent(activity, Splash_Activity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
        stackBuilder.addParentStack(Splash_Activity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);

        Notification notification = builder.setContentTitle(activity.getString(R.string.app_name))
                .setSmallIcon(R.drawable.icon_silhouette)
                .setContentTitle(parentname)
                .setContentText(newMessage.message)
                        // .setContent(contentView)
                .setAutoCancel(true)
                .setTicker("Message")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent).build();

        notification.defaults |= Notification.DEFAULT_SOUND;

        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

}
