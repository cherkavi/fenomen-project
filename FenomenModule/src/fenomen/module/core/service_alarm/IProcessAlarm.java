package fenomen.module.core.service_alarm;

import java.util.ArrayList;
import fenomen.module.web_service.common.ContainerModuleAlarm;

/** интерфейс, который реализует объект дл€ манипул€ций с очередью из ModuleAlarm */
public interface IProcessAlarm {
	/**  проведение манипул€ций с очередью из элементов {@link ModuleAlarmContainer} дл€ отправки на сервер */
	public void processAlarm(ArrayList<ContainerModuleAlarm> listOfAlarm);
}
