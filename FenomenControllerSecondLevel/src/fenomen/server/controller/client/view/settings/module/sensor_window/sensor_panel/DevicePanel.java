package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel;

import java.util.ArrayList;



import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.RegionPosition;
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
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.event.GridCellListener;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

import fenomen.server.controller.client.utility.INotifyClose;
import fenomen.server.controller.client.utility.NotifyCloseContainer;
import fenomen.server.controller.client.view.settings.module.sensor_window.Device;
import fenomen.server.controller.client.view.settings.module.sensor_window.DeviceRegister;
import fenomen.server.controller.client.view.settings.module.sensor_window.SensorManagerAsync;

/** панель, которая отображает отдельное устройство на удаленном модуле */
public class DevicePanel extends Panel implements INotifyClose{
	/** уникальный идентификатор модуля в масштабе базы данных  */
	private int moduleId;
	/** обертка для датчика */
	private Device device;
	private SensorPanelConstants constants=GWT.create(SensorPanelConstants.class);
	private SensorManagerAsync sensorManager;
	/** объект, который содержит все дочерние панели, которые требуют оповещения о закрытии */
	private NotifyCloseContainer notifyCloseContainer=new NotifyCloseContainer();
	/** таблица с данными, которая отображает все доступные регистры по данному модулю */
	private GridPanel table=null;
	
	/** панель, которая отображает датчик в системе
	 * @param moduleId - уникальный идентификатор модуля в масштабе базы данных 
	 * @param device - устройство, которое нужно отобразить
	 * @param sensorManager - менеджер датчиков 
	 */
	public DevicePanel(int moduleId,
					   Device device,
					   SensorManagerAsync sensorManager){
		this.moduleId=moduleId;
		this.sensorManager=sensorManager;
		this.device=device;
		String title=device.getModbusAddress()+" : "+device.getSensorTypeDescription();
		this.setTitle(title);
		this.addTool(new Tool(Tool.REFRESH,new Function(){
			@Override
			public void execute() {
				onButtonRefresh();
			}
		},constants.buttonRefreshSensor()));
		
		this.setLayout(new BorderLayout());
		
		table=new GridPanel();
		Store store=new Store(new MemoryProxy(getDataFromList(this.device.getListOfRegister())),
							  new ArrayReader(new RecordDef(new FieldDef[]{
									  									   new IntegerFieldDef("id"),
									  									   new IntegerFieldDef("registerAddress"),
									  									   new IntegerFieldDef("registerValue"),
									  									   new StringFieldDef("description")
							  })));
		store.load();
		table.setStore(store);
		table.setColumnModel(new ColumnModel(new ColumnConfig[]{
				new ColumnConfig("number","id",50,true),
				new ColumnConfig("address","registerAddress",50,true),
				new ColumnConfig("Value","registerValue",50,true),
				new ColumnConfig("Note","description",200,true)
		}));
		this.add(table,new BorderLayoutData(RegionPosition.CENTER));
		this.add(new HTML("&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"),new BorderLayoutData(RegionPosition.WEST));
		table.addGridCellListener(new GridCellListener(){
			@Override
			public void onCellClick(GridPanel grid, int rowIndex, int colIndex,
					EventObject e) {
			}
			@Override
			public void onCellContextMenu(GridPanel grid, 
										  int rowIndex,
										  int cellIndex, EventObject e) {
			}
			@Override
			public void onCellDblClick(GridPanel grid, 
									   int rowIndex,
									   int colIndex, 
									   EventObject e) {
				Record record=DevicePanel.this.table.getStore().getAt(rowIndex);
				DevicePanel.this.showWindow(record.getAsInteger("registerAddress"));
			}
		});
	}

	/** отобразить окно с одним регистром  */
	private void showWindow(int registerAddress){
		DeviceRegister register=null;
		for(int counter=0;counter<this.device.getListOfRegister().size();counter++){
			if(this.device.getListOfRegister().get(counter).getRegisterAddress()==registerAddress){
				register=this.device.getListOfRegister().get(counter);
				break;
			}
		}
		if(register!=null){
			new RegisterWindow(this.moduleId, this.device, register, this.sensorManager, this.notifyCloseContainer);
		}else{
			MessageBox.alert("Error","register not found");
		}
	}
	
	/** преобразовать список регистров в двухмерный массив объектов для отображения в таблице */
	private Object[][] getDataFromList(ArrayList<DeviceRegister> listOfRegister){
		Object[][] returnValue=new Object[listOfRegister.size()][];
		for(int counter=0;counter<listOfRegister.size();counter++){
			returnValue[counter]=new Object[]{listOfRegister.get(counter).getId(),
											  listOfRegister.get(counter).getRegisterAddress(),
											  listOfRegister.get(counter).getRegisterValue(),
											  listOfRegister.get(counter).getDescription()};
		}
		return returnValue;
	}
	
	private void onButtonRefresh(){
		/*MessageBox.confirm(constants.refreshConfirmTitle(), 
						   constants.refreshConfirmText(), 
						   new ConfirmCallback() {
			@Override
			public void execute(String btnID) {
				if(btnID.equalsIgnoreCase("yes")){
					fullRefreshSensorData();
				}else{
					// user skip this query
				}
			}
		});*/
		refreshDeviceData();
	}

	/** обновление значений по датчику-сенсору, без запроса данных на удаленном модуле */
	private void refreshDeviceData(){
		this.sensorManager.getSensorIntoModule(this.device.getIdModule(), 
											   this.device.getModbusAddress(), 
											   new AsyncCallback<Device>(){
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.hide();
				MessageBox.alert(DevicePanel.this.constants.errorServerExchange());
			}

			@Override
			public void onSuccess(Device result) {
				MessageBox.hide();
				if(result!=null){
					DevicePanel.this.device=result;
					DevicePanel.this.table.getStore().setDataProxy(new MemoryProxy(getDataFromList(DevicePanel.this.device.getListOfRegister())));
					DevicePanel.this.table.getStore().load();
				}else{
					MessageBox.alert(DevicePanel.this.constants.errorServerExchange());
				}
			}
		});
		MessageBox.show(new MessageBoxConfig(){
			{
				this.setWait(true);
				this.setWidth(300);
				this.setProgressText(DevicePanel.this.constants.serverExchange());
				this.setWaitConfig(new WaitConfig(){
					{
						this.setInterval(200);
					}
				});
			}
		});
	}

	@Override
	public void notifyClose() {
		this.notifyCloseContainer.notifyAllObjectAndRemoveThey();
	}
}


