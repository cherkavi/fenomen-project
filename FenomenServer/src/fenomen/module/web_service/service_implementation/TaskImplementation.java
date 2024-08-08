package fenomen.module.web_service.service_implementation;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.Module;
import database.wrap.ModuleTaskWrap;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleTask;
import fenomen.module.web_service.common.ModuleTaskContainer;
import fenomen.module.web_service.service.ITask;
import fenomen.module.web_service.service_implementation.settings.StaticSettings;
import fenomen.module.web_service.service_implementation.storage.IStorage;
import fenomen.module.web_service.service_implementation.storage.TextFileStorage;

public class TaskImplementation implements ITask{
	Logger logger=Logger.getLogger(this.getClass());
	/** максимальное число объектов в контейнере */
	private int maxContainerTaskConsist;
	
	/** место хранения файлов-заданий для отправки на сервер */
	private IStorage<String> storageTaskXml=null;
	
	
	public TaskImplementation(){
		try{
			this.maxContainerTaskConsist=(Integer)StaticSettings.getObject(StaticSettings.taskMaxInContainer);
			storageTaskXml=new TextFileStorage(StaticSettings.getPathToStorage("Task"), 
												 (String)StaticSettings.getObject(StaticSettings.taskStorageExtension));
		}catch(Exception ex){
			logger.error("constructor Exception: "+ex.getMessage());
		}
	}
	
	/** заполнить контейнер задачами с указанным предельным числом объектов 
	 * @param connector - соединение с базой данных
	 * @param idModule - уникальный идентификатор модуля 
	 * @param moduleTaskContainer - контейнер, в который должны быть добавлены прочитанные Task
	 * @param maxCount - максимальное кол-во объектов {@link ModuleTask}, которые должны быть добавлены 
	 * @return кол-во добавленных в контейнер {@link ModuleTask} 
	 */
	@SuppressWarnings("unchecked")
	private int fillModuleTaskContainer(ConnectWrap connector, int idModule, ModuleTaskContainer moduleTaskContainer, int maxCount){
		List<ModuleTaskWrap> list=(List<ModuleTaskWrap>)connector.getSession().createCriteria(ModuleTaskWrap.class)
																			  .add(Restrictions.eq("idModule", idModule))
																			  .add(Restrictions.eq("idState", new Integer(0)))
																			  .setMaxResults(maxCount).list();
		// INFO сервер.наполнение ModuleTaskContainer объектами ModuleTask
		ArrayList<ModuleTask> listOfTask=new ArrayList<ModuleTask>();
		for(int counter=0;counter<list.size();counter++){
			try{
				// прочесть XML содержимое из хранилища
				ModuleTask moduleTask=new ModuleTask(list.get(counter).getId(),storageTaskXml.read(list.get(counter).getIdStorage()));
				// установить уникальный код Task.id
				moduleTask.setId(list.get(counter).getId());
				listOfTask.add(moduleTask);
			}catch(Exception ex){
				logger.error("add ModuleTask to container Exception: "+ex.getMessage());
			}
		}
		moduleTaskContainer.setContent(listOfTask.toArray(new ModuleTask[]{}));
		return moduleTaskContainer.getContent().length;
	}
	
