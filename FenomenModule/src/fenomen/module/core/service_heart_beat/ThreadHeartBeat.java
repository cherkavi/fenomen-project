package fenomen.module.core.service_heart_beat;

import org.apache.log4j.Logger;

import fenomen.module.core.IHeartBeatAware;
import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.ITaskExistsNotifier;
import fenomen.module.core.IUpdateSettings;
import fenomen.module.core.settings.ModuleSettings;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.service.IHeartBeat;

/** �����, ������� "��������" � �������� � �������� ��� ���������� � ���������� ������ �� ����� */
public class ThreadHeartBeat extends Thread implements IUpdateSettings{
	private Logger logger=Logger.getLogger(this.getClass());
	/** ������, ������� ������� ����������� � ��������� �������-�������, ������� ����� ����������� � �������� */
	private IHeartBeatAware heartBeatAware;
	private IHeartBeat heartBeatService;
	private IModuleSettingsAware moduleSettingsAware;
	private ModuleIdentifier moduleIdentifier;
	private ITaskExistsNotifier taskExists;
	
	/** �����, ������� "��������" � �������� � �������� ��� ���������� � ���������� ������ �� ����� (<b>������������</b>)
	 * <br>
	 * <i>IMPORTANT:</i> ������ ��������� � ������� ( <b>.start()</b> ) 
	 * @param heartBeatAware - ������, ������� ����� ������������ ������-������ �� ����� � ��������
	 * @param moduleSettingsAware - ������, ������� ����� ������������ ������-�������� ��� ������ 
	 * @param moduleIdentifier - ������-������������� ��� ������
	 * @param taskExists - ������, ������� �������� ��������� �� ���������� � ������� �� ������� ���������� �������, ������� ����� ����������    
	 */
	public ThreadHeartBeat(IHeartBeatAware heartBeatAware, 
						   IModuleSettingsAware moduleSettingsAware, 
						   ModuleIdentifier moduleIdentifier,
						   ITaskExistsNotifier taskExists){
		this.heartBeatAware=heartBeatAware;
		this.moduleSettingsAware=moduleSettingsAware;
		this.moduleIdentifier=moduleIdentifier;
		this.taskExists=taskExists;
		heartBeatService=this.heartBeatAware.getHeartBeat();
	}
	
	@Override
	public void notifyUpdateSettings(){
		synchronized (flagSettingsChange) {
			this.flagSettingsChange=true;
		}
	}

	/** �������� ��������� ��������� ������, ������� �������� ������ ��������� ���������� */
	private void updateSettings(){
		synchronized(this.flagSettingsChange){
			this.flagSettingsChange=false;
		}
		ModuleSettings moduleSettings=this.moduleSettingsAware.getModuleSettings();
		this.timeWait=moduleSettings.getParameterAsLong(ModuleSettings.heartBeatTimeWait, 30*1000);
		this.timeError=moduleSettings.getParameterAsLong(ModuleSettings.heartBeatTimeError, 60*1000);
	}
	
	/** ����� �������� ����� ��������� ��������� */
	private long timeWait=0;
	/** ����� �������� ����� ��������� �������� �������� ������ */
	private long timeError=0;
	/** ����, ������� ������� � ������������� ���������� ��������� ������ �� Settings */
	private Boolean flagSettingsChange=false;
	
	private void sleepThread(long time){
		try{
			Thread.sleep(time);
		}catch(Exception ex){};
	}
	
	@Override
	public void run(){
		this.updateSettings();
		while(true){
			// ������� ������� "������������" �� ������ 
			try{
				if(this.flagSettingsChange.booleanValue()==true){
					// �������� ��������� ������ 
					this.updateSettings();
				}
				// �������� ������
				String response=this.heartBeatService.hearBeat(this.moduleIdentifier);
				if(response==null){
					logger.warn("IHeartBeat send error ");
					// ������� ������ � �������
					throw new Exception("ThreadHeartBeat#run server send Error package ");
				}else if(response.equals(IHeartBeat.sendOk)){
					logger.debug("send ok");
				}else if(response.equals(IHeartBeat.sendError)){
					// ������� ������ � �������
					throw new Exception("ThreadHeartBeat#run server send Error package ");
				}else if(response.equals(IHeartBeat.taskExists)){
					logger.debug("notify ITaskExists about task on server for this module ");
					this.taskExists.notifyTaskExists();
				}else{
					// ����������� ����� 
					logger.error("IHeartBeat server answer unknown value:"+response);
				}
				// ������� ��� ��������� ��������
				this.sleepThread(this.timeWait);
			}catch(Exception ex){
				logger.warn("Exception: "+ex.getMessage());
				// ��������� ������ �� ����� ������� �������� HeartBeat - �������� �� ��������� ����� � ��������� �������
				this.sleepThread(this.timeError);
				if(this.heartBeatService==null){
					this.heartBeatService=this.heartBeatAware.getHeartBeat();
				}
			}
		}
	}
}
