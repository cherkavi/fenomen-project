package fenomen.module.web_service.service_implementation.information_handler;

import java.util.Date;

import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import database.ConnectWrap;
import database.StaticConnector;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;

/** объект-обработчик для настроек потока Thread */
public class InformationHandlerSettingsSensor extends InformationHandler{
	/** имя параметра в базе данных, в таблице module_settings_parameter */
	private final String idTimeWait="time_wait";
	/** код секции */
	private Integer kodSection=null;
	/** код параметра время ожидания */
	private int kodTimeWait=0;

	/** объект-обработчик для настроек потока Thread */
	public InformationHandlerSettingsSensor(){
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			kodSection=this.getSectionKod(connector.getSession(), this.idSensor);
			kodTimeWait=this.getParameterKod(connector.getSession(), kodSection, idTimeWait);
		}catch(Exception ex){
			logger.error(" constructor Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
		
	}
	
	@Override
	protected boolean isValid(ModuleInformation moduleInformation) {
		try{
			return (this.getNode(this.getXmlFromString(moduleInformation.getContent()), this.getXPathToTask(false)+"/sensor_thread")!=null);
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
			String timeWait=((Element)this.getNode(document, this.getXPathToTask(false)+"/sensor_thread/time_wait")).getTextContent().trim();
			
			// сохранить параметры в базе
			Session session=connector.getSession();
			Date timeWrite=new Date();
			Integer idModule=this.getModuleIdFromIdentifier(session, moduleIdentifier);
			this.saveParameter(session, idModule, this.kodSection, this.kodTimeWait, timeWait, timeWrite);
			
		}catch(Exception ex){
			logger.error(" process Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}

}
