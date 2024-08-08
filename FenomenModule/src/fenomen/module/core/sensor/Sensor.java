package fenomen.module.core.sensor;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.cherkashin.vitaliy.modbus.core.schedule_read.Device;
import com.cherkashin.vitaliy.modbus.core.schedule_read.DeviceRegisterBlock;

import fenomen.module.web_service.common.ModuleAlarm;
import fenomen.module.web_service.common.ModuleInformation;
import fenomen.server.controller.server.generator_alarm_checker.calc.Checker;
import fenomen.server.controller.server.generator_alarm_checker.message.AlarmMessage;
import fenomen.server.controller.server.generator_alarm_checker.message.InformationMessage;

/** ������, ������� ��������� � ���� Modbus */
public class Sensor {
	private Logger logger=Logger.getLogger(this.getClass());
	private ArrayList<ModuleAlarm> tempListOfModuleAlarm=new ArrayList<ModuleAlarm>();
	private ArrayList<ModuleInformation> tempListOfModuleInformation=new ArrayList<ModuleInformation>();
	
	/** ��������� ��� ��������-������������ �������������� ���������  */
	private ICheckerStorage<Checker<InformationMessage>> checkerInformationList;
	/** ��������� ��� ��������-������������ ��������� ���������  */
	private ICheckerStorage<Checker<AlarmMessage>> checkerAlarmList;
	
	/** ����� ������� � ������� MODBUS*/
	private int number;
	/** ��� ������� */
	private String type;
	/** ����, ������� ������� � ������������� ����������� ������� ������ */
	private volatile boolean enabled=true;
	/** ���������� � ���� ModBus */
	private Device device;
	
	/** ������/������, ������� ��������� � ������� 
	 * @param number - ����� ������ � ������� ModBus
	 * @param type - ��� ������
	 * @param fileSystemCheckerStorage - ��������� ��� CheckerInformation ( ������ ������ ���� ��������� )
	 * @param storageCheckerAlarm - ��������� ��� CheckerAlarm ( ������ ������ ���� ��������� )
	 * @param ����� ������� ��� ������, ������� ����� ������������� ����������� �� ���� ModBus 
	 */
	public Sensor(int number, 
				  String type, 
				  ICheckerStorage<Checker<InformationMessage>> fileSystemCheckerStorage, 
				  ICheckerStorage<Checker<AlarmMessage>> storageCheckerAlarm,
				  DeviceRegisterBlock blocks[]){
		this.type=type;
		this.number=number;
		this.checkerInformationList=fileSystemCheckerStorage;
		this.checkerAlarmList=storageCheckerAlarm;
		this.device=new Device(number);
		if(blocks!=null){
			for(int counter=0;counter<blocks.length;counter++){
				this.device.addReadBlock(blocks[counter]);
			}
		}
	}

	/** �������� ���������� ModBus */
	public Device getDevice(){
		return this.device;
	}
	
	/** ������ �� �������� ��������� �������� � ���������, ��������� ��� Checker-� �� ������� ��������� � ��� ������� �������� */
	public ModuleAlarm[] getModuleAlarm(int sensorIndex) {
		tempListOfModuleAlarm.clear();
		// INFO ������.��������� AlarmMessage �� ��������� ������� �������� ��������� ������
		// �������� �� ���� Checker-��
		for(int alarmCounter=0;alarmCounter<this.checkerAlarmList.getStorageSize();alarmCounter++){
			Checker<AlarmMessage> currentChecker=this.checkerAlarmList.readFromStorage(alarmCounter);
			// �������� �� ���� ������ 
			for(int blockIndex=0;blockIndex<this.getDevice().getBlockCount();blockIndex++){
				DeviceRegisterBlock currentBlock=this.getDevice().getBlock(blockIndex);
				// �������� �� ���� ��������� � ����� 
				for(int registerCounter=0;registerCounter<currentBlock.getRegisterCount();registerCounter++){
					int absoluteNumber=registerCounter+currentBlock.getAddressBegin();
					int value=currentBlock.getRegister(registerCounter);
					Date date=currentBlock.getTimeOfLastOperation();
					AlarmMessage currentEvent=currentChecker.checkForMessage(absoluteNumber, value, date);
					if(currentEvent!=null){
						logger.debug("finded Alarm Message: number="+absoluteNumber+"   value="+value);
						ModuleAlarm alarm=new ModuleAlarm();
						alarm.setEventDate(currentEvent.getEventDate());
						alarm.setContent(currentEvent.getDescription());
						alarm.setValue(currentEvent.getValue());
						alarm.setIdSensor(sensorIndex);
						alarm.setRegisterAddress(currentEvent.getRegisterAddress());
						tempListOfModuleAlarm.add(alarm);
					}
				}
			}
		}
		return tempListOfModuleAlarm.toArray(new ModuleAlarm[]{});
	}

