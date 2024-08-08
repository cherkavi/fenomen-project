package fenomen.module.web_service.service_implementation;


import java.sql.ResultSet;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.Module;
import database.wrap.ModuleHeartBeat;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.service.IHeartBeat;
import fenomen.monitor.notifier.worker.MonitorHeartBeatWatchDogFactory;

public class HeartBeatImplementation implements IHeartBeat{
	static{
		Logger.getLogger("fenomen.module").setLevel(Level.DEBUG);
		Logger.getLogger("fenomen.module").addAppender(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} %5p %c{1}:%L - %m%n")));
	}
	Logger logger=Logger.getLogger(this.getClass());
	
	/** �������� ���������� id ������ �� �������������� 
	 * @param connector -  ���������� � ����� ������ 
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @return 
	 * <li> <b> value&lt0 </b>  �� ������� ���������������� ������ </li>
	 * <li> <b>value&gt0</b> ���� ������ ������� ��������������� </li>
	 */
	private int getIdFromIdentifier(ConnectWrap connector, ModuleIdentifier moduleIdentifier){
		int returnValue=(-1);
		try{
			Session session=connector.getSession();
			Module module=(Module)session.createCriteria(Module.class).add(Restrictions.eq("idModule", moduleIdentifier.getId())).uniqueResult();
			if(module!=null){
				returnValue=module.getId();
			}
		}catch(Exception ex){
			if(moduleIdentifier!=null){
				logger.error("getIdFromIdentifier: Exception: "+ex.getMessage()+" ModuleIdentifier is not recognized: "+moduleIdentifier.getId());
			}else{
				logger.error("getIdFromIdentifier ModuleIdentifier is null ");
			}
			
		}
		return returnValue; 
	}
	
	/** ��������� � ���� ������������ 
	 * @param connector - ���������� � ����� ������ 
	 * @param id - ���������� ������������� ������, �� ������� MODULE
	 * @param moduleIdentifier - ������������� ������, ������� ��� ������� ����� ������� 
	 * @return
	 */
	private boolean saveHeartBeat(ConnectWrap connector, int id, ModuleIdentifier moduleIdentifier ){
		boolean returnValue=false;
		if(id>0){
			try{
				Session session=connector.getSession();
				session.beginTransaction();
				ModuleHeartBeat moduleHeartBeat=new ModuleHeartBeat();
				moduleHeartBeat.setIdModule(id);
				session.save(moduleHeartBeat);
				session.getTransaction().commit();
				returnValue=true;
				// INFO ������.���������� �������� � ������� HeartBeat - �������� ������� �� ������� ������
				//monitorFilter.notifyHeartBeatEvent(connector, moduleHeartBeat);
				MonitorHeartBeatWatchDogFactory.getInstance().moduleSendHeartBeat(id);
			}catch(Exception ex){
				returnValue=false;
				logger.error("saveHeartBeat Exception: "+ex.getMessage());
			}
		}else{
			logger.error("saveHeartBeat id is not recognized, HeartBeat is not save "+moduleIdentifier.getId() );
		}
		return returnValue;
	}
	
	/** INFO ������. ���� �� ������� ��� ������ (��������� ������� Task (�������) ��� ������� ������ )*/
	private boolean isTaskExists(ConnectWrap connector, int idModule){
		boolean returnValue=false;
		ResultSet rs=null;
		try{
			rs=connector.getConnection().createStatement().executeQuery("select id from module_task where id_module="+idModule+" and id_state<=1");
			if(rs.next()){
				// ���� ������ �� ������ 
				returnValue=true;
			}else{
				// ��� ����� �� ������ 
				returnValue=false;
			}
		}catch(Exception ex){
			logger.error("idTaskExists Exception: "+ex.getMessage());
		}finally{
			try{
				rs.getStatement().close();
			}catch(Exception ex){};
		}
		return returnValue;
	}
	
	@Override
	public String hearBeat(ModuleIdentifier moduleIdentifier) {
		String returnValue=IHeartBeat.sendError;
		// INFO ������. ��������� ������ ������������
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			/** ������������� ������ � ���� ������ */
			int id=this.getIdFromIdentifier(connector, moduleIdentifier);
			// ��������� ������ ������������ 
			if(this.saveHeartBeat(connector, id, moduleIdentifier)){
				// ��������� ���� ������ �� ������� Task ��� ������� ������ 
				if(isTaskExists(connector,id)){
					returnValue=IHeartBeat.taskExists;
				}else{
					returnValue=IHeartBeat.sendOk;
				}
			}else{
				returnValue=IHeartBeat.sendError;
			}
		}catch(Exception ex){
			logger.error("heartBeat Exception: "+ex.getMessage());
		}finally{
			try{
				connector.close();
			}catch(Exception ex){};
		}
		return returnValue;
	}

}
