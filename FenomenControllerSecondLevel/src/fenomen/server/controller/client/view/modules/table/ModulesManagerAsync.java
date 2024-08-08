package fenomen.server.controller.client.view.modules.table;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fenomen.server.controller.client.view.modules.table.row.RowData;

public interface ModulesManagerAsync {
	void getAllRowElement(AsyncCallback<RowData[]> callback);

	void removeRowElement(int id, AsyncCallback<Boolean> callback);

	void updateRowElement(int id, RowData rowData,
			AsyncCallback<String> callback);

	void createRowElement(RowData rowData, AsyncCallback<String> callback);
}
