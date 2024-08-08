package fenomen.module.web_service.service_implementation.information_handler;

import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import database.ConnectWrap;
import database.StaticConnector;

import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;

/** обработчик для полученных от модуля настроек ThreadHeartBeat */
public class InformationHandlerSettingsHeartBeat extends InformationHandler{
	/** имя параметра в базе данных, в таблице module_settings_parameter */
	private final String idTimeWait="time_wait";
	/** имя параметра в базе данных, в таблице module_settings_parameter */
	private final String idTimeError="time_error";
	
	/** код секции */
	private Integer kodSection=null;
	/** код параметра время ожидания */
	private int kodTimeWait=0;
	/** код параметра время повторной передачи после ошибки */
	private int kodTimeError=0;
	
	/** обработчик для полученных от модуля настроек ThreadHeartBeat */
	public InformationHandlerSettingsHeartBeat(){
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// получить код секции
			kodSection=this.getSectionKod(connector.getSession(), this.idInformation);
			// получить код параметра TimeWait
			kodTimeWait=this.getParameterKod(connector.getSession(), kodSection, idTimeWait);
			// получить код параметра TimeError
			kodTimeError=this.getParameterKod(connector.getSession(), kodSection, idTimeError);
		}catch(Exception ex){
			logger.error(" constructor Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}

	@Override
	protected boolean isValid(ModuleInformation moduleInformation) {
		try{
			return (this.getNode(this.getXmlFromString(moduleInformation.getContent()), this.getXPathToTask(false)+"/heart_beat")!=null);
		}catch(Exception ex){
			return false;
		}
	}

	@Override
	protected void process(ModuleIdentifier moduleIdentifier, ModuleInformation moduleInformation) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		Document document=this.getXmlFromString(moduleInformation.getContent());
		try{
			String timeWait=((Element)this.getNode(document, this.getXPathToTask(false)+"/heart_beat/time_wait")).getTextContent().trim();
			// сохранить полученный параметр 
			String timeError=((Element)this.getNode(document, this.getXPathToTask(false)+"/heart_beat/time_error")).getTextContent().trim();
			// время записи параметров 
			Date dateWrite=new Date();
			Integer idModule=this.getModuleIdFromIdentifier(connector.getSession(), moduleIdentifier);
			// сохранить полученный параметр
			this.saveParameter(connector.getSession(), idModule, this.kodSection, this.kodTimeWait, timeWait, dateWrite);
			// сохранить полученный параметр
			this.saveParameter(connector.getSession(), idModule, this.kodSection, this.kodTimeError, timeError, dateWrite);
		}catch(Exception ex){
			logger.error("process Error: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}

}
