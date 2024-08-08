package com.cherkashin.vitaliy.modbus.core.direct;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.cherkashin.vitaliy.modbus.function.Function;
import com.cherkashin.vitaliy.modbus.function.FunctionReadHoldingRegisters;
import com.cherkashin.vitaliy.modbus.function.FunctionWriteManyRegister;
import com.cherkashin.vitaliy.modbus.session.Session;
import com.cherkashin.vitaliy.modbus.transport.Transport;
import com.cherkashin.vitaliy.modbus.transport.stable_rxtx.SerialPortProxy;

/** объект, который полность эмулирует сеть ModBus, реализуя основные функции работы с сетью, <strong> Особенность объекта - блокирующий</strong> */
public class ModBusNet {
	private Logger logger=Logger.getLogger(this.getClass());
	private Transport transport;
	private Session session;
	/** время ожидания начала посылки */
	private int timeWaitStartSending;
	/** время ожидания конца посылки */
	private int timeWaitEndSending;
	/** объект, который полность эмулирует сеть ModBus, реализуя основные функции работы с сетью
				System.out.println("#команда запуска программы чтения из порта <имя порта> <rate:2400,4800,9600,...115200> <databit: 5,6,7,8> <stop bit: 1,1.5, 2> <parity: none, even, odd, even, mark, none, odd, space> <server> <port of data> <port of heart beat> <delay of heart beat>");
				System.out.println("");
 
	 *   
	 * @param tcp_port_input_data - [20100] порт, на который нужно посылать данные (программе ComPortReaderWriter)
	 * @param tcp_port_output_data - [20101] порт, на который будут приходить данные (в программу ComPortReaderWriter)
	 * @param tcp_port_heart_beat - [20102] порт, который прослушивает сигнал сердцебиения 
	 * @param time_heart_beat - (5000) время ожидания сигнала сердцебиения (в милисекундах )
	 * @param run_command -команда запуска программы чтения из порта [имя порта] [rate:2400,4800,9600,...115200] [databit: 5,6,7,8] [stop bit: 1,1.5, 2] [parity: none, even, odd, even, mark, none, odd, space] [server] [port of data] [port of heart beat] [delay of heart beat]
	 * <br>
	 * <b> run_command=java -jar ComPortReaderWriter.jar COM4 9600 7 2 none 127.0.0.1 20100 20101 20102 4000 </b>
	 * @param timeWaitStartSending - время ожидания начала посылки 
	 * @param timeWaitEndSending - время ожидания окончания посылки
	 * @param outputDebug - (nullable) место вывода отладочной информации 
	 * @param outputError - (nullable) место вывода ошибочной информации 
	 * @throws Exception - возникло исключение при попытке инициализации 
	 * <strong> Особенность объекта - блокирующий, максимальное время блокировки - [время ожидания начала посылки], [время ожидания окончания посылки] </strong>
	 */
	public ModBusNet(Transport transport,
					 int timeWaitStartSending,
					 int timeWaitEndSending,
					 PrintStream outputDebug, 
					 PrintStream outputError) throws Exception {
		this.timeWaitStartSending=timeWaitStartSending;
		this.timeWaitEndSending=timeWaitEndSending;
		this.transport=transport;
		this.session=new Session(transport,this.timeWaitStartSending,this.timeWaitEndSending);
		if(outputDebug==null){
			session.setOutputDebug(false);
			session.setDebugPrintStream(System.out);
		}else{
			session.setOutputDebug(true);
			session.setDebugPrintStream(outputDebug);
		}
		if(outputError==null){
			session.setOutputError(false);
			session.setErrorPrintStream(System.err);
		}else{
			session.setOutputError(true);
			session.setErrorPrintStream(outputError);
		}
	}

	/** прочесть группу регистров  
	 * @param deviceAddress - уникальный номер устройства в сети ModBus 
	 * @param addressBegin - адрес начального регистра (0..65000)
	 * @param count - кол-во регистров 
	 * @return массив значений, прочитанный из регистров 
	 * @throws Exception
	 */
	public int[] readGroupRegister(int deviceAddress, int addressBegin, int count) throws Exception{
		session.clearLastError();
		
		int[] returnValue=null;
		FunctionReadHoldingRegisters function=new FunctionReadHoldingRegisters(addressBegin, count);
		if(session.sendFunction(deviceAddress, function)==true){
			logger.debug("данные успешно отработаны(получить значения ):");
			int registerCount=function.getRecordCount();
			logger.debug("Кол-во регистров: "+registerCount);
			returnValue=new int[registerCount];
			for(int counter=0;counter<registerCount;counter++){
				logger.debug(counter+" : "+function.getRegister(counter));
				returnValue[counter]=function.getRegister(counter);
			}
			return returnValue;
		}else{
			logger.error("ошибка обработки данных");
			throw new Exception(this.convertErrorToString(session.getLastError()));
		}
	}

