package com.cherkashin.vitaliy.modbus.transport.stable_rxtx.listener;

/** ��������� COM-�����  */
public interface ISocketPortListener {
	/** ��������� � ����� ������, ������� ������ �� ���� */
	public void notifyDataFromPort(byte[] data);
}
