package swing_framework.window;

import swing_framework.AbstractInternalFrame;

/** аттрибуты зарегестрированного окна, которое в данный момент находится в системе */
public class RegisteredWindow {
	private AbstractInternalFrame frame;
	/** идентификаторы окна, которые содержат уникальные атрибуты для окон */
	private WindowIdentifier windowIdentifiers;
	
	public RegisteredWindow(AbstractInternalFrame frame, WindowIdentifier windowIdentifier){
		this.frame=frame;
		this.windowIdentifiers=windowIdentifier;
	}

	public RegisteredWindow(AbstractInternalFrame frame){
		this.frame=frame;
	}

	public WindowIdentifier getWindowIdentifiers() {
		return windowIdentifiers;
	}


	public void setWindowIdentifiers(WindowIdentifier windowIdentifiers) {
		this.windowIdentifiers = windowIdentifiers;
	}

	public AbstractInternalFrame getFrame() {
		return frame;
	}

	public void setFrame(AbstractInternalFrame frame) {
		this.frame = frame;
	}
}
