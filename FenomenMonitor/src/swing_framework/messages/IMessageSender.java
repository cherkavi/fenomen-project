package swing_framework.messages;

import swing_framework.AbstractInternalFrame;
import swing_framework.window.WindowIdentifier;

/** объект, которые рассылает указанное сообщение другим окнам в системе */
public interface IMessageSender {
	/** отправка сообщения на объект, который рассылает слушателям 
	 * @param owner - собственник данного сообщения ( или null)
	 * @param message - сообщение, которое будет послано 
	 * @return
	 */
	public boolean sendMessage(AbstractInternalFrame owner, WindowMessage message);
	
	/** зарегестрировать окно в системе */
	public boolean addWindowListener(AbstractInternalFrame listener,WindowIdentifier identifier);
	
	/** удалить зарегестрированное окно из системы */
	public boolean removeWindowListener(AbstractInternalFrame listener); 
}
