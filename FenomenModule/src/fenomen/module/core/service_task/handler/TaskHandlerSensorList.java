package fenomen.module.core.service_task.handler;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import fenomen.module.core.IAlarmAware;
import fenomen.module.core.IInformationAware;
import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.IModuleSettingsListener;
import fenomen.module.core.ISensorContainerAware;
import fenomen.module.core.sensor.SensorContainer;
import fenomen.module.core.service_information.IModuleInformationListener;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;
import fenomen.module.web_service.common.ContainerModuleInformation;
import fenomen.module.web_service.service.IAlarm;
import fenomen.module.web_service.service.IInformation;

/** получить список сенсоров  */
public class TaskHandlerSensorList extends TaskHandler {
	private Logger logger=Logger.getLogger(this.getClass());
	
	@Override
	public boolean checkDocumentForProcess(Document document) {
		return this.isPathExitsts(document, this.getXPathToTask(false)+"/sensor_list");
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
			Element sensor_list=(Element)this.getNode(document, this.getXPathToTask(false)+"/sensor_list");
			while(true){
				Object get=this.getNode(sensor_list, "get");
				if(get!=null){
					informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getSensorList(taskId,sensorContainerAware))));
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

	
	private String getSensorList(int taskId,
								 ISensorContainerAware sensorContainerAware) {
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
			
			Element sensorList=document.createElement("sensor_list");
			task.appendChild(sensorList);
			
			SensorContainer sensorContainer=sensorContainerAware.getSensorContainer();
			if((sensorContainer!=null)&&(sensorContainer.getSize()>0)){
				for(int counter=0;counter<sensorContainer.getSize();counter++){
					Element sensorNode=this.getSensorNode(document, sensorContainer.getSensor(counter));
					if(sensorNode!=null){
						sensorList.appendChild(sensorNode);
					}else{
						// sensorNode is null
					}
				}
			}
			returnValue=this.convertXmlDocumentToString(document);
		}catch(Exception ex){
			returnValue=null;
			logger.error("getSensorList Exception:"+ex.getMessage());
		}
		return returnValue;
	}

	/*
	private Element getSensorNode(Document document, Sensor sensor, int sensorId) {
		Element sensorNode=null;
		try{
			sensorNode=document.createElement("sensor");
			
			Element id=document.createElement("id");
			id.setTextContent(Integer.toString(sensorId));
			sensorNode.appendChild(id);
			
			Element id_modbus=document.createElement("id_modbus");
			id_modbus.setTextContent(Integer.toString(sensor.getNumber()));
			sensorNode.appendChild(id_modbus);
			
			Element type=document.createElement("type");
			type.setTextContent(sensor.getType());
			sensorNode.appendChild(type);
			
			Element enabled=document.createElement("enabled");
			if(sensor.isEnabled()){
				enabled.setTextContent("true");
			}else{
				enabled.setTextContent("false");
			}
			sensorNode.appendChild(enabled);
			
			Element registerList=document.createElement("register_list");
			sensorNode.appendChild(registerList);
			Device device=sensor.getDevice();
			for(int indexBlock=0;indexBlock<device.getBlockCount();indexBlock++){
				DeviceRegisterBlock currentBlock=device.getBlock(indexBlock);
				for(int indexRegister=0;indexRegister<currentBlock.getRegisterCount();indexRegister++){
					Element register=document.createElement("register");
					registerList.appendChild(register);
					
					Element number=document.createElement("number");
					number.setTextContent(Integer.toString(currentBlock.getAddressBegin()+indexRegister));
					register.appendChild(number);
					
					Element value=document.createElement("value");
					value.setTextContent(Integer.toString(currentBlock.getRegister(indexRegister)));
					register.appendChild(value);
					
					Element date_write=document.createElement("date_write");
					date_write.setTextContent(this.sdf.format(currentBlock.getTimeOfLastOperation()));
					register.appendChild(date_write);
				}
			}
		
		}catch(Exception ex){
			sensorNode=null;
		}
		return sensorNode;
	}
	*/
	
	
}
