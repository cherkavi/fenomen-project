package com.cherkashin.vitaliy.modbus.transport.direct_rxtx;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;

import com.cherkashin.vitaliy.modbus.transport.Transport;

/** транспортный объект, который предоставляет варианты связи с внешними устройствами посредством COM порта */
@SuppressWarnings("restriction")
public class ComTransport extends Transport implements SerialPortEventListener{
	/** кол-во бит данных в посылке - 5*/
	public static final int DATABITS_5=SerialPort.DATABITS_5;
	/** кол-во бит данных в посылке - 6*/
	public static final int DATABITS_6=SerialPort.DATABITS_6;
	/** кол-во бит данных в посылке - 7*/
	public static final int DATABITS_7=SerialPort.DATABITS_7;
	/** кол-во бит данных в посылке - 8*/
	public static final int DATABITS_8=SerialPort.DATABITS_8;

	/** кол-во стопбит  */
	public static final int STOPBITS_1=SerialPort.STOPBITS_1;
	/** кол-во стопбит  */
	public static final int STOPBITS_1_5=SerialPort.STOPBITS_1_5;
	/** кол-во стопбит  */
	public static final int STOPBITS_2=SerialPort.STOPBITS_2;

	/** контроль четности */
	public static final int PARITY_EVEN=SerialPort.PARITY_EVEN;
	/** контроль четности */
	public static final int PARITY_MARK=SerialPort.PARITY_MARK;
	/** контроль четности */
	public static final int PARITY_NONE=SerialPort.PARITY_NONE;
	/** контроль четности */
	public static final int PARITY_ODD=SerialPort.PARITY_ODD;
	/** контроль четности */
	public static final int PARITY_SPACE=SerialPort.PARITY_SPACE;

	/** скорость обмена с COM портом - 110 */
	public static final int RATE_110=110;
	/** скорость обмена с COM портом - 300 */
	public static final int RATE_300=300;
	/** скорость обмена с COM портом - 600 */
	public static final int RATE_600=600;
	/** скорость обмена с COM портом - 1200 */
	public static final int RATE_1200=1200;
	/** скорость обмена с COM портом - 2400 */
	public static final int RATE_2400=2400;
	/** скорость обмена с COM портом - 4800 */
	public static final int RATE_4800=4800;
	/** скорость обмена с COM портом - 9600 */
	public static final int RATE_9600=9600;
	/** скорость обмена с COM портом - 19200 */
	public static final int RATE_19200=19200;
	/** скорость обмена с COM портом - 38400 */
	public static final int RATE_38400=38400;
	/** скорость обмена с COM портом - 57600 */
	public static final int RATE_57600=57600;
	/** скорость обмена с COM портом - 115200 */
	public static final int RATE_115200=115200;
	/** скорость обмена с COM портом - 12800 */
	public static final int RATE_12800=12800;
	/** скорость обмена с COM портом - 25600 */
	public static final int RATE_25600=25600;
	
	private SerialPort serialPort=null;

	/** транспортный объект, который предоставляет варианты связи с внешними устройствами посредством COM порта 
	 * @param comPortName - имя порта для соединения (Example: "COM1"..."COM24"), соединение по умолчанию: (8 databits, no parity, 1 stopbits) 
	 * @throws Exception - в случае неудачной попытки соединения 
	 */
	public ComTransport(String comPortName) throws Exception {
		this(comPortName,9600,DATABITS_8,STOPBITS_1,PARITY_NONE);
	}

	/** транспортный объект, который предоставляет варианты связи с внешними устройствами посредством COM порта 
	 * @param comPortName - имя порта для соединения (Example: "COM1"..."COM24")
	 * @param rate - скорость обмена с портом ({@link #RATE_110},{@link #RATE_300}, {@link #RATE_600}, {@link #RATE_1200}, {@link #RATE_2400}, {@link #RATE_4800}, {@link #RATE_9600} )
	 * @param databit кол-во бит данных ({@link #DATABITS_5}, {@link #DATABITS_6}, {@link #DATABITS_7}, {@link #DATABITS_8} )
	 * @param stopbits кол-во стоп-бит ({@link #STOPBITS_1}, {@link #STOPBITS_1_5}, {@link #STOPBITS_2})
	 * @param parity контроль четности ({@link #PARITY_EVEN}, {@link #PARITY_MARK}, {@link #PARITY_NONE}, {@link #PARITY_ODD}, {@link #PARITY_SPACE} )
	 * @throws - ошибка в случае невозможности подключения данного устройства 
	 */
	public ComTransport(String comPortName, 
						int rate, 
						int databit, 
						int stopbits, 
						int parity) throws Exception{
		CommPortIdentifier portIdentifier=CommPortIdentifier.getPortIdentifier(comPortName);
		serialPort=(SerialPort)portIdentifier.open(this.getClass().getName(), 3000);
		serialPort.setSerialPortParams(rate, 
								       databit, 
								       stopbits, 
								       parity);
		serialPort.addEventListener(this);
		serialPort.notifyOnDataAvailable(true);
	}
	
	@Override
	public OutputStream getOutputStream() throws Exception{
		return this.serialPort.getOutputStream();
	}

	byte[] readBuffer=new byte[256];
	@Override
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
	    case SerialPortEvent.BI:
	    case SerialPortEvent.OE:
	    case SerialPortEvent.FE:
	    case SerialPortEvent.PE:
	    case SerialPortEvent.CD:
	    case SerialPortEvent.CTS:
	    case SerialPortEvent.DSR:
	    case SerialPortEvent.RI:
	    case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
	    	break;
	    case SerialPortEvent.DATA_AVAILABLE:
	    	try{
	    		ByteArrayOutputStream baos=new ByteArrayOutputStream();
	    		int count=0;
	    		while((count=this.serialPort.getInputStream().read(readBuffer))>0){
	    			baos.write(readBuffer,0,count);
	    		}
	    		this.notifyAllListeners(baos.toByteArray());
	    	}catch(Exception ex){
		    	System.err.println("ComTransport#serialEvent Exception: "+ex.getMessage());
	    	}
	    	break;
	    }
	}		

	@Override
	public void close(){
		try{
			this.serialPort.notifyOnDataAvailable(false);
			this.serialPort.removeEventListener();
			this.serialPort.close();
		}catch(Exception ex){};
	}
}
