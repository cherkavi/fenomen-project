package database.wrap;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.*;

@Entity
@PersistenceCapable(detachable="false")
@Table(name="module_information_checker")
public class ModuleInformationCheckerWrap {
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;
	@Column(name="id_module")
	private int idModule;
	@Column(name="id_storage")
	private String idStorage;
	@Column(name="id_state")
	private int idState;
	@Column(name="time_write")
	private Date timeWrite;
	@Column(name="description")
	private String description;

	@Column(name="sensor_register_address")
	private Integer sensorRegisterAddress;
	@Column(name="sensor_modbus_address")
	private Integer sensorModbusAddress;
	@Column(name="sensor_modbus_id_on_device")
	private Integer sensorModbusIdOnDevice;

	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdModule() {
		return idModule;
	}
	public void setIdModule(int idModule) {
		this.idModule = idModule;
	}
	public String getIdStorage() {
		return idStorage;
	}
	public void setIdStorage(String idStorage) {
		this.idStorage = idStorage;
	}
	public int getIdState() {
		return idState;
	}
	public void setIdState(int idState) {
		this.idState = idState;
	}
	public Date getTimeWrite() {
		return timeWrite;
	}
	public void setTimeWrite(Date timeWrite) {
		this.timeWrite = timeWrite;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getSensorRegisterAddress() {
		return sensorRegisterAddress;
	}

	@Override
	public boolean equals(Object object){
		boolean returnValue=false;
		if(object instanceof ModuleInformationCheckerWrap){
			ModuleInformationCheckerWrap destination=(ModuleInformationCheckerWrap)object;
			return (  (destination.getId()==this.getId())
			        &&(destination.getIdModule()==this.getIdModule())
			        &&(destination.getSensorRegisterAddress()==this.getSensorRegisterAddress())
			        );
		}
		return returnValue;
	}
	public Integer getSensorModbusAddress() {
		return sensorModbusAddress;
	}
	public void setSensorModbusAddress(Integer sensorModbusAddress) {
		this.sensorModbusAddress = sensorModbusAddress;
	}
	public Integer getSensorModbusIdOnDevice() {
		return sensorModbusIdOnDevice;
	}
	public void setSensorModbusIdOnDevice(Integer sensorModbusIdOnDevice) {
		this.sensorModbusIdOnDevice = sensorModbusIdOnDevice;
	}
	public void setSensorRegisterAddress(Integer sensorRegisterAddress) {
		this.sensorRegisterAddress = sensorRegisterAddress;
	}

	
}
