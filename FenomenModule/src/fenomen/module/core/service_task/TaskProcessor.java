package fenomen.module.core.service_task;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import fenomen.module.core.IAlarmAware;
import fenomen.module.core.IInformationAware;
import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.IModuleSettingsListener;
import fenomen.module.core.ISensorContainerAware;
import fenomen.module.core.ITaskAware;
import fenomen.module.core.service_information.IModuleInformationListener;
import fenomen.module.core.service_task.handler.TaskHandler;
import fenomen.module.core.service_task.handler.TaskHandlerAlarm;
import fenomen.module.core.service_task.handler.TaskHandlerAlarmChecker;
import fenomen.module.core.service_task.handler.TaskHandlerHeartBeat;
import fenomen.module.core.service_task.handler.TaskHandlerInformation;
import fenomen.module.core.service_task.handler.TaskHandlerInformationChecker;
import fenomen.module.core.service_task.handler.TaskHandlerSensorThread;
import fenomen.module.core.service_task.handler.TaskHandlerSensorGet;
import fenomen.module.core.service_task.handler.TaskHandlerSensorList;
import fenomen.module.core.service_task.handler.TaskHandlerSensorSet;
import fenomen.module.core.settings.ModuleSettings;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleTask;
import fenomen.module.web_service.common.ModuleTaskContainer;
import fenomen.module.web_service.service.IAlarm;
import fenomen.module.web_service.service.IInformation;
import fenomen.module.web_service.service.ITask;

/** объект, который обрабатывает входящие задания */
public class TaskProcessor {
	/** объект, который следит за логом данных */
	private Logger logger=Logger.getLogger(this.getClass());
	/** уникальный идентификатор модуля */
	private ModuleIdentifier moduleIdentifier;
	/** подтверждение очередного задания */
	private long waitError;
	/** набор обработчиков, которые могут получать/обрабатывать запросы от сервера в виде Task */
	private ArrayList<TaskHandler> listOfHandler=new ArrayList<TaskHandler>();
	/** объект-получатель информационных сообщений */
	private IModuleInformationListener moduleInformationListener;
	/** объект-осведомитель о текущих настройках модуля */
	private IModuleSettingsAware moduleSettingsAware;
	/** объект, который содержит интерфейс для глобального изменения в настройках модуля */
	private IModuleSettingsListener moduleSettingsListener;
	/** объект, который предоставляет доступ к контейнеру сенсоров */
	private ISensorContainerAware sensorContainerAware;
	
	/** объект для получения сервиса IAlarm */
	private IAlarmAware alarmServiceAware;
	/** объект сервиса IAlarm */
	private IAlarm alarmService;
	/** объект для получения сервиса IInformation */
	private IInformationAware informationServiceAware;
	/** объект сервиса IInformation */
	private IInformation informationService;
	
	/**  обработчик ModuleTask  ( обработка входящих заданий от сервера )
	 * @param moduleIdentifier - уникальный идентификатор модуля
	 * @param waitError - время ожидания в мс перед попыткой следующей отправки подтверждения задания
	 * @param moduleInformationListener - объект-получатель информационных сообщений о передаче данных 
	 * @param moduleSettingsAware - объект-осведомитель о текущих настройках модуля
	 * @param moduleSettingsListener - слушатель для изменений настроек модуля 
	 * @param sensorContainerAware - объект для получения контейнера с датчиками/сенсорами 
	 * @param alarmServiceAware - объект для получения сервиса IAlarm
	 * @param alarmService - объект сервиса IAlarm
	 * @param informationServiceAware - объект для получения сервиса IInformation
	 * @param informationService - объект сервиса IInformation
	 */
	public TaskProcessor(ModuleIdentifier moduleIdentifier, 
						 long waitError, 
						 IModuleInformationListener moduleInformationListener,
						 IModuleSettingsAware moduleSettingsAware,
						 IModuleSettingsListener moduleSettingsListener,
						 ISensorContainerAware sensorContainerAware,
						 IAlarmAware alarmServiceAware,
						 IAlarm alarmService,
						 IInformationAware informationServiceAware,
						 IInformation informationService) {
		this.moduleIdentifier=moduleIdentifier;
		this.waitError=waitError;
		this.moduleInformationListener=moduleInformationListener;
		this.moduleSettingsAware=moduleSettingsAware;
		this.moduleSettingsListener=moduleSettingsListener;
		this.sensorContainerAware=sensorContainerAware;
		this.alarmServiceAware=alarmServiceAware;
		this.alarmService=alarmService;
		this.informationServiceAware=informationServiceAware;
		this.informationService=informationService;
		
		// INFO модуль. TaskHandler присоединение всех TaskHandler - регистрация обработчиков  
		listOfHandler.add(new TaskHandlerHeartBeat());
		listOfHandler.add(new TaskHandlerInformation());
		listOfHandler.add(new TaskHandlerAlarm());
		listOfHandler.add(new TaskHandlerSensorList());
		listOfHandler.add(new TaskHandlerSensorGet());
		listOfHandler.add(new TaskHandlerSensorSet());
		listOfHandler.add(new TaskHandlerAlarmChecker());
		listOfHandler.add(new TaskHandlerInformationChecker());
		listOfHandler.add(new TaskHandlerSensorThread());
	}
	
