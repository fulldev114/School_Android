package com.xmpp.teacher;


import com.adapter.teacher.Childbeans;

public interface XmppMessageListener {
	void onMessageReceived(String from, Childbeans message);

	void isTyping(String username, boolean isTyping);
}
