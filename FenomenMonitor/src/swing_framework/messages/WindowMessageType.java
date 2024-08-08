package swing_framework.messages;

/** объект, который содержит все сообщения, которыми могут обмениваться окна */
public class WindowMessageType {
	/** пользовательское сообщение, с дополнительным параметром и объектом */
	public static final int NOTIFY_USER=0;
	/** регистрация JInternalFrame в главном окне new MessageWindow(REGISTER_WINDOW, additinalIdentifier:WindowIdentifier, argument: AbstractInternalFrame)*/
	public static final int REGISTER_WINDOW=1;
	/** переместить данное окно в самое верхнее состояние */
	public static final int MOVE_ON_TOP=2;
	/** окно сообщает о своем закрытии  - сообщение для главного окна (<b>owner</b> - окно, которое будет отправило сообщение, <b>Argument</b> - окно которое нужно закрыть)*/
	public static final int UNREGISTER_WINDOW=3;
	/** передвинуть окно в середину рабочего стола new MessageWindow(MOVE_TO_CENTER, additinalIdentifier:null, argument: AbstractInternalFrame)*/
	public static final int MOVE_TO_CENTER=4;
	/** установить фокус на данном окне  */
	public static final int SELECTED_SET=5;
	/** окно было активировано - оповестить основное окно об активации  */
	public static final int FOCUS_WAS_GOT=6;
	/** снять фокус в указанном окне */
	public static final int UNSELECTED_SET=7;
	/** добавить окно на рабочий стол <b>new WindowMessage(REGISTER_CHILD_WINDOW, additinalIdentifier:WindowIdentifier, argument: AbstractInternalFrame)</b>*/
	public static final int REGISTER_CHILD_WINDOW=8;
	/** убрать окно с рабочего стола <b>new WindowMessage(UNREGISTER_CHILD_WINDOW, additionalIdentifier:WindowIdentifier, argument: AbstractInternalFrame)</b>*/
	public static final int UNREGISTER_CHILD_WINDOW=9;
	/** получить размерность рабочего стола - ответ будет получен в качестве Dimension в Argument */
	public static final int GET_DESKTOP_DIMENSION=10;
	/** сообщение о том что данное окно должно быть закрыто new WindowMessage(CLOSE_WINDOW, additionalIdentifier:null, argument: AbstractIternalFrame - которое должно быть закрыто ) */
	public static final int CLOSE_WINDOW=11;
	/** сообщение о том что данное окно должно быть свёрнуто в иконку new WindowMessage(ICONIFY_WINDOW, additionalIdentifier:null, argument: AbstractInternalFrame - которое должно быть свернуто )*/
	public static final int ICONIFY_WINDOW = 12;
	/** сообщение о том что данное окно должно быть развернуто из иконки new WindowMessage(DEICONIFY_WINDOW, additionalIdentifier:null, argument: AbstractInternalFrame - которое должно быть развернуто )*/
	public static final int DEICONIFY_WINDOW = 13;
	/** сообщение для родительского окна что какое-то дочернее окно установило флаг модальности - отключить все управляющие интерфейсы для окна*/
	public static final int CHILD_SET_MODAL = 14;
}
