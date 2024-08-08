package fenomen.monitor.notifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import database.ConnectWrap;
import database.wrap.ModuleAlarmWrap;
import database.wrap.MonitorEventAlarm;
import fenomen.monitor.notifier.worker.MonitorWorkerFactory;

/** ������, ������� ���������� ��������� ��� �������� ��������� ���������, �������� ��( ��������� ) ���-�� � ���������� ���������� �� ������� �������� */
public class EventFilterAlarm {
	private Logger logger=Logger.getLogger(this.getClass());
	
	/** ������, ������� ���������� ��������� ��� �������� ��������� ���������, �������� ��( ��������� ) ���-�� � ���������� ���������� �� ������� �������� */
	public EventFilterAlarm(){
	}
	
	
	
	String query="select module_alarm.id module_alarm_id, \n"+
       			 "monitor.id monitor_id \n" +
       			 "from module_alarm \n" +
       			 "inner join monitor \n" +
       			 "      on monitor.id in \n" +
       			 "      (select id_monitor from monitor_settings_alarm \n" +
       			 "                         where monitor_settings_alarm.id_module=module_alarm.id_module \n" +
       			 "                           and monitor_settings_alarm.is_enabled=1 \n" +
       			 "                           and monitor_settings_alarm.id_monitor=monitor.id) \n" +
       			 "where module_alarm.id=? \n";
	
	/** �������� ����� Alarm ���������  
	 * @param connector - ���������� � ����� ������ 
	 * @param moduleAlarmWrap - ������, ������� ��� �������� � ���� ������ ��� �������� ��������� ��������� �� ������
	 * @return 
	 * 	<ul>
	 * 		<li><b>true</b> - ������� ��������� </li>
	 * 		<li><b>false</b> - ������ ��������� </li>
	 * 	<ul> 
	 */
	public boolean notifyAlarmEvent(ConnectWrap connector, ModuleAlarmWrap moduleAlarmWrap){
		boolean returnValue=true;
		PreparedStatement ps=null;
		ResultSet rs=null;
		boolean needNotify=false;
		// ���������� ������� �� ��������� (monitor), � �������� ������ �� ������� monitor_settings_alarm
		try{
			Connection connection=connector.getConnection();
			Session session=connector.getSession();
			ps=connection.prepareStatement(query);
			ps.setInt(1, moduleAlarmWrap.getId());
			rs=ps.executeQuery();
			while(rs.next()){
				// ��������� ������ �� ������������ �������� �� �������� � ������� ��� �������, ������� ����� �������� ������� monitor_event_alarm
				MonitorEventAlarm event=new MonitorEventAlarm();
				event.setIdModuleAlarm(rs.getInt("module_alarm_id"));
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
				logger.debug("���������� Worker, ������� ������������� ������� ������ � ������� Alarm, ������� ����� ������� ��������");
				MonitorWorkerFactory.getInstance().pulseEventAlarm();
			}
		}catch(Exception ex){
			try{
				rs.close();
			}catch(Exception exInner){};
			try{
				ps.close();
			}catch(Exception exInner){};
			returnValue=false;
			logger.error("notifyAlarmEvent: "+ex.getMessage(),ex);
		}
		return returnValue;
	}
}
