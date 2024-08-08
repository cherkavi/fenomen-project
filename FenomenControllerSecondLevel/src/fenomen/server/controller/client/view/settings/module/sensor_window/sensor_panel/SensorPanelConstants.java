package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel;

import com.google.gwt.i18n.client.Constants;

public interface SensorPanelConstants extends Constants{
	/** кнопка для обновления всех параметров по датчику */
	public String buttonRefreshSensor();
	/** заголовок запроса на подтверждение обновления всех параметров */
	public String refreshConfirmTitle();
	/** запрос на подтверждение обновления всех параметров */
	public String refreshConfirmText();
	/** ошибка информационного обмена с сервером */
	public String errorServerExchange();
	/** обмен с сервером */
	public String serverExchange();
}
