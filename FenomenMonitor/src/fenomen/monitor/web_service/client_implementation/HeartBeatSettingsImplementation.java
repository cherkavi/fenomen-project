package fenomen.monitor.web_service.client_implementation;

import fenomen.monitor.web_service.common.HeartBeatSettingsElement;
import fenomen.monitor.web_service.common.MonitorIdentifier;
import fenomen.monitor.web_service.interf.IHeartBeatSettings;

/** удаленный вызов методов WebService для HeartBeatSettings */
public class HeartBeatSettingsImplementation extends XFireImplementation<IHeartBeatSettings> implements IHeartBeatSettings{

	/** удаленный вызов методов WebService для HeartBeatSettings
	 * @param url - полный путь к серверу ( или приложению FenomenServer )
	 *  */
	public HeartBeatSettingsImplementation(String url) {
		super(url, "services/HeartBeatSettings");
	}

	@Override
	protected Class<IHeartBeatSettings> getGenericClass() {
		return IHeartBeatSettings.class;
	}

	@Override
	public HeartBeatSettingsElement[] getList(MonitorIdentifier monitorIdentifier) throws Exception{
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
	public boolean updateList(MonitorIdentifier monitorIdentifier, HeartBeatSettingsElement[] list) throws Exception{
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
