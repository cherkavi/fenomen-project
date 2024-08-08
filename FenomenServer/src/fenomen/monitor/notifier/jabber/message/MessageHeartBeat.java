package fenomen.monitor.notifier.jabber.message;

import fenomen.monitor.web_service.common.XmlMessage;

public class MessageHeartBeat extends XmlMessage{

	public MessageHeartBeat() {
		super(XmlMessage.typeHeartBeat);
	} 

}
