package fenomen.module.web_service.service_implementation.settings;

import java.util.HashMap;

import org.hibernate.criterion.Restrictions;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.ModuleStorage;
/** ��������� ������� 
 * <table border=1>
 * 	<tr>
 * 		<th>����</th> <th>��������</th>
 * 	</tr>
 *  <tr>
 *  	<td></td> <td></td>
 *  </tr>
 * </table>
 * 
 * */
public class StaticSettings {
	private static HashMap<String,Object> storage=new HashMap<String,Object>();
	/** ���������� ������, � ������� ����� ��������� ���������� �� ������� AlarmI */
	public final static String alarmStorageExtension="service_implementation.alarm_extension";
	
	/** ���������� ������, � ������� ����� ��������� AlarmChecker ��� ��������� ������� */
	public final static String alarmCheckerStorageExtension="service_implementation.alarm_checker_extension";
	

	/** ���������� ������, � ������� ����� ��������� ���������� �� ������� informationI */
	public final static String informationStorageExtension="service_implementation.information_extension";
	
	/** ���������� ������, � ������� ����� ��������� informationChecker ��� ��������� ������� */
	public final static String informationCheckerStorageExtension="service_implementation.information_checker_extension";
	
	/** ���������� ������, � ������� ����� ��������� ������ ��� ��������� ������� */
	public final static String taskStorageExtension="service_implementation.task_extension";
	
	/** ������������ ���-�� ��������� TaskModule � ���������� */
	public final static String taskMaxInContainer="service_implementation.task_in_container";
	
	static{
		StaticSettings.setObject(alarmStorageExtension,".alarm");
		StaticSettings.setObject(alarmCheckerStorageExtension,".alarm_checker");

		StaticSettings.setObject(informationStorageExtension,".info");
		
		StaticSettings.setObject(informationCheckerStorageExtension,".info_checker");

		StaticSettings.setObject(taskStorageExtension,".task");
		
		StaticSettings.setObject(taskMaxInContainer,new Integer(5));
	}
	
	/** �������� � ��������� ������ */
	public static void setObject(String key, Object value){
		storage.put(key, value);
	}
	
	/** �������� ������ �� ��������� 
	 * @param key - ����, �� �������� ����� �������� ������
	 * @return ���������� ������, ���� null ( ���� ���� �� ������ )
	 */
	public static Object getObject(String key){
		return storage.get(key);
	}
	
	/** �������� ���� � ��������
	 * @param synonim - ��������� ���, �� �������� ����� �������� ������� �� ������� <b>module_storage</b>, ��������� ��������:
	 * <li><b>Task</b> ������ ���� � Task �������� </li>
	 * <li><b>Alarm</b> ������ ���� � Alarm �������� </li>
	 * <li><b>Information</b> ������ ���� � Information </li>
	 * <li><b>AlarmChecker</b> ������� AlarmChecker-� ��� ������� �� ������ </li>
	 * <li><b>InformationChecker</b> ������� InformationChecker-� ��� ������� �� ������ </li>
	 * <li><b>TaskObject</b> �������-������ ��� ������� �� ������ </li>
	 * @return ���� � �������� ��� ���������� XML ������ ���������� ����:
	 */
	public static String getPathToStorage(String synonim){
		String returnValue=null;
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			returnValue=(String) ((ModuleStorage)connector.getSession().createCriteria(ModuleStorage.class).add(Restrictions.eq("name", synonim)).uniqueResult()).getDirectory();
		}catch(Exception ex){
			// logger.warn("ModuleStorage#getPathToStorage Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
		return returnValue;
	}
	
}