	public ModuleInformation[] getModuleInformation(int sensorIndex) {
		tempListOfModuleInformation.clear();
		// INFO ������.��������� InformationMessage �� ��������� ������� �������� ������
		for(int counter=0;counter<this.checkerInformationList.getStorageSize();counter++){
			Checker<InformationMessage> currentChecker=this.checkerInformationList.readFromStorage(counter);
			// �������� �� ���� ������ 
			for(int blockIndex=0;blockIndex<this.getDevice().getBlockCount();blockIndex++){
				DeviceRegisterBlock currentBlock=this.getDevice().getBlock(blockIndex);
				// �������� �� ���� ��������� � ����� 
				for(int registerCounter=0;registerCounter<currentBlock.getRegisterCount();registerCounter++){
					int absoluteNumber=registerCounter+currentBlock.getAddressBegin();
					int value=currentBlock.getRegister(registerCounter);
					Date date=currentBlock.getTimeOfLastOperation();
					InformationMessage currentEvent=currentChecker.checkForMessage(absoluteNumber, value,date);
					if(currentEvent!=null){
						logger.debug("finded Information Message: number="+absoluteNumber+"   value="+value);
						ModuleInformation information=new ModuleInformation();
						information.setEventDate(currentEvent.getEventDate());
						information.setContent(currentEvent.getDescription());
						information.setValue(currentEvent.getValue());
						information.setIdSensor(sensorIndex);
						information.setRegisterAddress(currentEvent.getRegisterAddress());
						tempListOfModuleInformation.add(information);
					}
				}
			}
			 
		}
		return tempListOfModuleInformation.toArray(new ModuleInformation[]{});
	}
	
	/* �������� InformationChecker �� ���������� ������� 
	 * @param index - ������ �������� ��� ������ 
	 * @param InformationChecker - �������, ������� ����� �������� 
	 * @return 
	 * <li><b>true</b> ������ ����������� </li>
	 * <li><b>false</b> ������ ������ ��������</li>
	public boolean replaceInformationChecker(int index, InformationChecker InformationChecker){
		try{
			this.checkerInformationList.set(index, InformationChecker);
			return true;
		}catch(IndexOutOfBoundsException ex){
			return false;
		}
	}
	 */

	/*
	private SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:sss");
	
	private class TestValue extends Thread{
		private TestValue(){
			this.start();
		}
		public void run(){
			while(true){
				changeValue();
				try{
					TimeUnit.SECONDS.sleep(30);
				}catch(Exception ex){};
			}
		}
		private void changeValue(){
			if(value.equals("0")){
				this.value="1";
			}else{
				this.value="0";
			}
		}
		private String value="1"; 
		public String getValue(){
			return this.value;
		}
	}
	private TestValue testValue=new TestValue();
	
	// �������� �������� �� ���� ModBus
	public void readDataFromModBus() {
		if(this.enabled==true){
			try{
				this.timeWrite=new Date();
				this.value=testValue.getValue();
				this.old=false;
				logger.debug("read data from Sensor("+this+") #"+this.number+"   Value:"+this.value+"    TimeRead:"+sdf.format(this.timeWrite));
			}catch(Exception ex){
				// ������ ������ ������ �� �������
				logger.error("readDataFromModBus "+sdf.format(new Date())+" Exception:"+ex.getMessage());
				this.old=true;
			}
		}else{
			// sensor is disabled
		}
	}*/

	/** �������� ������������ �� ������� Information */
	public void addInformationChecker(Checker<InformationMessage> InformationChecker){
		this.checkerInformationList.saveToStorage(InformationChecker);
	}

	/** �������� ���-�� InformationChecker-�� */
	public int getInformationCheckerSize(){
		return this.checkerInformationList.getStorageSize();
	}
	
	/** �������� InformationChecker �� ������� */
	public Checker<InformationMessage> getInformationChecker(int index){
		try{
			return this.checkerInformationList.readFromStorage(index);
		}catch(IndexOutOfBoundsException ex){
			return null;
		}
	}
	
	/** ������� InformationChecker �� ���������� ������� */
	public void removeInformationChecker(int index){
		this.checkerInformationList.removeFromStorage(index);
	}
	/** �������� ������������ �� ������� Alarm */
	public String addAlarmChecker(Checker<AlarmMessage> alarmChecker){
		return this.checkerAlarmList.saveToStorage(alarmChecker);
	}

	/** �������� ���-�� AlarmChecker-�� */
	public int getAlarmCheckerSize(){
		return this.checkerAlarmList.getStorageSize();
	}
	
	/** �������� AlarmChecker �� ������� */
	public Checker<AlarmMessage> getAlarmChecker(int index){
		try{
			return this.checkerAlarmList.readFromStorage(index);
		}catch(IndexOutOfBoundsException ex){
			return null;
		}
	}
	
	/** ������� AlarmChecker �� ���������� ������� */
	public void removeAlarmChecker(int index){
		this.checkerAlarmList.removeFromStorage(index);
	}
	/** �������� ����� ������� � ������� ModBus */
	public int getNumber() {
		return number;
	}

	/** �������� ��� ������� � ��������� ����, ������� �������� ����� �� ������� � ������� sensor_type.name*/
	public String getType() {
		return type;
	}

	/** ����, ������� ������� � ������������� ����������� ������� ������� */
	public boolean isEnabled() {
		return enabled;
	}

	/** ����, ������� ������� � ������������� ����������� ������� ������� 
	 * @param enabled
	 * <li><b>true</b> - ���������� </li>
	 * <li><b>false</b> - �� ���������� </li>
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
