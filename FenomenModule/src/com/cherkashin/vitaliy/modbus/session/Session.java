package com.cherkashin.vitaliy.modbus.session;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import com.cherkashin.vitaliy.modbus.function.Function;
import com.cherkashin.vitaliy.modbus.transport.Transport;

/** объект, который передает функцию и выдает ответы этой функции */
public class Session  {
	/** ошибка выполнения последовательности действий алгоритма */
	public static final int ERROR_ALGORITHM=101;
	/** не получен первый кадр последовательности  */
	public static final int ERROR_MODULE_NOT_RESPONSE=102;

	private Logger logger=Logger.getLogger(this.getClass());
	/** Error print Stream */
	private PrintStream printStreamError=System.err;
	
	/** Debug print stream */
	private PrintStream printStreamDebug=System.out;
	
	/** заголовок для начала последовательности */
	private byte[] beginSequence=new byte[]{0x3a};

	/** концевик для последовательности */
	private byte[] endSequence=new byte[]{0x0d, 0x0a};
	
	/** транспорт, на основании которого осуществляется информационный обмен с конечных, внешним устройством */
	private Transport transport;
	
	/** объект, который производит чтение из транспортного объекта */
	private TransportReader transportReader;
	/** объект, который передает функцию и выдает ответы этой функции 
	 * @param transport - транспортный объект, через который будет передаваться информация 
	 */
	public Session(Transport transport) throws Exception{
		this(transport, 200,400);
	}

	/** объект, который передает функцию и выдает ответы этой функции 
	 * @param transport - транспортный объект, через который будет передаваться информация 
	 * @param msWaitBegin - время в милисекундах ожидания начала посылки (:)
	 * @param msWaitEnd - время в милисекундах ожидания конца посылки (0x0d, 0x0a)
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
	
	/** установить поток для вывода ошибочных сообщений */
	public void setErrorPrintStream(PrintStream printStream){
		this.printStreamError=printStream;
	}

	/** установить поток для вывода отладочных сообщений */
	public void setDebugPrintStream(PrintStream printStream){
		this.printStreamDebug=printStream;
	}
	
	/** флаг, который говорит о необходимости вывода отладочных сообщений отладки */
	private boolean flagDebug=false;
	/** флаг, который говорит о необходимости вывода ошибочных сообщений */
	private boolean flagError=false;
	
	/** установить возможность вывода отладочных сообщений, который был установлен {@link #setDebugPrintStream(PrintStream)}*/
	public void setOutputDebug(boolean value){
		this.flagDebug=value;
	}

	/** установить возможность вывода отладочных сообщений, который был установлен {@link #setErrorPrintStream(PrintStream)}*/
	public void setOutputError(boolean value){
		this.flagError=value;
	}

	
	/** 
	 * послать функцию и получить ответ на эту функцию в виде последовательности байт
	 * @param address - адрес устройства (1..247) которому будет адресована посылка 
	 * @param function - функция, которая должа быть послана
	 * @return
	 * <li> <b>true</b> - данные обработаны, ответ можно получить из функции, которая был передана как аргумент </li> 
	 * <li> <b>false</b> - отрицательный ответ обработки данных (прочесть ошибку {@link #getLastError})</li> 
	 */
	public boolean sendFunction(int address, Function function){
		boolean returnValue=false;
		try{
			logger.debug("создать последовательность байт, которая будет послана в OutputStream");
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			
			baos.write((byte)address);
			baos.write((byte)function.getNumber());
			baos.write(function.getData());
			logger.debug("высчитать Longitudinal Redundancy Check – продольный контроль по избыточности"); 
			baos.write(function.getLRC(baos.toByteArray()));
			
			byte[] sequenceForWrite=baos.toByteArray();
			logger.debug("присоединить начальный символ и концевик");
			byte[] forOutput=this.addByteArray(this.beginSequence, 
					   this.convertByteToSequenceOfAscii(sequenceForWrite, 0, sequenceForWrite.length),
					   this.endSequence);
			if(this.flagDebug){
				this.printStreamDebug.println(function+"   ");
				printByteArray(this.printStreamDebug, forOutput);
			}
			byte[] answer=this.transportReader.write(forOutput);
			if(answer!=null){
				logger.debug("ответ получен - декодировать ответ");
				function.decodeAnswer(address, answer);
				return true;
			}else{
				logger.error("удаленный модуль не ответил");
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
	/** получить код последней ошибки после вызова функции {@link sendRequest}*/
	public int getLastError(){
		return this.lastError;
	}
	
	/** используется для отладки
	 * @param out - объект, в который нужно передать данные для вывода   
	 * @param value - массив, который нужно отобразить на System.out
	 */
	private void printByteArray(PrintStream out, byte[] value){
		for(int counter=0;counter<value.length;counter++){
			out.print(Integer.toHexString(value[counter])+"  ");
		}
		out.println();
	}

	/** преобразовать байты значений 123..EF в 0x30, 0x31, 0x32, 0x33... 0x0
	 * @param array - массив из значений, которые следует преобразовать
	 * @param start - стартовый бит 
	 * @param length - кол-во 
	 * @return последовательность для передачи в OutputStream  
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
	
	/** соединить все массивы в один общий */
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

	/** очистить последнюю ошибку для данной сессии */
	public void clearLastError() {
		this.lastError=0;
	}

}
