package com.cherkashin.vitaliy.modbus.transport;

/** ��������� ������� ������ ��� ������ � ������� */
public interface IDataForRead {
	/** ���������� � ���, ��� ���� ������ ��� ������ �� �������  */
	public void notifyData(byte[] data);
}
