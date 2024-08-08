package fenomen.server.controller.client.view.modules.table.add_window;

import com.google.gwt.i18n.client.Constants;

public interface AddWindowConstants extends Constants{
	/** заголовок для основного окна */
	public String titleWindow();
	/** заголовок для адреса */
	public String titleAddress();
	/** надпись на кнопке добавить */
	public String captionButtonAdd();
	/** надпись на кнопке Отмены добавления */
	public String captionButtonCancel();
	/** ошибка информационного обмена с сервером */
	public String serverError();
	/** заголовок для модуля */
	public String titleModule();
}