	/** преобразование кода ошибки в текст */
	public String convertErrorToString(int errorCode){
		switch(errorCode){
			case Function.ERROR_DATA_NOT_RECIEVE: return "данные не получены";
			case Function.ERROR_ADDRESS: return "ответ не от того адресата";
			case Function.ERROR_FUNCTION: return "модуль отработал функции с ошибками";
			case Function.ERROR_DATA: return "ошибка полученных данных";
			case Function.ERROR_CRC: return "ошибка контрольной суммы";
			case Session.ERROR_ALGORITHM: return "ошибка выполнения последовательности действий алгоритма";
			case Session.ERROR_MODULE_NOT_RESPONSE: return "не получен первый кадр последовательности";
			default: return "Other Error";
		}
	}

	
	/**  записать группу регистров 
	 * @param deviceAddress - уникальный номер устройства в сети ModBus
	 * @param addressBegin - адрес начального регистра (0..65000)
	 * @param values - значения, которые следует записать 
	 * @throws Exception - ошибка в отрботке функции  
	 */
	public void writeGroupRegister(int deviceAddress, int addressBegin, int[] values) throws Exception{
		FunctionWriteManyRegister function=new FunctionWriteManyRegister(addressBegin, values);
		if(session.sendFunction(deviceAddress, function)==true){
			logger.info("данные успешно отработаны(получить значения ):");
		}else{
			logger.error("ошибка обработки данных");
			throw new Exception(this.convertErrorToString(session.getLastError()));
		}
	}
	
	/**  записать один регистр 
	 * @param deviceAddress - уникальный адрес устройства в сети ModBus
	 * @param addressBegin - адрес начального регистра (0..65000)
	 * @param values - значение, которые следует записать 
	 * @throws Exception - ошибка в отрботке функции  
	 */
	public void writeGroupRegister(int deviceAddress,  int addressBegin, int value) throws Exception{
		FunctionWriteManyRegister function=new FunctionWriteManyRegister(addressBegin, value);
		if(session.sendFunction(deviceAddress, function)==true){
			logger.info("данные успешно отработаны(получить значения ):");
		}else{
			logger.error("ошибка обработки данных");
			throw new Exception(this.convertErrorToString(session.getLastError()));
		}
	}

	public void close(){
		this.transport.close();
	}
	
	@Override
	protected void finalize() throws Throwable {
		try{
			this.close();
		}catch(Exception ex){};
	}
	
	public static void main(String[] args) throws Exception{
		Logger.getRootLogger().setLevel(Level.DEBUG);
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.DEFAULT_CONVERSION_PATTERN)));
		
		Properties properties=new Properties();
		File fileProperties=new File("settings.properties");
		properties.load(new FileInputStream(fileProperties));
		/** данный объект должен быть в отдельном потоке, т.к. может ожидать Socket-ного соединения, которое будет "тормозить" данный поток */
		Transport transport=new SerialPortProxy(Integer.parseInt(properties.getProperty("tcp_port_input_data")),
												Integer.parseInt(properties.getProperty("tcp_port_output_data")),
												Integer.parseInt(properties.getProperty("tcp_port_heart_beat")),
												Integer.parseInt(properties.getProperty("time_heart_beat")),
												properties.getProperty("run_command"));
		try{Thread.sleep(Integer.parseInt(properties.getProperty("time_heart_beat")));}catch(InterruptedException innerEx){};
		
		ModBusNet modbus=new ModBusNet(transport,400,400,null,null);
		for(int counter=1;counter<5;counter++){
			try{
				int [] returnValue=modbus.readGroupRegister(counter, 0, 1);
				System.out.println("device Found: "+counter+" ("+returnValue[0]+") ");
			}catch(Exception ex){
				System.out.println(counter+" : device NOT found");
			}
		}
		modbus.close();
	}
	
}
