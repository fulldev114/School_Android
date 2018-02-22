package com.cloudstream.cslink.teacher;
 
import android.content.Context;
import android.content.Intent;
 
public final class CommonUtilities {
     
    public static final String SENDER_ID = "212510802887";//837726051114(new working)

    /**968648262445
     * Tag used on log messages.
     */
    static final String TAG = "CSadm";
 
    public static final String DISPLAY_MESSAGE_ACTION =
            "com.admin.apps.DISPLAY_MESSAGE";
 
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