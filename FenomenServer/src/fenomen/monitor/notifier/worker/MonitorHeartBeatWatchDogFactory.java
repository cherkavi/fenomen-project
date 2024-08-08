package fenomen.monitor.notifier.worker;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import database.ConnectWrap;
import database.StaticConnector;

/** объект, который слушает входящие от удаленных модулей сигналы HeartBeat и генерирует события-оповещения HeartBeat о пропаже модулей */
public class MonitorHeartBeatWatchDogFactory{
	private static MonitorHeartBeatWatchDogFactory instance;
	static {
		instance=new MonitorHeartBeatWatchDogFactory();
	}
	/** объект, который слушает входящие от удаленных модулей сигналы HeartBeat и генерирует события-оповещения HeartBeat о пропаже модулей */
	public static MonitorHeartBeatWatchDogFactory getInstance(){
		return instance;
	}
	
	private Logger logger=Logger.getLogger(this.getClass()); 
	
	/** список всех зарегестрированных MonitorHeartBeatWatchDog слушателей по модулю  */
	private ArrayList<MonitorHeartBeatWatchDog> list=new ArrayList<MonitorHeartBeatWatchDog>();
	
	
	/** объект, который слушает входящие от удаленных модулей сигналы HeartBeat и генерирует события-оповещения HeartBeat о пропаже модулей */
	private MonitorHeartBeatWatchDogFactory(){
		// получить все модули из системы и добавить их уникальные Id в объект  
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
	
	/** обновить настройки по модулю  
	 * @param idModule - код модуля из таблицы module.id
	 * */
	public void updateSettingsByModule(int idModule){
		MonitorHeartBeatWatchDog elementInList=this.getFromList(idModule);
		if(elementInList==null){
			logger.debug("сторожевой таймер не найден ");
		}else{
			logger.debug("оповещение о необходимости обновления данных для сторожевого таймера:"+idModule);
			elementInList.updateSettings();
		}
	}

	/** обновить настройки по модулю.монитору  
	 * @param idModule - код модуля из таблицы module.id
	 * @param idMonitor - код монитора
	 * */
	public void updateSettingsByModule(int idModule, int idMonitor) {
		MonitorHeartBeatWatchDog elementInList=this.getFromList(idModule);
		if(elementInList==null){
			logger.debug("сторожевой таймер не найден ");
		}else{
			logger.debug("оповещение о необходимости обновления данных для сторожевого таймера по монитору :"+idModule);
			elementInList.updateSettings(idMonitor);
		}
	}
	
	/** оповещение о приходе от удаленного модуля сигнала HeartBeat  
	 * @param idModule - код модуля из таблицы module.id
	 * */
	public void moduleSendHeartBeat(int idModule){
		MonitorHeartBeatWatchDog elementInList=this.getFromList(idModule);
		if(elementInList==null){
			logger.debug("сторожевой таймер не найден: "+idModule);
		}else{
			logger.debug("оповещение сторожевого таймера о модуле: "+idModule);
			elementInList.eventHeartBeatGetFromModule();
		}
	}

	/** оповещение об удалении модуля */
	public void removeModule(int idModule){
		// TODO сервер.удаление модуля через Controller2
		MonitorHeartBeatWatchDog elementInList=this.getFromList(idModule);
		if(elementInList==null){
			logger.debug("сторожевой таймер не найден: "+idModule);
		}else{
			elementInList.stopThread();
			this.list.remove(elementInList);
			logger.debug("сторожевой таймер найден, удален: "+idModule);
		}
	}
	
	/** оповещение о добавлении модуля в систему  */
	public void addModule(int idModule){
		MonitorHeartBeatWatchDog elementInList=this.getFromList(idModule);
		if(elementInList==null){
			logger.debug("добавить сторожевой таймер:"+idModule);
			this.list.add(new MonitorHeartBeatWatchDog(idModule));
		}else{
			logger.debug("сторожевой таймер уже добавлен "+idModule);
		}
	}
	
	/** получить монитор по индексу  */
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
