package fenomen.monitor.notifier;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import database.ConnectWrap;
import database.wrap.ModuleRestart;
import database.wrap.MonitorEventRestart;
import fenomen.monitor.notifier.worker.MonitorWorkerFactory;

/** ������, ������� ���������� ��������� ��� �������� ��������� ���������, �������� ��( ��������� ) ���-�� � ���������� ���������� �� ������� �������� */
public class EventFilterRestart {
	private Logger logger=Logger.getLogger(this.getClass());
	
	/** ������, ������� ���������� ��������� ��� �������� ��������� ���������, �������� ��( ��������� ) ���-�� � ���������� ���������� �� ������� �������� */
	public EventFilterRestart(){
	}
	
	String query=
		"	select module_restart.id module_restart_id,	\n "+
		"	       monitor.id monitor_id	\n "+
		"	from module_restart	\n "+
		"	inner join monitor	\n "+
		"	    on monitor.id in	\n "+
		"	    (select id_monitor from monitor_settings_restart	\n "+
		"	      where monitor_settings_restart.id_module=module_restart.id_module	\n "+
		"	      and monitor_settings_restart.is_enabled=1	\n "+
		"	      and monitor_settings_restart.id_monitor=monitor.id)	\n "+
		"	where module_restart.id=?	\n ";
	
	/** �������� ����� Alarm ���������  
	 * @param connector - ���������� � ����� ������ 
	 * @param moduleRestart - ������, ������� ��� �������� � ���� ������ ��� �������� ��������� ��������� �� ������
	 * @return 
	 * 	<ul>
	 * 		<li><b>true</b> - ������� ��������� </li>
	 * 		<li><b>false</b> - ������ ��������� </li>
	 * 	<ul> 
	 */
	public boolean notifyRestartEvent(ConnectWrap connector, ModuleRestart moduleRestart){
		boolean returnValue=true;
		PreparedStatement ps=null;
		ResultSet rs=null;
		boolean needNotify=false;
		// ���������� ������� �� ��������� (monitor), � �������� ������ �� ������� monitor_settings_alarm
		try{
			Connection connection=connector.getConnection();
			Session session=connector.getSession();
			ps=connection.prepareStatement(query);
			ps.setInt(1, moduleRestart.getId());
			rs=ps.executeQuery();
			while(rs.next()){
				// ��������� ������ �� ������������ �������� �� �������� � ������� ��� �������, ������� ����� �������� ������� monitor_event_alarm
				MonitorEventRestart event=new MonitorEventRestart();
				event.setIdModuleRestart(rs.getInt("module_restart_id"));
				event.setIdMonitor(rs.getInt("monitor_id"));
				event.setIdMonitorEventState(1);
				event.setStateTimeWrite(new Date());
				event.setIdMonitorEventResolve(0);
				event.setResolveTimeWrite(new Date());
				session.beginTransaction();
				session.save(event);
				session.getTransaction().commit();
				needNotify=true;
			}
			returnValue=true;
			if(needNotify==true){
				logger.debug("���������� Worker, ������� ������������� ������� ������ � ������� Restart, ������� ����� ������� ��������");   
				MonitorWorkerFactory.getInstance().pulseEventRestart();
			}
		}catch(Exception ex){
			try{
				rs.close();
			}catch(Exception exInner){};
			try{
				ps.close();
			}catch(Exception exInner){};
			returnValue=false;
			logger.error("notifyRestartEvent: "+ex.getMessage(),ex);
		}
		return returnValue;
	}
}
