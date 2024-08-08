package com.cherkashin.vitaliy.modbus.core.schedule_read.test;

import java.io.File;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.cherkashin.vitaliy.modbus.core.direct.ModBusNet;
import com.cherkashin.vitaliy.modbus.core.schedule_read.Device;
import com.cherkashin.vitaliy.modbus.core.schedule_read.DeviceRegisterBlock;
import com.cherkashin.vitaliy.modbus.core.schedule_read.ScheduleThread;
import com.cherkashin.vitaliy.modbus.transport.Transport;
import com.cherkashin.vitaliy.modbus.transport.stable_rxtx.SerialPortProxy;

public class Test extends Thread{
	
	public static void main(String[] args){
		Logger.getRootLogger().setLevel(Level.ERROR);
		//Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.DEFAULT_CONVERSION_PATTERN)));
		//Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-4r [%t] %-5p %c %x - %m%n")));
		try{
			Logger.getRootLogger().addAppender(new FileAppender(new PatternLayout("%-4r [%t] %-5p %c{1} %x - %m%n"),"out.debug"));
			//Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-4r [%t] %-5p %c{1} %x - %m%n")));
		}catch(Exception ex){
			Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-4r [%t] %-5p %c{1} %x - %m%n")));
		}
		(new Test()).start();
	}
	
	@Override
	public void run(){
		System.out.println("begin");
		try{
			TableOfRegisters tableOfRegisters=new TableOfRegisters(20);
			Device device=new Device(0x01);
			device.addReadBlock(new DeviceRegisterBlock(0,20));
			Properties properties=new Properties();
			File fileProperties=new File("settings.properties");
			properties.load(new FileInputStream(fileProperties));
			/** данный объект должен быть в отдельном потоке, т.к. может ожидать Socket-ного соединения, которое будет "тормозить" данный поток */
			Transport transport=new SerialPortProxy(Integer.parseInt(properties.getProperty("tcp_port_input_data")),
												    Integer.parseInt(properties.getProperty("tcp_port_output_data")),
												    Integer.parseInt(properties.getProperty("tcp_port_heart_beat")),
												    Integer.parseInt(properties.getProperty("time_heart_beat")),
												    properties.getProperty("run_command"));
			
			//Transport transport=new ComTransport("COM4",9600,ComTransport.DATABITS_7,ComTransport.STOPBITS_2,ComTransport.PARITY_NONE);
			try{Thread.sleep(Integer.parseInt(properties.getProperty("time_heart_beat")));}catch(InterruptedException innerEx){};
			
			ModBusNet modbus=new ModBusNet(transport,400,400,null,null);
			
			ScheduleThread schedule=new ScheduleThread(modbus, 400, new Device[]{device});
			schedule.start();
			while(true){
				DeviceRegisterBlock block=device.getBlock(0);
				synchronized(block){
					for(int counter=0;counter<block.getRegisterCount();counter++){
						tableOfRegisters.setText(counter, Integer.toString(block.getRegister(counter)));
					}
				}
				try{
					Thread.sleep(300);
				}catch(InterruptedException inEx){};
			}
		}catch(Exception ex){
			System.err.println("Exception:"+ex.getMessage());
		}
		System.out.println("end");
	}
}
