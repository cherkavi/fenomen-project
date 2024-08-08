package com.cherkashin.vitaliy.modbus.core.schedule_read;

/** задача для записи значения регистра  */
public class TaskWriteRegister {
	/** уникальный номер регистра */
	private int registerNumber;
	/** значение для регистра */
	private int value;
	
	/** задача для записи значения регистра  
	 * @param register 
	 * @param value
	 */
	public TaskWriteRegister(int registerNumber, int value){
		this.registerNumber=registerNumber;
		this.value=value;
	}

	public int getRegisterNumber() {
		return registerNumber;
	}

	public void setRegisterNumber(int registerNumber) {
		this.registerNumber = registerNumber;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
}
