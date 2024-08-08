package fenomen.server.controller.client.view.settings.module.thread_settings_window;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("moduleSettingsManager")
public interface ModuleSettingsManager extends RemoteService{
	/** получить список параметров на основании 
	 * @param idModule - уникальный номер модуля
	 * @param idSection - уникальный номер секции 
	 * <table>
	 * 	<tr>
	 * 		<th>id</th><th>name</th>
	 *  </tr>
	 * 	<tr>
	 * 		<th>1</th><th>HeartBeat</th>
	 *  </tr>
	 * 	<tr>
	 * 		<th>2</th><th>Information</th>
	 *  </tr>
	 * 	<tr>
	 * 		<th>3</th><th>Alarm</th>
	 *  </tr>
	 * 	<tr>
	 * 		<th>4</th><th>Sensor</th>
	 *  </tr>
	 * </table>
	 * @return
	 * <li><b> array </b> если получены данные </li>
	 * <li><b> null </b> ошибка получения данных </li>
	 */
	public ModuleSettingsWrap[] getListOfParameters(int idModule, int idSection);

	/** 
	 * Установить и/или обновить параметр 
	 * @param idModule - уникальный идентификатор модуля 
	 * @param idSection - уникальный идентификатор секции
	 * @param idParameter - уникальный идентификатор параметр
	 * @param value текстовое значение параметра
	 * @return 
	 * <li><b> null </b> успешно обновлен/создан </li>
	 * <li><b> not null </b> ошибка создания </li>
	 */
	public String saveOrUpdateParameter(int idModule, int idSection, int idParameter, String value);
	
	/** обновить/создать группу параметров
	 * @param idModule - уникальный идентификатор модуля  
	 * @param idSection - уникальный идентификатор секци
	 * <table border=1>
	 * 	<tr><th>Id</th><th>Description</th></tr>
	 * 	<tr><td>1</td><td>HeartBeat</td></tr>
	 * 	<tr><td>2</td><td>Information</td></tr>
	 * 	<tr><td>3</td><td>Alarm</td></tr>
	 * 	<tr><td>4</td><td>Sensor</td></tr>
	 * </table>
	 * @param parameters - группа параметров, которые нужно обновить/создать
	 * @return 
	 * <li><b> null </b> успешно обновлен/создан </li>
	 * <li><b> not null </b> ошибка создания (возможно уже некоторые были записаны )</li>
	 */
	public String saveOrUpdateParameter(int idModule, int idSection, ModuleSettingsWrap[] parameters);
	
}
