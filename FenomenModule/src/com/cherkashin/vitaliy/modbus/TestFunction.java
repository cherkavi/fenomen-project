package com.cherkashin.vitaliy.modbus;

import java.io.File;
import java.io.FileInputStream;

import java.io.PrintStream;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.cherkashin.vitaliy.modbus.function.FunctionReadHoldingRegisters;
import com.cherkashin.vitaliy.modbus.session.Session;
import com.cherkashin.vitaliy.modbus.transport.Transport;
import com.cherkashin.vitaliy.modbus.transport.stable_rxtx.SerialPortProxy;

public class TestFunction {
	public static void main(String[] args){
		System.out.println("-begin-");
		Transport transport=null;
		try{
			/*transport=new ComTransport("COM4",
									   ComTransport.RATE_9600,
									   ComTransport.DATABITS_7, 
									   ComTransport.STOPBITS_2, 
									   ComTransport.PARITY_NONE);*/
			Properties properties=new Properties();
			File fileProperties=new File("settings.properties");
			if((args.length==0)&&(fileProperties.exists()==false)){
				System.err.println(" <config.properties>");
				System.out.println("#порт, на который нужно посылать данные (программе ComPortReaderWriter)");
				System.out.println("tcp_port_input_data=20100");
				System.out.println("#порт, на который будут приходить данные (в программу ComPortReaderWriter) ");
				System.out.println("tcp_port_output_data=20101");
				System.out.println("#порт, который прослушивает сигнал сердцебиения ");
				System.out.println("tcp_port_heart_beat=20102");
				System.out.println("#время ожидания сигнала сердцебиения (в милисекундах )");
				System.out.println("time_heart_beat=5000 ");
				System.out.println("#команда запуска программы чтения из порта <имя порта> <rate:2400,4800,9600,...115200> <databit: 5,6,7,8> <stop bit: 1,1.5, 2> <parity: none, even, odd, even, mark, none, odd, space> <server> <port of data> <port of heart beat> <delay of heart beat>");
				System.out.println("run_command=java -jar ComPortReaderWriter.jar COM4 9600 7 2 none 127.0.0.1 20100 20101 20102 4000");
				System.out.println("#logger Level: DEBUG, INFO, WARN, ERROR ");
				System.out.println("logger_level=ERROR ");
				System.out.println("#logger file: path to file or null(console)");
				System.out.println("logger_file=null ");
				System.exit(1);
			}
			if(args.length==0){
				properties.load(new FileInputStream(fileProperties));
			}else{
				properties.load(new FileInputStream(args[0]));
			}
			setRootLoggerLevel(properties.getProperty("logger_level"));
			setRootLoggerAppender(properties.getProperty("logger_file"));
			/** данный объект должен быть в отдельном потоке, т.к. может ожидать Socket-ного соединения, которое будет "тормозить" данный поток */
			transport=new SerialPortProxy(Integer.parseInt(properties.getProperty("tcp_port_input_data")),
				    					  Integer.parseInt(properties.getProperty("tcp_port_output_data")),
				    					  Integer.parseInt(properties.getProperty("tcp_port_heart_beat")),
				    					  Integer.parseInt(properties.getProperty("time_heart_beat")),
				    					  properties.getProperty("run_command"));
			
			try{Thread.sleep(Integer.parseInt(properties.getProperty("time_heart_beat")));}catch(InterruptedException innerEx){};
			
			Session session=new Session(transport,200,200);
			session.setOutputDebug(false);
			session.setDebugPrintStream(System.out);
			session.setOutputError(true);
			session.setErrorPrintStream(System.err);
			session.clearLastError();
			
			FunctionReadHoldingRegisters function=new FunctionReadHoldingRegisters(10, 10);

			if(session.sendFunction(0x01, function)==true){
				System.out.println("данные успешно отработаны(получить значения ):");
				int registerCount=function.getRecordCount();
				System.out.println("Кол-во регистров: "+registerCount);
				for(int counter=0;counter<registerCount;counter++){
					System.out.println(counter+" : "+function.getRegister(counter));
				}
			}else{
				// ошибка обработки данных
				System.out.println("Error: "+session.getLastError());
			}
			
			try{
				Thread.sleep(15000);
			}catch(Exception ex){};
			// repeat 
			if(session.sendFunction(0x01, function)==true){
				System.out.println("данные успешно отработаны(получить значения ):");
				int registerCount=function.getRecordCount();
				System.out.println("Кол-во регистров: "+registerCount);
				for(int counter=0;counter<registerCount;counter++){
					System.out.println(counter+" : "+function.getRegister(counter));
				}
			}else{
				// ошибка обработки данных 
				System.out.println("Error: "+session.getLastError());
			}
		
		}catch(Exception ex){
			System.err.println("Exception: "+ex.getMessage());
		}finally{
			transport.close();
		}
		System.out.println("--end--");
	}
	
	/** используется для отладки
	 * @param out - объект, в который нужно передать данные для вывода   
	 * @param value - массив, который нужно отобразить на System.out
	 */
	@SuppressWarnings("unused")
	private static void printByteArray(PrintStream out, byte[] value){
		for(int counter=0;counter<value.length;counter++){
			out.print(Integer.toHexString(value[counter])+"  ");
		}
		out.println();
	}

	private static void setRootLoggerAppender(String file){
		if((file!=null)&&(!file.trim().equalsIgnoreCase("null"))){
			try{
				Logger.getRootLogger().addAppender(new FileAppender(new PatternLayout("%d{ABSOLUTE} %5p %c{1}:%L - %m%n"),file));
			}catch(Exception ex){
				Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} %5p %c{1}:%L - %m%n")));
			}
		}else{
			Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} %5p %c{1}:%L - %m%n")));
		}
	}
	
	private static void setRootLoggerLevel(String level){
		if(level!=null){
			if(level.trim().equalsIgnoreCase("DEBUG")){
				Logger.getRootLogger().setLevel(Level.DEBUG);
			}
			if(level.trim().equalsIgnoreCase("INFO")){
				Logger.getRootLogger().setLevel(Level.INFO);
			}
			if(level.trim().equalsIgnoreCase("WARN")){
				Logger.getRootLogger().setLevel(Level.WARN);
			}
			if(level.trim().equalsIgnoreCase("ERROR")){
				Logger.getRootLogger().setLevel(Level.ERROR);
			}
		}
	}
	
}
