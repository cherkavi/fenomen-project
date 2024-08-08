package fenomen.server.controller.client.view.modules;

/** объект принимает команды от других модулей  */
public interface IActionNotify {
	/** оповещение о выполнении действия 
	 * @param actionName - уникальное имя действия (идентификатор действия )
	 * @param parameter - параметр данного действия (nullable)
	 */
	public void actionNotify(String actionName, Object parameter);
}
