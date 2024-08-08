package fenomen.module.core.service_information;

import fenomen.module.web_service.common.ContainerModuleInformation;


/** ��������� ��������� ���������� � ����� �������������� �������� */
public interface IModuleInformationListener {
	/** ���������� � ����� �������������� �������� 
	 * @param moduleInformation - ������, ������� ������� ���� ������� �� ������ � �������� ��������������� ����� 
	 */
	public void notifyInformation(ContainerModuleInformation moduleInformation);
}
