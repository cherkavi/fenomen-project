package fenomen.module.web_service.service;

import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleTaskContainer;

/** ���������, ������� ��������� ������� ��� ��������� Task � ������ ������ �� Task */
public interface ITask {
	
	/** ������ ������� ���������� ������� �� ��������� 
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @return ModuleTaskContainer - ������ ��� ������ 
	 * */
	public ModuleTaskContainer getTask(ModuleIdentifier moduleIdentifier);
	
	/** ������������� ������� ��������� ������� - ������ ������ ������  
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @param taskId - ���������� ������������� ����������� ������� ( � ���������� ������� ��������� )
	 */
	public void tookTask(ModuleIdentifier moduleIdentifier, Integer taskId);
	
	/** ������������� ������� ��������� ������� - ������ ������� � ��������� ������� 
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @param taskId - ���������� ������������� ����������� ������� ( � ���������� ������� ��������� )
	 */
	public void taskProcessOk(ModuleIdentifier moduleIdentifier, Integer taskId);
	
	
	/** ������������� ������� ��������� ������� - ������ ������� � �� ���������  
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @param taskId - ���������� ������������� ����������� ������� ( � ���������� ������� ��������� )
	 */
	public void taskProcessError(ModuleIdentifier moduleIdentifier, Integer taskId);

	
	/** ������������� ������� ��������� ������� - ������ �� �������  
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @param taskId - ���������� ������������� ����������� ������� ( � ���������� ������� ��������� )
	 */
	public void taskProcessNotFound(ModuleIdentifier moduleIdentifier, Integer taskId);
	
}
