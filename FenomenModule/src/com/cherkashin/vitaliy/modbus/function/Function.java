package com.cherkashin.vitaliy.modbus.function;

import java.io.ByteArrayOutputStream;

import java.io.PrintStream;

/** ����������� ������� ModBus ASCII 
 * ���� �������� - ������� � ������ {@link #getData} ������, ����� ������� ������� � ����������� ������ � �������� �������,
 * � ������������ ���������� ��������� � ������ {@link #decodeAnswer(int, byte[])}
 * */
public abstract class Function {
	/** ������ �� �������� */
	public static final int ERROR_DATA_NOT_RECIEVE=1;
	/** ����� �� �� ���� �������� */
	public static final int ERROR_ADDRESS=2;
	/** ������ ��������� ������� � ��������  */
	public static final int ERROR_FUNCTION = 3;
	/** ������ ���������� ������ */
	public static final int ERROR_DATA=4;
	/** ������ ����������� �����  */
	public static final int ERROR_CRC=5;
	
	
	
	/** ����� ������� */
	private char number; 
	
	/** ��������� �������� ������� */
	private String description;
	
	/** ����������� ������� ModBus ASCII 
	 * @param number - ����� ������� 
	 * @param description - �������� �������
	 * <strong>
	 * ���� �������� - ������� � ������ {@link #getData} ������, ����� ������� ������� � ����������� ������ � �������� �������,
	 * � ������������ ���������� ��������� � ������ {@link #decodeAnswer(int, byte[])}
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
	
	/** �������� ����� �������  */
	public char getNumber(){
		return this.number;
	}
	
	/** �������� ������ ��� �������� � ���� ������������������ ����  */
	public abstract byte[] getData();
	
	/** �������� ��� ����� �� ��������� ������ �������� [�������, �������]*/
	protected byte[] getTwoByteFromInt(int value){
		return new byte[]{(byte)(value >> 8 & 0xff), (byte)(value & 0xff)};
		/*byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;*/		
	}
	
	/** �������� �� ��������� ������� int �������� ������ ���� ����, ������� ������������ ���� int(FFFF) ��� ��� ����� (FF and FF)*/
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

	/** �����, ������� ��� ������� �� ���������� ������ �� ������ ���������� ������� 
	 * @param address - ����� ����������, ������� ������ ����� 
	 * @param array - ������ ������, ������� ������� � �������� ������ 
	 */
	public abstract void decodeAnswer(int address, byte[] array);
	
	/** ������������ ��� �������
	 * @param out - ������, � ������� ����� �������� ������ ��� ������   
	 * @param value - ������, ������� ����� ���������� �� System.out
	 */
	protected void printByteArray(PrintStream out, byte[] value){
		for(int counter=0;counter<value.length;counter++){
			out.print(Integer.toHexString(value[counter])+"  ");
		}
		out.println();
	}

	/** �������� ��������� �� ������� 
	 * @param source - ��������, �������� ������ 
	 * @param indexBegin - ������ ������( ������������ )
	 * @param indexEnd - ������ ��������� ( �� ������������ )
	 * @return - ���������� ��������� �� ������� <b>[</b> indexBegin,indexEnd <b>)</b>
	 */
	protected byte[] getSubArray(byte[] source, int indexBegin, int indexEnd){
		byte[] returnValue=new byte[indexEnd-indexBegin];
		for(int counter=indexBegin;counter<indexEnd;counter++){
			returnValue[counter-indexBegin]=source[counter];
		}
		return returnValue;
	}

	/** ��������� ��������� ������� */
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
	
	/** ���������� "���������� �������� �� ������������"
	 * <strong> �������� LRC ����������� ����� ����������������� �������� 8-������� ������ ��������� 
	 * (�� ������ � ������ ASCII-�������� ��� ������ ������� ��� ����� ��������������� ���������� � ����.�����.�����.) 
	 * � �������������� ������������ ��� ��������. ����� ������������ �������� ��������� ���������� ���������� ����� 
	 * (�������������� �������� �����.�����.�����.), </strong>
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
	/** ���������� ��� ������, ������� ��������� ��� �������� ����������� �������� */
	public int getError(){
		return this.error;
	}
	
	/** �������� �� ASCII ������������������ ������ ����, ����� ���������� (0x3A) � ����������� (0x0D 0x0A), �� ������������ */
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
