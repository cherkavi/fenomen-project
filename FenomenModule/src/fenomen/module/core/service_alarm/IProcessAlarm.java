package fenomen.module.core.service_alarm;

import java.util.ArrayList;
import fenomen.module.web_service.common.ContainerModuleAlarm;

/** ���������, ������� ��������� ������ ��� ����������� � �������� �� ModuleAlarm */
public interface IProcessAlarm {
	/**  ���������� ����������� � �������� �� ��������� {@link ModuleAlarmContainer} ��� �������� �� ������ */
	public void processAlarm(ArrayList<ContainerModuleAlarm> listOfAlarm);
}
