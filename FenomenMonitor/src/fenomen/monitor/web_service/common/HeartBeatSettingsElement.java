package fenomen.monitor.web_service.common;

/** объект, который представляет один элемент из таблицы monitor_settings_heart_beat */
public class HeartBeatSettingsElement {
	/** уникальный идентификатор в базе данных */
	private int id;
	/** имя модуля для идентификации, как правило число  */
	private String moduleId;
	/** адрес модуля */
	private String moduleAddress;
	/** нужно ли оповещать по данному модулю  */
	private boolean enabled;
	/** кол-во секунд после которых следует выставлять сигнал сердцебиения как не принятый*/
	private int timeWait;
	/** значение в секундах, которое установлено для модуля в качестве сердцебиения, и timeWait должно быть больше */
	private int settingValue;
	
	/** объект, который представляет один элемент из таблицы Restart */
	public HeartBeatSettingsElement(){
	}

	/** уникальный идентификатор по базе данных (monitor_settings_restart.id)*/
	public int getId() {
		return id;
	}

	/** уникальный идентификатор по базе данных (monitor_settings_restart.id)*/
	public void setId(int id) {
		this.id = id;
	}


	/** флаг оповещения */
	public boolean isEnabled() {
		return enabled;
	}

	/** флаг оповещения */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/** адрес модуля  */
	public String getModuleAddress() {
		return moduleAddress;
	}

	/** адрес модуля  */
	public void setModuleAddress(String moduleAddress) {
		this.moduleAddress = moduleAddress;
	}

	/** наименование модуля */
	public String getModuleId() {
		return moduleId;
	}

	/** наименование модуля */
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	/** время ожидания сигнала heartBeat, после которого будет создано событие HeartBeat */
	public int getTimeWait() {
		return timeWait;
	}

	/** время ожидания сигнала heartBeat, после которого будет создано событие HeartBeat */
	public void setTimeWait(int timeWait) {
		this.timeWait = timeWait;
	}

	/** значение из настройки модуля, через какое время должен модуль посылать сигналы на сервер */
	public int getSettingsValue() {
		return this.settingValue;
	}
	
	/** значение из настройки модуля, через какое время должен модуль посылать сигналы на сервер */
	public void setSettingsValue(int value) {
		this.settingValue=value;
	}
	
}
