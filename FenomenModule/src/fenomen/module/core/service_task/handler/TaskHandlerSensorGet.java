package fenomen.module.core.service_task.handler;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import fenomen.module.core.IAlarmAware;
import fenomen.module.core.IInformationAware;
import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.IModuleSettingsListener;
import fenomen.module.core.ISensorContainerAware;
import fenomen.module.core.sensor.Sensor;
import fenomen.module.core.sensor.SensorContainer;
import fenomen.module.core.service_information.IModuleInformationListener;
import fenomen.module.web_service.common.ContainerModuleInformation;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;
import fenomen.module.web_service.service.IAlarm;
import fenomen.module.web_service.service.IInformation;

/** получить описание одного сенсора/датчика */
public class TaskHandlerSensorGet extends TaskHandler {
	private Logger logger=Logger.getLogger(this.getClass());
	
	@Override
	public boolean checkDocumentForProcess(Document document) {
		return this.isPathExitsts(document, this.getXPathToTask(false)+"/sensor_get");
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
			Element sensor_get=(Element)this.getNode(document, this.getXPathToTask(false)+"/sensor_get");
			while(true){
				Object id_modbus=this.getNode(sensor_get, "id_modbus");
				if(id_modbus!=null){
					int modbusId=Integer.parseInt(((Element)id_modbus).getTextContent());

					// this.getSensorNode(document, sensorContainerAware.getSensorContainer().getSensorByModbusAddress(modbusId));
					informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getStringXmlByModbusId(taskId,sensorContainerAware,modbusId))));
					returnValue=true;
					break;
				}
				informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
				returnValue=true;
				break;
			}
		}catch(Exception ex){
			informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
			returnValue=false;
			logger.error("processDocument Exception: "+ex.getMessage());
		}
		return returnValue;
	}

	
	/** получить строку текста, которая бы являлась частью XML документа и содержала ответ для сервера 
	 * @param taskId - уникальный идентификатор задачи, полученный от сервера
	 * @param sensorContainerAware - объект, который владеет контейнером сенсоров/модулей
	 * @param modbusAddress - номер сенсора/датчика в сети Modbus
	 * @return
	 */
	private String getStringXmlByModbusId(int taskId,
								 ISensorContainerAware sensorContainerAware,
								 int modbusAddress) {
		String returnValue=null;
		try{
			Document document=this.getEmptyXmlDocument();
			Element task=this.getTaskElement(document);
			
			Element id=document.createElement("id");
			id.setTextContent(Integer.toString(taskId));
			task.appendChild(id);
			
			Element value=document.createElement("value");
			value.setTextContent("OK");
			task.appendChild(value);
			
			SensorContainer sensorContainer=sensorContainerAware.getSensorContainer();
			
			Sensor currentSensor=sensorContainer.getSensorByModbusAddress(modbusAddress);
			if(currentSensor!=null){
				Element sensorNode=this.getSensorNode(document, currentSensor);
				if(sensorNode!=null){
					task.appendChild(sensorNode);
				}else{
					// sensorNode is null
				}
			}else{
				// sensorNode is not found
			}
			returnValue=this.convertXmlDocumentToString(document);
		}catch(Exception ex){
			returnValue=null;
			logger.error("getSensorList Exception:"+ex.getMessage());
		}
		return returnValue;
	}


	
	
}
