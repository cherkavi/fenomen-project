package fenomen.server.controller.client.view.settings;

/** интерфейс по поиску модуля */
public interface IFindModule {
	/** наложить фильтр на список 
	 * @param moduleId - ( nullable ) уникальный идентификатор модуля 
	 * @param address - ( nullable ) адрес модуля 
	 */
	public void filter(String moduleId, String address);
}
