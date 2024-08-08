package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.layout.AnchorLayout;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import fenomen.server.controller.client.utility.INotifyClose;
import fenomen.server.controller.client.utility.IUpdateList;
import fenomen.server.controller.client.view.modules.IActionNotify;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.AddAlarmChecker;

/** панель, которая содержит список AlarmChecker-ов по Module.Sensor  */
public class RegisterAlarmCheckerPanel extends Panel implements INotifyClose, IUpdateList, IActionNotify{
	private AlarmCheckerConstants constants=GWT.create(AlarmCheckerConstants.class);
	private AlarmCheckerManagerAsync checkerManager=null;
	/** панель Checker-ов, которые зарегестрированы в модуле  */
	private AlarmCheckerTable panelOfCheckers;
	/** уникальный идентификатор модуля (Module.id)*/
	private int moduleId;
	/** уникальный идентификатор датчика в модуле */
	private int modbusAddress;
	/** уникальный ( в масштабе устройства ) адрес регистра */
	private int registerAddress;
	
	/** панель, которая содержит список AlarmChecker-ов по Module.Sensor  
	 * @param moduleId - уникальный идентификатор модуля в масштабе базы данных ( module.id )   
	 * @param modbusAddress - номер датчика/сенсора в сети Modbus 
	 * @param registerAddress - уникальный номер регистра в масштабе устройства ( которое, в свою очередь, находится на удаленном модуле )
	 */
	public RegisterAlarmCheckerPanel(int moduleId, int modbusAddress, int registerAddress){
		this.moduleId=moduleId;
		this.modbusAddress=modbusAddress;
		this.registerAddress=registerAddress;
		this.setTitle(constants.title());
		checkerManager=GWT.create(AlarmCheckerManager.class);
		// create components
		Button buttonReadAll=new Button(constants.buttonReadAllCaption());
		buttonReadAll.setEnabled(false);
		this.panelOfCheckers=new AlarmCheckerTable(constants,this);
		
		Button buttonAddChecker=new Button(constants.buttonAddChecker());
		//buttonAddChecker.setEnabled(false);
		
		// add listener's
		buttonReadAll.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonReadAll();
			}
		});
		buttonAddChecker.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonAddChecker();
			}
		});
		
		// placing component's
		Panel panelMainManager=new Panel();
		panelMainManager.setLayout(new AnchorLayout());
		panelMainManager.setHeight(30);
		panelMainManager.setWidth("100%");
		panelMainManager.add(buttonReadAll, new AnchorLayoutData("100%")); //new AnchorLayoutData("50%")
		
		Panel panelManager=new Panel();
		panelManager.setLayout(new HorizontalLayout(10));
		panelManager.setHeight(30);
		panelManager.add(buttonAddChecker);
		
		this.setLayout(new BorderLayout());
		this.add(panelMainManager, new BorderLayoutData(RegionPosition.NORTH));
		this.add(panelOfCheckers, new BorderLayoutData(RegionPosition.CENTER));
		this.add(panelManager, new BorderLayoutData(RegionPosition.SOUTH));
		// прочесть все AlarmChecker-ы из модуля
		this.updateList();
	}
	
	/** запрос на сопряжение сервера и удаленного модуля  */
	private void onButtonReadAll(){
		// TODO сделать сопряжение того что есть на модуле, и того что есть на сервере 
	}
	
	/** отобразить окно, которое предоставит интерфейс для добавления нового AlarmChecker */
	private void onButtonAddChecker(){
		AddAlarmChecker addChecker=new AddAlarmChecker(null,
													   this.constants.addAlarmChecker()+"  register #"+this.registerAddress,	
													   this.moduleId, 
											 		   this.modbusAddress,
											 		   this.registerAddress,
											 		   this.checkerManager,
											 		   this);
		addChecker.show();
	}
	
	@Override
	public void notifyClose() {
		// прерывание возможных информационных потоков, сигнализирование о закрытии данного окна родительским  
	}

	@Override
	public void updateList() {
		// необходимо получить список всех сенсоров
		MessageBox.show(new MessageBoxConfig(){
			{
				//this.setMsg();
				this.setProgressText(RegisterAlarmCheckerPanel.this.constants.getDataFromServer());
				this.setWait(true);
				this.setWaitConfig(new WaitConfig(){
					{
						this.setInterval(250);
					}
				});
			}
		});
		this.checkerManager.getListOfAlarmChecker(this.moduleId, 
												  this.modbusAddress,
												  new AsyncCallback<AlarmCheckerTableElement[]>(){
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.hide();
				MessageBox.alert(RegisterAlarmCheckerPanel.this.constants.error(), 
								 RegisterAlarmCheckerPanel.this.constants.serverExchangeError());
			}

			@Override
			public void onSuccess(AlarmCheckerTableElement[] result) {
				MessageBox.hide();
				if(result!=null){
					RegisterAlarmCheckerPanel.this.fillTable(result); 
				}else{
					MessageBox.alert(RegisterAlarmCheckerPanel.this.constants.error(), 
							 RegisterAlarmCheckerPanel.this.constants.serverExchangeError());
				}
			}
			
		});
	}
	
	/** наполнение таблицы текущими данными */
	private void fillTable(AlarmCheckerTableElement[] elements){
		this.panelOfCheckers.updateModel(elements);
	}

	@Override
	public void actionNotify(String actionName, Object parameter) {
		if(actionName.equals("remove")){
			Integer id=(Integer)parameter;
			MessageBox.wait(this.constants.waitServerExchange());
			this.checkerManager.removeAlarmChecker(this.moduleId, 
												   this.modbusAddress, 
												   this.registerAddress,
												   id, 
												   new AsyncCallback<String>(){
				@Override
				public void onFailure(Throwable caught) {
					MessageBox.hide();
					MessageBox.alert(RegisterAlarmCheckerPanel.this.constants.serverExchangeError());
				}

				@Override
				public void onSuccess(String result) {
					MessageBox.hide();
					RegisterAlarmCheckerPanel.this.updateList();
				}});
		};
		if(actionName.equals("edit")){
			Integer id=(Integer)parameter;
			AddAlarmChecker addChecker=new AddAlarmChecker(id,
														   this.constants.editAlarmChecker()+"  register #"+this.registerAddress,	
					   									   this.moduleId, 
					   									   this.modbusAddress,
					   									   this.registerAddress,
					   									   this.checkerManager,
					   									   this);
			addChecker.show();
			// Integer edit=(Integer)parameter;
			// TODO Контролер.редактирование установленных AlarmChecker-ов
		}
	}
}
