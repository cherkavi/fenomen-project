package fenomen.server.controller.client.view.settings.module.thread_settings_window;

import com.google.gwt.user.client.rpc.IsSerializable;

/** параметр для Settings модуля (для чтения/установки значений)
 * <table border=1>
 * 	<tr><th>Parameter Name </th><th>Description</th></tr>
 * 	<tr><td>idSection</td><td>уникальный идентификатор секции (module_settings_section.id)</td></tr>
 * 	<tr><td>idParameter</td><td>уникальный идентификатор параметра в базе (module_settings_parameter.id)</td></tr>
 * 	<tr><td>parameterName</td><td>наименование параметра (module_settings_parameter.parameter_name)</td></tr>
 * 	<tr><td>settingsValue</td><td>значение параметра в текстовом виде (module_settings.settings_value)</td></tr>
 * 	<tr><td>moduleRecieve</td><td>0 - записано в базу, 1 - принято модулем</td></tr>
 * </table>
 * 
 * */
public class ModuleSettingsWrap implements IsSerializable{
	private int idSection;
	private int idParameter;
	private String parameterName;
	private String settingsValue;
	private Integer moduleRecieve;
	
	/** параметр для Settings модуля (для чтения/установки значений)*/
	public ModuleSettingsWrap(){
	}

	/** получить уникальный идентификатор секции ( из таблицы module_settings_section.id )*/
	public int getIdSection() {
		return idSection;
	}

	/** @param idSection установить уникальный идентификатор секции ( из таблицы module_settings_section.id )*/
	public void setIdSection(int idSection) {
		this.idSection = idSection;
	}

	/** получить уникальный идентификатор параметра ( из таблицы module_settings_parameter.id ) */
	public int getIdParameter() {
		return idParameter;
	}

	/** @param idParameter установить уникальный идентификатор параметра ( из таблицы module_settings_parameter.id ) */
	public void setIdParameter(int idParameter) {
		this.idParameter = idParameter;
	}

	/** получить уникальное ( в пределах секции ) имя параметра module_settings_parameter.name */
	public String getParameterName() {
		return parameterName;
	}

	/** @param parameterName установить  уникальное ( в пределах секции ) имя параметра module_settings_parameter.name */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	/** получить текстовое значение установленного параметра */
	public String getSettingsValue() {
		return settingsValue;
	}

	/** установить текстовое значение параметра */
	public void setSettingsValue(String settingsValue) {
		this.settingsValue = settingsValue;
	}

	/** флаг записанного/установленного параметра   
	 * <li> <b>0</b> - записано в базу </li> 
	 * <li> <b>1</b> - принято модулем </li>
	 * */
	public Integer getModuleRecieve() {
		return moduleRecieve;
	}

	/** флаг записанного/установленного параметра
	 * <li> <b>0</b> - записано в базу </li> 
	 * <li> <b>1</b> - принято модулем </li>
	 * */
	public void setModuleRecieve(Integer moduleRecieve) {
		this.moduleRecieve = moduleRecieve;
	}
	
}
