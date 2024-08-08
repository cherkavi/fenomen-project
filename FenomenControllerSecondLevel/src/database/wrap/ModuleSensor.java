package database.wrap;

import java.util.ArrayList;
import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.*;

/**
 * обертка для таблицы module_sensor
 * <table border=1>
 * 	<tr>
 * 		<th>Database</th> <th> POJO </th> <th>Description</th> 
 * 	</tr>
 *  <tr>
 *  	<td> id</td> <td> id </td> <td> уникальный Sequence в базе </td> 
 *  </tr>
 *  <tr>
 *  	<td> id_module</td> <td> idModule </td> <td> уникальный код модуля по базе (module.id)</td> 
 *  </tr>
 *  <tr>
 *  	<td> id_modbus</td> <td> idModbus </td> <td> идентификатор в модуле по протоколу Modbus</td> 
 *  </tr>
 *  <tr>
 *  	<td> id_sensor_type</td> <td> idSensorType </td> <td> тип сенсора/датчика по базе </td> 
 *  </tr>
 *  <tr>
 *  	<td> sensor_value</td> <td> idSensorValue </td> <td> значение сенсора/датчика по базе </td> 
 *  </tr>
 *  <tr>
 *  	<td> is_enabled</td> <td> isEnabled </td> <td> является ли данный Сенсор-датчик Enabled/Disabled <li> 0 - Disabled</li> <li>1 - Enabled</li> </td> 
 *  </tr>
 *  <tr>
 *  	<td> sensor_value_timestamp</td> <td> sensorValueTimestamp </td> <td> время считывания значения с датчика </td> 
 *  </tr>
 *  <tr>
 *  	<td> old</td> <td> old </td> <td> является ли значение устаревшим,т.е. была ошибка считывания данных во время опроса датчиков <li> 0 - Fresh Value </li> <li> 1 - Old value </li> </td> 
 *  </tr>
 *  <tr>
 *  	<td> time_write</td> <td> timeWrite</td> <td> время вставки/создания строки(record) в таблицу </td> 
 *  </tr>
 * </table>
 */
@Entity
@PersistenceCapable(detachable="false")
@Table(name="module_sensor")
public class ModuleSensor {
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;
	
	@Column(name="id_module")
	private int idModule;
	
	@Column(name="id_modbus",length=45)
	private String idModbus;
	
	@Column(name="id_sensor_type")
	private int idSensorType;
	
	@Column(name="is_enabled")
	private int isEnabled;
	
	@Column(name="time_write")
	private Date timeWrite;
	
	@Transient
	private ArrayList<ModuleSensorRegister> listOfRegister=new ArrayList<ModuleSensorRegister>();
	
	/** получить список всех регистров, которые зарегестрированы по данному сенсору/модулю */
	@Transient
	public ArrayList<ModuleSensorRegister> getListOfRegister(){
		return this.listOfRegister;
	}
	
	
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

	public String getIdModbus() {
		return idModbus;
	}

	public void setIdModbus(String idModbus) {
		this.idModbus = idModbus;
	}

	public int getIdSensorType() {
		return idSensorType;
	}

	public void setIdSensorType(int idSensorType) {
		this.idSensorType = idSensorType;
	}

	public int getIsEnabled() {
		return isEnabled;
	}

	/**
	 * @param isEnabled
	 * <table border=1>
	 * 	<tr>
	 * 		<th> Value </th> <th> Description</th> 	
	 * </tr>
	 * 	<tr>
	 * 		<td> <b>0</b></td> <td> Disabled</td> 	
	 * </tr>
	 * 	<tr>
	 * 		<td> <b>1</b></td> <td> Enabled</td> 	
	 * </tr>
	 * </table>
	 */
	public void setIsEnabled(int isEnabled) {
		this.isEnabled = isEnabled;
	}

	public Date getTimeWrite() {
		return timeWrite;
	}

	public void setTimeWrite(Date timeWrite) {
		this.timeWrite = timeWrite;
	}

	@Override
	public boolean equals(Object object){
		boolean returnValue=false;
		if(object instanceof ModuleSensor){
			ModuleSensor comparableObject=(ModuleSensor)object;
			returnValue=  (comparableObject.getIdModbus().equals(this.getIdModbus()))
			            &&(comparableObject.getIdModule()==this.getIdModule())
			            &&(comparableObject.getIdSensorType()==this.getIdSensorType());
		}else{
			// object is not ModuleSensor 
			returnValue=false;
		}
		return returnValue;
	}
}
