package fenomen.monitor.notifier.jabber.message;

import fenomen.monitor.web_service.common.XmlMessage;

public class MessageAlarm extends XmlMessage{
	public MessageAlarm(){
		super(XmlMessage.typeAlarm);
	}
	
}
