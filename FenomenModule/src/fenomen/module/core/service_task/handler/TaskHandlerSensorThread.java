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

/** обработчик для запросов ThreadSensor*/
public class TaskHandlerSensorThread extends TaskHandler{
	private Logger logger=Logger.getLogger(this.getClass());
	@Override
	public boolean checkDocumentForProcess(Document document) {
		return this.isPathExitsts(document, this.getXPathToTask(false)+"/sensor_thread");
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
			Element sensor=(Element)this.getNode(document, this.getXPathToTask(false)+"/sensor_thread");
			Object get=this.getNode(sensor, "get");
			Object set=this.getNode(sensor, "set");
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
								moduleSettingsListener.setModuleSettings(ModuleSettings.sensorTimeWait, ((Element)time_wait).getTextContent().trim());
							}catch(Exception ex){
								returnValue=false;
								logger.error("/set/time_wait Exception:"+ex.getMessage());
								break;
							}
						}
						break;
					}
					if(returnValue==true){
						informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskOk(taskId))));
					}else{
						informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
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
			
			Element sensor=document.createElement("sensor_thread");
			task.appendChild(sensor);
			
				Element time_wait=document.createElement("time_wait");
				time_wait.setTextContent(moduleSettings.getParameter(ModuleSettings.sensorTimeWait));
				sensor.appendChild(time_wait);
			
			return this.convertXmlDocumentToString(document);
		}catch(Exception ex){
			logger.error("getXmlStringForGet Exception: "+ex.getMessage());
			return null;
		}
	}
	
}
