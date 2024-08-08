package fenomen.server.controller.client.view.settings;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fenomen.server.controller.client.view.modules.table.row.RowData;

public interface FindModulesAsync {

	void findModules(RowData findData, AsyncCallback<RowData[]> callback);

}
