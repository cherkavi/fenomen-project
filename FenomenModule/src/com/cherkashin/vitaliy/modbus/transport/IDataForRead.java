package com.cherkashin.vitaliy.modbus.transport;

/** интерфейс наличия данных для чтения в объекте */
public interface IDataForRead {
	/** оповещение о том, что есть данные для чтения из объекта  */
	public void notifyData(byte[] data);
}
