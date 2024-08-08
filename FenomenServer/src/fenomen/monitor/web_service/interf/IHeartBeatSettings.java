package fenomen.monitor.web_service.interf;

import fenomen.monitor.web_service.common.HeartBeatSettingsElement;

import fenomen.monitor.web_service.common.MonitorIdentifier;

/** управление списком HeartBeatSettings  */
public interface IHeartBeatSettings {
	/** получить список 
	 * @param moduleIdentifier - уникальный идентификатор монитора 
	 * */
	public HeartBeatSettingsElement[] getList(MonitorIdentifier moduleIdentifier) throws Exception;
	/** обновить список  
	 * @param moduleIdentifier - уникальный идентификатор монитора
	 * @param list - список элементов, который нужно обновить в базе данных (не обязательно полный список всех сопряжений, полученный {@link #getList(MonitorIdentifier)}) 
	 * */
	public boolean updateList(MonitorIdentifier moduleIdentifier, HeartBeatSettingsElement[] list) throws Exception;
}
