package com.xmpp.parent;

/**
 * Created by etech on 23/5/16.
 */
public class Constant {
    public static final String CUSTOM_INTENT_XMPP = "com.absents.app.customIntents.xmpp";
    public static final int MESSAGE_TYPE_RECEIVED = 202;
    public static final char[] keystore_pwd="mysecret".toCharArray();
    public static final String USER_FILENAME = "absentapp";
    public static final String EMERGENCY_FILENAME = "emeregency";
    public static final String XMPP_FILENAME="Xmpp_host";
    public static final String Update_Message="message";
    public static final String ForceUpdate="forceUpdateApp";
    public static final String IsVersionDifferent="isVersionDifferent";
    public static final String URL="URL";
    public static final String Skipbuttontitle="skipbuttontitle";
    public static final String Updatebuttontitle="updatebuttontitle";
    public static final String Current_version_app="app_version";
    public static final String CUSTOM_INTENT_XMPP_MESSAGE_DELIVERED = "com.absent.app.customIntents.xmpp.messageDelivered";
    public static final String SELECT_CHILD ="com.absent.app.fragmenthome" ;
    public static boolean iscomplete=true;
    public static String rmssetTime;

    public static String getUserName(String user_jid) {
        if(user_jid==null && user_jid.length()==0)
            return null;

            String username = user_jid.split("@")[0];
        return username;
    }


    /*public static String encrypt(String jidUser) {
            jidUser=jidUser.replace(servicename,"csapp");
        return jidUser;
    }

    public static String decrypt(String sender_jid) {
        sender_jid=sender_jid.replace("csapp",servicename);
        return sender_jid;
    }*/
}
