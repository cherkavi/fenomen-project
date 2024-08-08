package swing_framework;

import javax.swing.JInternalFrame;

/** интерфейс, который содержит функционал по реагированию на закрытие дочернего окна */
public interface IParent {
	/** дочернее окно оповещает родительское о необходимости изменения своего статуса видимости */
	public void selfSetVisible(boolean isVisible);
	/** дочернее окно оповещает родительское о своем закрытии, и передает в аргументе само себя */
	public void childWindowWasClosed(JInternalFrame source);
}