	@Override
	public ModuleTaskContainer getTask(ModuleIdentifier moduleIdentifier) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// получить уникальный идентификатор модуля в масштабе базы данных
			int idModule=this.getIdFromIdentifier(connector, moduleIdentifier);
			ModuleTaskContainer returnValue=new ModuleTaskContainer();
			// прочесть накопившиеся задачи для модуля
			// собрать все задачи в контейнер и выслать в качестве ответа
			if(this.fillModuleTaskContainer(connector, idModule, returnValue, this.maxContainerTaskConsist)>0){
				// задачи записаны как "новые" - при обработке на модуле должны быть подтверждены как прочитанные 
			}
			return returnValue;
		}catch(Exception ex){
			logger.error("taskProcessError Exception: "+ex.getMessage());
			return new ModuleTaskContainer();
		}finally{
			try{
				connector.close();
			}catch(Exception ex){};
		}
	}

	/** получить уникальный id модуля по идентификатору 
	 * @param connector -  соединение с базой данных 
	 * @param moduleIdentifier - Уникальный идентификатор модуля 
	 * @return 
	 * <li> <b> value&lt0 </b>  не удалось идентифицировать модуль </li>
	 * <li> <b>value&gt0</b> если модуль успешно идентифицирован </li>
	 */
	private int getIdFromIdentifier(ConnectWrap connector, ModuleIdentifier moduleIdentifier){
		int returnValue=(-1);
		try{
			Session session=connector.getSession();
			Module module=(Module)session.createCriteria(Module.class).add(Restrictions.eq("idModule", moduleIdentifier.getId())).uniqueResult();
			if(module!=null){
				returnValue=module.getId();
			}
		}catch(Exception ex){
			if(moduleIdentifier!=null){
				logger.error("getIdFromIdentifier: Exception: "+ex.getMessage()+" ModuleIdentifier is not recognized: "+moduleIdentifier.getId());
			}else{
				logger.error("getIdFromIdentifier ModuleIdentifier is null ");
			}
			
		}
		return returnValue; 
	}
	
	/** получить по уникальному идентификатору и соединению с базой объект, который содержит необходимую задачу */
	private ModuleTaskWrap getTaskById(ConnectWrap connector, Integer taskId){
		return (ModuleTaskWrap)connector.getSession().get(ModuleTaskWrap.class, taskId);
	}
	
	
	@Override
	public void taskProcessError(ModuleIdentifier moduleIdentifier,
								 Integer taskId) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// получить уникальный идентификатор модуля в масштабе базы данных
			int idModule=this.getIdFromIdentifier(connector, moduleIdentifier);
			// получить по уникальному коду задачу в базе
			ModuleTaskWrap moduleTask=this.getTaskById(connector, taskId);
			// проверить соответствие модуля и присланного идентификатора
			if(moduleTask.getIdModule()==idModule){
				// записать ошибку выполнения для задачи
				connector.getSession().beginTransaction();
				moduleTask.setIdState(2);
				moduleTask.setIdResult(3);
				connector.getSession().update(moduleTask);
				connector.getSession().getTransaction().commit();
				// TODO сервер. перенести из module_task в архив ( все у кого id_state=2)
			}else{
				throw new Exception("taskProcessError Module is not Recognized "+moduleIdentifier.getId()+" for Task.id="+taskId);
			}
		}catch(Exception ex){
			logger.error("taskProcessError Exception: "+ex.getMessage());
		}finally{
			try{
				connector.close();
			}catch(Exception ex){};
		}
	}

	@Override
	public void taskProcessNotFound(ModuleIdentifier moduleIdentifier,
									Integer taskId) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// получить уникальный идентификатор модуля в масштабе базы данных
			int idModule=this.getIdFromIdentifier(connector, moduleIdentifier);
			// получить по уникальному коду задачу в базе
			ModuleTaskWrap moduleTask=this.getTaskById(connector, taskId);
			// проверить соответствие модуля и присланного идентификатора
			if(moduleTask.getIdModule()==idModule){
				// записать что задача не была найдена 
				connector.getSession().beginTransaction();
				moduleTask.setIdState(2);
				moduleTask.setIdResult(2);
				connector.getSession().update(moduleTask);
				connector.getSession().getTransaction().commit();
				// TODO сервер. перенести из module_task в архив ( все у кого id_state=2)
			}else{
				throw new Exception("taskProcessNotFound Module is not Recognized "+moduleIdentifier.getId()+" for Task.id="+taskId);
			}
		}catch(Exception ex){
			logger.error("taskProcessNotFound Exception: "+ex.getMessage());
		}finally{
			try{
				connector.close();
			}catch(Exception ex){};
		}
	}

	@Override
	public void taskProcessOk(ModuleIdentifier moduleIdentifier, 
							  Integer taskId) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// получить уникальный идентификатор модуля в масштабе базы данных
			int idModule=this.getIdFromIdentifier(connector, moduleIdentifier);
			// получить по уникальному коду задачу в базе
			ModuleTaskWrap moduleTask=this.getTaskById(connector, taskId);
			// проверить соответствие модуля и присланного идентификатора
			if(moduleTask.getIdModule()==idModule){
				// записать что задача была успешно выполнена 
				connector.getSession().beginTransaction();
				moduleTask.setIdState(2);
				moduleTask.setIdResult(1);
				connector.getSession().update(moduleTask);
				connector.getSession().getTransaction().commit();
				// TODO сервер.перенести из module_task в архив ( все у кого id_state=2)
			}else{
				throw new Exception("taskProcessOk Module is not Recognized "+moduleIdentifier.getId()+" for Task.id="+taskId);
			}
		}catch(Exception ex){
			logger.error("taskProcessOk Exception: "+ex.getMessage());
		}finally{
			try{
				connector.close();
			}catch(Exception ex){};
		}
	}

	@Override
	public void tookTask(ModuleIdentifier moduleIdentifier, Integer taskId) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Session session=connector.getSession();
			ModuleTaskWrap moduleTaskWrap=(ModuleTaskWrap)session.get(ModuleTaskWrap.class, taskId);
			if(moduleTaskWrap!=null){
				logger.debug("установить для задачи состояние - забрано модулем для выполнения Id:"+taskId);
				moduleTaskWrap.setIdState(1);
				session.beginTransaction();
				session.update(moduleTaskWrap);
				session.getTransaction().commit();
			}
		}catch(Exception ex){
			logger.error("tookTask Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}

}
