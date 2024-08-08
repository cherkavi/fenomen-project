package fenomen.monitor.menu_notifier.jabber.message_table;

import fenomen.monitor.web_service.common.XmlMessage;

/** панель, которая отображает событие, поступившее от сервера  */
public class MessageElement{
	private final static long serialVersionUID=1L;
	/** данные, которые были получены от сервера  */
	private XmlMessage data;
	/** флаг отображения того, что данный элемент находится в состоянии ожидания ответа от сервера  */
	private boolean process;
	
	/** панель, которая отображает событие, поступившее от сервера  
	 * @param xmlMessage - событие, которое поступило от сервера 
	 */
	public MessageElement(XmlMessage xmlMessage){
		this.data=xmlMessage;
	}
	
	/** получить объект, содержащий необходимые данные */
	public XmlMessage getData(){
		return this.data;
	}

	/** флаг отображения того, что данный элемент находится в состоянии ожидания ответа от сервера  */
	public boolean isProcess() {
		return process;
	}

	/** флаг отображения того, что данный элемент находится в состоянии ожидания ответа от сервера  */
	public void setProcess(boolean process) {
		this.process = process;
	}
	
	
}
