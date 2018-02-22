package com.xmpp.parent;


import com.adapter.parent.Childbeans;

public interface XmppMessageListener {
	void onMessageReceived(String from, Childbeans message);

	void isTyping(String username, boolean isTyping);
}
