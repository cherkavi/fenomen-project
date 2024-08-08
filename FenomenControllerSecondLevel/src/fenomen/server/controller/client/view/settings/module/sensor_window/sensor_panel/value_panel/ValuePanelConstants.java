package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.value_panel;

import com.google.gwt.i18n.client.Constants;

public interface ValuePanelConstants extends Constants{
	/** заголовок для панели */
	public String title();
	/** Значение датчика */
	public String labelSensorValue();
	/** дата установки датчика в указанное значение */
	public String labelDateValue();
	/** заголовок для кнопки "установить значение"*/
	public String labelButtonSet();
	/** заголовок для кнопки "получить значение"*/
	public String labelButtonGet();
	
	/** подтверждение установки значения Заголовок */
	public String confirmSetValueTitle();
	
	/** предупреждение о невозможности связи с сервером */
	public String serverError();
	
	/** происходит обмен с сервером */
	public String serverExchange();
	/** задача успешно добавлена в очередь, ожидайте ответа от модуля */
	public String taskWasSended();
}
