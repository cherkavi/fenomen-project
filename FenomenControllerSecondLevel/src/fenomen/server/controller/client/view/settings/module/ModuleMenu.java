package fenomen.server.controller.client.view.settings.module;

import com.google.gwt.core.client.GWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.gwtext.client.core.Function;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.layout.VerticalLayout;

import fenomen.server.controller.client.view.RootComposite;
import fenomen.server.controller.client.view.settings.ModuleChoice;
import fenomen.server.controller.client.view.settings.module.sensor_window.ModuleSensorWindow;
import fenomen.server.controller.client.view.settings.module.thread_settings_window.ModuleSettingsWindow;


/** основное меню для настроек модуля модуля
 * <li> <b>Настройка рабочих потоков</b> - переменные по установке данных для рабочих потоков </li>
 * <li> <b>Настройка Датчиков</b> - все датчики на удаленном модуле </li>
 * */
public class ModuleMenu extends Composite{
	private ModuleConstants constants=GWT.create(ModuleConstants.class);
	/** уникальный идентификатор модуля */
	private int id;
	/** основное меню для модуля 
	 * @param title - заголовок для отображения
	 * @param id - уникальный идентификатор модуля 
	 */
	public ModuleMenu(String title, int id){
		this.id=id;
		Panel panel=new Panel();
		panel.setTitle(title);
		panel.addTool(new Tool(Tool.CLOSE, new Function(){
			@Override
			public void execute() {
				onButtonClose();
			}
		},constants.buttonCloseTooltip()));
		panel.setFrame(true);
		panel.setPaddings(5);
		
		VerticalLayout verticalLayout=new VerticalLayout(10);
		verticalLayout.setColumns(1);
		panel.setLayout(verticalLayout);
		// panel.setSize(RootComposite.getWindowWidth(), RootComposite.getWindowHeight());
		
		Button buttonSettings=new Button(constants.buttonSettingsCaption());
		buttonSettings.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonSettings();
			}
		});
		panel.add(buttonSettings);
		buttonSettings.setWidth("200px");
		
		Button buttonSensor=new Button(constants.buttonSensorCaption());
		buttonSensor.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonSensor();
			}
		});
		panel.add(buttonSensor);
		buttonSensor.setWidth("200px");
		
		this.initWidget(panel);
	}
	
	private void onButtonClose(){
		if(moduleSettingsWindow!=null){
			try{
				moduleSettingsWindow.close();
				moduleSettingsWindow=null;
			}catch(Exception ex){};
		}
		if(moduleSensorWindow!=null){
			try{
				moduleSensorWindow.close();
				moduleSensorWindow=null;
			}catch(Exception ex){};
		}
		RootComposite.showView(new ModuleChoice());
	}
	
	private ModuleSettingsWindow moduleSettingsWindow;
	private ModuleSensorWindow moduleSensorWindow;
	
	/** нажатие на кнопку Settings */
	private void onButtonSettings(){
		if(moduleSettingsWindow!=null){
			try{
				moduleSettingsWindow.close();
				moduleSettingsWindow=null;
			}catch(Exception ex){};
		}
		this.moduleSettingsWindow=new ModuleSettingsWindow(this.id);
		this.moduleSettingsWindow.show();
	}
	
	/** нажатие на кнопку Sensor*/
	private void onButtonSensor(){
		if(moduleSensorWindow!=null){
			try{
				moduleSensorWindow.close();
				moduleSensorWindow=null;
			}catch(Exception ex){};
		}
		this.moduleSensorWindow=new ModuleSensorWindow(this.id);
		this.moduleSensorWindow.show();
	}
	
}
