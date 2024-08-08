package fenomen.module.web_service.service;

import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ContainerModuleInformation;
import fenomen.module.web_service.common.TransportChecker;

/** оповещение сервера информационными событи€ми */
public interface IInformation {
	/** флаг возврата, который "говорит" об успешной передаче данных на сервер */
	public static final String returnOk="return_ok";
	/** флаг возврата, который "говорит" об ошибочной передаче данных на сервер */
	public static final String returnError="return_error";
	
	/** оповещение сервера информационными событи€ми
	 * @param moduleIdentifier - уникальный идентификатор модул€ 
	 * @param moduleInformation - информаци€ дл€ передачи 
	 * @return - возвращаемое значение сервером  
	 */
	public String sendInformation(ModuleIdentifier moduleIdentifier, ContainerModuleInformation moduleInformation);
	
	/** получить от сервера объект-провер€ющий дл€ событий InformationChecker 
	 * @param moduleIdentifier - уникальный идентификатор модул€ 
	 * @param fileId - уникальный идентификатор объекта InformationChecker 
	 * @return - объект дл€ данного модул€ по указанному идентификатору 
	 */
	public TransportChecker getInformationCheckerById(ModuleIdentifier moduleIdentifier, String fileId);
	
	/** подтверждение в получении от сервера файла  
	 * @param moduleIdentifier - уникальный идентификатор модул€ 
	 * @param fileId - уникальный идентификатор объекта InformationChecker 
	 * @return - возвращаемое значение
	 * <li><b>return_ok {@link IInformation#returnOk}</b> - успешно обработано сервером </li>
	 * <li><b>return_Error {@link IInformation#returnError}</b> - ошибка обработки сервером </li>
	 */
	public String confirmInformationCheckerGet(ModuleIdentifier moduleIdentifier, String fileId);
	
}
