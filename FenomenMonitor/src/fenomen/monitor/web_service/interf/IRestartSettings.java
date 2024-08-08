package fenomen.monitor.web_service.interf;

import fenomen.monitor.web_service.common.MonitorIdentifier;
import fenomen.monitor.web_service.common.RestartSettingsElement;

/** управление списком RestartSettings  */
public interface IRestartSettings {
	/** получить список 
	 * @param moduleIdentifier - уникальный идентификатор монитора 
	 * */
	public RestartSettingsElement[] getList(MonitorIdentifier moduleIdentifier) throws Exception;
	/** обновить список  
	 * @param moduleIdentifier - уникальный идентификатор монитора
	 * @param list - список элементов, который нужно обновить в базе данных (не обязательно полный список всех сопряжений, полученный {@link #getList(MonitorIdentifier)}) 
	 * */
	public boolean updateList(MonitorIdentifier moduleIdentifier, RestartSettingsElement[] list) throws Exception;
}
