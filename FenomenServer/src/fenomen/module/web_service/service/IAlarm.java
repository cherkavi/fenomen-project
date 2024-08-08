package fenomen.module.web_service.service;

import fenomen.module.web_service.common.TransportChecker;
import fenomen.module.web_service.common.ContainerModuleAlarm;
import fenomen.module.web_service.common.ModuleIdentifier;

public interface IAlarm {
	/** ���� ��������, ������� "�������" �� �������� �������� ������ �� ������ */
	public static final String returnOk="return_ok";
	/** ���� ��������, ������� "�������" �� ��������� �������� ������ �� ������ */
	public static final String returnError="return_error";

	
	/** 
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @return - 
	 * <li> <b>true</b> - ������� ���������� </li>
	 * <li> <b>false</b> - ������ ��������   </li>
	 */
	public boolean moduleWasRestarted(ModuleIdentifier moduleIdentifier);
	
	/**  ������� �� ������ ����������� � ������� Alarm ��������� 
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @param moduleAlarm - ��������� ������� ��� �������� 
	 * @return - ������������ ��������
	 * <li><b>return_ok({@link IAlarm.returnOk})</b> - ������� ���������� �������� </li>
	 * <li><b>return_Error({@link IAlarm.returnError})</b> - ������ ��������� �������� </li>
	 */
	public String sendAlarm(ModuleIdentifier moduleIdentifier, ContainerModuleAlarm moduleAlarm);
	
	/** �������� �� ������� ������-����������� ��� ������� AlarmChecker 
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @param fileId - ���������� ������������� ������� AlarmChecker 
	 * @return - ������ ��� ������� ������ �� ���������� �������������� 
	 */
	public TransportChecker getAlarmCheckerById(ModuleIdentifier moduleIdentifier, String fileId);
	
	/** ������������� � ��������� �� ������� �����  
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @param fileId - ���������� ������������� ������� AlarmChecker 
	 * @return - ������������ ��������
	 * <li><b>return_ok {@link IAlarm.returnOk}</b> - ������� ���������� �������� </li>
	 * <li><b>return_Error {@link IAlarm.returnError}</b> - ������ ��������� �������� </li>
	 */
	public String confirmAlarmCheckerGet(ModuleIdentifier moduleIdentifier, String fileId);
}
