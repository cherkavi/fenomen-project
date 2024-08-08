package com.cherkashin.vitaliy.modbus.transport.stable_rxtx;

import java.io.OutputStream;
import org.apache.log4j.Logger;

import com.cherkashin.vitaliy.modbus.transport.Transport;
import com.cherkashin.vitaliy.modbus.transport.stable_rxtx.listener.ISocketPortListener;
import com.cherkashin.vitaliy.modbus.transport.stable_rxtx.listener.SocketHeartBeatProgramRunner;
import com.cherkashin.vitaliy.modbus.transport.stable_rxtx.listener.SocketPortListener;

/** ����� ������� ������� ������� ������ � �������� SerialPort, ������� ����������� � ��������� �������� � ��������������� ������ ����������� TCP ������
 * (�������������� ������ ��������, � ������ �� ����������� ������� ������������)*/
public class SerialPortProxy extends Transport implements ISocketPortListener {
	private Logger logger=Logger.getLogger(this.getClass());
	private SerialPortProxyOutputStream outputStream;
	
	/** ����� ������� ������� ������� ������ � �������� SerialPort, ������� ����������� � ��������� �������� � ��������������� ������ ����������� TCP ������
	 * (�������������� ������ ��������, � ������ �� ����������� ������� ������������)
	 * @param portWrite - ����, � ������� ����� ������������ ������ ��� ������ � ������ (20100)
	 * @param portRead - ���� �� �������� ����� �������� ������, ��������� ���������� ������ � ������ (20101)
	 * @param portHeartBeat - ����, ������� ����� �������������� �� ������� ������� ������� "������������" (20102) 
	 * @param heartBeatDelay - ������������ ����� �������� ������� "������������" (5000)
	 * @param executeProgram - ������ ��� ������� ��������� � ��������� ������ ��� ������������� COM ����� {@link com_port.ComPort}
	 *  java -jar ComPortReaderWriter.jar COM4 9600 8 1 none 127.0.0.1 20100 20101 20102 4000
	 *  <br>
	 *  ��� ��������� ������� � SerialPort ��������� �������� ����� ������-������� ������, �.�. ��� ����� ��������� � ��������� ������ ��������� ( ����� ���� - ���� ��� ),
	 *  ����� ������� ������� ��������� �� 1000 �� - ��� ������� ����� �� ����� ������ � ���������� ����� ���������
	 *  
	 *   @throws - ���� �� ������� ���������� ������������� ������ 
	 */
	public SerialPortProxy(int portRead, int portWrite, int portHeartBeat, int heartBeatDelay, String executeProgram) throws Exception{
		// �����, ������� ����� ���������� ������ � ����
		this.outputStream=new SerialPortProxyOutputStream(portWrite);
		socketPortListener=new SocketPortListener(portRead);
		socketPortListener.addDataListener(this);
		socketHeartBeat=new SocketHeartBeatProgramRunner(portHeartBeat, heartBeatDelay, executeProgram);
	}
	private SocketPortListener socketPortListener;
	private SocketHeartBeatProgramRunner socketHeartBeat;
	
	@Override
	public void notifyDataFromPort(byte[] data) {
		if(data!=null){
			logger.debug("�������� ������ � �����:"+data.length);
			this.notifyAllListeners(data);
		}
	}
	
	@Override
	public OutputStream getOutputStream(){
		return this.outputStream;
	}
	
	@Override
	public void close() {
		this.socketPortListener.stopThread();
		socketHeartBeat.stopThread();
	}
}
