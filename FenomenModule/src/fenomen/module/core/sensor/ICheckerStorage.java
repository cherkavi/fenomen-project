package fenomen.module.core.sensor;

import java.io.Serializable;

/** интерфейс по сохранению/восстановлению/удалению необходимых в системе файлов */
public interface ICheckerStorage<T extends Serializable> {
	/** прочесть из хранилища по заданному индексу объект 
	 * @param index - индекс объекта 
	 * @return объект, либо null
	 */
	public T readFromStorage(int index);
	
	/** сохранить в хранилище объект и вернуть его индекс 
	 * @param value - объект, который должен быть сохранен в хранилище
	 * @return 
	 * <ul>
	 * 	<li><b>null</b> - если объект не сохранен, </li> 
	 * 	<li><b>not null </b> - либо же уникальный идентификатор в хранилище по данному объекту </li>
	 * </ul>
	 */
	public String saveToStorage(T value);
	
	/** получить кол-во объектов в хранилище */
	public int getStorageSize();
	
	/** удалить из хранилища по указанному индексу 
	 * @param index - индекс в хранилище
	 * @return true - успешно удален, false - ошибка удаления
	 */
	public boolean removeFromStorage(int index);
}
