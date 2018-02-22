package com.appsbusiness.cslink;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.parent.EmeregencyPopupActivity;
import com.cloudstream.cslink.parent.FragmentImportantMessage;
import com.cloudstream.cslink.Global;
import com.cloudstream.cslink.parent.GroupMessageActivity;
import com.cloudstream.cslink.parent.MainActivity;
import com.cloudstream.cslink.parent.Pincode_activity;
import com.cloudstream.cslink.R;
import com.cloudstream.cslink.parent.Splash_Activity;
import com.adapter.parent.Childbeans;
import com.db.parent.DatabaseHelper;
import com.google.android.gcm.GCMBaseIntentService;
import com.xmpp.parent.Constant;
import com.xmpp.parent.XMPPMethod;


import static com.cloudstream.cslink.parent.CommonUtilities.SENDER_ID;

public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";
    private int badge = 0, chat_msg = 0, abi = 0, abn = 0, emb = 0, tob = 0;
    private String Childid = "";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    static Intent mainintent;

    int from_id = 0, kidid = 0;
    String type, alert, message, kidname, teachername, teacherimage, senderjid, receiverjid;

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        // ........................sharedpreferences.......................//
        SharedPreferences myPrefs = getSharedPreferences("absentapp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("registrationId", registrationId);
        editor.commit();


        /*Intent in = new Intent(context, SelectChildActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(in);*/
    }

    /**
     * Method called on device un registred
     */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
    }

    /**
     * Method called on Receiving a new message
     */
    @Override
    protected void onMessage(Context context, Intent intent) {
        SharedPreferences myPrefs = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        mainintent = intent;
        Childid = myPrefs.getString("childid", "");
        Log.d(TAG,intent.getExtras().toString());
//        if(Global.logarr!=null)
//            Global.logarr.add("GCM Push Message => "+intent.getExtras().toString());

        if (intent.hasExtra("message"))
            message = intent.getExtras().getString("message");
        if (intent.hasExtra("type"))
            type = intent.getExtras().getString("type");
        if (intent.hasExtra("kidid"))
            kidid = Integer.valueOf(intent.getExtras().getString("kidid"));
        if (intent.hasExtra("kidname"))
            kidname = intent.getExtras().getString("kidname");
        if (intent.hasExtra("alert"))
            alert = intent.getExtras().getString("alert") + "\n\n" + kidname;
        if (intent.hasExtra("from_id"))
            from_id = Integer.valueOf(intent.getExtras().getString("from_id"));
        if (intent.hasExtra("teachername"))
            teachername = intent.getExtras().getString("teachername");
        if (intent.hasExtra("image"))
            teacherimage = intent.getExtras().getString("image");
        if (intent.hasExtra("teacherjid"))
            senderjid = intent.getExtras().getString("teacherjid").toUpperCase();
        if (intent.hasExtra("kidjid"))
            receiverjid = intent.getExtras().getString("kidjid").toUpperCase();

        if (intent.hasExtra("badge"))
            badge = Integer.valueOf(intent.getExtras().getString("badge"));
        if (intent.hasExtra("abi"))
            abi = Integer.valueOf(intent.getExtras().getString("abi"));
        if (intent.hasExtra("abn"))
            abn = Integer.valueOf(intent.getExtras().getString("abn"));
        if (intent.hasExtra("emb"))
            emb = Integer.valueOf(intent.getExtras().getString("emb"));
        if (intent.hasExtra("tob"))
            tob = Integer.valueOf(intent.getExtras().getString("tob"));
        if (intent.hasExtra("chb"))
            chat_msg = Integer.valueOf(intent.getExtras().getString("chb"));
        final Message msg = new Message();
        msg.arg1 = 1;

        if (Childid != null && Childid.length() > 0 && !Childid.equalsIgnoreCase("")) {
            if (ApplicationData.getMainActivity() != null && XMPPMethod.getconnectivity() == null) {

//                if(Global.logarr!=null)
//                    Global.logarr.add("open app offline message");

                if (type.equalsIgnoreCase("emg_msg")) {
                    final MediaPlayer mp = MediaPlayer.create(ApplicationData.getMainActivity(), R.raw.alarm);
                    mp.start();
                    SharedPreferences mpreference = getSharedPreferences(Constant.EMERGENCY_FILENAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = mpreference.edit();
                    edit.putString("eme_message", message);
                    edit.putBoolean("emeregency_popup", true);
                    edit.commit();
                    SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
                    SharedPreferences.Editor editor = sharedpref.edit();
                    editor.putInt("from_pincode", 0);
                    editor.commit();

                    Intent i = new Intent(context, EmeregencyPopupActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {
                    //Activity is GroupMessageActivity
                    if (ApplicationData.getImportantActivity() != null && type.equalsIgnoreCase("abi") &&
                            Childid.equalsIgnoreCase(String.valueOf(kidid))) {
                        ((GroupMessageActivity) ApplicationData.getImportantActivity()).setadapter(alert, message, type, kidid, from_id, kidname, teachername, teacherimage);
                    } else if (ApplicationData.getAbsentactivity() != null && type.equalsIgnoreCase("abn")
                            && Childid.equalsIgnoreCase(String.valueOf(kidid))) {
                        ((FragmentImportantMessage) ApplicationData.getAbsentactivity()).setadapter(alert, message, type, kidid, from_id, kidname, teachername, teacherimage);
                    } else {
                        DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(context);
                        if (type.equalsIgnoreCase("chat_msg")) {
                            Childbeans newMessage = new Childbeans();
                            newMessage.message = message;
                            newMessage.message_id = null;
                            //  newMessage.setGroupChat(false);
                            newMessage.username = kidname;
                            newMessage.user_id = String.valueOf(kidid);
                            newMessage.teacher_id = String.valueOf(from_id);
                            newMessage.receiver_id_jid = receiverjid;
                            newMessage.sender_id_jid = senderjid;
                            newMessage.typing = false;
                            db.insertchatbadge(newMessage, String.valueOf(kidid));
                        } else {
                            db.insertbadge(alert, message, type, kidid + "", from_id + "", kidname, abi, abn, chat_msg, badge);
                            if (!context.getClass().getSimpleName().equalsIgnoreCase(Pincode_activity.class.getSimpleName())) {
                                ApplicationData.setNotiMessage(alert, message, type, kidid, from_id);
                                ApplicationData.handler.sendMessage(msg);
                            }
                        }
                        MainActivity.sendbadgetoMainActivity(alert, message, type, kidid, from_id, 0, abi, abn, chat_msg);
                    }

                }
            } else if (XMPPMethod.getconnectivity() == null) {

//                if(Global.logarr!=null)
//                    Global.logarr.add("Close app offline message");

                Childbeans newMessage = new Childbeans();
                newMessage.message = message;
                newMessage.message_id = null;
                newMessage.username = kidname;
                newMessage.user_id = String.valueOf(kidid);
                newMessage.teacher_id = String.valueOf(from_id);
                newMessage.receiver_id_jid = receiverjid;
                newMessage.sender_id_jid = senderjid;
                newMessage.typing = false;
                DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(context);
                db.updateBadge(newMessage, String.valueOf(kidid));
                db.insertbadge(alert, message, type, kidid + "", from_id + "", kidname, abi, abn, chat_msg, badge);

                sendNotification(context);
            }
        }
    }
   /* Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    final MediaPlayer mp = MediaPlayer.create(activity, defaultSoundUri);
    mp.start();
*/

    /**
     * Method called on receiving a deleted message
     */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i("checked", "Received deleted messages notification");
        // generateNotification(context, message);
    }

    /**
     * Method called on Error
     */
    @Override
    public void onError(Context context, String errorId) {
        Log.i("checked", "Received error: " + errorId);
        // displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i("checked", "Received recoverable error: " + errorId);
        // displayMessage(context,
        // getString(R.string.gcm_recoverable_error, errorId));
        return super.onRecoverableError(context, errorId);
    }

    private void sendNotification(Context context) {
        Intent intent = new Intent(context, Splash_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("noti_kidid", kidid);
        intent.putExtra("noti_type", type);
        intent.putExtra("noti_from_id", from_id);

        if (type != null && type.length() > 0) {
            if (type.equalsIgnoreCase("emg_msg")) {
                SharedPreferences mpreference = getSharedPreferences(Constant.EMERGENCY_FILENAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = mpreference.edit();
                edit.putString("eme_message", message);
                edit.putBoolean("emeregency_popup", true);
                edit.commit();
            }
        }
//		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon_silhouette)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

        if (type.equalsIgnoreCase("emg_msg")) {
            Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm);
            notificationBuilder.setSound(path);
        } else
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);

        ApplicationData.count++;
        Notification notificationn = notificationBuilder.getNotification();
        notificationManager.notify(0, notificationn);

        ApplicationData.showAppBadge(context, tob);
        // ShortcutBadger.applyCount(context, badge);
    }
}


