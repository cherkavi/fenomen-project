package fenomen.server.controller.client.view.settings.filter_window;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;

import fenomen.server.controller.client.view.settings.IFindModule;

/** окно фильтра для модулей  */
public class FilterModuleWindow extends Window{
	private IFindModule findModule;
	private FilterModuleWindowConstants constants=GWT.create(FilterModuleWindowConstants.class);
	private TextField textIdModule;
	private TextField textAddress;
	
	/** окно фильтра для поиска модулей */
	public FilterModuleWindow(IFindModule findModule){
		this.findModule=findModule;
		this.initComponents();
	}
	
	private void initComponents(){
		this.setTitle(constants.title());
		this.setModal(true);
		this.setWidth(240);
		this.setHeight(160);
		this.setLayout(new VerticalLayout(15));
		
		VerticalPanel filter=new VerticalPanel();
		filter.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.add(filter);
		
		filter.add(new HTML(constants.titleIdModule()));
		textIdModule=new TextField();
		filter.add(textIdModule);
		
		filter.add(new HTML(constants.titleAddress()));
		textAddress=new TextField();
		filter.add(textAddress);
		
		Panel panelManager=new Panel();
		panelManager.setLayoutData(new HorizontalLayout(10));
		Button buttonFilter=new Button(constants.titleButtonFilter());
		buttonFilter.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonFilter();
			}
		});
		panelManager.add(buttonFilter);
		
		Button buttonCancel=new Button(constants.titleButtonCancel());
		buttonCancel.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonCancel();
			}
		});
		panelManager.add(buttonCancel);
		filter.add(panelManager);
		this.add(filter);
	}
	
	/** реакция на нажатие клавиши Filter */
	private void onButtonFilter(){
		this.findModule.filter(this.textIdModule.getText(), this.textAddress.getText());
		this.close();
	}
	
	private void onButtonCancel(){
		this.close();
	}
}
