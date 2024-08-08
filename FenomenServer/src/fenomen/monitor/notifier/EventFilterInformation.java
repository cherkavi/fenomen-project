package fenomen.monitor.notifier;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import database.ConnectWrap;
import database.wrap.ModuleInformationWrap;
import database.wrap.MonitorEventInformation;
import fenomen.monitor.notifier.worker.MonitorWorkerFactory;

/** ������, ������� ���������� ��������� ��� �������� ��������� ���������, �������� ��( ��������� ) ���-�� � ���������� ���������� �� ������� �������� */
public class EventFilterInformation {
	private Logger logger=Logger.getLogger(this.getClass());
	
	/** ������, ������� ���������� ��������� ��� �������� ��������� ���������, �������� ��( ��������� ) ���-�� � ���������� ���������� �� ������� �������� */
	public EventFilterInformation(){
	}
	
	
	
	String query=
		"	select module_information.id module_information_id,	\n "+
		"	       monitor.id monitor_id	\n "+
		"	from module_information	\n "+
		"	inner join monitor	\n "+
		"	    on monitor.id in	\n "+
		"	    (select id_monitor from monitor_settings_information	\n "+
		"	      where monitor_settings_information.id_module=module_information.id_module	\n "+
		"	      and monitor_settings_information.is_enabled=1	\n "+
		"	      and monitor_settings_information.id_monitor=monitor.id)	\n "+
		"	where module_information.id=?	\n ";
		
	
	/** �������� ����� Information ���������  
	 * @param connector - ���������� � ����� ������ 
	 * @param moduleInformationWrap - ������, ������� ��� �������� � ���� ������ ��� �������� ��������� ��������� �� ������
	 * @return 
	 * 	<ul>
	 * 		<li><b>true</b> - ������� ��������� </li>
	 * 		<li><b>false</b> - ������ ��������� </li>
	 * 	<ul> 
	 */
	public boolean notifyInformationEvent(ConnectWrap connector, ModuleInformationWrap moduleInformationWrap){
		boolean returnValue=true;
		PreparedStatement ps=null;
		ResultSet rs=null;
		boolean needNotify=false;
		// ���������� ������� �� ��������� (monitor), � �������� ������ �� ������� monitor_settings_alarm
		try{
			Connection connection=connector.getConnection();
			Session session=connector.getSession();
			ps=connection.prepareStatement(query);
			ps.setInt(1, moduleInformationWrap.getId());
			rs=ps.executeQuery();
			while(rs.next()){
				// ��������� ������ �� ������������ �������� �� �������� � ������� ��� �������, ������� ����� �������� ������� monitor_event_alarm
				MonitorEventInformation event=new MonitorEventInformation();
				event.setIdModuleInformation(rs.getInt("module_information_id"));
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
				logger.debug("���������� Worker, ������� ������������� ������� ������ � ������� Information, ������� ����� ������� ��������");
				MonitorWorkerFactory.getInstance().pulseEventInformation();
				
			}
		}catch(Exception ex){
			try{
				rs.close();
			}catch(Exception exInner){};
			try{
				ps.close();
			}catch(Exception exInner){};
			returnValue=false;
			logger.error("notifyInformationEvent: "+ex.getMessage(),ex);
		}
		return returnValue;
	}
}
