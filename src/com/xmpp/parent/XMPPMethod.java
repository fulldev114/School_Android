package com.xmpp.parent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.R;
import com.adapter.parent.Childbeans;
import com.common.SharedPreferFile;
import com.common.utils.GlobalConstrants;
import com.db.parent.DatabaseHelper;
import com.listener.parent.XMPPListener;
import com.xmpp.parent.history.MamHistory;

import org.jivesoftware.smack.AbstractConnectionClosedListener;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.UnparseableStanza;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StreamError;
import org.jivesoftware.smack.parsing.ParsingExceptionCallback;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.dns.HostAddress;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

//import org.jivesoftware.smack.packet.DefaultPacketExtension;

/**
 * Created by etech on 19/5/16.
 */
public class XMPPMethod {
    private static SSLContext sslctx = null;
    // private static XMPPTCPConnection connection;
    private static XMPPConnection xmppcon;
    private static boolean flag = false;
    private static String TAG = "xmppmethod";
    private static AbstractXMPPConnection conn2;
    // private static AbstractXMPPConnection conn2;
    private ConnectionConfiguration config;

    static StrictMode.ThreadPolicy thmode = new StrictMode.ThreadPolicy.Builder().permitAll().build();
  /*  public static void createaccount(Intent data) {
        AccountManager am = AccountManager.getInstance(xmppcon);
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", data.getStringExtra("unm"));
        map.put("password", data.getStringExtra("pwd"));

        try {
            am.createAccount(data.getStringExtra("unm"), data.getStringExtra("pwd"), map);
        } catch (XMPPException e) {
            Log.e("Cannot create new user", "0");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e(" not logged in.", "0");
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }
    }*/


