package fenomen.module.core.sensor;

import org.apache.log4j.Logger;

import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.IUpdateSettings;
import fenomen.module.core.settings.ModuleSettings;

/** ������-�����, ������� "������" ��������� �������� ����� ��������� ��������� �������, � ������ � ���� ������� ���������/�������� ���� �������� � ������� */
public class ThreadSensor extends Thread implements IUpdateSettings{
	private Logger logger=Logger.getLogger(this.getClass());
	/** ����, ������� "�������" � ������������� ���������� ���������� */
	private Boolean needUpdateSettings=false;
	/** ������, �� �������� ����� �������� ������� ��������� ������ */
	private IModuleSettingsAware moduleSettingsAware;
	/** ����� �������� � ��. ����� ��������� ������� ������ �� �������� */
	private long timeWait=250;
	/** ������-���������� ���������� �� �������� ��������� */
	private SensorProcessor sensorProcessor;
	/** ������, ������� �������� ��� ������� � ������� */
	private SensorContainer sensorContainer;

	/** ������-�����, ������� "������" ��������� �������� ����� ��������� ��������� �������, � ������ � ���� ������� ���������/�������� ���� �������� � �������
	 * @param moduleSettingsAware - ������, ������� ������� �������� ����������� ������
	 * @param sensorProcessor - ������, ������� ������������ ��� 
	 * @param sensorContainer - ��� ������� � ������� ( ��� ����������� ������ )
	 */
	public ThreadSensor(IModuleSettingsAware moduleSettingsAware, 
						SensorProcessor sensorProcessor, 
						SensorContainer sensorContainer){
		this.moduleSettingsAware=moduleSettingsAware;
		this.sensorProcessor=sensorProcessor;
		this.sensorContainer=sensorContainer;
	}
	
	@Override
	public void run(){
		this.updateSettings();
		try{
			while(true){
				// �������� �� ������������� ���������� �������� �� ������ 
				if(this.needUpdateSettings==true){
					this.updateSettings();
				}
				// ���������������� ����� ������ �� ��������
				this.sensorProcessor.processSensorValues(sensorContainer);
				// ������� �� ����� ����� ��������� ���������
				try{Thread.sleep(this.timeWait);}catch(Exception ex){};
			}
		}catch(Exception ex){
			logger.error("Thread Sensor EXCEPTION: "+ex.getMessage());
		}
	}

	
	/** ������������� � ���������� �������� ������  */
	private void updateSettings(){
		synchronized(this.needUpdateSettings){
			this.needUpdateSettings=false;
		}
		// ���������� ���������� ������ 
		ModuleSettings moduleSettings=this.moduleSettingsAware.getModuleSettings();
		// ������ �������� �� ModuleSettings � ������� ������ ThreadSensor
		this.timeWait=moduleSettings.getParameterAsLong(ModuleSettings.sensorTimeWait);
	}
	
	@Override
	public void notifyUpdateSettings() {
		synchronized(needUpdateSettings){
			this.needUpdateSettings=true;
		}
	}
}
