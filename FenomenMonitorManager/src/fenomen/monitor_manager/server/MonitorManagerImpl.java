package fenomen.monitor_manager.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.Monitor;

import fenomen.monitor_manager.client.view.monitor_list.MonitorManager;
import fenomen.monitor_manager.client.view.monitor_list.MonitorWrap;

public class MonitorManagerImpl extends RemoteServiceServlet implements MonitorManager{
	private final static long serialVersionUID=1L;
	private Logger logger=Logger.getLogger(this.getClass());
	
	@Override
	public MonitorWrap[] getMonitors() {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		MonitorWrap[] returnValue=new MonitorWrap[]{};
		try{
			Connection connection=connector.getConnection();
			ResultSet rs=connection.createStatement().executeQuery("select * from monitor order by id asc");
			ArrayList<MonitorWrap> list=new ArrayList<MonitorWrap>();
			while(rs.next()){
				MonitorWrap monitor=new MonitorWrap();
				monitor.setId(rs.getInt("id"));
				monitor.setLogin(rs.getString("monitor_login"));
				monitor.setPassword(rs.getString("monitor_password"));
				monitor.setDescription(rs.getString("monitor_description"));
				monitor.setJabberUrl(rs.getString("jabber_url"));
				monitor.setJabberPort(rs.getInt("jabber_port"));
				monitor.setJabberProxy(rs.getString("jabber_proxy"));
				monitor.setJabberLogin(rs.getString("jabber_login"));
				monitor.setJabberPassword(rs.getString("jabber_password"));
				list.add(monitor);
			}
			returnValue=list.toArray(returnValue);
			rs.getStatement().close();
		}catch(Exception ex){
			logger.error("getMonitors Exception:"+ex.getMessage());
		}finally{
			connector.close();
		}
		return returnValue;
	}

	@Override
	public String addMonitor(MonitorWrap monitor) {
		String returnValue=null;
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Monitor databaseMonitor=new Monitor();
			databaseMonitor.setLogin(monitor.getLogin());
			databaseMonitor.setPassword(monitor.getPassword());
			databaseMonitor.setDescription(monitor.getDescription());
			databaseMonitor.setJabberUrl(monitor.getJabberUrl());
			databaseMonitor.setJabberPort(monitor.getJabberPort());
			databaseMonitor.setJabberProxy(monitor.getJabberProxy());
			databaseMonitor.setJabberLogin(monitor.getJabberLogin());
			databaseMonitor.setJabberPassword(monitor.getJabberPassword());
			Session session=connector.getSession();
			// проверить существование логина в базе 
			if(session.createCriteria(Monitor.class).add(Restrictions.eq("login", monitor.getLogin())).list().size()>0){
				returnValue="Input another login";
			}else{
				session.beginTransaction();
				session.save(databaseMonitor);
				session.getTransaction().commit();
			}
		}catch(Exception ex){
			returnValue=ex.getMessage();
			logger.error("addMonitor Exception:"+returnValue);
		}finally{
			connector.close();
		}
		return returnValue;
	}

	@Override
	public String editMonitor(MonitorWrap monitor) {
		String returnValue=null;
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Session session=connector.getSession();
			Monitor databaseMonitor=(Monitor)session.get(Monitor.class, new Integer(monitor.getId()));
			if(monitor==null){
				returnValue="Monitor not found";
			}else{
				databaseMonitor.setLogin(monitor.getLogin());
				databaseMonitor.setPassword(monitor.getPassword());
				databaseMonitor.setDescription(monitor.getDescription());
				databaseMonitor.setJabberUrl(monitor.getJabberUrl());
				databaseMonitor.setJabberPort(monitor.getJabberPort());
				databaseMonitor.setJabberProxy(monitor.getJabberProxy());
				databaseMonitor.setJabberLogin(monitor.getJabberLogin());
				databaseMonitor.setJabberPassword(monitor.getJabberPassword());
				session.beginTransaction();
				session.update(databaseMonitor);
				session.getTransaction().commit();
			}
		}catch(Exception ex){
			returnValue=ex.getMessage();
			logger.error("editMonitor Exception:"+returnValue);
		}finally{
			connector.close();
		}
		return returnValue;
	}

	@Override
	public String removeMonitor(MonitorWrap monitor) {
		String returnValue=null;
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Session session=connector.getSession();
			Monitor databaseMonitor=(Monitor)session.get(Monitor.class, new Integer(monitor.getId()));
			if(monitor==null){
				returnValue="Monitor not found";
			}else{
				session.beginTransaction();
				session.delete(databaseMonitor);
				session.getTransaction().commit();
			}
		}catch(Exception ex){
			returnValue=ex.getMessage();
			logger.error("removeMonitor Exception:"+returnValue);
		}finally{
			connector.close();
		}
		return returnValue;
	}

}
