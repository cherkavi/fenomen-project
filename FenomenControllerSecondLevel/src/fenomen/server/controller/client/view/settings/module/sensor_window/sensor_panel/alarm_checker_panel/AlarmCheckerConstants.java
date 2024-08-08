package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel;

import com.google.gwt.i18n.client.Constants;

public interface AlarmCheckerConstants extends Constants{
	/** прочесть все Checker-ы */
	public String buttonReadAllCaption();
	/** заголовок для панели */
	public String title();
	/** caption для кнопки добавить Checker */
	public String buttonAddChecker();
	/** ошибка обмена с сервером  */
	public String serverExchangeError();// 
	/** ошибка */
	public String error();
	/** получение данных от сервера */
	public String getDataFromServer();
	/** описание Alarm checker-a*/
	public String titleDescription();
	/** предупреждение  */
	public String warning();
	/** изменение AlarmChcker-a */
	public String changeChecker();
	/** Редактирование */
	public String edit();
	/** Отмена */
	public String cancel();
	/** Удаление */
	public String remove();
	/** уверены в удалении элемента ?*/
	public String removeConfirm();
	/** Удалить */
	public String buttonRemove();
	/** Отменить */
	public String buttonCancel();
	/** информационный обмен с сервером */
	public String waitServerExchange();
	/** добавить Alarm Checker */
	public String addAlarmChecker();
	/** редактировать Alarm Checker */
	public String editAlarmChecker();
}
