package fenomen.monitor.notifier.worker;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.MonitorEventHeartBeat;

/** ������-���������� ������ ��� ������ �� "��������" �������� �������� HeartBeat */
public class MonitorHeartBeatWatchDog{
	private Logger logger=Logger.getLogger(this.getClass());
	/** ������ ���� ���������, ������� ������ ���� ��������� � �������  */
	private ArrayList<MonitorWatchDog> listOfMonitorWatchDog=new ArrayList<MonitorWatchDog>();
	/** ���������� ������������� ������, �� �������� ������ ������ ���������� ������ */
	private int moduleId;
	
	
	/** ������-���������� ������ ��� ������ �� "��������" �������� �������� HeartBeat 
	 * <br>
	 * �������� ����� ��������
	 * @param moduleId - ���������� ������������� ������, �� ��������� �������� ��������� ������ ���������� ������  
	 * */
	public MonitorHeartBeatWatchDog(int moduleId){
		this.moduleId=moduleId;
		init();
	}
	
	

	// logger.debug("���������� ������� �"+this.listOfMonitorWatchDog.get(counter).getIdMonitor()+"  � ����������� ������� HeartBeat ");
	// this.notifyAboutEventHeartBeat(this.listOfMonitorWatchDog.get(counter).getIdMonitor());
	
	
	/** �������������� ������������� ������� ����������� ������� */
	private void init(){
		logger.debug("�������������");
		this.updateSettings();
	}
	
	/** �������� ���������� ������������� ������, �� �������� ������� ������ ���������� ������ */
	public int getModuleId(){
		return this.moduleId; 
	}

	/** ���������� � ������������� ���������� �������� (���� ���������) �� ������� ����������� �������  */
	public void updateSettings() {
		logger.debug("��������� �������� ��������� �� ������: "+this.moduleId);
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			logger.debug("���������� ��� ������"); 
			this.stopThread();
			logger.debug("�������� ������"); 
			this.listOfMonitorWatchDog.clear();
			ResultSet rs=connector.getConnection().createStatement().executeQuery("select * from monitor_settings_heart_beat where id_module="+this.moduleId+" and is_enabled>0");
			while(rs.next()){
				MonitorWatchDog monitor=new MonitorWatchDog(this.moduleId,
						  rs.getInt("id_monitor"),
						  rs.getInt("time_wait")); // !!! � �������� !!!
				monitor.start();
				listOfMonitorWatchDog.add(monitor);
			}
			logger.debug("����������� �� �����������");
		}catch(Exception ex){
			logger.error("init Exception:"+ex.getMessage());
		}finally{
			connector.close();
		}
	}

	/** ���������� � ������������� ���������� �������� �� ������� ����������� ������� �� ������������� ��������  */
	public void updateSettings(int idMonitor) {
		logger.debug("���������� �������� �� ������ "+this.moduleId+" �� ������������� ��������: "+idMonitor);
		MonitorWatchDog monitor=this.getMonitorWatchDogByIdMonitor(idMonitor);
		if(monitor!=null){
			logger.debug("������� ������ - ���������� ��������� ");
			ConnectWrap connector=StaticConnector.getConnectWrap();
			try{
				monitor.stopThread();
				try{
					// ������ ����� ���� ������ ��������� ������ 
					this.listOfMonitorWatchDog.remove(monitor);
				}catch(Exception ex){};
				
				ResultSet rs=connector.getConnection().createStatement().executeQuery("select * from monitor_settings_heart_beat where id_module="+this.moduleId+" and is_enabled>0 and id_monitor="+idMonitor);
				if(rs.next()){
					MonitorWatchDog monitorNew=new MonitorWatchDog(this.moduleId);
					monitorNew.setIdMonitor(rs.getInt("id_monitor"));
					monitorNew.setTimeWait(rs.getInt("time_wait")); // !!! � �������� !!!
					monitorNew.start();
					logger.debug("������� ������ ");
					listOfMonitorWatchDog.add(monitorNew);
				}else{
					// ����� ������������� ���� ��� ��������.������ - �� ��������� � ��������  
				}
			}catch(Exception ex){
				logger.error("init Exception:"+ex.getMessage());
			}finally{
				connector.close();
			}
		}else{
			logger.debug("������� �� ������ - �������� ������ ��������/������ � ���� ");
			ConnectWrap connector=StaticConnector.getConnectWrap();
			try{
				ResultSet rs=connector.getConnection().createStatement().executeQuery("select * from monitor_settings_heart_beat where id_module="+this.moduleId+" and is_enabled>0 and id_monitor="+idMonitor);
				if(rs.next()){
					MonitorWatchDog monitorNew=new MonitorWatchDog(this.moduleId);
					monitorNew.setIdMonitor(rs.getInt("id_monitor"));
					monitorNew.setTimeWait(rs.getInt("time_wait")); // !!! � �������� !!!
					monitorNew.start();
					logger.debug("������� ������ ");
					listOfMonitorWatchDog.add(monitorNew);
				}else{
					logger.info("�������� ���������� � ������������� UpdateSettings for Monitor:"+idMonitor+" � ��� ��� � ������ �� ����������, �������� �������� � ��������� ������ is_enabled=false ");
				}
			}catch(Exception ex){
				logger.error("init Exception:"+ex.getMessage());
			}finally{
				connector.close();
			}
		}
		
	}
	
	/** �������� ������� �� ������ ���� ��������� �� ����������� id  */
	private MonitorWatchDog getMonitorWatchDogByIdMonitor(int idMonitor){
		MonitorWatchDog monitor=null;
		for(int counter=0;counter<this.listOfMonitorWatchDog.size();counter++){
			if(this.listOfMonitorWatchDog.get(counter).getIdMonitor()==idMonitor){
				monitor=this.listOfMonitorWatchDog.get(counter);
				break;
			}
		}
		return monitor;
	}
	
	/** ���������� � �������� ������� HeartBeat �� ���������� ������ */
	public void eventHeartBeatGetFromModule(){
		logger.debug("�������� ��������� HeartBeat �� ������:"+this.moduleId);
		// ������� ���� ������� ���������� � ������� ������ �������
		for(int counter=0;counter<this.listOfMonitorWatchDog.size();counter++){
			this.listOfMonitorWatchDog.get(counter).interrupt();
		}
	}


	/** ���������� ��� �������� ������ */
	public void stopThread() {
		// ������� ���� ������� ������ � ����������� ������ 
		for(int counter=0;counter<this.listOfMonitorWatchDog.size();counter++){
			this.listOfMonitorWatchDog.get(counter).stopThread();
		}
		try{
			this.listOfMonitorWatchDog.clear();
		}catch(Exception ex){};
	}
}

