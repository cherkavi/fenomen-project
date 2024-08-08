package fenomen.module.web_service.common;

import java.io.Serializable;
import java.util.Date;

/** ������� Alarm �� ������ */
public class ModuleAlarm implements Serializable{
	private final static long serialVersionUID=1L;
	/** ���������� ������ � ��������� ���� */
	private String content;
	/** ��������, ���������� �� ������ */
	private String value;
	/** ���� ��������� ������� */
	private Date eventDate;
	/** ���������� ������������� ������� � �������� ������ */
	private int idSensor;
	/** ���������� ����� ��������, ������� ��������� � ������/������� */
	private int registerAddress;
	
	
	/** ������� Alarm �� ������ */
	public ModuleAlarm(){
	}

	/** ������� Alarm �� ������ */
	public ModuleAlarm(String content){
		this.content=content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public void setIdSensor(int idSensor) {
		this.idSensor=idSensor;
	}

	public int getIdSensor() {
		return this.idSensor;
	}

	public int getRegisterAddress() {
		return registerAddress;
	}

	public void setRegisterAddress(int registerAddress) {
		this.registerAddress = registerAddress;
	}
	
	
}
