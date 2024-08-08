package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

import fenomen.server.controller.client.utility.IUpdateList;
import fenomen.server.controller.client.view.RootComposite;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.WrapChecker;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.AlarmCheckerManagerAsync;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.panel_create.IValuePresentationListener;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.panel_create.PanelValueCondition;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.panel_create.PanelValuePresentation;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.value_checker.CheckValueForType;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.value_checker.DoubleCheckValue;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.value_checker.IntegerCheckValue;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.value_checker.StringCheckValue;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.value_checker.ZeroOrOneCheckValue;

/** окно, которое содержит интерфейс дл€ создани€ Checker-а по датчику */
public class AddAlarmChecker extends Window implements IValuePresentationListener {
	private AddAlarmCheckerConstants constants=GWT.create(AddAlarmCheckerConstants.class);
	/** уникальный идентификатор редактируемого значени€ */
	private Integer idEdit;
	/** текст дл€ получени€  */
	private TextField textForClient;
	/** текст дл€ описани€ текущего AlarmChecker-a */
	private TextField textForDescription;
	/** таблица, котора€ содержит варианты приведени€ строкового значени€ */
	private PanelValuePresentation panelValuePresentation;
	/** таблица, котора€ содержит варианты условий */
	private PanelValueCondition panelValueCondition;
	/** уникальный идентификатор модул€ в масштабе базы данных */
	private int idModule;
	/** адрес устройства в сети ModBus*/
	private int modbusAddress;
	/** регистр на устройстве */
	private int registerAddress;
	/** управл€ющий AlarmChecker-ами */
	private AlarmCheckerManagerAsync checkerManager;
	/** объект, который нужно оповещать о необходимости обновлени€ данных */
	private IUpdateList updateNotify;
	
	/**
	 * интерфейс дл€ добавлени€ AlarmChecker-a
	 * @param id - уникальный идентификатор дл€ редактировани€
	 * 	<ul>
	 * 		<li><b>null</b> - добавить </li>
	 * 		<li><b>value</b> - редактировать </li>
	 * 	</ul>
	 * @param title - заголовок, который следует отобразить 
	 * @param idModule - уникальный идентификатор модул€ в масштабе базы данных  
	 * @param modbusAddress - адрес устройства в сети Modbus
	 * @param registerAddress  - адрес регистра на устройстве 
	 * @param checkerManager - управл€ющий AlarmChecker-ами на стороне сервера 
	 */
	public AddAlarmChecker(Integer id,
						   String title, 
						   int idModule, 
						   int modbusAddress,
						   int registerAddress,
						   AlarmCheckerManagerAsync checkerManager, 
						   IUpdateList updateNotify){
		this.idEdit=id;
		this.updateNotify=updateNotify;
		this.idModule=idModule;
		this.modbusAddress=modbusAddress;
		this.registerAddress=registerAddress;
		this.updateNotify=updateNotify;
		this.checkerManager=checkerManager;
		this.setTitle(title);
		this.setModal(true);
		this.setClosable(false);
		this.setSize( (int)(RootComposite.getWindowWidth()/1.2), (int)(RootComposite.getWindowHeight()/1.4));
		this.addTool(new Tool(Tool.CLOSE,new Function(){
			@Override
			public void execute() {
				AddAlarmChecker.this.close();
			}
		},constants.buttonCloseHint()));
		// throw Exception
		// this.center();
		this.initComponents();
	}
	
