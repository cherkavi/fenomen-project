package fenomen.server.controller.client.view.modules.table;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

import fenomen.server.controller.client.view.modules.IUpdateNotify;
import fenomen.server.controller.client.view.modules.table.row.RowData;
import fenomen.server.controller.client.view.modules.table.row.RowElement;

/** таблица, котора€ отображает строки-модули */
public class TableModules extends Composite implements IModulesManagerAsyncAware{
	private ModulesManagerAsync modulesManager=GWT.create(ModulesManager.class);
	/** строки в таблице  */
	private ArrayList<RowData> rows=new ArrayList<RowData>();
	/** таблица, котора€ содержит эти строки  */
	private FlexTable table;
	/** объект, который нужно оповещать о необходимости обновлени€ данных */
	private IUpdateNotify updateNotify;
	
	/** таблица, котора€ отображает строки-модули */
	public TableModules(IUpdateNotify updateNotify){
		this.updateNotify=updateNotify;
		table=new FlexTable();
		this.initWidget(table);
	}
	
	@Override
	public ModulesManagerAsync getModulesManager(){
		return this.modulesManager;
	}
	
	public void updateTable(){
		// получить объекты от сервера
		modulesManager.getAllRowElement(new AsyncCallback<RowData[]>(){
			@Override
			public void onFailure(Throwable caught) {
				System.err.println("updateTable Exception: "+caught.getMessage());
			}
			@Override
			public void onSuccess(RowData[] result) {
				TableModules.this.onSuccess(result);
			}
		});
	}

	/** сервер вернул положительный результат в виде массива элементов, который отображают все доступные элементы */
	private void onSuccess(RowData[] result){
		// удалить все строки
		table.removeAllRows();
		rows.clear();
		for(int counter=0;counter<result.length;counter++){
			RowData rowData=result[counter];
			//System.out.println("RowData:"+rowData);
			rows.add(rowData);
			table.insertRow(counter);
			table.insertCell(counter, 0);
			table.setWidget(counter, 0, new RowElement(TableModules.this.updateNotify, TableModules.this, rowData));
		}
	}
}
