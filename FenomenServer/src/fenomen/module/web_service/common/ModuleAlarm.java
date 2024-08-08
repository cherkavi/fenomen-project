package fenomen.module.web_service.common;

import java.io.Serializable;
import java.util.Date;

/** событие Alarm от модуля */
public class ModuleAlarm implements Serializable{
	private final static long serialVersionUID=1L;
	/** содержимое модуля в текстовом виде */
	private String content;
	/** значение, полученное от модуля */
	private String value;
	/** дата выявления события */
	private Date eventDate;
	/** уникальный идентификатор сенсора в масштабе модуля */
	private int idSensor;
	/** уникальный адрес регистра, который находится в модуле/сенсоре */
	private int registerAddress;
	
	
	/** событие Alarm от модуля */
	public ModuleAlarm(){
	}

	/** событие Alarm от модуля */
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
