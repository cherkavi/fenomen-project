package fenomen.monitor.web_service.common;

/** объект, который представляет один элемент из таблицы monitor_settings_alarm */
public class AlarmSettingsElement {
	/** уникальный идентификатор в базе данных */
	private int id;
	/** имя модуля для идентификации, как правило число  */
	private String moduleId;
	/** адрес модуля */
	private String moduleAddress;
	/** нужно ли оповещать по данному модулю  */
	private boolean enabled;
	
	/** объект, который представляет один элемент из таблицы Restart */
	public AlarmSettingsElement(){
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

	public String getModuleAddress() {
		return moduleAddress;
	}

	public void setModuleAddress(String moduleAddress) {
		this.moduleAddress = moduleAddress;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
}
