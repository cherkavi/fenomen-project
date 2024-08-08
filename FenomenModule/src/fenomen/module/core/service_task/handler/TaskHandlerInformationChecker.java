package fenomen.module.core.service_task.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fenomen.module.core.IAlarmAware;
import fenomen.module.core.IInformationAware;
import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.IModuleSettingsListener;
import fenomen.module.core.ISensorContainerAware;
import fenomen.module.core.sensor.Sensor;
import fenomen.module.core.service_information.IModuleInformationListener;
import fenomen.module.web_service.common.ContainerModuleInformation;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;
import fenomen.module.web_service.common.TransportChecker;
import fenomen.module.web_service.service.IAlarm;
import fenomen.module.web_service.service.IInformation;
import fenomen.server.controller.server.generator_alarm_checker.calc.Checker;
import fenomen.server.controller.server.generator_alarm_checker.message.InformationMessage;

/** обработчик входящих задач для InformationChecker */
public class TaskHandlerInformationChecker extends TaskHandler {
	private Logger logger=Logger.getLogger(this.getClass());
	
	@Override
	public boolean checkDocumentForProcess(Document document) {
		return this.isPathExitsts(document, this.getXPathToTask(false)+"/information_checker"); 
	}

	@Override
	public boolean processDocument(ModuleIdentifier moduleIdentifier,int taskId, 
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
			Element information_checker=(Element)this.getNode(document,this.getXPathToTask(false)+"/information_checker");
			if(information_checker!=null){
				int id_modbus=Integer.parseInt(((Element)this.getNode(information_checker, "id_modbus")).getTextContent());
				Sensor currentSensor=sensorContainerAware.getSensorContainer().getSensorByModbusAddress(id_modbus);
				if(currentSensor!=null){
					while(true){
						Element get_list=(Element)this.getNode(information_checker, "get_list");
						if(get_list!=null){
							logger.debug("получена ветка XML /information_checker/get_list");
							informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getInformationCheckerList(taskId, currentSensor))));
							returnValue=true;
							break;
						}
						/** номер регистра по которому будут осуществляться операции */
						int register=0;
						try{
							Element registerElement=(Element)this.getNode(information_checker,"register");
							register=Integer.parseInt(registerElement.getTextContent().trim());
						}catch(Exception ex){
							logger.warn("get /information_checker/register ERROR:"+ex.getMessage());
						};
						Element add=(Element)this.getNode(information_checker,"add");
						if(add!=null){
							logger.debug("получена ветка XML /information_checker/add ");
							Element id_file=(Element)this.getNode(add, "id_file");
							if((id_file!=null)&&(id_file.getTextContent()!=null)&&(!id_file.getTextContent().trim().equals(""))){
								this.addInformationChecker(informationServiceAware, 
														   informationService,
														   informationTimeError, 
														   moduleIdentifier, 
														   currentSensor, 
														   register, 
														   id_file.getTextContent().trim());
								logger.error("данные  успешно обработаны /information_checker/add ");
								informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskOk(taskId))));
							}else{
								logger.error("ошибка обработки /information_checker/add ");
								informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
							};
							returnValue=true;
							break;
						}
						
						Element replace=(Element)this.getNode(information_checker,"replace");
						if(replace!=null){
							logger.debug("получена ветка XML /information_checker/replace ");
							Element id_file=(Element)this.getNode(replace, "id_file");
							Element id_checker=(Element)this.getNode(replace,"id_checker");
							if(  (id_file!=null)&&(id_file.getTextContent()!=null)&&(!id_file.getTextContent().trim().equals(""))
								&&(id_checker!=null)&&(id_checker.getTextContent()!=null)&&(!id_checker.getTextContent().trim().equals(""))
								){
								this.replaceinformationChecker(informationServiceAware, informationService,informationTimeError, moduleIdentifier, id_checker, currentSensor, register, id_file.getTextContent().trim());
								logger.debug("данные успешно обработаны /information_checker/replace ");
								informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskOk(taskId))));
							}else{
								logger.error("ошибка в обработке данных /information_checker/replace ");
								informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
							};
							returnValue=true;
							break;
						}

						Element remove=(Element)this.getNode(information_checker,"remove");
						if(remove!=null){
							logger.debug("получена ветка XML /information_checker/remove ");
							Element id_checker=(Element)this.getNode(remove,"id_checker");
							if(  (id_checker!=null)&&(id_checker.getTextContent()!=null)&&(!id_checker.getTextContent().trim().equals(""))
								){
								this.removeinformationChecker(id_checker, currentSensor);
								logger.debug("данные успешно обработаны /information_checker/remove ");
								informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskOk(taskId))));
							}else{
								logger.error("ошибка в обработке данных /information_checker/remove ");
								informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
							};
							returnValue=true;
							break;
						}
						logger.warn("command is not recognized ");
						informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
						break;
					}
				}else{
					logger.warn("id_sensor is not valid ");
					informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
				}
			}else{
				logger.warn("information_checker is not found ");
				informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
			}
		}catch(Exception ex){
			informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
			returnValue=false;
			logger.error("processDocument Exception: "+ex.getMessage());
		}
		return returnValue;
	}

	/** удалить в указанном сенсоре/датчик файл от сервера в виде проверки на наличие события information */
	private void removeinformationChecker(Element idChecker,
										  Sensor sensor) {
		sensor.removeInformationChecker(Integer.parseInt(idChecker.getTextContent()));
	}
	
	/** заменить в указанном сенсоре/датчик файл от сервера в виде проверки на наличие события information */
	private void replaceinformationChecker(IInformationAware informationServiceAware,
		 	   							   IInformation informationService,
		 	   							   long timeError,
		 	   							   ModuleIdentifier moduleIdentifier,
		 	   							   Element idChecker,
		 	   							   Sensor sensor,
		 	   							   int register,
									 	   String idFile
									 	   ) {
		sensor.removeInformationChecker(Integer.parseInt(idChecker.getTextContent()));
		this.addInformationChecker(informationServiceAware, informationService,timeError, moduleIdentifier, sensor, register, idFile);
	}

	/** добавить в указанный сенсор/датчик файл от сервера в виде проверки на наличие события information */
	private void addInformationChecker(IInformationAware informationServiceAware, 
			 						   IInformation informationService,
			 						   long errorTime, 
			 						   ModuleIdentifier moduleIdentifier,
									   Sensor sensor,
									   int register,
								 	   String idFile) {
		Checker<InformationMessage> checker=this.getInformationChecker(informationServiceAware, informationService, errorTime, moduleIdentifier, idFile);
		if(checker!=null){
			logger.debug("checker readed, confirm");
			this.confirmInformationChecker(informationServiceAware, informationService, errorTime, moduleIdentifier, idFile);
			logger.debug("checker add as InformationChecker");
			sensor.addInformationChecker(checker);
		}else{
			logger.error("checker does not readed from server: "+idFile);
		}
	}

    /** получить объект из массива байт */
	private Object getObjectFromByteArray(byte[] data) throws IOException, ClassNotFoundException{
        Object return_value=null;
        ByteArrayInputStream inputStream=new ByteArrayInputStream(data);
        ObjectInputStream ois=new ObjectInputStream(inputStream);
        return_value=ois.readObject();
        ois.close();
        inputStream.close();
        System.out.println("Read OK");
        return return_value;
    }
	
	/** получить от сервера класс InformationChecker */
	@SuppressWarnings("unchecked")
	private Checker<InformationMessage> getInformationChecker(IInformationAware informationServiceAware, 
													 IInformation informationService,
													 long errorTime, 
													 ModuleIdentifier moduleIdentifier, 
													 String idFile){
		Checker<InformationMessage> returnValue=null;
		TransportChecker checker=null;
		while(true){
			try{
				checker=informationService.getInformationCheckerById(moduleIdentifier, idFile);
				break;
			}catch(Exception  ex){
				if(informationService==null){
					informationService=informationServiceAware.getInformation();
				}
				logger.error("getInformationChecker Exception: "+ex.getMessage());
				try{
					Thread.sleep(errorTime);
				}catch(Exception exInner){};
			}
		}
		try{
			returnValue=(Checker<InformationMessage>)this.getObjectFromByteArray(checker.getObjectAsByteArray());
		}catch(Exception ex){
			System.out.println("getInformationChecker: "+ex.getMessage());
		}
		return returnValue;
	}

	/** выслать подтверждение о получении файла  */
	private boolean confirmInformationChecker(IInformationAware informationServiceAware, 
													 IInformation informationService,
													 long errorTime, 
													 ModuleIdentifier moduleIdentifier, 
													 String idFile){
		boolean returnValue=false;
		while(true){
			try{
				returnValue=IInformation.returnOk.equals(informationService.confirmInformationCheckerGet(moduleIdentifier, idFile));
				break;
			}catch(Exception  ex){
				if(informationService==null){
					informationService=informationServiceAware.getInformation();
				}
				logger.error("confirmInformationChecker Exception: "+ex.getMessage());
				try{
					Thread.sleep(errorTime);
				}catch(Exception exInner){};
			}
		}
		return returnValue;
	}
	
	
	/* отправить подтверждение получения задания 
	 * @param informationServiceAware - генератор сервисов 
	 * @param informationService - сервис 
	 * @param errorTime - время ожидания перед очередной попыткой 
	 * @param moduleIdentifier - идентификатор модуля 
	 * @param idFile - уникальный идентификатор файла 
	private void sendAlarmCheckerConfirm(IInformationAware informationServiceAware, 
										 IInformation informationService,
										 long errorTime,
										 ModuleIdentifier moduleIdentifier, 
										 String idFile){
		while(true){
			try{
				informationService.confirmInformationCheckerGet(moduleIdentifier, idFile);
				break;
			}catch(Exception ex){
				if(informationService==null){
					informationService=informationServiceAware.getInformation();
				}
				logger.error("sendInformationCheckerConfirm Exception: "+ex.getMessage());
				try{
					Thread.sleep(errorTime);
				}catch(Exception exInner){};
			}
		}
	}
	 */
	
	
	/** получить XML строку со списком всех присоединенных к данному сенсору/датчику 
	 * @param taskId - уникальный номер задачи 
	 * @param idSensor - номер сенсора/датчика в масштабе контейнера 
	 * @param sensor - датчик/сенсор 
	 * @return строка XML 
	 */
	private String getInformationCheckerList(int taskId, Sensor sensor) {
		try{
			Document document=this.getEmptyXmlDocument();
			Element task=this.getTaskElement(document);
			
			Element id=document.createElement("id");
			id.setTextContent(Integer.toString(taskId));
			task.appendChild(id);
			
			Element value=document.createElement("value");
			value.setTextContent("OK");
			task.appendChild(value);
			
			Element information_checker=document.createElement("information_checker");
			task.appendChild(information_checker);
			
				Element id_modbus=document.createElement("id_modbus");
				id_modbus.setTextContent(Integer.toString(sensor.getNumber()));
				information_checker.appendChild(id_modbus);
				
				Element list=document.createElement("list");
				information_checker.appendChild(list);
				
					for(int index=0;index<sensor.getInformationCheckerSize();index++){
						Checker<?> informationChecker=sensor.getInformationChecker(index);
						if(informationChecker!=null){
							list.appendChild(this.getChecker(informationChecker,document,index, sensor.getNumber()));
						}
					}
			
			return this.convertXmlDocumentToString(document);
		}catch(Exception ex){
			logger.error("getinformationCheckerList Exception:"+ex.getMessage());
			return null;
		}
	}

	
}
