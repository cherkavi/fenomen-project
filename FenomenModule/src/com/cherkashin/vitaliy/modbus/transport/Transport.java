package com.cherkashin.vitaliy.modbus.transport;

import java.io.OutputStream;

import java.util.ArrayList;

/** ������������ ������, ������� ������� �������� (Input/Output), � ��� �� ���������� ������� ������ ��� ��������� */
public abstract class Transport {
	private ArrayList<IDataForRead> list=new ArrayList<IDataForRead>();
	
	/** OutputStream � ������ � ������� �������������� �������������� ����� */
	public abstract OutputStream getOutputStream() throws Exception;
	
	/** �������� ��������� ������� ������ ��� ������ */
	public void addDataForReadListener(IDataForRead listener){
		this.list.add(listener);
	}
	
	/** ���������� ���������� � ������� ������ ��� ������ */
	protected void notifyAllListeners(byte[] data){
		for(int counter=0;counter<list.size();counter++){
			this.list.get(counter).notifyData(data);
		}
	}

	/** ������� ������, ������� ����� ��������� */
	public void removeDataForReadListener(IDataForRead listener) {
		this.list.remove(listener);
	}
	 
	/** ������� ������������ ������ */
	public abstract void close();
}
