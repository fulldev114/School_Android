package com.xmpp.parent.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.adapter.parent.Childbeans;
import com.db.parent.DatabaseHelper;
import com.xmpp.parent.Constant;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.forward.packet.Forwarded;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.mam.element.MamElements;
import org.jivesoftware.smackx.mam.element.MamFinIQ;
import org.jivesoftware.smackx.rsm.packet.RSMSet;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by etech on 22/8/16.
 */
public class MamHistory {


    private static MamHistory instance;
    private static MamManager mamManager;
    static Context context;
    List<Childbeans> newMessage = null;

    public static MamHistory init(Context context, XMPPConnection xmppcon) {
        if (instance == null) {
            instance = new MamHistory();
        }
        instance.context = context;
        mamManager = MamManager.getInstanceFor(xmppcon);
        return instance;
    }

    public List<Childbeans> loadHistory(String withJid) {
        try {
            if (mamManager != null) {
                if (mamManager.isSupportedByServer()) {
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    Jid receiverjid = JidCreate.bareFrom(withJid);
                    String ts = DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());


                    //pass query to fetch history
                    MamManager.MamQueryResult mamQueryResult = mamManager.pageBefore(receiverjid, ts, 10);
                    //page(dataform, rsmset);

                    //set result of history
                    List<Forwarded> forwardedMessages = mamQueryResult.forwardedMessages;
                    MamFinIQ finsetval = mamQueryResult.mamFin;
                    Constant.iscomplete = finsetval.isComplete();
                    Constant.rmssetTime = finsetval.getRSMSet().getFirst();
                    if (newMessage != null && newMessage.size() > 0)
                        newMessage.clear();
                    newMessage = new ArrayList<Childbeans>();
                    //fetch message from forwardedmessage and add into array list
                    for (int i = 0; i < forwardedMessages.size(); i++) {
                        Stanza packet = forwardedMessages.get(i).getForwardedStanza();
                        Date stamp = forwardedMessages.get(i).getDelayInformation().getStamp();

                        Message msghistory = (Message) packet;

                        newMessage.add(getmessagefromstanza(msghistory, packet, stamp));
                    }

                }
            }
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } /*catch (ParseException e) {
            e.printStackTrace();
        }*/
        return newMessage;
    }

    public List<Childbeans> loadoldHistory(String withJid) {
        try {
            Jid receiverjid = JidCreate.bareFrom(withJid);
            RSMSet rsmset = new RSMSet(10, Constant.rmssetTime, RSMSet.PageDirection.before);
            FormField field = new FormField(FormField.FORM_TYPE);
            field.setType(FormField.Type.hidden);
            field.addValue(MamElements.NAMESPACE);
            field.addValue(withJid);
            DataForm dataform = new DataForm(DataForm.Type.submit);
            dataform.addField(field);

            //pass query to fetch history
            MamManager.MamQueryResult mamQueryResult = mamManager.pageBefore(receiverjid, Constant.rmssetTime, 10);

            //set result of history
            List<Forwarded> forwardedMessages = mamQueryResult.forwardedMessages;


            MamFinIQ finsetval = mamQueryResult.mamFin;
            Constant.iscomplete = finsetval.isComplete();
            Constant.rmssetTime = finsetval.getRSMSet().getFirst();
            if (newMessage != null && newMessage.size() > 0)
                newMessage.clear();

            newMessage = new ArrayList<Childbeans>();
            //fetch message from forwardedmessage and add into array list

            for (int i = 0; i < forwardedMessages.size(); i++) {
                Stanza packet = forwardedMessages.get(i).getForwardedStanza();
                Date stamp = forwardedMessages.get(i).getDelayInformation().getStamp();
                Message msghistory = (Message) packet;
                // Constant.rmssetTime=convertlocalize(forwardedMessages.get(0).getDelayInformation().getStamp());
                newMessage.add(getmessagefromstanza(msghistory, packet, stamp));
            }
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        return newMessage;
    }

    private Childbeans getmessagefromstanza(Message msgreceived, Stanza packet, Date stamp) {
        DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(context);

        Childbeans newMessage = new Childbeans();
        newMessage.message_body = msgreceived.getBody();
        newMessage.messageTimeMilliseconds = 00;
        newMessage.messageType = Constant.MESSAGE_TYPE_RECEIVED;
        newMessage.message_id = packet.getPacketID();
        //  newMessage.setGroupChat(false);
        newMessage.username = Constant.getUserName(msgreceived.getFrom().toString()).toUpperCase();
        newMessage.receiver_id_jid = Constant.getUserName(msgreceived.getTo().toString()).toUpperCase();
        newMessage.sender_id_jid = Constant.getUserName(msgreceived.getFrom().toString()).toUpperCase();
        newMessage.typing = false;
        newMessage.message_status = db.getmessagestatus(packet.getPacketID());
        newMessage.user_id = "0";
        newMessage.name = "0";
        newMessage.image = "";
        newMessage.child_marked_id = "0";
        SimpleDateFormat ddf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            newMessage.created_at = ddf.format(stamp);
        } catch (Exception e1) {
            e1.printStackTrace();

        }
      //  newMessage.created_at = convertlocalize(stamp.toString());
        newMessage.sender = getsenderinfo(Constant.getUserName(msgreceived.getTo().toString()).toUpperCase());
        newMessage.sender_id = "";
        newMessage.teacher_id = "";
        newMessage.receiver_name = "";
        newMessage.receiver_image = "";
        newMessage.iscarboncopy = getcarboncopy(Constant.getUserName(msgreceived.getTo().toString()).toUpperCase(), msgreceived);
        newMessage.parentno = getparentno(msgreceived);
       /* if (newMessage.iscarboncopy) {

        }*/
        return newMessage;
    }

    private String getsenderinfo(String tojid) {
        String sender = "me";
        SharedPreferences prefer = context.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        String userjid = Constant.getUserName(prefer.getString("jid", ""));
        if (userjid.equalsIgnoreCase(tojid))
            sender = "from";
        else {
            sender = "me";
        }
        return sender;
    }

    private boolean getcarboncopy(String tojid, Message msgreceived) {
        boolean sender = false;
        SharedPreferences prefer = context.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        String userjid = Constant.getUserName(prefer.getString("jid", ""));
        if (userjid.equalsIgnoreCase(tojid))
            sender = false;
        else {
            StandardExtensionElement extTimestamp = (StandardExtensionElement) msgreceived.getExtension("urn:xmpp:extra");
            if (extTimestamp != null) {
                List<StandardExtensionElement> element = extTimestamp.getElements();
                if (element.size() > 0) {
                    String parentno = element.get(0).getText() != null ? element.get(0).getText() : "";
                    if (parentno.equalsIgnoreCase(prefer.getString("parent_no", ""))) {
                        sender = false;
                    } else {
                        sender = true;
                    }
                }
            }
        }
        return sender;
    }

    private String getparentno(Message msgreceived) {
        String parentno = "";
        SharedPreferences prefer = context.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);

        StandardExtensionElement extTimestamp = (StandardExtensionElement) msgreceived.getExtension("urn:xmpp:extra");
        if (extTimestamp != null) {
            List<StandardExtensionElement> element = extTimestamp.getElements();
            if (element.size() > 0) {
                parentno = element.get(0).getText() != null ? element.get(0).getText() : "";
            }
        }
        return parentno;
    }

    private String convertlocalize(String created_at) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat ddf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        try {
            created_at = sdf.format(ddf.parse(created_at));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return created_at;
    }


}
