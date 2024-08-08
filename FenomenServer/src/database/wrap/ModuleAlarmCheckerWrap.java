package database.wrap;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.*;

/**
 *<table border=1>
 *	<tr>
 *		<td colspan=3> таблица для хранения Checker-ов для Alarm событий </td>
 *	</tr>
 *	<tr>
 *		<th>Table.Field</th> <th>POJO</th> <th>Description</th>
 *	</tr>
 *	<tr>
 *		<td>id</td>  <td>id</td>  <td></td>
 *	</tr>
 *	<tr>
 *		<td>id_module</td>  <td>idModule</td>  <td>уникальный идентификатор модуля </td>
 *	</tr>
 *	<tr>
 *		<td>id_storage</td>  <td>idStorage</td>  <td>идентификатор хранилища</td>
 *	</tr>
 *	<tr>
 *		<td>id_state</td>  <td>idState</td>  <td>идентификатор состояния ( 0 - новое задание )     ( 1 - забрано модулем )   ( 2 - успешно внедрено в модуль )  ( -1 - есть на модуле, нет на сервере )</td>
 *	</tr>
 *	<tr>
 *		<td>time_write</td>  <td>timeWrite</td>  <td>время записи</td>
 *	</tr>
 *	<tr>
 *		<td>description</td>  <td>description</td>  <td>описание </td>
 *	</tr>
 *	<tr>
 *		<td>sensor_modbus_address</td>  <td>sensorModbusAddress</td>  <td> уникальный адрес датчика-сенсора на удаленном модуле</td>
 *	</tr>
 *	<tr>
 *		<td>sensor_register_address</td>  <td>sensorRegisterAddress</td>  <td>адрес регистра на датчике-сенсоре, если таковой имеет место быть </td>
 *	</tr>
 *	<tr>
 *		<td>sensor_modbus_id_on_device</td>  <td>sensorModbusIdOnDevice</td>  <td>порядковый номер Checker-а на модуле (появляется после полного анализа пакета "sensor_get")</td>
 *	</tr>
 *
 *</table> 
 *
 */
@Entity
@PersistenceCapable(detachable="false")
@Table(name="module_alarm_checker")
public class ModuleAlarmCheckerWrap {
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
	
	@Override
	public boolean equals(Object object){
		boolean returnValue=false;
		if(object instanceof ModuleAlarmCheckerWrap){
			ModuleAlarmCheckerWrap destination=(ModuleAlarmCheckerWrap)object;
			return (  (destination.getId()==this.getId())
			        &&(destination.getIdModule()==this.getIdModule())
			        &&(destination.getSensorRegisterAddress()==this.getSensorRegisterAddress())
			        );
		}
		return returnValue;
	}
	public Integer getSensorRegisterAddress() {
		return sensorRegisterAddress;
	}
	public void setSensorRegisterAddress(Integer sensorRegisterAddress) {
		this.sensorRegisterAddress = sensorRegisterAddress;
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
}
