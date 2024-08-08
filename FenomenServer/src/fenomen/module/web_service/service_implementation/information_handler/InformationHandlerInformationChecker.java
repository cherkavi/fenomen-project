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
import database.wrap.ModuleInformationCheckerWrap;
import database.wrap.ModuleTaskWrap;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;
import fenomen.server.controller.server.utility.XmlData;
import fenomen.server.controller.server.utility.xml_data.task.TaskDataInformationCheckerAdd;

/** ���������� ��� ����������� �� ������ �������������� ���������� ��� ���� ������������������ Information Checker-�� */
public class InformationHandlerInformationChecker extends InformationHandler{

	@Override
	protected boolean isValid(ModuleInformation moduleInformation) {
		try{
			return (this.getNode(this.getXmlFromString(moduleInformation.getContent()), this.getXPathToTask(false)+"/information_checker")!=null);
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
			logger.debug("�������� ������ InformationChecker-�� ���������� �� ������");
			int id_modbus=this.getSensorIdFromModuleInformation(moduleInformation);
			List<ModuleInformationCheckerWrap> checkerFromModule=this.getInformationCheckerFromModule(moduleId, moduleInformation);
			logger.debug("�������� ������ InformationChecker-�� ���������� � ����");
			List<ModuleInformationCheckerWrap> checkerFromDatabase=this.getInformationCheckerFromDatabase(moduleId,session);
			logger.debug("���������������� ������, ������� ���� � ���� ������, �� �� ��� � ������"); 
			for(int counter=0;counter<checkerFromDatabase.size();counter++){
				if(checkerFromModule.indexOf(checkerFromDatabase.get(counter))<0){
					logger.info("������ InformationChecker ������, ������� ���� � ���� ������, �� �� ��� � ������ ");
					if(checkerFromDatabase.get(counter).getIdState()==2){
						// INFO ������.����� ��������� ��������� �������� ��� InformationChecker ( ������� ���� ������, ������� ������������� �� ���������, �� ��� �� ������� )
						session.beginTransaction();
						checkerFromDatabase.get(counter).setIdState(0);
						session.update(checkerFromDatabase.get(counter));
						session.getTransaction().commit();
						// ������.�������� ������� � ������� TaskData ( ��� AddInformationChecker ) ��� ���������� ������ ������ 
							// ��������� � ��������� Task ��� ���������� InformationChecker
						TaskDataInformationCheckerAdd checkerAdd=new TaskDataInformationCheckerAdd(id_modbus, 
																								   checkerFromDatabase.get(counter).getSensorRegisterAddress(), 
																								   checkerFromDatabase.get(counter).getId());
						String checkerFileNameInStorage=checkerAdd.saveXmlAsFile(checkerAdd.getPathToStorage(XmlData.pathTask));
							// ������� ������ � ������� module_task
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
						// ������ ������ ������� ( �������� ��� ���������) �� �� ������� ���� � Checker-� (���� ��������� ������������ - ������ ��� �������� ������ ���� � 1 � 0 ) - �������� �������� �������� 
					}
				}
			}
			logger.debug("���������������� ������, ������� ���� � ������, �� �� ��� � ���� ������ ");
			for(int counter=0;counter<checkerFromModule.size();counter++){
				if(checkerFromDatabase.indexOf(checkerFromModule.get(counter))<0){
					checkerFromModule.get(counter).setId(0);
					checkerFromModule.get(counter).setIdState(-1);
					session.beginTransaction();
					session.save(checkerFromModule.get(counter));
					session.getTransaction().commit();
					logger.warn("������ InformationChecker ������� ���� �� ������, �� ��� � ����, �������� "+checkerFromModule.get(counter).getId());
				}
			}
		}catch(Exception ex){
			logger.error("process Exception ");
		}finally{
			connector.close();
		}
	}

	/** 
	 * @param moduleInformation - ���������� �������������� ��������� �� �������  
	 * @return ���������� ����� ������/������� � ���� Modbus <b>(-1 - � ������ �� ���������� )</b> 
	 * */
	private int getSensorIdFromModuleInformation(ModuleInformation moduleInformation) {
		Document document=this.getXmlFromString(moduleInformation.getContent());
		int returnValue=(-1);
		try{
			returnValue= Integer.parseInt( ((Element)this.getNode(document, this.getXPathToTask(false)+"/information_checker/id_modbus")).getTextContent().trim());
		}catch(Exception ex){
			logger.error("getSensorIdFromModuleInformation Exception: "+ex.getMessage());
		}
		return returnValue;
	}

	/** �������� ������ AlarmChecker-�� �� ��������� ������ �� {@link ModuleInformation}*/
	private ArrayList<ModuleInformationCheckerWrap> getInformationCheckerFromModule(int moduleId, ModuleInformation moduleInformation){
		Document document=this.getXmlFromString(moduleInformation.getContent());
		// �������� ��� �������
		Object object=this.getNode(document, this.getXPathToTask(false)+"/information_checker/list/checker");
		ArrayList<ModuleInformationCheckerWrap> returnValue=new ArrayList<ModuleInformationCheckerWrap>();
		if(object instanceof NodeList){
			NodeList listOfNode=(NodeList)object;
			for(int counter=0;counter<listOfNode.getLength();counter++){
				Element element=(Element)listOfNode.item(counter);
				ModuleInformationCheckerWrap moduleChecker=this.getInformationCheckerFromElement(moduleId, element);
				if(moduleChecker!=null){
					returnValue.add(moduleChecker);
				}
			}
		}else{
			Element element=(Element)object;
			ModuleInformationCheckerWrap moduleChecker=this.getInformationCheckerFromElement(moduleId, element);
			if(moduleChecker!=null){
				returnValue.add(moduleChecker);
			}
		}
		return returnValue;
	}
	
	
	
	/** �������� ������ AlarmChecker-�� ���������� � ���� */
	@SuppressWarnings("unchecked")
	private List<ModuleInformationCheckerWrap> getInformationCheckerFromDatabase(int moduleId, Session session){
		List<ModuleInformationCheckerWrap> returnValue=null;
		try{
			returnValue=(List<ModuleInformationCheckerWrap>)session.createCriteria(ModuleInformationCheckerWrap.class).add(Restrictions.eq("idModule", new Integer(moduleId))).list();
		}catch(Exception ex){
			logger.error("getInformationCheckerFromDatabase Exception: "+ex.getMessage());
		}
		return returnValue;
	}
	
}