	/** обработчик команд от сервера - обязательное оповещение сервера о стадии выполнения   
	 * @param taskContainer - задание, которые необходимо обработать
	 * @param taskAware - объект, который генерирует сервисные службы {@link ITask} по информационному обмену с сервером 
	 * @param taskService - объект по информационному обмену с сервером
	 * @return <li> true - задание обработано </li><li> false - ошибка обработки задания</li>
	 */
	public boolean processTaskAndSendConfirm(ModuleTaskContainer taskContainer, 
										  	 ITaskAware taskAware, 
										  	 ITask taskService){
		boolean returnValue=true;
		if(taskContainer.getContent().length>0){
			logger.debug("есть задания для отработки:");
			logger.debug("послать на сервер подтверждение о том что задания были выданы модулю ");
			for(int counter=0;counter<taskContainer.getContent().length;counter++){
				int taskId=taskContainer.getContent()[counter].getId();
				this.tookTask(taskAware, taskService, taskId);
			}
			logger.debug("отработать полученные задания");
			for(int counter=0;counter<taskContainer.getContent().length;counter++){
				// очередное задание от сервера 
				ModuleTask moduleTask=taskContainer.getContent()[counter];
				/** уникальный номер задачи */
				int uniqueId=moduleTask.getId();
				Document document=this.getXmlFromString(moduleTask.getXmlString());
				if(document!=null){
					try{
						// INFO модуль.место обработки всех полученных заданий от сервера 
						if(processTask(uniqueId, document)==true){
							// processTask обязан положить в Information подтверждение о задании в виде OK или ERROR выполнения 
							//this.sendTaskOk(taskAware, taskService, uniqueId);
						}else{
							// ни одним зарегестрированным обработчиком задание не обработано, отправить подтверждение о выполнении задания, чтобы не получить его вновь - ошибка выполенения 
							this.sendTaskError(taskAware, taskService, uniqueId);
						}
					}catch(Exception ex){
						// не найдена задача, которую нужно обрабатывать 
						this.sendTaskNotFound(taskAware, taskService, uniqueId);
					}
				}else{
					// ошибка получения задания из строки в виде XML файла 
					logger.warn("processTaskAndSendConfirm Error getXmlString: "+uniqueId);
				}
				 
			}
		}else{
			logger.debug("нет заданий для отработки"); 
			returnValue=true;
		}
		return returnValue;
	}
	

	/**
	 * послать на сервер подтверждение о том, что поток успешно забрал высланные для него задания  
	 * @param taskAware - объект, который генерирует {@link ITask}
	 * @param taskService - сервис подтверждений 
	 * @param id - уникальный идентификатор задачи 
	 */
	private void tookTask(ITaskAware taskAware, ITask taskService, int id){
		while(true){
			try{
				taskService.tookTask(moduleIdentifier, id);
				break;
			}catch(Exception ex){
				if(taskService==null){
					taskService=taskAware.getTask();
				}
				try{
					Thread.sleep(this.waitError);
				}catch(Exception exInner){};
			}
		}
	}

	
	/**
	 * послать на сервер подтверждение об успешном выполнении задания 
	 * @param taskAware - объект, который генерирует {@link ITask}
	 * @param taskService - сервис подтверждений 
	 * @param id - уникальный идентификатор задачи 
	 */
	@SuppressWarnings("unused")
	private void sendTaskOk(ITaskAware taskAware, ITask taskService, int id){
		while(true){
			try{
				taskService.taskProcessOk(moduleIdentifier, id);
				break;
			}catch(Exception ex){
				if(taskService==null){
					taskService=taskAware.getTask();
				}
				try{
					Thread.sleep(this.waitError);
				}catch(Exception exInner){};
			}
		}
	}
	
