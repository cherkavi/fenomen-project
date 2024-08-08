package fenomen.monitor_manager.client.view.monitor_list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.NameValuePair;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.MessageBox.PromptCallback;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

import fenomen.monitor_manager.client.utility.RootComposite;
import fenomen.monitor_manager.client.view.MainMenu;
import fenomen.monitor_manager.client.view.monitor_list.add.MonitorEditWindow;

/** список мониторов в системе с возможностью их редактирования */
public class MonitorList extends Composite{
	private MonitorListConstants constants=GWT.create(MonitorListConstants.class);
	private MonitorManagerAsync monitorManager=GWT.create(MonitorManager.class);
	private FlexTable table;
		
	public MonitorList(){
		Panel panel=new Panel();
		panel.addTool(new Tool(Tool.CLOSE,new Function(){
			@Override
			public void execute() {
				onClose();
			}
		}));
		panel.setTitle(constants.title());
		panel.setBorder(true);
		panel.setBodyStyle("border-style:solid; border-color:#99BBE8;border-width:2px;border-top-width:0px;");
		panel.setWidth(RootComposite.getWindowWidth()/2);
		panel.setHeight(RootComposite.getWindowHeight());
		panel.setLayout(new BorderLayout());
		
		table=new FlexTable();
		panel.add(table, new BorderLayoutData(RegionPosition.CENTER));
		
		Button buttonAdd=new Button(constants.buttonAddMonitor());
		buttonAdd.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonAdd();
			}
		});
		panel.add(buttonAdd, new BorderLayoutData(RegionPosition.SOUTH));
		
		this.initWidget(panel);
		// обновить данные по всем Мониторам в системе 
		this.updateTable();
	}

	/** обновить данные по всем мониторам в системе */
	public void updateTable(){
		MessageBox.show(new MessageBoxConfig(){
			{
				this.setWait(true);
				this.setWaitConfig(new WaitConfig(){
					{
						this.setInterval(200);
					}
				});
				this.setWidth(250);
				this.setProgressText(constants.serverExchangeWait());
			}
		});
		this.monitorManager.getMonitors(new AsyncCallback<MonitorWrap[]>(){
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.hide();
				MessageBox.alert("Error", constants.serverExchangeError());
			}

			@Override
			public void onSuccess(MonitorWrap[] result) {
				MessageBox.hide();
				updateTable(result);
			}
		});
	}
	
	private void updateTable(MonitorWrap[] result){
		table.removeAllRows();
		if(result!=null){
			for(int counter=0;counter<result.length;counter++){
				this.table.setWidget(counter, 
									 0, 
									 new MonitorPanel(this, 
											 		  result[counter], 
											 		  constants.buttonEdit(), 
											 		  constants.buttonRemove(),
											 		  30,
											 		  (int)(RootComposite.getWindowWidth()/2.2)));
			}
		}
	}
	
	private void onClose(){
		RootComposite.showView(new MainMenu());
	}
	
	private void onButtonAdd(){
		System.out.println("Add module");
		new MonitorEditWindow(this,this.monitorManager,null);
	}
	
	public void editMonitor(MonitorWrap monitor){
		System.out.println("Edit monitor");
		new MonitorEditWindow(this,this.monitorManager,monitor);
	}
	
	public void removeMonitorAction(MonitorWrap monitor){
		System.out.println("Remove monitor");
		MessageBox.show(new MessageBoxConfig(){
			{
				this.setWait(true);
				this.setWidth(300);
				this.setProgressText(constants.serverExchangeWait());
				this.setWaitConfig(new WaitConfig(){
					{
						this.setInterval(200);
					}
				});
			}
		});
		this.monitorManager.removeMonitor(monitor, new AsyncCallback<String>(){
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.hide();
				MessageBox.alert(constants.serverExchangeError());
			}
			@Override
			public void onSuccess(String result) {
				MessageBox.hide();
				if(result!=null){
					MessageBox.alert(result);
				}else{
					MonitorList.this.updateTable();
				}
			}
		});
	}
	
	public void removeMonitor(final MonitorWrap monitor){
		MessageBox.show(new MessageBoxConfig(){
			{
				this.setTitle(constants.removeConfirmTitle());
				this.setMsg(constants.removeConfirm());
				this.setWidth(300);
				this.setButtons(new NameValuePair[]{
						new NameValuePair("yes","Remove"),
						new NameValuePair("no","Cancel"),
				});
				this.setCallback(new PromptCallback(){
					@Override
					public void execute(String btnID, String text) {
						if(btnID.equals("yes")){
							removeMonitorAction(monitor);
						}
						if(btnID.equals("no")){
							
						}
					}
				});
			}
		});
	}
}
