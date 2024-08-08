package fenomen.server.controller.client.view.settings.module.thread_settings_window.settings_change;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.NameValuePair;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.PropertyGridPanel;
import com.gwtext.client.widgets.grid.event.PropertyGridPanelListener;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

import fenomen.server.controller.client.view.settings.module.thread_settings_window.ModuleSettingsManagerAsync;
import fenomen.server.controller.client.view.settings.module.thread_settings_window.ModuleSettingsWrap;

/** настройки ThreadAlarm модуля */
public class SettingsChange extends Panel{
	/** уникальный идентификатор в базе "module_settings_section.id"*/
	private int idSection=3;
	private ModuleSettingsManagerAsync managerSettings;
	private SettingsChangeConstants constants=GWT.create(SettingsChangeConstants.class);
	private int id;
	private PropertyGridPanel grid;
	/** данные, которые были прочитаны последний раз от сервера для данного модуля */
	private ModuleSettingsWrap[] lastData=null;
	
	
	/** настройки ThreadAlarm модуля
	 * @param title - заголовок для данного модуля 
	 * @param idSection - уникальный идентификатор для секции 
	 * <table>
	 * 	<tr><th>Id</th><th>Description</th></tr>
	 * 	<tr><td>1</td><td>HeartBeat</td></tr>
	 * 	<tr><td>2</td><td>Information</td></tr>
	 * 	<tr><td>3</td><td>Alarm</td></tr>
	 * 	<tr><td>4</td><td>Sensor</td></tr>
	 * </table>
	 * @param managerSettings - менеджер настроек
	 * @param id - уникальный идентификатор модуля
	 */
	public SettingsChange(String title, int idSection, ModuleSettingsManagerAsync managerSettings, int id){
		this.id=id;
		this.idSection=idSection;
		this.managerSettings=managerSettings;
		this.setTitle(title);
		this.addTool(new Tool(Tool.REFRESH,new Function(){
			@Override
			public void execute() {
				onButtonRefresh();
			}
		},constants.buttonRefreshTitle()));
		this.grid=new PropertyGridPanel();
		this.grid.setSorted(false);
		this.grid.setAutoWidth(true);
		this.grid.setAutoHeight(true);
		GridView view=new GridView();
		view.setForceFit(true);
		this.grid.setView(view);
		this.grid.addPropertyGridPanelListener(new PropertyGridPanelListener() {
			@Override
			public void onPropertyChange(PropertyGridPanel source, 
										 String recordID,
										 Object value, 
										 Object oldValue) {
				for(int counter=0;counter<SettingsChange.this.lastData.length;counter++){
					if(SettingsChange.this.lastData[counter].getParameterName().equals(recordID)){
						if(value==null){
							SettingsChange.this.lastData[counter].setSettingsValue(null);
						}else{
							SettingsChange.this.lastData[counter].setSettingsValue((String)value);
						}
						break;
					}
				}
			}
			@Override
			public boolean doBeforePropertyChange(PropertyGridPanel source,
					String recordID, Object value, Object oldValue) {
				return true;
			}
		});
		onButtonRefresh();
		
		this.setLayout(new BorderLayout());
		this.add(this.grid, new BorderLayoutData(RegionPosition.CENTER));
		
		Button buttonSave=new Button(constants.buttonSaveTitle());
		buttonSave.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonSave();
			}
		});
		
		HorizontalPanel panelManager=new HorizontalPanel();
		panelManager.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panelManager.add(buttonSave);
		
		BorderLayoutData southData=new BorderLayoutData(RegionPosition.NORTH);
		southData.setMinHeight(50);
		this.add(panelManager, southData);
	}

	/** обновить параметры в панели свойств */
	private void onButtonRefresh(){
		this.managerSettings.getListOfParameters(this.id, idSection, new AsyncCallback<ModuleSettingsWrap[]>(){
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert(SettingsChange.this.constants.serverExchangeError());
			}

			@Override
			public void onSuccess(ModuleSettingsWrap[] result) {
				SettingsChange.this.updateProperty(result);
			}
		});
	}
	
	/** сохранить приведенные параметры */
	private void onButtonSave(){
		// прочесть все данные в массив, и отправить на сервер 
		this.managerSettings.saveOrUpdateParameter(id, this.idSection, this.getModuleSettingsWrap(), new AsyncCallback<String>(){
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert(constants.serverExchangeError());
			}
			@Override
			public void onSuccess(String result) {
				MessageBox.alert(constants.saveOk());
				onButtonRefresh();
			}
		});
		 
	}
	
	/** прочесть все данные из PropertyGrid */
	private ModuleSettingsWrap[] getModuleSettingsWrap() {
		// не удалось добиться чтения <параметр>=<значение> из PropertyGridPanel   
		/*for(int counter=0;counter<this.lastData.length;counter++){
			// найти в объекте строку со значением
			for(int index=0;index<this.lastData.length;index++){
				this.grid.getView().get
				String controlParameterName=this.grid.getView().getCell(index, 0).getNodeValue();
				String controlParameterValue=this.grid.getView().getCell(index, 1).getNodeValue();
				if(controlParameterName.equals(this.lastData[counter].getParameterName())){
					this.lastData[counter].setSettingsValue(controlParameterValue);
					break;
				}
			}
		}*/
		return this.lastData;
	}
	/** обновить параметры в списке */
	private void updateProperty(ModuleSettingsWrap[] result){
		lastData=result;
		this.grid.setSource(getMapFromModuleSettingsWrap(result));
	}

	/** из переменных модуля сделать */
	private NameValuePair[] getMapFromModuleSettingsWrap(
			ModuleSettingsWrap[] result) {
		NameValuePair[] returnValue=new NameValuePair[result.length];
		for(int counter=0;counter<result.length;counter++){
			returnValue[counter]=new NameValuePair(result[counter].getParameterName(), result[counter].getSettingsValue());
		}
		return returnValue;
	}
}
