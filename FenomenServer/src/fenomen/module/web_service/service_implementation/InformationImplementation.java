package fenomen.module.web_service.service_implementation;

import java.io.ByteArrayInputStream;


import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.Module;
import database.wrap.ModuleInformationCheckerWrap;
import database.wrap.ModuleInformationWrap;
import database.wrap.ModuleTaskWrap;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;
import fenomen.module.web_service.common.ContainerModuleInformation;
import fenomen.module.web_service.common.TransportChecker;
import fenomen.module.web_service.service.IInformation;
import fenomen.module.web_service.service_implementation.information_handler.InformationHandler;
import fenomen.module.web_service.service_implementation.information_handler.InformationHandlerAlarmChecker;
import fenomen.module.web_service.service_implementation.information_handler.InformationHandlerInformationChecker;
import fenomen.module.web_service.service_implementation.information_handler.InformationHandlerResponseTask;
import fenomen.module.web_service.service_implementation.information_handler.InformationHandlerSensor;
import fenomen.module.web_service.service_implementation.information_handler.InformationHandlerSensorList;
import fenomen.module.web_service.service_implementation.information_handler.InformationHandlerSettingsAlarm;
import fenomen.module.web_service.service_implementation.information_handler.InformationHandlerSettingsHeartBeat;
import fenomen.module.web_service.service_implementation.information_handler.InformationHandlerSettingsInformation;
import fenomen.module.web_service.service_implementation.information_handler.InformationHandlerSettingsSensor;
import fenomen.module.web_service.service_implementation.settings.StaticSettings;
import fenomen.module.web_service.service_implementation.storage.ObjectFileStorage;
import fenomen.module.web_service.service_implementation.storage.IStorage;
import fenomen.monitor.notifier.EventFilterInformation;
import fenomen.server.controller.server.generator_alarm_checker.calc.Checker;
import fenomen.server.controller.server.generator_alarm_checker.message.InformationMessage;

public class InformationImplementation implements IInformation{
	Logger logger=Logger.getLogger(this.getClass());
	/** хранилище для объектов, полученных от удаленных модулей */
	private IStorage<Object> storageSaveInformation=null;
	/** хранилище для объектов, предназначенных для отправки на модуль */
	private IStorage<Object> storageModuleChecker=null;
	private XPath xpath=XPathFactory.newInstance().newXPath();
	/** генератор событий для мониторов */
	private EventFilterInformation monitorEvent=new EventFilterInformation();
	
	public InformationImplementation(){
		// создать хранилища для объектов
		storageSaveInformation=new ObjectFileStorage(StaticSettings.getPathToStorage("Information"),
				   									 (String)StaticSettings.getObject(StaticSettings.informationStorageExtension));
		storageModuleChecker=new ObjectFileStorage(StaticSettings.getPathToStorage("InformationChecker"),
				   								   (String)StaticSettings.getObject(StaticSettings.informationCheckerStorageExtension));
		// INFO сервер. обработчики ModuleInformation, полученных от удаленных модулей
		// настройки для ThreadHeartBeat
		this.addInformationHandler(new InformationHandlerSettingsHeartBeat());
		// настройки для ThreadInformation
		this.addInformationHandler(new InformationHandlerSettingsInformation());
		// настройки для ThreadAlarm
		this.addInformationHandler(new InformationHandlerSettingsAlarm());
		// получение списка сенсоров/датчиков sensor_list
		this.addInformationHandler(new InformationHandlerSensorList());
		// получить значение сенсора/датчика sensor.get
		this.addInformationHandler(new InformationHandlerSensor());
		// получить список Alarm_checker-ов от удаленного модуля 
		this.addInformationHandler(new InformationHandlerAlarmChecker());
		// получить список Information_checker-ов от удаленного модуля 
		this.addInformationHandler(new InformationHandlerInformationChecker());
		// получить настройки ThreadSensor
		this.addInformationHandler(new InformationHandlerSettingsSensor());
		// Анализатор ответов о пложительной либо отрицательной обработке Task.id (OK, Error )
		this.addInformationHandler(new InformationHandlerResponseTask());
	}
	
	/** получить уникальный id модуля по идентификатору 
	 * @param connector -  соединение с базой данных 
	 * @param moduleIdentifier - Уникальный идентификатор модуля 
	 * @return 
	 * <li> <b> value&lt0 </b>  не удалось идентифицировать модуль </li>
	 * <li> <b>value&gt0</b> если модуль успешно идентифицирован </li>
	 */
	private int getIdFromIdentifier(ConnectWrap connector, ModuleIdentifier moduleIdentifier){
		int returnValue=(-1);
		try{
			Session session=connector.getSession();
			Module module=(Module)session.createCriteria(Module.class).add(Restrictions.eq("idModule", moduleIdentifier.getId())).uniqueResult();
			if(module!=null){
				returnValue=module.getId();
			}
		}catch(Exception ex){
			if(moduleIdentifier!=null){
				logger.error("getIdFromIdentifier: Exception: "+ex.getMessage()+" ModuleIdentifier is not recognized: "+moduleIdentifier.getId());
			}else{
				logger.error("getIdFromIdentifier ModuleIdentifier is null ");
			}
			
		}
		return returnValue; 
	}


