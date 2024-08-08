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
	/** ��������� ��� ��������, ���������� �� ��������� ������� */
	private IStorage<Object> storageSaveInformation=null;
	/** ��������� ��� ��������, ��������������� ��� �������� �� ������ */
	private IStorage<Object> storageModuleChecker=null;
	private XPath xpath=XPathFactory.newInstance().newXPath();
	/** ��������� ������� ��� ��������� */
	private EventFilterInformation monitorEvent=new EventFilterInformation();
	
	public InformationImplementation(){
		// ������� ��������� ��� ��������
		storageSaveInformation=new ObjectFileStorage(StaticSettings.getPathToStorage("Information"),
				   									 (String)StaticSettings.getObject(StaticSettings.informationStorageExtension));
		storageModuleChecker=new ObjectFileStorage(StaticSettings.getPathToStorage("InformationChecker"),
				   								   (String)StaticSettings.getObject(StaticSettings.informationCheckerStorageExtension));
		// INFO ������. ����������� ModuleInformation, ���������� �� ��������� �������
		// ��������� ��� ThreadHeartBeat
		this.addInformationHandler(new InformationHandlerSettingsHeartBeat());
		// ��������� ��� ThreadInformation
		this.addInformationHandler(new InformationHandlerSettingsInformation());
		// ��������� ��� ThreadAlarm
		this.addInformationHandler(new InformationHandlerSettingsAlarm());
		// ��������� ������ ��������/�������� sensor_list
		this.addInformationHandler(new InformationHandlerSensorList());
		// �������� �������� �������/������� sensor.get
		this.addInformationHandler(new InformationHandlerSensor());
		// �������� ������ Alarm_checker-�� �� ���������� ������ 
		this.addInformationHandler(new InformationHandlerAlarmChecker());
		// �������� ������ Information_checker-�� �� ���������� ������ 
		this.addInformationHandler(new InformationHandlerInformationChecker());
		// �������� ��������� ThreadSensor
		this.addInformationHandler(new InformationHandlerSettingsSensor());
		// ���������� ������� � ������������ ���� ������������� ��������� Task.id (OK, Error )
		this.addInformationHandler(new InformationHandlerResponseTask());
	}
	
	/** �������� ���������� id ������ �� �������������� 
	 * @param connector -  ���������� � ����� ������ 
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @return 
	 * <li> <b> value&lt0 </b>  �� ������� ���������������� ������ </li>
	 * <li> <b>value&gt0</b> ���� ������ ������� ��������������� </li>
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


	/** �������� ������ �� ������� � ���� ������ (module_information_checker) 
	 * @param connector - ���������� � ����� ������ 
	 * @param idModule - ���������� ������������� ������ 
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @param fileId - ���������� ��� ������ � ������� 
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
	
	/** ����������� ������ � ������ ���� */
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
			// �������� ���������� ������������� ������ 
			int moduleId=this.getIdFromIdentifier(connector, moduleIdentifier);
			if(moduleId>0){
				// �������� ������, ������� �������� ������ �� Checker 
				ModuleInformationCheckerWrap checker=this.getCheckerInformation(connector, moduleId, moduleIdentifier, fileId);
				TransportChecker returnValue=new TransportChecker();
				@SuppressWarnings("unchecked")
				Checker<InformationMessage> objectForSend=(Checker<InformationMessage>)this.storageModuleChecker.read(checker.getIdStorage());
				objectForSend.setIdFile(checker.getId());
				objectForSend.setDescription(checker.getDescription());
				// INFO ������.��������� ���������� ������ Checker<InformationMessage>
				returnValue.setObjectAsByteArray(convertObjectToByteArray(objectForSend));
				// ��������� ������������� � ���, ��� ������ ���� ������� 
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
			// �������� ���������� ������������� ������ 
			int moduleId=this.getIdFromIdentifier(connector, moduleIdentifier);
			if(moduleId>0){
				// �������� ������, ������� �������� ������ �� Checker 
				ModuleInformationCheckerWrap checker=this.getCheckerInformation(connector, moduleId, moduleIdentifier, fileId);
				if(checker!=null){
					// ��������� ������������� � ���, ��� ������ ���� ������������ 
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

	/** ������ ������������������ ������������ ��� ���������� �������������� ��������� �� ������ */
	private ArrayList<InformationHandler> listOfHandler=new ArrayList<InformationHandler>();
	
	/** �������� ���������� �������������� ���������, �������� ����� �������� �������� {@link ModuleInformation}*/
	public void addInformationHandler(InformationHandler handler){
		this.listOfHandler.add(handler);
	}
	
	/** ������� ���������� �������������� ���������, ������� ������������ �������� ������� �������������� ��������� �� ��������� �������  */
	public void removeInformationHandler(InformationHandler handler){
		this.listOfHandler.remove(handler);
	}
	
	@Override
	public String sendInformation(ModuleIdentifier moduleIdentifier,
								  ContainerModuleInformation moduleInformation) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// �������� ���������� ����� ������
			int moduleId=this.getIdFromIdentifier(connector, moduleIdentifier);
			if(moduleId>0){
				String fileName=null;
				// ��������� �������������� ��������� � ��������� (�������� �� ����� �������)
				for(int index=0;index<moduleInformation.getContent().length;index++){
					// ��������� ���� � ���������, � �������� ��� 
					fileName=this.storageSaveInformation.save(moduleInformation.getContent()[index]);
					// ��������� �������������� ��������� � ����
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
					// INFO ������.���������� �������� � ������� Information - ���������� ��������� ��������� ��� �������������� ������
					this.monitorEvent.notifyInformationEvent(connector, moduleInformationWrap);
					// INFO ������.�������������� ���������.���������
					checkModuleInformationOnTaskId(connector, moduleInformation.getContent()[index]);
					// �������� �������� ��������� ���� �������������� ������������, ������� ���� ���������������� 
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
	 * �������� �� String, ���������� ��� XML ����� ������ Document
	 * @param value ������, ���������� XML �����
	 * @return null, ���� ��������� ������ ��������, ���� �� ��� Document
	 */
	private Document getXmlFromString(String value){
		Document return_value=null;
		javax.xml.parsers.DocumentBuilderFactory document_builder_factory=javax.xml.parsers.DocumentBuilderFactory.newInstance();
        // ���������� ������������� ���������� Parser-��
        document_builder_factory.setValidating(false);
        try {
            // ��������� �����������
            javax.xml.parsers.DocumentBuilder parser=document_builder_factory.newDocumentBuilder();
            // Parse ��������
            return_value=parser.parse(new ByteArrayInputStream(value.getBytes()));
        }catch(Exception ex){
        	logger.error("getXmlFromString Exception: "+ex.getMessage());
        }
		return return_value;
	}
	
	/** �������� 
	 * @param document - �������� ( ������� �� ������� )
	 * @param xpath - ���� � ��������  
	 * @return null, ���� ��������� ������ ��������� ������ ��� ������
	 */
	private Object getNode(Node document, String xpathString){
		try{
			return this.xpath.evaluate(xpathString, document,XPathConstants.NODE);
		}catch(Exception ex){
			return null;
		}
	}
	
	/**
	 * ��������� ��������� ��������� �� ���������� ������ �� ������������� ���������(���� �� ����� � XML ��������� /task_response/task/value=="OK")
	 * @param connector
	 * @param moduleInformation
	 */
	private void checkModuleInformationOnTaskId(ConnectWrap connector,ModuleInformation moduleInformation) {
		// �������� XML �������� �� ������
		Document document=this.getXmlFromString(moduleInformation.getContent());
		try{
			// �������� Node �� ��������� XPath ����
			int taskId=Integer.parseInt( ((Element)this.getNode(document, "//task_response/task/id")).getTextContent());
			// ��������� �� ������� ���� task_response.task.value
			String value=null;
			try{
				value=((Element)this.getNode(document, "//task_response/task/value")).getTextContent().trim();
			}catch(Exception ex){};
			// �������� ������ �� ���� ModuleTaskWrap
			Session session=connector.getSession();
			ModuleTaskWrap taskWrap=(ModuleTaskWrap)session.get(ModuleTaskWrap.class, new Integer(taskId));
			if(taskWrap!=null){
				// ���������� ModuleTaskWrap.state=2 - ���������� ������� 
				taskWrap.setIdState(2);
				if(value!=null){
					if(value.equalsIgnoreCase("OK")){
						// ������ ������� ���������� 
						taskWrap.setIdResult(1);
					}else{
						// ������ ��������� ������
						taskWrap.setIdResult(3);
					}
				}else{
					// ����������� ����� �� ������ 
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
