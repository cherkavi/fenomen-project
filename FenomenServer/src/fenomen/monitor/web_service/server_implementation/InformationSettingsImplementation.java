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
import database.wrap.MonitorSettingsInformation;
import fenomen.monitor.web_service.common.InformationSettingsElement;
import fenomen.monitor.web_service.common.MonitorIdentifier;
import fenomen.monitor.web_service.interf.IInformationSettings;

/** настройки оповещения события Information для мониторов */
public class InformationSettingsImplementation implements IInformationSettings{
	private Logger logger=Logger.getLogger(this.getClass());
	
	/** настройки оповещения события Information для мониторов */
	public InformationSettingsImplementation(){
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public InformationSettingsElement[] getList(MonitorIdentifier monitorIdentifier) {
		InformationSettingsElement[] returnValue=new InformationSettingsElement[]{};
		ConnectWrap connector=StaticConnector.getConnectWrap();
		List<MonitorSettingsInformation> listOfMonitorSettings=null;
		try{
			logger.debug("получить список всех модулей в системе");
			Session session=connector.getSession();
			List<Module> listOfModule=(List<Module>)session.createCriteria(Module.class).addOrder(Order.asc("id")).list();
			logger.debug("получить список всех InformationSettings модулей ( по данному монитору) ");
			listOfMonitorSettings=(List<MonitorSettingsInformation>)session.createCriteria(MonitorSettingsInformation.class).add(Restrictions.eq("idMonitor", monitorIdentifier.getId())).addOrder(Order.asc("idModule")).list();
			
			logger.debug("получить список таких MonitorSettingsInformation, по которым уже нет модулей (были удалены) ");
			ArrayList<MonitorSettingsInformation> moduleSettingsRemove=new ArrayList<MonitorSettingsInformation>();
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
					logger.debug("найдена такая настройка MonitorSettingsInformation по которой уже нет модуля ");
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
				//listOfMonitorSettings=(List<MonitorSettingsInformation>)session.createCriteria(MonitorSettingsInformation.class).add(Restrictions.eq("idMonitor", monitorIdentifier.getId())).addOrder(Order.asc("idModule")).list();
			}
			
			logger.debug("получить список таких Module, которых нет в MonitorSettingsInformation и добавить их в список ");
			for(int counter=0;counter<listOfModule.size();counter++){
				int moduleId=listOfModule.get(counter).getId();
				MonitorSettingsInformation currentSettings=null;
				for(int index=0;index<listOfMonitorSettings.size();index++){
					if(moduleId==listOfMonitorSettings.get(index).getIdModule()){
						currentSettings=listOfMonitorSettings.get(index);
						break;
					}
				}
				if(currentSettings==null){
					logger.debug("создать новый MonitorSettingsInformation по текущему модулю "+moduleId);
					MonitorSettingsInformation newSettings=new MonitorSettingsInformation();
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
			ResultSet rs=connection.createStatement().executeQuery("select module.id_module module_name, module.address module_address, monitor_settings_Information.* from monitor_settings_Information inner join module on module.id=monitor_settings_Information.id_module where monitor_settings_Information.id_monitor="+monitorIdentifier.getId());
			ArrayList<InformationSettingsElement> list=new ArrayList<InformationSettingsElement>();
			while(rs.next()){
				InformationSettingsElement element=new InformationSettingsElement();
				element.setId(rs.getInt("id"));
				element.setModuleId(rs.getString("module_name"));
				element.setModuleAddress(rs.getString("module_address"));
				element.setEnabled(rs.getInt("is_enabled")>0);
				list.add(element);
			}
			returnValue=list.toArray(returnValue);
		}catch(Exception ex){
			logger.error("InformationSettingsImplementation Exception:"+ex.getMessage());
		}finally{
			connector.close();
		}
		return returnValue;
	}

	
	
	@Override
	public boolean updateList(MonitorIdentifier monitorIdentifier, InformationSettingsElement[] list) {
		boolean returnValue=true;
		if(list!=null){
			ConnectWrap connector=StaticConnector.getConnectWrap();
			try{
				Session session=connector.getSession();
				for(int counter=0;counter<list.length;counter++){
					list[counter].getId();
					MonitorSettingsInformation element=(MonitorSettingsInformation)session.get(MonitorSettingsInformation.class, new Integer(list[counter].getId()));
					if(element!=null){
						if(  (element.getIsEnabled()>0)!=list[counter].isEnabled() ){
							element.setIsEnabled(list[counter].isEnabled()?1:0);
							session.beginTransaction();
							session.update(element);
							session.getTransaction().commit();
						}
					}else{
						logger.debug("получен от удаленного модуля MonitorSettingsInformation, который уже был удален");
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
		InformationSettingsImplementation implementation=new InformationSettingsImplementation();
		MonitorIdentifier monitorIdentifier=new MonitorIdentifier();
		
		InformationSettingsElement[] list=implementation.getList(monitorIdentifier);
		InformationSettingsElement element=list[0];
		element.setEnabled(false);
		for(int counter=0;counter<list.length;counter++){
			System.out.println(counter+" : "+list[counter].getModuleAddress());
		}
		implementation.updateList(monitorIdentifier, new InformationSettingsElement[]{element});
		System.out.println("end");
	}
}
