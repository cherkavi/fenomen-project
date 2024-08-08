package fenomen.module.core.sensor;

import java.util.ArrayList;

import com.cherkashin.vitaliy.modbus.core.direct.ModBusNet;
import com.cherkashin.vitaliy.modbus.core.schedule_read.Device;
import com.cherkashin.vitaliy.modbus.core.schedule_read.DeviceRegisterBlock;
import com.cherkashin.vitaliy.modbus.core.schedule_read.ScheduleThread;

/** объект, который содержит все датчики в системе */
public class SensorContainer {
	private ArrayList<Sensor> sensors=new ArrayList<Sensor>();
	/** объект, который эмулирует сеть ModBus */
	private ModBusNet modbus=null;

	/** объект, который содержит все датчики в системе 
	 * @param modbus - объект, который эмулирует сеть ModBus
	 * <br>
	 * необходимо вызывать метод {@link #startService() запуск сервиса} - для начала работы  
	 */
	public SensorContainer(ModBusNet modbus){
		this.modbus=modbus;
	}
	
	/** запуск сервиса на выполнение  */
	public void startService(){
		Device[] devices=new Device[this.sensors.size()];
		for(int counter=0;counter<this.sensors.size();counter++){
			devices[counter]=this.sensors.get(counter).getDevice();
		}
		(new ScheduleThread(modbus, 200, devices)).start();
	}
	
	/** возвращает полученные на основании типа датчика значения  
	 * @param type - тип устройства
	 * */
	public static DeviceRegisterBlock[] getRegisterBlock(int[] type){
		// TODO модуль. на основании типа устройства получить блоки для чтения данных
		return new DeviceRegisterBlock[]{new DeviceRegisterBlock(0,20)}; 
	}
	
	
	/** запросить устройство и вернуть его тип, если данное устройство найдено  
	 * @param number - уникальный номер устройства
	 * @return <li>null - устройство не найдено</li> <li> массив типа int[2] 0:(старший байт),1:(младший байт)</li> 
	 * */
	public int[] discoverDeviceByModbusNumber(int number){
		try{
			// device found
			return modbus.readGroupRegister(number, 2, 2);
		}catch(Exception ex){
			// device not found
			return null;
		}
	}
	
	/** добавить датчик в систему */
	public void addSensor(Sensor sensor){
		this.sensors.add(sensor);
	}
	
	/** удалить датчик из системы 
	 * @param sensor - датчик, который нужно удалить 
	 * @return - 
	 * <li> <b>Sensor</b> удаленный из контейнера Датчик/сенсор</li>
	 * <li> <b>null</b> ошибка удаления датчика </li>
	 */
	public Sensor removeSensor(Sensor sensor){
		if(this.sensors.remove(sensor)==true){
			return sensor;
		}else{
			return null;
		}
	}

	/** удалить датчик из системы 
	 * @param sensor - датчик, который нужно удалить 
	 * @return - 
	 * <li> <b>Sensor</b> удаленный из контейнера Датчик/сенсор</li>
	 * <li> <b>null</b> ошибка удаления датчика </li>
	 */
	public Sensor removeSensor(int index){
		try{
			return this.sensors.remove(index);
		}catch(IndexOutOfBoundsException ex){
			return null;
		}
	}

	/** получить кол-во сенсоров в системе */
	public int getSize(){
		return this.sensors.size();
	}
	
	/** получить сенсор по его порядковому номеру в контейнере */
	public Sensor getSensor(int index){
		try{
			return this.sensors.get(index);
		}catch(IndexOutOfBoundsException ex){
			return null;
		}
	}
	
	/** получить сенсор по уникальному идентификатору - адресу в сети ModBus */
	public Sensor getSensorByModbusAddress(int address){
		Sensor returnValue=null;
		for(int counter=0;counter<this.getSize();counter++){
			if(this.getSensor(counter).getNumber()==address ){
				returnValue=this.getSensor(counter);
				break;
			}
		}
		return returnValue;
	}
}
