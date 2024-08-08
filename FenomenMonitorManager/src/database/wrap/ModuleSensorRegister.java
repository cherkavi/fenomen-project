package database.wrap;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@PersistenceCapable(detachable="false")
@Table(name="module_sensor_register")
public class ModuleSensorRegister {
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;

	@Column(name="id_sensor")
	private int idSensor;
	
	@Column(name="register_address")
	private int registerAddress;
	
	@Column(name="register_value")
	private int registerValue;
	
	@Column(name="description")
	private String description;

	@Column(name="register_value_date_write")
	private Date registerValueDateWrite;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getRegisterValue() {
		return registerValue;
	}

	public void setRegisterValue(int registerValue) {
		this.registerValue = registerValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getRegisterValueDateWrite() {
		return registerValueDateWrite;
	}

	public void setRegisterValueDateWrite(Date registerValueDateWrite) {
		this.registerValueDateWrite = registerValueDateWrite;
	}
	
}
