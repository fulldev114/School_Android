package com.xmpp.teacher;


import org.jivesoftware.smack.filter.FlexibleStanzaTypeFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;


/**
 * Created by etech on 25/5/16.
 */


class XMPPOfflineMessage extends FlexibleStanzaTypeFilter<Message> {

    public static final StanzaFilter NORMAL = new XMPPOfflineMessage(Message.Type.normal);
    public static final StanzaFilter CHAT = new XMPPOfflineMessage(Message.Type.chat);
    public static final StanzaFilter GROUPCHAT = new XMPPOfflineMessage(Message.Type.groupchat);
    public static final StanzaFilter HEADLINE = new XMPPOfflineMessage(Message.Type.headline);
    public static final StanzaFilter ERROR = new XMPPOfflineMessage(Message.Type.error);
    public static final StanzaFilter NORMAL_OR_CHAT = new OrFilter(NORMAL, CHAT);
    public static final StanzaFilter NORMAL_OR_CHAT_OR_HEADLINE = new OrFilter(NORMAL_OR_CHAT,
            HEADLINE);

    private final Message.Type type;


/**
     * Creates a new message type filter using the specified message type.
     *
     * @param type the message type.
     */

    protected XMPPOfflineMessage(Message.Type type) {
        super(Message.class);
        this.type = type;
    }

    @Override
    protected boolean acceptSpecific(Message message) {
        return message.getType() == type;
    }
}

