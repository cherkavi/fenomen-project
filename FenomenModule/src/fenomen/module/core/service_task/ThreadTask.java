package fenomen.module.core.service_task;

import org.apache.log4j.Logger;

import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.ITaskAware;
import fenomen.module.core.ITaskExistsNotifier;
import fenomen.module.core.IUpdateSettings;
import fenomen.module.core.settings.ModuleSettings;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleTaskContainer;
import fenomen.module.web_service.service.ITask;


/** �����, ������� �������� ������� �� �������, ��������� ��� ������� �� ������, � ���������� ��������� �� ������ */
public class ThreadTask extends Thread implements ITaskExistsNotifier, IUpdateSettings{
	private Logger logger=Logger.getLogger(this.getClass());
	/** ����, ������� ������� � ������������� ��������� � ������� ���������� ������� */
	private Integer taskExists=0;
	/** ����, ������� ������������� � ������������� UPDATE */
	private Boolean needUpdateSettings=false;
	/** ������, ������� �������� ����� �������� ��� ������ ������� ��� ������� */
	private Object controlObject=new Object();
	/** ������, ������������ �������-������� �� ����� � �������� */
	private ITaskAware taskAware;
	/** ������, ������� ������������� ������� �� ��������� TaskModule �� ������� */
	private ITask serviceTask;
	/** ������, ������������ �������-��������� ������ */
	private IModuleSettingsAware moduleSettingsAware;
	/** ���������� ������������� ������ */
	private ModuleIdentifier moduleIdentifier;
	/** ����� �������� ����� ��������� �������� �� ������ �� Task  */
	private long timeWait=0;
	/** ����� �������� ����� ��������� �������� ������� �� ������  */
	private long timeError=0;
	/** ���������� ������� �� ������� */
	private TaskProcessor taskProcessor;
	
	/** �����, ������� �������� ������� �� �������, ��������� ��� ������� �� ������, � ���������� ��������� �� ������
	 * @param taskAware ������, ������� ����� ������������ ������ �� ��������� Task � ������� 
	 * @param moduleSettingsAware ������, ������� �������� ������� ��������� ������  
	 * @param moduleIdentifier ���������� ������������� ������ 
	 * @param taskProcessor ������-���������� ��� �������� �������
	 */
	public ThreadTask(ITaskAware taskAware, 
					  IModuleSettingsAware moduleSettingsAware, 
					  ModuleIdentifier moduleIdentifier,
					  TaskProcessor taskProcessor){
		this.taskAware=taskAware;
		this.moduleSettingsAware=moduleSettingsAware;
		this.moduleIdentifier=moduleIdentifier;
		this.taskProcessor=taskProcessor;
	}
	
	@Override 
	public void run(){
		this.updateSettings();
		while(true){
			if((taskExists.intValue()>0)||(needUpdateSettings.booleanValue()==true)){
				if(needUpdateSettings.booleanValue()==true){
					this.updateSettings();
				}
				// ���������� �� �������, ������� ����� ���������� ?
				if(taskExists.intValue()>0){
					synchronized(taskExists){
						this.taskExists=0;
					}
					// Task ���������� - ������� � ����������
					ModuleTaskContainer currentTask=this.getTaskFromServer();
					if(isTaskNeedProcessing(currentTask)){
						logger.debug("process Task, send confirm");
						this.taskProcessor.processTaskAndSendConfirm(currentTask,this.taskAware, this.serviceTask);
						// currentTask
						try{Thread.sleep(this.timeWait);}catch(Exception ex){};
						synchronized(taskExists){
							this.taskExists=1;
						}
						continue;
					}else{
						// �������� ������ �������, ��������� �� ��������� ��������� �����
						synchronized(this.taskExists){
							if(this.taskExists.intValue()>0){
								continue;
							}else{
								try{
									this.controlObject.wait();
								}catch(Exception ex){};
							}
						}
					}
				}
			}else{
				synchronized(this.controlObject){
					if((taskExists.intValue()>0)||(needUpdateSettings.booleanValue()==true)){
						continue;
					}else{
						try{
							this.controlObject.wait();
						}catch(Exception ex){}
					}
				}
			}
		}
	}
	
	/** �������� ��������� ��������� ������, ������� �������� ������ ��������� ���������� */
	private void updateSettings(){
		synchronized(this.needUpdateSettings){
			this.needUpdateSettings=false;
		}
		ModuleSettings moduleSettings=this.moduleSettingsAware.getModuleSettings();
		this.timeWait=moduleSettings.getParameterAsLong(ModuleSettings.taskTimeWait);
		this.timeError=moduleSettings.getParameterAsLong(ModuleSettings.taskTimeError);
	}
	
	
	/** ���������, ����� �� ������ Task ������������ ��� �� �� �� ����� � ���� ������� */
	private boolean isTaskNeedProcessing(ModuleTaskContainer moduleTaskContainer){
		// ��������� �� ������� ������� � ������� ModuleTask
		return (moduleTaskContainer.getContent().length>0);
	}
	
	/** �������� ModuleTask �� ������� */
	private ModuleTaskContainer getTaskFromServer(){
		ModuleTaskContainer moduleTask=null;
		while(true){
			try{
				// ������� ��������� ���������� �������
				moduleTask=this.serviceTask.getTask(this.moduleIdentifier);
				if(moduleTask==null){
					logger.warn("getTaskFromServer ������ ����� � �������� ");
					try{
						Thread.sleep(this.timeError);
					}catch(Exception exInner){};
					continue;
				}else{
					logger.debug("getTaskFromServer ModuleTask was get ");
					break;
				}
			}catch(Exception ex){
				logger.warn("getTaskFromServer (maybe serviceTask is null) Exception: "+ex.getMessage());
				if(this.serviceTask==null){
					this.serviceTask=this.taskAware.getTask();
				}
				try{
					Thread.sleep(this.timeError);
				}catch(Exception exInner){};
				continue;
			}
		}
		return moduleTask;
	}
	
	@Override
	public void notifyTaskExists() {
		logger.debug("notifyTaskExists");
		// �� ������� ���� ����� �������, ������� ����� ������� - ���������� ������ � ������������� ��������� �������� ����� 
		synchronized(this.taskExists){
			this.taskExists=this.taskExists.intValue()+1;
		}
		synchronized(this.controlObject){
			this.controlObject.notify();
		}
	}

	@Override
	public void notifyUpdateSettings() {
		logger.debug("notifyUpdateSettings");
		synchronized(this.needUpdateSettings){
			this.needUpdateSettings=true;
		}
		synchronized(this.controlObject){
			this.controlObject.notify();			
		}
	}
	
}
