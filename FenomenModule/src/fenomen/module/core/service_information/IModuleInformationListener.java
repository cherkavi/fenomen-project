package fenomen.module.core.service_information;

import fenomen.module.web_service.common.ContainerModuleInformation;


/** интерфейс получения оповещения о новых информационных событиях */
public interface IModuleInformationListener {
	/** оповещение о новых информационных событиях 
	 * @param moduleInformation - объект, который должнен быть передан на сервер в качестве информационного блока 
	 */
	public void notifyInformation(ContainerModuleInformation moduleInformation);
}
