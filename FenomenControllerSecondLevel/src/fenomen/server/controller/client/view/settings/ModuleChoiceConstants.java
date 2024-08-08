package fenomen.server.controller.client.view.settings;

import com.google.gwt.i18n.client.Constants;

public interface ModuleChoiceConstants extends Constants{
	/** заголовок дл€ панели */
	@DefaultStringValue("Modules")
	public String panelTitle();
	
	/** всплывающа€ подсказка дл€ кнопки "ѕоиск" в верхней части панели */
	@DefaultStringValue("Modules")
	public String searchButtonTitle();
	
	/** ошибка соединени€ с сервером */
	@DefaultStringValue("server error")
	public String serverConnectionError();
	
	/** заголовок дл€ таблицы ID */
	@DefaultStringValue("id")
	public String gridHeaderId();
	
	/** заголовок дл€ »дентификатора модул€ */
	@DefaultStringValue("id module")
	public String gridHeaderIdModule();
	
	/** заголовок дл€ јдреса */
	@DefaultStringValue("Address")
	public String gridHeaderAddress();

	/** закрыть окно и вернутьс€ в главное меню */
	@DefaultStringValue("Close")
	public String closeWindow();
}
