package com.xmpp.teacher;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by etech on 23/5/16.
 */
public class Constant {
    public static final String CUSTOM_INTENT_XMPP = "com.admin.apps.customIntents.xmpp";
    public static final String CUSTOM_INTENT_XMPP_INTERNAL="com.admin.apps.customIntents.xmpp.internal";
    public static final int MESSAGE_TYPE_RECEIVED = 202;
    public static final char[] keystore_pwd="mysecret".toCharArray();
    public static final String USER_FILENAME = "adminapp";
    public static final String XMPP_FILENAME="Xmpp_host";
    public static final String EMERGENCY_FILENAME = "emeregency";
    public static final String Update_Message="message";
    public static final String ForceUpdate="forceUpdateApp";
    public static final String IsVersionDifferent="isVersionDifferent";
    public static final String URL="URL";
    public static final String Skipbuttontitle="skipbuttontitle";
    public static final String Updatebuttontitle="updatebuttontitle";
    public static final String Current_version_app="app_version";
    public static final String CUSTOM_INTENT_XMPP_MESSAGE_DELIVERED = "com.admin.app.customIntents.xmpp.messageDelivered";
    public static final String CUSTOM_INTENT_XMPP_MESSAGE_DELIVERED_INTERNAL = "com.admin.app.customIntents.xmpp.messageDelivered.internal";
    // public static final String servicename="@192.168.1.128";
    public static boolean iscomplete=true;
    public static String rmssetTime;

    public static String getUserName(String user_jid) {
        if(user_jid==null && user_jid.length()==0)
            return null;

            String username = user_jid.split("@")[0];
        return username;
    }

    public static enum STATE {
        CONNECTED, DISCONNECTED, CONNECTING
    }

}
