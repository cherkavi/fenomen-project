package database.wrap;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <table border=1>
 * 	<tr>
 * 		<th> Database Field</th>  <th>POJO</th> <th> Description  </th>
 * 	</tr>
 * 	<tr>
 * 		<td>id</td>  <td> id </td> <td> уникальный идентификатор в базе данных </td>
 * 	</tr>
 * 	<tr>
 * 		<td>id_sensory_type</td>  <td> idSensorType </td> <td> ключ из таблицы sensor_type.id</td>
 * 	</tr>
 * 	<tr>
 * 		<td>address_register</td>  <td>addressRegister</td> <td>номер регистра на устройстве(сенсоре)</td>
 * 	</tr>
 * 	<tr>
 * 		<td>description</td>  <td>description</td> <td> описание данного регистра </td>
 * 	</tr>
 * </table>
 */
@Entity
@PersistenceCapable(detachable="false")
@Table(name="sensor_register_type")
public class SensorRegisterType {
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;
	@Column(name="id_sensor_type")
	private int idSensorType;
	@Column(name="address_register")
	private int addressRegister;
	@Column(name="description",length=255)
	private String description;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdSensorType() {
		return idSensorType;
	}
	public void setIdSensorType(int idSensorType) {
		this.idSensorType = idSensorType;
	}
	public int getAddressRegister() {
		return addressRegister;
	}
	public void setAddressRegister(int addressRegister) {
		this.addressRegister = addressRegister;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
