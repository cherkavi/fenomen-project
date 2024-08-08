package fenomen.module.core.service_alarm;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import fenomen.module.core.IAlarmAware;
import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.IUpdateSettings;
import fenomen.module.core.settings.ModuleSettings;
import fenomen.module.web_service.common.ContainerModuleAlarm;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.service.IAlarm;
import fenomen.module.web_service.service.ITask;

/** поток, который посылает на сервер тревожные сообщения, полученные от датчиков */
public class ThreadAlarm extends Thread implements IUpdateSettings, IModuleAlarmListener{
	private Logger logger=Logger.getLogger(this.getClass());
	/** уникальный идентификатор данного модуля */
	private ModuleIdentifier moduleIdentifier;
	/** объект, который генерирует сервис по передаче информации на сервер */
	private IAlarmAware alarmAware;
	private IAlarm serviceAlarm;
	/** объект, который генерирует сервис по получению информационного объекта */
	private IModuleSettingsAware moduleSettingsAware;
	/** объект, который содержит необходимые информационные объекты для отправки на севрер */
	private ArrayList<ContainerModuleAlarm> listOfAlarm=new ArrayList<ContainerModuleAlarm>();
	
	/** флаг,который сигнализирует о необходимости обновления настроек для модуля */
	private Boolean needUpdateSettings=false;
	/** объект, который будет сигнализировать о появлении событий для обработки */
	private Object signal=new Object();
	
	/** время задержки перед следующей посылкой*/
	private long timeWait;
	/** время задержки перед очередной попыткой отправки */
	private long timeError;
	/** максимальное кол-во элементов Alarm в очереди на отправку */
	private int maxAlarmCount;

	/** поток, который посылает на сервер тревожные сообщения, полученные от датчиков 
	 * @param alarmAware объект, который генерирует сервисы по связи с сервером
	 * @param moduleSettingsAware объект, который "ведает" о настройках модуля 
	 * @param moduleIdentifier объект-идентификатор для модуля
	 * IMPORTANT: требуется вызов метода start()   
	 */
	public ThreadAlarm(IAlarmAware alarmAware,
					   IModuleSettingsAware moduleSettingsAware,
					   ModuleIdentifier moduleIdentifier){
		this.alarmAware=alarmAware;
		this.alarmAware.getAlarm();
		this.moduleSettingsAware=moduleSettingsAware;
		this.moduleIdentifier=moduleIdentifier;
		
	}

	/**
	 * послать на сервер уведомление о старте/рестарте данного модуля   
	 * @param taskAware - объект, который генерирует {@link ITask}
	 * @param taskService - сервис подтверждений 
	 * @param id - уникальный идентификатор задачи 
	 */
	private void moduleWasRestarted(IAlarmAware alarmAware, IAlarm alarmService, ModuleIdentifier moduleIdentifier){
		while(true){
			try{
				alarmService.moduleWasRestarted(moduleIdentifier);
				break;
			}catch(Exception ex){
				if(alarmService==null){
					alarmService=alarmAware.getAlarm();
				}
				try{
					Thread.sleep(5000);
				}catch(Exception exInner){};
			}
		}
	}
	
	
	@Override
	public void run(){
		// оповестить сервер о Restart модуля 
		this.moduleWasRestarted(this.alarmAware, this.serviceAlarm, moduleIdentifier);
		
		this.updateSettings();
		ContainerModuleAlarm currentForSend=null;
		while(true){
			// проверка на изменение в настройках модуля
			if((this.needUpdateSettings.booleanValue()==true)||(this.listOfAlarm.size()>0)){
				if(this.needUpdateSettings.booleanValue()==true){
					this.updateSettings();
				}
				// отправка объекта 
				if(this.listOfAlarm.size()>0){
					// получение очередного объекта
					synchronized(this.listOfAlarm){
						currentForSend=this.listOfAlarm.remove(0);
					}
					logger.debug("run: sendAlarm");
					this.sendAlarm(currentForSend);
					// wait for next send
					try{Thread.sleep(this.timeWait);}catch(Exception ex){};
				}
			}else{
				logger.debug("no alarm data for send - wait for work");
				synchronized(this.signal){
					if((this.needUpdateSettings.booleanValue()==true)||(this.listOfAlarm.size()>0)){
						logger.debug("for work");
						continue;
					}else{
						try{
							this.signal.wait();
						}catch(InterruptedException ex){};
					}
				}
			}
		}
	}

	@Override
	public void notifyUpdateSettings() {
		// оповещение потока о необходимости инициализации рабочего цикла
		synchronized(this.needUpdateSettings){
			this.needUpdateSettings=true;
		}
		synchronized(this.signal){
			this.signal.notify();
		}
	}
	
	
	/** обновить возможные настройки модуля, которые касаются именно временных интервалов */
	private void updateSettings(){
		synchronized(this.needUpdateSettings){
			this.needUpdateSettings=false;
		}
		ModuleSettings moduleSettings=this.moduleSettingsAware.getModuleSettings();
		this.timeWait=moduleSettings.getParameterAsLong(ModuleSettings.alarmTimeWait, 5*1000);
		this.timeError=moduleSettings.getParameterAsLong(ModuleSettings.alarmTimeError, 60*1000);
		this.maxAlarmCount=(int)moduleSettings.getParameterAsLong(ModuleSettings.alarmMaxInformationCount,100);
		if(this.maxAlarmCount<0){this.maxAlarmCount=0;};
	}
	
	/** отправить информационное сообщение на сервер
	 * @param moduleInformation - объект, который должен быть отправлен на сервер 
	 */
	private void sendAlarm(ContainerModuleAlarm moduleAlarm){
		while(true){
			try{
				// проверка на разрушение/очистку сервисного объекта 
				if(this.serviceAlarm==null){
					this.serviceAlarm=this.alarmAware.getAlarm();
				}
				// передача данных на сервер
				String returnValue=this.serviceAlarm.sendAlarm(this.moduleIdentifier, moduleAlarm);
				if((returnValue==null)||(returnValue.equals(IAlarm.returnError))){
					try{Thread.sleep(this.timeError);}catch(Exception ex){};
					logger.debug("sendAlarm Error - repeat after Send ");
					continue;
				}else if(returnValue.equals(IAlarm.returnOk)){
					logger.debug("sendAlarm: Data was sended ");
					break;
				}else {
					logger.error("sendAlarm: Unknown server response:"+returnValue);
					break;
				}
			}catch(Exception ex){
				logger.warn("sendInformation Exception:"+ex.getMessage());
			}
		}
	}
	

	@Override
	public void notifyAlarm(ContainerModuleAlarm moduleAlarm) {
		synchronized(this.listOfAlarm){
			this.listOfAlarm.add(moduleAlarm);
			while(this.maxAlarmCount<this.listOfAlarm.size()){
				this.listOfAlarm.remove(0);
			}
		}
		// оповещение потока о необходимости инициализации рабочего цикла 
		synchronized(this.signal){
			this.signal.notify();
		}
	}
	
	/** передать на обработку существующую очередь из информационных сообщений 
	 * @param objectForProcess - объект, который будет обрабатывать очередь событий (в синхронизированном режиме )
	 */
	public void processAlarm(IProcessAlarm objectForProcess){
		synchronized(this.listOfAlarm){
			objectForProcess.processAlarm(this.listOfAlarm);
		}
		// оповещение потока о необходимости инициализации рабочего цикла 
		synchronized(this.signal){
			this.signal.notify();
		}
	}
	
}
