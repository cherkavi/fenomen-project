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

/** модуль, который находится в сети Modbus */
public class Sensor {
	private Logger logger=Logger.getLogger(this.getClass());
	private ArrayList<ModuleAlarm> tempListOfModuleAlarm=new ArrayList<ModuleAlarm>();
	private ArrayList<ModuleInformation> tempListOfModuleInformation=new ArrayList<ModuleInformation>();
	
	/** хранилище для объектов-анализаторов Информационных сообщений  */
	private ICheckerStorage<Checker<InformationMessage>> checkerInformationList;
	/** хранилище для объектов-анализаторов Тревожных сообщений  */
	private ICheckerStorage<Checker<AlarmMessage>> checkerAlarmList;
	
	/** номер датчика в системе MODBUS*/
	private int number;
	/** тип датчика */
	private String type;
	/** флаг, который говорит о необходимости опрашивания данного модуля */
	private volatile boolean enabled=true;
	/** устройство в сети ModBus */
	private Device device;
	
	/** сенсор/датчик, который подключен к системе 
	 * @param number - номер модуля в системе ModBus
	 * @param type - тип модуля
	 * @param fileSystemCheckerStorage - хранилище для CheckerInformation ( данные должны быть загружены )
	 * @param storageCheckerAlarm - хранилище для CheckerAlarm ( данные должны быть загружены )
	 * @param блоки адресов для чтения, которые будут автоматически считываться из сети ModBus 
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

	/** получить устройство ModBus */
	public Device getDevice(){
		return this.device;
	}
	
	/** исходя из текущего состояния значений в регистрах, перебрать все Checker-ы на предмет выявления в них событий датчиков */
	public ModuleAlarm[] getModuleAlarm(int sensorIndex) {
		tempListOfModuleAlarm.clear();
		// INFO модуль.получение AlarmMessage на основании текущих значений регистров модуля
		// пробежка по всем Checker-ам
		for(int alarmCounter=0;alarmCounter<this.checkerAlarmList.getStorageSize();alarmCounter++){
			Checker<AlarmMessage> currentChecker=this.checkerAlarmList.readFromStorage(alarmCounter);
			// пробежка по всем блокам 
			for(int blockIndex=0;blockIndex<this.getDevice().getBlockCount();blockIndex++){
				DeviceRegisterBlock currentBlock=this.getDevice().getBlock(blockIndex);
				// пробежка по всем регистрам в блоке 
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
		// INFO модуль.получение InformationMessage на основании текущих значений модуля
		for(int counter=0;counter<this.checkerInformationList.getStorageSize();counter++){
			Checker<InformationMessage> currentChecker=this.checkerInformationList.readFromStorage(counter);
			// пробежка по всем блокам 
			for(int blockIndex=0;blockIndex<this.getDevice().getBlockCount();blockIndex++){
				DeviceRegisterBlock currentBlock=this.getDevice().getBlock(blockIndex);
				// пробежка по всем регистрам в блоке 
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
	
	/* заменить InformationChecker по указанному индексу 
	 * @param index - индекс элемента для замены 
	 * @param InformationChecker - элемент, который нужно вставить 
	 * @return 
	 * <li><b>true</b> замена произведена </li>
	 * <li><b>false</b> ошибка замены элемента</li>
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
	
	// прочесть значение из сети ModBus
	public void readDataFromModBus() {
		if(this.enabled==true){
			try{
				this.timeWrite=new Date();
				this.value=testValue.getValue();
				this.old=false;
				logger.debug("read data from Sensor("+this+") #"+this.number+"   Value:"+this.value+"    TimeRead:"+sdf.format(this.timeWrite));
			}catch(Exception ex){
				// ошибка чтения данных из датчика
				logger.error("readDataFromModBus "+sdf.format(new Date())+" Exception:"+ex.getMessage());
				this.old=true;
			}
		}else{
			// sensor is disabled
		}
	}*/

	/** добавить проверяющего на событие Information */
	public void addInformationChecker(Checker<InformationMessage> InformationChecker){
		this.checkerInformationList.saveToStorage(InformationChecker);
	}

	/** получить кол-во InformationChecker-ов */
	public int getInformationCheckerSize(){
		return this.checkerInformationList.getStorageSize();
	}
	
	/** получить InformationChecker по индексу */
	public Checker<InformationMessage> getInformationChecker(int index){
		try{
			return this.checkerInformationList.readFromStorage(index);
		}catch(IndexOutOfBoundsException ex){
			return null;
		}
	}
	
	/** удалить InformationChecker по указанному индексу */
	public void removeInformationChecker(int index){
		this.checkerInformationList.removeFromStorage(index);
	}
	/** добавить проверяющего на событие Alarm */
	public String addAlarmChecker(Checker<AlarmMessage> alarmChecker){
		return this.checkerAlarmList.saveToStorage(alarmChecker);
	}

	/** получить кол-во AlarmChecker-ов */
	public int getAlarmCheckerSize(){
		return this.checkerAlarmList.getStorageSize();
	}
	
	/** получить AlarmChecker по индексу */
	public Checker<AlarmMessage> getAlarmChecker(int index){
		try{
			return this.checkerAlarmList.readFromStorage(index);
		}catch(IndexOutOfBoundsException ex){
			return null;
		}
	}
	
	/** удалить AlarmChecker по указанному индексу */
	public void removeAlarmChecker(int index){
		this.checkerAlarmList.removeFromStorage(index);
	}
	/** получить номер датчика в системе ModBus */
	public int getNumber() {
		return number;
	}

	/** получить тип датчика в текстовом виде, который является одной из записей в таблице sensor_type.name*/
	public String getType() {
		return type;
	}

	/** флаг, который говорит о необходимости опрашивания данного датчика */
	public boolean isEnabled() {
		return enabled;
	}

	/** флаг, который говорит о необходимости опрашивания данного датчика 
	 * @param enabled
	 * <li><b>true</b> - опрашивать </li>
	 * <li><b>false</b> - не опрашивать </li>
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
