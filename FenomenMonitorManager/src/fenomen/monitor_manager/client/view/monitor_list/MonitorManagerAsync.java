package fenomen.monitor_manager.client.view.monitor_list;

import com.google.gwt.user.client.rpc.AsyncCallback;

/** управляющий мониторами  (интерфейс для клиентской стороны, асинхронный)*/
public interface MonitorManagerAsync {

	/** получить все доступные мониторы в системе 
	 * @return список мониторов в системе 
	 * */
	void getMonitors(AsyncCallback<MonitorWrap[]> callback);

	/** добавить новый монитор в систему 
	 * @param monitor монитор, который следует добавить в систему ( monitor.getId()==0)
	 * @return - текст ошибки или null( если все прошло успешно )   
	 * */
	void addMonitor(MonitorWrap monitor, AsyncCallback<String> callback);

	/** обновить данные по монитору в системе 
	 * @param monitor монитор, который следует обновить в системе ( monitor.getId()!=0)
	 * @return - текст ошибки или null( если все прошло успешно )   
	 * */
	void editMonitor(MonitorWrap monitor, AsyncCallback<String> callback);

	/**
	 * удалить указанный монитор из системы 
	 * @param monitor монитор, который следует удалить 
	 * @return
	 * <ul>
	 * 	<li>null - успешно удален</li>
	 * 	<li>text - ошибка удаления, текст ошибки </li>
	 * <ul>
	 */
	void removeMonitor(MonitorWrap monitor, AsyncCallback<String> callback);

}
