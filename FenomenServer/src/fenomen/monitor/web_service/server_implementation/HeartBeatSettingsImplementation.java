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
import database.wrap.MonitorSettingsHeartBeat;
import fenomen.monitor.notifier.worker.MonitorHeartBeatWatchDogFactory;
import fenomen.monitor.web_service.common.HeartBeatSettingsElement;
import fenomen.monitor.web_service.common.MonitorIdentifier;
import fenomen.monitor.web_service.interf.IHeartBeatSettings;

/** настройки оповещения события HeartBeat для мониторов */
public class HeartBeatSettingsImplementation implements IHeartBeatSettings{
	private Logger logger=Logger.getLogger(this.getClass());
	
	/** настройки оповещения события HeartBeat для мониторов */
	public HeartBeatSettingsImplementation(){
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public HeartBeatSettingsElement[] getList(MonitorIdentifier monitorIdentifier) {
		HeartBeatSettingsElement[] returnValue=new HeartBeatSettingsElement[]{};
		ConnectWrap connector=StaticConnector.getConnectWrap();
		List<MonitorSettingsHeartBeat> listOfMonitorSettings=null;
		try{
			logger.debug("получить список всех модулей в системе");
			Session session=connector.getSession();
			List<Module> listOfModule=(List<Module>)session.createCriteria(Module.class).addOrder(Order.asc("id")).list();
			logger.debug("получить список всех HeartBeatSettings модулей ( по данному монитору) ");
			listOfMonitorSettings=(List<MonitorSettingsHeartBeat>)session.createCriteria(MonitorSettingsHeartBeat.class).add(Restrictions.eq("idMonitor", monitorIdentifier.getId())).addOrder(Order.asc("idModule")).list();
			
			logger.debug("получить список таких MonitorSettingsHeartBeat, по которым уже нет модулей (были удалены) ");
			ArrayList<MonitorSettingsHeartBeat> moduleSettingsRemove=new ArrayList<MonitorSettingsHeartBeat>();
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
					logger.debug("найдена такая настройка MonitorSettingsHeartBeat по которой уже нет модуля ");
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
				//listOfMonitorSettings=(List<MonitorSettingsHeartBeat>)session.createCriteria(MonitorSettingsHeartBeat.class).add(Restrictions.eq("idMonitor", monitorIdentifier.getId())).addOrder(Order.asc("idModule")).list();
			}
			
			logger.debug("получить список таких Module, которых нет в MonitorSettingsHeartBeat и добавить их в список ");
			for(int counter=0;counter<listOfModule.size();counter++){
				int moduleId=listOfModule.get(counter).getId();
				MonitorSettingsHeartBeat currentSettings=null;
				for(int index=0;index<listOfMonitorSettings.size();index++){
					if(moduleId==listOfMonitorSettings.get(index).getIdModule()){
						currentSettings=listOfMonitorSettings.get(index);
						break;
					}
				}
				if(currentSettings==null){
					logger.debug("создать новый MonitorSettingsHeartBeat по текущему модулю "+moduleId);
					MonitorSettingsHeartBeat newSettings=new MonitorSettingsHeartBeat();
					newSettings.setIdModule(moduleId);
					newSettings.setIdMonitor(monitorIdentifier.getId());
					newSettings.setIsEnabled(0);
					newSettings.setTimeWait(600);
					session.beginTransaction();
					session.save(newSettings);
					session.getTransaction().commit();
				}
			}
			logger.debug("получить список для отправки на удаленный монитор ");
			Connection connection=connector.getConnection();
			ResultSet rs=connection.createStatement().executeQuery("select module.id_module module_name, module.address module_address, module_settings.settings_value, monitor_settings_heart_beat.* from monitor_settings_heart_beat inner join module on module.id=monitor_settings_heart_beat.id_module left join module_settings on module.id=module_settings.id_module and module_settings.id_section=1 and module_settings.id_parameter=1 where monitor_settings_heart_beat.id_monitor="+monitorIdentifier.getId());
			ArrayList<HeartBeatSettingsElement> list=new ArrayList<HeartBeatSettingsElement>();
			while(rs.next()){
				HeartBeatSettingsElement element=new HeartBeatSettingsElement();
				element.setId(rs.getInt("id"));
				element.setModuleId(rs.getString("module_name"));
				element.setModuleAddress(rs.getString("module_address"));
				element.setEnabled(rs.getInt("is_enabled")>0);
				element.setTimeWait(rs.getInt("time_wait"));
				// установить время в секундах ( хранится в таблице module_settings ) в милисекундах
				try{
					element.setSettingsValue(Integer.parseInt(rs.getString("settings_value"))/1000);
				}catch(Exception ex){};
				list.add(element);
			}
			returnValue=list.toArray(returnValue);
		}catch(Exception ex){
			logger.error("HeartBeatSettingsImplementation Exception:"+ex.getMessage());
		}finally{
			connector.close();
		}
		return returnValue;
	}