/** ������-������� ��� �������� �� ������� MonitorHeartBeatWatchDog */
class MonitorWatchDog extends Thread{
	private Logger logger=Logger.getLogger(this.getClass());
	private int moduleId;
	/** ���������� ��� �������� monitor.id � �������� ����  */
	private int idMonitor;
	/** ����� �������� ������� � �������� */
	private int timeWait;
	
	/** ������-������� ��� �������� �� ������� MonitorHeartBeatWatchDog
	 * @param moduleId - ���������� ������������� ������  
	 * */
	public MonitorWatchDog(int moduleId){
		this.moduleId=moduleId;
	}
	
	/** ������-������� ��� �������� �� ������� MonitorHeartBeatWatchDog
	 * @param moduleId - ���������� ������������� ������ 
	 * @param idMonitor - ���������� ������������� �������� (monitor.id) 
	 * @param timeWait - ����� �������� ��� ������� �������� 
	 * */
	public MonitorWatchDog(int moduleId, int idMonitor, int timeWait){
		this.moduleId=moduleId;
		this.idMonitor=idMonitor;
		this.timeWait=timeWait;
	}
	
	
	private boolean flagRun=true;
	
	@Override
	public void run(){
		while(flagRun){
			try{
				TimeUnit.SECONDS.sleep(this.timeWait);
				notifyAboutEventHeartBeat(idMonitor);
			}catch(InterruptedException ex){
				logger.debug("����� ��� �������"); 
			}
		}
	}
	
	
	/** ���������� ������� � ���������� ������� HeartBeat */
	public void notifyAboutEventHeartBeat(int idMonitor){
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			logger.debug("�������� � ������� monitor_event_heart_beat �������");
			MonitorEventHeartBeat event=new MonitorEventHeartBeat();
			event.setIdModuleHeartBeat(0);// TODO ������. ���������� ��������� �� ���������� �� ������� ������
			event.setIdMonitor(idMonitor);
			event.setIdMonitorEventState(1);// new 
			event.setStateTimeWrite(new Date());// state date
			event.setIdMonitorEventResolve(0); // not resolve
			event.setResolveTimeWrite(null); // default 
			event.setIdModule(this.moduleId); // current module 
			Session session=connector.getSession();
			session.beginTransaction();
			session.save(event);
			session.getTransaction().commit();
			logger.debug("���������� ���������� � ������� ������� HeartBeat ");
			MonitorWorkerFactory.getInstance().pulseEventHeartBeat();
		}catch(Exception ex){
			logger.error("������ �� ����� ������� �������� ������� �� �������� ");
		}finally{
			connector.close();
		}
	}
	
	public void stopThread(){
		this.flagRun=false;
		this.interrupt();
	}
	

	/** ���������� ��� �������� monitor.id � �������� ����  */
	public int getIdMonitor() {
		return idMonitor;
	}

	/** ���������� ��� �������� monitor.id � �������� ����  */
	public void setIdMonitor(int idMonitor) {
		this.idMonitor = idMonitor;
	}

	/** ����� �������� ������� � �������� */
	public int getTimeWait() {
		return timeWait;
	}

	/** ����� �������� ������� � �������� */
	public void setTimeWait(int timeWait) {
		this.timeWait = timeWait;
	}
}
