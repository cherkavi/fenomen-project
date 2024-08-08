package com.cherkashin.vitaliy.modbus.function;


/** ������� ������ ������ �������� */
public class FunctionWriteOneRegister extends Function{
	private byte[] data=null;
	/** ������� ������ ������ �������� 
	 * @param register - ����� ��������, � ������� ������� ����������� ������
	 * @param value - �������� ��������
	 */
	public FunctionWriteOneRegister(int register, int value) {
		super((char)0x06, "Set one register");
		this.data=this.addByteArray(this.getTwoByteFromInt(register), 
									this.getTwoByteFromInt(value));
	}

	@Override
	public void decodeAnswer(int address, byte[] array) {
		/** �������� "������" ����� ������, ����� ASCII �������������� */
		byte[] data=this.getBytesFromAsciiResponse(array);
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
			// �������� �� ������� ����������� ������ ������
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
