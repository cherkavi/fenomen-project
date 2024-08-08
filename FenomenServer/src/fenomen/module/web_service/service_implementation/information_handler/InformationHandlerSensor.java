package fenomen.module.web_service.service_implementation.information_handler;

import org.hibernate.Session;
import org.w3c.dom.Element;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.ModuleSensor;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;

public class InformationHandlerSensor extends InformationHandler {

	@Override
	protected boolean isValid(ModuleInformation moduleInformation) {
		try{
			return (this.getNode(this.getXmlFromString(moduleInformation.getContent()), this.getXPathToTask(false)+"/sensor")!=null);
		}catch(Exception ex){
			return false;
		}
	}

	@Override
	protected void process(ModuleIdentifier moduleIdentifier,
						   ModuleInformation moduleInformation) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Session session=connector.getSession();
			int moduleId=this.getModuleIdFromIdentifier(session, moduleIdentifier);
			ModuleSensor moduleSensor=this.getModuleSensorFromElement(moduleId, 	
																	  session, 
																	  (Element)this.getNode(this.getXmlFromString(moduleInformation.getContent()), 
																	  this.getXPathToTask(false)+"/sensor") );
			// обновить moduleSensor в базе данных 
			this.updateModuleSensorIntoDatabase(session, moduleId, moduleSensor);
			// получить 
		}catch(Exception ex){
			logger.error("process Exception:"+ex.getMessage());
		}finally{
			connector.close();
		}
	}

}
