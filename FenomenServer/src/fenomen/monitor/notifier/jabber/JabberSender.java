package fenomen.monitor.notifier.jabber;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import database.ConnectWrap;
import database.StaticConnector;
import fenomen.monitor.notifier.jabber.wrap.IMessageListener;
import fenomen.monitor.notifier.jabber.wrap.IPresenceListener;
import fenomen.monitor.notifier.jabber.wrap.JabberWrap;

/** jabber-клиент для общения с удаленными модулями */
public class JabberSender extends Thread implements IMessageListener, 
												    IPresenceListener{
	private JabberWrap jabber=null;
	private Logger logger=Logger.getLogger(this.getClass());
	private ArrayList<Message> listOfMessage=new ArrayList<Message>();
	private String login=null;
	private String password=null;
	
	/** список всех слушателей входящих сообщений */
	private ArrayList<IMessageListener> listOfMessageListener=new ArrayList<IMessageListener>();
	/** список всех слушателей служебных сообщений */
	private ArrayList<IPresenceListener> listOfPresenceListener=new ArrayList<IPresenceListener>();
	
	/** добавить слушателя входящих сообщений  */
	public void addMessageListener(IMessageListener messageListener){
		this.listOfMessageListener.add(messageListener);
	}
	/** удалить слушателя входящих сообщений  */
	public void removeMessageListener(IMessageListener messageListener){
		this.listOfMessageListener.remove(messageListener);
	}
	
	/** добавить слушателя служебных сообщений  */
	public void addPresenceListener(IPresenceListener presenceListener){
		this.listOfPresenceListener.add(presenceListener);
	}
	/** удалить слушателя служебных сообщений  */
	public void removePresenceListener(IPresenceListener presenceListener){
		this.listOfPresenceListener.remove(presenceListener);
	}
	
	/** jabber-клиент для общения с удаленными модулями 
	 * загружает свою конфигурацию из базы данных system_jabber_monitor_settings
	 * @param messageListener - слушатель входящих текстовых сообщений
	 * @param presenceListener - слушатель служебных сообщений 
	 * @throws Exception
	 */
	public JabberSender(IMessageListener messageListener, IPresenceListener presenceListener) throws Exception {
		this();
		this.addMessageListener(messageListener);
		this.addPresenceListener(presenceListener);
	}
	
	/** jabber-клиент для общения с удаленными модулями 
	 * загружает свою конфигурацию из базы данных system_jabber_monitor_settings 
	 * */
	public JabberSender() throws Exception{
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			ResultSet rs=connector.getConnection().createStatement().executeQuery("select * from system_jabber_monitor_settings");
			if(rs.next()){
				jabber=new JabberWrap(rs.getString("jabber_server"), rs.getInt("jabber_server_port"), rs.getString("jabber_server_proxy"));
				login=rs.getString("jabber_login");
				password=rs.getString("jabber_password");
				logger.debug("получены логин и пароль для доступа к Jabber-серверу:");
				logger.debug("login: "+login);
			}else{
				throw new Exception(" system_jabber_monitor_settings not consists records ");
			}
		}catch(Exception ex){
			logger.error("constructor Exception:"+ex.getMessage(), ex);
		}finally{
			connector.close();
		}
		this.jabber.addMessageListener(this);
		this.jabber.addPresenceListener(this);
		logger.debug("соединение с Jabber server");
		this.jabber.connect(login, password);
		this.flagRun=true;
		logger.debug("установлено соединение с Jabber сервер ");
	}

	@Override
	public void run(){
		logger.debug("Jabber клиент запущен ");
		Message message=null;
		while(flagRun){
			message=null;
			
			if(this.listOfMessage.size()>0){
				synchronized(this.listOfMessage){
					message=this.listOfMessage.remove(0);
				}
			}
			if(message!=null){
				logger.debug("послать сообщение из очереди удаленному клиенту "+message.getRecipient());
				this.jabber.sendMessage(message.getRecipient(), message.getText());
			}else{
				synchronized(this.listOfMessage){
					if(this.listOfMessage.size()==0){
						try{
							this.listOfMessage.wait();
						}catch(Exception ex){}
					}
				}
			}
		}
		this.jabber.disconnect();
	}

	private boolean flagRun=false;
	/** остановить данный поток и отсоединиться от сервера */
	public void stopThread(){
		this.flagRun=false;
		
	}
	

	/** добавить сообщение в очередь для отправки */
	public void addMessageForSend(String recipient, String text){
		Message message=new Message(recipient, text);
		synchronized(this.listOfMessage){
			this.listOfMessage.add(message);
			this.listOfMessage.notify();
		}
	}

	@Override
	public void messageNotify(String from, 
							  String text) {
		logger.debug("получено входящее сообщение, оповестить всех слушателей входящих сообщений");
		try{
			for(int counter=0;counter<this.listOfMessageListener.size();counter++){
				this.listOfMessageListener.get(counter).messageNotify(from, text);
			}
		}catch(Exception ex){
			logger.error("messageNotify Exception: "+ex.getMessage());
		}
	}
	
	@Override
	public void userEnter(String user) {
		logger.debug("пользователь зашел в сеть "+user); 
		try{
			for(int counter=0;counter<this.listOfPresenceListener.size();counter++){
				this.listOfPresenceListener.get(counter).userEnter(user);
			}
		}catch(Exception ex){
			logger.error("messageNotify Exception: "+ex.getMessage());
		}
	}

	@Override
	public void userExit(String user) {
		logger.debug("пользователь покинул в сеть "+user);
		try{
			for(int counter=0;counter<this.listOfPresenceListener.size();counter++){
				this.listOfPresenceListener.get(counter).userExit(user);
			}
		}catch(Exception ex){
			logger.error("messageNotify Exception: "+ex.getMessage());
		}
	}

	@Override
	public void userError(String user) {
		logger.warn("!!! пользователь прислал ошибочное сообщение:"+user);
	}

	
}

/** объект для отправки через Jabber клиент */
class Message{
	private String recipient;
	private String text;
	
	public Message(){
	}

	public Message(String recipient, String text){
		this.recipient=recipient;
		this.text=text;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}