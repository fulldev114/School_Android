package com.xmpp.teacher;

import android.util.Log;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;


/**
 * Created by etech on 12/7/16.
 */
public class MessageListenerImpl implements MessageListener, ChatStateListener {

    @Override
    public void processMessage(Chat arg0, Message arg1) {
        System.out.println("Received message: " + arg1);
    }


    @Override
    public void processMessage(Message message) {

    }

    @Override
    public void stateChanged(Chat arg0, ChatState arg1, Message message) {
        if (ChatState.composing.equals(arg1)) {
            Log.e("Chat State", arg0.getParticipant() + " is typing..");
        } else if (ChatState.gone.equals(arg1)) {
            Log.e("Chat State", arg0.getParticipant() + " has left the conversation.");
        } else {
            Log.e("Chat State", arg0.getParticipant() + ": " + arg1.name());
        }

    }
}
