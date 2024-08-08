package fenomen.monitor.menu_notifier.jabber.wrap;

/** оповещатель о входящих сообщениях */
public interface IMessageListener {
	/** оповещатель о входящих сообщениях 
	 * @param from - от кого
	 * @param text - текст входящего сообщения
	 */
	public void messageNotify(String from, String text);
}
