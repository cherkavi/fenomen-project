package fenomen.monitor.menu_notifier.jabber.wrap;

/** слушатель служебных сообщений, в частности появления или исчезновения из сети списка контактов клиентов */
public interface IPresenceListener {
	/** пользователь появился в сети  */
	public void userEnter(String user);
	/** пользователь покинул сеть  */
	public void userExit(String user); 
	/** ошибочное сообщение от пользователя - не возникало во время тестов*/
	public void userError(String user); 
}
