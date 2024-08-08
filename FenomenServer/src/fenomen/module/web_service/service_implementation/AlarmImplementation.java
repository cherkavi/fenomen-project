package fenomen.module.web_service.service_implementation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import javax.xml.xpath.XPath;
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
import database.wrap.ModuleAlarmCheckerWrap;
import database.wrap.ModuleAlarmWrap;
import database.wrap.ModuleRestart;
import database.wrap.ModuleTaskWrap;
import fenomen.module.web_service.common.TransportChecker;
import fenomen.module.web_service.common.ModuleAlarm;
import fenomen.module.web_service.common.ContainerModuleAlarm;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.service.IAlarm;
import fenomen.module.web_service.service_implementation.settings.StaticSettings;
import fenomen.module.web_service.service_implementation.storage.ObjectFileStorage;
import fenomen.module.web_service.service_implementation.storage.IStorage;
import fenomen.monitor.notifier.EventFilterAlarm;
import fenomen.monitor.notifier.EventFilterRestart;
import fenomen.server.controller.server.generator_alarm_checker.calc.Checker;
import fenomen.server.controller.server.generator_alarm_checker.message.AlarmMessage;

public class AlarmImplementation implements IAlarm{
	Logger logger=Logger.getLogger(this.getClass());
	/** ��������� ��� AlarmInformation, ������� ���� �������� �� �������  */
	private IStorage<Object> storageSaveAlarm=null;
	/** ��������� ��� Checker[AlarmMessage] ��� �������� �� ������ */
	private IStorage<Object> storageModuleChecker=null;
	private XPath xpath=XPathFactory.newInstance().newXPath();
	/** ��������� ������� ��� ���������, ��������� ������� �� ������ event_settings_*  */
	private EventFilterAlarm monitorAlarmFilter=new EventFilterAlarm();
	/** ��������� ������� ��� ���������, ��������� ������� �� ������ event_settings_*  */
	private EventFilterRestart monitorRestartFilter=new EventFilterRestart();
	
