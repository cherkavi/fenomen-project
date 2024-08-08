package fenomen.module.web_service.service_implementation;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.Module;
import database.wrap.ModuleTaskWrap;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleTask;
import fenomen.module.web_service.common.ModuleTaskContainer;
import fenomen.module.web_service.service.ITask;
import fenomen.module.web_service.service_implementation.settings.StaticSettings;
import fenomen.module.web_service.service_implementation.storage.IStorage;
import fenomen.module.web_service.service_implementation.storage.TextFileStorage;

public class TaskImplementation implements ITask{
	Logger logger=Logger.getLogger(this.getClass());
	/** ������������ ����� �������� � ���������� */
	private int maxContainerTaskConsist;
	
	/** ����� �������� ������-������� ��� �������� �� ������ */
	private IStorage<String> storageTaskXml=null;
	
	
	public TaskImplementation(){
		try{
			this.maxContainerTaskConsist=(Integer)StaticSettings.getObject(StaticSettings.taskMaxInContainer);
			storageTaskXml=new TextFileStorage(StaticSettings.getPathToStorage("Task"), 
												 (String)StaticSettings.getObject(StaticSettings.taskStorageExtension));
		}catch(Exception ex){
			logger.error("constructor Exception: "+ex.getMessage());
		}
	}
	
	/** ��������� ��������� �������� � ��������� ���������� ������ �������� 
	 * @param connector - ���������� � ����� ������
	 * @param idModule - ���������� ������������� ������ 
	 * @param moduleTaskContainer - ���������, � ������� ������ ���� ��������� ����������� Task
	 * @param maxCount - ������������ ���-�� �������� {@link ModuleTask}, ������� ������ ���� ��������� 
	 * @return ���-�� ����������� � ��������� {@link ModuleTask} 
	 */
	@SuppressWarnings("unchecked")
	private int fillModuleTaskContainer(ConnectWrap connector, int idModule, ModuleTaskContainer moduleTaskContainer, int maxCount){
		List<ModuleTaskWrap> list=(List<ModuleTaskWrap>)connector.getSession().createCriteria(ModuleTaskWrap.class)
																			  .add(Restrictions.eq("idModule", idModule))
																			  .add(Restrictions.eq("idState", new Integer(0)))
																			  .setMaxResults(maxCount).list();
		// INFO ������.���������� ModuleTaskContainer ��������� ModuleTask
		ArrayList<ModuleTask> listOfTask=new ArrayList<ModuleTask>();
		for(int counter=0;counter<list.size();counter++){
			try{
				// �������� XML ���������� �� ���������
				ModuleTask moduleTask=new ModuleTask(list.get(counter).getId(),storageTaskXml.read(list.get(counter).getIdStorage()));
				// ���������� ���������� ��� Task.id
				moduleTask.setId(list.get(counter).getId());
				listOfTask.add(moduleTask);
			}catch(Exception ex){
				logger.error("add ModuleTask to container Exception: "+ex.getMessage());
			}
		}
		moduleTaskContainer.setContent(listOfTask.toArray(new ModuleTask[]{}));
		return moduleTaskContainer.getContent().length;
	}
	
