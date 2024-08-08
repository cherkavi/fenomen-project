package swing_framework.messages;

/** сообщение, которое отправляется окнам, которые зарегестрированы как слушатели */
public class WindowMessage {
	private int messageId;
	private Object additionalIdentifier;
	private Object argument;
	
	/** сообщение, которое отправляется окнам, которые зарегестрированы как слушатели 
	 * @param messageid {@link WindowMessageType}
	 * */
	public WindowMessage(int messageId){
		this.messageId=messageId;
	}

	/** сообщение, которое отправляется окнам, которые зарегестрированы как слушатели 
	 * @param messageid {@link WindowMessageType}
	 * @param additionalIdentifier - дополнительный объект, который описывает задачу
	 * */
	public WindowMessage(int messageId, String additionalIdentifier){
		this.messageId=messageId;
		this.additionalIdentifier=additionalIdentifier;
	}
	
	/** сообщение, которое отправляется окнам, которые зарегестрированы как слушатели 
	 * @param messageid {@link WindowMessageType}
	 * @param additionalIdentifier - дополнительный объект, который описывает задачу
	 * @param argument - объект, который передается в качестве аргумента
	 * */
	public WindowMessage(int messageId, Object additionalIdentifier, Object argument){
		this.messageId=messageId;
		this.additionalIdentifier=additionalIdentifier;
		this.argument=argument;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public Object getAdditionalIdentifier() {
		return additionalIdentifier;
	}

	public void setAdditionalIdentifier(Object additionalIdentifier) {
		this.additionalIdentifier = additionalIdentifier;
	}

	public Object getArgument() {
		return argument;
	}

	public void setArgument(Object argument) {
		this.argument = argument;
	}
	
	
	
}
