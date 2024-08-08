package fenomen.server.controller.client.view.settings.module.sensor_window;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SensorManagerAsync {
	/** послать на базу задачу по получению от удаленного модуля, когда он выйдет на связь, необходимой информации обо всех датчиках/сенсорах в системе
	 * @param moduleId - уникальный идентификатор модуля по базе данных (module.id)  
	 * */
	void setTaskGetAllSensor(int moduleId, AsyncCallback<Boolean> callback);

	/** получить все датчики в системе на основании уникального номера модуля 
	 * @param moduleId - уникальный идентификатор модуля по базе данных (module.id)
	 * */
	void getAllSensorsIntoModule(int moduleId,
			AsyncCallback<Device[]> callback);

	/** получить полное описание датчика 
	 * @param moduleId - код модуля в базе данных  (module.id)
	 * @param addressModbus - уникальный адрес в сети Modbus для датчика/сенсора
	 * @return {@link Device} объект-обертка для записи из таблицы базы данных 
	 */
	void getSensorIntoModule(int moduleId, int addressModbus,
			AsyncCallback<Device> callback);

	/** послать запрос на удаленный модуль для получения значений из всех регистров по датчику  
	 * @param moduleId - код модуля в базе данных (module.id)
	 * @param addressModbus - уникальный адрес в сети Modbus для данного датчика/сенсора 
	 * @return 
	 * <li> <b>true</b> task успешно установлен </li> 
	 * <li> <b>false</b> ошибка установки Task </li> 
	 * */
	void setTaskGetValueFromModule(int moduleId, int addressModbus,
			AsyncCallback<Boolean> callback);

	/** послать запрос на установку значения в модуле 
	 * @param moduleId - код модуля в базе данных  (module.id)
	 * @param addressModbus - уникальный адрес сенсора/датчика в сети ModBus
	 * @param registerAddress - уникальный адрес регистра на модуле  
	 * @param value - значение, которое нужно передать в регистр
	 * @param isEnabled  
	 * <ul type="square">
	 * 		<li> <b>true</b>- установить значение для датчика Enabled</li>
	 * 		<li> <b>false</b>- установить значение для датчика Disabled</li>
	 * 		<li> <b>null</b>- не изменять значения датчика</li>
	 * </ul>
	 * @return 
	 * <li> <b>true</b> task успешно установлен </li> 
	 * <li> <b>false</b> ошибка установки Task </li> 
	 * */
	void setTaskPutValueToModuleToRegister(int moduleId, int addressModbus,
			int registerAddress, int value, Boolean isEnabled,
			AsyncCallback<Boolean> callback);

	/** послать запрос на установку значений в модуле 
	 * @param moduleId - код модуля в базе данных  (module.id)
	 * @param addressModbus - уникальный адрес сенсора/датчика в сети ModBus
	 * @param registers - адреса регистров на модуле ( в виде массива )   
	 * @param values - значения для регистров (в виде массива соответствие параметру registers )
	 * @param isEnabled  
	 * <ul type="square">
	 * 		<li> <b>true</b>- установить значение для датчика Enabled</li>
	 * 		<li> <b>false</b>- установить значение для датчика Disabled</li>
	 * 		<li> <b>null</b>- не изменять значения датчика</li>
	 * </ul>
	 * @return 
	 * <li> <b>true</b> task успешно установлен </li> 
	 * <li> <b>false</b> ошибка установки Task </li> 
	 * */
	void setTaskPutValueToModuleToRegister(int moduleId, int addressModbus,
			int[] registers, int[] values, Boolean isEnabled,
			AsyncCallback<Boolean> callback);

}