	public AlarmImplementation(){
		storageSaveAlarm=new ObjectFileStorage(StaticSettings.getPathToStorage("Alarm"),
											   (String)StaticSettings.getObject(StaticSettings.alarmStorageExtension));
		storageModuleChecker=new ObjectFileStorage(StaticSettings.getPathToStorage("AlarmChecker"),
												   (String)StaticSettings.getObject(StaticSettings.alarmCheckerStorageExtension));
		
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


	/** �������� ������ �� ������� � ���� ������ (module_alarm_checker) 
	 * @param connector - ���������� � ����� ������ 
	 * @param idModule - ���������� ������������� ������ 
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @param fileId - ���������� ��� ������ � ������� 
	 * @return
	 */
	private ModuleAlarmCheckerWrap getCheckerAlarm(ConnectWrap connector, int idModule, ModuleIdentifier moduleIdentifier, String fileId){
		ModuleAlarmCheckerWrap returnValue=(ModuleAlarmCheckerWrap)connector.getSession().get(ModuleAlarmCheckerWrap.class, new Integer(Integer.parseInt(fileId)));
		if(returnValue.getIdModule()==idModule){
			return returnValue;
		}else{
			logger.error("getChekcerAlarm ModuleAlarmChecker is not recognized:"+moduleIdentifier.getId() +"    FileId:"+fileId);
			return null;
		}
	}

	@Override
	public String sendAlarm(ModuleIdentifier moduleIdentifier,
							ContainerModuleAlarm moduleAlarmContainer) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// �������� ���������� ����� ������
			int moduleId=this.getIdFromIdentifier(connector, moduleIdentifier);
			if(moduleId>0){
				String fileName=null;
				// ��������� ��������� ��������� � ��������� (�������� �� ����� �������)
				for(int index=0;index<moduleAlarmContainer.getContent().length;index++){
					fileName=this.storageSaveAlarm.save(moduleAlarmContainer.getContent()[index]);
					// INFO ������.��������� ��������� ��������� �� ������ � ����
					connector.getSession().beginTransaction();
					ModuleAlarmWrap moduleAlarmWrap=new ModuleAlarmWrap();
					moduleAlarmWrap.setIdModule(moduleId);
					moduleAlarmWrap.setIdStorage(fileName);
					moduleAlarmWrap.setDescription(moduleAlarmContainer.getContent()[index].getContent());
					moduleAlarmWrap.setIdSensor(moduleAlarmContainer.getContent()[index].getIdSensor());
					moduleAlarmWrap.setSensorRegisterAddress(moduleAlarmContainer.getContent()[index].getRegisterAddress());
					// moduleAlarmContainer.getContent()[index].getEventDate();
					// moduleAlarmContainer.getContent()[index].getValue();
					connector.getSession().save(moduleAlarmWrap);
					connector.getSession().getTransaction().commit();
					// ��������� �� ������� task_reponse.tast.id - �� �����, �.�. AlarmMessage �������� ��������� ���������, � �� �������, ��� ��� ������ Information
					// checkModuleAlarmOnTaskId(connector, moduleAlarmContainer.getContent()[index]);

					// INFO ������.���������� �������� � ������� Alarm
					monitorAlarmFilter.notifyAlarmEvent(connector, moduleAlarmWrap);
				}
			}else{
				throw new Exception("moduleId not recognized: "+moduleIdentifier.getId());
			}
			return IAlarm.returnOk;
		}catch(Exception ex){
			logger.error("sendAlarm Exception: "+ex.getMessage());
			return IAlarm.returnError;
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
			return this.xpath.evaluate(xpathString, document);
		}catch(Exception ex){
			return null;
		}
	}
	
	/** ��������� ������ moduleAlarm �� ������� � ��� task_reponse.task.id � ���� �� ������, �������� � ���� ������ �� ������ ������ State=2 (���������� ������� ) 
	 * @param connector - ���������� � ����� ������
	 * @param moduleAlarm - ������, ������� �������� ����� XML 
	 */
	@SuppressWarnings("unused")
	private void checkModuleAlarmOnTaskId(ConnectWrap connector,
									      ModuleAlarm moduleAlarm) {
		// �������� XML �������� �� ������
		Document document=this.getXmlFromString(moduleAlarm.getContent());
		try{
			// �������� Node �� ��������� XPath ����
			int taskId=Integer.parseInt( ((Element)this.getNode(document, "//task_response/task/id")).getTextContent());
			// ��������� �� ������� ���� task_response.task.value
			String error=null;
			try{
				error=((Element)this.getNode(document, "//task_response/task/value")).getTextContent().trim();
			}catch(Exception ex){};
			// �������� ������ �� ���� ModuleTaskWrap
			Session session=connector.getSession();
			ModuleTaskWrap taskWrap=(ModuleTaskWrap)session.get(ModuleTaskWrap.class, new Integer(taskId));
			if(taskWrap!=null){
				// ���������� ModuleTaskWrap.state=2
				taskWrap.setIdState(2);
				if(error!=null){
					taskWrap.setIdResult(2);
				}else{
					taskWrap.setIdResult(1);
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

	@Override
	public String confirmAlarmCheckerGet(ModuleIdentifier moduleIdentifier,
										 String fileId) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// �������� ���������� ������������� ������ 
			int moduleId=this.getIdFromIdentifier(connector, moduleIdentifier);
			if(moduleId>0){
				// �������� ������, ������� �������� ������ �� Checker 
				ModuleAlarmCheckerWrap checker=this.getCheckerAlarm(connector, moduleId, moduleIdentifier, fileId);
				if(checker!=null){
					// ��������� ������������� � ���, ��� ������ ���� ������������ 
					connector.getSession().beginTransaction();
					checker.setIdState(2);
					connector.getSession().update(checker);
					connector.getSession().getTransaction().commit();
					return IAlarm.returnOk;
				}else{
					return IAlarm.returnError;
				}
			}else{
				throw new Exception("module is not recognized: "+moduleIdentifier.getId());
			}
		}catch(Exception ex){
			logger.error("getAlarmCheckerById Exception: "+ex.getMessage());
			return IAlarm.returnError;
		}finally{
			try{
				connector.close();
			}catch(Exception ex){};
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
	public TransportChecker getAlarmCheckerById(ModuleIdentifier moduleIdentifier,
											String fileId) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// �������� ���������� ������������� ������ 
			int moduleId=this.getIdFromIdentifier(connector, moduleIdentifier);
			if(moduleId>0){
				// �������� ������, ������� �������� ������ �� Checker 
				ModuleAlarmCheckerWrap checker=this.getCheckerAlarm(connector, moduleId, moduleIdentifier, fileId);
				TransportChecker returnValue=new TransportChecker();
				@SuppressWarnings("unchecked")
				Checker<AlarmMessage> objectForSend=(Checker<AlarmMessage>)this.storageModuleChecker.read(checker.getIdStorage());
				// INFO ������.���������� checker-� ����������� �������������� ��� ������������� �� ����� ��������
				objectForSend.setIdFile(checker.getId());
				objectForSend.setDescription(checker.getDescription());
				// INFO ������. ��������� ���������� ������ Checker<AlarmMessage>
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
	public boolean moduleWasRestarted(ModuleIdentifier moduleIdentifier) {
		// INFO ������.������ ��� RESTARTED
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			int moduleId=this.getIdFromIdentifier(connector, moduleIdentifier);
			// �������� � ������� ������������ ������ �������� 
			Session session=connector.getSession();
			ModuleRestart moduleRestart=new ModuleRestart();
			moduleRestart.setIdModule(moduleId);
			session.beginTransaction();
			session.save(moduleRestart);
			session.getTransaction().commit();
			// ���������� ��� ������ �� ������, ������� ����� State=1 - ����� �������, ���������� � 0 - ����� ������ �� ������
			connector.getConnection().createStatement().executeUpdate("update module_task set module_task.id_state=0 where module_task.id_state=1 and module_task.id_module="+moduleId);
			connector.getConnection().commit();
			// INFO ������.���������� �������� � ������� RESTART
			monitorRestartFilter.notifyRestartEvent(connector, moduleRestart);
		}catch(Exception ex){
			logger.error("moduleWasRestarted Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
		return true;
	}


}