	/**
	 * послать на сервер подтверждение о неуспешном выполнении задания
	 * @param taskAware - объект, который генерирует {@link ITask}
	 * @param taskService - сервис подтверждений 
	 * @param id - уникальный идентификатор задачи 
	 */
	private void sendTaskError(ITaskAware taskAware, ITask taskService, int id){
		while(true){
			try{
				taskService.taskProcessError(moduleIdentifier, id);
				break;
			}catch(Exception ex){
				if(taskService==null){
					taskService=taskAware.getTask();
				}
				try{
					Thread.sleep(this.waitError);
				}catch(Exception exInner){};
			}
		}
	}
	
	/**
	 * послать на сервер подтверждение о невозможности выполнения, в силу непонимания самой задачи 
	 * @param taskAware - объект, который генерирует {@link ITask}
	 * @param taskService - сервис подтверждений 
	 * @param id - уникальный идентификатор задачи 
	 */
	private void sendTaskNotFound(ITaskAware taskAware, ITask taskService, int id){
		while(true){
			try{
				taskService.taskProcessNotFound(moduleIdentifier, id);
				break;
			}catch(Exception ex){
				if(taskService==null){
					taskService=taskAware.getTask();
				}
				try{
					Thread.sleep(this.waitError);
				}catch(Exception exInner){};
			}
		}
	}

	
	/** обработать задание (пропустить через все зарегестрированные {@link TaskHandler})
	 * @param taskId - уникальный идентификатор задачи 
	 * @param document - задание, которое нужно отработать в виде Document 
	 * @return
	 * <li> <b>true</b> успешно отработано </li>
	 * <li> <b>false</b> ошибка отработки </li>
	 * @throws - не найден обработчик 
	 */
	private boolean processTask(int taskId, Document document) throws Exception{
		for(int counter=0;counter<this.listOfHandler.size();counter++){
			if(this.listOfHandler.get(counter).checkDocumentForProcess(document)){
				// INFO модуль.TaskHandler обработка входящего Task   
				return this.listOfHandler.get(counter).processDocument(this.moduleIdentifier,
																	   taskId, 
																	   document,
																	   this.moduleInformationListener,
																	   this.moduleSettingsAware,
																	   this.moduleSettingsListener,
																	   this.sensorContainerAware,
																	   this.alarmServiceAware,
																	   this.alarmService,
																	   Integer.parseInt(this.moduleSettingsAware.getModuleSettings().getParameter(ModuleSettings.alarmTimeError)),
																	   this.informationServiceAware,
																	   this.informationService,
																	   Integer.parseInt(this.moduleSettingsAware.getModuleSettings().getParameter(ModuleSettings.informationTimeError)));
				
			}
		}
		throw new Exception("TaskHandler is not found:");
	}
	
	/** 
	 * получить из String, содержащий вид XML файла объект Document
	 * @param value строка, содержащая XML текст
	 * @return null, если произошла ошибка парсинга, либо же сам Document
	 */
	private Document getXmlFromString(String value){
		Document returnValue=null;
		javax.xml.parsers.DocumentBuilderFactory document_builder_factory=javax.xml.parsers.DocumentBuilderFactory.newInstance();
        // установить непроверяемое порождение Parser-ов
        document_builder_factory.setValidating(false);
        try {
            // получение анализатора
            javax.xml.parsers.DocumentBuilder parser=document_builder_factory.newDocumentBuilder();
            // Parse источник
            returnValue=parser.parse(new ByteArrayInputStream(value.getBytes()));
        }catch(Exception ex){
        	returnValue=null;
        }
		return returnValue;
	}
	
}
