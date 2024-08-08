package fenomen.module.core;

import fenomen.module.core.settings.ModuleSettings;

public interface IModuleSettingsAware {
	/** получить настройки модуля */
	public ModuleSettings getModuleSettings();
}
