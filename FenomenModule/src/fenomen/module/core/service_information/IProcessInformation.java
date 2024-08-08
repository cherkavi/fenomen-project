package fenomen.module.core.service_information;

import java.util.ArrayList;


import fenomen.module.web_service.common.ContainerModuleInformation;

/** ���������, ������� ��������� ������ ��� ����������� � �������� �� ModuleInformation */
public interface IProcessInformation {
	/**  ���������� ����������� � �������� �� ��������� {@link ModuleInformationContainer} ��� �������� �� ������ */
	public void processInformation(ArrayList<ContainerModuleInformation> listOfInformation);
}
