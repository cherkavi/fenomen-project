package fenomen.monitor_manager.client.view.monitor_list;

import com.google.gwt.i18n.client.Constants;

/** константы для мониторов в системе  */
public interface MonitorListConstants extends Constants{
	@DefaultStringValue(value="Main menu")
	public String title();
	
	@DefaultStringValue(value="Add monitor")
	public String buttonAddMonitor();
	
	@DefaultStringValue(value="Server exchange")
	public String serverExchangeWait();
	
	@DefaultStringValue(value="Server Exchange ERROR")
	public String serverExchangeError();
	
	@DefaultStringValue(value="Edit")
	public String buttonEdit();
	
	@DefaultStringValue(value="Remove")
	public String buttonRemove();
	
	@DefaultStringValue(value="Are you sure to remove Monitor ?")
	public String removeConfirm();
	
	@DefaultStringValue(value="Attention")
	public String removeConfirmTitle();
}
