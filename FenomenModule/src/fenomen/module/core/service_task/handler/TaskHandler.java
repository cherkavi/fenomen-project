package fenomen.module.core.service_task.handler;

import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.cherkashin.vitaliy.modbus.core.schedule_read.Device;
import com.cherkashin.vitaliy.modbus.core.schedule_read.DeviceRegisterBlock;

import fenomen.module.core.IAlarmAware;
import fenomen.module.core.IInformationAware;
import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.IModuleSettingsListener;
import fenomen.module.core.ISensorContainerAware;
import fenomen.module.core.sensor.Sensor;
import fenomen.module.core.service_information.IModuleInformationListener;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.service.IAlarm;
import fenomen.module.web_service.service.IInformation;
import fenomen.server.controller.server.generator_alarm_checker.calc.Checker;

/** ����������� �����-���������� ��� ������, ������� ���� �������� �� ������� � ���� XML ������ */
public abstract class TaskHandler {
	protected SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	
	private XPath xpath=XPathFactory.newInstance().newXPath();
	
	protected XPath getXPath(){
		return xpath;
	}
	
	/** 
	 * @param document - XML ��������
	 * @param stringXpath XPath ���� 
	 * @return �������� �� ��������� ���� Element(Node) ��� �� ������ ���� �� ����������
	 * <li><b> true </b>- ���� ���������� </li>
	 * <li><b> false </b>- ���� �� ���������� </li>
	 */
	protected boolean isPathExitsts(Document document, String stringXpath){
		Object object=this.getNode(document, stringXpath);
		if(object==null){
			return false;
		}else{
			if(object instanceof String){
				if(((String)object).trim().equals("")){
					return false;
				}else{
					return true;
				}
			}else{
				return true;
			}
		}
	}
	
	/** ����������, �������� �� ������ ����� ������������ ���������, �� ��������� �������� ����� */
	public abstract boolean checkDocumentForProcess(Document document);
	
	/** ���������� ���������� �������� ( �������������� ������ {@link #checkDocumentForProcess(Document)}<br>
	 * <b>!!!����� ���� ����� ���������� true - ����� �������� � ThreadInformation ��� ThreadAlarm ������ � ����������� </b><br>
	 * @param moduleIdentifier ������������� ������� ������ 
	 * @param taskId - ���������� ������������� ������ 
	 * @param document - ��������, ������� �������� ������ �� �������
	 * @param informationListener - ������, ������� ��������� ������� ModuleInformation ��� �������� �� ������
	 * @param moduleSettingsAware - ������, ������� ����� � ������� ����� "������" ������ �������� ��� ������
	 * @param moduleSettingsListener - ������, ������� ������� � ��������� ��������� � ���������� ������
	 * @param sensorContainerAware - ������, ������� ����� ������������ ��� ������� � ���� ������ ���������� 
	 * @return
	 * <li><b>true</b> ������ ������� ���������� </li>   
	 * <li><b>false</b> ������ ��������� ������ </li>   
	 * */
	public abstract boolean processDocument(ModuleIdentifier moduleIdentifier,
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
			   int informationTimeError);
	
	/** �������� 
	 * @param document - �������� ( ������� �� ������� )
	 * @param xpath - ���� � ��������  
	 * @return null, ���� ��������� ������ ��������� ������ ��� ������
	 */
	protected Object getNode(Node document, String xpath){
		try{
			Object returnValue=this.getXPath().evaluate(xpath, document,XPathConstants.NODE);
			return returnValue;
		}catch(Exception ex){
			return null;
		}
	}
	
	/** �������� �����, ������ XML �������� ( ��� ��������� �������� ) */
	protected Document getEmptyXmlDocument(){
		javax.xml.parsers.DocumentBuilderFactory document_builder_factory=javax.xml.parsers.DocumentBuilderFactory.newInstance();
		document_builder_factory.setValidating(false);
		document_builder_factory.setIgnoringComments(true);
		try{
			javax.xml.parsers.DocumentBuilder document_builder=document_builder_factory.newDocumentBuilder();
			return document_builder.newDocument();
		}catch(Exception ex){
			return null;
		}
	}
	
