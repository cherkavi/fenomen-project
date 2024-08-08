package fenomen.server.controller.client.view.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.ExtElement;
import com.gwtext.client.core.Function;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.ToolHandler;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.event.GridCellListener;

import fenomen.server.controller.client.view.RootComposite;
import fenomen.server.controller.client.view.main_menu.MainMenu;
import fenomen.server.controller.client.view.modules.table.row.RowData;
import fenomen.server.controller.client.view.settings.filter_window.FilterModuleWindow;
import fenomen.server.controller.client.view.settings.module.ModuleMenu;

/** окно по выбору модуля из списка */
public class ModuleChoice extends Composite implements IFindModule{
	/** текстовые переменный для данного модуля */
	private ModuleChoiceConstants constants=GWT.create(ModuleChoiceConstants.class);
	/** менеджер поиска записей */
	private FindModulesAsync findManager=GWT.create(FindModules.class);
	/** панель по отображению данных в виде Grid */
	private GridPanel grid;
	
	/** окно по выбору модуля */
	public ModuleChoice(){
		grid=new GridPanel();
		grid.addTool(new Tool(Tool.SEARCH,new ToolHandler(){
			@Override
			public void onClick(EventObject e, 
								ExtElement toolEl, 
								Panel panel) {
				onSearchClick();
			}
		},constants.searchButtonTitle()));
		grid.addTool(new Tool(Tool.CLOSE,new Function(){
			@Override
			public void execute() {
				onCloseClick();
			}
		},constants.closeWindow()));
		// create Grid
			// Gird.store
		RecordDef recordDef=new RecordDef(new FieldDef[]{
				new IntegerFieldDef("id"),
				new StringFieldDef("idModule"),
				new StringFieldDef("address")
		});
		Store store=new Store(new ArrayReader(recordDef));
			// Grid.column
		ColumnConfig[] columns=new ColumnConfig[]{
			//new ColumnConfig(constants.gridHeaderId(),"id",50,true),
			new ColumnConfig(constants.gridHeaderId(),"id",50,true),
			new ColumnConfig(constants.gridHeaderIdModule(),"idModule",200,true),
			new ColumnConfig(constants.gridHeaderAddress(),"address",300,true),
		};
			// Grid main
		grid.setColumnModel(new ColumnModel(columns));
		
		grid.setStore(store);
		grid.setTitle(constants.panelTitle());
		grid.setStore(store);
		grid.setWidth(RootComposite.getWindowWidth());
		grid.setHeight(RootComposite.getWindowHeight());
		grid.setStripeRows(true);
		grid.setAutoScroll(true);
		grid.addGridCellListener(new GridCellListener(){
			@Override
			public void onCellClick(GridPanel grid, 
									int rowIndex, 
									int colIndex,
									EventObject e) {
			}
			@Override
			public void onCellContextMenu(GridPanel grid, 
										  int rowIndex,
										  int cellIndex, 
										  EventObject e) {
			}
			@Override
			public void onCellDblClick(GridPanel grid, 
									   int rowIndex,
									   int colIndex, 
									   EventObject e) {
				RootComposite.showView(new ModuleMenu(grid.getStore().getAt(rowIndex).getAsString("idModule")+" : "+grid.getStore().getAt(rowIndex).getAsString("address"),
												  grid.getStore().getAt(rowIndex).getAsInteger("id")
												  )
										);
				//System.out.println("rowIndex:"+rowIndex+"   columnIndex:"+colIndex);
			}
		});
		this.initWidget(grid);
		this.filter(null, null);
	}
	
	/** реакция на нажатие клавиши закрытия окна */
	private void onCloseClick(){
		RootComposite.showView(new MainMenu());
	}
	
	/** реакция нажатия на клавишу поиска */
	private void onSearchClick(){
		// отобразить модальное окно с поиском по idModule и по адресу 
		(new FilterModuleWindow(this)).show();
	}

	@Override
	public void filter(String moduleId, String address) {
		MessageBox.wait("process", "Server request");
		String prepareModuleId=null;
		if(moduleId!=null){
			if(moduleId.trim().equals("")){
				prepareModuleId=null;
			}else{
				prepareModuleId=moduleId;
			}
		}
		String prepareAddress=null;
		if(address!=null){
			if(address.trim().equals("")){
				prepareAddress=null;
			}else{
				prepareAddress=address;
			}
		}
		// фильтр для модуля 
		findManager.findModules(new RowData(0,prepareModuleId, prepareAddress), new AsyncCallback<RowData[]>(){
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.hide();
				MessageBox.alert(ModuleChoice.this.constants.serverConnectionError());
			}

			@Override
			public void onSuccess(RowData[] result) {
				MessageBox.hide();
				ModuleChoice.this.filterCallback(result);
			}
		});
	}

	private void filterCallback(RowData[] rowData){
		this.grid.getStore().setDataProxy(new MemoryProxy(this.convertRowDataToArray(rowData)));
		this.grid.getStore().reload();
	}
	
	private Object[][] convertRowDataToArray(RowData[] rowData){
		Object[][] returnValue=new Object[rowData.length][3];
		for(int counter=0;counter<rowData.length;counter++){
			returnValue[counter][0]=rowData[counter].getId();
			returnValue[counter][1]=rowData[counter].getIdModule();
			returnValue[counter][2]=rowData[counter].getAddress();
		}
		return returnValue;
	}
	
}