	/** получить запись из таблицы в базе данных (module_information_checker) 
	 * @param connector - соединение с базой данных 
	 * @param idModule - уникальный идентификатор модуля 
	 * @param moduleIdentifier - полученный идентификатор модуля 
	 * @param fileId - уникальный код записи в таблице 
	 * @return
	 */
	private ModuleInformationCheckerWrap getCheckerInformation(ConnectWrap connector, int idModule, ModuleIdentifier moduleIdentifier, String fileId){
		ModuleInformationCheckerWrap returnValue=(ModuleInformationCheckerWrap)connector.getSession().get(ModuleInformationCheckerWrap.class, new Integer(Integer.parseInt(fileId)));
		if(returnValue.getIdModule()==idModule){
			return returnValue;
		}else{
			logger.error("getChekcerInformation ModuleInformationChecker is not recognized:"+moduleIdentifier.getId() +"    FileId:"+fileId);
			return null;
		}
	}
	
	/** представить объект в массив байт */
	private byte[] convertObjectToByteArray(Object object) throws Exception{
        ByteArrayOutputStream byte_array=new ByteArrayOutputStream();
        ObjectOutputStream oos=new ObjectOutputStream(byte_array);
        oos.writeObject(object);
        oos.flush();
        oos.close();
        byte_array.close();
        return byte_array.toByteArray();
	}