    public static boolean connect(final Context context, final String host, final String port, final String pwd) throws Exception {

        try {

            StrictMode.setThreadPolicy(thmode);
            Resources res = context.getResources();
            int bks_version;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                bks_version = R.raw.cskeystore; //The BKS file
            } else {
                bks_version = R.raw.cskeystore_oldversion; //The BKS (v-1) file
            }
            InputStream in = context.getResources().openRawResource(bks_version);
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(in, Constant.keystore_pwd);

            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(
                                X509Certificate[] certs,
                                String authType) {
                        }

                        public void checkServerTrusted(
                                X509Certificate[] certs,
                                String authType) {
                        }
                    }
            };
            tmf.init(ks);
            sslctx = SSLContext.getInstance("TLS");
            sslctx.init(null, trustAllCerts, new SecureRandom());


        } catch (KeyStoreException e) {
            e.printStackTrace();
            Log.e("keystore : : ", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.e("Algorithm : : ", e.getMessage());
        } catch (KeyManagementException e) {
            e.printStackTrace();
            Log.e("Management : : ", e.getMessage());
        }//        XMPPConnectionListener connectionListener = new XMPPConnectionListener(ActivityIMChat.this);
        catch (CertificateException e) {
            e.printStackTrace();
            Log.e("Certification : : ", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception : : ", e.getMessage());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(Calendar.getInstance().getTime());

//        connectionListener.connected(connection);
        SmackConfiguration.setDefaultPacketReplyTimeout(100000);

        try {
            DomainBareJid serviceName = JidCreate.domainBareFrom(host.substring(host.lastIndexOf("@") + 1, host.length()));
            XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();

            config.setUsernameAndPassword(host.substring(0, host.lastIndexOf("@")), pwd)
                    .setXmppDomain(serviceName)//("hosted.im")
                    .setHost(host.substring(host.lastIndexOf("@") + 1, host.length()))//("etechlocal.p1.im")//
                    .setResource(ApplicationData.getUniqId(context))
                    .setDebuggerEnabled(true)
                    .setPort(Integer.parseInt(port))
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setCustomSSLContext(sslctx)
                            // .allowEmptyOrNullUsernames()
                    .build();
            SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");

            XMPPTCPConnection.setUseStreamManagementResumptionDefault(true);
            XMPPTCPConnection.setUseStreamManagementDefault(true);

            conn2 = new XMPPTCPConnection(config.build());
            xmppcon = conn2;
            conn2.connect();
            final Childbeans xm = new Childbeans();
            xm.servicename = host.substring(host.lastIndexOf("@") + 1, host.length());

            conn2.setParsingExceptionCallback(new ParsingExceptionCallback() {
                @Override
                public void handleUnparsableStanza(UnparseableStanza stanzaData) throws Exception {
                    Log.e(TAG, "stanzadata");
                }
            });


            conn2.addConnectionListener(new ConnectionListener() {
                @Override
                public void connected(XMPPConnection connection) {
                    flag = true;
                    xmppcon = connection;
                }

                @Override
                public void authenticated(XMPPConnection connection, boolean resumed) {

                    flag = true;

                    XMPPListener listener = new XMPPListener(context);
                    listener.initializeListeners();

                    ApplicationData.ignorbadge = true;

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Handler handle = new Handler();
                            handle.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ApplicationData.ignorbadge = false;
                                }
                            }, 2000);
                        }
                    });

                    try {
                        if (xmppcon != null) {
                            if (xmppcon.isConnected() && xmppcon.isAuthenticated()) {
                                if (CarbonManager.getInstanceFor(xmppcon).isSupportedByServer()) {
                                    Log.i(TAG, "Message Carbons Supported ");
                                    CarbonManager.getInstanceFor(xmppcon).enableCarbons();
                                }
                            }
                        }
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //  receiveofflinemessage(context, host);
                   /* SharedPreferences myPrefs = context.getSharedPreferences("absentapp", Context.MODE_PRIVATE);

                    if (myPrefs.getString("offlinetag", "0").equals("0"))*/
                }

                @Override
                public void connectionClosed() {
                    //   flag=false;
                }

                @Override
                public void connectionClosedOnError(Exception e) {
                    if (e instanceof XMPPException.StreamErrorException) {
                        XMPPException.StreamErrorException xmppEx = (XMPPException.StreamErrorException) e;
                        StreamError error = xmppEx.getStreamError();
                        String reason = error.getConditionText();
                        Log.d(TAG, "reason : " + reason);
                        if ("conflict".equals(reason)) {
                            return;
                        }

                    } else {
                        Log.d(TAG, "connectionClosedOnError : " + e.getMessage());
                    }
                }


                @Override
                public void reconnectionSuccessful() {
                }

                @Override
                public void reconnectingIn(int seconds) {
                }

                @Override
                public void reconnectionFailed(Exception e) {

                }
            });
            conn2.login();

        } catch (SmackException.ConnectionException e) {
            for (int i = 0; i < e.getFailedAddresses().size(); i++) {
                HostAddress element = e.getFailedAddresses().get(i);
                Log.e("failed Address : ", element.getErrorMessage());
                Log.e("failed Address port : ", String.valueOf(element.getPort()));
            }
        } catch (SmackException | IOException | XMPPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return flag;
    }


   /* private static void Deleiverymodeon() {
        ProviderManager.addExtensionProvider(DeliveryReceipt.ELEMENT, DeliveryReceipt.NAMESPACE, new DeliveryReceipt.Provider());
        ProviderManager.addExtensionProvider(DeliveryReceiptRequest.ELEMENT, new DeliveryReceiptRequest().getNamespace(),
                new DeliveryReceiptRequest.Provider());

        DeliveryReceiptManager.getInstanceFor(connection).addReceiptReceivedListener(new ReceiptReceivedListener() {
            @Override
            public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
                Log.e("Host", fromJid);
                Log.e("Receipt", toJid);
                Log.e("ID", "PACKED GOT--" + receiptId);
            }
        });

        DeliveryReceiptManager receiveack = DeliveryReceiptManager.getInstanceFor(connection);
        receiveack.autoAddDeliveryReceiptRequests();
    }*/

    public static XMPPConnection getconnectivity() {
        if (xmppcon != null)
            if (xmppcon.isConnected() && xmppcon.isAuthenticated())
                return xmppcon;

        return null;
    }

    private static void receiveofflinemessage(final Context context, final String host) {

        final SharedPreferences sharedpref = context.getSharedPreferences("absentapp", 0);

        StanzaFilter filter = new XMPPOfflineMessage(Message.Type.chat);
        final SharedPreferFile file = new SharedPreferFile(context);
        xmppcon.addPacketListener(new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) {
                try {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        Message.Type type = message.getType();

                        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        String fmt = sdf.format(cal.getTime());

                        String fromName = Constant.getUserName(message.getFrom().toString());
                        Log.i("XMPPClient", "Got text [" + message.getBody()
                                + "] from [" + fromName + "]" + "[ type :" + type + "]");
                        if (!fromName.equalsIgnoreCase(file.getJidUser().split("@")[0])) {
                            Childbeans childbeans = new Childbeans();
                            childbeans.message_id = "0";
                            childbeans.user_id = "0";
                            childbeans.name = "0";
                            childbeans.image = "0";
                            childbeans.message_body = message.getBody();
                            childbeans.created_at = fmt;
                            childbeans.child_marked_id = "0";
                            childbeans.child_moblie = sharedpref.getString("phone", "");
                            childbeans.sender = "from";
                            childbeans.sender_id = sharedpref.getString("parent_id", "");
                            childbeans.sender_id_jid = Constant.getUserName(message.getFrom().toString().split("/")[0]);
                            childbeans.receiver_id_jid = Constant.getUserName(host);
                            childbeans.message_status = "";
                            childbeans.teacher_id = "";
                            childbeans.receiver_name = "";
                            childbeans.receiver_image = "";
                            Log.e("receive message second ", childbeans.message_body + ">>" + childbeans.receiver_id_jid);
                            //childbeans.chat_time=formattedDate;
                            DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(context);
                            db.inserthistory(childbeans);
                            Userisonline(context, host);
                            try {

                                if (DeliveryReceiptManager.hasDeliveryReceiptRequest(message)) {
                                    sendDeliveryReport(xmppcon, message);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, filter);
        SharedPreferences myPrefs = context.getSharedPreferences("absentapp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("offlinetag", "1");
        editor.commit();


    }

    public static void acceptReq(String username) {
        Presence presence = new Presence(Presence.Type.subscribe);


        try {
            if (username != null && username.length() != 0) {
                // to_username = attachServiceName(context, to_username);
                presence.setTo(JidCreate.bareFrom(username));
                xmppcon.sendPacket(presence);
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Childbeans sendmessage(final String receiver_jid, final String mesg, Activity context, boolean istyping) {
        Childbeans data = null;
        SharedPreferences myPrefs = context.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        if (xmppcon != null) {
            try {
                EntityBareJid enji = JidCreate.entityBareFrom(receiver_jid);
                Chat chat = ChatManager.getInstanceFor(xmppcon).createChat(enji, new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {

                    }
                });
                Message msg = new Message(enji, Message.Type.chat);
                msg.setBody(mesg);

                DefaultExtensionElement dfl = new DefaultExtensionElement("extra", "urn:xmpp:extra");
                dfl.setValue("parent", myPrefs.getString("parent_no", "1"));
                dfl.setValue("parentname", myPrefs.getString("childname", ""));
                msg.addExtension(dfl);

                DeliveryReceiptManager delm = DeliveryReceiptManager.getInstanceFor(xmppcon);
                delm.autoAddDeliveryReceiptRequests();
                String val = DeliveryReceiptManager.addDeliveryReceiptRequest(msg);

                chat.sendMessage(msg);
                //send typing status stop
                typingstatus(context, receiver_jid, false);

                flag = true;

                data = new Childbeans();
                data.flag = true;
                data.message_id = val;

            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return data;
    }


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
 /*   @Override
    public void processMessage(Chat chat, Message message) {


    }*/

    /*  public static ArrayList<XMPPBean> getUserList(ActivityUserList activityUserList, String host) {
          Presence presence = new Presence(Presence.Type.available);

          ArrayList<XMPPBean> alx = new ArrayList<XMPPBean>();

              Roster roster = Roster.getInstanceFor(xmppcon);
              //Get all rosters
              Collection<RosterEntry> entries = roster.getEntries();
              //loop through
              for (RosterEntry entry : entries) {
              //example: get presence, type, mode, status
                  XMPPBean xm = new XMPPBean();
                  RosterPacket.ItemType mItemType = entry.getType();
                  xm.setUsername(entry.getUser());
                  xm.setName(entry.getName());
                  Log.e("uuuu::: ", xm.getUsername() + ":::" + xm.getName());
                  Presence entryPresence = roster
                          .getPresence(entry.getUser());
                  Presence.Mode rrr = entryPresence.getMode();
                  xm.setServicename(String.valueOf(rrr));
                  Log.e("status::: ", xm.getServicename());
                  if(mItemType.equals(RosterPacket.ItemType.both))
                      alx.add(xm);
              }

          return alx;
      }

      public static boolean createaccount(XMPPBean xmppBean) {
          boolean flag=true;
          AccountManager am = AccountManager.getInstance(xmppcon);
          Map<String, String> map = new HashMap<String, String>();
          map.put("username", xmppBean.getUsername());
          map.put("password", xmppBean.getPassword());

          try{
              am.createAccount( xmppBean.getUsername(), xmppBean.getPassword(), map);
          } catch (XMPPException e) {
              Log.e("Cannot create new user", "0");
              flag=false;
              e.printStackTrace();
          } catch (IllegalStateException e) {
              Log.e(" not logged in.", "0");
              flag=false;
              e.printStackTrace();
          } catch (SmackException.NotConnectedException e) {
              flag=false;
              e.printStackTrace();
          } catch (SmackException.NoResponseException e) {
              flag=false;
              e.printStackTrace();
          }
          return flag;
      }
  */


    public static void Userisoffline(Context context, String sender_jid) {
        try {
            XmppCall.sendPresence(context, xmppcon, Presence.Type.unavailable, sender_jid);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    public static void disconnection(Context activity, String sender_jid) {
        if (xmppcon != null) {
            if (xmppcon.isAuthenticated()) {
                try {
                    XMPPListener listener = new XMPPListener(activity);
                    listener.removeListeners();

                    conn2.removeConnectionListener(new AbstractConnectionClosedListener() {
                        @Override
                        public void connectionTerminated() {
                            Log.e("connectionClosed :", "yes");
                            xmppcon = null;
                        }
                    });
                    conn2.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void Userisonline(Context context, String sender_jid) {
        try {
            if (xmppcon != null)
                XmppCall.sendPresence(context, xmppcon, Presence.Type.available, sender_jid);
            else if ((xmppcon != null && !xmppcon.isAuthenticated()) || xmppcon == null) {
                SharedPreferences myPrefs = context.getSharedPreferences("absentapp", Context.MODE_PRIVATE);
                XmppCall.sendPresence(context, xmppcon, Presence.Type.available, sender_jid);
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isavailable(Context context, String receiver_jid) {
        boolean status = false;
        if (xmppcon != null) {
            Roster roster = Roster.getInstanceFor(xmppcon);
            try {
                EntityBareJid entityjid = JidCreate.entityBareFrom(receiver_jid);
                Presence type = roster.getPresence(entityjid);
                if (String.valueOf(type.getType()).equals("available")) {
                    status = true;
                } else
                    status = false;
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }
            return status;
        }
        return status;
    }

    public static void typingstatus(Context context, String receiver_jid, boolean istyping) {
        if (xmppcon != null) {
            if (xmppcon.isConnected() && xmppcon.isAuthenticated())
                XmppCall.sendTyping(context, receiver_jid, istyping, xmppcon);
        }
    }

    public static void isconnected(Context context) throws InterruptedException {
        SharedPreferences myPrefs = context.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        if (xmppcon != null) {
            if (!xmppcon.isConnected() || !xmppcon.isAuthenticated()) {
                ConnectivityTask task = new ConnectivityTask(context, xmppcon);
                task.execute();
            }
        } else if (xmppcon == null) {
            try {
                connect(context, myPrefs.getString("jid", ""), "5222", myPrefs.getString("jid_pwd", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("offlinetag", "0");
        editor.commit();*/

    }

    public static List<Childbeans> loadHistory(final Activity activity, final String withJid) {
        List<Childbeans> history = new ArrayList<Childbeans>();
        if (GlobalConstrants.isWifiConnected(activity)) {
            if (xmppcon.isAuthenticated() && xmppcon.isConnected()) {
                MamHistory mamhistory = MamHistory.init(activity, xmppcon);
                if (mamhistory != null && xmppcon != null && xmppcon.isAuthenticated() && xmppcon.isConnected()) {
                    history = mamhistory.loadHistory(withJid);
                    if (history != null) {
                        if (history.size() >= 0) {
                            return history;
                        }
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(activity, "Please relaunch your app", Toast.LENGTH_LONG).show();
                                    // ApplicationData.showMessage(activity, "", "Please relaunch you app", activity.getResources().getString(R.string.str_ok));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                } else {

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(activity, "Connection error", Toast.LENGTH_LONG).show();
                                // ApplicationData.showMessage(activity, "", "Please relaunch you app", activity.getResources().getString(R.string.str_ok));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(activity, "Connection error", Toast.LENGTH_LONG).show();
                            // ApplicationData.showMessage(activity, "", "Please relaunch you app", activity.getResources().getString(R.string.str_ok));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        return history;
    }

    public static List<Childbeans> fetchbeforehistory(final Activity activity, final String withJid) {
        List<Childbeans> history1 = new ArrayList<Childbeans>();
        if (xmppcon != null) {
            if (xmppcon.isAuthenticated() && xmppcon.isConnected()) {
                if (GlobalConstrants.isWifiConnected(activity)) {
                    MamHistory mamhistory = MamHistory.init(activity, xmppcon);
                    if (mamhistory != null && xmppcon != null && xmppcon.isAuthenticated() && xmppcon.isConnected()) {
                        history1 = mamhistory.loadoldHistory(withJid);
                        if (history1 != null) {
                            if (history1.size() >= 0) {
                                Collections.reverse(history1);
                                return history1;
                            }
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(activity, "Connection error", Toast.LENGTH_LONG).show();
                                        // ApplicationData.showMessage(activity, "", "Connection error", activity.getResources().getString(R.string.str_ok));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                               /* try {
                                ApplicationData.showMessage(activity, "", "Error", activity.getResources().getString(R.string.str_ok));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/
                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(activity, activity.getString(R.string.no_record), Toast.LENGTH_LONG).show();
                                //  ApplicationData.showMessage(activity, "", "Connection error", activity.getResources().getString(R.string.str_ok));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                       /* try {
                            ApplicationData.showMessage(activity, "", activity.getString(R.string.no_record), activity.getResources().getString(R.string.str_ok));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                }
            }
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Toast.makeText(activity, "Connection error", Toast.LENGTH_LONG).show();
                        // ApplicationData.showMessage(activity, "", "Connection error", activity.getResources().getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return history1;
    }

    public XMPPConnection getActiveConnection() {
        return null;
    }


 /* KeyStore trusted = KeyStore.getInstance("BKS");
            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            InputStream in = getResources().openRawResource(R.raw.imappkeystore);
            try {
                // Initialize the keystore with the provided trusted certificates
                // Also provide the password of the keystore
                trusted.load(in, "bhumika".toCharArray());
            } finally {
                in.close();
            }
            // Pass the keystore to the SSLSocketFactory. The factory is responsible
            // for the verification of the server certificate.
            SSLSocketFactory sf = new SSLSocketFactory(trusted);
            // Hostname verification from certificate
            // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);*/


    private class XmppReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent data) {
            String packetid = data.getStringExtra("packetId");
            Log.e("packettttiiddddd", packetid);

        }
    }

       /* public static void sendrequest(Childbeans xm) {
        Roster roster = Roster.getInstanceFor(xmppcon);
        try {
            roster.createEntry(xm.username, xm.name, null);
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
            Log.e("not logged : ", e.getMessage());
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.e("not response : ", e.getMessage());
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.e("xmpp error : ", e.getMessage());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.e("not connected : ", e.getMessage());
        }
    }*/

    /* public static ArrayList<Childbeans> getrequest(Context applicationContext) {
         ArrayList<Childbeans> al = new ArrayList<Childbeans>();
         String host="";
         if(!connection.isConnected())
         {
             SharedPreferences sh = applicationContext.getSharedPreferences("Profile", Context.MODE_PRIVATE);
              host = (sh.getString("host", ""));
             String port = (sh.getString("port", ""));
             String pwd = (sh.getString("pwd", ""));
           //  connect((Activity)applicationContext,host,port,pwd);
         }
         Roster roster = Roster.getInstanceFor(xmppcon);
         Set<RosterEntry> request = roster.getEntries();

         Childbeans bean=null;
         for(RosterEntry rs:request)
         {
             String user=rs.getUser();
             Log.e("user ::", user);
             bean = new Childbeans();
             bean.username=user;
             bean.name=rs.getName();
             al.add(bean);
         }
         /*//*RosterEntry value = roster.getEntry(host);
      *//*  if(value!=null) {
            if(value.getName()!=null && value.getName().length()>0)
                if(value.getUser()!=null && value.getUser().length()>0)
            System.out.println("get entry of user :: " + value.getName() + "::: " + value.getUser());
        }*//*
        return al;
    }
*/
    /*public static boolean removeuser(String username) {
        Presence presence = new Presence(Presence.Type.unsubscribed);
        if (username != null && username.length() != 0) {
            // to_username = attachServiceName(context, to_username);
            presence.setTo(username);
        }
        try {
            xmppcon.sendPacket(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        boolean remove = removeEntryFromRoster(username.substring(0, username.lastIndexOf("@")));
        return remove;
    }

    public static boolean removeEntryFromRoster(String username) {
        boolean flag = false;
        Roster roster = Roster.getInstanceFor(xmppcon);

        RosterEntry entry = roster.getEntry(username + "@"
                + xmppcon.getServiceName());
        if (entry != null) {
            try {
                roster.removeEntry(entry);
                flag = true;
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }
*/
}