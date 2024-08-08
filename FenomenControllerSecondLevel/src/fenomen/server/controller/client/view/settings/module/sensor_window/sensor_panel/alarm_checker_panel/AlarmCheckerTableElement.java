package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/** элемент из таблицы-списка, который отображает {@link RegisterAlarmCheckerPanel} для отдельно взятого датчика по отедельно взятому модулю 
 * <table border=1>
 * 	<tr>
 * 		<th>Поле объекта</th> <th>Описание</th>
 * 	</tr>
 * 	<tr>
 * 		<td>id</td>	<td> уникальный идентификатор</td> 
 * 	</tr>
 * 	<tr>
 * 		<td>idModule</td>	<td> номер модуля </td> 
 * 	</tr>
 * 	<tr>
 * 		<td>modbusAddress</td>	<td> уникальный адрес устройства (которому принадлежит данный Checker )</td> 
 * 	</tr>
 * 	<tr>
 * 		<td>regiserAddress</td>	<td> номер регистра на устройстве ( может быть неопределенным )</td> 
 * 	</tr>
 * 	<tr>
 * 		<td>idOnDevice</td>	<td> порядковый номер на устройстве</td> 
 * 	</tr>
 * 	<tr>
 * 		<td>description</td>	<td> описание </td> 
 * 	</tr>
 * 	<tr>
 * 		<td>idState</td>	<td> <li>0 - новое задание </li><li> 1 - забрано модулем </li><li> 2 - успешно внедрено в модуль </li><li> -1 - есть на модуле, нет на сервере </li> </td> 
 * 	</tr>
 * 	<tr>
 * 		<td>timeWrite</td>	<td> время обновления записи в базе данных </td> 
 * 	</tr>
 * </table>
 * 
 * */
public class AlarmCheckerTableElement implements IsSerializable{
	/** уникальный идентификатор module_alarm_checker.id*/
	private int id;
	/** уникальный идентификатор модуля module.id */
	private int idModule;
	/** номер устройства, к которому прикреплен данный Checker */
	private int modbusAddress;
	/** номер регистра, к которому прикреплен данный адрес  */
	private Integer registerAddress;
	/** порядковый номер данного checker-a на удаленном модуле */
	private Integer idOnDevice;
	/** описание, которое следует отображать для сенсора */
	private String description;
	/** состояние данного checker-а
	 * идентификатор состояния <li>0 - новое задание </li><li> 1 - забрано модулем </li><li> 2 - успешно внедрено в модуль </li><li> -1 - есть на модуле, нет на сервере </li>
	 */
	private int state;
	/** дата записи данного Checker-a */
	private Date date;
	
	/* 
	 * элемент из таблицы-списка, который отображает {@link AlarmChecker} для отдельно взятого датчика по отедельно взятому модулю
	 */
	public AlarmCheckerTableElement(){
	}
	
	/** элемент из таблицы-списка, который отображает {@link RegisterAlarmCheckerPanel} для отдельно взятого датчика по отедельно взятому модулю 
	 * @param id - уникальный идентификатор записи в таблице  
	 * @param idModule - уникальный идентификатор модуля
	 * @param modbusAddress номер устройства, к которому прикреплен данный Checker
	 * @param registerAddress номер регистра, к которому прикреплен данный адрес  
	 * @param inOnDevice порядковый номер данного checker-a на удаленном модуле 
	 * @param description - описание данного AlarmChecker-a
	 */
	public AlarmCheckerTableElement(int id, 
									int idModule, 
									int modbusAddress,
									Integer registerAddress,
									Integer idOnDevice,
									String description, 
									int state, 
									Date date){
		this.id=id;
		this.idModule=idModule;
		this.modbusAddress=modbusAddress;
		this.registerAddress=registerAddress;
		this.idOnDevice=idOnDevice;
		this.description=description;
		this.state=state;
		this.date=date;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/** состояние данного checker-а
	 * идентификатор состояния <li> 0 - новое задание </li> <li> 1 - забрано модулем </li> <li> 2 - успешно внедрено в модуль </li> <li> -1 - есть на модуле, нет на сервере </li>
	 */
	public int getState() {
		return state;
	}

	/** состояние данного checker-а
	 * идентификатор состояния <li> 0 - новое задание </li> <li> 1 - забрано модулем </li> <li> 2 - успешно внедрено в модуль </li> <li> -1 - есть на модуле, нет на сервере </li>
	 */
	public void setState(int state) {
		this.state = state;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getModbusAddress() {
		return modbusAddress;
	}

	public void setModbusAddress(int modbusAddress) {
		this.modbusAddress = modbusAddress;
	}

	public Integer getRegisterAddress() {
		return registerAddress;
	}

	public void setRegisterAddress(Integer registerAddress) {
		this.registerAddress = registerAddress;
	}

	public Integer getIdOnDevice() {
		return idOnDevice;
	}

	public void setIdOnDevice(Integer idOnDevice) {
		this.idOnDevice = idOnDevice;
	}

}
