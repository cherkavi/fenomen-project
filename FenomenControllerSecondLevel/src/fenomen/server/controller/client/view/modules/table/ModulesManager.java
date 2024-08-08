package fenomen.server.controller.client.view.modules.table;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fenomen.server.controller.client.view.modules.table.row.RowData;

/** управление модулями на сервере (добавить, удалить, редактировать)*/
@RemoteServiceRelativePath("modulesManager")
public interface ModulesManager extends RemoteService{
	/** получить все элементы от сервера */
	public RowData[] getAllRowElement();
	
	/** оповестить сервер о необходимости удаления модуля 
	 * @param id - уникальный идентификатор модуля 
	 * @return 
	 * <li><b>true</b> - удален </li>
	 * <li><b>false</b> - ошибка удаления данных </li>
	 */
	public boolean removeRowElement(int id);
	
	/** оповестить сервер о необходимости обновления модуля 
	 * @param id -уникальный идентификатор модуля для обновления
	 * @param rowData - данные для обновления  
	 * @return
	 * <li><b>null</b> - добавление прошло успешно </li>
	 * <li><b>not null </b> - текст причины не добавления данных </li>
	 */
	public String updateRowElement(int id, RowData rowData);

	/** оповестить сервер о необходимости создания нового элемента-модуля
	 * @param rowData - элемент, который должен быть создан (без Id)
	 * @return 
	 * <li><b>null</b> - добавление прошло успешно </li>
	 * <li><b>not null </b> - текст причины не добавления данных </li>
	 * */
	public String createRowElement(RowData rowData);
}
