package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.WrapChecker;

public interface AlarmCheckerManagerAsync {
	public final static String presentationInteger="integer";
	public final static String presentationDouble="double";
	public final static String presentationZeroOne="0 or 1";
	public final static String presentationString="string";
	
	public final static String conditionEQ="==";
	public final static String conditionNE="!=";
	public final static String conditionGT=">";
	public final static String conditionGE=">=";
	public final static String conditionLT="<";
	public final static String conditionLE="<=";

	/**
	 * получить все зарегестрированные в базе данных AlarmChecker-ы по модулю.датчику
	 * @param idModule - уникальный id модуля (module.id в масштабе базы данных ) 
	 * @param modbusAddress - уникальный адрес сенсора-модуля 
	 * @return список зарегестрированных по данному модулю сенсоров 
	 */
	void getListOfAlarmChecker(int idModule, int modbusAddress,
			AsyncCallback<AlarmCheckerTableElement[]> callback);

	/** получить адреса всех регистров по модулю.датчику 
	 * @param idModule - уникальный номер модуля в масштабе базы (module.id)
	 * @param modbusAddress - уникальный идентификатор модуля в сети modbus
	 * @return массив типа int, состоящий из адресов доступных регистров  
	 */
	void getListOfRegisterAddress(int idModule, int modbusAddress,
			AsyncCallback<int[]> callback);

	/**
	 * удалить AlarmChecker
	 * @param idModule - уникальный номер модуля в базе данных (module.id)
	 * @param modbusAddress - уникальный номер модуля в сети ModBus ()
	 * @param registerNumber - номер регистра на сенсоре в сети Modbus 
	 * @param idChecker - уникальный номер checker-a по базе данных
	 * @return
	 * <li><b>null</b> remove was done </li>
	 * <li><b>Test of Error</b> remove error, returned text of error</li>
	 */
	void removeAlarmChecker(int idModule, 
							int modbusAddress, 
							int registerNumber,
							int idChecker, 
							AsyncCallback<String> callback);


	/**
	 * добавить/редактировать  AlarmChecker
	 * @param wrapChecker - обертка для Checker-a
	 * @return
	 * <li><b>null</b> Checker удачно добавлен  </li>
	 * <li><b>Value</b> текст ошибки </li>
	 */
	void addAlarmChecker(WrapChecker wrapChecker, AsyncCallback<String> callback);


	/** получить Checker по уникальному имени 
	 * @param id - уникальный идентификатор из базы данных: module_alarm_checker.id
	 * */
	void getAlarmChecker(Integer id, AsyncCallback<WrapChecker> callback);

}
