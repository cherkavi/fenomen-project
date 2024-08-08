package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker;

import com.google.gwt.i18n.client.Constants;

public interface AddAlarmCheckerConstants extends Constants{
	/** всплывающая подсказка для кнопки закрыть */
	public String buttonCloseHint();
	
	/** заголовок для ввода сообщения, которое будет приходить на сервер при возникновении события */
	public String captionAlertForUserMessage();// Сообщение, которое будет отображаться как тревожное:
	
	/** заголовок для ввода описания, которое будет отображаться в качестве Description для AlertChecker */
	public String captionAlertForDescription();// Краткое описание данного AlertChecker-a
	
	/** caption for Button save */
	public String captionButtonSave();// Сохранить
	
	/** caption for Button Cancel */
	public String captionButtonCancel();// Отменить
	/** Недостаточно данных */
	public String lessData(); 
	/** Ошибка преобразования */
	public String errorConversion(); 
	/** Ожидается тип */
	public String needForType();
	/** Необходимо ввести время повторного не получения значения  */
	public String needForTimeRepeat();
	/** Введите тревожное сообщение */
	public String inputAlarmMessage();
	/** Сделайте выбор по преобразованию значения */
	public String choicePresentationValue();
	/** Сделайте выбор по условию на значение */
	public String choiceConditionValue();
	/** Ожидание */
	public String waitMessage();
	/** Сохранение...  */
	public String saving();
	/** Ошибка обмена с сервером */
	public String serverExchangeError();
}
