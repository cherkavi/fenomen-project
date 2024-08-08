package fenomen.module.web_service.service_implementation.information_handler;

import java.util.Date;

import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import database.ConnectWrap;
import database.StaticConnector;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;

/** объект-обработчик для потока модуля ThreadInformation */
public class InformationHandlerSettingsInformation extends InformationHandler{

	/** имя параметра в базе данных, в таблице module_settings_parameter */
	private final String idTimeWait="time_wait";
	/** имя параметра в базе данных, в таблице module_settings_parameter */
	private final String idTimeError="time_error";
	/** имя параметра в базе данных, в таблице module_settings_parameter */
	private final String idMaxCount="max_count";
	
	/** код секции */
	private Integer kodSection=null;
	/** код параметра время ожидания */
	private int kodTimeWait=0;
	/** код параметра время повторной передачи после ошибки */
	private int kodTimeError=0;
	/** код параметра максимального числа информационных сообщений в очереди на отправку */
	private int kodMaxCount=0;
	
	
	public InformationHandlerSettingsInformation(){
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			kodSection=this.getSectionKod(connector.getSession(), this.idInformation);
			kodTimeWait=this.getParameterKod(connector.getSession(), kodSection, idTimeWait);
			kodTimeError=this.getParameterKod(connector.getSession(), kodSection, idTimeError);
			kodMaxCount=this.getParameterKod(connector.getSession(), kodSection, idMaxCount);
		}catch(Exception ex){
			logger.error(" constructor Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}
	
	@Override
	protected boolean isValid(ModuleInformation moduleInformation) {
		try{
			return (this.getNode(this.getXmlFromString(moduleInformation.getContent()), this.getXPathToTask(false)+"/information")!=null);
		}catch(Exception ex){
			return false;
		}
	}

	@Override
	protected void process(ModuleIdentifier moduleIdentifier,
						   ModuleInformation moduleInformation) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		Document document=this.getXmlFromString(moduleInformation.getContent()); 
		try{
			// прочесть все параметры из объекта
			String timeWait=((Element)this.getNode(document, this.getXPathToTask(false)+"/information/time_wait")).getTextContent().trim();
			String timeError=((Element)this.getNode(document, this.getXPathToTask(false)+"/information/time_error")).getTextContent().trim();
			String maxCount=((Element)this.getNode(document, this.getXPathToTask(false)+"/information/max_count")).getTextContent().trim();
			
			// сохранить параметры в базе
			Session session=connector.getSession();
			Date timeWrite=new Date();
			Integer idModule=this.getModuleIdFromIdentifier(session, moduleIdentifier);
			this.saveParameter(session, idModule, this.kodSection, this.kodTimeWait, timeWait, timeWrite);
			this.saveParameter(session, idModule, this.kodSection, this.kodTimeError, timeError, timeWrite);
			this.saveParameter(session, idModule, this.kodSection, this.kodMaxCount, maxCount, timeWrite);
			
		}catch(Exception ex){
			logger.error(" process Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}

}
