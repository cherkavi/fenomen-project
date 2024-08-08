package fenomen.monitor_manager.client.view.monitor_list;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/** управляющий мониторами  (интерфейс)*/
@RemoteServiceRelativePath(value="monitor_manager")
public interface MonitorManager extends RemoteService{

	/** получить все доступные мониторы в системе 
	 * @return список мониторов в системе 
	 * */
	public MonitorWrap[] getMonitors();
	
	/** добавить новый монитор в систему 
	 * @param monitor монитор, который следует добавить в систему ( monitor.getId()==0)
	 * @return - текст ошибки или null( если все прошло успешно )   
	 * */
	public String addMonitor(MonitorWrap monitor);
	
	/** обновить данные по монитору в системе 
	 * @param monitor монитор, который следует обновить в системе ( monitor.getId()!=0)
	 * @return - текст ошибки или null( если все прошло успешно )   
	 * */
	public String editMonitor(MonitorWrap monitor);
	
	/**
	 * удалить указанный монитор из системы 
	 * @param monitor монитор, который следует удалить 
	 * @return
	 * <ul>
	 * 	<li>null - успешно удален</li>
	 * 	<li>text - ошибка удаления, текст ошибки </li>
	 * <ul>
	 */
	public String removeMonitor(MonitorWrap monitor);
}
