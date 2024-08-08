package fenomen.server.controller.client.view.main_menu;

import com.google.gwt.core.client.GWT;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import fenomen.server.controller.client.view.RootComposite;
import fenomen.server.controller.client.view.modules.Modules;
import fenomen.server.controller.client.view.settings.ModuleChoice;

public class MainMenu extends Composite{
	private MainMenuConstants constants=GWT.create(MainMenuConstants.class);
	public MainMenu(){
		// create element's
		Button buttonModules=new Button(constants.titleButton());
		Button buttonSettings=new Button(constants.titleButtonSettings());
		// add listener's
		buttonModules.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				RootComposite.showView(new Modules());
			}
		});
		buttonSettings.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				RootComposite.showView(new ModuleChoice());
				// RootComposite.showView(new Test());
			}
		});
		// placing
		VerticalPanel panel=new VerticalPanel();
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setSpacing(5);
		panel.setTitle(constants.titlePanel());
		panel.add(buttonModules);
		panel.add(buttonSettings);
		this.initWidget(panel);
	}
}
