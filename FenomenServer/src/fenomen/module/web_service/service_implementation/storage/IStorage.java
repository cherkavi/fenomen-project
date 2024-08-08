package fenomen.module.web_service.service_implementation.storage;

public interface IStorage<T> {
	/** сохранить объект 
	 * @param object - объект, который должен быть сохранен в хранилище ( Serializable ) 
	 * @return - уникальный идентификатор сохраненного значения
	 * @throws  выбрасывает исключение, если не удалось сохранить 
	 */
	public String save(T object) throws Exception ;
	
	/**
	 * прочесть объект из хранилища  
	 * @param identifier уникальный идентификатор объекта
	 * @return объект, который прочитан из хранилища ( instanceof Serializable ) 
	 * @throws если не удалось прочесть значение 
	 */
	public T read(String identifier) throws Exception ;
}
