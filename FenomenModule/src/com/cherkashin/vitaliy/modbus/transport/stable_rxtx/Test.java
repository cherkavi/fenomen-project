package com.cherkashin.vitaliy.modbus.transport.stable_rxtx;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class Test {
	// private Logger logger=Logger.getLogger(this.getClass());
	
	public static void main(String[] args){
		try{
			if(args.length==0){
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
			Properties properties=new Properties();
			properties.load(new FileInputStream(args[0]));
			
			setRootLoggerLevel(properties.getProperty("logger_level"));
			setRootLoggerAppender(properties.getProperty("logger_file"));
			new SerialPortProxy(Integer.parseInt(properties.getProperty("tcp_port_input_data")),
							    Integer.parseInt(properties.getProperty("tcp_port_output_data")),
						   		Integer.parseInt(properties.getProperty("tcp_port_heart_beat")),
						   		Integer.parseInt(properties.getProperty("time_heart_beat")),
						   		properties.getProperty("run_command"));
		}catch(Exception ex){
			System.err.println("Check your COM port ");
			System.err.println("Exception ex:"+ex.getMessage());
		}
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
