package fenomen.monitor.menu_notifier;

import java.awt.BorderLayout;

import java.awt.Cursor;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import fenomen.monitor.Monitor;
import fenomen.monitor.menu_notifier.jabber.message_table.TableOfMessage;
import fenomen.monitor.menu_notifier.jabber.wrap.IMessageListener;
import fenomen.monitor.menu_notifier.jabber.wrap.IPresenceListener;
import fenomen.monitor.menu_notifier.jabber.wrap.JabberWrap;
import fenomen.monitor.web_service.client_implementation.LoginImplementation;
import fenomen.monitor.web_service.common.JabberSettings;

import swing_framework.AbstractInternalFrame;
import swing_framework.messages.IMessageSender;

public class MenuNotifier extends AbstractInternalFrame implements IMessageListener, IPresenceListener{
	private static final long serialVersionUID=1L;
	/** настройки дл€ Jabber-сервера */
	private JabberSettings jabberSettings=null;
	/** jabber-клиент - транспорт дл€ обмена с сервером  */
	private JabberWrap jabber=null;
	/** таблица, котора€ содержит все событи€, которые пришли от сервера */
	private TableOfMessage table=null;
	
	public MenuNotifier( IMessageSender messageSender) {
		super("ћонитор оповещени€", messageSender, 750, 400, false, true);
		initComponents();
		this.addInternalFrameListener(new InternalFrameListener(){
			@Override
			public void internalFrameActivated(InternalFrameEvent arg0) {
			}
			@Override
			public void internalFrameClosed(InternalFrameEvent arg0) {
			}
			@Override
			public void internalFrameClosing(InternalFrameEvent arg0) {
				onCloseWindow();
			}
			@Override
			public void internalFrameDeactivated(InternalFrameEvent arg0) {
			}
			@Override
			public void internalFrameDeiconified(InternalFrameEvent arg0) {
			}
			@Override
			public void internalFrameIconified(InternalFrameEvent arg0) {
			}
			@Override
			public void internalFrameOpened(InternalFrameEvent arg0) {
			}
		});
		// получить настройки дл€ Jabber сети по данному монитору
		if(getJabberSettings()==false){
			JOptionPane.showMessageDialog(this, "ќшибка получени€ от сервера настроек ", "работа невозможна", JOptionPane.ERROR_MESSAGE);
			this.closeCurrentWindow();
		}else{
			// настройки получены - подключение к Jabber-серверу 
			jabber=new JabberWrap(jabberSettings.getJabberUrl(), jabberSettings.getJabberPort(), jabberSettings.getJabberProxy());
			try{
				jabber.connect(this.jabberSettings.getJabberLogin(), this.jabberSettings.getJabberPassword(), this,this);
			}catch(Exception ex){
				JOptionPane.showMessageDialog(this, ex.getMessage(), "ќшибка подключени€",JOptionPane.ERROR_MESSAGE);
				this.jabberDisconnect();
				this.closeCurrentWindow();
			}
		}
	}
	
	/** послать на сервер сообщение о разрешении очередного Event  
	 * @param data - данные по которым прин€то решение 
	 * @param value - значение, которое определ€ет это решение 
	 * @return ошибку, если не удалось отправить сообщение 
	 */
	public String sendToServer(String text){
		String returnValue=null;
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try{
			this.jabber.sendMessage(this.jabberSettings.getServerAddress(), text);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}catch(Exception ex){
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			returnValue=ex.getMessage();
		}
		return returnValue;
	}

	/** получить Jabber настройки от сервера  */
	private boolean getJabberSettings(){
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try{
			LoginImplementation loginManager=new LoginImplementation(Monitor.url);
			jabberSettings=loginManager.getJabberSettings(Monitor.monitorIdentifier);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return true;
		}catch(Exception ex){
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return false;
		}
	}
	
	/** первоначальна€ инициализаци€ компонентов */
	private void initComponents(){
		this.table=new TableOfMessage(this, this);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(new JScrollPane(this.table), BorderLayout.CENTER);
	}

	@Override
	public void messageNotify(String from, String text) {
		// получено вход€щее сообщение от пользовател€ 
		if(from.equals(this.jabberSettings.getServerAddress())){
			// расшифровать сообщение
			String messageForSend=this.table.inputMessageFromServer(text);
			if(messageForSend!=null){
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				this.jabber.sendMessage(this.jabberSettings.getServerAddress(), messageForSend);
				this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}else{
				// нет данных дл€ отправки
			}
		}else{
			// получено сообщение не от сервера - возможно спам 
		}
	}

	@Override
	public void userEnter(String user) {
		// вход пользовател€ в сеть
	}

	@Override
	public void userError(String user) {
		// получение ошибочного сообщени€
	}

	@Override
	public void userExit(String user) {
		// SwingUtilities.invokeLater(new Runnable(){})
		// может быть изменен курсор, если происходит ожидание ответа от сервера Jabber
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		// выход пользовател€ из сети
		JOptionPane.showMessageDialog(this, "—ервер вышел из сети, окно будет закрыто","ѕредупреждение", JOptionPane.WARNING_MESSAGE);
		this.jabberDisconnect();
		this.closeCurrentWindow();
	}

	/** слушатель закрыти€ текущего окна */
	private void onCloseWindow(){
		this.jabberDisconnect();
	}
	
	/** отсоединитьс€ от сервера Jabber  */
	private void jabberDisconnect(){
		try{
			this.jabber.disconnect();
		}catch(Exception ex){
		}
	}
}
