package fenomen.module.web_service.service_implementation.information_handler;

import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.ModuleTaskWrap;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;

public class InformationHandlerResponseTask extends InformationHandler {
	
	

	@Override
	protected boolean isValid(ModuleInformation moduleInformation) {
		try{
			return (this.getNode(this.getXmlFromString(moduleInformation.getContent()), this.getXPathToTask(false)+"/value")!=null);
		}catch(Exception ex){
			return false;
		}
	}

	@Override
	protected void process(ModuleIdentifier moduleIdentifier,
						   ModuleInformation moduleInformation) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Document document=this.getXmlFromString(moduleInformation.getContent());
			int id=Integer.parseInt( ((Element)this.getNode(document, this.getXPathToTask(false)+"/id")).getTextContent());
			String value=((Element)this.getNode(document, this.getXPathToTask(false)+"/value")).getTextContent().trim();
			int id_result=(value.equalsIgnoreCase("OK"))?1:2;
			Session session=connector.getSession();
			ModuleTaskWrap moduleTask=(ModuleTaskWrap)session.get(ModuleTaskWrap.class,new Integer(id));
			logger.debug("ModuleTask.id="+id+"    Result:"+id_result); 
			// установить для задачи флаг состояния как выполненный модулем
			moduleTask.setIdState(2);
			// установить для задачи флаг результата выполнения модулем 
			moduleTask.setIdResult(id_result);
			session.beginTransaction();
			session.update(moduleTask);
			session.getTransaction().commit();
		}catch(Exception ex){
			logger.error("process Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}

}
