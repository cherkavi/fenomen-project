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

/** ������, ������� �������� ��������� ���� ModBus, �������� �������� ������� ������ � �����, <strong> ����������� ������� - �����������</strong> */
public class ModBusNet {
	private Logger logger=Logger.getLogger(this.getClass());
	private Transport transport;
	private Session session;
	/** ����� �������� ������ ������� */
	private int timeWaitStartSending;
	/** ����� �������� ����� ������� */
	private int timeWaitEndSending;
	/** ������, ������� �������� ��������� ���� ModBus, �������� �������� ������� ������ � �����
				System.out.println("#������� ������� ��������� ������ �� ����� <��� �����> <rate:2400,4800,9600,...115200> <databit: 5,6,7,8> <stop bit: 1,1.5, 2> <parity: none, even, odd, even, mark, none, odd, space> <server> <port of data> <port of heart beat> <delay of heart beat>");
				System.out.println("");
 
	 *   
	 * @param tcp_port_input_data - [20100] ����, �� ������� ����� �������� ������ (��������� ComPortReaderWriter)
	 * @param tcp_port_output_data - [20101] ����, �� ������� ����� ��������� ������ (� ��������� ComPortReaderWriter)
	 * @param tcp_port_heart_beat - [20102] ����, ������� ������������ ������ ������������ 
	 * @param time_heart_beat - (5000) ����� �������� ������� ������������ (� ������������ )
	 * @param run_command -������� ������� ��������� ������ �� ����� [��� �����] [rate:2400,4800,9600,...115200] [databit: 5,6,7,8] [stop bit: 1,1.5, 2] [parity: none, even, odd, even, mark, none, odd, space] [server] [port of data] [port of heart beat] [delay of heart beat]
	 * <br>
	 * <b> run_command=java -jar ComPortReaderWriter.jar COM4 9600 7 2 none 127.0.0.1 20100 20101 20102 4000 </b>
	 * @param timeWaitStartSending - ����� �������� ������ ������� 
	 * @param timeWaitEndSending - ����� �������� ��������� �������
	 * @param outputDebug - (nullable) ����� ������ ���������� ���������� 
	 * @param outputError - (nullable) ����� ������ ��������� ���������� 
	 * @throws Exception - �������� ���������� ��� ������� ������������� 
	 * <strong> ����������� ������� - �����������, ������������ ����� ���������� - [����� �������� ������ �������], [����� �������� ��������� �������] </strong>
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

	/** �������� ������ ���������  
	 * @param deviceAddress - ���������� ����� ���������� � ���� ModBus 
	 * @param addressBegin - ����� ���������� �������� (0..65000)
	 * @param count - ���-�� ��������� 
	 * @return ������ ��������, ����������� �� ��������� 
	 * @throws Exception
	 */
	public int[] readGroupRegister(int deviceAddress, int addressBegin, int count) throws Exception{
		session.clearLastError();
		
		int[] returnValue=null;
		FunctionReadHoldingRegisters function=new FunctionReadHoldingRegisters(addressBegin, count);
		if(session.sendFunction(deviceAddress, function)==true){
			logger.debug("������ ������� ����������(�������� �������� ):");
			int registerCount=function.getRecordCount();
			logger.debug("���-�� ���������: "+registerCount);
			returnValue=new int[registerCount];
			for(int counter=0;counter<registerCount;counter++){
				logger.debug(counter+" : "+function.getRegister(counter));
				returnValue[counter]=function.getRegister(counter);
			}
			return returnValue;
		}else{
			logger.error("������ ��������� ������");
			throw new Exception(this.convertErrorToString(session.getLastError()));
		}
	}

	/** �������������� ���� ������ � ����� */
	public String convertErrorToString(int errorCode){
		switch(errorCode){
			case Function.ERROR_DATA_NOT_RECIEVE: return "������ �� ��������";
			case Function.ERROR_ADDRESS: return "����� �� �� ���� ��������";
			case Function.ERROR_FUNCTION: return "������ ��������� ������� � ��������";
			case Function.ERROR_DATA: return "������ ���������� ������";
			case Function.ERROR_CRC: return "������ ����������� �����";
			case Session.ERROR_ALGORITHM: return "������ ���������� ������������������ �������� ���������";
			case Session.ERROR_MODULE_NOT_RESPONSE: return "�� ������� ������ ���� ������������������";
			default: return "Other Error";
		}
	}

	
	/**  �������� ������ ��������� 
	 * @param deviceAddress - ���������� ����� ���������� � ���� ModBus
	 * @param addressBegin - ����� ���������� �������� (0..65000)
	 * @param values - ��������, ������� ������� �������� 
	 * @throws Exception - ������ � �������� �������  
	 */
	public void writeGroupRegister(int deviceAddress, int addressBegin, int[] values) throws Exception{
		FunctionWriteManyRegister function=new FunctionWriteManyRegister(addressBegin, values);
		if(session.sendFunction(deviceAddress, function)==true){
			logger.info("������ ������� ����������(�������� �������� ):");
		}else{
			logger.error("������ ��������� ������");
			throw new Exception(this.convertErrorToString(session.getLastError()));
		}
	}
	
	/**  �������� ���� ������� 
	 * @param deviceAddress - ���������� ����� ���������� � ���� ModBus
	 * @param addressBegin - ����� ���������� �������� (0..65000)
	 * @param values - ��������, ������� ������� �������� 
	 * @throws Exception - ������ � �������� �������  
	 */
	public void writeGroupRegister(int deviceAddress,  int addressBegin, int value) throws Exception{
		FunctionWriteManyRegister function=new FunctionWriteManyRegister(addressBegin, value);
		if(session.sendFunction(deviceAddress, function)==true){
			logger.info("������ ������� ����������(�������� �������� ):");
		}else{
			logger.error("������ ��������� ������");
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
		/** ������ ������ ������ ���� � ��������� ������, �.�. ����� ������� Socket-���� ����������, ������� ����� "���������" ������ ����� */
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
