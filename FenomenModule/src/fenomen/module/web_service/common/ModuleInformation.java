package fenomen.module.web_service.common;

import java.io.Serializable;
import java.util.Date;

/** событие Information */
public class ModuleInformation implements Serializable{
	private final static long serialVersionUID=1L;

	/** значение, полученное от модул€ */
	private String value;
	/** дата вы€влени€ событи€ */
	private Date eventDate;
	
	private String content;

	/** уникальный идентификатор сенсора в масштабе модул€ */
	private int idSensor;

	/** уникальный адрес регистра, который находитс€ в модуле/сенсоре */
	private int registerAddress;
	
	
	/** событие Information */
	public ModuleInformation(){
	}
	
	public ModuleInformation(String content){
		this.content=content;
	}

	/** получить содержимое объекта */
	public String getContent() {
		return content;
	}

	/** получить содержимое объекта */
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

	public int getIdSensor() {
		return idSensor;
	}

	public void setIdSensor(int idSensor) {
		this.idSensor = idSensor;
	}

	public int getRegisterAddress() {
		return registerAddress;
	}

	public void setRegisterAddress(int registerAddress) {
		this.registerAddress = registerAddress;
	}
	
	
}
