package com.cherkashin.vitaliy.modbus.function;

import java.io.ByteArrayOutputStream;

import java.io.PrintStream;

/** абстрактная функция ModBus ASCII 
 * цель потомков - вернуть в методе {@link #getData} данные, между номером функции и контрольной суммой в качестве запроса,
 * и декодировать полученное сообщение в методе {@link #decodeAnswer(int, byte[])}
 * */
public abstract class Function {
	/** данные не получены */
	public static final int ERROR_DATA_NOT_RECIEVE=1;
	/** ответ не от того адресата */
	public static final int ERROR_ADDRESS=2;
	/** модуль отработал функции с ошибками  */
	public static final int ERROR_FUNCTION = 3;
	/** ошибка полученных данных */
	public static final int ERROR_DATA=4;
	/** ошибка контрольной суммы  */
	public static final int ERROR_CRC=5;
	
	
	
	/** номер функции */
	private char number; 
	
	/** текстовое описание функции */
	private String description;
	
	/** абстрактная функция ModBus ASCII 
	 * @param number - номер функции 
	 * @param description - описание функции
	 * <strong>
	 * цель потомков - вернуть в методе {@link #getData} данные, между номером функции и контрольной суммой в качестве запроса,
	 * и декодировать полученное сообщение в методе {@link #decodeAnswer(int, byte[])}
	 * </strong>
	 * */
	public Function(char number, String description){
		this.description=description;
		this.number=number;
		this.setError(ERROR_DATA_NOT_RECIEVE);
	}
	
	@Override
	public String toString(){
		return this.description;
	}
	
	/** получить номер функции  */
	public char getNumber(){
		return this.number;
	}
	
	/** получить данные для передачи в виде последовательности байт  */
	public abstract byte[] getData();
	
	/** получить два байта на основании целого значения [старший, младший]*/
	protected byte[] getTwoByteFromInt(int value){
		return new byte[]{(byte)(value >> 8 & 0xff), (byte)(value & 0xff)};
		/*byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;*/		
	}
	
	/** получить на основании массива int значений массив байт байт, которые представляют один int(FFFF) как два байта (FF and FF)*/
	protected byte[] getTwoByteArrayFromIntArray(int ... values){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		for(int counter=0;counter<values.length;counter++){
			baos.write((byte)(values[counter] >> 8 & 0xff));
			baos.write((byte)(values[counter] >> 0xff));
		}
		return baos.toByteArray();
	}
	
	protected byte[] addByteArray(byte[] ... arrays){
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

	/** ответ, который был получен от удаленного модуля на запрос выполнения функции 
	 * @param address - адрес устройства, которое выдало ответ 
	 * @param array - массив данных, который получен в качестве ответа 
	 */
	public abstract void decodeAnswer(int address, byte[] array);
	
	/** используется для отладки
	 * @param out - объект, в который нужно передать данные для вывода   
	 * @param value - массив, который нужно отобразить на System.out
	 */
	protected void printByteArray(PrintStream out, byte[] value){
		for(int counter=0;counter<value.length;counter++){
			out.print(Integer.toHexString(value[counter])+"  ");
		}
		out.println();
	}

	/** получить подмассив из массива 
	 * @param source - источник, исходный массив 
	 * @param indexBegin - индекс начала( включительно )
	 * @param indexEnd - индекс окончания ( не включительно )
	 * @return - возвращает подмассив из массива <b>[</b> indexBegin,indexEnd <b>)</b>
	 */
	protected byte[] getSubArray(byte[] source, int indexBegin, int indexEnd){
		byte[] returnValue=new byte[indexEnd-indexBegin];
		for(int counter=indexBegin;counter<indexEnd;counter++){
			returnValue[counter-indexBegin]=source[counter];
		}
		return returnValue;
	}

	/** сравнение элементов массива */
	protected boolean compareArray(byte[] first, byte[] second){
		boolean returnValue=false;
		if((first==null)||(second==null)){
			returnValue=false;
		}else{
			if(first.length!=second.length){
				returnValue=false;
			}else{
				returnValue=true;
				for(int counter=0;counter<first.length;counter++){
					if(first[counter]!=second[counter]){
						returnValue=false;
						break;
					}
				}
			}
		}
		return returnValue;
	}
	
	/** подсчитать "Продольный контроль по избыточности"
	 * <strong> Значение LRC вычисляется путем последовательного сложения 8-битовых байтов сообщения 
	 * (не путать с парами ASCII-символов при помощи которых эти байты непосредственно передаются в сети.–прим.перев.) 
	 * с игнорированием переполнения при сложении. Затем производится операция двоичного дополнения полученной суммы 
	 * (арифметическая инверсия числа.–прим.перев.), </strong>
	 * */
	public byte getLRC(byte[] array){
	   /*int checksum = 0;
       for (int i = 0; i < array.length; i++) {
             checksum ^= (array[i] & 0xFF);
       }
       return (byte)checksum; //this.convertByteToSequenceOfAscii(new byte[]{(byte)checksum}, 0, 1);
       */
		byte returnValue=0;
		for(int counter=0;counter<array.length;counter++){
			returnValue+=array[counter];
		}
		int d = 0;
		d = returnValue & 0xFF;
		d = ~d;
		return (byte)(d+1);
	}

	private int error=0;
	
	protected void clearError(){
		this.error=0;
	}
	
	protected void setError(int value){
		this.error=value;
	}
	/** возвращает код ошибки, который произошел при парсинге полученного значения */
	public int getError(){
		return this.error;
	}
	
	/** получить из ASCII последовательности массив байт, между преамбулой (0x3A) и постамбулой (0x0D 0x0A), не включительно */
	protected byte[] getBytesFromAsciiResponse(byte[] array){
		// this.printByteArray(System.out, this.getSubArray(array, 1, array.length-2));
		String value=new String(this.getSubArray(array, 1, array.length-2));
		// System.out.println(value);
		byte[] data=new byte[value.length()/2];
		for(int counter=0;counter<(value.length()/2);counter++){
			data[counter]=(byte)Integer.parseInt(value.substring(counter*2, counter*2+2),16);
		}
		return data;
	}
}
