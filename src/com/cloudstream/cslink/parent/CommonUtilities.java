package com.cloudstream.cslink.parent;
 
import android.content.Context;
import android.content.Intent;
 
public final class CommonUtilities {
     
    // give your server registration url here
   static final String SERVER_URL = "http://54.169.121.210/yay/push_message.php";
    //static final String SERVER_URL = "http://innosolve.net/chattingApp/push_test.php";
    // Google project id
    public static final String SENDER_ID = "410334804901";//526105049726 (new working)
    
   
 
    /**968648262445
     * Tag used on log messages.
     */
    static final String TAG = "CSlink";
 
    public static final String DISPLAY_MESSAGE_ACTION =
            "com.absents.apps.DISPLAY_MESSAGE";
 
    public static final String EXTRA_MESSAGE = "message";
 
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}