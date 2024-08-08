package fenomen.monitor.notifier.jabber.wrap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
/*
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
*/

/** объект-обертка над библиотекой Jabber */
public class JabberWrap implements PacketListener{
	private HashSet<IMessageListener> listOfListener=new HashSet<IMessageListener>();
	private HashSet<IPresenceListener> listOfPresence=new HashSet<IPresenceListener>();
	private ConnectionConfiguration connConfig ;
    private XMPPConnection connection ;
	
	/** объект-обертка над библиотекой Jabber
     * @param url - полный путь к Jabber серверу ("talk.google.com")
     * @param port порт сервера (5222)
     * @param proxy - proxy of Jabber ("gmail.com")
     * @param listeners - слушатели входящих сообщений
     */
    public JabberWrap(String url, int port, String proxy){
    	if(proxy==null){
    		connConfig = new ConnectionConfiguration(url, port);
    	}else{
    		connConfig = new ConnectionConfiguration(url, port, proxy);
    	}
    	connection=new XMPPConnection(connConfig);
    	// connection.DEBUG_ENABLED=true;
	}

    /** оповестить всех слушателей о входящем пакете  */
    private void notifyAllMessageListeners(Message message){
    	if(message!=null){
    		String from=this.getUserAddress(message.getFrom());
    		String text=message.getBody();
        	Iterator<IMessageListener> iterator=this.listOfListener.iterator();
        	while(iterator.hasNext()){
        		iterator.next().messageNotify(from, text);
        	}
    	}
    }
    
    /** добавить слушателя входящих сообщений 
     * @param listener - слушатель входящих сообщений
     * */
    public void addMessageListener(IMessageListener listener){
    	if(listener!=null){
    		this.listOfListener.add(listener);
    	}
    }
    
    /** удалить слушателя входящих сообщений 
     * @param listener - слушатель входящих сообщений
     */
    public void removeMessageListener(IMessageListener listener){
    	this.listOfListener.remove(listener);
    }
    
    /** удалить всех слушателей входящих сообщений */
    public void removeAllMessageListener(){
    	this.listOfListener.clear();
    }

    /** добавить слушателя служебных сообщений 
     * @param presenceListener - слушатель служебных сообщений
     * */
    public void addPresenceListener(IPresenceListener presenceListener){
    	if(presenceListener!=null){
    		this.listOfPresence.add(presenceListener);
    	}
    }
    
    /** удалить слушателя служебных сообщений 
     * @param presenceListener - слушатель служебных сообщений
     */
    public void removePresenceListener(IPresenceListener presenceListener){
    	this.listOfPresence.remove(presenceListener);
    }
    
    /** удалить всех слушателей служебных сообщений */
    public void removeAllPresenceListener(){
    	this.listOfPresence.clear();
    }
    
    /** проверить получателя на валидность приема от него сообщений
     * @param from - адрес получателя, которому было адресовано сообщение  
     * */
    private boolean isRecipientExists(String from){
    	// TODO сервер. проверить получателя на такого, который находится в контакт-листе (локальном )
    	// String user=this.getUserAddress(from);
    	return true;
    }

    /** соединиться с Jabber сервером  
     * @param login - логин соединения
     * @param password - пароль соединения
     * */
    public void connect(String login, String password) throws XMPPException{
    	this.connect(login, password, null, null);
    }
    
    /** соединиться с Jabber сервером  
     * @param login - логин соединения
     * @param password - пароль соединения
     * @param messageListener - слушатель входящих сообщений
     * @param presenceListener - слушатель служебных сообщений
     * @throws XMPPException - ошибка при соединении с сервером 
     */
    public void connect(String login, String password, IMessageListener messageListener, IPresenceListener presenceListener) throws XMPPException{
    	// проверить соединение, если было - разорвать
    	if(connection.isConnected()){
    		connection.disconnect();
    	}
    	// соединиться
    	connection.connect();
    	// залогиниться
    	connection.login(login, password);

    	// послать пакет соединения с сервером
    	/*Presence presence = new Presence(Presence.Type.available);
        connection.sendPacket(presence);*/
        
        connection.removePacketListener(this);
        connection.addPacketListener(this, new PacketFilter(){
			@Override
			public boolean accept(Packet packet) {
				return isRecipientExists(packet.getFrom());
			}
    	});
        this.addMessageListener(messageListener);
        this.addPresenceListener(presenceListener);
    }
    
    /** отсоединиться от сервера Jabber и удалить всех слушателей */
    public void disconnect(){
    	Presence presence=new Presence(Presence.Type.unavailable);
    	this.listOfListener.clear();
    	this.listOfPresence.clear();
    	connection.sendPacket(presence);
    	connection.disconnect();
    }
    
