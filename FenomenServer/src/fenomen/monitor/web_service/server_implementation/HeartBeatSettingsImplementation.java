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

/** ��������� ���������� ������� HeartBeat ��� ��������� */
public class HeartBeatSettingsImplementation implements IHeartBeatSettings{
	private Logger logger=Logger.getLogger(this.getClass());
	
	/** ��������� ���������� ������� HeartBeat ��� ��������� */
	public HeartBeatSettingsImplementation(){
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public HeartBeatSettingsElement[] getList(MonitorIdentifier monitorIdentifier) {
		HeartBeatSettingsElement[] returnValue=new HeartBeatSettingsElement[]{};
		ConnectWrap connector=StaticConnector.getConnectWrap();
		List<MonitorSettingsHeartBeat> listOfMonitorSettings=null;
		try{
			logger.debug("�������� ������ ���� ������� � �������");
			Session session=connector.getSession();
			List<Module> listOfModule=(List<Module>)session.createCriteria(Module.class).addOrder(Order.asc("id")).list();
			logger.debug("�������� ������ ���� HeartBeatSettings ������� ( �� ������� ��������) ");
			listOfMonitorSettings=(List<MonitorSettingsHeartBeat>)session.createCriteria(MonitorSettingsHeartBeat.class).add(Restrictions.eq("idMonitor", monitorIdentifier.getId())).addOrder(Order.asc("idModule")).list();
			
			logger.debug("�������� ������ ����� MonitorSettingsHeartBeat, �� ������� ��� ��� ������� (���� �������) ");
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
					logger.debug("������� ����� ��������� MonitorSettingsHeartBeat �� ������� ��� ��� ������ ");
					moduleSettingsRemove.add(listOfMonitorSettings.get(counter));
				}
			}
			logger.debug("�������� ��������� '�������' �������� (�� ������� ��� ��� �������)");
			if(moduleSettingsRemove.size()>0){
				session.beginTransaction();
				for(int counter=0;counter<moduleSettingsRemove.size();counter++){
					session.delete(moduleSettingsRemove.get(counter));
				}
				session.getTransaction().commit();
				// ����� �� ��������� 
				//logger.debug("�������� ������ ���� �������� ������� �� ������� �������� ");
				//listOfMonitorSettings=(List<MonitorSettingsHeartBeat>)session.createCriteria(MonitorSettingsHeartBeat.class).add(Restrictions.eq("idMonitor", monitorIdentifier.getId())).addOrder(Order.asc("idModule")).list();
			}
			
			logger.debug("�������� ������ ����� Module, ������� ��� � MonitorSettingsHeartBeat � �������� �� � ������ ");
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
					logger.debug("������� ����� MonitorSettingsHeartBeat �� �������� ������ "+moduleId);
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
			logger.debug("�������� ������ ��� �������� �� ��������� ������� ");
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
				// ���������� ����� � �������� ( �������� � ������� module_settings ) � ������������
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

	/** ������� �������� ����� ��� ��������� ������� ��������� HeartBeat ���������� ��� ������ ( ������ ��� ������������� �� ������ �������� ) 
	 * <br>
	 * @param tryValue - ��������, ������� �������� �� ��������
	 * @param ���������� � ����� ������ 
	 * @return ��������, ������� ����� ����������
	 * */
	private int checkSetHeartBeatTimeWait(int tryValue, int moduleId, ConnectWrap connector){
		int returnValue=tryValue;
		// �������� �������� Settings �� ����
		try{
			// INFO ������.������� heart_beat ���������� ��������� ��������
			Connection connection=connector.getConnection();
			ResultSet rs=connection.createStatement().executeQuery("select module_settings.* from module_settings where id_section=1 and id_parameter=1 and id_module="+moduleId);
			rs.next();
			int value=Integer.parseInt(rs.getString("settings_value"))/1000;
			if(returnValue<=value){
				// ����������� ����� �������� - ������ � ��� ���� 
				returnValue=value*2;
				// ���������� ���������� �� ��������� � HeartBeatSettings �� ������� ������
				notifyHeartBeatSettingsChange=true;
			}
		}catch(Exception ex){
			logger.error("#checkTimeWait Exception: "+ex.getMessage());
		}
		return returnValue;
	}
	/** ����, ������� ��������������� ������ ��� ������������� ��������� �� ���������� � ���������� HeartBeat alarm */
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
							// ��������� ����� ��������� ��������� HeartBeat ���������� �� ���������� ������ ���� ������ ���� �����
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
						logger.debug("������� �� ���������� ������ MonitorSettingsHeartBeat, ������� ��� ��� ������");
					}
				}
			}catch(Exception ex){
				logger.error("updateList Exception:"+ex.getMessage());
				returnValue=false;
			}finally{
				connector.close();
			}
		}else{
			logger.debug("��� ������ ��� ���������� ");
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
