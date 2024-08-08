package fenomen.server.controller.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.ModuleSettings;
import database.wrap.ModuleTaskWrap;

import fenomen.server.controller.client.view.settings.module.thread_settings_window.ModuleSettingsManager;
import fenomen.server.controller.client.view.settings.module.thread_settings_window.ModuleSettingsWrap;
import fenomen.server.controller.server.utility.XmlData;
import fenomen.server.controller.server.utility.xml_data.task.TaskData;
import fenomen.server.controller.server.utility.xml_data.task.TaskDataAlarmSet;
import fenomen.server.controller.server.utility.xml_data.task.TaskDataHeartBeatSet;
import fenomen.server.controller.server.utility.xml_data.task.TaskDataInformationSet;
import fenomen.server.controller.server.utility.xml_data.task.TaskDataSensorThreadSet;

public class ModuleSettingsManagerImpl extends RemoteServiceServlet implements ModuleSettingsManager{
	private final static long serialVersionUID=1L;
	private Logger logger=Logger.getLogger(this.getClass());
	
	@Override
	public ModuleSettingsWrap[] getListOfParameters(int idModule,
														 int idSection) {
		ModuleSettingsWrap[] returnValue=null;
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Connection connection=connector.getConnection();
			StringBuffer query=new StringBuffer();
			query.append("select \n");
			query.append("  module_settings_parameter.id_section id_section, \n");
			query.append("  module_settings_parameter.id id_parameter, \n");
			query.append("  module_settings_parameter.parameter_name, \n");
			query.append("  module_settings.settings_value, \n");
			query.append("  module_settings.module_recieve \n");
			query.append("from module_settings_parameter \n");
			query.append("left join module_settings on module_settings.id_parameter=module_settings_parameter.id and module_settings.id_module="+idModule+" \n");
			query.append("where module_settings_parameter.id_section="+idSection+" \n");
			ResultSet rs=connection.createStatement().executeQuery(query.toString());
			ArrayList<ModuleSettingsWrap> list=new ArrayList<ModuleSettingsWrap>();
			while(rs.next()){
				list.add(this.getModuleSettingsParameterFromResultSet(rs));
			}
			returnValue=list.toArray(new ModuleSettingsWrap[]{});
		}catch(Exception ex){
			logger.error(" getListOfParameter Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
		return returnValue;
	}

	private ModuleSettingsWrap getModuleSettingsParameterFromResultSet(ResultSet rs){
		ModuleSettingsWrap returnValue=new ModuleSettingsWrap();
		try{
			returnValue.setIdSection(rs.getInt("id_section"));
			returnValue.setIdParameter(rs.getInt("id_parameter"));
			returnValue.setParameterName(rs.getString("parameter_name"));
			returnValue.setSettingsValue(rs.getString("settings_value"));
			int moduleRecieve=rs.getInt("module_recieve");
			if(rs.wasNull()){
				returnValue.setModuleRecieve(null);
			}else{
				returnValue.setModuleRecieve(moduleRecieve);
			}
		}catch(Exception ex){
			logger.error("ModuleSettingsParameter Exception: "+ex.getMessage());
		}
		return returnValue;
	}
	
	/** проверить значение для устанавливаемого параметра  
	 * @param value - значение, которое должно быть установлено
	 * @param connector - соединение с базой данных
	 * @return возвращает 
	 * <ul>
	 * 	<li><b>true</b> - значение может быть установлено/обновлено </li>
	 * 	<li><b>false</b> - значение не может быть установлено или обновлено </li>
	 * </ul>
	 * */
	private boolean checkModuleSettings(ModuleSettings object, ConnectWrap connector){
		// INFO Controller2.проверка значения для устанавливаемого параметра
		boolean returnValue=true;
		if((object.getIdParameter()==1)&&(object.getIdSection()==1)){
			// INFO Controller2.heart_beat оповещение изменение настроек
			try{
				/** значение, которое будет установлено в HeartBeat */
				int value=Integer.parseInt(object.getSettingsValue())/1000;
				ArrayList<Integer> updateList=new ArrayList<Integer>();
				// текущее действие: попытка установки времени для отправки сигнала HeartBeat на сервер 
				logger.debug("обновление по всем мониторам значения для срабатывания оповещения о не получении сигнала HeartBeat");
				ResultSet rs=connector.getConnection().createStatement().executeQuery("select * from monitor_settings_heart_beat where id_module="+object.getIdModule());
				while(rs.next()){
					if(rs.getInt("time_wait")<=(value) ){
						updateList.add(rs.getInt("id"));
					}
				}
				if(updateList.size()>0){
					logger.debug("обновить все значения time_wait из списка, установить для них значение времени ожидания в "+value*2);
					StringBuffer query=new StringBuffer();
					query.append("update monitor_settings_heart_beat set time_wait="+(value*2)+" where id in (0");
					for(int counter=0;counter<updateList.size();counter++){
						query.append(","+updateList.get(counter)+"\n");
					}
					query.append(")");
					String queryUpdate=query.toString();
					logger.debug("Execute: "+queryUpdate);
					connector.getConnection().createStatement().executeUpdate(queryUpdate);
					// TODO добавить оповещение Fenomen.Server о необходимости Update настроек по ожиданию Оповещению событий HeartBeat (обновление потоков, которые слушают приход событий HeartBeat )
				}
			}catch(Exception ex){
				logger.error("checkModuleSettings,HeartBeat Exception: ");
			}
		}
		return returnValue;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String saveOrUpdateParameter(int idModule, 
									    int idSection,
									    int idParameter, 
									    String value) {
		String returnValue=null;
		// TODO Controller2.проверить значение параметра, переданного на установку, на валидность  
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			logger.debug("получить настройку");
			Session session=connector.getSession();
			List<ModuleSettings> list=(List<ModuleSettings>)session.createCriteria(ModuleSettings.class)
				.add(Restrictions.eq("idModule",idModule))
				.add(Restrictions.eq("idSection",idSection))
				.add(Restrictions.eq("idParameter",idParameter)).list();
			if(list.size()>0){
				logger.debug("элемент найден - update");
				ModuleSettings object=(ModuleSettings)list.get(0);
				object.setSettingsValue(value);
				object.setModuleRecieve(0);
				object.setTimeWrite(new Date());
				if(this.checkModuleSettings(object,connector)){
					session.beginTransaction();
					session.update(object);
					session.getTransaction().commit();
				}else{
					logger.error("значение не может быть установлено ");
				}
			}else{
				logger.debug("элемент не найден - создать");
				ModuleSettings object=new ModuleSettings();
				object.setIdModule(idModule);
				object.setIdSection(idSection);
				object.setIdParameter(idParameter);
				object.setSettingsValue(value);
				object.setModuleRecieve(0);
				if(this.checkModuleSettings(object,connector)){
					session.beginTransaction();
					session.save(object);
					session.getTransaction().commit();
				}else{
					logger.error("значение не может быть установлено ");
				}
			}
		}catch(Exception ex){
			returnValue=ex.getMessage();
			logger.error(" saveOrUpdateParameter Exception: "+returnValue);
		}finally{
			connector.close();
		}
		return returnValue;
	}
	
	/** получить имя параметра 
	 * @param parameters - список параметров
	 * @param findSectionIndex - индекс секции по которой производится поиск
	 * @param findParameterName  - имя параметра в секции по которой производится поиск 
	 * @return 
	 * <li> <b>null</b> - в списке приведенный параметр не найден </li>
	 * <li> <b>{@link ModuleSettingsWrap}</b> - найденный параметр из списка по указанным критериям поиска </li>
	 */
	private ModuleSettingsWrap getParameterByName(ModuleSettingsWrap[] parameters, int findSectionIndex, String findParameterName){
		ModuleSettingsWrap returnValue=null;
		for(int counter=0;counter<parameters.length;counter++){
			if(parameters[counter].getIdSection()==findSectionIndex){
				if(parameters[counter].getParameterName().equalsIgnoreCase(findParameterName)){
					// parameter was finded 
					returnValue=parameters[counter];
					break;
				}else{
					// this is that section but another parameter
				}
			}else{
				// this is another section 
			}
		}
		return returnValue;
	}
	
	/** запрос к базе данных на получение параметра по IdModule, IdSection, ParameterName */
	private String queryParameterFromDatabase="select module_settings.settings_value from module_settings " +
											  "inner join module_settings_parameter on module_settings_parameter.id=module_settings.id_parameter "+ 
											  " where module_settings.id_module=? and module_settings.id_section=?" +
											  "and module_settings_parameter.parameter_name=? ";
	
	/** получить значение параметра из базы (используется в экстренных случаях не передачи параметра в списоке заданных ) 
	 * @param connector - соединение с базой данных 
	 * @param idModule - ункальный идентификатор модуля 
	 * @param idSection - уникальный идентификатор секции
	 * @param parameterName - имя параметра, который должен быть получен из базы данных 
	 * @return
	 * <li> <b>{@link null}</b> если заданный параметр не найден  </li>
	 * <li> <b>{@link String}</b> если заданный параметр найден </li>
	 */
	private String getParameterFromDatabase(ConnectWrap connector, int idModule, int idSection, String parameterName){
		String returnValue=null;
		PreparedStatement statement=null;
		try{
			Connection connection=connector.getConnection();
			statement=connection.prepareStatement(queryParameterFromDatabase);
			statement.setInt(1, idModule);
			statement.setInt(2,idSection);
			statement.setString(3, parameterName);
			ResultSet rs=statement.executeQuery();
			if(rs.next()){
				returnValue=rs.getString(1);
			}
		}catch(Exception ex){
			logger.error("getParameterFromDatabase Exception: "+ex.getMessage());
		}finally{
			try{
				statement.close();
			}catch(Exception ex){};
		}
		return returnValue;
	}
	
	/** получить параметр в виде значения Integet из списка переданных параметров, и в случае не нахождения получить значение из базы данных 
	 * @param connector - соединение с базой данных 
	 * @param parameters - параметры, которые были получены от удаленного модуля 
	 * @param idModule - уникальный идентификатор модуля 
	 * @param idSection - уникальный идентификатор секции 
	 * @param parameterNameInDatabase - имя параметра в базе данных по указанной секции 
	 * @return 
	 * <li><b>0</b> - возможно, параметр не был найден в parameters и не был найден в базе данных (либо же в базе данных он равен null ) </li>
	 * <li><b>Integer value</b> - значение параметра в виде Integer Value </li>
	 */
	private int getIntParameter(ConnectWrap connector, ModuleSettingsWrap[] parameters, int idModule, int idSection, String parameterNameInDatabase){
		int returnValue=0;
		ModuleSettingsWrap timeWaitParameter=this.getParameterByName(parameters, idSection, parameterNameInDatabase);
		try{
			returnValue=Integer.parseInt(timeWaitParameter.getSettingsValue());
		}catch(Exception ex){
			logger.warn("getIntParameter ошибка преобразования/конвертирования параметра в Integer:  "+ex.getMessage());
			String parameterFromDatabase=this.getParameterFromDatabase(connector, idModule, idSection, parameterNameInDatabase);
			try{
				returnValue=Integer.parseInt(parameterFromDatabase);
			}catch(Exception exInner){
				returnValue=0;
			};
		}
		return returnValue;
	}
	
	@Override
	public String saveOrUpdateParameter(int idModule, int idSection, ModuleSettingsWrap[] parameters) {
		String returnValue=null;
		for(int counter=0;counter<parameters.length;counter++){
			returnValue=this.saveOrUpdateParameter(idModule, parameters[counter].getIdSection(), parameters[counter].getIdParameter(), parameters[counter].getSettingsValue());
			if(returnValue!=null){
				break;
			}
		}

		// положить в базу данных Task для модуля idModule
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			TaskData taskData=null;
			if(idSection==1){
				// записать Task для модуля idModule. HeartBeat Settings 
				// создать TaskXML задачу
				int timeWait=this.getIntParameter(connector, parameters, idModule, idSection, "time_wait");
				int timeError=this.getIntParameter(connector, parameters, idModule, idSection, "time_error");
				taskData=new TaskDataHeartBeatSet(timeWait, timeError);
			}
			if(idSection==2){
				// записать Task для модуля idModule. Information  Settings
				// создать TaskXML задачу
				int timeWait=this.getIntParameter(connector, parameters, idModule, idSection, "time_wait");
				int timeError=this.getIntParameter(connector, parameters, idModule, idSection, "time_error");
				int maxCount=this.getIntParameter(connector, parameters, idModule, idSection, "max_count");
				taskData=new TaskDataInformationSet(timeWait, timeError, maxCount);
			}
			if(idSection==3){
				// записать Task для модуля idModule. Alarm  Settings
				// создать TaskXML задачу
				int timeWait=this.getIntParameter(connector, parameters, idModule, idSection, "time_wait");
				int timeError=this.getIntParameter(connector, parameters, idModule, idSection, "time_error");
				int maxCount=this.getIntParameter(connector, parameters, idModule, idSection, "max_count");
				taskData=new TaskDataAlarmSet(timeWait, timeError, maxCount);
			}
			if(idSection==4){
				// записать Task для модуля idModule. Sensor  Settings
				// создать TaskXML задачу
				int timeWait=this.getIntParameter(connector, parameters, idModule, idSection, "time_wait");
				taskData=new TaskDataSensorThreadSet(timeWait);
			}
			if(taskData!=null){
				// сохранить TaskXML задачу в хранилище
				String taskFileName=taskData.saveXmlAsFile(taskData.getPathToStorage(XmlData.pathTask));
				// сохранить задачу в базе данных по указанному IdModule
				ModuleTaskWrap moduleTask=new ModuleTaskWrap();
				moduleTask.setIdModule(idModule);
				moduleTask.setIdResult(0);
				moduleTask.setIdState(0);
				moduleTask.setIdStorage(taskFileName);
				moduleTask.setTimeWrite(new java.util.Date());
				Session session=connector.getSession();
				session.beginTransaction();
				session.save(moduleTask);
				session.getTransaction().commit();
			}else{
				logger.error("saveOrUpdateParameter секция для сохранения параметров не определена "+idSection);
			}
		}catch(Exception ex){
			logger.error("saveOfUpdateParameter Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
		return returnValue;
	}

}
