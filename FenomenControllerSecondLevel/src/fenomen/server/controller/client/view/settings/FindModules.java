package fenomen.server.controller.client.view.settings;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fenomen.server.controller.client.view.modules.table.row.RowData;

@RemoteServiceRelativePath("findModules")
public interface FindModules extends RemoteService{
	/** поиск модулей по уникальным ключам - адресу и по идентификатору модуля */
	public RowData[] findModules(RowData findData);
}
