package fenomen.monitor.web_service.client_implementation;

import fenomen.monitor.web_service.common.MonitorIdentifier;
import fenomen.monitor.web_service.common.RestartSettingsElement;
import fenomen.monitor.web_service.interf.IRestartSettings;

/** удаленный вызов методов WebService для RestartSettings */
public class RestartSettingsImplementation extends XFireImplementation<IRestartSettings> implements IRestartSettings{

	/** удаленный вызов методов WebService для RestartSettings
	 * @param url - полный путь к серверу ( или приложению FenomenServer )
	 *  */
	public RestartSettingsImplementation(String url) {
		super(url, "services/RestartSettings");
	}

	@Override
	protected Class<IRestartSettings> getGenericClass() {
		return IRestartSettings.class;
	}

	@Override
	public RestartSettingsElement[] getList(MonitorIdentifier monitorIdentifier) throws Exception{
        try{
        	// первая попытка
        	return this.getProxy().getList(monitorIdentifier); 
        }catch(Exception ex){
        	// вторая попытка 
        	this.reconnectToService();
        	return this.getProxy().getList(monitorIdentifier);
        }
	}

	@Override
	public boolean updateList(MonitorIdentifier monitorIdentifier, RestartSettingsElement[] list) throws Exception{
        try{
        	// первая попытка
        	return this.getProxy().updateList(monitorIdentifier, list); 
        }catch(Exception ex){
        	// вторая попытка 
        	this.reconnectToService();
        	return this.getProxy().updateList(monitorIdentifier, list);
        }
	}

}
