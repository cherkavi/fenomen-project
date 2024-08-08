package fenomen.monitor_manager.client.view.monitor_list.add;

import com.google.gwt.i18n.client.Constants;

public interface MonitorEditWindowConstants extends Constants{

	@DefaultStringValue(value="Add Monitor")
	public String titleAdd();

	@DefaultStringValue(value="Edit Monitor")
	public String titleEdit();
	
	@DefaultStringValue(value="Save")
	public String buttonSaveCaption();
	
	@DefaultStringValue(value="Cancel")
	public String buttonCancelCaption();
	
	@DefaultStringValue(value="Login")
	public String login();
	@DefaultStringValue(value="login is null, input login")
	public String loginIsNull();
	@DefaultStringValue(value="choose another login, this is already present")
	public String loginPresent();
	
	@DefaultStringValue(value="password")
	public String password();
	@DefaultStringValue(value="password is null, input password")
	public String passwordIsNull();
	
	@DefaultStringValue(value="description")
	public String description();
	@DefaultStringValue(value="description is null, input description")
	public String descriptionIsNull();

	@DefaultStringValue(value="error")
	public String error();
	@DefaultStringValue(value="attention")
	public String attention();
	
	@DefaultStringValue(value="server exchange error")
	public String serverExchangeError();
	
	@DefaultStringValue(value="server exchange")
	public String serverExchange();
	

	@DefaultStringValue(value="jabber url")
	public String jabberUrl();
	@DefaultStringValue(value="input jabber url")
	public String jabberUrlIsNull();
	
	@DefaultStringValue(value="jabber port")
	public String jabberPort();
	@DefaultStringValue(value="input jabber port")
	public String jabberPortIsNull();
	@DefaultStringValue(value="input number of jabber port")
	public String jabberPortIsNan();
	
	@DefaultStringValue(value="jabber proxy")
	public String jabberProxy();
	@DefaultStringValue(value="input jabber proxy")
	public String jabberProxyIsNull();
	
	@DefaultStringValue(value="jabber login")
	public String jabberLogin();
	@DefaultStringValue(value="input jabber login")
	public String jabberLoginIsNull();
	
	@DefaultStringValue(value="jabber password")
	public String jabberPassword();
	@DefaultStringValue(value="input jabber password")
	public String jabberPasswordIsNull();
}
