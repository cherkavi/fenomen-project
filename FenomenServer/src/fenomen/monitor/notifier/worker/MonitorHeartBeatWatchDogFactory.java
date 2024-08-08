package fenomen.monitor.notifier.worker;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import database.ConnectWrap;
import database.StaticConnector;

/** ������, ������� ������� �������� �� ��������� ������� ������� HeartBeat � ���������� �������-���������� HeartBeat � ������� ������� */
public class MonitorHeartBeatWatchDogFactory{
	private static MonitorHeartBeatWatchDogFactory instance;
	static {
		instance=new MonitorHeartBeatWatchDogFactory();
	}
	/** ������, ������� ������� �������� �� ��������� ������� ������� HeartBeat � ���������� �������-���������� HeartBeat � ������� ������� */
	public static MonitorHeartBeatWatchDogFactory getInstance(){
		return instance;
	}
	
	private Logger logger=Logger.getLogger(this.getClass()); 
	
	/** ������ ���� ������������������ MonitorHeartBeatWatchDog ���������� �� ������  */
	private ArrayList<MonitorHeartBeatWatchDog> list=new ArrayList<MonitorHeartBeatWatchDog>();
	
	
	/** ������, ������� ������� �������� �� ��������� ������� ������� HeartBeat � ���������� �������-���������� HeartBeat � ������� ������� */
	private MonitorHeartBeatWatchDogFactory(){
		// �������� ��� ������ �� ������� � �������� �� ���������� Id � ������  
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			ResultSet rs=connector.getConnection().createStatement().executeQuery("select * from module");
			while(rs.next()){
				this.addModule(rs.getInt("id"));
			}
		}catch(Exception ex){
			logger.error("constructor Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}
	
	/** �������� ��������� �� ������  
	 * @param idModule - ��� ������ �� ������� module.id
	 * */
	public void updateSettingsByModule(int idModule){
		MonitorHeartBeatWatchDog elementInList=this.getFromList(idModule);
		if(elementInList==null){
			logger.debug("���������� ������ �� ������ ");
		}else{
			logger.debug("���������� � ������������� ���������� ������ ��� ����������� �������:"+idModule);
			elementInList.updateSettings();
		}
	}

	/** �������� ��������� �� ������.��������  
	 * @param idModule - ��� ������ �� ������� module.id
	 * @param idMonitor - ��� ��������
	 * */
	public void updateSettingsByModule(int idModule, int idMonitor) {
		MonitorHeartBeatWatchDog elementInList=this.getFromList(idModule);
		if(elementInList==null){
			logger.debug("���������� ������ �� ������ ");
		}else{
			logger.debug("���������� � ������������� ���������� ������ ��� ����������� ������� �� �������� :"+idModule);
			elementInList.updateSettings(idMonitor);
		}
	}
	
	/** ���������� � ������� �� ���������� ������ ������� HeartBeat  
	 * @param idModule - ��� ������ �� ������� module.id
	 * */
	public void moduleSendHeartBeat(int idModule){
		MonitorHeartBeatWatchDog elementInList=this.getFromList(idModule);
		if(elementInList==null){
			logger.debug("���������� ������ �� ������: "+idModule);
		}else{
			logger.debug("���������� ����������� ������� � ������: "+idModule);
			elementInList.eventHeartBeatGetFromModule();
		}
	}

	/** ���������� �� �������� ������ */
	public void removeModule(int idModule){
		// TODO ������.�������� ������ ����� Controller2
		MonitorHeartBeatWatchDog elementInList=this.getFromList(idModule);
		if(elementInList==null){
			logger.debug("���������� ������ �� ������: "+idModule);
		}else{
			elementInList.stopThread();
			this.list.remove(elementInList);
			logger.debug("���������� ������ ������, ������: "+idModule);
		}
	}
	
	/** ���������� � ���������� ������ � �������  */
	public void addModule(int idModule){
		MonitorHeartBeatWatchDog elementInList=this.getFromList(idModule);
		if(elementInList==null){
			logger.debug("�������� ���������� ������:"+idModule);
			this.list.add(new MonitorHeartBeatWatchDog(idModule));
		}else{
			logger.debug("���������� ������ ��� �������� "+idModule);
		}
	}
	
	/** �������� ������� �� �������  */
	private MonitorHeartBeatWatchDog getFromList(int idModule){
		MonitorHeartBeatWatchDog returnValue=null;
		for(int counter=0;counter<this.list.size();counter++){
			if(list.get(counter).getModuleId()==idModule){
				returnValue=list.get(counter);
				break;
			}
		}
		return returnValue;
	}
}
