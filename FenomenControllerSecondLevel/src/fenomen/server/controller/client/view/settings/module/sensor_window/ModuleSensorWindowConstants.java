package fenomen.server.controller.client.view.settings.module.sensor_window;

import com.google.gwt.i18n.client.Constants;

public interface ModuleSensorWindowConstants extends Constants{
	/** заголовок для окна */
	public String windowTitle();
	/** всплывающая подсказка для кнопки закрыть */
	public String buttonCloseTooltip();
	/** кнопка прочесть все сенсоры */
	public String buttonReadAllSensors();
	/** ошибка общения с сервером */
	public String serverExchangeError();
	
	/** заголовок предупреждения полной замены всех сенсоров */
	public String titleConfirmReadAllSensor();
	/** текст предупреждения замены всех сенсоров */
	public String textConfirmReadAllSensor();
	/** задача по обновлению всех датчиков по данному модулю с запросом на удаленный модуль */
	public String taskRefreshAllSensorSetOk();
	/** информационный обмен с сервером */
	public String serverExchange();
	
}
