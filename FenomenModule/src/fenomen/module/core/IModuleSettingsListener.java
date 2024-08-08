package fenomen.module.core;

import fenomen.module.core.settings.ModuleSettings;

/** ��������� ��� ��������� ������� {@link ModuleSettings} ��� �� ��� ������ */
public interface IModuleSettingsListener {
	/** ���������� ���� �� ���������� ModuleSettings � ���������� ���������� � ������������� ������/���������� ������  */
	public void setModuleSettings(String paramName, String paramValue);
	/** �������� ��������� ������ ModuleSettings */
	public void setModuleSettings(ModuleSettings moduleSettings);
}
