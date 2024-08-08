package fenomen.server.controller.client.view.modules.table;

/** объект, владеющий {@link ModulesManagerAsync}*/
public interface IModulesManagerAsyncAware {
	/** получить службу сервиса */
	public ModulesManagerAsync getModulesManager();
}
