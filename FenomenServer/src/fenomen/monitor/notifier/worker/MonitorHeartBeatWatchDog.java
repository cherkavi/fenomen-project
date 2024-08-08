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

/** объект-сторожевой таймер для модуля по "слушанию" входящих сигналов HeartBeat */
public class MonitorHeartBeatWatchDog{
	private Logger logger=Logger.getLogger(this.getClass());
	/** список всех мониторов, которые должны быть оповещены о событии  */
	private ArrayList<MonitorWatchDog> listOfMonitorWatchDog=new ArrayList<MonitorWatchDog>();
	/** уникальный идентификатор модуля, по которому создан данный сторожевой таймер */
	private int moduleId;
	
	
	/** объект-сторожевой таймер для модуля по "слушанию" входящих сигналов HeartBeat 
	 * <br>
	 * стартует после создания
	 * @param moduleId - уникальный идентификатор модуля, на основании которого создается данный сторожевой таймер  
	 * */
	public MonitorHeartBeatWatchDog(int moduleId){
		this.moduleId=moduleId;
		init();
	}
	
	

	// logger.debug("оповестить монитор №"+this.listOfMonitorWatchDog.get(counter).getIdMonitor()+"  о неполучении события HeartBeat ");
	// this.notifyAboutEventHeartBeat(this.listOfMonitorWatchDog.get(counter).getIdMonitor());
	
	
	/** первоначальная инициализация данного сторожевого таймера */
	private void init(){
		logger.debug("инициализация");
		this.updateSettings();
	}
	
	/** получить уникальный идентификатор модуля, по которому запущен данных сторожевой таймер */
	public int getModuleId(){
		return this.moduleId; 
	}

	/** оповещение о необходимости обновления настроек (всех мониторов) по данному сторожевому таймеру  */
	public void updateSettings() {
		logger.debug("полностью обновить настройки по модулю: "+this.moduleId);
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			logger.debug("остановить все потоки"); 
			this.stopThread();
			logger.debug("очистить список"); 
			this.listOfMonitorWatchDog.clear();
			ResultSet rs=connector.getConnection().createStatement().executeQuery("select * from monitor_settings_heart_beat where id_module="+this.moduleId+" and is_enabled>0");
			while(rs.next()){
				MonitorWatchDog monitor=new MonitorWatchDog(this.moduleId,
						  rs.getInt("id_monitor"),
						  rs.getInt("time_wait")); // !!! в секундах !!!
				monitor.start();
				listOfMonitorWatchDog.add(monitor);
			}
			logger.debug("упорядочить по возрастанию");
		}catch(Exception ex){
			logger.error("init Exception:"+ex.getMessage());
		}finally{
			connector.close();
		}
	}

	/** оповещение о необходимости обновления настроек по данному сторожевому таймеру по определенному монитору  */
	public void updateSettings(int idMonitor) {
		logger.debug("обновление настроек по модулю "+this.moduleId+" по определенному монитору: "+idMonitor);
		MonitorWatchDog monitor=this.getMonitorWatchDogByIdMonitor(idMonitor);
		if(monitor!=null){
			logger.debug("монитор найден - обновление параметра ");
			ConnectWrap connector=StaticConnector.getConnectWrap();
			try{
				monitor.stopThread();
				try{
					// ссылка может быть убрана сборщиком мусора 
					this.listOfMonitorWatchDog.remove(monitor);
				}catch(Exception ex){};
				
				ResultSet rs=connector.getConnection().createStatement().executeQuery("select * from monitor_settings_heart_beat where id_module="+this.moduleId+" and is_enabled>0 and id_monitor="+idMonitor);
				if(rs.next()){
					MonitorWatchDog monitorNew=new MonitorWatchDog(this.moduleId);
					monitorNew.setIdMonitor(rs.getInt("id_monitor"));
					monitorNew.setTimeWait(rs.getInt("time_wait")); // !!! в секундах !!!
					monitorNew.start();
					logger.debug("монитор создан ");
					listOfMonitorWatchDog.add(monitorNew);
				}else{
					// новый установленный флаг для монитора.модуля - не оповещать о событиях  
				}
			}catch(Exception ex){
				logger.error("init Exception:"+ex.getMessage());
			}finally{
				connector.close();
			}
		}else{
			logger.debug("монитор не найден - возможно только добавлен/создан в базу ");
			ConnectWrap connector=StaticConnector.getConnectWrap();
			try{
				ResultSet rs=connector.getConnection().createStatement().executeQuery("select * from monitor_settings_heart_beat where id_module="+this.moduleId+" and is_enabled>0 and id_monitor="+idMonitor);
				if(rs.next()){
					MonitorWatchDog monitorNew=new MonitorWatchDog(this.moduleId);
					monitorNew.setIdMonitor(rs.getInt("id_monitor"));
					monitorNew.setTimeWait(rs.getInt("time_wait")); // !!! в секундах !!!
					monitorNew.start();
					logger.debug("монитор создан ");
					listOfMonitorWatchDog.add(monitorNew);
				}else{
					logger.info("получено оповещение о необходимости UpdateSettings for Monitor:"+idMonitor+" а его нет в списке на оповещение, возможно обновлен с повторным флагом is_enabled=false ");
				}
			}catch(Exception ex){
				logger.error("init Exception:"+ex.getMessage());
			}finally{
				connector.close();
			}
		}
		
	}
	
	/** получить монитор из списка всех мониторов по уникальному id  */
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
	
	/** оповещение о входящем событии HeartBeat от удаленного модуля */
	public void eventHeartBeatGetFromModule(){
		logger.debug("входящее сообщение HeartBeat по модулю:"+this.moduleId);
		// послать всем потокам оповещение о приходе нового события
		for(int counter=0;counter<this.listOfMonitorWatchDog.size();counter++){
			this.listOfMonitorWatchDog.get(counter).interrupt();
		}
	}


	/** остановить все дочерние потоки */
	public void stopThread() {
		// послать всем потокам сигнал о прекращении работы 
		for(int counter=0;counter<this.listOfMonitorWatchDog.size();counter++){
			this.listOfMonitorWatchDog.get(counter).stopThread();
		}
		try{
			this.listOfMonitorWatchDog.clear();
		}catch(Exception ex){};
	}
}

