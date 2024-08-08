package com.cherkashin.vitaliy.modbus.function;


/** функция записи одного регистра */
public class FunctionWriteOneRegister extends Function{
	private byte[] data=null;
	/** функция записи одного регистра 
	 * @param register - номер регистра, в который следует производить запись
	 * @param value - значение регистра
	 */
	public FunctionWriteOneRegister(int register, int value) {
		super((char)0x06, "Set one register");
		this.data=this.addByteArray(this.getTwoByteFromInt(register), 
									this.getTwoByteFromInt(value));
	}

	@Override
	public void decodeAnswer(int address, byte[] array) {
		/** получить "чистые" байты данных, после ASCII преобразований */
		byte[] data=this.getBytesFromAsciiResponse(array);
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
			// проверка на возврат одинакового потока данных
			if(!this.compareArray(this.data, this.getSubArray(data, 2, 6))){
				this.setError(Function.ERROR_DATA);
				break;
			}
			break;
		}
	}

	@Override
	public byte[] getData() {
		return this.data;
	}

}
