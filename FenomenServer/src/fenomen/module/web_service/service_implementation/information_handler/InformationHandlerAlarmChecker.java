package fenomen.module.web_service.service_implementation.information_handler;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.ModuleAlarmCheckerWrap;
import database.wrap.ModuleTaskWrap;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;
import fenomen.server.controller.server.utility.xml_data.task.TaskData;
import fenomen.server.controller.server.utility.xml_data.task.TaskDataAlarmCheckerAdd;

/** обработчик для поступающих от модуля информационных сообщениях обо всех зарегестрированных Alarm Checker-ах */
public class InformationHandlerAlarmChecker extends InformationHandler{

	@Override
	protected boolean isValid(ModuleInformation moduleInformation) {
		try{
			return (this.getNode(this.getXmlFromString(moduleInformation.getContent()), this.getXPathToTask(false)+"/alarm_checker")!=null);
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
			Document document=this.getXmlFromString(moduleInformation.getContent());
			
			logger.debug("получено сообщение от модуля: "+moduleId);
			int addressModbus=this.getModbusFromModuleInformation(document);
			logger.debug("получено сообщение от датчика/сенсора на модуле по адресу:"+addressModbus);
			List<ModuleAlarmCheckerWrap> checkerFromModule=this.getAlarmCheckerFromModule(moduleId, document);
			logger.debug("список AlarmChecker-ов присланных от модуля:"+checkerFromModule.size());

			if(isAlarmCheckerList(document)){
				logger.debug("получить список AlarmChecker-ов записанных в базе");
				List<ModuleAlarmCheckerWrap> checkerFromDatabase=this.getAlarmCheckerFromDatabase(moduleId,session);
				logger.debug("проанализировать записи, которые есть в базе данных, но их нет в модуле - отправить на модуль в качестве заданий"); 
				for(int counter=0;counter<checkerFromDatabase.size();counter++){
					if(checkerFromModule.indexOf(checkerFromDatabase.get(counter))<0){
						logger.info("найден AlarmChecker запись, которая есть в базе данных, но ее нет в модуле ");
						if(checkerFromDatabase.get(counter).getIdState()==2){
							// INFO сервер.место установки повторной отправки для AlarmChecker
							// сервер.положить задание на TaskData AddAlarmChecker
								// обновить AlarmChecker, установив состояние - IdState=0 (новая задача) 
							checkerFromDatabase.get(counter).setIdState(0);
							session.beginTransaction();
							session.update(checkerFromDatabase.get(counter));
							session.getTransaction().commit();
								// создать XML объект Task для посылки на модуль
							TaskDataAlarmCheckerAdd checkerAdd=new TaskDataAlarmCheckerAdd(addressModbus, 
																						   checkerFromDatabase.get(counter).getSensorRegisterAddress(),
																						   checkerFromDatabase.get(counter).getId());
								// сохранить в Task объект в хранилище 
							String checkerFileNameInStorage=checkerAdd.saveXmlAsFile(checkerAdd.getPathToStorage(TaskData.pathTask));
								// сохранить новый объект в TaskModule
							ModuleTaskWrap moduleTask=new ModuleTaskWrap();
							moduleTask.setIdModule(moduleId);
							moduleTask.setIdState(0);
							moduleTask.setIdResult(0);
							moduleTask.setIdStorage(checkerFileNameInStorage);
							moduleTask.setTimeWrite(new java.util.Date());
							session.beginTransaction();
							session.save(moduleTask);
							session.getTransaction().commit();
						}
						if(checkerFromDatabase.get(counter).getIdState()==1){
							// модуль забрал задание ( помечено как забранное) но не добавил себе в Checker-ы (если произошла перезагрузка - должен был сброссть данный флаг с 1 в 0 ) - ситуация довольно странная 
						}
					}
				}
				logger.debug("проанализировать записи, которые есть в модуле, но их нет в базе данных ");
				for(int counter=0;counter<checkerFromModule.size();counter++){
					if(checkerFromDatabase.indexOf(checkerFromModule.get(counter))<0){
						checkerFromModule.get(counter).setId(0);
						checkerFromModule.get(counter).setIdState(-1);
						session.beginTransaction();
						session.save(checkerFromModule.get(counter));
						session.getTransaction().commit();
						logger.warn("найден AlarmChecker который есть на модуле, но нет в базе, сохранен "+checkerFromModule.get(counter).getId());
					}
				}
			}else if(isAlarmCheckerAddConfirm(document)){
				Integer register=null;
				try{
					register=Integer.parseInt(((Element)this.getNode(document, this.getXPathToTask(false)+"/alarm_checker/add_confirm/register")).getTextContent());
				}catch(Exception ex){
					logger.warn("Path /alarm_checker/add_confirm/register is not recognized ");
				};

				Integer id_file=null;
				try{
					id_file=Integer.parseInt(((Element)this.getNode(document, this.getXPathToTask(false)+"/alarm_checker/add_confirm/id_file")).getTextContent());
				}catch(Exception ex){
					logger.warn("Path /alarm_checker/add_confirm/id_file is not recognized ");
				};
				
				String id_on_module=null;
				try{
					id_on_module=((Element)this.getNode(document, this.getXPathToTask(false)+"/alarm_checker/add_confirm/id_on_module")).getTextContent();
				}catch(Exception ex){
					logger.warn("Path /alarm_checker/add_confirm/id_on_module is not recognized ");
				};
				if((id_file==null)||(id_on_module==null)){
					throw new Exception("Important field was not finded into ModuleInformation");
				}else{
					saveIdOnModule(id_file, id_on_module, addressModbus, register);
				}
			}else{
				logger.warn("Not recognized Information from module:"+moduleIdentifier.getId());
			}
			
		}catch(Exception ex){
			logger.error("process Exception ");
		}finally{
			connector.close();
		}
	}

