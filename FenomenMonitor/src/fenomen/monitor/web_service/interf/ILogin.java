package fenomen.monitor.web_service.interf;

import fenomen.monitor.web_service.common.JabberSettings;
import fenomen.monitor.web_service.common.MonitorIdentifier;

/** идентификация пользователя в сети  */
public interface ILogin {
	/** получить идентификатор монитора на основании логина и пароля
	 * @param login - логин 
	 * @param password - пароль
	 * @return - 
	 * <ul>
	 * 	<li>уникальный идентификатор монитора, для дальнейшего обмена с сервером</li>
	 * 	<li>null - пользователь или пароль не найдены </li>
	 * </ul>
	 * @throws в случае ошибки обмена с ссервером
	 */
	public MonitorIdentifier enter(String login, String password) throws Exception;
	
	/** получить jabber настройки по данному клиенту для связи с сервером через XMPP протокол */
	public JabberSettings getJabberSettings(MonitorIdentifier monitorIdentifier) throws Exception ;
}
