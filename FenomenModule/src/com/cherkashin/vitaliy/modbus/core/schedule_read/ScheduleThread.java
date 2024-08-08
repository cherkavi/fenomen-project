package com.cherkashin.vitaliy.modbus.core.schedule_read;

import java.util.Date;

import org.apache.log4j.Logger;

import com.cherkashin.vitaliy.modbus.core.direct.ModBusNet;

/** ������, ������� ���������� ���� ModBus ����� �������� ���������� �������, � ���������� ������ � ����������� ���������� */
public class ScheduleThread extends Thread{
	/** ������������ ���-�� ������� �������� �������� � ���� ModBus �� ��������� �������� �������� */
	public static int maximumNumbersOfAttempts=3;
	private Logger logger=Logger.getLogger(this.getClass());
	/** ���� ModBus, ������� ����� ������������ */
	private ModBusNet modbus;
	/** ���������� � �������� ����� ��������� ��������� ����������� */
	private Device[] devices;
	/** �������� � ������������ */
	private int timeDelay;
	
	/** ������, ������� ���������� ���� ModBus ����� �������� ���������� �������, � ���������� ������ � ����������� ���������� 
	 * @param modbus - ����, � ������� ����� ����������� ������  
	 * @param timeDelay - ����� �������� ����� ������� ���������� ������� ����() 
	 * @param devices - ���������� � ����, ������� ����� ������������
	 * <br>
	 * <b> important - need to start this object </b> 
	 */
	public ScheduleThread(ModBusNet modbus,
						  int timeDelay,
						  Device[] devices){
		this.modbus=modbus;
		this.devices=devices;
		this.timeDelay=timeDelay;
	}
	
	private boolean flagRun=false;
	
	/** ���������� ����� ��������� ������ */
	public void stopThread(){
		this.interrupt();
		this.flagRun=false;
	}
	
	public void run(){
		flagRun=true;
		while(this.flagRun){
			logger.debug("process all devices ");
			for(int counter=0;counter<this.devices.length;counter++){
				if(this.devices[counter].getTaskWriteRegisterCount()>0){
					logger.debug("Device #"+counter+" Task:"+this.devices[counter].getTaskWriteRegisterCount());
					while(this.devices[counter].getTaskWriteRegisterCount()>0){
						TaskWriteRegister taskWriteRegister=this.devices[counter].popTaskWriteRegister();
						int errorCounter=maximumNumbersOfAttempts;
						while(true){
							if(errorCounter<=0)break;
							try{
								this.modbus.writeGroupRegister(this.devices[counter].getAddress(), taskWriteRegister.getRegisterNumber(), taskWriteRegister.getValue());
								break;
							}catch(Exception ex){
								logger.error("Register Write Error: "+ex.getMessage());
								errorCounter--;
							}
						}
					}
				}
				
				logger.debug("Device #"+counter+"   BlockCount:"+this.devices[counter].getBlockCount());
				for(int index=0;index<this.devices[counter].getBlockCount(); index++){
					DeviceRegisterBlock currentBlock=this.devices[counter].getBlock(index);
					try{
						int[] values=this.modbus.readGroupRegister(this.devices[counter].getAddress(), currentBlock.getAddressBegin(), currentBlock.getRegisterCount());
						synchronized(currentBlock){
							currentBlock.clearRegisters();
							currentBlock.setValuesIntoRegisters(new Date(), values);
						}
					}catch(Exception ex){
						logger.warn("Device: "+counter+"("+this.devices[counter].getAddress()+") block:"+index+" (Addr:"+currentBlock.getAddressBegin()+", "+currentBlock.getRegisterCount()+") Exception:"+ex.getMessage());
					}
				}
				
			}
			try{
				logger.debug("sleep before next read block");
				Thread.sleep(this.timeDelay);
			}catch(InterruptedException ex){};
		}
	}
}