	/**
	 * ������������� XML �������� � ������ ������ 
	 */
	protected String convertXmlDocumentToString(Document document){
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
			System.err.println("TaskHandler#getStringFromXmlDocument:"+ex.getMessage());
		}
		return (out==null)?"":out.toString();
	}

	/** �������� XML ������ � ������� �� ��������� ��������� ������ */
	protected String getTaskError(int taskId){
		try{
			Document document=this.getEmptyXmlDocument();
			Element task=this.getTaskElement(document);
			
			Element id=document.createElement("id");
			id.setTextContent(Integer.toString(taskId));
			task.appendChild(id);
			
			Element value=document.createElement("value");
			value.setTextContent("ERROR");
			task.appendChild(value);
			
			return this.convertXmlDocumentToString(document); 
		}catch(Exception ex){
			return null;
		}
	}

	/** �������� XML ������ � ������� � ������������� ��������� ������ */
	protected String getTaskOk(int taskId){
		try{
			Document document=this.getEmptyXmlDocument();
			Element task=this.getTaskElement(document);
			
			Element id=document.createElement("id");
			id.setTextContent(Integer.toString(taskId));
			task.appendChild(id);
			
			Element value=document.createElement("value");
			value.setTextContent("OK");
			task.appendChild(value);
			
			return this.convertXmlDocumentToString(document); 
		}catch(Exception ex){
			return null;
		}
	}

	/** �������� ������� Task, � �������� ����� ������������ ����������� Element */
	protected Element getTaskElement(Document document){
		Element task_response=document.createElement("task_response");
		document.appendChild(task_response);
		
		Element task=document.createElement("task");
		task_response.appendChild(task);
		return task;
	}
	
	/** �������� XPath ���� � XML �������� Task */
	protected String getXPathToTask(boolean isResponse){
		if(isResponse){
			return "//task_response/task";
		}else{
			return "//task_request/task";
		}
		
	}
	
	/** �������� ������� Sensor ��� XML ���������
	 * @param document - ��������, ������� ������� ������������ ��� ������������ XML
	 * @param sensor - ������/������, ������� ������� ������������ ��� �������� ������ 
	 * @return
	 */
	protected Element getSensorNode(Document document, Sensor sensor) {
		Element sensorNode = null;
		try {
			sensorNode = document.createElement("sensor");

			Element id_modbus = document.createElement("id_modbus");
			id_modbus.setTextContent(Integer.toString(sensor.getNumber()));
			sensorNode.appendChild(id_modbus);

			Element type = document.createElement("type");
			type.setTextContent(sensor.getType());
			sensorNode.appendChild(type);

			Element enabled = document.createElement("enabled");
			if (sensor.isEnabled()) {
				enabled.setTextContent("true");
			} else {
				enabled.setTextContent("false");
			}
			sensorNode.appendChild(enabled);

			Element registerList = document.createElement("register_list");
			sensorNode.appendChild(registerList);
			Device device = sensor.getDevice();
			for (int indexBlock = 0; indexBlock < device.getBlockCount(); indexBlock++) {
				DeviceRegisterBlock currentBlock = device.getBlock(indexBlock);
				for (int indexRegister = 0; indexRegister < currentBlock
						.getRegisterCount(); indexRegister++) {
					Element register = document.createElement("register");
					registerList.appendChild(register);

					Element number = document.createElement("number");
					number.setTextContent(Integer.toString(currentBlock
							.getAddressBegin()
							+ indexRegister));
					register.appendChild(number);

					Element value = document.createElement("value");
					value.setTextContent(Integer.toString(currentBlock
							.getRegister(indexRegister)));
					register.appendChild(value);

					Element date_write = document.createElement("date_write");
					date_write.setTextContent(this.sdf.format(currentBlock
							.getTimeOfLastOperation()));
					register.appendChild(date_write);
				}
			}
		} catch (Exception ex) {
			sensorNode = null;
		}
		return sensorNode;
	}

	/** �������� ������� Checker ��� XML ������������� 
	 * 
	 * 
	 * */
	/**
	 * @param alarmChecker - checker, ������� ����� ������� � ���� "�����" XML
	 * @param document - ��������, �� ��������� �������� ����� ��������� XML Element
	 * @param index - ������ Checker-a � ����� ����� (0..n-1) 
	 * @param modbusAddress - ���������� ����� Modbus � ���� 
	 * @return
	 */
	protected Element getChecker(Checker<?> alarmChecker, Document document, int index, int modbusAddress){
		try{
			Element checker=document.createElement("checker");
			
			Element id=document.createElement("id");
			id.setTextContent(Integer.toString(index));
			checker.appendChild(id);
			
			Element id_sensor=document.createElement("id_modbus");
			id_sensor.setTextContent(Integer.toString(modbusAddress));
			checker.appendChild(id_sensor);
			
			Element register=document.createElement("register");
			register.setTextContent(Integer.toString(alarmChecker.getRegisterAddress()));
			checker.appendChild(register);
			
			Element id_file=document.createElement("id_file");
			id_file.setTextContent(Integer.toString(alarmChecker.getIdFile()));
			checker.appendChild(id_file);
			
			Element description=document.createElement("description");
			description.setTextContent(alarmChecker.getDescription());
			checker.appendChild(description);
			
			return checker;
		}catch(Exception ex){
			return null;
		}
	}
	
}
