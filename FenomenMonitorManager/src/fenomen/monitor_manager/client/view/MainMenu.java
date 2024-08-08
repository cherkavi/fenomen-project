package fenomen.monitor_manager.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.ColumnLayout;

import fenomen.monitor_manager.client.utility.RootComposite;
import fenomen.monitor_manager.client.view.monitor_list.MonitorList;

public class MainMenu extends Composite{
	private MainMenuConstants constants=GWT.create(MainMenuConstants.class);
	
	public MainMenu(){
		Panel panel=new Panel();
		panel.setTitle(constants.title());
		panel.setBorder(true);
		panel.setBodyStyle("border-style:solid; border-color:#99BBE8;border-width:2px;border-top-width:0px;");
		panel.setWidth(300);
		panel.setHeight(200);
		panel.setLayout(new ColumnLayout());
		
		Button button=new Button(constants.buttonEditMonitor());
		button.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonClick();
			}
		});
		HorizontalPanel horizontalPanel=new HorizontalPanel();
		//horizontalPanel.setBorderWidth(1);
		horizontalPanel.setWidth("99%");
		horizontalPanel.setHeight("100%");
		panel.add(horizontalPanel);
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.add(button);
		
		this.initWidget(panel);
	}
	
	private void onButtonClick(){
		RootComposite.showView(new MonitorList());
	}
}