    /** послать сообщение получателю с указанным текстом  
     * @param recipient - адрес получателя
     * @param text - текст для получателя
     */
    public void sendMessage(String recipient, String text){
    	Message msg=new Message(recipient,Message.Type.chat);
    	msg.setBody(text);
    	this.connection.sendPacket(msg);
    }

	@Override
	public void processPacket(Packet packet) {
		/** получен пакет, который идентифицирует пользователя как присоединенного-отсоединенного */
		if(packet instanceof Presence){
			Presence presence=(Presence)packet;
			//System.out.println("получен пакет присутствия: ");
			/*
			presence.getMode(); // возможные варианты установки статуса (в сети, отошел, не беспокоить, не доступен )
			Presence.Mode.available - 
			*/
			// System.out.println("Type:"+presence.getType().toString());
			if(presence.getType().equals(Presence.Type.available)){
				this.notifyEventUserEnter(presence);
			}
			if(presence.getType().equals(Presence.Type.unavailable)){
				this.notifyEventUserExit(presence);
			}
			if(presence.getType().equals(Presence.Type.error)){
				this.notifyEventUserError(presence);
			}
		}
		// this.printPacketInfo(packet);
		if(packet instanceof Message){
			notifyAllMessageListeners((Message)packet);
		}
	}
	
	/** оповестить всех слушателей о входе пользователя из контакт-листа в сеть  */
	private void notifyEventUserEnter(Presence presence){
		if(presence!=null){
			String user=this.getUserAddress(presence.getFrom());
			Iterator<IPresenceListener> iterator=this.listOfPresence.iterator();
			while(iterator.hasNext()){
				IPresenceListener listener=iterator.next();
				if(listener!=null){
					listener.userEnter(user);
				}
			}
		}
	}
	/** оповестить всех слушателей о выходе пользователя из контакт-листа из сети */
	private void notifyEventUserExit(Presence presence){
		if(presence!=null){
			String user=this.getUserAddress(presence.getFrom());
			Iterator<IPresenceListener> iterator=this.listOfPresence.iterator();
			while(iterator.hasNext()){
				IPresenceListener listener=iterator.next();
				if(listener!=null){
					listener.userExit(user);
				}
			}
		}
	}
	
	/** оповестить всех слушателей о выходе пользователя из контакт-листа из сети */
	private void notifyEventUserError(Presence presence){
		if(presence!=null){
			String user=this.getUserAddress(presence.getFrom());
			Iterator<IPresenceListener> iterator=this.listOfPresence.iterator();
			while(iterator.hasNext()){
				IPresenceListener listener=iterator.next();
				if(listener!=null){
					listener.userError(user);
				}
			}
		}
	}

	/** получить из строки From адрес пользователя, который прислал данное сообщение  
	 * @param строка из принятого пакета IQ которая содержит адрес и версию ПО 
	 * */
	private String getUserAddress(String from ){
		if(from!=null){
			int slashPosition=from.indexOf("/");
			if(slashPosition>0){
				return from.substring(0,slashPosition);
			}else{
				return from;
			}
		}else{
			return "";
		}
	}
	
	@SuppressWarnings("unused")
	private void printPacketInfo(Packet packet){
		System.out.println("--------------------------------------------");
		System.out.println("Packet Class: "+packet.getClass().getName());
		System.out.println("From: "+packet.getFrom());
		System.out.println("To: "+packet.getTo());
		System.out.println("Xmlns: "+packet.getXmlns());
		System.out.println("Error: "+packet.getError());
		//System.out.println("      Extensions: ");
		//printExtensions(packet);
		//System.out.println("      Property: ");
		//printProperties(packet); - глючит
		System.out.println("============================================");
	}
	
	@SuppressWarnings("unused")
	private void printExtensions(Packet packet){
		Collection<PacketExtension> collection=packet.getExtensions();
		Iterator<PacketExtension> iterator=collection.iterator();
		while(iterator.hasNext()){
			PacketExtension extension=iterator.next();
			System.out.println("          NameSpace: "+extension.getNamespace()+"    ElementName: "+extension.getElementName()+"    "+packet.getExtension(extension.getNamespace(), extension.getElementName()).toXML());
		}
	}
	
	@SuppressWarnings("unused")
	private void printProperties(Packet packet){
		Collection<String> collection=packet.getPropertyNames();
		Iterator<String> iterator=collection.iterator();
		while(iterator.hasNext()){
			String property=iterator.next();
			System.out.println("          PropertyName:"+property+"    PropertyValue:"+packet.getProperty(property));
		}
	}
}
