package fenomen.module.core.service_heart_beat;

import org.apache.log4j.Logger;

import fenomen.module.core.IHeartBeatAware;
import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.ITaskExistsNotifier;
import fenomen.module.core.IUpdateSettings;
import fenomen.module.core.settings.ModuleSettings;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.service.IHeartBeat;

/** поток, который "общается" с сервером и передает ему информацию о нахождении модуля на связи */
public class ThreadHeartBeat extends Thread implements IUpdateSettings{
	private Logger logger=Logger.getLogger(this.getClass());
	/** объект, который владеет информацией о получении объекта-сервиса, который может связываться с сервером */
	private IHeartBeatAware heartBeatAware;
	private IHeartBeat heartBeatService;
	private IModuleSettingsAware moduleSettingsAware;
	private ModuleIdentifier moduleIdentifier;
	private ITaskExistsNotifier taskExists;
	
	/** поток, который "общается" с сервером и передает ему информацию о нахождении модуля на связи (<b>Сердцебиение</b>)
	 * <br>
	 * <i>IMPORTANT:</i> объект нуждается в запуске ( <b>.start()</b> ) 
	 * @param heartBeatAware - объект, который может предоставить объект-сервис по связи с сервером
	 * @param moduleSettingsAware - объект, который может предоставить объект-настроку для модуля 
	 * @param moduleIdentifier - объект-идентификатор для модуля
	 * @param taskExists - объект, который содержит интерфейс по оповещению о наличии на сервере очередного задания, которое нужно отработать    
	 */
	public ThreadHeartBeat(IHeartBeatAware heartBeatAware, 
						   IModuleSettingsAware moduleSettingsAware, 
						   ModuleIdentifier moduleIdentifier,
						   ITaskExistsNotifier taskExists){
		this.heartBeatAware=heartBeatAware;
		this.moduleSettingsAware=moduleSettingsAware;
		this.moduleIdentifier=moduleIdentifier;
		this.taskExists=taskExists;
		heartBeatService=this.heartBeatAware.getHeartBeat();
	}
	
	@Override
	public void notifyUpdateSettings(){
		synchronized (flagSettingsChange) {
			this.flagSettingsChange=true;
		}
	}

	/** обновить возможные настройки модуля, которые касаются именно временных интервалов */
	private void updateSettings(){
		synchronized(this.flagSettingsChange){
			this.flagSettingsChange=false;
		}
		ModuleSettings moduleSettings=this.moduleSettingsAware.getModuleSettings();
		this.timeWait=moduleSettings.getParameterAsLong(ModuleSettings.heartBeatTimeWait, 30*1000);
		this.timeError=moduleSettings.getParameterAsLong(ModuleSettings.heartBeatTimeError, 60*1000);
	}
	
	/** время ожидание перед очередной отправкой */
	private long timeWait=0;
	/** время ожидания перед очередной попыткой отправки данных */
	private long timeError=0;
	/** флаг, который говорит о необходимости очередного прочтения данных из Settings */
	private Boolean flagSettingsChange=false;
	
	private void sleepThread(long time){
		try{
			Thread.sleep(time);
		}catch(Exception ex){};
	}
	
	@Override
	public void run(){
		this.updateSettings();
		while(true){
			// посылка сигнала "сердцебиение" на сервер 
			try{
				if(this.flagSettingsChange.booleanValue()==true){
					// прочесть настройки модуля 
					this.updateSettings();
				}
				// передать данные
				String response=this.heartBeatService.hearBeat(this.moduleIdentifier);
				if(response==null){
					logger.warn("IHeartBeat send error ");
					// посылка прошла с ошибкой
					throw new Exception("ThreadHeartBeat#run server send Error package ");
				}else if(response.equals(IHeartBeat.sendOk)){
					logger.debug("send ok");
				}else if(response.equals(IHeartBeat.sendError)){
					// посылка прошла с ошибкой
					throw new Exception("ThreadHeartBeat#run server send Error package ");
				}else if(response.equals(IHeartBeat.taskExists)){
					logger.debug("notify ITaskExists about task on server for this module ");
					this.taskExists.notifyTaskExists();
				}else{
					// неизвестный ответ 
					logger.error("IHeartBeat server answer unknown value:"+response);
				}
				// заснуть для следующей итерации
				this.sleepThread(this.timeWait);
			}catch(Exception ex){
				logger.warn("Exception: "+ex.getMessage());
				// произошла ошибка во время попытки отправки HeartBeat - замереть на некоторое время и повторить попытку
				this.sleepThread(this.timeError);
				if(this.heartBeatService==null){
					this.heartBeatService=this.heartBeatAware.getHeartBeat();
				}
			}
		}
	}
}