/** объект-обертка для монитора по данному MonitorHeartBeatWatchDog */
class MonitorWatchDog extends Thread{
	private Logger logger=Logger.getLogger(this.getClass());
	private int moduleId;
	/** уникальный код монитора monitor.id в масштабе базы  */
	private int idMonitor;
	/** время ожидания сигнала в секундах */
	private int timeWait;
	
	/** объект-обертка для монитора по данному MonitorHeartBeatWatchDog
	 * @param moduleId - уникальный идентификатор модуля  
	 * */
	public MonitorWatchDog(int moduleId){
		this.moduleId=moduleId;
	}
	
	/** объект-обертка для монитора по данному MonitorHeartBeatWatchDog
	 * @param moduleId - уникальный идентификатор модуля 
	 * @param idMonitor - уникальный идентификатор монитора (monitor.id) 
	 * @param timeWait - время ожидания для данного монитора 
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
				logger.debug("поток был прерван"); 
			}
		}
	}
	
	
	/** оповестить монитор о недоставке события HeartBeat */
	public void notifyAboutEventHeartBeat(int idMonitor){
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			logger.debug("записать в таблицу monitor_event_heart_beat событие");
			MonitorEventHeartBeat event=new MonitorEventHeartBeat();
			event.setIdModuleHeartBeat(0);// TODO сервер. установить последний из полученных по данному модулю
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
			logger.debug("оповестить пульсацией о наличии события HeartBeat ");
			MonitorWorkerFactory.getInstance().pulseEventHeartBeat();
		}catch(Exception ex){
			logger.error("ошибка во время попытки создания события по монитору ");
		}finally{
			connector.close();
		}
	}
	
	public void stopThread(){
		this.flagRun=false;
		this.interrupt();
	}
	

	/** уникальный код монитора monitor.id в масштабе базы  */
	public int getIdMonitor() {
		return idMonitor;
	}

	/** уникальный код монитора monitor.id в масштабе базы  */
	public void setIdMonitor(int idMonitor) {
		this.idMonitor = idMonitor;
	}

	/** время ожидания сигнала в секундах */
	public int getTimeWait() {
		return timeWait;
	}

	/** время ожидания сигнала в секундах */
	public void setTimeWait(int timeWait) {
		this.timeWait = timeWait;
	}
}
