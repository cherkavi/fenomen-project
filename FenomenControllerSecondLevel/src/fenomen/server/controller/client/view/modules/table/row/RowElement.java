package fenomen.server.controller.client.view.modules.table.row;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.widgets.MessageBox;

import fenomen.server.controller.client.view.modules.IUpdateNotify;
import fenomen.server.controller.client.view.modules.table.IModulesManagerAsyncAware;
import fenomen.server.controller.client.view.modules.table.edit_window.EditWindow;

/** элемент в таблице, который отображает один модуль */
public class RowElement extends Composite implements ClickHandler{
	private RowElementConstants constants=GWT.create(RowElementConstants.class);
	private IModulesManagerAsyncAware modulesManagerAware;
	private RowData rowData;
	private Button buttonEdit;
	private Button buttonRemove;
	private IUpdateNotify updateNotify;	

	/** элемент в таблице, который отображает один модуль 
	 * @param updateNotify - объект, который осведомляется о необходимости Update всех элементов 
	 * @param modulesManagerAware - объект, который владеет сервисом ModulesManager общения с сервером 
	 * @param id уникальный идентификатор модуля
	 * @param idModule - уникальный идентификатор модуля  
	 * @param address адрес модуля 
	 */
	public RowElement(IUpdateNotify updateNotify, IModulesManagerAsyncAware modulesManagerAware, int id, String idModule, String address){
		this.rowData=new RowData(id,idModule, address);
		this.modulesManagerAware=modulesManagerAware;
		this.updateNotify=updateNotify;
		this.initComponents(id, address);
	}
	
	/** элемент в таблице, который отображает один модуль 
	 * @param updateNotify - объект, который осведомляется о необходимости Update всех элементов 
	 * @param modulesManagerAware - объект, который владеет сервисом ModulesManager общения с сервером 
	 * @param rowData объект-обертка 
	 */
	public RowElement(IUpdateNotify updateNotify, IModulesManagerAsyncAware modulesManagerAware, RowData rowData){
		this.modulesManagerAware=modulesManagerAware;
		this.rowData=rowData;
		this.updateNotify=updateNotify;
		this.initComponents(rowData.getId(), rowData.getAddress());
	}
	
	private void initComponents(int id, String address){
		HorizontalPanel panel=new HorizontalPanel();
		panel.setSpacing(5);
		
		
		Label label=new Label(Integer.toString(id)+"["+this.rowData.getIdModule()+"] : "+address);
		panel.add(label);
		
		buttonEdit=new Button(constants.captionButtonEdit());
		buttonEdit.addClickHandler(this);
		panel.add(buttonEdit);

		buttonRemove=new Button(constants.captionButtonRemove());
		buttonRemove.addClickHandler(this);
		panel.add(buttonRemove);
		
		this.initWidget(panel);
		
	}
	
	
	private void onButtonEdit(){
		(new EditWindow(this.updateNotify,this.modulesManagerAware.getModulesManager(),this.rowData)).show();
	}
	private void onButtonRemove(){
		MessageBox.confirm(constants.removeElementTitle(), constants.removeElementText(),new MessageBox.ConfirmCallback() {
			@Override
			public void execute(String btnID) {
				if(btnID.equalsIgnoreCase("yes")){
					// yes remove
					RowElement.this.removeElement();
				}else{
					// user put Cancel button
				}
			}
		}); 
	}

	private void removeElement(){
		this.modulesManagerAware.getModulesManager().removeRowElement(this.rowData.getId(), new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				RowElement.this.updateNotify.needUpdate();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert(constants.removeElementServerError());
			}
		});
	}
	
	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource().equals(buttonEdit)){
			onButtonEdit();
		}else if (event.getSource().equals(buttonRemove)){
			onButtonRemove();
		}
		
	}
}
