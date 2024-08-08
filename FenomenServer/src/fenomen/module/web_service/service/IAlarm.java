package fenomen.module.web_service.service;

import fenomen.module.web_service.common.TransportChecker;
import fenomen.module.web_service.common.ContainerModuleAlarm;
import fenomen.module.web_service.common.ModuleIdentifier;

public interface IAlarm {
	/** флаг возврата, который "говорит" об успешной передаче данных на сервер */
	public static final String returnOk="return_ok";
	/** флаг возврата, который "говорит" об ошибочной передаче данных на сервер */
	public static final String returnError="return_error";

	
	/** 
	 * @param moduleIdentifier - уникальный идентификатор модул€ 
	 * @return - 
	 * <li> <b>true</b> - успешно отработано </li>
	 * <li> <b>false</b> - ошибка передачи   </li>
	 */
	public boolean moduleWasRestarted(ModuleIdentifier moduleIdentifier);
	
	/**  послать на сервер уведомление о наличии Alarm сообщени€ 
	 * @param moduleIdentifier - уникальный идентификатор модул€ 
	 * @param moduleAlarm - контейнер событий дл€ отправки 
	 * @return - возвращаемое значение
	 * <li><b>return_ok({@link IAlarm.returnOk})</b> - успешно обработано сервером </li>
	 * <li><b>return_Error({@link IAlarm.returnError})</b> - ошибка обработки сервером </li>
	 */
	public String sendAlarm(ModuleIdentifier moduleIdentifier, ContainerModuleAlarm moduleAlarm);
	
	/** получить от сервера объект-провер€ющий дл€ событий AlarmChecker 
	 * @param moduleIdentifier - уникальный идентификатор модул€ 
	 * @param fileId - уникальный идентификатор объекта AlarmChecker 
	 * @return - объект дл€ данного модул€ по указанному идентификатору 
	 */
	public TransportChecker getAlarmCheckerById(ModuleIdentifier moduleIdentifier, String fileId);
	
	/** подтверждение в получении от сервера файла  
	 * @param moduleIdentifier - уникальный идентификатор модул€ 
	 * @param fileId - уникальный идентификатор объекта AlarmChecker 
	 * @return - возвращаемое значение
	 * <li><b>return_ok {@link IAlarm.returnOk}</b> - успешно обработано сервером </li>
	 * <li><b>return_Error {@link IAlarm.returnError}</b> - ошибка обработки сервером </li>
	 */
	public String confirmAlarmCheckerGet(ModuleIdentifier moduleIdentifier, String fileId);
}
