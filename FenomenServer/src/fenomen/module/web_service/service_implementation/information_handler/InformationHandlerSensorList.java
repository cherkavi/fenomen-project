package fenomen.module.web_service.service_implementation.information_handler;

import java.util.ArrayList;

import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.ModuleSensor;

import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;

public class InformationHandlerSensorList extends InformationHandler{

	@Override
	protected boolean isValid(ModuleInformation moduleInformation) {
		try{
			return (this.getNode(this.getXmlFromString(moduleInformation.getContent()), this.getXPathToTask(false)+"/sensor_list")!=null);
		}catch(Exception ex){
			return false;
		}
	}
	
	/** удалить все датчики/сенсоры по заданному модулю 
	 * @param connection - соединение с базой данных 
	 * @param moduleId - уникальный идентификатор модуля в масштабе базы данных 
	 */
/*	private boolean removeAllSensors(Connection connection, Integer moduleId){
		boolean returnValue=false;
		try{
			connection.createStatement().executeUpdate("delete from module_sensor where id_module="+moduleId);
			connection.commit();
			returnValue=true;
		}catch(Exception ex){
			logger.error("removeAllSensors Exception: "+ex.getMessage());
		}
		return returnValue;
	}
*/

	private void writeSensorsIntoDatabase(Session session, Integer idModule, ArrayList<ModuleSensor> listOfSensor){
		logger.debug("получить все датчики из базы по данному модулю ");
		try{
			// по алгоритму все сенсоры будут удалены 
			/*List<ModuleSensor> databaseListOfSensor=session.createCriteria(ModuleSensor.class).add(Restrictions.eq("idModule", idModule)).list();
			logger.debug("список датчиков которые есть в базе, но их нет в модуле ");
			ArrayList<ModuleSensor> uniqueDatabaseList=new ArrayList<ModuleSensor>();
			for(int counter=0;counter<databaseListOfSensor.size();counter++){
				if(listOfSensor.indexOf(databaseListOfSensor.get(counter))<0){
					uniqueDatabaseList.add(databaseListOfSensor.get(counter));
				}
			}
			logger.debug("удалить из базы все те,которых нет на модуле, всего: "+uniqueDatabaseList.size());
			for(int counter=0;counter<uniqueDatabaseList.size();counter++){
				session.beginTransaction();
				session.delete(uniqueDatabaseList.get(counter));
				session.getTransaction().commit();
			}
			*/
			logger.debug("Обновить датчики в базе, согласно полученным данным от модуля ");
			for(int counter=0;counter<listOfSensor.size();counter++){
				this.updateModuleSensorIntoDatabase(session, idModule, listOfSensor.get(counter));
			}
		}catch(Exception ex){
			logger.error("writeSensorsIntoDatabase Exception: "+ex.getMessage());
		}
	}
	
	@Override
	protected void process(ModuleIdentifier moduleIdentifier,
						   ModuleInformation moduleInformation) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		Document document=this.getXmlFromString(moduleInformation.getContent());
		try{
			Integer moduleId=this.getModuleIdFromIdentifier(connector.getSession(), moduleIdentifier);
			logger.debug("прочесть все сенсоры");
			Object object=this.getNode(document, this.getXPathToTask(false)+"/sensor_list");
			if(object instanceof NodeList){
				NodeList sensorList=(NodeList)object;
				logger.debug("записать список сенсоров-устройств сенсоров в базу данных");
				ArrayList<ModuleSensor> listOfSensor=new ArrayList<ModuleSensor>();
				for(int counter=0;counter<sensorList.getLength();counter++){
					if((sensorList.item(counter) instanceof Element)&&( ((Element)sensorList.item(counter)).getNodeName().trim().equalsIgnoreCase("sensor"))){
						ModuleSensor moduleSensor=this.getModuleSensorFromElement(moduleId, connector.getSession(), (Element)sensorList.item(counter));
						if(moduleSensor==null){
							logger.debug(" moduleSensor is null");
						}else{
							logger.debug("moduleSensor is NOT null");
							listOfSensor.add(moduleSensor);
						}
					}
				}
				this.writeSensorsIntoDatabase(connector.getSession(), moduleId, listOfSensor);
			}else {
				Element sensor=(Element)object;
				logger.debug("записать сенсор-устройство в базу данных");
				ModuleSensor moduleSensor=this.getModuleSensorFromElement(moduleId, connector.getSession(), sensor);
				ArrayList<ModuleSensor> listOfSensor=new ArrayList<ModuleSensor>();
				listOfSensor.add(moduleSensor);
				this.writeSensorsIntoDatabase(connector.getSession(), moduleId, listOfSensor);
			}
		}catch(Exception ex){
			logger.error(" process Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}
	
}
