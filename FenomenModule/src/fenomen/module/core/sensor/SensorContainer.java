package fenomen.module.core.sensor;

import java.util.ArrayList;

import com.cherkashin.vitaliy.modbus.core.direct.ModBusNet;
import com.cherkashin.vitaliy.modbus.core.schedule_read.Device;
import com.cherkashin.vitaliy.modbus.core.schedule_read.DeviceRegisterBlock;
import com.cherkashin.vitaliy.modbus.core.schedule_read.ScheduleThread;

/** ������, ������� �������� ��� ������� � ������� */
public class SensorContainer {
	private ArrayList<Sensor> sensors=new ArrayList<Sensor>();
	/** ������, ������� ��������� ���� ModBus */
	private ModBusNet modbus=null;

	/** ������, ������� �������� ��� ������� � ������� 
	 * @param modbus - ������, ������� ��������� ���� ModBus
	 * <br>
	 * ���������� �������� ����� {@link #startService() ������ �������} - ��� ������ ������  
	 */
	public SensorContainer(ModBusNet modbus){
		this.modbus=modbus;
	}
	
	/** ������ ������� �� ����������  */
	public void startService(){
		Device[] devices=new Device[this.sensors.size()];
		for(int counter=0;counter<this.sensors.size();counter++){
			devices[counter]=this.sensors.get(counter).getDevice();
		}
		(new ScheduleThread(modbus, 200, devices)).start();
	}
	
	/** ���������� ���������� �� ��������� ���� ������� ��������  
	 * @param type - ��� ����������
	 * */
	public static DeviceRegisterBlock[] getRegisterBlock(int[] type){
		// TODO ������. �� ��������� ���� ���������� �������� ����� ��� ������ ������
		return new DeviceRegisterBlock[]{new DeviceRegisterBlock(0,20)}; 
	}
	
	
	/** ��������� ���������� � ������� ��� ���, ���� ������ ���������� �������  
	 * @param number - ���������� ����� ����������
	 * @return <li>null - ���������� �� �������</li> <li> ������ ���� int[2] 0:(������� ����),1:(������� ����)</li> 
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
	
	/** �������� ������ � ������� */
	public void addSensor(Sensor sensor){
		this.sensors.add(sensor);
	}
	
	/** ������� ������ �� ������� 
	 * @param sensor - ������, ������� ����� ������� 
	 * @return - 
	 * <li> <b>Sensor</b> ��������� �� ���������� ������/������</li>
	 * <li> <b>null</b> ������ �������� ������� </li>
	 */
	public Sensor removeSensor(Sensor sensor){
		if(this.sensors.remove(sensor)==true){
			return sensor;
		}else{
			return null;
		}
	}

	/** ������� ������ �� ������� 
	 * @param sensor - ������, ������� ����� ������� 
	 * @return - 
	 * <li> <b>Sensor</b> ��������� �� ���������� ������/������</li>
	 * <li> <b>null</b> ������ �������� ������� </li>
	 */
	public Sensor removeSensor(int index){
		try{
			return this.sensors.remove(index);
		}catch(IndexOutOfBoundsException ex){
			return null;
		}
	}

	/** �������� ���-�� �������� � ������� */
	public int getSize(){
		return this.sensors.size();
	}
	
	/** �������� ������ �� ��� ����������� ������ � ���������� */
	public Sensor getSensor(int index){
		try{
			return this.sensors.get(index);
		}catch(IndexOutOfBoundsException ex){
			return null;
		}
	}
	
	/** �������� ������ �� ����������� �������������� - ������ � ���� ModBus */
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
