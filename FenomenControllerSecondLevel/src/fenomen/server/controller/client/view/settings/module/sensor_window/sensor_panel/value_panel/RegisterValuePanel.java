package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.value_panel;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

import fenomen.server.controller.client.utility.INotifyClose;
import fenomen.server.controller.client.view.settings.module.sensor_window.Device;
import fenomen.server.controller.client.view.settings.module.sensor_window.DeviceRegister;
import fenomen.server.controller.client.view.settings.module.sensor_window.SensorManagerAsync;

/** панель, отображающая значение регистра на устройстве, на удаленном модуле */
public class RegisterValuePanel extends Panel implements INotifyClose{
	private ValuePanelConstants constant=GWT.create(ValuePanelConstants.class);
	/** уникальный идентификатор модуля в масштабе базы данных */
	private int moduleId;
	/** уникальный идентификатор датчика-сенсора на панели  */
	private int modbusAddress;
	/** адрес регистра по данному датчику */
	private int registerAddress;
	/** дата установки датчика */
	private Label dateTime=new Label("");
	/** значение датчика */
	private TextField value=new TextField();
	/** менеджер по получению значений датчиков/сенсоров */
	private SensorManagerAsync sensorManager;
	/** кнопка по получению последнего значения от сервера */
	private Button buttonGet=null;
	/** таймер, который опрашивает сервер на наличие нового значения от модуля.датчика*/
	private Timer timer=null;
	/** последнее значение даты полученного значения */
	private Date registerDateWrite=null;
	
