package fenomen.module.web_service.service;

import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleTaskContainer;

/** интерфейс, который реализует функции для получения Task и выдачи ответа на Task */
public interface ITask {
	
	/** запрос модулем очередного задания на отработку 
	 * @param moduleIdentifier - уникальный идентификатор модуля 
	 * @return ModuleTaskContainer - задача для модуля 
	 * */
	public ModuleTaskContainer getTask(ModuleIdentifier moduleIdentifier);
	
	/** подтверждение модулем получения задания - модуль забрал задачу  
	 * @param moduleIdentifier - уникальный идентификатор модуля 
	 * @param taskId - уникальный идентификатор полученного задания ( в контейнере заданий несколько )
	 */
	public void tookTask(ModuleIdentifier moduleIdentifier, Integer taskId);
	
	/** подтверждение модулем отработки задания - задача найдена и выполнена успешно 
	 * @param moduleIdentifier - уникальный идентификатор модуля 
	 * @param taskId - уникальный идентификатор полученного задания ( в контейнере заданий несколько )
	 */
	public void taskProcessOk(ModuleIdentifier moduleIdentifier, Integer taskId);
	
	
	/** подтверждение модулем отработки задания - задача найдена и не выполнена  
	 * @param moduleIdentifier - уникальный идентификатор модуля 
	 * @param taskId - уникальный идентификатор полученного задания ( в контейнере заданий несколько )
	 */
	public void taskProcessError(ModuleIdentifier moduleIdentifier, Integer taskId);

	
	/** подтверждение модулем отработки задания - задача не найдена  
	 * @param moduleIdentifier - уникальный идентификатор модуля 
	 * @param taskId - уникальный идентификатор полученного задания ( в контейнере заданий несколько )
	 */
	public void taskProcessNotFound(ModuleIdentifier moduleIdentifier, Integer taskId);
	
}
