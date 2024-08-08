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
import fenomen.module.core.sensor.SensorContainer;
import fenomen.module.core.service_information.IModuleInformationListener;
import fenomen.module.web_service.common.ContainerModuleInformation;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;
import fenomen.module.web_service.common.TransportChecker;
import fenomen.module.web_service.service.IAlarm;
import fenomen.module.web_service.service.IInformation;
import fenomen.server.controller.server.generator_alarm_checker.calc.Checker;
import fenomen.server.controller.server.generator_alarm_checker.message.AlarmMessage;

/** обработчик входящих задач для AlarmChecker */
public class TaskHandlerAlarmChecker extends TaskHandler {
	private Logger logger=Logger.getLogger(this.getClass());
	
	@Override
	public boolean checkDocumentForProcess(Document document) {
		return this.isPathExitsts(document, this.getXPathToTask(false)+"/alarm_checker");
	}

	/** проверить в контейнере модулей/сенсоров наличие в нем ModBus адреса
	 * @param container - контейнер модулей/сенсоров
	 * @param idModbus -адрес, который нужно проверить на наличие в контейнере 
	 * @return 
	 * <li> <b>true</b> - адрес найден  </li>
	 * <li> <b>false</b> - нет данного адреса в сети Modbus </li>
	 */
	private boolean consistsIdModbusIntoContainer(SensorContainer container, int idModbus){
		return container.getSensorByModbusAddress(idModbus)!=null;
	}
	
