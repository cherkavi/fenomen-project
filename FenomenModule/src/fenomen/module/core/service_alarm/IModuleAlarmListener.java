package fenomen.module.core.service_alarm;

import fenomen.module.web_service.common.ContainerModuleAlarm;

/** ��������� ��������� ���������� � ����� �������������� �������� */
public interface IModuleAlarmListener {
	/** ���������� � ����� ��������� �������� 
	 * @param moduleAlarm - ������, ������� ������� ���� ������� �� ������ � �������� ���������� ��������� ����� 
	 */
	public void notifyAlarm(ContainerModuleAlarm moduleAlarm);
}
