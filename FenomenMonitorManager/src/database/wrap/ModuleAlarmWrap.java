package database.wrap;
import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.*;
/**
 * <table border=1>
 * 	<tr>
 * 		<th> DataBase </th>   <th> POJO</th> <th> Description </th> 
 * 	</tr>
 * 	<tr>
 * 		<td> id</td>   <td> id</td> <td> </td> 
 * 	</tr>
 * 	<tr>
 * 		<td> id_module</td>   <td> idModule</td> <td> уникальный идентификатор модуля </td> 
 * 	</tr>
 * 	<tr>
 * 		<td> id_storage</td>   <td> idStorage</td> <td> уникальный идентификатор данного Alarm в хранилище на модуле</td> 
 * 	</tr>
 * 	<tr>
 * 		<td> id_description</td>   <td>description</td> <td> описание</td> 
 * 	</tr>
 * 	<tr>
 * 		<td> time_write</td>   <td>time_write</td> <td> время записи значения </td> 
 * 	</tr>
 * 	<tr>
 * 		<td>id_sensor</td>   <td>idSensor</td> <td> уникальный номер датчика, по которому данное событие зарегестрировано </td> 
 * 	</tr>
 * 	<tr>
 * 		<td>sensor_register_address</td>   <td>sensorRegisterAddress</td> <td> адрес регистра на устройстве</td> 
 * 	</tr>
 * </table> 
 *
 */

@Entity
@PersistenceCapable(detachable="false")
@Table(name="module_alarm")
public class ModuleAlarmWrap {
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;
	@Column(name="id_module")
	private int idModule;
	@Column(name="id_storage",length=255)
	private String idStorage;
	@Column(name="id_description",length=1024)
	private String description;
	@Column(name="time_write")
	private Date timeWrite;
	@Column(name="id_sensor")
	private int idSensor;

	@Column(name="sensor_register_address")
	private int sensorRegisterAddress;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getTimeWrite() {
		return timeWrite;
	}
	public void setTimeWrite(Date timeWrite) {
		this.timeWrite = timeWrite;
	}
	public int getIdSensor() {
		return idSensor;
	}
	public void setIdSensor(int idSensor) {
		this.idSensor = idSensor;
	}
	public int getSensorRegisterAddress() {
		return sensorRegisterAddress;
	}
	public void setSensorRegisterAddress(int sensorRegisterAddress) {
		this.sensorRegisterAddress = sensorRegisterAddress;
	}
}