	@Override
	public boolean processDocument(ModuleIdentifier moduleIdentifier, 
								   int taskId, 
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
								   int informationTimeError) {
		boolean returnValue=false;
		try{
			Element alarm_checker=(Element)this.getNode(document,this.getXPathToTask(false)+"/alarm_checker");
			if(alarm_checker!=null){
				int addressModbus=Integer.parseInt(((Element)this.getNode(alarm_checker, "id_modbus")).getTextContent());
				SensorContainer sensorContainer=sensorContainerAware.getSensorContainer();
				if(consistsIdModbusIntoContainer(sensorContainer,addressModbus)==true){
					logger.debug("адресат найден - "+addressModbus);
					while(true){
						Element get_list=(Element)this.getNode(alarm_checker, "get_list");
						if(get_list!=null){
							logger.debug("добавить в ThreadInformation задание на отправку на сервер списка всех AlarmChecker-ов по данному датчику");
							informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getAlarmCheckerList(taskId, 
									addressModbus, 
																																				sensorContainer.getSensorByModbusAddress(addressModbus)
																																				)
																													   )
																								  )
																  );
							returnValue=true;
							break;
						}
						logger.debug("попытка получения alarm_checker/register из XML ");
						
						int register=0;
						try{
							Element registerXmlElement=(Element)this.getNode(alarm_checker, "register");
							if((registerXmlElement==null)||(registerXmlElement.getTextContent().trim().equals(""))){
								logger.error("в XML не была найдена ветка /alarm_checker/register ");
								informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
								returnValue=true;
								break;
							}
							register=Integer.parseInt(registerXmlElement.getTextContent().trim());
						}catch(Exception ex){};
						
						Element add=(Element)this.getNode(alarm_checker,"add");
						if(add!=null){
							logger.debug("ветка alarm_checker/add");
							Element id_file=(Element)this.getNode(add, "id_file");
							if((id_file!=null)&&(id_file.getTextContent()!=null)&&(!id_file.getTextContent().trim().equals(""))){
								String addedTaskId=this.addAlarmChecker(moduleIdentifier, 
													 sensorContainer, 
													 addressModbus,
													 register,
													 id_file.getTextContent().trim(),
													 alarmServiceAware,
													 alarmService, 
													 alarmTimeError);
								if(addedTaskId!=null){
									logger.debug("ветка alarm_checker/add выслать подтверждение о получении задачи и ее выполнении"); 
									informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getAddConfirm(taskId,addressModbus,register, id_file.getTextContent().trim(),addedTaskId))));
								}else{
									logger.error("ветка alarm_checker/add выслать ошибку в выполнении задачи - файл не прочитан с сервера "); 
									informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
								}
							}else{
								logger.error("ветка alarm_checker/add выслать ошибку в выполнении задачи"); 
								informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
							};
							returnValue=true;
							break;
						}
						
						Element replace=(Element)this.getNode(alarm_checker,"replace");
						if(replace!=null){
							logger.debug("ветка alarm_checker/replace");
							Element id_file=(Element)this.getNode(replace, "id_file");
							Element id_checker=(Element)this.getNode(replace,"id_checker");
							if(  (id_file!=null)&&(id_file.getTextContent()!=null)&&(!id_file.getTextContent().trim().equals(""))
								&&(id_checker!=null)&&(id_checker.getTextContent()!=null)&&(!id_checker.getTextContent().trim().equals(""))
								){
								String savedFileId=this.replaceAlarmChecker(moduleIdentifier, 
														 id_checker, 
														 sensorContainer, 
														 addressModbus,
														 register,
														 id_file.getTextContent().trim(),
														 alarmServiceAware, 
														 alarmService,
														 alarmTimeError);
								if(savedFileId!=null){
									logger.debug("ветка alarm_checker/replace  вылсать подтверждение в выполнении задачи"); 
									informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getAddConfirm(taskId, addressModbus, register, id_file.getTextContent(), savedFileId))));
								}else{
									logger.debug("ветка alarm_checker/replace  выслать ошибку в выполнении задачи"); 
									informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
								}
							}else{
								logger.debug("ветка alarm_checker/replace  выслать ошибку в выполнении задачи"); 
								informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
							};
							returnValue=true;
							break;
						}

						Element remove=(Element)this.getNode(alarm_checker,"remove");
						if(remove!=null){
							logger.debug("ветка alarm_checker/remove");
							Element id_checker=(Element)this.getNode(remove,"id_checker");
							if(  (id_checker!=null)&&(id_checker.getTextContent()!=null)&&(!id_checker.getTextContent().trim().equals(""))
								){
								if(this.removeAlarmChecker(id_checker, sensorContainer, addressModbus)==true){
									logger.debug("ветка alarm_checker/remove: выслать подтверждение в выполнении задачи"); 
									informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskOk(taskId))));
								}else{
									logger.debug("ветка alarm_checker/remove: вылсать ошибку выполнения указанной задачи"); 
									informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
								}
							}else{
								logger.debug("ветка alarm_checker/remove: вылсать ошибку выполнения указанной задачи"); 
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
					logger.warn("адресат не найден в контейнере датчиков/модулей ");
					informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
				}
			}else{
				logger.warn("ветка alarm_checker не найдена в документе, но управление было передано данному объекту - проверьте вызов функции #checkDocumentForProcess");
				informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
			}
			
		}catch(Exception ex){
			informationListener.notifyInformation(new ContainerModuleInformation(new ModuleInformation(this.getTaskError(taskId))));
			returnValue=false;
			logger.error("processDocument Exception: "+ex.getMessage());
		}
		return returnValue;
	}

	/** получить XML в виде строки, который выдает подтверждение получения удаленным клиентом задачи на добавление AlarmChecker-a 
	 * @param taskId - уникальный номер задачи 
	 * @param addressModbus - уникальный адрес устройства в сети Modbus
	 * @param register - уникальный номер регистра в масштабе устройства ( код которого в сети Modbus - addressModbus ) 
	 * @param idOnModule - уникальный идентификатор 
	 * @return
	 */
	private String getAddConfirm(int taskId, int addressModbus, Integer addressRegister, String id_file, String idOnModule) {
		try{
			Document document=this.getEmptyXmlDocument();
			Element task=this.getTaskElement(document);
			
			Element id=document.createElement("id");
			id.setTextContent(Integer.toString(taskId));
			task.appendChild(id);
			
			Element value=document.createElement("value");
			value.setTextContent("OK");
			task.appendChild(value);
			
			Element alarm_checker=document.createElement("alarm_checker");
			task.appendChild(alarm_checker);

				Element id_modbus=document.createElement("id_modbus");
				id_modbus.setTextContent(Integer.toString(addressModbus));
				alarm_checker.appendChild(id_modbus);
			
				Element add_confirm=document.createElement("add_confirm");
				alarm_checker.appendChild(add_confirm);
					
					
					Element register=document.createElement("register");
					if(addressRegister!=null){
						register.setTextContent(addressRegister.toString());
					}else{
						register.setTextContent("");
					}
					add_confirm.appendChild(register);
					
					Element idFile=document.createElement("id_file");
					idFile.setTextContent(id_file);
					add_confirm.appendChild(idFile);
					
					Element id_on_module=document.createElement("id_on_module");
					id_on_module.setTextContent(idOnModule);
					add_confirm.appendChild(id_on_module);
					
			return this.convertXmlDocumentToString(document);
		}catch(Exception ex){
			logger.error("getAlarmCheckerList Exception:"+ex.getMessage());
			return null;
		}
	}

	/** удалить в указанном сенсоре/датчик файл от сервера в виде проверки на наличие события Alarm 
	 * @param idChecker - уникальный номер элемента, который нужно удалить  
	 * @param sensorContainer - контейнер, который содержит датчики/сенсоры в сети ModBus
	 * @param addressModbus - уникальный адрес датчика/сенсора в сети Modbus 
	 */
	private boolean removeAlarmChecker(Element idChecker,
									SensorContainer sensorContainer, 
									int addressModbus) {
		try{
			sensorContainer.getSensorByModbusAddress(addressModbus).removeAlarmChecker(Integer.parseInt(idChecker.getTextContent()));
			return true;
		}catch(Exception ex){
			logger.error("произошла ошибка при попытке удаления Checker-a ");
			return false;
		}
		
	}
	
	/** заменить в указанном сенсоре/датчик файл от сервера в виде проверки на наличие события Alarm 
	 * @param moduleIdentifier - уникальный идентификатор данного модуля 
	 * @param idChecker - уникальный иденификатор
	 * @param sensorContainer - контейнер, который содержит
	 * @param addressModbus - адрес модуля/сенсора в масштабе сети Modbus
	 * @param idFile - идентификатор файла на сервере
	 * @param alarmServiceAware - фабрика по генерации сервисов свзяи с базой
	 * @param alarmService - текущий сервис связи с базой
	 * @param timeError - время задержки перед очередной попыткой связи с сервером после неудачного соединения
	 */
	private String replaceAlarmChecker(ModuleIdentifier moduleIdentifier,
									 Element idChecker,
									 SensorContainer sensorContainer, 
									 int addressModbus,
									 int addressRegister, 
									 String idFile,
									 IAlarmAware alarmServiceAware, 
									 IAlarm alarmService, 
									 int timeError) {
		try{
			int id_checker=Integer.parseInt(idChecker.getTextContent().trim());
			sensorContainer.getSensorByModbusAddress(addressModbus).removeAlarmChecker(id_checker);
			Checker<AlarmMessage> checker=this.getAlarmChecker(moduleIdentifier, idFile,alarmServiceAware, alarmService, timeError);
			if(checker==null){
				throw new Exception("сервер не выслал объект Checker на запрос файла:"+idFile);
			}
			return sensorContainer.getSensorByModbusAddress(addressModbus).addAlarmChecker(checker);
			// отправить подтверждение 
			// sendAlarmCheckerConfirm(alarmServiceAware,alarmService, timeError, moduleIdentifier, idFile);
		}catch(Exception ex){
			logger.error("replaceAlarmChecker Exception: "+ex.getMessage());
			return null;
		}
	}

	/** добавить в указанный сенсор/датчик файл от сервера в виде проверки на наличие события Alarm 
	 * @param moduleIdentifier - уникальный идентификатор модуля 
	 * @param sensorContainer - контейнер сенсоров 
	 * @param addressModbus - адрес в сети Modbus
	 * @param addressRegister - адрес регистра на модуле/сенсоре 
	 * @param idFile - уникальный идентификатор файла 
	 * @param alarmServiceAware - сервис, который генерирует AlarmServcie
	 * @param alarmService - текущий Alarm сервис, если не действителен, будет вызван AlarmServiceAware
	 * @param timeError - время ожидания, в случае не ответа сервера
	 * @result уникальный идентификатор данного checker-а на модуле  
	 */
	private String addAlarmChecker(ModuleIdentifier moduleIdentifier,
								 SensorContainer sensorContainer, 
								 int addressModbus,
								 int addressRegister,
								 String idFile,
								 IAlarmAware alarmServiceAware, 
								 IAlarm alarmService, 
								 int timeError) {
		Checker<AlarmMessage> checker=this.getAlarmChecker(moduleIdentifier, 
														   idFile,
														   alarmServiceAware, 
														   alarmService, 
														   timeError);
		if(checker!=null){
			return sensorContainer.getSensorByModbusAddress(addressModbus).addAlarmChecker(checker);
			// отправить подтверждение получения задания от сервера 
			// sendAlarmCheckerConfirm(alarmServiceAware,alarmService, timeError, moduleIdentifier, idFile);
		}else{
			// INFO - модуль. ошибка получения от сервера Checker-а
			logger.error("сервер не вернул Checker<AlarmMessage> по файлу:"+idFile);
			return null;
			// sendAlarmCheckerConfirm(alarmServiceAware,alarmService, timeError, moduleIdentifier, idFile);
		}
	}

	/* отправить подтверждение получения задания 
	 * @param alarmServiceAware - генератор сервисов 
	 * @param alarmService - сервис 
	 * @param moduleIdentifier - идентификатор модуля 
	 * @param idFile - уникальный идентификатор файла 
	private void sendAlarmCheckerConfirm(IAlarmAware alarmServiceAware, 
										 IAlarm alarmService,
										 long timeError, 
										 ModuleIdentifier moduleIdentifier, 
										 String idFile){
		while(true){
			try{
				alarmService.confirmAlarmCheckerGet(moduleIdentifier, idFile);
				break;
			}catch(Exception ex){
				if(alarmService==null){
					alarmService=alarmServiceAware.getAlarm();
				}
				logger.error("sendAlarmCheckerConfirm Exception: "+ex.getMessage());
				try{
					Thread.sleep(timeError);
				}catch(Exception exInner){};
			}
		}
	}
	 */
	
    /** получить объект из массива байт ( десериализовать из потока байт )*/
	private Object getObjectFromByteArray(byte[] data) throws IOException, ClassNotFoundException{
        Object return_value=null;
        ByteArrayInputStream inputStream=new ByteArrayInputStream(data);
        ObjectInputStream ois=new ObjectInputStream(inputStream);
        return_value=ois.readObject();
        ois.close();
        inputStream.close();
        return return_value;
    }
	
	
	/** получить от сервера и сохранить в локальном хранилище полученный класс Checker ( чтобы можно было при первоначальной загрузке получить данный класс из хранилища) 
	 * @param moduleIdentifier - уникальный идентификатор модуля 
	 * @param idFile - идентификатор файла
	 * @param alarmServiceAware - сервис, которые генерирует объекты связи с сервером
	 * @param alarmService - сервис по связи с сервером 
	 * @param timeError - время, которое нужно выждать перед очередной попыткой связи с сервером 
	 * @return объект, который прочитан из репозитория базы данных 
	 * */
	@SuppressWarnings("unchecked")
	private Checker<AlarmMessage> getAlarmChecker(ModuleIdentifier moduleIdentifier, 
										 		  String idFile,
										 		  IAlarmAware alarmServiceAware, 
										 		  IAlarm alarmService, 
										 		  int timeError){
		Checker<AlarmMessage> returnValue=null;
		TransportChecker transportValue=null;
		while(true){
			try{
				transportValue=alarmService.getAlarmCheckerById(moduleIdentifier, idFile);
				sendConfirmGettingFile(moduleIdentifier,idFile, alarmServiceAware, alarmService,timeError);
				break;
			}catch(Exception ex){
				logger.error("getAlarmChecker Exception: "+ex.getMessage());
				if(alarmService==null){
					alarmService=alarmServiceAware.getAlarm();
				}
				try{
					Thread.sleep(timeError);
				}catch(Exception exInner){};
			}
		}
		try{
			returnValue=(Checker<AlarmMessage>)this.getObjectFromByteArray(transportValue.getObjectAsByteArray());
		}catch(Exception ex){};
		return returnValue;
	}
	
	/** послать на сервер уведомление о получении данного файла */
	private void sendConfirmGettingFile(ModuleIdentifier moduleIdentifier, 
	 		  							String idFile,
	 		  							IAlarmAware alarmServiceAware, 
	 		  							IAlarm alarmService, 
	 		  							int timeError){
		while(true){
			try{
				alarmService.confirmAlarmCheckerGet(moduleIdentifier, idFile);
				break;
			}catch(Exception ex){
				logger.error("sendConfirmGettingFile Exception: "+ex.getMessage());
				if(alarmService==null){
					alarmService=alarmServiceAware.getAlarm();
				}
				try{
					Thread.sleep(timeError);
				}catch(Exception exInner){};
			}
		}
	}
	
	/** получить XML строку со списком всех присоединенных к данному сенсору/датчику AlarmChecker-ов 
	 * @param taskId - уникальный номер задачи 
	 * @param modbusAddress - уникальный номер датчика/модуля в сети ModBus 
	 * @param sensor - датчик/сенсор 
	 * @return строка XML 
	 */
	private String getAlarmCheckerList(int taskId, 
									   int modbusAddress, 
									   Sensor sensor) {
		try{
			Document document=this.getEmptyXmlDocument();
			Element task=this.getTaskElement(document);
			
			Element id=document.createElement("id");
			id.setTextContent(Integer.toString(taskId));
			task.appendChild(id);
			
			Element value=document.createElement("value");
			value.setTextContent("OK");
			task.appendChild(value);
			
			Element alarm_checker=document.createElement("alarm_checker");
			task.appendChild(alarm_checker);
			
				Element id_modbus=document.createElement("id_modbus");
				id_modbus.setTextContent(Integer.toString(modbusAddress));
				alarm_checker.appendChild(id_modbus);
				
				Element list=document.createElement("list");
				alarm_checker.appendChild(list);
				
					for(int index=0;index<sensor.getAlarmCheckerSize();index++){
						Checker<AlarmMessage> alarmChecker=sensor.getAlarmChecker(index);
						if(alarmChecker!=null){
							list.appendChild(this.getChecker(alarmChecker,
															 document,
															 index, 
															 modbusAddress));
						}
					}
			
			return this.convertXmlDocumentToString(document);
		}catch(Exception ex){
			logger.error("getAlarmCheckerList Exception:"+ex.getMessage());
			return null;
		}
	}

	
}
