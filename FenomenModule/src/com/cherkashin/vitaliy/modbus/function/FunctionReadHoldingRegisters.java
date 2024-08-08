package com.cherkashin.vitaliy.modbus.function;

/** �������� �� ��������� ������ ������ ��������� 
 * <b> 0x03 </b>
 * */
public class FunctionReadHoldingRegisters extends Function{
	private byte[] data=null;
	
	/** �������� �� ��������� ������ ������ ��������� 
	 * <b> 0x03</b> 
	 * @param startRegisterForRead - ��������� ����� �������� (0..65535)
	 * @param registerCount - ���-�� ���������, ������� ����� �������� 
	 */
	public FunctionReadHoldingRegisters(int startRegisterForRead, int registerCount) {
		super((char)0x03, "Read Holding Registers");
		this.data=this.addByteArray(this.getTwoByteFromInt(startRegisterForRead), this.getTwoByteFromInt(registerCount));
	}

	@Override
	public byte[] getData() {
		return data;
	}

	@Override
	public void decodeAnswer(int address, byte[] array) {
		/** �������� "������" ����� ������, ����� ASCII �������������� */
		byte[] data=this.getBytesFromAsciiResponse(array);
		// this.printByteArray(System.out, data);
		while(true){
			// �������� CRC
			if(this.getLRC(this.getSubArray(data, 0, data.length-1))!=data[data.length-1]){
				this.setError(ERROR_CRC);
				break;
			}
			// �������� ������
			if(address!=data[0]){
				// address is invalid
				this.setError(ERROR_ADDRESS);
				break;
			}
			// �������� ������� 
			if(this.getNumber()!=data[1]){
				this.setError(ERROR_FUNCTION);
				break;
			}
			// ������� �������� ������ 
			try{
				int dataCount=data[2]/2;
				this.returnValue=new int[dataCount];
				// System.out.println(dataCount+"  : ");
				for(int counter=0;counter<this.returnValue.length;counter++){
					this.returnValue[counter]=(((byte)data[counter*2+3])<<8)+( (byte)data[counter*2+4]) ;
					if(this.returnValue[counter]<0){
						this.returnValue[counter]=this.returnValue[counter]+256;
					}
					// System.out.print(this.returnValue[counter]+"   ");
				}
				// System.out.println();
				this.clearError();
				break;
			}catch(Exception ex){
				this.setError(ERROR_DATA);
				break;
			}
		}
	}
	private int[] returnValue=null;
	
	/** �������� ���-�� ���������, ������� ���� ����������  */
	public int getRecordCount(){
		if(this.returnValue!=null){
			return this.returnValue.length;
		}else{
			return 0;
		}
	}
	
	/** �������� �������� �������� (�������������, �� ���� 0..n, ��� n ���� ������ )*/
	public int getRegister(int index){
		try{
			return this.returnValue[index];
		}catch(Exception ex){
			return 0;
		}
	}
}