	@Override
	public ModuleTaskContainer getTask(ModuleIdentifier moduleIdentifier) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// �������� ���������� ������������� ������ � �������� ���� ������
			int idModule=this.getIdFromIdentifier(connector, moduleIdentifier);
			ModuleTaskContainer returnValue=new ModuleTaskContainer();
			// �������� ������������ ������ ��� ������
			// ������� ��� ������ � ��������� � ������� � �������� ������
			if(this.fillModuleTaskContainer(connector, idModule, returnValue, this.maxContainerTaskConsist)>0){
				// ������ �������� ��� "�����" - ��� ��������� �� ������ ������ ���� ������������ ��� ����������� 
			}
			return returnValue;
		}catch(Exception ex){
			logger.error("taskProcessError Exception: "+ex.getMessage());
			return new ModuleTaskContainer();
		}finally{
			try{
				connector.close();
			}catch(Exception ex){};
		}
	}

	/** �������� ���������� id ������ �� �������������� 
	 * @param connector -  ���������� � ����� ������ 
	 * @param moduleIdentifier - ���������� ������������� ������ 
	 * @return 
	 * <li> <b> value&lt0 </b>  �� ������� ���������������� ������ </li>
	 * <li> <b>value&gt0</b> ���� ������ ������� ��������������� </li>
	 */
	private int getIdFromIdentifier(ConnectWrap connector, ModuleIdentifier moduleIdentifier){
		int returnValue=(-1);
		try{
			Session session=connector.getSession();
			Module module=(Module)session.createCriteria(Module.class).add(Restrictions.eq("idModule", moduleIdentifier.getId())).uniqueResult();
			if(module!=null){
				returnValue=module.getId();
			}
		}catch(Exception ex){
			if(moduleIdentifier!=null){
				logger.error("getIdFromIdentifier: Exception: "+ex.getMessage()+" ModuleIdentifier is not recognized: "+moduleIdentifier.getId());
			}else{
				logger.error("getIdFromIdentifier ModuleIdentifier is null ");
			}
			
		}
		return returnValue; 
	}
	
	/** �������� �� ����������� �������������� � ���������� � ����� ������, ������� �������� ����������� ������ */
	private ModuleTaskWrap getTaskById(ConnectWrap connector, Integer taskId){
		return (ModuleTaskWrap)connector.getSession().get(ModuleTaskWrap.class, taskId);
	}
	
	
	@Override
	public void taskProcessError(ModuleIdentifier moduleIdentifier,
								 Integer taskId) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// �������� ���������� ������������� ������ � �������� ���� ������
			int idModule=this.getIdFromIdentifier(connector, moduleIdentifier);
			// �������� �� ����������� ���� ������ � ����
			ModuleTaskWrap moduleTask=this.getTaskById(connector, taskId);
			// ��������� ������������ ������ � ����������� ��������������
			if(moduleTask.getIdModule()==idModule){
				// �������� ������ ���������� ��� ������
				connector.getSession().beginTransaction();
				moduleTask.setIdState(2);
				moduleTask.setIdResult(3);
				connector.getSession().update(moduleTask);
				connector.getSession().getTransaction().commit();
				// TODO ������. ��������� �� module_task � ����� ( ��� � ���� id_state=2)
			}else{
				throw new Exception("taskProcessError Module is not Recognized "+moduleIdentifier.getId()+" for Task.id="+taskId);
			}
		}catch(Exception ex){
			logger.error("taskProcessError Exception: "+ex.getMessage());
		}finally{
			try{
				connector.close();
			}catch(Exception ex){};
		}
	}

	@Override
	public void taskProcessNotFound(ModuleIdentifier moduleIdentifier,
									Integer taskId) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// �������� ���������� ������������� ������ � �������� ���� ������
			int idModule=this.getIdFromIdentifier(connector, moduleIdentifier);
			// �������� �� ����������� ���� ������ � ����
			ModuleTaskWrap moduleTask=this.getTaskById(connector, taskId);
			// ��������� ������������ ������ � ����������� ��������������
			if(moduleTask.getIdModule()==idModule){
				// �������� ��� ������ �� ���� ������� 
				connector.getSession().beginTransaction();
				moduleTask.setIdState(2);
				moduleTask.setIdResult(2);
				connector.getSession().update(moduleTask);
				connector.getSession().getTransaction().commit();
				// TODO ������. ��������� �� module_task � ����� ( ��� � ���� id_state=2)
			}else{
				throw new Exception("taskProcessNotFound Module is not Recognized "+moduleIdentifier.getId()+" for Task.id="+taskId);
			}
		}catch(Exception ex){
			logger.error("taskProcessNotFound Exception: "+ex.getMessage());
		}finally{
			try{
				connector.close();
			}catch(Exception ex){};
		}
	}

	@Override
	public void taskProcessOk(ModuleIdentifier moduleIdentifier, 
							  Integer taskId) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// �������� ���������� ������������� ������ � �������� ���� ������
			int idModule=this.getIdFromIdentifier(connector, moduleIdentifier);
			// �������� �� ����������� ���� ������ � ����
			ModuleTaskWrap moduleTask=this.getTaskById(connector, taskId);
			// ��������� ������������ ������ � ����������� ��������������
			if(moduleTask.getIdModule()==idModule){
				// �������� ��� ������ ���� ������� ��������� 
				connector.getSession().beginTransaction();
				moduleTask.setIdState(2);
				moduleTask.setIdResult(1);
				connector.getSession().update(moduleTask);
				connector.getSession().getTransaction().commit();
				// TODO ������.��������� �� module_task � ����� ( ��� � ���� id_state=2)
			}else{
				throw new Exception("taskProcessOk Module is not Recognized "+moduleIdentifier.getId()+" for Task.id="+taskId);
			}
		}catch(Exception ex){
			logger.error("taskProcessOk Exception: "+ex.getMessage());
		}finally{
			try{
				connector.close();
			}catch(Exception ex){};
		}
	}

	@Override
	public void tookTask(ModuleIdentifier moduleIdentifier, Integer taskId) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Session session=connector.getSession();
			ModuleTaskWrap moduleTaskWrap=(ModuleTaskWrap)session.get(ModuleTaskWrap.class, taskId);
			if(moduleTaskWrap!=null){
				logger.debug("���������� ��� ������ ��������� - ������� ������� ��� ���������� Id:"+taskId);
				moduleTaskWrap.setIdState(1);
				session.beginTransaction();
				session.update(moduleTaskWrap);
				session.getTransaction().commit();
			}
		}catch(Exception ex){
			logger.error("tookTask Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}

}
