package com.cherkashin.vitaliy.modbus.function;

/** прочесть на удаленном модуле группу регистров 
 * <b> 0x03 </b>
 * */
public class FunctionReadHoldingRegisters extends Function{
	private byte[] data=null;
	
	/** прочесть на удаленном модуле группу регистров 
	 * <b> 0x03</b> 
	 * @param startRegisterForRead - начальный адрес регистра (0..65535)
	 * @param registerCount - кол-во регистров, которые нужно прочесть 
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
		/** получить "чистые" байты данных, после ASCII преобразований */
		byte[] data=this.getBytesFromAsciiResponse(array);
		// this.printByteArray(System.out, data);
		while(true){
			// проверка CRC
			if(this.getLRC(this.getSubArray(data, 0, data.length-1))!=data[data.length-1]){
				this.setError(ERROR_CRC);
				break;
			}
			// проверка адреса
			if(address!=data[0]){
				// address is invalid
				this.setError(ERROR_ADDRESS);
				break;
			}
			// проверка функции 
			if(this.getNumber()!=data[1]){
				this.setError(ERROR_FUNCTION);
				break;
			}
			// попытка парсинга данных 
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
	
	/** получить кол-во регистров, которые были возвращены  */
	public int getRecordCount(){
		if(this.returnValue!=null){
			return this.returnValue.length;
		}else{
			return 0;
		}
	}
	
	/** получить значение регистра (относительное, то есть 0..n, где n было задано )*/
	public int getRegister(int index){
		try{
			return this.returnValue[index];
		}catch(Exception ex){
			return 0;
		}
	}
}
