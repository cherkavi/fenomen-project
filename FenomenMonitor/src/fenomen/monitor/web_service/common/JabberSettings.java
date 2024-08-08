package fenomen.monitor.web_service.common;

public class JabberSettings {
	/** URL jabber-сервера */
	private String jabberUrl;
	/** порт jabber */
	private int jabberPort;
	/** jabber proxy */
	private String jabberProxy;
	/** логин */
	private String jabberLogin;
	/** пароль */
	private String jabberPassword;
	
	/** адрес сервера, на который нужно посылать уведомления */
	private String serverAddress;
	
	public JabberSettings(){
	}

	/** URL jabber-сервера */
	public String getJabberUrl() {
		return jabberUrl;
	}

	/** URL jabber-сервера */
	public void setJabberUrl(String jabberUrl) {
		this.jabberUrl = jabberUrl;
	}

	/** порт jabber */
	public int getJabberPort() {
		return jabberPort;
	}

	/** порт jabber */
	public void setJabberPort(int jabberPort) {
		this.jabberPort = jabberPort;
	}

	/** jabber proxy */
	public String getJabberProxy() {
		return jabberProxy;
	}

	/** jabber proxy */
	public void setJabberProxy(String jabberProxy) {
		this.jabberProxy = jabberProxy;
	}

	/** логин */
	public String getJabberLogin() {
		return jabberLogin;
	}

	/** логин */
	public void setJabberLogin(String jabberLogin) {
		this.jabberLogin = jabberLogin;
	}


	
	/** пароль */
	public String getJabberPassword() {
		return jabberPassword;
	}

	/** пароль */
	public void setJabberPassword(String jabberPassword) {
		this.jabberPassword = jabberPassword;
	}


	
	/** адрес сервера, на который нужно посылать уведомления */
	public String getServerAddress() {
		return serverAddress;
	}

	/** адрес сервера, на который нужно посылать уведомления */
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	
}
