package fenomen.server.controller.client.view.modules.table.add_window;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.HorizontalLayout;

import fenomen.server.controller.client.view.modules.IUpdateNotify;
import fenomen.server.controller.client.view.modules.table.ModulesManagerAsync;
import fenomen.server.controller.client.view.modules.table.row.RowData;

public class AddWindow extends Window{
	final private AddWindowConstants constants=GWT.create(AddWindowConstants.class);
	private ModulesManagerAsync modulesManager;
	private TextField fieldAddress=new TextField();
	private IUpdateNotify updateNotify;
	private TextField fieldModule=new TextField();
	
	public AddWindow(IUpdateNotify updateNotify, ModulesManagerAsync modulesManager){
		this.updateNotify=updateNotify;
		this.modulesManager=modulesManager;
	
		this.setTitle(constants.titleWindow());
		this.setHeight(160);
		this.setMinHeight(200);
		this.setWidth(300);
		this.setMinWidth(300);
		this.setPaddings(5);
		this.setButtonAlign(Position.CENTER);
		this.setModal(true);
		
		Panel panelModule=new Panel();
		panelModule.setTitle(constants.titleModule());
		HorizontalLayout layoutOfModule=new HorizontalLayout(5);
		panelModule.setLayout(layoutOfModule);
		fieldModule=new TextField();
		panelModule.add(fieldModule);
		fieldModule.setWidth(280);
		// fieldModule.setValue("");
		this.add(panelModule);

		Panel panelAddress=new Panel();
		panelAddress.setTitle(constants.titleAddress());
		HorizontalLayout layoutOfAddress=new HorizontalLayout(5);
		panelAddress.setLayout(layoutOfAddress);
		panelAddress.add(fieldAddress);
		fieldAddress.setWidth(280);
		
		this.add(panelAddress);
		
		HorizontalPanel panelManager=new HorizontalPanel();
		panelManager.setWidth("280px");
		panelManager.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		Button buttonAdd=new Button(constants.captionButtonAdd());
		buttonAdd.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonAdd();
			}
		});
		Button buttonCancel=new Button(constants.captionButtonCancel());
		buttonCancel.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				onButtonCancel();
			}
		});
		panelManager.add(buttonAdd);
		panelManager.add(buttonCancel);
		this.add(panelManager);
	}
	
	private void onButtonAdd(){
		RowData rowData=new RowData(0,this.fieldModule.getText(), this.fieldAddress.getText());
		this.modulesManager.createRowElement(rowData, new AsyncCallback<String>(){
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert(constants.serverError());
			}

			@Override
			public void onSuccess(String result) {
				if(result==null){
					onServerModulesCreated();
				}else{
					onServerModulesCreatedError(result);
				}
				
			}
		});
	}
	
	private void onServerModulesCreated(){
		this.close();
		this.updateNotify.needUpdate();
	}

	private void onServerModulesCreatedError(String textError){
		MessageBox.alert(textError);
		
	}
	
	private void onButtonCancel(){
		this.close();
	}
	
}
