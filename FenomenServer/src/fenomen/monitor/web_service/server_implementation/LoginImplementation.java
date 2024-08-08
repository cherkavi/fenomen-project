package fenomen.monitor.web_service.server_implementation;

import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.Monitor;
import fenomen.monitor.web_service.common.JabberSettings;
import fenomen.monitor.web_service.common.MonitorIdentifier;
import fenomen.monitor.web_service.interf.ILogin;

/** серверная реализация интерфейса {@link ILogin}*/
public class LoginImplementation implements ILogin{
	private Logger logger=Logger.getLogger(this.getClass());
	
	/** серверная реализация интерфейса {@link ILogin}*/
	public LoginImplementation(){
	}
	
	@Override
	public MonitorIdentifier enter(String login, String password) throws Exception {
		MonitorIdentifier returnValue=null;
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Monitor monitor=(Monitor)connector.getSession().createCriteria(Monitor.class)
														   .add(Restrictions.eq("login",login))
														   .add(Restrictions.eq("password",password))
														   .setMaxResults(1)
														   .uniqueResult();
			if(monitor!=null){
				returnValue=new MonitorIdentifier();
				returnValue.setId(monitor.getId());
			}
		}catch(Exception ex){
			logger.error("LoginImplementation#enter Exception:"+ex.getMessage());
		}finally{
			connector.close();
		}
		return returnValue;
	}

	@Override
	public JabberSettings getJabberSettings(MonitorIdentifier monitorIdentifier) throws Exception {
		if(monitorIdentifier!=null){
			ConnectWrap connector=StaticConnector.getConnectWrap();
			try{
				ResultSet rs=connector.getConnection().createStatement().executeQuery("select * from monitor where monitor.id="+monitorIdentifier.getId());
				if(rs.next()){
					JabberSettings returnValue=new JabberSettings();
					returnValue.setJabberUrl(rs.getString("jabber_url"));
					returnValue.setJabberPort(rs.getInt("jabber_port"));
					returnValue.setJabberProxy(rs.getString("jabber_proxy"));
					returnValue.setJabberLogin(rs.getString("jabber_login"));
					returnValue.setJabberPassword(rs.getString("jabber_password"));
					
					JabberSettings serverSettings=this.getJabberSettingsOfServer();
					returnValue.setServerAddress(serverSettings.getJabberLogin());
					return returnValue;
				}else{
					logger.error("не найден модуль с идентификатором: "+monitorIdentifier.getId());
					return null;
				}
			}catch(Exception ex){
				logger.error("getJabberSettings Exception: "+ex.getMessage());
				return null;
			}finally{
				connector.close();
			}
		}else{
			return null;
		}
	}

	/** получение настроек jabber-клиента для сервера  
	 * @return 
	 * <ul>
	 * 	<li><b>null</b> - ошибка при получении настроек для сервера </li>
	 * 	<li><b>settings</b>  - настройки сервера </li>
	 * </ul>
	 * */
	private JabberSettings getJabberSettingsOfServer(){
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			ResultSet rs=connector.getConnection().createStatement().executeQuery("select * from system_jabber_monitor_settings");
			if(rs.next()){
				JabberSettings settings=new JabberSettings();
				settings.setJabberUrl(rs.getString("jabber_server"));
				settings.setJabberPort(rs.getInt("jabber_server_port"));
				settings.setJabberProxy(rs.getString("jabber_server_proxy"));
				settings.setJabberLogin(rs.getString("jabber_login"));
				settings.setJabberPassword(rs.getString("jabber_password"));
				return settings;
			}else{
				logger.error("getJabberSettingsOfServer settings does not found ");
				return null;
			}
		}catch(Exception ex){
			logger.error("getJabberSettingsOfServer Exception: "+ex.getMessage());
			return null;
		}finally{
			connector.close();
		}
	}
}