	/** панель, отображающая значение данного датчика 
	 * @param moduleId - уникальный идентификатор модуля в масштабе базы данных
	 * @param modbusAddress - уникальный адрес устройства в сети Modbus на модуле
	 * @param sensorManager - управляющий устройствами на модуле  
	 * */
	public RegisterValuePanel(int moduleId, 
					  		  int modbusAddress,
					  		  DeviceRegister register,
					  		  SensorManagerAsync sensorManager){
		this.sensorManager=sensorManager;
		this.moduleId=moduleId;
		this.modbusAddress=modbusAddress;
		this.registerAddress=register.getRegisterAddress();
		this.setLayout(new BorderLayout());
		this.setCollapsible(false);
		this.setTitle(constant.title());

		Label labelSensor=new Label(constant.labelSensorValue());
		Label labelDateTime=new Label(constant.labelDateValue());
		Button buttonSet=new Button(constant.labelButtonSet());
		buttonSet.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonSet();
			}
		});
		buttonGet=new Button(constant.labelButtonGet());
		buttonGet.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event){
				onButtonGet();
			}
		});

		FlexTable layout=new FlexTable();
		FlexCellFormatter cellFormatter=layout.getFlexCellFormatter();

		layout.setWidget(0, 0, labelSensor);
		cellFormatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		layout.setWidget(0, 1,labelDateTime);
		cellFormatter.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		layout.setWidget(1,0,value);
		cellFormatter.setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
		layout.setWidget(1,1,dateTime);
		cellFormatter.setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_CENTER);
		cellFormatter.setColSpan(2, 0, 3);
		layout.setWidget(2,0, buttonSet);
		cellFormatter.setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
		
		layout.setWidget(0, 2, buttonGet);
		cellFormatter.setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_CENTER);
		cellFormatter.setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_MIDDLE);
		cellFormatter.setRowSpan(0, 2, 3);

		layout.setWidth("300px");
		layout.setHeight("120px");
		
		HorizontalPanel panelMain=new HorizontalPanel();
		panelMain.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panelMain.add(layout);
		this.add(panelMain, new BorderLayoutData(RegionPosition.CENTER));

		// установить текущее значение в поля 
		this.registerDateWrite=register.getRegisterValueDateTime();
		try{
			dateTime.setText(RegisterValuePanel.this.registerDateWrite.toString());
		}catch(Exception ex){};
		value.setValue(Integer.toString(register.getRegisterValue()));
	}
	
	/** получено очередное значение от модуля  */
	private AsyncCallback<Device> getSensorDataFromServer=new AsyncCallback<Device>(){
		@Override
		public void onFailure(Throwable caught) {
			// ошибка при очередной попытке получения значения от сервера 
			timer.cancel();
			buttonGet.setEnabled(true);
			MessageBox.alert(RegisterValuePanel.this.constant.serverError());
		}

		@Override
		public void onSuccess(Device result) {
			if(result==null){
				timer.cancel();
				buttonGet.setEnabled(true);
				MessageBox.alert(RegisterValuePanel.this.constant.serverError());
			}else{
				for(int counter=0;counter<result.getListOfRegister().size();counter++){
					if(result.getListOfRegister().get(counter).getRegisterAddress()==RegisterValuePanel.this.registerAddress){
						// датчик найден, проверка даты
						if(result.getListOfRegister().get(counter).getRegisterValueDateTime().after(RegisterValuePanel.this.registerDateWrite)){
							// данные от модуля успешно получены
							timer.cancel();
							buttonGet.setEnabled(true);
							RegisterValuePanel.this.value.setValue(Integer.toString(result.getListOfRegister().get(counter).getRegisterValue()));
							RegisterValuePanel.this.registerDateWrite=result.getListOfRegister().get(counter).getRegisterValueDateTime();
							RegisterValuePanel.this.dateTime.setText(result.getListOfRegister().get(counter).getRegisterValueDateTime().toString());
						}else{
							// удаленный модуль не ответил
						}
						break;
					}
				}
			}
		}
	};
	
	private void onButtonGet(){
		this.buttonGet.setEnabled(false);
		// послать Task на сервер о необходимости получения последнего значения от модуля.датчика
		this.sensorManager.setTaskGetValueFromModule(this.moduleId, 
													 this.modbusAddress, 
													 new AsyncCallback<Boolean>(){
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.hide();
				MessageBox.alert(constant.serverError());
				RegisterValuePanel.this.buttonGet.setEnabled(true);
			}

			@Override
			public void onSuccess(Boolean result) {
				MessageBox.hide();
				if(result==true){
					// задача успешно установлена
					if(timer!=null){
						timer.cancel();
						timer=null;
					}
					// создать таймер, который будет постоянно опрашивать удаленный сервер на наличие нового значения от модуля 
					timer=new Timer(){
						@Override
						public void run() {
							RegisterValuePanel.this.sensorManager.getSensorIntoModule(RegisterValuePanel.this.moduleId, 
																					  RegisterValuePanel.this.modbusAddress, 
																					  getSensorDataFromServer);
						}
					};
					MessageBox.alert(constant.taskWasSended());
					timer.scheduleRepeating(5000);
				}else{
					MessageBox.alert("Server Database Error", constant.serverError());
				}
			}
		});
		MessageBox.show(new MessageBoxConfig(){
			{
				this.setWait(true);
				this.setWidth(200);
				this.setProgressText(RegisterValuePanel.this.constant.serverExchange());
				this.setWaitConfig(new WaitConfig(){
					{
						this.setInterval(200);
					}
				});
			}
		});
	}
	
	/** послать значение на удаленный модуль ( установить задачу )
	 * @param register - уникальный номер регистра, в который нужно установить значение 
	 * @param value - числовое значение, которое нужно установить в регистр 
	 */
	private void sendValueToServer(int register, int value){
		this.sensorManager.setTaskPutValueToModuleToRegister(this.moduleId, 
												   			 this.modbusAddress,
												   			 register,
												   			 value, 
												   			 null, // INFO Controller2 возможность Device Enabled/Disabled
												   			 new AsyncCallback<Boolean>(){
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert(constant.serverError());
			}

			@Override
			public void onSuccess(Boolean result) {
				if(result==true){
					// возможно нужно запустить поток опроса датчика на получение значения от сервера
					onButtonGet();
				}else{
					MessageBox.alert("Server Database Error", constant.serverError());
				}
			}
		});
	}
	
	private void onButtonSet(){
		MessageBox.confirm(constant.confirmSetValueTitle(), 
						   this.value.getText(),
						   new MessageBox.ConfirmCallback() {
								@Override
								public void execute(String btnID) {
									if(btnID.equalsIgnoreCase("yes")){
										sendValueToServer(RegisterValuePanel.this.registerAddress,Integer.parseInt(RegisterValuePanel.this.value.getText()));
										value.setValue("");
									}
								}
							});
	}

	@Override
	public void notifyClose() {
		this.timer.cancel();
	}
}
