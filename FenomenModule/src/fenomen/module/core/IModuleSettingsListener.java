package fenomen.module.core;

import fenomen.module.core.settings.ModuleSettings;

/** интерфейс для изменения объекта {@link ModuleSettings} или же его частей */
public interface IModuleSettingsListener {
	/** установить один из параметров ModuleSettings и оповестить слушателей о необходимости замены/обновления данных  */
	public void setModuleSettings(String paramName, String paramValue);
	/** заменить полностью объект ModuleSettings */
	public void setModuleSettings(ModuleSettings moduleSettings);
}
