package fenomen.monitor.web_service.server_implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.Module;
import database.wrap.MonitorSettingsAlarm;
import fenomen.monitor.web_service.common.AlarmSettingsElement;
import fenomen.monitor.web_service.common.MonitorIdentifier;
import fenomen.monitor.web_service.interf.IAlarmSettings;

/**настройки оповещения события Alarm для мониторов */
public class AlarmSettingsImplementation implements IAlarmSettings{
	private Logger logger=Logger.getLogger(this.getClass());
	
	/**настройки оповещения события Alarm для мониторов */
	public AlarmSettingsImplementation(){
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public AlarmSettingsElement[] getList(MonitorIdentifier monitorIdentifier) {
		AlarmSettingsElement[] returnValue=new AlarmSettingsElement[]{};
		ConnectWrap connector=StaticConnector.getConnectWrap();
		List<MonitorSettingsAlarm> listOfMonitorSettings=null;
		try{
			logger.debug("получить список всех модулей в системе");
			Session session=connector.getSession();
			List<Module> listOfModule=(List<Module>)session.createCriteria(Module.class).addOrder(Order.asc("id")).list();
			logger.debug("получить список всех AlarmSettings модулей ( по данному монитору) ");
			listOfMonitorSettings=(List<MonitorSettingsAlarm>)session.createCriteria(MonitorSettingsAlarm.class).add(Restrictions.eq("idMonitor", monitorIdentifier.getId())).addOrder(Order.asc("idModule")).list();
			
			logger.debug("получить список таких MonitorSettingsAlarm, по которым уже нет модулей (были удалены) ");
			ArrayList<MonitorSettingsAlarm> moduleSettingsRemove=new ArrayList<MonitorSettingsAlarm>();
			for(int counter=0;counter<listOfMonitorSettings.size();counter++){
				int moduleId=listOfMonitorSettings.get(counter).getIdModule();
				Module currentModule=null;
				for(int index=0;index<listOfModule.size();index++){
					if(listOfModule.get(index).getId()==moduleId){
						currentModule=listOfModule.get(index);
						break;
					}
				}
				if(currentModule==null){
					logger.debug("найдена такая настройка MonitorSettingsAlarm по которой уже нет модуля ");
					moduleSettingsRemove.add(listOfMonitorSettings.get(counter));
				}
			}
			logger.debug("удаление найденных 'висячих' настроек (по которым уже нет модулей)");
			if(moduleSettingsRemove.size()>0){
				session.beginTransaction();
				for(int counter=0;counter<moduleSettingsRemove.size();counter++){
					session.delete(moduleSettingsRemove.get(counter));
				}
				session.getTransaction().commit();
				// можно не обновлять 
				//logger.debug("обновить список всех настроек модулей по данному монитору ");
				//listOfMonitorSettings=(List<MonitorSettingsAlarm>)session.createCriteria(MonitorSettingsAlarm.class).add(Restrictions.eq("idMonitor", monitorIdentifier.getId())).addOrder(Order.asc("idModule")).list();
			}
			
			logger.debug("получить список таких Module, которых нет в MonitorSettingsAlarm и добавить их в список ");
			for(int counter=0;counter<listOfModule.size();counter++){
				int moduleId=listOfModule.get(counter).getId();
				MonitorSettingsAlarm currentSettings=null;
				for(int index=0;index<listOfMonitorSettings.size();index++){
					if(moduleId==listOfMonitorSettings.get(index).getIdModule()){
						currentSettings=listOfMonitorSettings.get(index);
						break;
					}
				}
				if(currentSettings==null){
					logger.debug("создать новый MonitorSettingsAlarm по текущему модулю "+moduleId);
					MonitorSettingsAlarm newSettings=new MonitorSettingsAlarm();
					newSettings.setIdModule(moduleId);
					newSettings.setIdMonitor(monitorIdentifier.getId());
					newSettings.setIsEnabled(0);
					session.beginTransaction();
					session.save(newSettings);
					session.getTransaction().commit();
				}
			}
			logger.debug("получить список для отправки на удаленный монитор ");
			Connection connection=connector.getConnection();
			ResultSet rs=connection.createStatement().executeQuery("select module.id_module module_name, module.address module_address, monitor_settings_Alarm.* from monitor_settings_Alarm inner join module on module.id=monitor_settings_Alarm.id_module where monitor_settings_Alarm.id_monitor="+monitorIdentifier.getId());
			ArrayList<AlarmSettingsElement> list=new ArrayList<AlarmSettingsElement>();
			while(rs.next()){
				AlarmSettingsElement element=new AlarmSettingsElement();
				element.setId(rs.getInt("id"));
				element.setModuleId(rs.getString("module_name"));
				element.setModuleAddress(rs.getString("module_address"));
				element.setEnabled(rs.getInt("is_enabled")>0);
				list.add(element);
			}
			returnValue=list.toArray(returnValue);
		}catch(Exception ex){
			logger.error("AlarmSettingsImplementation Exception:"+ex.getMessage());
		}finally{
			connector.close();
		}
		return returnValue;
	}

	
	
	@Override
	public boolean updateList(MonitorIdentifier monitorIdentifier, AlarmSettingsElement[] list) {
		boolean returnValue=true;
		if(list!=null){
			ConnectWrap connector=StaticConnector.getConnectWrap();
			try{
				Session session=connector.getSession();
				for(int counter=0;counter<list.length;counter++){
					list[counter].getId();
					MonitorSettingsAlarm element=(MonitorSettingsAlarm)session.get(MonitorSettingsAlarm.class, new Integer(list[counter].getId()));
					if(element!=null){
						if(  (element.getIsEnabled()>0)!=list[counter].isEnabled() ){
							element.setIsEnabled(list[counter].isEnabled()?1:0);
							session.beginTransaction();
							session.update(element);
							session.getTransaction().commit();
						}
					}else{
						logger.debug("получен от удаленного модуля AlarmSettingsAlarm, который уже был удален");
					}
				}
			}catch(Exception ex){
				logger.error("updateList Exception:"+ex.getMessage());
				returnValue=false;
			}finally{
				connector.close();
			}
		}else{
			logger.debug("нет данных для обновления ");
		}
		return returnValue;
	}

	
	public static void main(String[] args){
		System.out.println("begin");
		AlarmSettingsImplementation implementation=new AlarmSettingsImplementation();
		MonitorIdentifier monitorIdentifier=new MonitorIdentifier();
		
		AlarmSettingsElement[] list=implementation.getList(monitorIdentifier);
		AlarmSettingsElement element=list[0];
		element.setEnabled(false);
		for(int counter=0;counter<list.length;counter++){
			System.out.println(counter+" : "+list[counter].getModuleAddress());
		}
		implementation.updateList(monitorIdentifier, new AlarmSettingsElement[]{element});
		System.out.println("end");
	}
}
