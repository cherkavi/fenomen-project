package fenomen.module.core.service_alarm;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import fenomen.module.core.IAlarmAware;
import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.IUpdateSettings;
import fenomen.module.core.settings.ModuleSettings;
import fenomen.module.web_service.common.ContainerModuleAlarm;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.service.IAlarm;
import fenomen.module.web_service.service.ITask;

/** �����, ������� �������� �� ������ ��������� ���������, ���������� �� �������� */
public class ThreadAlarm extends Thread implements IUpdateSettings, IModuleAlarmListener{
	private Logger logger=Logger.getLogger(this.getClass());
	/** ���������� ������������� ������� ������ */
	private ModuleIdentifier moduleIdentifier;
	/** ������, ������� ���������� ������ �� �������� ���������� �� ������ */
	private IAlarmAware alarmAware;
	private IAlarm serviceAlarm;
	/** ������, ������� ���������� ������ �� ��������� ��������������� ������� */
	private IModuleSettingsAware moduleSettingsAware;
	/** ������, ������� �������� ����������� �������������� ������� ��� �������� �� ������ */
	private ArrayList<ContainerModuleAlarm> listOfAlarm=new ArrayList<ContainerModuleAlarm>();
	
	/** ����,������� ������������� � ������������� ���������� �������� ��� ������ */
	private Boolean needUpdateSettings=false;
	/** ������, ������� ����� ��������������� � ��������� ������� ��� ��������� */
	private Object signal=new Object();
	
	/** ����� �������� ����� ��������� ��������*/
	private long timeWait;
	/** ����� �������� ����� ��������� �������� �������� */
	private long timeError;
	/** ������������ ���-�� ��������� Alarm � ������� �� �������� */
	private int maxAlarmCount;

	/** �����, ������� �������� �� ������ ��������� ���������, ���������� �� �������� 
	 * @param alarmAware ������, ������� ���������� ������� �� ����� � ��������
	 * @param moduleSettingsAware ������, ������� "������" � ���������� ������ 
	 * @param moduleIdentifier ������-������������� ��� ������
	 * IMPORTANT: ��������� ����� ������ start()   
	 */
	public ThreadAlarm(IAlarmAware alarmAware,
					   IModuleSettingsAware moduleSettingsAware,
					   ModuleIdentifier moduleIdentifier){
		this.alarmAware=alarmAware;
		this.alarmAware.getAlarm();
		this.moduleSettingsAware=moduleSettingsAware;
		this.moduleIdentifier=moduleIdentifier;
		
	}

	/**
	 * ������� �� ������ ����������� � ������/�������� ������� ������   
	 * @param taskAware - ������, ������� ���������� {@link ITask}
	 * @param taskService - ������ ������������� 
	 * @param id - ���������� ������������� ������ 
	 */
	private void moduleWasRestarted(IAlarmAware alarmAware, IAlarm alarmService, ModuleIdentifier moduleIdentifier){
		while(true){
			try{
				alarmService.moduleWasRestarted(moduleIdentifier);
				break;
			}catch(Exception ex){
				if(alarmService==null){
					alarmService=alarmAware.getAlarm();
				}
				try{
					Thread.sleep(5000);
				}catch(Exception exInner){};
			}
		}
	}
	
	
	@Override
	public void run(){
		// ���������� ������ � Restart ������ 
		this.moduleWasRestarted(this.alarmAware, this.serviceAlarm, moduleIdentifier);
		
		this.updateSettings();
		ContainerModuleAlarm currentForSend=null;
		while(true){
			// �������� �� ��������� � ���������� ������
			if((this.needUpdateSettings.booleanValue()==true)||(this.listOfAlarm.size()>0)){
				if(this.needUpdateSettings.booleanValue()==true){
					this.updateSettings();
				}
				// �������� ������� 
				if(this.listOfAlarm.size()>0){
					// ��������� ���������� �������
					synchronized(this.listOfAlarm){
						currentForSend=this.listOfAlarm.remove(0);
					}
					logger.debug("run: sendAlarm");
					this.sendAlarm(currentForSend);
					// wait for next send
					try{Thread.sleep(this.timeWait);}catch(Exception ex){};
				}
			}else{
				logger.debug("no alarm data for send - wait for work");
				synchronized(this.signal){
					if((this.needUpdateSettings.booleanValue()==true)||(this.listOfAlarm.size()>0)){
						logger.debug("for work");
						continue;
					}else{
						try{
							this.signal.wait();
						}catch(InterruptedException ex){};
					}
				}
			}
		}
	}

	@Override
	public void notifyUpdateSettings() {
		// ���������� ������ � ������������� ������������� �������� �����
		synchronized(this.needUpdateSettings){
			this.needUpdateSettings=true;
		}
		synchronized(this.signal){
			this.signal.notify();
		}
	}
	
	
	/** �������� ��������� ��������� ������, ������� �������� ������ ��������� ���������� */
	private void updateSettings(){
		synchronized(this.needUpdateSettings){
			this.needUpdateSettings=false;
		}
		ModuleSettings moduleSettings=this.moduleSettingsAware.getModuleSettings();
		this.timeWait=moduleSettings.getParameterAsLong(ModuleSettings.alarmTimeWait, 5*1000);
		this.timeError=moduleSettings.getParameterAsLong(ModuleSettings.alarmTimeError, 60*1000);
		this.maxAlarmCount=(int)moduleSettings.getParameterAsLong(ModuleSettings.alarmMaxInformationCount,100);
		if(this.maxAlarmCount<0){this.maxAlarmCount=0;};
	}
	
	/** ��������� �������������� ��������� �� ������
	 * @param moduleInformation - ������, ������� ������ ���� ��������� �� ������ 
	 */
	private void sendAlarm(ContainerModuleAlarm moduleAlarm){
		while(true){
			try{
				// �������� �� ����������/������� ���������� ������� 
				if(this.serviceAlarm==null){
					this.serviceAlarm=this.alarmAware.getAlarm();
				}
				// �������� ������ �� ������
				String returnValue=this.serviceAlarm.sendAlarm(this.moduleIdentifier, moduleAlarm);
				if((returnValue==null)||(returnValue.equals(IAlarm.returnError))){
					try{Thread.sleep(this.timeError);}catch(Exception ex){};
					logger.debug("sendAlarm Error - repeat after Send ");
					continue;
				}else if(returnValue.equals(IAlarm.returnOk)){
					logger.debug("sendAlarm: Data was sended ");
					break;
				}else {
					logger.error("sendAlarm: Unknown server response:"+returnValue);
					break;
				}
			}catch(Exception ex){
				logger.warn("sendInformation Exception:"+ex.getMessage());
			}
		}
	}
	

	@Override
	public void notifyAlarm(ContainerModuleAlarm moduleAlarm) {
		synchronized(this.listOfAlarm){
			this.listOfAlarm.add(moduleAlarm);
			while(this.maxAlarmCount<this.listOfAlarm.size()){
				this.listOfAlarm.remove(0);
			}
		}
		// ���������� ������ � ������������� ������������� �������� ����� 
		synchronized(this.signal){
			this.signal.notify();
		}
	}
	
	/** �������� �� ��������� ������������ ������� �� �������������� ��������� 
	 * @param objectForProcess - ������, ������� ����� ������������ ������� ������� (� ������������������ ������ )
	 */
	public void processAlarm(IProcessAlarm objectForProcess){
		synchronized(this.listOfAlarm){
			objectForProcess.processAlarm(this.listOfAlarm);
		}
		// ���������� ������ � ������������� ������������� �������� ����� 
		synchronized(this.signal){
			this.signal.notify();
		}
	}
	
}
