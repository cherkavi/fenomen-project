package com.cherkashin.vitaliy.modbus.core.schedule_read;

import java.util.Date;

/** ���� ��������� � ���������� */
public class DeviceRegisterBlock {
	/** ����� ������� �������� */
	private int addressBegin;

	/** ���-�� ��������� */
	private int count;

	/** ����� ��������� ��������� �������� */
	private Date lastOperation;

	/** �������� �������� ��������� */
	private int[] registers;
	
	/** ���� ��������� � ���������� 
	 * @param addressBegin - ����� ������� ��������
	 * @param count - ���-�� ��������� 
	 */
	public DeviceRegisterBlock(int addressBegin, int count){
		this.addressBegin=addressBegin;
		this.count=count;
		this.registers=new int[count];
	}
	
	/** �������� ������������� �������� �������� (������������ ������ ) 
	 * @param relativeNumber - ������������� ����� �������� 
	 * @return �������� ��������
	 */
	public int getRegister(int relativeNumber){
		return this.registers[relativeNumber];
	}

	/** �������� ����� ���������� ���������� */
	public int getAddressBegin() {
		return addressBegin;
	}

	/** ���������� ����� ���������� ���������� */
	public void setAddressBegin(int addressBegin) {
		this.addressBegin = addressBegin;
	}

	/** �������� ���-�� ��������� � ������ ����� */
	public int getRegisterCount() {
		return count;
	}

	/** ���������� ���-�� ��������� � ������ ����� */
	public void setRegisterCount(int count) {
		this.count = count;
	}
	
	/** �������� ����� ��������� �������� */
	public Date getTimeOfLastOperation(){
		return this.lastOperation;
	}
	
	/** ���������� ����� ��������� �������� */
	public void setTimeOfLastOperation(Date timeOperation){
		this.lastOperation=timeOperation;
	}
	
	/** ���������� ����������� �������� � ���� 
	 * @param date - ���� ������ �������� 
	 * @param values - ��������, ������� ����� ���������� 
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
	
	/** �������� �������� ��������� */
	public void clearRegisters(){
		for(int counter=0;counter<this.registers.length;counter++){
			this.registers[counter]=0;
		}
	}
}
