package fenomen.module.web_service.service_implementation.settings;

import java.util.HashMap;

import org.hibernate.criterion.Restrictions;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.ModuleStorage;
/** настройки системы 
 * <table border=1>
 * 	<tr>
 * 		<th>ключ</th> <th>описание</th>
 * 	</tr>
 *  <tr>
 *  	<td></td> <td></td>
 *  </tr>
 * </table>
 * 
 * */
public class StaticSettings {
	private static HashMap<String,Object> storage=new HashMap<String,Object>();
	/** расширение файлов, в которых будут сохранены полученные от модулей AlarmI */
	public final static String alarmStorageExtension="service_implementation.alarm_extension";
	
	/** расширение файлов, в которых будут сохранены AlarmChecker для удаленных модулей */
	public final static String alarmCheckerStorageExtension="service_implementation.alarm_checker_extension";
	

	/** расширение файлов, в которых будут сохранены полученные от модулей informationI */
	public final static String informationStorageExtension="service_implementation.information_extension";
	
	/** расширение файлов, в которых будут сохранены informationChecker для удаленных модулей */
	public final static String informationCheckerStorageExtension="service_implementation.information_checker_extension";
	
	/** расширение файлов, в которых будут сохранены задачи для удаленных модулей */
	public final static String taskStorageExtension="service_implementation.task_extension";
	
	/** максимальное кол-во элементов TaskModule в контейнере */
	public final static String taskMaxInContainer="service_implementation.task_in_container";
	
	static{
		StaticSettings.setObject(alarmStorageExtension,".alarm");
		StaticSettings.setObject(alarmCheckerStorageExtension,".alarm_checker");

		StaticSettings.setObject(informationStorageExtension,".info");
		
		StaticSettings.setObject(informationCheckerStorageExtension,".info_checker");

		StaticSettings.setObject(taskStorageExtension,".task");
		
		StaticSettings.setObject(taskMaxInContainer,new Integer(5));
	}
	
	/** положить в хранилище объект */
	public static void setObject(String key, Object value){
		storage.put(key, value);
	}
	
	/** получить объект из хранилища 
	 * @param key - ключ, по которому нужно получить объект
	 * @return возвращает объект, либо null ( если ключ не найден )
	 */
	public static Object getObject(String key){
		return storage.get(key);
	}
	
	/** получить путь к каталогу
	 * @param synonim - текстовое имя, по которому нужно получить каталог из таблицы <b>module_storage</b>, возможные варианты:
	 * <li><b>Task</b> полный путь к Task каталогу </li>
	 * <li><b>Alarm</b> полный путь к Alarm каталогу </li>
	 * <li><b>Information</b> полный путь к Information </li>
	 * <li><b>AlarmChecker</b> объекты AlarmChecker-ы для посылки на модуль </li>
	 * <li><b>InformationChecker</b> объекты InformationChecker-ы для посылки на модуль </li>
	 * <li><b>TaskObject</b> объекты-задачи для посылки на модуль </li>
	 * @return путь к каталогу для сохранения XML файлов различного типа:
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
