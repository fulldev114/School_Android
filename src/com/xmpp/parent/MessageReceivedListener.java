package com.xmpp.parent;

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

import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.Global;
import com.cloudstream.cslink.parent.MainActivity;
import com.cloudstream.cslink.R;
import com.cloudstream.cslink.parent.Splash_Activity;
import com.adapter.parent.Childbeans;
import com.db.parent.DatabaseHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.carbons.packet.CarbonExtension;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

//import org.jivesoftware.smack.packet.DefaultPacketExtension;

/**
 * Created by etech on 23/5/16.
 */
public class MessageReceivedListener implements StanzaListener {
    private Context context;
    //MessageService service;
    private XMPPConnection xmppcon;
    boolean isTyping = false;
    String TAG = "MessageReceivedListener";
    private String teacher_name;

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
                            XMPPMethod.sendDeliveryReport(xmppcon, msgreceived);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(MessageReceivedListener.class
                                + " :: proPacket-Exception", e + "");
                    }
                    boolean messageavailable = false;
                    //get value from stanza
                    Childbeans newMessage = getmessagefromstanza(msgreceived, packet, true, false);

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
                            if (!teacher_name.equalsIgnoreCase("1") && !teacher_name.equalsIgnoreCase("2") && !teacher_name.equalsIgnoreCase("3")) {
                                handlemessage(msgreceived, from_user, newMessage);
                            }
                        }
                    }

                } else if (msgreceived.getType().equals(Message.Type.chat)) {

                    ChatStateExtension extension = XmppCall.getExtension(msgreceived, TypingExtension.NAMESPACE);
                    if (extension != null) {
                        if (ApplicationData.receiver_jid.equalsIgnoreCase(msgreceived.getFrom().toString().contains("/") ? ApplicationData.getjid(msgreceived.getFrom().toString().toUpperCase()) : msgreceived.getFrom().toString())) {
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
                                newMessage.message_id = packet.getPacketID();
                                //  newMessage.setGroupChat(false);
                                newMessage.username = from_user;
                                newMessage.receiver_name = to_user;
                                newMessage.receiver_id_jid = Constant.getUserName(msgreceived.getTo().toString()).toUpperCase();
                                newMessage.sender_id_jid = Constant.getUserName(msgreceived.getFrom().toString()).toUpperCase();
                                newMessage.typing = isTyping;
                                newMessage.iscarboncopy = false;

                                broadcastMessageReceived(from_user, newMessage);
                            }
                        }
                    } else if (XmppCall.getMAMExtension(msgreceived, TypingExtension.MAMHISTORY) != null) {
                    } else if (XmppCall.getCarbonExtension(msgreceived, TypingExtension.CARBONHISTORY) != null) {
                        CarbonExtension cbe = CarbonExtension.getFrom(msgreceived);
                        if (cbe.getForwarded() != null) {
                            Stanza forward = cbe.getForwarded().getForwardedPacket();
                            Message msgreceiv = (Message) forward;

                            if (ApplicationData.receiver_jid.equalsIgnoreCase(msgreceiv.getFrom().toString().contains("/") ? ApplicationData.getjid(msgreceiv.getFrom().toString().toUpperCase()) : msgreceiv.getFrom().toString().toUpperCase())
                                    || ApplicationData.receiver_jid.equalsIgnoreCase(msgreceiv.getTo().toString().contains("/") ? ApplicationData.getjid(msgreceiv.getTo().toString().toUpperCase()) : msgreceiv.getTo().toString().toUpperCase())) {
                                Childbeans newMessage = getmessagefromstanza(msgreceiv, forward, false, true);
                                broadcastMessageReceived(from_user, newMessage);
                            }
                        }
                    } else {

                    }

                } else {

                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handlemessage(Message msgreceived, String from_user, Childbeans newMessage) {
        if (ApplicationData.getChatActivity() != null) {
            SharedPreferences sharedpref = ApplicationData.getChatActivity().getSharedPreferences(Constant.USER_FILENAME, 0);
            String sender_jid = sharedpref.getString("jid", "");
            String childid = sharedpref.getString("childid", "");
            if (ApplicationData.receiver_jid.equalsIgnoreCase(msgreceived.getFrom().toString().contains("/") ? ApplicationData.getjid(msgreceived.getFrom().toString().toUpperCase()) : msgreceived.getFrom().toString().toUpperCase())
                    || sender_jid.equalsIgnoreCase(msgreceived.getFrom().toString().contains("/") ? ApplicationData.getjid(msgreceived.getFrom().toString().toUpperCase()) : msgreceived.getFrom().toString().toUpperCase()))
                broadcastMessageReceived(from_user, newMessage);
            else {
                DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(ApplicationData.getChatActivity());
                db.insertchatbadge(newMessage, childid);
                MainActivity.sendchatbadge();
                broadcastbadgereceive(childid, newMessage);
                localnotification(ApplicationData.getChatActivity(), newMessage);


            }
        } else if (ApplicationData.getMainActivity() != null) {
            SharedPreferences sharedpref = ApplicationData.getMainActivity().getSharedPreferences(Constant.USER_FILENAME, 0);
            String childid = sharedpref.getString("childid", "");
            String sender_jid = sharedpref.getString("jid", "");
            DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(ApplicationData.getMainActivity());
            db.insertchatbadge(newMessage, childid);
            MainActivity.sendchatbadge();
            if (sender_jid.equalsIgnoreCase(msgreceived.getTo().toString().contains("/") ? ApplicationData.getjid(msgreceived.getTo().toString().toUpperCase()) : msgreceived.getTo().toString().toUpperCase()))
                broadcastbadgereceive(childid, newMessage);

            localnotification(ApplicationData.getMainActivity(), newMessage);
        }

    }

    private void localnotification(Activity activity, Childbeans newMessage) {

        if (Global.logarr != null)
            Global.logarr.add("local Push Message => " + "message : " + newMessage.message + ",teachername : " + teacher_name);

        Intent notificationIntent = new Intent(activity, Splash_Activity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
        stackBuilder.addParentStack(Splash_Activity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);

        Notification notification = builder.setContentTitle(activity.getString(R.string.app_name))
                .setSmallIcon(R.drawable.icon_silhouette)
                .setContentTitle(teacher_name)
                .setContentText(newMessage.message)
                        // .setContent(contentView)
                .setAutoCancel(true)
                .setTicker("Message")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent).build();

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
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
            Log.e(MessageReceivedListener.class
                    + "broadcastMsgReceivedExc", e + "");
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

    public Childbeans getdatafromservice(Childbeans newmesg) {
        Childbeans childbeans = new Childbeans();
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String fmt = sdf.format(cal.getTime());
        childbeans.message_id = newmesg.message_id;
        childbeans.user_id = "0";
        childbeans.name = "0";
        childbeans.image = "";
        childbeans.message_body = newmesg.message;
        childbeans.child_marked_id = "0";
        childbeans.child_moblie = "";
        childbeans.created_at = fmt;
        childbeans.sender = "from";
        childbeans.sender_id = "";
        childbeans.sender_id_jid = newmesg.sender_id_jid;
        childbeans.receiver_id_jid = newmesg.receiver_id_jid;
        childbeans.message_status = "Received";
        childbeans.teacher_id = "";
        childbeans.receiver_name = "";
        childbeans.receiver_image = "";//newmesg.receiver_name;
        childbeans.iscarboncopy = false;

        return childbeans;
    }

    private Childbeans getmessagefromstanza(Message msgreceived, Stanza packet, boolean b, boolean iscarboncopy) {
        Childbeans newMessage = new Childbeans();
        newMessage.message = msgreceived.getBody();
        newMessage.messageTimeMilliseconds = 00;
        newMessage.messageType = Constant.MESSAGE_TYPE_RECEIVED;
        newMessage.message_id = packet.getPacketID();
        //  newMessage.setGroupChat(false);
        newMessage.username = Constant.getUserName(msgreceived.getFrom().toString()).toUpperCase();
        newMessage.receiver_id_jid = Constant.getUserName(msgreceived.getTo().toString()).toUpperCase();
        newMessage.sender_id_jid = Constant.getUserName(msgreceived.getFrom().toString()).toUpperCase();
        newMessage.typing = false;
        newMessage.iscarboncopy = iscarboncopy;

        if (b) {
            StandardExtensionElement extTimestamp = (StandardExtensionElement) msgreceived.getExtension("urn:xmpp:extra");
            List<StandardExtensionElement> element = extTimestamp.getElements();
            teacher_name = element.get(0).getText() != null ? element.get(0).getText() : "";
        }
        if (iscarboncopy) {
            StandardExtensionElement extTimestamp = (StandardExtensionElement) msgreceived.getExtension("urn:xmpp:extra");
            if (extTimestamp != null) {
                List<StandardExtensionElement> element = extTimestamp.getElements();
                if (element.size() > 0) {
                    newMessage.parentno = element.get(0).getText() != null ? element.get(0).getText() : "";
                }
            }
        }

        return newMessage;
    }


}