	/** вернуть валидное время для установки времени появления HeartBeat оповещения для модуля ( больше чем установленное на модуле значение ) 
	 * <br>
	 * @param tryValue - значение, которое передано от монитора
	 * @param соединение с базой данных 
	 * @return значение, которое нужно установить
	 * */
	private int checkSetHeartBeatTimeWait(int tryValue, int moduleId, ConnectWrap connector){
		int returnValue=tryValue;
		// получить значение Settings из базы
		try{
			// INFO сервер.монитор heart_beat оповещение изменение настроек
			Connection connection=connector.getConnection();
			ResultSet rs=connection.createStatement().executeQuery("select module_settings.* from module_settings where id_section=1 and id_parameter=1 and id_module="+moduleId);
			rs.next();
			int value=Integer.parseInt(rs.getString("settings_value"))/1000;
			if(returnValue<=value){
				// минимальное время ожидание - больше в два раза 
				returnValue=value*2;
				// необходимо оповестить об изменении в HeartBeatSettings по данному модулю
				notifyHeartBeatSettingsChange=true;
			}
		}catch(Exception ex){
			logger.error("#checkTimeWait Exception: "+ex.getMessage());
		}
		return returnValue;
	}
	/** флаг, который устанавливается только при необходимости оповщения об изменениях в настройках HeartBeat alarm */
	private boolean notifyHeartBeatSettingsChange=false;
	
	@Override
	public boolean updateList(MonitorIdentifier monitorIdentifier, HeartBeatSettingsElement[] list) {
		boolean returnValue=true;
		if(list!=null){
			ConnectWrap connector=StaticConnector.getConnectWrap();
			try{
				Session session=connector.getSession();
				for(int counter=0;counter<list.length;counter++){
					list[counter].getId();
					MonitorSettingsHeartBeat element=(MonitorSettingsHeartBeat)session.get(MonitorSettingsHeartBeat.class, new Integer(list[counter].getId()));
					if(element!=null){
						if((  (element.getIsEnabled()>0)!=list[counter].isEnabled() )||(element.getTimeWait()!=list[counter].getTimeWait())){
							element.setIsEnabled(list[counter].isEnabled()?1:0);
							// проверить время установки появления HeartBeat оповещения на валидность должно быть больше либо равно
							element.setTimeWait(checkSetHeartBeatTimeWait(list[counter].getTimeWait(),element.getIdModule(), connector)); 
							session.beginTransaction();
							session.update(element);
							session.getTransaction().commit();
							if(notifyHeartBeatSettingsChange==true){
								MonitorHeartBeatWatchDogFactory.getInstance().updateSettingsByModule(element.getIdModule(),element.getIdMonitor());
								notifyHeartBeatSettingsChange=false;
							}
						}
					}else{
						logger.debug("получен от удаленного модуля MonitorSettingsHeartBeat, который уже был удален");
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
		HeartBeatSettingsImplementation implementation=new HeartBeatSettingsImplementation();
		MonitorIdentifier monitorIdentifier=new MonitorIdentifier();
		
		HeartBeatSettingsElement[] list=implementation.getList(monitorIdentifier);
		HeartBeatSettingsElement element=list[0];
		element.setEnabled(false);
		for(int counter=0;counter<list.length;counter++){
			System.out.println(counter+" : "+list[counter].getModuleAddress());
		}
		implementation.updateList(monitorIdentifier, new HeartBeatSettingsElement[]{element});
		System.out.println("end");
	}
}
