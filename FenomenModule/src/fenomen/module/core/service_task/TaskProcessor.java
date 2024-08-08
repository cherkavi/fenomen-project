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

/** ������, ������� ������������ �������� ������� */
public class TaskProcessor {
	/** ������, ������� ������ �� ����� ������ */
	private Logger logger=Logger.getLogger(this.getClass());
	/** ���������� ������������� ������ */
	private ModuleIdentifier moduleIdentifier;
	/** ������������� ���������� ������� */
	private long waitError;
	/** ����� ������������, ������� ����� ��������/������������ ������� �� ������� � ���� Task */
	private ArrayList<TaskHandler> listOfHandler=new ArrayList<TaskHandler>();
	/** ������-���������� �������������� ��������� */
	private IModuleInformationListener moduleInformationListener;
	/** ������-������������ � ������� ���������� ������ */
	private IModuleSettingsAware moduleSettingsAware;
	/** ������, ������� �������� ��������� ��� ����������� ��������� � ���������� ������ */
	private IModuleSettingsListener moduleSettingsListener;
	/** ������, ������� ������������� ������ � ���������� �������� */
	private ISensorContainerAware sensorContainerAware;
	
	/** ������ ��� ��������� ������� IAlarm */
	private IAlarmAware alarmServiceAware;
	/** ������ ������� IAlarm */
	private IAlarm alarmService;
	/** ������ ��� ��������� ������� IInformation */
	private IInformationAware informationServiceAware;
	/** ������ ������� IInformation */
	private IInformation informationService;
	
	/**  ���������� ModuleTask  ( ��������� �������� ������� �� ������� )
	 * @param moduleIdentifier - ���������� ������������� ������
	 * @param waitError - ����� �������� � �� ����� �������� ��������� �������� ������������� �������
	 * @param moduleInformationListener - ������-���������� �������������� ��������� � �������� ������ 
	 * @param moduleSettingsAware - ������-������������ � ������� ���������� ������
	 * @param moduleSettingsListener - ��������� ��� ��������� �������� ������ 
	 * @param sensorContainerAware - ������ ��� ��������� ���������� � ���������/��������� 
	 * @param alarmServiceAware - ������ ��� ��������� ������� IAlarm
	 * @param alarmService - ������ ������� IAlarm
	 * @param informationServiceAware - ������ ��� ��������� ������� IInformation
	 * @param informationService - ������ ������� IInformation
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
		
		// INFO ������. TaskHandler ������������� ���� TaskHandler - ����������� ������������  
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
	
	/** ���������� ������ �� ������� - ������������ ���������� ������� � ������ ����������   
	 * @param taskContainer - �������, ������� ���������� ����������
	 * @param taskAware - ������, ������� ���������� ��������� ������ {@link ITask} �� ��������������� ������ � �������� 
	 * @param taskService - ������ �� ��������������� ������ � ��������
	 * @return <li> true - ������� ���������� </li><li> false - ������ ��������� �������</li>
	 */
	public boolean processTaskAndSendConfirm(ModuleTaskContainer taskContainer, 
										  	 ITaskAware taskAware, 
										  	 ITask taskService){
		boolean returnValue=true;
		if(taskContainer.getContent().length>0){
			logger.debug("���� ������� ��� ���������:");
			logger.debug("������� �� ������ ������������� � ��� ��� ������� ���� ������ ������ ");
			for(int counter=0;counter<taskContainer.getContent().length;counter++){
				int taskId=taskContainer.getContent()[counter].getId();
				this.tookTask(taskAware, taskService, taskId);
			}
			logger.debug("���������� ���������� �������");
			for(int counter=0;counter<taskContainer.getContent().length;counter++){
				// ��������� ������� �� ������� 
				ModuleTask moduleTask=taskContainer.getContent()[counter];
				/** ���������� ����� ������ */
				int uniqueId=moduleTask.getId();
				Document document=this.getXmlFromString(moduleTask.getXmlString());
				if(document!=null){
					try{
						// INFO ������.����� ��������� ���� ���������� ������� �� ������� 
						if(processTask(uniqueId, document)==true){
							// processTask ������ �������� � Information ������������� � ������� � ���� OK ��� ERROR ���������� 
							//this.sendTaskOk(taskAware, taskService, uniqueId);
						}else{
							// �� ����� ������������������ ������������ ������� �� ����������, ��������� ������������� � ���������� �������, ����� �� �������� ��� ����� - ������ ����������� 
							this.sendTaskError(taskAware, taskService, uniqueId);
						}
					}catch(Exception ex){
						// �� ������� ������, ������� ����� ������������ 
						this.sendTaskNotFound(taskAware, taskService, uniqueId);
					}
				}else{
					// ������ ��������� ������� �� ������ � ���� XML ����� 
					logger.warn("processTaskAndSendConfirm Error getXmlString: "+uniqueId);
				}
				 
			}
		}else{
			logger.debug("��� ������� ��� ���������"); 
			returnValue=true;
		}
		return returnValue;
	}
	

	/**
	 * ������� �� ������ ������������� � ���, ��� ����� ������� ������ ��������� ��� ���� �������  
	 * @param taskAware - ������, ������� ���������� {@link ITask}
	 * @param taskService - ������ ������������� 
	 * @param id - ���������� ������������� ������ 
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
	 * ������� �� ������ ������������� �� �������� ���������� ������� 
	 * @param taskAware - ������, ������� ���������� {@link ITask}
	 * @param taskService - ������ ������������� 
	 * @param id - ���������� ������������� ������ 
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
	 * ������� �� ������ ������������� � ���������� ���������� �������
	 * @param taskAware - ������, ������� ���������� {@link ITask}
	 * @param taskService - ������ ������������� 
	 * @param id - ���������� ������������� ������ 
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
	 * ������� �� ������ ������������� � ������������� ����������, � ���� ����������� ����� ������ 
	 * @param taskAware - ������, ������� ���������� {@link ITask}
	 * @param taskService - ������ ������������� 
	 * @param id - ���������� ������������� ������ 
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

	
	/** ���������� ������� (���������� ����� ��� ������������������ {@link TaskHandler})
	 * @param taskId - ���������� ������������� ������ 
	 * @param document - �������, ������� ����� ���������� � ���� Document 
	 * @return
	 * <li> <b>true</b> ������� ���������� </li>
	 * <li> <b>false</b> ������ ��������� </li>
	 * @throws - �� ������ ���������� 
	 */
	private boolean processTask(int taskId, Document document) throws Exception{
		for(int counter=0;counter<this.listOfHandler.size();counter++){
			if(this.listOfHandler.get(counter).checkDocumentForProcess(document)){
				// INFO ������.TaskHandler ��������� ��������� Task   
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
	 * �������� �� String, ���������� ��� XML ����� ������ Document
	 * @param value ������, ���������� XML �����
	 * @return null, ���� ��������� ������ ��������, ���� �� ��� Document
	 */
	private Document getXmlFromString(String value){
		Document returnValue=null;
		javax.xml.parsers.DocumentBuilderFactory document_builder_factory=javax.xml.parsers.DocumentBuilderFactory.newInstance();
        // ���������� ������������� ���������� Parser-��
        document_builder_factory.setValidating(false);
        try {
            // ��������� �����������
            javax.xml.parsers.DocumentBuilder parser=document_builder_factory.newDocumentBuilder();
            // Parse ��������
            returnValue=parser.parse(new ByteArrayInputStream(value.getBytes()));
        }catch(Exception ex){
        	returnValue=null;
        }
		return returnValue;
	}
	
}
