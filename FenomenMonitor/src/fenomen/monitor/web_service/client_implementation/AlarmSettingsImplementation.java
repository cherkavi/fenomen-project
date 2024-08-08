package fenomen.monitor.web_service.client_implementation;

import fenomen.monitor.web_service.common.AlarmSettingsElement;

import fenomen.monitor.web_service.common.MonitorIdentifier;
import fenomen.monitor.web_service.interf.IAlarmSettings;

/** удаленный вызов методов WebService для AlarmSettings */
public class AlarmSettingsImplementation extends XFireImplementation<IAlarmSettings> implements IAlarmSettings{

	/** удаленный вызов методов WebService для AlarmSettings
	 * @param url - полный путь к серверу ( или приложению FenomenServer )
	 *  */
	public AlarmSettingsImplementation(String url) {
		super(url, "services/AlarmSettings");
	}

	@Override
	protected Class<IAlarmSettings> getGenericClass() {
		return IAlarmSettings.class;
	}

	@Override
	public AlarmSettingsElement[] getList(MonitorIdentifier monitorIdentifier) throws Exception{
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
	public boolean updateList(MonitorIdentifier monitorIdentifier, AlarmSettingsElement[] list) throws Exception{
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
