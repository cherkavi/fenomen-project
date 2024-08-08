package fenomen.server.controller.client.view.modules;

import com.google.gwt.core.client.GWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.HorizontalLayout;

import fenomen.server.controller.client.view.RootComposite;
import fenomen.server.controller.client.view.main_menu.MainMenu;
import fenomen.server.controller.client.view.modules.table.TableModules;
import fenomen.server.controller.client.view.modules.table.add_window.AddWindow;

/** панель со списком всех модулей в системе */
public class Modules extends Composite implements IUpdateNotify{
	private ModulesConstants constant=GWT.create(ModulesConstants.class);
	private TableModules tableModules;
	
	public Modules(){
		tableModules=new TableModules(this);
		tableModules.updateTable();
		
		// main panel 
		Panel panel=new Panel();
		panel.setWidth(RootComposite.getWindowWidth());
		panel.setHeight(RootComposite.getWindowHeight());
		panel.setBorder(true);
		panel.setPaddings(15);
		panel.setTitle(constant.titlePanel());
		panel.setLayout(new BorderLayout());
		
		// center panel 
		Panel panelCenter=new Panel();
		panelCenter.add(tableModules);
		panelCenter.setAutoScroll(true);
		BorderLayoutData panelCenterData=new BorderLayoutData(RegionPosition.CENTER);
		
		// south panel 
		Panel panelManager=new Panel();
		HorizontalLayout panelManagerLayout=new HorizontalLayout(10);
		panelManagerLayout.setSpacing("5px");
		panelManager.setLayout(panelManagerLayout);
		panelManager.setPaddings(5, 5, 5, 5);
		//panelManager.setAutoHeight(true);
		panelManager.setHeight(40);
		Button buttonClose=new Button(constant.buttonClose());
		buttonClose.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onButtonClose();
			}
		});
		
		Button buttonAdd=new Button(constant.buttonAdd());
		buttonAdd.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonAdd(event);
			}
		});

		panelManager.add(buttonAdd);
		panelManager.add(buttonClose);
		
		BorderLayoutData panelManagerData=new BorderLayoutData(RegionPosition.SOUTH);
		panelManagerData.setSplit(false);
		//panelManagerData.setMinSize(30);
		//panelManagerData.setMaxSize(50);
		
		// placing components
		panel.add(panelCenter,panelCenterData);
		panel.add(panelManager,panelManagerData);
		// new Viewport(panel);
		initWidget(panel);
	}
	
	private void onButtonClose(){
		RootComposite.showView(new MainMenu());
	}
	
	/** добавить элемент */
	private void onButtonAdd(ClickEvent event){
		AddWindow addWindow=new AddWindow(this, this.tableModules.getModulesManager());
		addWindow.show(event.getRelativeElement().getId());
	}

	@Override
	public void needUpdate() {
		this.tableModules.updateTable();
	}
}
