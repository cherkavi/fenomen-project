package com.cherkashin.vitaliy.modbus.session;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import com.cherkashin.vitaliy.modbus.function.Function;
import com.cherkashin.vitaliy.modbus.transport.Transport;

/** ������, ������� �������� ������� � ������ ������ ���� ������� */
public class Session  {
	/** ������ ���������� ������������������ �������� ��������� */
	public static final int ERROR_ALGORITHM=101;
	/** �� ������� ������ ���� ������������������  */
	public static final int ERROR_MODULE_NOT_RESPONSE=102;

	private Logger logger=Logger.getLogger(this.getClass());
	/** Error print Stream */
	private PrintStream printStreamError=System.err;
	
	/** Debug print stream */
	private PrintStream printStreamDebug=System.out;
	
	/** ��������� ��� ������ ������������������ */
	private byte[] beginSequence=new byte[]{0x3a};

	/** �������� ��� ������������������ */
	private byte[] endSequence=new byte[]{0x0d, 0x0a};
	
	/** ���������, �� ��������� �������� �������������� �������������� ����� � ��������, ������� ����������� */
	private Transport transport;
	
	/** ������, ������� ���������� ������ �� ������������� ������� */
	private TransportReader transportReader;
	/** ������, ������� �������� ������� � ������ ������ ���� ������� 
	 * @param transport - ������������ ������, ����� ������� ����� ������������ ���������� 
	 */
	public Session(Transport transport) throws Exception{
		this(transport, 200,400);
	}

	/** ������, ������� �������� ������� � ������ ������ ���� ������� 
	 * @param transport - ������������ ������, ����� ������� ����� ������������ ���������� 
	 * @param msWaitBegin - ����� � ������������ �������� ������ ������� (:)
	 * @param msWaitEnd - ����� � ������������ �������� ����� ������� (0x0d, 0x0a)
	 */
	public Session(Transport transport, 
				   int msWaitBegin, 
				   int msWaitEnd) throws Exception{
		this.transport=transport;
		transportReader=new TransportReader(this.transport.getOutputStream(), 
											msWaitBegin, 
											this.beginSequence, 
											msWaitEnd, 
											this.endSequence);
		this.transport.addDataForReadListener(transportReader);		
	}
	
	/** ���������� ����� ��� ������ ��������� ��������� */
	public void setErrorPrintStream(PrintStream printStream){
		this.printStreamError=printStream;
	}

	/** ���������� ����� ��� ������ ���������� ��������� */
	public void setDebugPrintStream(PrintStream printStream){
		this.printStreamDebug=printStream;
	}
	
	/** ����, ������� ������� � ������������� ������ ���������� ��������� ������� */
	private boolean flagDebug=false;
	/** ����, ������� ������� � ������������� ������ ��������� ��������� */
	private boolean flagError=false;
	
	/** ���������� ����������� ������ ���������� ���������, ������� ��� ���������� {@link #setDebugPrintStream(PrintStream)}*/
	public void setOutputDebug(boolean value){
		this.flagDebug=value;
	}

	/** ���������� ����������� ������ ���������� ���������, ������� ��� ���������� {@link #setErrorPrintStream(PrintStream)}*/
	public void setOutputError(boolean value){
		this.flagError=value;
	}

	
	/** 
	 * ������� ������� � �������� ����� �� ��� ������� � ���� ������������������ ����
	 * @param address - ����� ���������� (1..247) �������� ����� ���������� ������� 
	 * @param function - �������, ������� ����� ���� �������
	 * @return
	 * <li> <b>true</b> - ������ ����������, ����� ����� �������� �� �������, ������� ��� �������� ��� �������� </li> 
	 * <li> <b>false</b> - ������������� ����� ��������� ������ (�������� ������ {@link #getLastError})</li> 
	 */
	public boolean sendFunction(int address, Function function){
		boolean returnValue=false;
		try{
			logger.debug("������� ������������������ ����, ������� ����� ������� � OutputStream");
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			
			baos.write((byte)address);
			baos.write((byte)function.getNumber());
			baos.write(function.getData());
			logger.debug("��������� Longitudinal Redundancy Check � ���������� �������� �� ������������"); 
			baos.write(function.getLRC(baos.toByteArray()));
			
			byte[] sequenceForWrite=baos.toByteArray();
			logger.debug("������������ ��������� ������ � ��������");
			byte[] forOutput=this.addByteArray(this.beginSequence, 
					   this.convertByteToSequenceOfAscii(sequenceForWrite, 0, sequenceForWrite.length),
					   this.endSequence);
			if(this.flagDebug){
				this.printStreamDebug.println(function+"   ");
				printByteArray(this.printStreamDebug, forOutput);
			}
			byte[] answer=this.transportReader.write(forOutput);
			if(answer!=null){
				logger.debug("����� ������� - ������������ �����");
				function.decodeAnswer(address, answer);
				return true;
			}else{
				logger.error("��������� ������ �� �������");
				this.lastError=ERROR_MODULE_NOT_RESPONSE;
				return false;
			}
		}catch(Exception ex){
			this.lastError=ERROR_ALGORITHM;
			if(this.flagError){
				this.printStreamError.println("Exception: "+ex.getMessage());
			}
		}
		return returnValue;
	}
	
	
	
	private int lastError=0;
	/** �������� ��� ��������� ������ ����� ������ ������� {@link sendRequest}*/
	public int getLastError(){
		return this.lastError;
	}
	
	/** ������������ ��� �������
	 * @param out - ������, � ������� ����� �������� ������ ��� ������   
	 * @param value - ������, ������� ����� ���������� �� System.out
	 */
	private void printByteArray(PrintStream out, byte[] value){
		for(int counter=0;counter<value.length;counter++){
			out.print(Integer.toHexString(value[counter])+"  ");
		}
		out.println();
	}

	/** ������������� ����� �������� 123..EF � 0x30, 0x31, 0x32, 0x33... 0x0
	 * @param array - ������ �� ��������, ������� ������� �������������
	 * @param start - ��������� ��� 
	 * @param length - ���-�� 
	 * @return ������������������ ��� �������� � OutputStream  
	 */
	private byte[] convertByteToSequenceOfAscii(byte[] array, int start, int length){
		StringBuffer sequence=new StringBuffer();
		for(int counter=start;counter<start+length;counter++){
			String currentValue=null;
			if(array[counter]<0){
				currentValue=Integer.toHexString(array[counter]+256).toUpperCase();
			}else{
				currentValue=Integer.toHexString(array[counter]).toUpperCase();
			}
			if(currentValue.length()==1){
				sequence.append('0');
				sequence.append(currentValue);
			}else{
				sequence.append(currentValue);
			}
			
		}
		return sequence.toString().getBytes();
	}
	
	/** ��������� ��� ������� � ���� ����� */
	private byte[] addByteArray(byte[] ... arrays){
		int length=0;
		for(int counter=0;counter<arrays.length;counter++){
			if((arrays[counter]!=null)&&(arrays[counter].length!=0)){
				length+=arrays[counter].length;
			}
		}
		byte[] out=new byte[length];
		int index=0;
		for(int counter=0;counter<arrays.length;counter++){
			if((arrays[counter]!=null)&&(arrays[counter].length!=0)){
				for(int innerCounter=0;innerCounter<arrays[counter].length;innerCounter++){
					out[index]=arrays[counter][innerCounter];
					index++;
				}
			}
		}
		return out;
	}

	/** �������� ��������� ������ ��� ������ ������ */
	public void clearLastError() {
		this.lastError=0;
	}

}
