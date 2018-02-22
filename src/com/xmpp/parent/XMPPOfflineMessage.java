package com.xmpp.parent;

//import org.jivesoftware.smack.filter.FlexiblePacketTypeFilter;
import org.jivesoftware.smack.filter.FlexibleStanzaTypeFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by etech on 25/5/16.
 */
public class XMPPOfflineMessage extends FlexibleStanzaTypeFilter<Message> {

    private final Message.Type type;

    public static final StanzaFilter NORMAL =  new XMPPOfflineMessage(Message.Type.normal);
    public static final StanzaFilter CHAT =  new XMPPOfflineMessage(Message.Type.chat);
    public static final StanzaFilter GROUPCHAT =  new XMPPOfflineMessage(Message.Type.groupchat);
    public static final StanzaFilter HEADLINE =  new XMPPOfflineMessage(Message.Type.headline);
    public static final StanzaFilter ERROR =  new XMPPOfflineMessage(Message.Type.error);
    public static final StanzaFilter NORMAL_OR_CHAT =  new OrFilter(NORMAL, CHAT);
    public static final StanzaFilter NORMAL_OR_CHAT_OR_HEADLINE = new OrFilter(NORMAL_OR_CHAT,
            HEADLINE);
    /**
     * Creates a new message type filter using the specified message type.
     *
     * @param type the message type.
     */
    public XMPPOfflineMessage(Message.Type type) {
        super(Message.class);
        this.type = type;
    }

    @Override
    protected boolean acceptSpecific(Message message) {
        return message.getType() == type;
    }


}
/*

 public static final PacketFilter NORMAL = new XMPPOfflineMessage(Message.Type.normal);
    public static final PacketFilter CHAT = new XMPPOfflineMessage(Message.Type.chat);
    public static final PacketFilter GROUPCHAT = new XMPPOfflineMessage(Message.Type.groupchat);
    public static final PacketFilter HEADLINE = new XMPPOfflineMessage(Message.Type.headline);
    public static final PacketFilter ERROR = new XMPPOfflineMessage(Message.Type.error);
    public static final PacketFilter NORMAL_OR_CHAT = new OrFilter(NORMAL, CHAT);
    public static final PacketFilter NORMAL_OR_CHAT_OR_HEADLINE = new OrFilter(NORMAL_OR_CHAT,
            HEADLINE);

    private final Message.Type type;

    /**
     * Creates a new message type filter using the specified message type.
     *
     * @param type the message type.

public XMPPOfflineMessage(Message.Type type) {
    super(Message.class);
    this.type = type;
}

    @Override
    protected boolean acceptSpecific(Message message) {
        return message.getType() == type;
    }
    */
