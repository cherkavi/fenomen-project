package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.NameValuePair;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.GridCellListener;

import fenomen.server.controller.client.view.modules.IActionNotify;


/** панель, которая отображает все AlarmChecker-ы */
public class AlarmCheckerTable extends GridPanel{
	private AlarmCheckerConstants constants=null;
	/** объект, которому нужно передавать управление в случае запроса на выполнение действий (edit, remove)*/
	private IActionNotify actionNotify;
	/** columns into data for view */
	private final String[] columns=new String[]{"id", // 0
												"idModule", // 1
												"modbusAddress", // 2
												"registerAddress",// 3
												"idOnDevice",// 4
												"state",// 5
												"description" // 6 
												};
	
	/** обновить данные в таблице */
	public void updateModel(AlarmCheckerTableElement[] elements){
		this.getStore().setDataProxy(new MemoryProxy(this.convertRowDataToArray(elements)));
		this.getStore().reload();
	}

	/** конвертация POJO в Object[][] */
	private Object[][] convertRowDataToArray(AlarmCheckerTableElement[] rowData){
		Object[][] returnValue=new Object[rowData.length][7];
		for(int counter=0;counter<rowData.length;counter++){
			returnValue[counter][0]=rowData[counter].getId();
			returnValue[counter][1]=rowData[counter].getIdModule();
			returnValue[counter][2]=rowData[counter].getModbusAddress();
			returnValue[counter][3]=rowData[counter].getRegisterAddress();
			returnValue[counter][4]=rowData[counter].getIdOnDevice();
			returnValue[counter][5]=rowData[counter].getState();
			returnValue[counter][6]=rowData[counter].getDescription();
		}
		return returnValue;
	}
	
	
	/** панель, которая отображает все AlarmChecker-ы */
	public AlarmCheckerTable(AlarmCheckerConstants constants, IActionNotify actionNotify){
		this.actionNotify=actionNotify;
		this.constants=constants;
		RecordDef recordDef=new RecordDef(new FieldDef[]{
				new IntegerFieldDef(columns[0]),// id
				new IntegerFieldDef(columns[1]),// idModule
				new IntegerFieldDef(columns[2]),// modbusAddress
				new IntegerFieldDef(columns[3]),// registerAddress
				new IntegerFieldDef(columns[4]),// idOnDevice
				new IntegerFieldDef(columns[5]),// State
				new StringFieldDef(columns[6])  // description
		});
		Store store=new Store(new ArrayReader(recordDef));
		// Grid.column
		ColumnConfig[] columnsConfig=new ColumnConfig[]{
				//new ColumnConfig(constants.gridHeaderId(),"id",50,true),
				new ColumnConfig("id",columns[0],30,true),
				new ColumnConfig("register",columns[3],30,true),
				new ColumnConfig("state",columns[5],250,true,new Renderer(){
					@Override
					public String render(Object value,
										 CellMetadata cellMetadata, 
										 Record record,
										 int rowIndex, 
										 int colNum, 
										 Store store) {
						if(value instanceof Integer){
							Integer integerValue=(Integer)value;
							// идентификатор состояния <li>0 - новое задание </li><li> 1 - забрано модулем </li><li> 2 - успешно внедрено в модуль </li><li> -1 - есть на модуле, нет на сервере </li>
							if(integerValue.intValue()==0){
								return "new task";
							}else if(integerValue.intValue()==1){
								return "recieved of module";
							}else if(integerValue.intValue()==2){
								return "module confirm";
							}else if(integerValue.intValue()==(-1)){
								return "module exists, server not found ";
							}else{
								return "unknown";
							}
						}else{
							if(value!=null){
								return value.toString();
							}else{
								return "";
							}
						}
					}
					
				}),
				new ColumnConfig(this.constants.titleDescription(),columns[6],250,true),
		};
		this.setColumnModel(new ColumnModel(columnsConfig));
		
		this.setStore(store);
		// this.setTitle(constants.panelTitle());
		this.setStore(store);
		this.setWidth("100%");
		this.setHeight("100%");
		this.setStripeRows(true);
		this.setAutoScroll(true);
		this.addGridCellListener(new GridCellListener(){
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
				AlarmCheckerTable.this.onDblClick(rowIndex);
			}
		});
	}
	
	private void onDblClick(int rowIndex){
		final int sourceRowIndex=rowIndex;
		final String description=this.getStore().getAt(rowIndex).getAsString(columns[3]);
		MessageBox.show(new MessageBoxConfig(){
			{
				this.setTitle(AlarmCheckerTable.this.constants.warning());
				this.setMsg(AlarmCheckerTable.this.constants.changeChecker()+" ("+description+")");
				this.setButtons(true);
				this.setButtons(new NameValuePair[]{new NameValuePair("yes",AlarmCheckerTable.this.constants.edit()),
							    new NameValuePair("cancel",AlarmCheckerTable.this.constants.cancel()),
							    new NameValuePair("no",AlarmCheckerTable.this.constants.remove()),
				});
				this.setCallback(new MessageBox.PromptCallback() {
					@Override
					public void execute(String btnID, String text) {
						if(btnID.equals("cancel")){
							// Cancel
						}
						if(btnID.equals("yes")){
							// Edit
							AlarmCheckerTable.this.editElement(sourceRowIndex);
						}
						if(btnID.equals("no")){
							// remove
							AlarmCheckerTable.this.requestRemoveElement(sourceRowIndex);
						}
					}
				});
			}
		});
	}
	
	private void requestRemoveElement(int rowIndex){
		final int storeRowIndex=rowIndex;
		final String description=this.getStore().getAt(rowIndex).getAsString(columns[3]);
		MessageBox.show(new MessageBoxConfig(){
			{
				this.setTitle(AlarmCheckerTable.this.constants.warning());
				this.setMsg(AlarmCheckerTable.this.constants.removeConfirm()+"("+description+")");
				this.setButtons(new NameValuePair[]{
								new NameValuePair("ok",AlarmCheckerTable.this.constants.buttonRemove()),
								new NameValuePair("cancel",AlarmCheckerTable.this.constants.buttonCancel())
				});
				this.setCallback(new MessageBox.PromptCallback() {
					@Override
					public void execute(String btnID, String text) {
						if(btnID.equals("ok")){
							AlarmCheckerTable.this.removeElement(storeRowIndex);
							System.out.println("Remove ");
						}
						if(btnID.equals("cancel")){
							// Cancel
						}
					}
				});
			}
		});
	}
	
	/** удалить элемент, все валидаторы пройдены */
	private void removeElement(int rowIndex){
		final int rowId=this.getStore().getAt(rowIndex).getAsInteger(columns[0]);
		this.actionNotify.actionNotify("remove", new Integer(rowId));
	}
	
	private void editElement(int rowIndex){
		final int rowId=this.getStore().getAt(rowIndex).getAsInteger(columns[0]);
		this.actionNotify.actionNotify("edit", new Integer(rowId));
	}
}
