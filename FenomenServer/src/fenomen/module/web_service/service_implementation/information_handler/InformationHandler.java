package fenomen.module.web_service.service_implementation.information_handler;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.Writer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.Module;
import database.wrap.ModuleAlarmCheckerWrap;
import database.wrap.ModuleInformationCheckerWrap;
import database.wrap.ModuleSensor;
import database.wrap.ModuleSensorRegister;
import database.wrap.ModuleSettings;
import database.wrap.ModuleSettingsParameter;
import database.wrap.ModuleSettingsSection;
import database.wrap.SensorType;

import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;

/** ����� �� ��������� ���������� �� ������ �������������� ��������� */
public abstract class InformationHandler {
	private XPath xpath=XPathFactory.newInstance().newXPath();
	protected Logger logger=Logger.getLogger(this.getClass());
	/** ���������� ������������� ��� �������� �� ������� module_settings_section ({@link ModuleSettingSection})*/
	protected final String idHeartBeat="HeartBeat";
	protected final String idInformation="Information";
	protected final String idAlarm="Alarm";
	protected final String idSensor="Sensor";
	private final SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	
	/** �������� XML ������ �� ��������� ���������� ������������� */
	protected Document getXmlFromString(String value){
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
	
	/** �������� ������������� � ������� ������� XPath ( � ����� �������� ������ )*/
	protected XPath getXPath(){
		return this.xpath;
	}
	
	/** �������� 
	 * @param document - �������� ( ������� �� ������� )
	 * @param xpath - ���� � ��������  
	 * @return null, ���� ��������� ������ ��������� ������ ��� ������
	 */
	protected Object getNode(Node document, String xpath){
		try{
			return this.getXPath().evaluate(xpath, document,XPathConstants.NODE);
		}catch(Exception ex){
			return null;
		}
	}

	/** �������� ��������� �������� Element, ���� � �������� ��������� xpath
	 * @param document - �������, ������������ �������� ����� ����������� �����
	 * @param xpath - ���� XPath
	 * @return ��������� �������� �������� ��� �� null, ���� ������� �� ������  
	 */
	protected String getStringFromElement(Node node, String xpath){
		String returnValue=null;
		try{
			returnValue=((Element)this.getNode(node, xpath)).getTextContent().trim();
		}catch(Exception ex){
			logger.warn("getStringFromElement Exception: "+ex.getMessage());
		}
		return returnValue;
	}
	
	/** ���������� ���������� {@link ModuleInformation} �� ���������� ������ 
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @param moduleInformation - �������������� ������, ���������� �� ���������� ������ 
	 */
	public void processModuleInformation(ModuleIdentifier moduleIdentifier, 	
										 ModuleInformation moduleInformation){
		// ���� ������ ����� ���������� ������� ��������
		if(isValid(moduleInformation)==true){
			// ���������� ������� �������� ���������� �� ���������� ������  
			this.process(moduleIdentifier, moduleInformation);
		}
	}
	
	/** ��������� ������ �� �������������� � �������� ������� ��� ��������� 
	 * @param moduleInformation - ������ ��� ��������� {@link ModuleInformation} 
	 * @return
	 * <li><b> true </b> - ������ ��� ��������� ������ �������� </li>
	 * <li><b> false </b> - �� ������������ ���������� ������  </li>
	 */
	protected abstract boolean isValid(ModuleInformation moduleInformation);
	
	/** ���������� ���������� ������
	 * @param moduleIdentifier - ���������� ������������� ������  
	 * @param moduleInformation - ������, ���������� ��������� ������� 
	 */
	protected abstract void process(ModuleIdentifier moduleIdentifier, ModuleInformation moduleInformation);
	
	
	/** �������� ��� ������ �� ����� module_settings_section 
	 * @param session - ������
	 * @param sectionName - ��� ������
	 * @return - ��� ������ ������ � ������� module_settings_section 
	 */
	protected Integer getSectionKod(Session session, String sectionName ){
		Integer returnValue=null;
		try{
			ModuleSettingsSection section=(ModuleSettingsSection)session.createCriteria(ModuleSettingsSection.class).add(Restrictions.eq("name", sectionName)).uniqueResult();
			returnValue=section.getId();
		}catch(Exception ex){
			logger.error(" getSectionKod SectionName: "+sectionName+"   Exception: "+ex.getMessage());
		}
		return returnValue;
	}
	
	/** �������� ��� ��������� �� ������ � ����� ��������� �� ������� module_settings_parameter 
	 * @param session - ������ Hibernate
	 * @param sectionKod - ���������� ��� ����� 
	 * @param parameterName - ���������� ��� ��������� 
	 * @return
	 */
	protected Integer getParameterKod(Session session, Integer sectionKod, String parameterName){
		Integer returnValue=null;
		try{
			ModuleSettingsParameter parameterTimeWait=(ModuleSettingsParameter)session.createCriteria(ModuleSettingsParameter.class)
															.add(Restrictions.eq("idSection", sectionKod))
															.add(Restrictions.eq("parameterName",parameterName)).uniqueResult();
			returnValue=parameterTimeWait.getId();
		}catch(Exception ex){
			logger.error(" getSectionKod SectionKod: "+sectionKod+" ParameterName: "+parameterName+"   Exception: "+ex.getMessage());
		}
		return returnValue;
	}
	
	/** �������� �������� � ���� ������, ��� �������� �� ������, �� ���������  ��������� ������:
	 * @param session - ���������� ����� ������ 
	 * @param idModule - ���������� ����� ������ 
	 * @param idSection - ���������� ����� ������ 
	 * @param idParameter - ���������� ����� ��������� � ������� ������ 
	 * @param value - ��������, ������� ������ ���� ��������
	 * @param timeWrite - ����� ������ ������� ��������� � ����  
	 * @return 
	 * <li> <b>true</b> - ������ ������ ������� </li>
	 * <li> <b>false</b> - ������ ������ � ���� ������ </li>
	 */
	@SuppressWarnings("unchecked")
	protected boolean saveParameter(Session session, Integer idModule, Integer idSection, Integer idParameter, String value, Date timeWrite){
		boolean returnValue=false;
		try{
			// ����� ������ �� ����������� ����� - idModule, idSection, idParameter
			List<ModuleSettings> listOfSettings=(List<ModuleSettings>)session.createCriteria(ModuleSettings.class)
																 .add(Restrictions.eq("idModule", idModule))
																 .add(Restrictions.eq("idSection", idModule))
																 .add(Restrictions.eq("idParameter", idModule))
																 .addOrder(Order.desc("id")).list();
			session.beginTransaction();
			if(listOfSettings.size()>0){
				// ���� ������ - ������������ 
				ModuleSettings moduleSettings =listOfSettings.get(0);
				moduleSettings.setSettingsValue(value);
				moduleSettings.setTimeWrite(new java.util.Date());
				moduleSettings.setModuleRecieve(1);
				session.update(moduleSettings);
			}else{
				// ��� ������ � �������
				ModuleSettings moduleSettings=new ModuleSettings();
				moduleSettings.setIdModule(idModule);
				moduleSettings.setIdSection(idSection);
				moduleSettings.setIdParameter(idParameter);
				moduleSettings.setModuleRecieve(1);
				moduleSettings.setSettingsValue(value);
				moduleSettings.setTimeWrite(timeWrite);
				session.save(moduleSettings);
			}
			session.getTransaction().commit();
			returnValue=true;
		}catch(Exception ex){
			logger.error("saveParameter IdModule:"+idModule+" IdSection:"+idSection+" IdParameter:"+idParameter+" Value:"+value+"  Exception: "+ex.getMessage());
			returnValue=false;
		}
		return returnValue;
	}
	
	/** �������� ���������� id ������ �� �������������� 
	 * @param connector -  ���������� � ����� ������ 
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @return 
	 * <li> <b> value&lt0 </b>  �� ������� ���������������� ������ </li>
	 * <li> <b>value&gt0</b> ���� ������ ������� ��������������� </li>
	 */
	protected int getModuleIdFromIdentifier(Session session, ModuleIdentifier moduleIdentifier){
		int returnValue=(-1);
		try{
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
	
	@SuppressWarnings("unused")
	private String getStringFromXmlDocument(Node document){
		Writer out=null;
		try{
			javax.xml.transform.TransformerFactory transformer_factory = javax.xml.transform.TransformerFactory.newInstance();  
			javax.xml.transform.Transformer transformer = transformer_factory.newTransformer();  
			javax.xml.transform.dom.DOMSource dom_source = new javax.xml.transform.dom.DOMSource(document); // Pass in your document object here  
			out=new StringWriter();
			//string_writer = new Packages.java.io.StringWriter();  
			javax.xml.transform.stream.StreamResult stream_result = new javax.xml.transform.stream.StreamResult(out);  
			transformer.transform(dom_source, stream_result);  
		}catch(Exception ex){
			logger.error("getStringFromXmlDocument:"+ex.getMessage());
		}
		return (out==null)?"":out.toString();
	}
	
	/** �������� ������ ��������� �� ������� ������  */
	private ArrayList<ModuleSensorRegister> getRegisterListFromElement(Element element){
		ArrayList<ModuleSensorRegister> returnValue=new ArrayList<ModuleSensorRegister>();
		try{
			NodeList elementList=(NodeList)element;
			for(int counter=0;counter<elementList.getLength();counter++){
				if(elementList instanceof Element){
					Element currentElement=(Element)elementList.item(counter);
					if(currentElement.getNodeName().trim().equalsIgnoreCase("register")){
						ModuleSensorRegister register=new ModuleSensorRegister();
						int registerNumber=0;
						try{
							registerNumber=Integer.parseInt(this.getStringFromElement(currentElement, "number"));
						}catch(Exception ex){
							logger.error("getRegisterListFromElement getRegisterNumber Exception:"+ex.getMessage());
						}
						int value=0;
						try{
							value=Integer.parseInt(this.getStringFromElement(currentElement, "value"));
						}catch(Exception ex){
							logger.error("getRegisterListFromElement getRegisterValue Exception:"+ex.getMessage());
						}
						Date dateWrite=null;
						try{
							dateWrite=sdf.parse(this.getStringFromElement(currentElement, "date_write"));
						}catch(Exception ex){
							logger.error("getRegisterListFromElement getRegisterValue Exception:"+ex.getMessage());
						}
						register.setRegisterAddress(registerNumber);
						register.setRegisterValue(value);
						register.setRegisterValueDateWrite(dateWrite);
						returnValue.add(register);
					}else{
						// Element is not <register>
					}
				}else{
					// Node is not Element
				}
			}
			
		}catch(Exception ex){
			logger.error("get list of Register by Module Exception: "+ex.getMessage());
		};
		return returnValue;
	}
	
	/** �������� ������ {@link ModuleSensor} �� XML Node 
	 * @param idModule - ���������� ����� ������ �� ����
	 * @param session - ���������� ���������� � ����� ������
	 * @param element - ������� XML ������� �������� ��� ������ �� �������/������� 
	 * @return null, ���� ��������� ������ ���������� (id, id_modbus, enabled, value, time_write, old
	 */
	protected ModuleSensor getModuleSensorFromElement(Integer idModule, Session session,  Element element){
		ModuleSensor returnValue=null;
		try{
			// String elementTextValue=getStringFromXmlDocument(element);
			// System.out.println("ElementTextValue: "+elementTextValue);
			String id_modbus=this.getStringFromElement(element, "id_modbus");
			String type=this.getStringFromElement(element,"type");
			String enabled=this.getStringFromElement(element,"enabled");
			
			ArrayList<ModuleSensorRegister> listOfRegister=this.getRegisterListFromElement((Element)this.getNode(element, "register_list"));
			
			Integer sensorType=this.getIdSensorType(session, type);
			if(anyIsNull(id_modbus, type, sensorType)){
				logger.warn("getModuleSensorFromElement is not recognized:  id_modbus:"+id_modbus+"  type:"+type+"  sensor_type:"+type);
			}else{
				returnValue=new ModuleSensor();
				returnValue.setIdModbus(id_modbus);
				returnValue.setIdModule(idModule);
				returnValue.setIdSensorType(sensorType);
				returnValue.setIsEnabled((enabled.trim().equalsIgnoreCase("true"))?1:0);
				for(int counter=0;counter<listOfRegister.size();counter++){
					listOfRegister.get(counter).setIdSensor(idModule);
					returnValue.getListOfRegister().add(listOfRegister.get(counter));
				}
			}
		}catch(Exception ex){
			logger.error("getModuleSensorFromElement Exception: "+ex.getMessage());
		}
		return returnValue;
	}
	
	/** �������� ���������� ������������� ���� ������� �� ��� ����� 
	 * @param session - ������ � ����� ������ 
	 * @param type - ���������� ������������� 
	 * @return - null, ���� �� ������, id 
	 */
	protected Integer getIdSensorType(Session session, String type){
		Integer returnValue=null;
		try{
			SensorType sensorType=(SensorType)session.createCriteria(SensorType.class).add(Restrictions.eq("name",type.trim())).uniqueResult();
			returnValue=sensorType.getId();
		}catch(Exception ex){
			logger.error("getIdSensorType Exception: "+ex.getMessage());
		}
		return returnValue;
	}
	
	/** ��������� ������ �������� �� ������� � ��� null ������ */
	private boolean anyIsNull(Object ... listOfObject){
		boolean returnValue=false;
		if(listOfObject!=null){
			for(int counter=0;counter<listOfObject.length;counter++){
				returnValue=returnValue||(listOfObject[counter]==null);
				if(returnValue==true)break;
			}
		}else{
			returnValue=false;
		}
		return returnValue;
	}
	
	/** �������� ��� �������� �� ������ */
	private void writeAllRegisterByModule(Session session, 
										  ModuleSensor moduleSensor, 
										  ArrayList<ModuleSensorRegister> listOfRegister){
		for(int counter=0;counter<listOfRegister.size();counter++){
			listOfRegister.get(counter).setIdSensor(moduleSensor.getId());
			session.save(listOfRegister.get(counter));
		}
	}
	
	/** �������� ������� � ������� �� ���������� ������ � ���� ������ � �� �����������, ����������� �� ModuleSensor 
	 * @param session - ������
	 * @param idModule - id ������ � ���� 
	 * @param moduleSensor - ����������, ���������� �� ���������� ������ 
	 * @throws Exception
	 */
	protected void updateModuleSensorIntoDatabase(Session session,
												  Integer idModule, 
												  ModuleSensor moduleSensor) throws Exception{
		// ���������, ���� �� �������� ������ � ���� ������
		ModuleSensor databaseModuleSensor=this.getDatabaseModuleSensor(session, idModule, moduleSensor);
		if(databaseModuleSensor!=null){
			// �������� ���������
			logger.debug("�������� ������ � �������: ");
			databaseModuleSensor.setIsEnabled(moduleSensor.getIsEnabled());
			databaseModuleSensor.setTimeWrite(new Date());
			// update 
			session.beginTransaction();
			session.update(databaseModuleSensor);
			// ������� ��� �������� �� ������� ������
			removeAllRegisterByModuleSensor(session, databaseModuleSensor);
			// �������� ��� �������� �� ������� ������ 
			writeAllRegisterByModule(session, databaseModuleSensor, moduleSensor.getListOfRegister());
			session.getTransaction().commit();
		}else{
			// �������� ����� 
			logger.debug("�������� ������ � �������: ");
			session.beginTransaction();
			session.save(moduleSensor);
			// ������� ��� �������� �� ������� ������
			removeAllRegisterByModuleSensor(session, databaseModuleSensor);
			// �������� ��� �������� �� ������� ������ 
			writeAllRegisterByModule(session, moduleSensor, moduleSensor.getListOfRegister());
			session.getTransaction().commit();
		}
	}
	
	/** ������� ��� �������� �� ���������� ������-��������� */
	private boolean removeAllRegisterByModuleSensor(Session session, ModuleSensor moduleSensor) {
		boolean returnValue=false;
		try{
			session.createSQLQuery("delete from module_sensor_register where id_sensor="+moduleSensor.getId()).executeUpdate();
			returnValue=true;
		}catch(Exception ex){
			returnValue=false;
			logger.error("execute Query Exception: "+ex.getMessage());
		}
		return returnValue;
	}

	/** �������� {@link ModuleSensor} �� ���� ������, �� ��������� ������, ���������� � ���������, �� ���� �������� ��������������� ������ � ����  
	 * @param session - ���������� � ����� ������
	 * @param idModule - ���������� ������������� ������, ������� ������� ������ ������ 
	 * @param moduleSensor - ������ �� ������ 
	 * @return <li>null - ������ � ���� �� ������ </li><li>not null - ������ �� ����</li>
	 */
	protected ModuleSensor getDatabaseModuleSensor(Session session, Integer idModule, ModuleSensor moduleSensor){
		ModuleSensor returnValue=null;
		try{
			returnValue=(ModuleSensor)session.createCriteria(ModuleSensor.class)
													.add(Restrictions.eq("idModule", idModule))
													.add(Restrictions.eq("idModbus", moduleSensor.getIdModbus()))
													.add(Restrictions.eq("idSensorType", moduleSensor.getIdSensorType()))
													.list().get(0);
		}catch(Exception ex){
			logger.error("getDatabaseModuleSensor Exception: "+ex.getMessage());
		}
		return returnValue;
	}
	
	/**
	 * �������� ������ ��������� �� ������� �������
	 * @param session - ������ � ����� ������
	 * @param databaseSensorId - ���������� ������������� ������� � ���� ������
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<ModuleSensorRegister> getDatabaseModuleRegister(Session session, Integer databaseSensorId){
		List<ModuleSensorRegister> returnValue=null;
		try{
			
			returnValue=(List<ModuleSensorRegister>)session.createCriteria(ModuleSensorRegister.class)
													.add(Restrictions.eq("idSensor", databaseSensorId))
													.list();
		}catch(Exception ex){
			logger.error("getDatabaseModuleRegister Exception: "+ex.getMessage());
		}
		return returnValue;
		
	}
	
	/** �������� XPath ���� � �������� Task
	 * @param isRequest - ������   
	 * @return 
	 * <li> <b>true</b> �������� ���� � Request ������ (�� ������� � ������)</li>
	 * <li> <b>false</b></li>
	 */
	protected String getXPathToTask(boolean isRequest){
		if(isRequest){
			return "//task_request/task";
		}else{
			return "//task_response/task";
		}
	}

	/** �� ��������� ���������� �������� �������� {@link ModuleAlarmCheckerWrap}, � �������� ������ � ����   
	 * @param moduleId - ���������� ������������� ������ 
	 * @param element - XML �������, �� �������� ����� �������� ��������� 
	 * @return ������� �� ���� ������, ������� �������� �� ��������� /alarm_checker/list/checker/id_file - ������� ������ ������� ModuleAlarmCheckerWrap.id
	 */
	protected ModuleAlarmCheckerWrap getAlarmCheckerFromElement(int moduleId, Element element){
		ConnectWrap connector=StaticConnector.getConnectWrap();
		ModuleAlarmCheckerWrap returnValue=null;
		try{
			int id=Integer.parseInt( ((Element)this.getNode(element, "id")).getTextContent().trim());
			int id_modbus=0;
			try{
				id_modbus=Integer.parseInt( ((Element)this.getNode(element, "id_modbus")).getTextContent().trim());
			}catch(Exception innerEx){};
			int register=0;
			try{
				register=Integer.parseInt( ((Element)this.getNode(element, "register")).getTextContent().trim());
			}catch(Exception innerEx){};
			int id_file=Integer.parseInt( ((Element)this.getNode(element, "id_file")).getTextContent().trim());
			String description=((Element)this.getNode(element, "description")).getTextContent();
			
			Session session=connector.getSession();
			returnValue=(ModuleAlarmCheckerWrap)session.createCriteria(ModuleAlarmCheckerWrap.class)
														.add(Restrictions.eq("id", new Integer(id_file))) // ���������� ������������� ����� �� ������
														.add(Restrictions.eq("idModule",new Integer(moduleId))) // �������� �� �������������� � ������, ������� ������� ������ 
														.uniqueResult();
			if(returnValue!=null){
				returnValue.setDescription(description);
				returnValue.setSensorModbusAddress(id_modbus);
				returnValue.setSensorModbusIdOnDevice(id);
				returnValue.setSensorRegisterAddress(register);
				session.beginTransaction();
				session.update(returnValue);
				session.getTransaction().commit();
			}
		}catch(Exception ex){
			logger.error("getAlarmCheckerFromElement Exception: "+ex.getMessage());
			returnValue=null;
		}finally{
			connector.close();
		}
		return returnValue;
	}

	/** �� ��������� ���������� �������� �������� {@link ModuleInformationCheckerWrap}  
	 * @param moduleId - ���������� ������������� ������ 
	 * @param element - XML �������, �� �������� ����� �������� ��������� 
	 * @return
	 */
	protected ModuleInformationCheckerWrap getInformationCheckerFromElement(int moduleId, Element element){
		ConnectWrap connector=StaticConnector.getConnectWrap();
		ModuleInformationCheckerWrap returnValue=null;
		try{
			int id=Integer.parseInt( ((Element)this.getNode(element, "id")).getTextContent().trim());
			int id_modbus=Integer.parseInt( ((Element)this.getNode(element, "id_modbus")).getTextContent().trim());
			int register=Integer.parseInt( ((Element)this.getNode(element, "register")).getTextContent().trim());
			int id_file=Integer.parseInt( ((Element)this.getNode(element, "id_file")).getTextContent().trim());
			String description=((Element)this.getNode(element, "description")).getTextContent();
			
			Session session=connector.getSession();
			returnValue=(ModuleInformationCheckerWrap)session.createCriteria(ModuleInformationCheckerWrap.class)
														.add(Restrictions.eq("id", new Integer(id_file))) // ���������� ������������� ����� �� ������
														.add(Restrictions.eq("idModule",new Integer(moduleId))) // �������� �� �������������� � ������, ������� ������� ������ 
														.uniqueResult();
			if(returnValue!=null){
				returnValue.setDescription(description);
				returnValue.setSensorModbusAddress(id_modbus);
				returnValue.setSensorModbusIdOnDevice(id);
				returnValue.setSensorRegisterAddress(register);
				session.beginTransaction();
				session.update(returnValue);
				session.getTransaction().commit();
			}
		}catch(Exception ex){
			logger.error("getInformationCheckerFromElement Exception: "+ex.getMessage());
			returnValue=null;
		}finally{
			connector.close();
		}
		return returnValue;
	}
	
}
