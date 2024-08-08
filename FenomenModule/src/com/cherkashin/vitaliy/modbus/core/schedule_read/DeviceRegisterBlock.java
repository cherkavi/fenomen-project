package com.cherkashin.vitaliy.modbus.core.schedule_read;

import java.util.Date;

/** блок регистров в устройстве */
public class DeviceRegisterBlock {
	/** адрес первого регистра */
	private int addressBegin;

	/** кол-во регистров */
	private int count;

	/** время последней установки значений */
	private Date lastOperation;

	/** реальные значения регистров */
	private int[] registers;
	
	/** блок регистров в устройстве 
	 * @param addressBegin - адрес первого регистра
	 * @param count - кол-во регистров 
	 */
	public DeviceRegisterBlock(int addressBegin, int count){
		this.addressBegin=addressBegin;
		this.count=count;
		this.registers=new int[count];
	}
	
	/** получить относительное значение регистра (относительно адреса ) 
	 * @param relativeNumber - относительный номер регистра 
	 * @return значение регистра
	 */
	public int getRegister(int relativeNumber){
		return this.registers[relativeNumber];
	}

	/** получить адрес начального считывания */
	public int getAddressBegin() {
		return addressBegin;
	}

	/** установить адрес начального считывания */
	public void setAddressBegin(int addressBegin) {
		this.addressBegin = addressBegin;
	}

	/** получить кол-во регистров в данном блоке */
	public int getRegisterCount() {
		return count;
	}

	/** установить кол-во регистров в данном блоке */
	public void setRegisterCount(int count) {
		this.count = count;
	}
	
	/** получить время последней операции */
	public Date getTimeOfLastOperation(){
		return this.lastOperation;
	}
	
	/** установить время последней операции */
	public void setTimeOfLastOperation(Date timeOperation){
		this.lastOperation=timeOperation;
	}
	
	/** установить прочитанные значения в блок 
	 * @param date - дата чтения значений 
	 * @param values - значения, которые нужно установить 
	 */
	public void setValuesIntoRegisters(Date date, int[] values){
		this.clearRegisters();
		this.setTimeOfLastOperation(date);
		try{
			for(int counter=0;counter<this.registers.length;counter++){
				this.registers[counter]=values[counter];
			}
		}catch(Exception ex){
		}
	}
	
	/** очистить значения регистров */
	public void clearRegisters(){
		for(int counter=0;counter<this.registers.length;counter++){
			this.registers[counter]=0;
		}
	}
}
