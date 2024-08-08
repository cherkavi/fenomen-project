package fenomen.module.web_service.service;

import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ContainerModuleInformation;
import fenomen.module.web_service.common.TransportChecker;

/** ���������� ������� ��������������� ��������� */
public interface IInformation {
	/** ���� ��������, ������� "�������" �� �������� �������� ������ �� ������ */
	public static final String returnOk="return_ok";
	/** ���� ��������, ������� "�������" �� ��������� �������� ������ �� ������ */
	public static final String returnError="return_error";
	
	/** ���������� ������� ��������������� ���������
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @param moduleInformation - ���������� ��� �������� 
	 * @return - ������������ �������� ��������  
	 */
	public String sendInformation(ModuleIdentifier moduleIdentifier, ContainerModuleInformation moduleInformation);
	
	/** �������� �� ������� ������-����������� ��� ������� InformationChecker 
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @param fileId - ���������� ������������� ������� InformationChecker 
	 * @return - ������ ��� ������� ������ �� ���������� �������������� 
	 */
	public TransportChecker getInformationCheckerById(ModuleIdentifier moduleIdentifier, String fileId);
	
	/** ������������� � ��������� �� ������� �����  
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @param fileId - ���������� ������������� ������� InformationChecker 
	 * @return - ������������ ��������
	 * <li><b>return_ok {@link IInformation#returnOk}</b> - ������� ���������� �������� </li>
	 * <li><b>return_Error {@link IInformation#returnError}</b> - ������ ��������� �������� </li>
	 */
	public String confirmInformationCheckerGet(ModuleIdentifier moduleIdentifier, String fileId);
	
}
