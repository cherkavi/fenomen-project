package fenomen.server.controller.client.view.settings.module.sensor_window;

import com.google.gwt.core.client.GWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.MessageBox.ConfirmCallback;
import com.gwtext.client.widgets.event.WindowListenerAdapter;
import com.gwtext.client.widgets.layout.AccordionLayout;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

import fenomen.server.controller.client.utility.NotifyCloseContainer;
import fenomen.server.controller.client.view.RootComposite;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.DevicePanel;

/** окно, отображающее все устройства на удаленном модуле  */
public class ModuleSensorWindow extends Window{
	/** текстовые переменны */
	private ModuleSensorWindowConstants constants=GWT.create(ModuleSensorWindowConstants.class);
	/** менеджер датчиков */
	private SensorManagerAsync sensorManager=GWT.create(SensorManager.class);
	
	/** уникальный идентификатор модуля */
	private int moduleId;
	
	/** панель на которой расположены все датчики в системе */
	private Panel panelMain;
	
	/** объект, который хранит ссылки на дочерние компоненты */
	private NotifyCloseContainer notifyCloseContainer=new NotifyCloseContainer();
	
	/** окно, отображающее все устройства на удаленном модуле   
	 * @param id - уникальный идентификатор модуля 
	 */
	public ModuleSensorWindow(int moduleId){
		this.moduleId=moduleId;
		this.setTitle(constants.windowTitle());
		this.setModal(false);
		this.setClosable(false);
		this.setSize( (int)(RootComposite.getWindowWidth()/1), (int)(RootComposite.getWindowHeight()/1));
		this.addTool(new Tool(Tool.CLOSE,new Function(){
			@Override
			public void execute() {
				ModuleSensorWindow.this.closeWindow();
			}
		},constants.buttonCloseTooltip()));
		
		
		/** кнопка "Прочесть все датчики в системе" */
		Button buttonReadAllSensors=new Button(constants.buttonReadAllSensors());
		buttonReadAllSensors.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonRefreshAll();
			}
		});
		HorizontalPanel panelManager=new HorizontalPanel();
		panelManager.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panelManager.add(buttonReadAllSensors);
		panelManager.setHeight("30px");
		this.setLayout(new BorderLayout());
		BorderLayoutData northData=new BorderLayoutData(RegionPosition.NORTH);
		this.add(panelManager,northData);
		
		this.panelMain=new Panel();
		panelMain.setLayout(new AccordionLayout());
		
		/** наполнить основную панель устройствами по данному модулю */
		this.fillPanelMainAsync();
		
		this.add(panelMain, new BorderLayoutData(RegionPosition.CENTER));
		this.addListener(new WindowListenerAdapter(){
			@Override
			public void onClose(Panel panel) {
				// оповестить все дочерние панели о необходимости прекращения любого информационного обмена с сервером
				notifyCloseContainer.notifyAllObjectAndRemoveThey();
				super.onClose(panel);
			}
		});
	}

	/** наполнить основную панель датчиками от сервера */
	private void fillPanelMainAsync(){
		MessageBox.show(new MessageBoxConfig(){
			{
				this.setWait(true);
				this.setWidth(300);
				this.setProgressText(ModuleSensorWindow.this.constants.serverExchange());
				this.setWaitConfig(new WaitConfig(){
					{
						this.setInterval(200);
					}
				});
			}
		});
		this.sensorManager.getAllSensorsIntoModule(this.moduleId, new AsyncCallback<Device[]>(){
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.hide();
				MessageBox.alert(constants.serverExchangeError());
			}
			@Override
			public void onSuccess(Device[] result) {
				MessageBox.hide();
				ModuleSensorWindow.this.fillPanelMain(result);
			}
		});
	}
	
	/** наполнить основную панель датчиками от сервера */
	private void fillPanelMain(Device[] result){
		this.panelMain.clear();
		if(result==null){
			MessageBox.alert(this.constants.serverExchangeError());
		}else{
			for(int counter=0;counter<result.length;counter++){
				// создать панель по каждому датчику
				DevicePanel sensorPanel=new DevicePanel(this.moduleId, 
													    result[counter],
														this.sensorManager
														);
				this.notifyCloseContainer.addNotifyClose(sensorPanel);
				this.panelMain.add(sensorPanel);
			}
			this.panelMain.setSize(this.panelMain.getWidth(), 
					   this.panelMain.getHeight()+1);
		}
	}
	
	
	/** обновить все кнопки  */
	private void onButtonRefreshAll(){
		MessageBox.confirm(constants.titleConfirmReadAllSensor(),constants.textConfirmReadAllSensor(), new ConfirmCallback(){
			@Override
			public void execute(String btnID) {
				if(btnID.equalsIgnoreCase("yes")){
					refreshAllDevices();
				}else{
					// user choice cancel 
				}
			}
		});
		System.out.println("Refresh All Button's");
	}
	
	/** обновить все устройства по данному модулю - прочесть устройтсва из модуля */
	private void refreshAllDevices(){
		// оповестить о необходимости закрытия все дочерние окна, которые возможно были открыты и удалить их из списка оповещения
		try{
			this.notifyCloseContainer.notifyAllObjectAndRemoveThey();
		}catch(NullPointerException npe){};
		// очистить панель от тех устройств, которые уже есть на панели ( в списке )
		this.panelMain.removeAll();
		
		// послать команду прочтения всех датчиков из удаленного Модуля
		this.sensorManager.setTaskGetAllSensor(this.moduleId, new AsyncCallback<Boolean>(){
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert(constants.serverExchangeError());
			}
			@Override
			public void onSuccess(Boolean result) {
				if(ModuleSensorWindow.this.timerWaitDevices!=null){
					ModuleSensorWindow.this.timerWaitDevices.cancel();
				};
				MessageBox.alert(ModuleSensorWindow.this.constants.taskRefreshAllSensorSetOk());
				// старт таймера, который будет опрашивать удаленную систему на наличие данных
				ModuleSensorWindow.this.timerWaitDevices=new Timer(){
					@Override
					public void run() {
						// послать запрос на сервер и получить ответ 
						ModuleSensorWindow.this.sensorManager.getAllSensorsIntoModule(ModuleSensorWindow.this.moduleId, new AsyncCallback<Device[]>(){
							@Override
							public void onFailure(Throwable caught) {
								MessageBox.alert(ModuleSensorWindow.this.constants.serverExchangeError());
							}
							@Override
							public void onSuccess(Device[] result) {
								System.out.println("ответ получен - обработка");
								// ответ получен, проверить на непустой ответ, если пустой - значит данные от удаленного модуля не получены
								if((result!=null)&&(result.length>0)){
									System.out.println("ответ получен - данные есть ");
									ModuleSensorWindow.this.timerWaitDevices.cancel();
									ModuleSensorWindow.this.fillPanelMain(result);
								}
							}
						});
					}
				};
				timerWaitDevices.scheduleRepeating(5000);
			}
		});
	}
	
	/** таймер, который опрашивает удаленные устройства на наличие данных */
	private Timer timerWaitDevices;
	
	
	private void closeWindow(){
		// прервать весь информационный обмен для таймеров
		if(this.timerWaitDevices!=null){
			this.timerWaitDevices.cancel();
		}
		// закрыть окно
		this.close();
	}
}
