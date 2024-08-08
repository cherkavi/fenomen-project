package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel;

import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.WindowListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

import fenomen.server.controller.client.utility.INotifyClose;
import fenomen.server.controller.client.utility.NotifyCloseContainer;
import fenomen.server.controller.client.view.RootComposite;
import fenomen.server.controller.client.view.settings.module.sensor_window.Device;
import fenomen.server.controller.client.view.settings.module.sensor_window.DeviceRegister;
import fenomen.server.controller.client.view.settings.module.sensor_window.SensorManagerAsync;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.RegisterAlarmCheckerPanel;
import fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.value_panel.RegisterValuePanel;

/** объект, который отображает окно одного регистра */
class RegisterWindow extends Window implements INotifyClose{
	/** объект, который содержит все дочерние панели, которые требуют оповещения о закрытии */
	private NotifyCloseContainer notifyCloseContainer=new NotifyCloseContainer();
	private NotifyCloseContainer parentNotifyContainer;

	/** объект, который отображает окно одного регистра
	 * @param moduleId - уникальный номер модуля в масштабе базы данных  
	 * @param device - устройство
	 * @param register - регистр по данному устройству
	 */
	public RegisterWindow(int moduleId, Device device, DeviceRegister register, SensorManagerAsync sensorManager, NotifyCloseContainer parentNotifyContainer){
		this.parentNotifyContainer=parentNotifyContainer;
		this.parentNotifyContainer.addNotifyClose(this);
		
		TabPanel tabPanel=new TabPanel();
		
		RegisterValuePanel valuePanel=new RegisterValuePanel(device.getIdModule(),device.getModbusAddress(),register, sensorManager);
		tabPanel.add(valuePanel);
		notifyCloseContainer.addNotifyClose(valuePanel);
		
		RegisterAlarmCheckerPanel alarmChecker=new RegisterAlarmCheckerPanel(device.getIdModule(), device.getModbusAddress(),register.getRegisterAddress());
		tabPanel.add(alarmChecker);
		notifyCloseContainer.addNotifyClose(alarmChecker);
		
		
		this.setLayout(new BorderLayout());
		this.add(tabPanel, new BorderLayoutData(RegionPosition.CENTER));
		
		this.setTitle(device.getSensorTypeDescription()+"  ("+register.getRegisterAddress()+")   "+register.getDescription());
		this.setModal(false);
		this.setClosable(true);
		this.addListener(new WindowListenerAdapter() {
			@Override
			public void onClose(Panel panel) {
				RegisterWindow.this.notifyClose();
			}
		});
		this.setSize( (int)(RootComposite.getWindowWidth()/1), (int)(RootComposite.getWindowHeight()/1));
		this.show();
	}
	
	@Override
	public void notifyClose() {
		try{
			this.notifyCloseContainer.notifyAllObjectAndRemoveThey();
			this.parentNotifyContainer.removeNotifyClose(this);
			this.close();
		}catch(Exception ex){
			System.err.println("Exception: "+ex.getMessage());
		}
	}
	
}