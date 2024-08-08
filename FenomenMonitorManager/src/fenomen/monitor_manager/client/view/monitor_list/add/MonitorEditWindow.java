package fenomen.monitor_manager.client.view.monitor_list.add;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.layout.VerticalLayout;

import fenomen.monitor_manager.client.view.monitor_list.MonitorList;
import fenomen.monitor_manager.client.view.monitor_list.MonitorManagerAsync;
import fenomen.monitor_manager.client.view.monitor_list.MonitorWrap;

/** окно для редактирования Monitor  */
public class MonitorEditWindow extends Window{
	private MonitorEditWindowConstants constants=GWT.create(MonitorEditWindowConstants.class);
	private TextBox login;
	private TextBox password;
	private TextBox description;
	private TextBox jabberUrl;
	private TextBox jabberPort;
	private TextBox jabberProxy;
	private TextBox jabberLogin;
	private TextBox jabberPassword;
	private MonitorList parent;
	private MonitorManagerAsync monitorManager;
	private MonitorWrap monitor;
	
	/** окно для редактирования Monitor  
	 * @param parent - окно, которое нужно оповестить о необходимости обновления списка 
	 * @param monitorManager - менеджер, который осуществляет информационный обмен с сервером 
	 * @param monitor монитор для редактирования, если null - добавление 
	 */
	public MonitorEditWindow(MonitorList parent, 
							 MonitorManagerAsync monitorManager,
							 MonitorWrap monitor){
		this.parent=parent;
		this.monitorManager=monitorManager;
		this.monitor=monitor;
		
		if(monitor==null){
			this.setTitle(constants.titleAdd());
		}else{
			this.setTitle(constants.titleEdit());
		}
		this.setHeight(450);
		this.setMinHeight(400);
		this.setWidth(190);
		this.setMinWidth(150);
		this.setPaddings(5);
		this.setResizable(false);
		// this.setButtonAlign(Position.CENTER);
		this.setModal(true);
		
		Panel panelManager=new Panel();
		// panelManager.setLayout(new HorizontalLayout(15));
		final Button buttonSave=new Button(constants.buttonSaveCaption());
		panelManager.add(buttonSave);
		final Button buttonCancel=new Button(constants.buttonCancelCaption());
		panelManager.add(buttonCancel);
		ClickHandler clickHandler=new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				if(event.getSource().equals(buttonSave)){
					onButtonSave();
				}
				if(event.getSource().equals(buttonCancel)){
					onButtonCancel();
				}
			}
		};
		buttonSave.addClickHandler(clickHandler);
		buttonCancel.addClickHandler(clickHandler);
		
		Panel panelInput=new Panel();
		panelInput.setLayout(new VerticalLayout(5));
		
		panelInput.add(new Label(constants.login()));
		login=new TextBox();
		panelInput.add(login);
		
		panelInput.add(new Label(constants.password()));
		password=new TextBox();
		panelInput.add(password);
		
		panelInput.add(new Label(constants.description()));
		description=new TextBox();
		panelInput.add(description);
		
		panelInput.add(new Label(constants.jabberUrl()));
		jabberUrl=new TextBox();
		panelInput.add(jabberUrl);
		
		panelInput.add(new Label(constants.jabberPort()));
		jabberPort=new TextBox();
		panelInput.add(jabberPort);
		
		panelInput.add(new Label(constants.jabberProxy()));
		jabberProxy=new TextBox();
		panelInput.add(jabberProxy);
		
		panelInput.add(new Label(constants.jabberLogin()));
		jabberLogin=new TextBox();
		panelInput.add(jabberLogin);
		
		panelInput.add(new Label(constants.jabberPassword()));
		jabberPassword=new TextBox();
		panelInput.add(jabberPassword);

		if(this.monitor!=null){
			this.login.setValue(this.monitor.getLogin());
			this.password.setValue(this.monitor.getPassword());
			this.description.setValue(this.monitor.getDescription());
			
			this.jabberUrl.setValue(this.monitor.getJabberUrl());
			if(this.monitor.getJabberPort()==null){
				this.jabberPort.setValue("5222");
			}else{
				this.jabberPort.setValue(this.monitor.getJabberPort().toString());
			}
			this.jabberProxy.setValue(this.monitor.getJabberProxy());
			this.jabberLogin.setValue(this.monitor.getJabberLogin());
			this.jabberPassword.setValue(this.monitor.getJabberPassword());
		}
		
		this.setLayout(new VerticalLayout());
		this.add(panelInput);
		this.add(panelManager);
		
		this.show();
	}
	

	private void onButtonCancel(){
		this.close();
	}

	/** проверка TextBox на пустое значение и отображение сообщения  */
	private boolean textBoxIsEmpty(TextBox textBox, String alertMessage){
		boolean returnValue=false;
		if((textBox.getValue()==null)||(textBox.getValue().trim().equals(""))){
			MessageBox.alert(alertMessage);
			returnValue=true;
		}
		return returnValue;
	}
	
	private void onButtonSave(){
		while(true){
			// проверки
			if(textBoxIsEmpty(this.login,constants.loginIsNull())){
				break;
			}
			if(textBoxIsEmpty(this.password,constants.passwordIsNull())){
				break;
			}
			if(textBoxIsEmpty(this.description,constants.descriptionIsNull())){
				break;
			}
			if(textBoxIsEmpty(this.jabberUrl,constants.jabberUrlIsNull())){
				break;
			}
			if(textBoxIsEmpty(this.jabberPort,constants.jabberPortIsNull())){
				break;
			}
			try{
				Integer.parseInt(this.jabberPort.getValue());
			}catch(Exception ex){
				MessageBox.alert(constants.jabberPortIsNan());
			}
			if(textBoxIsEmpty(this.jabberProxy,constants.jabberProxyIsNull())){
				break;
			}
			if(textBoxIsEmpty(this.jabberLogin,constants.jabberLoginIsNull())){
				break;
			}
			if(textBoxIsEmpty(this.jabberPassword,constants.jabberPasswordIsNull())){
				break;
			}
			
			AsyncCallback<String> callBack=new AsyncCallback<String>(){
				@Override
				public void onFailure(Throwable caught) {
					// ошибка обмена с сервером
					MessageBox.hide();
					MessageBox.alert(constants.error(), constants.serverExchangeError());
				}
				@Override
				public void onSuccess(String result) {
					MessageBox.hide();
					if(result!=null){
						// получен ошибочный результат и текст ошибки 
						MessageBox.alert(constants.error(), result);
					}else{
						// запуск обновления
						MonitorEditWindow.this.close();
						MonitorEditWindow.this.parent.updateTable();
					}
				}
			};
			if(this.monitor==null){
				// add
				MonitorWrap monitor=new MonitorWrap();
				monitor.setLogin(this.login.getValue());
				monitor.setPassword(this.password.getValue());
				monitor.setDescription(this.description.getValue());
				monitor.setJabberUrl(this.jabberUrl.getValue());
				monitor.setJabberPort(Integer.parseInt(this.jabberPort.getValue()));
				monitor.setJabberProxy(this.jabberProxy.getValue());
				monitor.setJabberLogin(this.jabberLogin.getValue());
				monitor.setJabberPassword(this.jabberPassword.getValue());
				this.showWaitWindow();
				this.monitorManager.addMonitor(monitor, callBack);
			}else{
				// edit
				monitor.setLogin(this.login.getValue());
				monitor.setPassword(this.password.getValue());
				monitor.setDescription(this.description.getValue());
				monitor.setJabberUrl(this.jabberUrl.getValue());
				monitor.setJabberPort(Integer.parseInt(this.jabberPort.getValue()));
				monitor.setJabberProxy(this.jabberProxy.getValue());
				monitor.setJabberLogin(this.jabberLogin.getValue());
				monitor.setJabberPassword(this.jabberPassword.getValue());
				this.showWaitWindow();
				this.monitorManager.editMonitor(monitor, callBack);
			}
			break;
		}
	}
	
	private void showWaitWindow(){
		MessageBox.show(new MessageBoxConfig(){
			{
				this.setWait(true);
				this.setWidth(300);
				this.setProgressText(constants.serverExchange());
				this.setWaitConfig(new WaitConfig(){
					{
						this.setInterval(200);
					}
				});
			}
		});
	}
}
