package database.wrap;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.*;

/**
 * <table border=1>
 * 	<tr>
 * 		<th>Database Field</th>  <th>POJO</th>  <th>Description</th>
 * 	</tr>
 * 	<tr>
 * 		<td>id</td>  <td>id</td>  <td> уникальный идентификатор в базе </td>
 * 	</tr>
 * 	<tr>
 * 		<td>monitor_login</td>  <td>login</td>  <td> логин </td>
 * 	</tr>
 * 	<tr>
 * 		<td>monitor_password</td>  <td>password</td>  <td> пароль </td>
 * 	</tr>
 * 	<tr>
 * 		<td>monitor_description</td>  <td>description</td>  <td> описание </td>
 * 	</tr>
 * 	<tr>
 * 		<td>jabber_url</td>  <td>jabberUrl</td>  <td>URL к Jabber-серверу ( talk.google.com )</td>
 * 	</tr>
 * 	<tr>
 * 		<td>jabber_port</td>  <td>jabberPort</td>  <td>Port к Jabber серверу ( 5222 )</td>
 * 	</tr>
 * 	<tr>
 * 		<td>jabber_proxy</td>  <td>jabberProxy</td>  <td>Proxy к Jabber серверу ( gmail.com )</td>
 * 	</tr>
 * 	<tr>
 * 		<td>jabber_login</td>  <td>jabberLogin</td>  <td>login к Jabber серверу  (technik7jobrobot@gmail.com)</td>
 * 	</tr>
 * 	<tr>
 * 		<td>jabber_password</td>  <td>jabberPassword</td>  <td>ѕароль к Jabber —ерверу (robottechnik7)</td>
 * 	</tr>
 * </table>
 */
@Entity
@PersistenceCapable(detachable="false")
@Table(name="monitor")
public class Monitor {
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;

	@Column(name="monitor_login",length=45)
	private String login;

	@Column(name="monitor_password",length=45)
	private String password;
	
	@Column(name="monitor_description",length=255)
	private String description;

	@Column(name="jabber_url")
	private String jabberUrl;
	
	@Column(name="jabber_port")
	private Integer jabberPort;
	
	@Column(name="jabber_proxy")
	private String jabberProxy;
	
	@Column(name="jabber_login")
	private String jabberLogin;
	
	@Column(name="jabber_password")
	private String jabberPassword;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
