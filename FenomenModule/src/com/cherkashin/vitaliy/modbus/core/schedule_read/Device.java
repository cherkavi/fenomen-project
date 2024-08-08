package com.cherkashin.vitaliy.modbus.core.schedule_read;

import java.util.ArrayList;

/** устройство в сети ModBus  */
public class Device {
	/** адрес устройства в сети ModBus  */
	private int address;
	/** массив блоков регистров */
	private ArrayList<DeviceRegisterBlock> blocks=new ArrayList<DeviceRegisterBlock>();
	
	/** устройство в сети ModBus  
	 * @param address - адрес устройства в сети 
	 */
	public Device(int address){
		this.address=address;
	}
	
	/** добавить в устройство блок для чтения */
	public void addReadBlock(DeviceRegisterBlock block){
		this.blocks.add(block);
	}
	
	/** удалить из устройства блок для чтения */
	public void removeBlock(DeviceRegisterBlock block){
		this.blocks.remove(block);
	}
	
	/** удалить из устройства блок для чтения по его номеру  */
	public void removeBlock(int index){
		this.blocks.remove(index);
	}
	
	/** получить кол-во всех блоков в системе  */
	public int getBlockCount(){
		return this.blocks.size();
	}
	
	/** получить блок по его номеру  */
	public DeviceRegisterBlock getBlock(int index){
		return this.blocks.get(index);
	}

	/** получить адрес устройства в сети ModBus  */
	public int getAddress() {
		return address;
	}
	
	/** установить адрес устройства в сети ModBus */
	public void setAddress(int address) {
		this.address = address;
	}

	/** установить значение для регистра */
	public void setRegisterValue(int number, int value) {
		// INFO модуль.установить значение для регистра в сети Modbus по конкретному устройству
		synchronized(this.taskWriteRegisterList){
			this.taskWriteRegisterList.add(new TaskWriteRegister(number, value));
		}
	}
	
	/** список регистров, значения которых нужно передать в сеть ModBus */
	private ArrayList<TaskWriteRegister> taskWriteRegisterList=new ArrayList<TaskWriteRegister>();
	
	/** получить кол-во заданий для записи регистров в Modbus по данному устройству  */
	public int getTaskWriteRegisterCount(){
		synchronized(this.taskWriteRegisterList){
			return this.taskWriteRegisterList.size();
		}
	}
	
	/** получить задание и удалить из списка  
	 * @return задание для записи регистра в сеть Modbus 
	 * */
	public TaskWriteRegister popTaskWriteRegister(){
		if(this.taskWriteRegisterList.size()>0){
			synchronized(this.taskWriteRegisterList){
				return this.taskWriteRegisterList.remove(0);
			}
		}else{
			return null;
		}
	}
}

