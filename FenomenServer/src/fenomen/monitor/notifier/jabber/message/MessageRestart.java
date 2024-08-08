package fenomen.monitor.notifier.jabber.message;

import fenomen.monitor.web_service.common.XmlMessage;

public class MessageRestart extends XmlMessage{
	public MessageRestart(){
		super(XmlMessage.typeRestart);
	}
}
