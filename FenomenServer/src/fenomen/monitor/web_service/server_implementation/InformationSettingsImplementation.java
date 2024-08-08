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

/** ��������� ���������� ������� Information ��� ��������� */
public class InformationSettingsImplementation implements IInformationSettings{
	private Logger logger=Logger.getLogger(this.getClass());
	
	/** ��������� ���������� ������� Information ��� ��������� */
	public InformationSettingsImplementation(){
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public InformationSettingsElement[] getList(MonitorIdentifier monitorIdentifier) {
		InformationSettingsElement[] returnValue=new InformationSettingsElement[]{};
		ConnectWrap connector=StaticConnector.getConnectWrap();
		List<MonitorSettingsInformation> listOfMonitorSettings=null;
		try{
			logger.debug("�������� ������ ���� ������� � �������");
			Session session=connector.getSession();
			List<Module> listOfModule=(List<Module>)session.createCriteria(Module.class).addOrder(Order.asc("id")).list();
			logger.debug("�������� ������ ���� InformationSettings ������� ( �� ������� ��������) ");
			listOfMonitorSettings=(List<MonitorSettingsInformation>)session.createCriteria(MonitorSettingsInformation.class).add(Restrictions.eq("idMonitor", monitorIdentifier.getId())).addOrder(Order.asc("idModule")).list();
			
			logger.debug("�������� ������ ����� MonitorSettingsInformation, �� ������� ��� ��� ������� (���� �������) ");
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
					logger.debug("������� ����� ��������� MonitorSettingsInformation �� ������� ��� ��� ������ ");
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
				//listOfMonitorSettings=(List<MonitorSettingsInformation>)session.createCriteria(MonitorSettingsInformation.class).add(Restrictions.eq("idMonitor", monitorIdentifier.getId())).addOrder(Order.asc("idModule")).list();
			}
			
			logger.debug("�������� ������ ����� Module, ������� ��� � MonitorSettingsInformation � �������� �� � ������ ");
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
					logger.debug("������� ����� MonitorSettingsInformation �� �������� ������ "+moduleId);
					MonitorSettingsInformation newSettings=new MonitorSettingsInformation();
					newSettings.setIdModule(moduleId);
					newSettings.setIdMonitor(monitorIdentifier.getId());
					newSettings.setIsEnabled(0);
					session.beginTransaction();
					session.save(newSettings);
					session.getTransaction().commit();
				}
			}
			logger.debug("�������� ������ ��� �������� �� ��������� ������� ");
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
						logger.debug("������� �� ���������� ������ MonitorSettingsInformation, ������� ��� ��� ������");
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
