package fenomen.monitor.web_service.client_implementation;

import fenomen.monitor.web_service.common.JabberSettings;
import fenomen.monitor.web_service.common.MonitorIdentifier;
import fenomen.monitor.web_service.interf.ILogin;

public class LoginImplementation extends XFireImplementation<ILogin> implements ILogin{

	public LoginImplementation(String url){
		super(url, "services/Login");
	}
	
	
	@Override
	public MonitorIdentifier enter(String login, String password) throws Exception{
        try{
        	// первая попытка
        	return this.getProxy().enter(login, password); 
        }catch(Exception ex){
        	// вторая попытка 
        	this.reconnectToService();
        	return this.getProxy().enter(login, password);
        }
	}

	@Override
	protected Class<ILogin> getGenericClass() {
		return ILogin.class;
	}


	@Override
	public JabberSettings getJabberSettings(MonitorIdentifier monitorIdentifier)
			throws Exception {
        try{
        	// первая попытка
        	return this.getProxy().getJabberSettings(monitorIdentifier); 
        }catch(Exception ex){
        	// вторая попытка 
        	this.reconnectToService();
        	return this.getProxy().getJabberSettings(monitorIdentifier);
        }
	}
	
}
