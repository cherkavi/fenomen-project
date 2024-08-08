package com.cherkashin.vitaliy.modbus.function;


/** функция записи нескольких регистров */
public class FunctionWriteManyRegister extends Function{
	private byte[] sendData=null;

	/** функция записи нескольких регистров 
	 * @param register - номер регистра, в который следует производить запись
	 * @param value - значение регистра
	 */
	public FunctionWriteManyRegister(int register, int ... values) {
		super((char)0x10, "Set many register's");
		this.sendData=this.addByteArray(this.getTwoByteFromInt(register),
									this.getTwoByteFromInt(values.length*2),
									this.getTwoByteArrayFromIntArray(values));
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
			// проверка на возврат значений "адрес регистра", "кол-во регистров"
			if(!this.compareArray(this.getSubArray(this.sendData, 0, 4), 
								  this.getSubArray(data, 2, 6))){
				this.setError(Function.ERROR_DATA);
				break;
			}
			break;
		}
	}

	@Override
	public byte[] getData() {
		return this.sendData;
	}

}