	/** сохранить подтверждение того, что модуль получил задание на добавление Checker-a 
	 * @param idFile - уникальный идентификатор записи в таблице module_alarm_checker
	 * @param idOnModule - уникальный идентификатор файла на модуле
	 * @param addressModbus - адрес устройства в сети Modbus
	 * @param addressRegister - адрес регистра на устройстве 
	 */
	private void saveIdOnModule(Integer idFile, 
								String idOnModule,
								int addressModbus,
								int addressRegister) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Session session=connector.getSession();
			ModuleAlarmCheckerWrap returnValue=(ModuleAlarmCheckerWrap)session.get(ModuleAlarmCheckerWrap.class, idFile);
			returnValue.setSensorModbusIdOnDevice(this.convertFileNameToInteger(idOnModule));
			if(returnValue.getSensorModbusAddress()!=addressModbus){
				logger.error("saveIdOnModule: Modbus address not equals with address into record ");
			}else{
				session.beginTransaction();
				session.update(returnValue);
				session.getTransaction().commit();
			}
		}catch(Exception ex){
			logger.error("saveIdOnModule: saveIdOnModule Exception:"+ex.getMessage());
		}finally{
			connector.close();
		}
	}
	
	private int convertFileNameToInteger(String fileName){
		int returnValue=(-1);
		try{
			returnValue=Integer.parseInt(fileName.substring(0,fileName.indexOf(".")));
		}catch(Exception ex){
			logger.warn("alarm_checker/add_confirm/id_on_module is not recognized ");
		}
		return returnValue;
	}

	/** является ли присланный от модуля пакет носителем списка AlarmChecker-ов */
	private boolean isAlarmCheckerList(Document document){
		if(this.getNode(document, this.getXPathToTask(false)+"/alarm_checker/list")!=null){
			return true;
		}else{
			return false;
		}
	}
	
	/** является ли присланный от модуля пакет носителем подтверждения о добавлении AlarmChecker-a */
	private boolean isAlarmCheckerAddConfirm(Document document){
		if(this.getNode(document, this.getXPathToTask(false)+"/alarm_checker/add_confirm")!=null){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * @param moduleInformation - переданное информационное сообщение от сервера  
	 * @return возвращает уникальный номер ID sensor <b>(-1 - в случае не нахождения )</b> 
	 */ 
	private int getModbusFromModuleInformation(Document document) {
		int returnValue=(-1);
		try{
			returnValue= Integer.parseInt( ((Element)this.getNode(document, this.getXPathToTask(false)+"/alarm_checker/id_modbus")).getTextContent().trim());
		}catch(Exception ex){
			logger.error("getSensorIdFromModuleInformation Exception: "+ex.getMessage());
		}
		return returnValue;
	}
	
	
	/** получить список AlarmChecker-ов на основании данных, которые были получены из модуля */
	private ArrayList<ModuleAlarmCheckerWrap> getAlarmCheckerFromModule(int moduleId, Document document){
		// получить все сенсоры
		Object object=this.getNode(document, this.getXPathToTask(false)+"/alarm_checker/list/checker");
		ArrayList<ModuleAlarmCheckerWrap> returnValue=new ArrayList<ModuleAlarmCheckerWrap>();
		if(object instanceof NodeList){
			NodeList listOfNode=(NodeList)object;
			for(int counter=0;counter<listOfNode.getLength();counter++){
				Element element=(Element)listOfNode.item(counter);
				ModuleAlarmCheckerWrap moduleChecker=this.getAlarmCheckerFromElement(moduleId, element);
				if(moduleChecker!=null){
					returnValue.add(moduleChecker);
				}
			}
		}else{
			Element element=(Element)object;
			ModuleAlarmCheckerWrap moduleChecker=this.getAlarmCheckerFromElement(moduleId, element);
			if(moduleChecker!=null){
				returnValue.add(moduleChecker);
			}
		}
		return returnValue;
	}
	
	
	
	/** получить список AlarmChecker-ов записанных в базе */
	@SuppressWarnings("unchecked")
	private List<ModuleAlarmCheckerWrap> getAlarmCheckerFromDatabase(int moduleId, Session session){
		List<ModuleAlarmCheckerWrap> returnValue=null;
		try{
			returnValue=(List<ModuleAlarmCheckerWrap>)session.createCriteria(ModuleAlarmCheckerWrap.class).add(Restrictions.eq("idModule", new Integer(moduleId))).list();
		}catch(Exception ex){
			logger.error("getAlarmCheckerFromDatabase Exception: "+ex.getMessage());
		}
		return returnValue;
	}
	
}
