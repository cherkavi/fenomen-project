package fenomen.module.core.service_task;

import org.apache.log4j.Logger;

import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.ITaskAware;
import fenomen.module.core.ITaskExistsNotifier;
import fenomen.module.core.IUpdateSettings;
import fenomen.module.core.settings.ModuleSettings;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleTaskContainer;
import fenomen.module.web_service.service.ITask;


/** поток, который получает задания от сервера, выполняет эти задания на модуле, и возвращает результат на сервер */
public class ThreadTask extends Thread implements ITaskExistsNotifier, IUpdateSettings{
	private Logger logger=Logger.getLogger(this.getClass());
	/** флаг, который говорит о необходимости получения с сервера очередного задания */
	private Integer taskExists=0;
	/** флаг, который сигнализирует о необходимости UPDATE */
	private Boolean needUpdateSettings=false;
	/** объект, который является общим ресурсом для разных потоков для захвата */
	private Object controlObject=new Object();
	/** объект, генерирующий объекты-сервисы по связи с сервером */
	private ITaskAware taskAware;
	/** объект, который предоставляет сервисы по получению TaskModule от сервера */
	private ITask serviceTask;
	/** объект, генерирующий объекты-настройки модуля */
	private IModuleSettingsAware moduleSettingsAware;
	/** уникальный идентификатор модуля */
	private ModuleIdentifier moduleIdentifier;
	/** время ожидания перед следующим запросом на сервер за Task  */
	private long timeWait=0;
	/** время ожидания перед следующей попыткой запроса на сервер  */
	private long timeError=0;
	/** обработчик заданий от сервера */
	private TaskProcessor taskProcessor;
	
	/** поток, который получает задания от сервера, выполняет эти задания на модуле, и возвращает результат на сервер
	 * @param taskAware объект, который может генерировать сервис по получению Task с сервера 
	 * @param moduleSettingsAware объект, который содержит текущий настройки модуля  
	 * @param moduleIdentifier уникальный идентификатор модуля 
	 * @param taskProcessor объект-обработчик для входящих заданий
	 */
	public ThreadTask(ITaskAware taskAware, 
					  IModuleSettingsAware moduleSettingsAware, 
					  ModuleIdentifier moduleIdentifier,
					  TaskProcessor taskProcessor){
		this.taskAware=taskAware;
		this.moduleSettingsAware=moduleSettingsAware;
		this.moduleIdentifier=moduleIdentifier;
		this.taskProcessor=taskProcessor;
	}
	
	@Override 
	public void run(){
		this.updateSettings();
		while(true){
			if((taskExists.intValue()>0)||(needUpdateSettings.booleanValue()==true)){
				if(needUpdateSettings.booleanValue()==true){
					this.updateSettings();
				}
				// существуют ли задания, которые нужно отработать ?
				if(taskExists.intValue()>0){
					synchronized(taskExists){
						this.taskExists=0;
					}
					// Task существует - забрать и отработать
					ModuleTaskContainer currentTask=this.getTaskFromServer();
					if(isTaskNeedProcessing(currentTask)){
						logger.debug("process Task, send confirm");
						this.taskProcessor.processTaskAndSendConfirm(currentTask,this.taskAware, this.serviceTask);
						// currentTask
						try{Thread.sleep(this.timeWait);}catch(Exception ex){};
						synchronized(taskExists){
							this.taskExists=1;
						}
						continue;
					}else{
						// получено пустое задание, проверить на возможное изменение флага
						synchronized(this.taskExists){
							if(this.taskExists.intValue()>0){
								continue;
							}else{
								try{
									this.controlObject.wait();
								}catch(Exception ex){};
							}
						}
					}
				}
			}else{
				synchronized(this.controlObject){
					if((taskExists.intValue()>0)||(needUpdateSettings.booleanValue()==true)){
						continue;
					}else{
						try{
							this.controlObject.wait();
						}catch(Exception ex){}
					}
				}
			}
		}
	}
	
	/** обновить возможные настройки модуля, которые касаются именно временных интервалов */
	private void updateSettings(){
		synchronized(this.needUpdateSettings){
			this.needUpdateSettings=false;
		}
		ModuleSettings moduleSettings=this.moduleSettingsAware.getModuleSettings();
		this.timeWait=moduleSettings.getParameterAsLong(ModuleSettings.taskTimeWait);
		this.timeError=moduleSettings.getParameterAsLong(ModuleSettings.taskTimeError);
	}
	
	
	/** проверить, нужно ли данный Task отрабатывать или же он не несет в себе заданий */
	private boolean isTaskNeedProcessing(ModuleTaskContainer moduleTaskContainer){
		// проверить на наличие заданий в объекте ModuleTask
		return (moduleTaskContainer.getContent().length>0);
	}
	
	/** получить ModuleTask от сервера */
	private ModuleTaskContainer getTaskFromServer(){
		ModuleTaskContainer moduleTask=null;
		while(true){
			try{
				// попытка получения очередного объекта
				moduleTask=this.serviceTask.getTask(this.moduleIdentifier);
				if(moduleTask==null){
					logger.warn("getTaskFromServer ошибка связи с сервером ");
					try{
						Thread.sleep(this.timeError);
					}catch(Exception exInner){};
					continue;
				}else{
					logger.debug("getTaskFromServer ModuleTask was get ");
					break;
				}
			}catch(Exception ex){
				logger.warn("getTaskFromServer (maybe serviceTask is null) Exception: "+ex.getMessage());
				if(this.serviceTask==null){
					this.serviceTask=this.taskAware.getTask();
				}
				try{
					Thread.sleep(this.timeError);
				}catch(Exception exInner){};
				continue;
			}
		}
		return moduleTask;
	}
	
	@Override
	public void notifyTaskExists() {
		logger.debug("notifyTaskExists");
		// на сервере есть новое задание, которое нужно забрать - оповестить объект о необходимости включения рабочего цикла 
		synchronized(this.taskExists){
			this.taskExists=this.taskExists.intValue()+1;
		}
		synchronized(this.controlObject){
			this.controlObject.notify();
		}
	}

	@Override
	public void notifyUpdateSettings() {
		logger.debug("notifyUpdateSettings");
		synchronized(this.needUpdateSettings){
			this.needUpdateSettings=true;
		}
		synchronized(this.controlObject){
			this.controlObject.notify();			
		}
	}
	
}
