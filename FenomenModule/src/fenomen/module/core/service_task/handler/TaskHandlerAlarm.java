package fenomen.module.core.service_task.handler;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fenomen.module.core.IAlarmAware;
import fenomen.module.core.IInformationAware;
import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.IModuleSettingsListener;
import fenomen.module.core.ISensorContainerAware;
import fenomen.module.core.service_information.IModuleInformationListener;
import fenomen.module.core.settings.ModuleSettings;
import fenomen.module.web_service.common.ContainerModuleInformation;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;
import fenomen.module.web_service.service.IAlarm;
import fenomen.module.web_service.service.IInformation;

/** обработчик для запросов Alarm */
public class TaskHandlerAlarm extends TaskHandler{
	private Logger logger=Logger.getLogger(this.getClass());
	@Override
	public boolean checkDocumentForProcess(Document document) {
		return isPathExitsts(document, this.getXPathToTask(false)+"/alarm");
	}

	@Override
	public boolean processDocument(ModuleIdentifier moduleIdentifier, int taskId,
								   Document document,
								   IModuleInformationListener informationListener,
								   IModuleSettingsAware moduleSettingsAware,
								   IModuleSettingsListener moduleSettingsListener,
								   ISensorContainerAware sensorContainerAware,
								   IAlarmAware alarmServiceAware,
								   IAlarm alarmService,
								   int alarmTimeError,
								   IInformationAware informationServiceAware,
								   IInformation informationService,
								   int informationTimeError){
		boolean returnValue=false;
		try{
			Element alarm=(Element)this.getNode(document, this.getXPathToTask(false)+"/alarm");
			Object get=this.getNode(alarm, "get");
			Object set=this.getNode(alarm, "set");
			if((get==null)&&(set==null)){
				returnValue=false;
			}else{
				if(get!=null){
					// предоставить новый объект Information для передачи на сервер
					informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getXmlStringForGet(taskId, moduleSettingsAware))));
					returnValue=true;
				}
				if(set!=null){
					returnValue=true;
					while(true){
						// получить time_wait
						Object time_wait=this.getNode((Element)set, "time_wait");
						if(time_wait!=null){
							try{
								moduleSettingsListener.setModuleSettings(ModuleSettings.alarmTimeWait, ((Element)time_wait).getTextContent().trim());
							}catch(Exception ex){
								returnValue=false;
								logger.error("/set/time_wait Exception:"+ex.getMessage());
								break;
							}
						}
						// получить time_error
						Object time_error=this.getNode((Element)set,"time_error");
						if(time_error!=null){
							try{
								moduleSettingsListener.setModuleSettings(ModuleSettings.alarmTimeError, ((Element)time_error).getTextContent().trim());
							}catch(Exception ex){
								returnValue=false;
								logger.error("/set/time_error Exception:"+ex.getMessage());
								break;
							}
						}
						// получить max_count
						Object max_count=this.getNode((Element)set,"max_count");
						if(max_count!=null){
							try{
								moduleSettingsListener.setModuleSettings(ModuleSettings.alarmMaxInformationCount, ((Element)max_count).getTextContent().trim());
							}catch(Exception ex){
								returnValue=false;
								logger.error("/set/max_count Exception:"+ex.getMessage());
								break;
							}
						}
						break;
					}
					if(returnValue==true){
						informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getXmlStringForSet(taskId, true))));
					}else{
						informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getXmlStringForSet(taskId, false))));
					}
					
				}
			}
		}catch(Exception ex){
			informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
			returnValue=false;
		}
		return returnValue;
	}

	/** получить строку запроса в виде XML файла, который нужно наполнить данными на основании запроса GET */
	private String getXmlStringForGet(int taskId, IModuleSettingsAware moduleSettingsAware){
		try{
			ModuleSettings moduleSettings=moduleSettingsAware.getModuleSettings();
			Document document=this.getEmptyXmlDocument();
			
			Element task=this.getTaskElement(document);
			
			Element id=document.createElement("id");
			id.setTextContent(Integer.toString(taskId));
			task.appendChild(id);
			
			Element value=document.createElement("value");
			value.setTextContent("OK");
			task.appendChild(value);
			
			Element alarm=document.createElement("alarm");
			task.appendChild(alarm);
			
			Element time_wait=document.createElement("time_wait");
			time_wait.setTextContent(moduleSettings.getParameter(ModuleSettings.alarmTimeWait));
			alarm.appendChild(time_wait);

			Element time_error=document.createElement("time_error");
			time_error.setTextContent(moduleSettings.getParameter(ModuleSettings.alarmTimeError));
			alarm.appendChild(time_error);
			
			Element max_count=document.createElement("max_count");
			max_count.setTextContent(moduleSettings.getParameter(ModuleSettings.alarmMaxInformationCount));
			alarm.appendChild(max_count);
			
			return this.convertXmlDocumentToString(document);
		}catch(Exception ex){
			logger.error("getXmlStringForGet Exception: "+ex.getMessage());
			return null;
		}
	}
	
	/** получить строку в виде XML файла как ответ OK или ERROR
	 * @param taskId - уникальный идентификатор задачи 
	 * @param isOk - <li><b>true</b> - OK </li><li><b>false</b> - ERROR </li>
	 * @return
	 */
	private String getXmlStringForSet(int taskId, boolean isOk){
		if(isOk){
			Document document=this.getEmptyXmlDocument();
			
			Element task=this.getTaskElement(document);
			
			Element id=document.createElement("id");
			id.setTextContent(Integer.toString(taskId));
			task.appendChild(id);
			
			Element value=document.createElement("value");
			value.setTextContent("OK");
			task.appendChild(value);

			return this.convertXmlDocumentToString(document);
		}else{
			Document document=this.getEmptyXmlDocument();
			
			Element task=this.getTaskElement(document);
			
			Element id=document.createElement("id");
			id.setTextContent(Integer.toString(taskId));
			task.appendChild(id);
			
			Element value=document.createElement("value");
			value.setTextContent("ERROR");
			task.appendChild(value);
			
			return this.convertXmlDocumentToString(document);
		}
	}
}
