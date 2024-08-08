package fenomen.monitor_manager.client.view.monitor_list;

import com.google.gwt.user.client.rpc.IsSerializable;

/** обертка для одного монитора (при обмене сервера и клиента) */
public class MonitorWrap implements IsSerializable{
	private int id;
	private String login;
	private String password;
	private String description;
	private String jabberUrl;
	private Integer jabberPort;
	private String jabberProxy;
	private String jabberLogin;
	private String jabberPassword;
	
	/** обертка для одного монитора  */
	public MonitorWrap(){
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getJabberUrl() {
		return jabberUrl;
	}

	public void setJabberUrl(String jabberUrl) {
		this.jabberUrl = jabberUrl;
	}

	public Integer getJabberPort() {
		return jabberPort;
	}

	public void setJabberPort(Integer jabberPort) {
		this.jabberPort = jabberPort;
	}

	public String getJabberProxy() {
		return jabberProxy;
	}

	public void setJabberProxy(String jabberProxy) {
		this.jabberProxy = jabberProxy;
	}

	public String getJabberLogin() {
		return jabberLogin;
	}

	public void setJabberLogin(String jabberLogin) {
		this.jabberLogin = jabberLogin;
	}

	public String getJabberPassword() {
		return jabberPassword;
	}

	public void setJabberPassword(String jabberPassword) {
		this.jabberPassword = jabberPassword;
	}
	
}
