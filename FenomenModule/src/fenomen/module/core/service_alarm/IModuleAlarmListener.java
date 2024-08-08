package fenomen.module.core.service_alarm;

import fenomen.module.web_service.common.ContainerModuleAlarm;

/** интерфейс получения оповещения о новых информационных событиях */
public interface IModuleAlarmListener {
	/** оповещение о новых тревожных событиях 
	 * @param moduleAlarm - объект, который должнен быть передан на сервер в качестве тревожного сообщения блока 
	 */
	public void notifyAlarm(ContainerModuleAlarm moduleAlarm);
}
