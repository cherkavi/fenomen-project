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
import database.wrap.MonitorSettingsRestart;
import fenomen.monitor.web_service.common.MonitorIdentifier;
import fenomen.monitor.web_service.common.RestartSettingsElement;
import fenomen.monitor.web_service.interf.IRestartSettings;

/** настройки оповещения события Restart для мониторов */
public class RestartSettingsImplementation implements IRestartSettings{
	private Logger logger=Logger.getLogger(this.getClass());
	
	/** настройки оповещения события Restart для мониторов */
	public RestartSettingsImplementation(){
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public RestartSettingsElement[] getList(MonitorIdentifier monitorIdentifier) {
		RestartSettingsElement[] returnValue=new RestartSettingsElement[]{};
		ConnectWrap connector=StaticConnector.getConnectWrap();
		List<MonitorSettingsRestart> listOfMonitorSettings=null;
		try{
			logger.debug("получить список всех модулей в системе");
			Session session=connector.getSession();
			List<Module> listOfModule=(List<Module>)session.createCriteria(Module.class).addOrder(Order.asc("id")).list();
			logger.debug("получить список всех RestartSettings модулей ( по данному монитору) ");
			listOfMonitorSettings=(List<MonitorSettingsRestart>)session.createCriteria(MonitorSettingsRestart.class).add(Restrictions.eq("idMonitor", monitorIdentifier.getId())).addOrder(Order.asc("idModule")).list();
			
			logger.debug("получить список таких MonitorSettingsRestart, по которым уже нет модулей (были удалены) ");
			ArrayList<MonitorSettingsRestart> moduleSettingsRemove=new ArrayList<MonitorSettingsRestart>();
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
					logger.debug("найдена такая настройка MonitorSettingsRestart по которой уже нет модуля ");
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
				//listOfMonitorSettings=(List<MonitorSettingsRestart>)session.createCriteria(MonitorSettingsRestart.class).add(Restrictions.eq("idMonitor", monitorIdentifier.getId())).addOrder(Order.asc("idModule")).list();
			}
			
			logger.debug("получить список таких Module, которых нет в MonitorSettingsRestart и добавить их в список ");
			for(int counter=0;counter<listOfModule.size();counter++){
				int moduleId=listOfModule.get(counter).getId();
				MonitorSettingsRestart currentSettings=null;
				for(int index=0;index<listOfMonitorSettings.size();index++){
					if(moduleId==listOfMonitorSettings.get(index).getIdModule()){
						currentSettings=listOfMonitorSettings.get(index);
						break;
					}
				}
				if(currentSettings==null){
					logger.debug("создать новый MonitorSettingsRestart по текущему модулю "+moduleId);
					MonitorSettingsRestart newSettings=new MonitorSettingsRestart();
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
			ResultSet rs=connection.createStatement().executeQuery("select module.id_module module_name, module.address module_address, monitor_settings_restart.* from monitor_settings_restart inner join module on module.id=monitor_settings_restart.id_module where monitor_settings_restart.id_monitor="+monitorIdentifier.getId());
			ArrayList<RestartSettingsElement> list=new ArrayList<RestartSettingsElement>();
			while(rs.next()){
				RestartSettingsElement element=new RestartSettingsElement();
				element.setId(rs.getInt("id"));
				element.setModuleId(rs.getString("module_name"));
				element.setModuleAddress(rs.getString("module_address"));
				element.setEnabled(rs.getInt("is_enabled")>0);
				list.add(element);
			}
			returnValue=list.toArray(returnValue);
		}catch(Exception ex){
			logger.error("RestartSettingsImplementation Exception:"+ex.getMessage());
		}finally{
			connector.close();
		}
		return returnValue;
	}

	
	
	@Override
	public boolean updateList(MonitorIdentifier monitorIdentifier, RestartSettingsElement[] list) {
		boolean returnValue=true;
		if(list!=null){
			ConnectWrap connector=StaticConnector.getConnectWrap();
			try{
				Session session=connector.getSession();
				for(int counter=0;counter<list.length;counter++){
					list[counter].getId();
					MonitorSettingsRestart element=(MonitorSettingsRestart)session.get(MonitorSettingsRestart.class, new Integer(list[counter].getId()));
					if(element!=null){
						if(  (element.getIsEnabled()>0)!=list[counter].isEnabled() ){
							element.setIsEnabled(list[counter].isEnabled()?1:0);
							session.beginTransaction();
							session.update(element);
							session.getTransaction().commit();
						}
					}else{
						logger.debug("получен от удаленного модуля MonitorSettingsRestart, который уже был удален");
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
		RestartSettingsImplementation implementation=new RestartSettingsImplementation();
		MonitorIdentifier monitorIdentifier=new MonitorIdentifier();
		
		RestartSettingsElement[] list=implementation.getList(monitorIdentifier);
		RestartSettingsElement element=list[0];
		element.setEnabled(false);
		for(int counter=0;counter<list.length;counter++){
			System.out.println(counter+" : "+list[counter].getModuleAddress());
		}
		implementation.updateList(monitorIdentifier, new RestartSettingsElement[]{element});
		System.out.println("end");
	}
}