	@Override
	public TransportChecker getInformationCheckerById(ModuleIdentifier moduleIdentifier, 
												  	  String fileId) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// получить уникальный идентификатор модуля 
			int moduleId=this.getIdFromIdentifier(connector, moduleIdentifier);
			if(moduleId>0){
				// получить запись, которая содержит ссылку на Checker 
				ModuleInformationCheckerWrap checker=this.getCheckerInformation(connector, moduleId, moduleIdentifier, fileId);
				TransportChecker returnValue=new TransportChecker();
				@SuppressWarnings("unchecked")
				Checker<InformationMessage> objectForSend=(Checker<InformationMessage>)this.storageModuleChecker.read(checker.getIdStorage());
				objectForSend.setIdFile(checker.getId());
				objectForSend.setDescription(checker.getDescription());
				// INFO сервер.отправить удаленному модулю Checker<InformationMessage>
				returnValue.setObjectAsByteArray(convertObjectToByteArray(objectForSend));
				// сохранить подтверждение о том, что задача была забрана 
				connector.getSession().beginTransaction();
				checker.setIdState(1);
				connector.getSession().update(checker);
				connector.getSession().getTransaction().commit();
				return returnValue;
			}else{
				throw new Exception("module is not recognized: "+moduleIdentifier.getId());
			}
		}catch(Exception ex){
			logger.error("getAlarmCheckerById Exception: "+ex.getMessage());
			return null;
		}finally{
			try{
				connector.close();
			}catch(Exception ex){};
		}
	}

	@Override
	public String confirmInformationCheckerGet(ModuleIdentifier moduleIdentifier, 
											   String fileId) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// получить уникальный идентификатор модуля 
			int moduleId=this.getIdFromIdentifier(connector, moduleIdentifier);
			if(moduleId>0){
				// получить запись, которая содержит ссылку на Checker 
				ModuleInformationCheckerWrap checker=this.getCheckerInformation(connector, moduleId, moduleIdentifier, fileId);
				if(checker!=null){
					// сохранить подтверждение о том, что задача была подтверждена 
					connector.getSession().beginTransaction();
					checker.setIdState(2);
					connector.getSession().update(checker);
					connector.getSession().getTransaction().commit();
					return IInformation.returnOk;
				}else{
					return IInformation.returnError;
				}
			}else{
				throw new Exception("module is not recognized: "+moduleIdentifier.getId());
			}
		}catch(Exception ex){
			logger.error("getAlarmCheckerById Exception: "+ex.getMessage());
			return IInformation.returnError;
		}finally{
			try{
				connector.close();
			}catch(Exception ex){};
		}
	}

	/** список зарегестрированных обработчиков для полученных информационных сообщений от модуля */
	private ArrayList<InformationHandler> listOfHandler=new ArrayList<InformationHandler>();
	
	/** добавить обработчик информационных сообщений, которому нужно передать принятые {@link ModuleInformation}*/
	public void addInformationHandler(InformationHandler handler){
		this.listOfHandler.add(handler);
	}
	
	/** удалить обработчик информационных сообщений, который обрабатывает входящие запросы информационные сообщения от удаленных модулей  */
	public void removeInformationHandler(InformationHandler handler){
		this.listOfHandler.remove(handler);
	}
	
	@Override
	public String sendInformation(ModuleIdentifier moduleIdentifier,
								  ContainerModuleInformation moduleInformation) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// получить уникальный номер модуля
			int moduleId=this.getIdFromIdentifier(connector, moduleIdentifier);
			if(moduleId>0){
				String fileName=null;
				// сохранить информационное сообщение в хранилище (пробежка по всему объекту)
				for(int index=0;index<moduleInformation.getContent().length;index++){
					// сохранить файл в хранилище, и передать его 
					fileName=this.storageSaveInformation.save(moduleInformation.getContent()[index]);
					// сохранить информационное сообщение в базе
					connector.getSession().beginTransaction();
					ModuleInformationWrap moduleInformationWrap=new ModuleInformationWrap();
					moduleInformationWrap.setIdModule(moduleId);
					moduleInformationWrap.setIdStorage(fileName);
					String description=moduleInformation.getContent()[index].getContent();
					if(description.length()>1024){
						description=description.substring(0,1023);
					}
					moduleInformationWrap.setDescription(description);
					moduleInformationWrap.setSensorRegisterAddress(moduleInformation.getContent()[index].getRegisterAddress());
					connector.getSession().save(moduleInformationWrap);
					connector.getSession().getTransaction().commit();
					// INFO сервер.оповестить мониторы о событии Information - оповещение мониторов поставить как первоочередную задачу
					this.monitorEvent.notifyInformationEvent(connector, moduleInformationWrap);
					// INFO сервер.информационные сообщения.обработка
					checkModuleInformationOnTaskId(connector, moduleInformation.getContent()[index]);
					// передать принятое сообщение всем информационным обработчикам, которые были зарегестрированы 
					for(InformationHandler handler : this.listOfHandler){
						handler.processModuleInformation(moduleIdentifier, moduleInformation.getContent()[index]);
					}
				}
			}else{
				throw new Exception("moduleId not recognized: "+moduleIdentifier.getId());
			}
			return IInformation.returnOk;
		}catch(Exception ex){
			logger.error("sendInformation Exception: "+ex.getMessage());
			return IInformation.returnError;
		}finally{
			try{
				connector.close();
			}catch(Exception ex){};
		}
	}

	/** 
	 * получить из String, содержащий вид XML файла объект Document
	 * @param value строка, содержащая XML текст
	 * @return null, если произошла ошибка парсинга, либо же сам Document
	 */
	private Document getXmlFromString(String value){
		Document return_value=null;
		javax.xml.parsers.DocumentBuilderFactory document_builder_factory=javax.xml.parsers.DocumentBuilderFactory.newInstance();
        // установить непроверяемое порождение Parser-ов
        document_builder_factory.setValidating(false);
        try {
            // получение анализатора
            javax.xml.parsers.DocumentBuilder parser=document_builder_factory.newDocumentBuilder();
            // Parse источник
            return_value=parser.parse(new ByteArrayInputStream(value.getBytes()));
        }catch(Exception ex){
        	logger.error("getXmlFromString Exception: "+ex.getMessage());
        }
		return return_value;
	}
	
	/** получить 
	 * @param document - документ ( задание от сервера )
	 * @param xpath - путь к элементу  
	 * @return null, если произошла ошибка получения данных или объект
	 */
	private Object getNode(Node document, String xpathString){
		try{
			return this.xpath.evaluate(xpathString, document,XPathConstants.NODE);
		}catch(Exception ex){
			return null;
		}
	}
	
	/**
	 * проверить пришедшее сообщение от удаленного модуля на положительную обработку(есть ли ветка в XML документа /task_response/task/value=="OK")
	 * @param connector
	 * @param moduleInformation
	 */
	private void checkModuleInformationOnTaskId(ConnectWrap connector,ModuleInformation moduleInformation) {
		// получить XML документ из текста
		Document document=this.getXmlFromString(moduleInformation.getContent());
		try{
			// получить Node на основании XPath пути
			int taskId=Integer.parseInt( ((Element)this.getNode(document, "//task_response/task/id")).getTextContent());
			// проверить на наличие пути task_response.task.value
			String value=null;
			try{
				value=((Element)this.getNode(document, "//task_response/task/value")).getTextContent().trim();
			}catch(Exception ex){};
			// получить объект из базы ModuleTaskWrap
			Session session=connector.getSession();
			ModuleTaskWrap taskWrap=(ModuleTaskWrap)session.get(ModuleTaskWrap.class, new Integer(taskId));
			if(taskWrap!=null){
				// установить ModuleTaskWrap.state=2 - обработано модулем 
				taskWrap.setIdState(2);
				if(value!=null){
					if(value.equalsIgnoreCase("OK")){
						// задача успешно обработана 
						taskWrap.setIdResult(1);
					}else{
						// ошибка обработки задачи
						taskWrap.setIdResult(3);
					}
				}else{
					// неизвестный ответ от модуля 
					taskWrap.setIdResult(0);
				}
				// update ModuleTaskWrap. 
				session.beginTransaction();
				session.update(taskWrap);
				session.getTransaction().commit();
				logger.debug("checkModuleAlarmOnTaskId Confirm OK ModuleTask.id="+taskId);
			}else{
				logger.error("checkModuleAlarmOnTaskId ModuleTask.id="+taskId+" is not found in Database");
			}
		}catch(Exception ex){
			logger.error("checkModuleAlarmOnTaskId Exception:"+ex.getMessage());
		}
	}


}
