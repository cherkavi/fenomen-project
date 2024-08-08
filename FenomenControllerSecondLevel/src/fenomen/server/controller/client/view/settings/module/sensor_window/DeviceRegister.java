package fenomen.server.controller.client.view.settings.module.sensor_window;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/** представление одного регистра на устройстве {@link Device} */
public class DeviceRegister implements IsSerializable{
	/** уникальный идентификатор в базе */
	private int id;
	/** уникальный идентификатор сенсора/датчика в базе  */
	private int idSensor;
	/** адрес данного регистра на модуле*/
	private int registerAddress;
	/** значение данного регистра */
	private int registerValue;
	/** описание */
	private String description;
	/** время установки значения */
	private Date registerValueDateTime;
	
	/** представление одного регистра на устройстве {@link Device} */
	public DeviceRegister(){
	}

	/** уникальный идентификатор в базе */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/** уникальный идентификатор сенсора/датчика в базе  */
	public int getIdSensor() {
		return idSensor;
	}

	public void setIdSensor(int idSensor) {
		this.idSensor = idSensor;
	}

	/** адрес данного регистра на модуле*/
	public int getRegisterAddress() {
		return registerAddress;
	}

	public void setRegisterAddress(int registerAddress) {
		this.registerAddress = registerAddress;
	}

	/** значение данного регистра */
	public int getRegisterValue() {
		return registerValue;
	}

	public void setRegisterValue(int registerValue) {
		this.registerValue = registerValue;
	}

	/** описание */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/** время установки значения */
	public Date getRegisterValueDateTime() {
		return registerValueDateTime;
	}

	public void setRegisterValueDateTime(Date registerValueDateTime) {
		this.registerValueDateTime = registerValueDateTime;
	}
	
	
}
