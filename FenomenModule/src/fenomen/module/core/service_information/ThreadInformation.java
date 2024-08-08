package fenomen.module.core.service_information;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import fenomen.module.core.IInformationAware;
import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.IUpdateSettings;
import fenomen.module.core.settings.ModuleSettings;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ContainerModuleInformation;
import fenomen.module.web_service.service.IInformation;

/** �����, ������� �������� �� ������ �������������� ���������, ���������� �� �������� */
public class ThreadInformation extends Thread implements IUpdateSettings, IModuleInformationListener{
	private Logger logger=Logger.getLogger(this.getClass());
	/** ���������� ������������� ������� ������ */
	private ModuleIdentifier moduleIdentifier;
	/** ������, ������� ���������� ������ �� �������� ���������� �� ������ */
	private IInformationAware informationAware;
	private IInformation serviceInformation;
	/** ������, ������� ���������� ������ �� ��������� ��������������� ������� */
	private IModuleSettingsAware moduleSettingsAware;
	/** ������, ������� �������� ����������� �������������� ������� ��� �������� �� ������ */
	private ArrayList<ContainerModuleInformation> listOfInformation=new ArrayList<ContainerModuleInformation>();
	
	/** ����,������� ������������� � ������������� ���������� �������� ��� ������ */
	private Boolean needUpdateSettings=false;
	/** ������, ������� ����� ��������������� � ��������� ������� ��� ��������� */
	private Object signal=new Object();
	
	/** ����� �������� ����� ��������� ��������*/
	private long timeWait;
	/** ����� �������� ����� ��������� �������� �������� */
	private long timeError;
	/** ������������ ���-�� �������������� ��������� � �������, ��� ��� ������ - ������������ ������ */
	private int maxInformationCount=0;

	/** �����, ������� �������� �� ������ �������������� ���������, ���������� �� �������� 
	 * @param informationAware ������, ������� ���������� ������� �� ����� � ��������
	 * @param moduleSettingsAware ������, ������� "������" � ���������� ������ 
	 * @param moduleIdentifier ������-������������� ��� ������
	 * IMPORTANT: ��������� ����� ������ start()   
	 */
	public ThreadInformation(IInformationAware informationAware,
							 IModuleSettingsAware moduleSettingsAware,
							 ModuleIdentifier moduleIdentifier){
		this.informationAware=informationAware;
		this.informationAware.getInformation();
		this.moduleSettingsAware=moduleSettingsAware;
		this.moduleIdentifier=moduleIdentifier;
	}
	
	@Override
	public void run(){
		this.updateSettings();
		ContainerModuleInformation currentForSend=null;
		while(true){
			if((this.needUpdateSettings.booleanValue()==true)||(this.listOfInformation.size()>0)){
				// �������� �� ��������� � ���������� ������
				if(this.needUpdateSettings.booleanValue()==true){
					this.updateSettings();
				}
				// �������� �� ������� ������� ��� �������� 
				if(this.listOfInformation.size()>0){
					// ��������� ���������� �������
					synchronized(this.listOfInformation){
						currentForSend=this.listOfInformation.remove(0);
					}
					logger.debug("run: sendInformation");
					this.sendInformation(currentForSend);
					// wait for next send
					try{Thread.sleep(this.timeWait);}catch(Exception ex){};
				}
			}else{
				synchronized(this.signal){
					if((this.needUpdateSettings.booleanValue()==true)||(this.listOfInformation.size()>0)){
						logger.debug("for work");
						continue;
					}else{
						try{
							logger.debug("no information data for send - wait for work");
							this.signal.wait();
						}catch(InterruptedException ex){};
					}
				}
			}
		}
	}

	/** �������� ��������� ��������� ������, ������� �������� ������ ��������� ���������� */
	private void updateSettings(){
		synchronized(this.needUpdateSettings){
			ModuleSettings moduleSettings=this.moduleSettingsAware.getModuleSettings();
			this.timeWait=moduleSettings.getParameterAsLong(ModuleSettings.informationTimeWait, 5*1000);
			this.timeError=moduleSettings.getParameterAsLong(ModuleSettings.informationTimeError, 60*1000);
			this.maxInformationCount=(int)moduleSettings.getParameterAsLong(ModuleSettings.informationMaxInformationCount,100);
			if(this.maxInformationCount<0){this.maxInformationCount=0;};
			this.needUpdateSettings=false;
		}
	}
	
	/** ��������� �������������� ��������� �� ������
	 * @param moduleInformation - ������, ������� ������ ���� ��������� �� ������ 
	 */
	private void sendInformation(ContainerModuleInformation moduleInformation){
		while(true){
			try{
				// �������� �� ����������/������� ���������� ������� 
				if(this.serviceInformation==null){
					this.serviceInformation=this.informationAware.getInformation();
				}
				// �������� ������ �� ������
				String returnValue=this.serviceInformation.sendInformation(this.moduleIdentifier, moduleInformation);
				if((returnValue==null)||(returnValue.equals(IInformation.returnError))){
					logger.debug("sendInformation: Data sended with error - repeat, after wait");
					try{Thread.sleep(this.timeError);}catch(Exception ex){};
					continue;
				}else if(returnValue.equals(IInformation.returnOk)){
					logger.debug("sendInformation: Data was sended ");
					break;
				}else {
					logger.error("sendInformation: Unknown server response:"+returnValue);
					break;
				}
			}catch(Exception ex){
				logger.warn("sendInformation Exception:"+ex.getMessage());
			}
		}
	}
	
	@Override
	public void notifyUpdateSettings() {
		synchronized(needUpdateSettings){
			this.needUpdateSettings=true;
		}
		// ���������� ������ � ������������� ������������� �������� ����� 
		synchronized(this.signal){
			this.signal.notify();
		}
	}

	@Override
	public void notifyInformation(ContainerModuleInformation moduleInformation) {
		synchronized(this.listOfInformation){
			this.listOfInformation.add(moduleInformation);
			while(this.maxInformationCount<this.listOfInformation.size()){
				this.listOfInformation.remove(0);
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
	public void processInformation(IProcessInformation objectForProcess){
		synchronized(this.listOfInformation){
			objectForProcess.processInformation(this.listOfInformation);
		}
		// ���������� ������ � ������������� ������������� �������� ����� 
		synchronized(this.signal){
			this.signal.notify();
		}
	}
}
