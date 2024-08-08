package swing_framework.messages;

/** объект - слушатель данного сообщения */
public interface IMessageListener {
	/** объект - слушатель данного сообщения 
	 * @param message - сообщение, которое будет послано другим окнам 
	 */
	public boolean notifyMessage(WindowMessage message);
}
