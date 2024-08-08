package fenomen.server.controller.client.view.settings.module;

import com.google.gwt.i18n.client.Constants;

public interface ModuleConstants extends Constants{
	/** получить заголовок для панели */
	public String title();
	
	/** всплывающая подсказка для закрывающегося окна */
	public String buttonCloseTooltip();

	/** заголовок для кнопки Settings */
	public String buttonSettingsCaption();
	
	/** заголовок для кнопки Sensor */
	public String buttonSensorCaption();
}