	private void initComponents(){
		// create component's
			// NorthPanel
		VerticalPanel northPanel=new VerticalPanel();
		northPanel.setBorderWidth(1);
		Label labelAlertMessageForClient=new Label(constants.captionAlertForUserMessage());
		labelAlertMessageForClient.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		labelAlertMessageForClient.setWidth("100%");
		textForClient=new TextField();
		textForClient.setWidth("100%");
		northPanel.add(labelAlertMessageForClient);
		northPanel.add(textForClient);
		northPanel.setWidth("100%");
			// SouthPanel. Description for Server
		VerticalPanel panelMessageDescription=new VerticalPanel();
		panelMessageDescription.setWidth("100%");
		Label labelAlertDescription=new Label(constants.captionAlertForDescription());
		labelAlertDescription.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		textForDescription=new TextField();
		textForDescription.setWidth("100%");
		panelMessageDescription.add(labelAlertDescription);
		panelMessageDescription.add(textForDescription);
			// SouthPanel. Manager
		HorizontalPanel panelManager=new HorizontalPanel();
		panelManager.setWidth("100%");
		Button buttonSave=new Button(constants.captionButtonSave());
		buttonSave.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonSave();
			}
		});
		buttonSave.setWidth("90%");
		
		Button buttonCancel=new Button(constants.captionButtonCancel());
		buttonCancel.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonCancel();
			}
		});
		buttonCancel.setWidth("60%");
		panelManager.add(buttonSave);
		panelManager.add(buttonCancel);
			// SouthPanel
		VerticalPanel southPanel=new VerticalPanel();
		southPanel.setBorderWidth(1);
		southPanel.add(panelMessageDescription);
		southPanel.add(panelManager);
		southPanel.setWidth("100%");
		
		// placing component's
		HorizontalSplitPanel centerPanel=new HorizontalSplitPanel();
		centerPanel.setSize("100%", "100%");

		panelValuePresentation=new PanelValuePresentation("presentation",presentationValues,-1);
		panelValuePresentation.addListener(this);
		centerPanel.setLeftWidget(panelValuePresentation);
		/** таблица, котора€ содержит варианты условий */
		panelValueCondition=new PanelValueCondition("condition",conditionValues);
		centerPanel.setRightWidget(panelValueCondition);
		centerPanel.setSplitPosition("30%");
		
		this.setLayout(new BorderLayout());
		this.add(northPanel,new BorderLayoutData(RegionPosition.NORTH));
		this.add(centerPanel,new BorderLayoutData(RegionPosition.CENTER));
		this.add(southPanel,new BorderLayoutData(RegionPosition.SOUTH));
		
		if(this.idEdit!=null){
			// загрузить данные в визуальные компоненты
			loadToVisualComponent(idEdit);
		}
	}

	/** загрузить в визуальные компоненты Checker  */
	private void loadToVisualComponent(Integer id){
		MessageBox.show(new MessageBoxConfig(){
			{
				this.setProgressText(AddAlarmChecker.this.constants.waitMessage());
				this.setWait(true);
				this.setWaitConfig(new WaitConfig(){
					{
						this.setInterval(200);
					}
				});
			}
		});
		this.checkerManager.getAlarmChecker(id, new AsyncCallback<WrapChecker>(){
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.hide();
				MessageBox.alert(AddAlarmChecker.this.constants.serverExchangeError());
			}

			@Override
			public void onSuccess(WrapChecker result) {
				MessageBox.hide();
				if(result!=null){
					// result.getIdModule();
					// result.getModbusAddress();
					// result.getRegisterAddress();
					
					AddAlarmChecker.this.textForDescription.setValue(result.getAlarmDescription());
					AddAlarmChecker.this.textForClient.setValue(result.getAlarmMessage());
					AddAlarmChecker.this.panelValuePresentation.setSelectedName(result.getValuePresentation());// panel Presentation
					AddAlarmChecker.this.panelValueCondition.setSelectedCondition(result.getValueCondition());// panel Condition
					AddAlarmChecker.this.panelValueCondition.setControlValue(result.getControlValue());// panel Condition
					AddAlarmChecker.this.panelValueCondition.setTimeDelay(result.getTimeDelay());// panel Condition
				}else{
					MessageBox.alert(AddAlarmChecker.this.constants.serverExchangeError());
				}
			}
		});
	}
	
	
	String[] presentationValues=new String[]{AlarmCheckerManagerAsync.presentationInteger,AlarmCheckerManagerAsync.presentationDouble,AlarmCheckerManagerAsync.presentationZeroOne, AlarmCheckerManagerAsync.presentationString};
	//String[] presentationCaptions=new String[]{"integer","double","0 or 1", "string"};
	String[] conditionValues=new String[]{AlarmCheckerManagerAsync.conditionEQ,AlarmCheckerManagerAsync.conditionNE,AlarmCheckerManagerAsync.conditionGT,AlarmCheckerManagerAsync.conditionGE,AlarmCheckerManagerAsync.conditionLT,AlarmCheckerManagerAsync.conditionLE};
	//String[] conditionCaptions=new String[]{"==","!=",">",">=","<","<="};
	
	int[][] indexes=new int[][]{{0,1,2,3,4,5,6}, 
								{0,1,2,3,4,5,6},
								{0,1},
								{0,1}};
	CheckValueForType[] checkerValue=new CheckValueForType[]{new IntegerCheckValue(), 
														     new DoubleCheckValue(), 
														     new ZeroOrOneCheckValue(), 
														     new StringCheckValue()};
	/** реакци€ на нажатие кнопки —охранить  */
	private void onButtonSave(){
		// перебрать возможные варианты 
		String lessData=constants.lessData();
		String errorConversion=constants.errorConversion();
		String needForType=constants.needForType();
		String needForTimeRepeat=constants.needForTimeRepeat();
		String inputAlarmMessage=constants.inputAlarmMessage();
		String choicePresentationValue=constants.choicePresentationValue();
		String choiceConditionValue=constants.choiceConditionValue();
		
		final String wait=constants.waitMessage();
		final String saving=constants.saving();
		while(true){
			// проверить введенные данные (проверить )
			if(this.textForClient.getText().trim().equals("")){
				MessageBox.alert(lessData,inputAlarmMessage);
				break;
			}
			if((this.panelValuePresentation.getSelectedName()==null)||(this.panelValuePresentation.getSelectedName().trim().equals(""))){
				MessageBox.alert(lessData,choicePresentationValue);
				break;
			}
			if((this.panelValueCondition.getSelectedCondition()==null)||(this.panelValueCondition.getSelectedCondition().trim().equals(""))){
				MessageBox.alert(lessData,choiceConditionValue);
				break;
			}
			int index=this.positionInArray(this.presentationValues, this.panelValuePresentation.getSelectedName());
			if(checkerValue[index].isType(this.panelValueCondition.getControlValue())==false){
				this.panelValueCondition.setFocusToControlValue();
				MessageBox.alert(errorConversion,(checkerValue[index].getDescription()));
				break;
			}
			
			if(this.textForDescription.getText().trim().equals("")){
				MessageBox.alert(lessData,needForType);
				break;
			}
			
			if((this.panelValueCondition.getTimeDelay()==null)||(this.panelValueCondition.getTimeDelay().trim().equals(""))){
				MessageBox.alert(errorConversion, needForTimeRepeat);
				break;
			}
			try{
				Integer.parseInt(this.panelValueCondition.getTimeDelay());
			}catch(Exception ex){
				MessageBox.alert(errorConversion, needForTimeRepeat);
				break;
			}
			// послать на сервер запрос с данными на добавление подобного Alarm-Checker-а
			/* System.out.println("AlarmMessage: "+this.textForClient.getText());
			System.out.println("Description: "+ this.textForDescription.getText());
			System.out.println("Presentation: "+this.panelValuePresentation.getSelectedName());
			System.out.println("Condition: "+this.panelValueCondition.getSelected());
			System.out.println("ControlValue: "+this.panelValueCondition.getControlValue());
			System.out.println("TimeDelay:"+this.panelValueCondition.getTimeDelay());*/
			// послать на сервер команду добавлени€ AlarmChecker-а
			this.checkerManager.addAlarmChecker(new WrapChecker(this.idEdit,
																this.idModule, 
																this.modbusAddress, 
																this.registerAddress,
																this.textForDescription.getText(), 
																this.textForClient.getText(), 
																this.panelValuePresentation.getSelectedName(), 
																this.panelValueCondition.getSelectedCondition(), 
																this.panelValueCondition.getControlValue(), 
																Integer.parseInt(this.panelValueCondition.getTimeDelay())), 
												new AsyncCallback<String>() {
													@Override
													public void onFailure(Throwable caught) {
														MessageBox.hide();
														MessageBox.alert("Server Exchange Error");
													}
													@Override
													public void onSuccess(String result) {
														MessageBox.hide();
														if(result==null){
															// послать родительскому объекту оповещение о необходимости обновлени€ измененнных данных
															AddAlarmChecker.this.updateNotify.updateList();
															AddAlarmChecker.this.close();
														}else{
															MessageBox.alert("Save Error",result);
														}
													}
												});
			// отображение панели задержки времени 
			MessageBox.show(new MessageBoxConfig(){
				{
					this.setMsg(wait);
					this.setProgressText(saving);
					this.setWidth(300);
					this.setWait(true);
					this.setWaitConfig(new WaitConfig(){
						{
							this.setInterval(200);
						}
					});
				}
			});
			break;
		}
	}
	
	/** реакци€ на нажатие кнопки ќтменить  */
	private void onButtonCancel(){
		this.close();
	}

	@Override
	public void onValueSelected(String name) {
		// System.out.println("Name: "+name);
		int position=this.positionInArray(this.presentationValues, name);
		if(position>=0){
			this.panelValueCondition.setDisabledAll();
			this.panelValueCondition.setEnabled(indexes[position]);
		}else{
			System.err.println("AddChecker#onValueSelected name was not found:"+name);
		}
		
	}
	
	/** получить позицию в массиве */
	private int positionInArray(String[] array, String value){
		for(int counter=0;counter<array.length;counter++){
			if(array[counter].equals(value)){
				return counter;
			}
		}
		return -1;
	}
	
}
