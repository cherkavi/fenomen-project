package fenomen.module.core.service_information;

import java.util.ArrayList;


import fenomen.module.web_service.common.ContainerModuleInformation;

/** интерфейс, который реализует объект дл€ манипул€ций с очередью из ModuleInformation */
public interface IProcessInformation {
	/**  проведение манипул€ций с очередью из элементов {@link ModuleInformationContainer} дл€ отправки на сервер */
	public void processInformation(ArrayList<ContainerModuleInformation> listOfInformation);
}
