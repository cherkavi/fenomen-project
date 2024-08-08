package fenomen.module.core.service_task.handler;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fenomen.module.core.IAlarmAware;
import fenomen.module.core.IInformationAware;
import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.IModuleSettingsListener;
import fenomen.module.core.ISensorContainerAware;
import fenomen.module.core.sensor.Sensor;
import fenomen.module.core.service_information.IModuleInformationListener;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;
import fenomen.module.web_service.common.ContainerModuleInformation;
import fenomen.module.web_service.service.IAlarm;
import fenomen.module.web_service.service.IInformation;

/** установить для сенсора/датчика необходимую настройку */
public class TaskHandlerSensorSet extends TaskHandler {
	private Logger logger=Logger.getLogger(this.getClass());
	
	@Override
	public boolean checkDocumentForProcess(Document document) {
		return this.isPathExitsts(document, this.getXPathToTask(false)+"/sensor_set");
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
			Element sensor_set=(Element)this.getNode(document, this.getXPathToTask(false)+"/sensor_set");
			Element id_modbus=(Element)this.getNode(sensor_set, "id_modbus");
			if(id_modbus!=null){
				Sensor sensor=sensorContainerAware.getSensorContainer().getSensorByModbusAddress(Integer.parseInt(id_modbus.getTextContent().trim()));
				// получение значения Enabled
				Element enabled=(Element)this.getNode(sensor_set, "enabled");
				if((enabled!=null)&&(!enabled.getTextContent().trim().equals(""))){
					sensor.setEnabled(Boolean.parseBoolean(enabled.getTextContent()));
				}
				Object object=this.getNode(sensor_set, "register_list");
				if(object instanceof NodeList){
					NodeList register_list=(NodeList)object;
					for(int counter=0;counter<register_list.getLength();counter++){
						if(register_list.item(counter) instanceof Element){
							try{
								Element register=(Element)register_list.item(counter);
								Element number=(Element)this.getNode(register,"number");
								Element value=(Element)this.getNode(register,"value");
								sensor.getDevice().setRegisterValue(Integer.parseInt(number.getTextContent()),
																	Integer.parseInt(value.getTextContent()));
							}catch(Exception ex){
								logger.error("parse register_list Exception: "+ex.getMessage());
							}
						}
					}
				}
				informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskOk(taskId))));
				returnValue=true;
			}else{
				informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
				returnValue=true;
			}
		}catch(Exception ex){
			informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
			returnValue=false;
			logger.error("processDocument Exception: "+ex.getMessage());
		}
		return returnValue;
	}

	
}
