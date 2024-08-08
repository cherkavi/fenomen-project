package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.information_checker_panel;

import com.google.gwt.core.client.GWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.AnchorLayout;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import fenomen.server.controller.client.utility.INotifyClose;

@SuppressWarnings("unused")
public class InformationChecker extends Panel implements INotifyClose{
	private InformationCheckerConstants constants=GWT.create(InformationCheckerConstants.class);
	/** панель Checker-ов, которые зарегестрированы в модуле  */
	private Panel panelOfCheckers;
	/** уникальный идентификатор модуля */
	private int id;
	/** уникальный идентификатор датчика в модуле */
	private int idSensor;
	
	public InformationChecker(int id, int idSensor){
		this.id=id;
		this.idSensor=idSensor;
		this.setTitle(constants.title());
		// create components
		Button buttonReadAll=new Button(constants.buttonReadAllCaption());
		this.panelOfCheckers=new Panel();
		this.readAllCheckers();
		
		// add listener's
		buttonReadAll.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonReadAll();
			}
		});
		
		// placing component's
		Panel panelManager=new Panel();
		panelManager.setLayout(new AnchorLayout());
		panelManager.setHeight(30);
		panelManager.setWidth("100%");
		panelManager.add(buttonReadAll, new AnchorLayoutData("50%"));
		
		this.setLayout(new BorderLayout());
		this.add(panelManager, new BorderLayoutData(RegionPosition.NORTH));
		this.add(panelOfCheckers, new BorderLayoutData(RegionPosition.CENTER));
		// TODO прочесть все AlarmChecker-ы из модуля
		try{
			panelManager.getEl().center();
		}catch(Exception ex){};
	}
	
	/** прочесть все */
	private void onButtonReadAll(){
		// TODO прочесть все информационные Checker-ы из модуля
	}
	
	private void readAllCheckers(){
		// прочесть все информационные Checke-ы из модуля 
	}

	@Override
	public void notifyClose() {
		// прерывание продолжительных процессов 
	}
}
