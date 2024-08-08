package fenomen.server.controller.client.view.settings.module.thread_settings_window;

import com.google.gwt.core.client.GWT;


import com.gwtext.client.core.Function;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.AccordionLayout;

import fenomen.server.controller.client.view.RootComposite;
import fenomen.server.controller.client.view.settings.module.thread_settings_window.settings_change.SettingsChange;

/** окно модуля, отображающее настройки потоков 
 * <li>HeartBeat</li>
 * <li>Information</li>
 * <li>Alarm</li> 
 * <li>Sensor</li> 
 * */
public class ModuleSettingsWindow extends Window{
	/** текстовые переменные */
	private ModuleSettingsWindowConstants constants=GWT.create(ModuleSettingsWindowConstants.class);
	/** сервис на сервере по изменению настроек на удаленном модуле */
	private ModuleSettingsManagerAsync managerSettings=GWT.create(ModuleSettingsManager.class);
	/** уникальный идентификатор модуля */
	private int id;
	
	/** окно модуля, отображающее настройки потоков HeartBeat, Information, Alarm, Sensor 
	 * @param id - уникальный идентификатор модуля 
	 */
	public ModuleSettingsWindow(int id){
		this.id=id;
		this.setModal(false);
		this.setTitle(constants.windowTitle());
		this.setClosable(false);
		this.addTool(new Tool(Tool.CLOSE,new Function(){
			@Override
			public void execute() {
				ModuleSettingsWindow.this.close();
			}
		},constants.buttonCloseTooltip()));
		this.setResizable(true);
		AccordionLayout accordionLayout=new AccordionLayout();
		this.setLayout(accordionLayout);
		this.setSize((int)(RootComposite.getWindowWidth()/1.5), (int)(RootComposite.getWindowHeight()/1.5));
		
		this.add(new SettingsChange("HeartBeat",1,this.managerSettings, this.id));
		this.add(new SettingsChange("Information",2,this.managerSettings, this.id));
		this.add(new SettingsChange("Alarm",3,this.managerSettings, this.id));
		this.add(new SettingsChange("Sensor",4,this.managerSettings, this.id));
	}
	
	
}
