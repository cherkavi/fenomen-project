package com.cherkashin.vitaliy.modbus.transport;

import java.io.OutputStream;

import java.util.ArrayList;

/** транспортный объект, который владеет потоками (Input/Output), а так же слушателем наличия данных для получения */
public abstract class Transport {
	private ArrayList<IDataForRead> list=new ArrayList<IDataForRead>();
	
	/** OutputStream в объект с которым осуществляется информационный обмен */
	public abstract OutputStream getOutputStream() throws Exception;
	
	/** добавить слушатель наличия данных для чтения */
	public void addDataForReadListener(IDataForRead listener){
		this.list.add(listener);
	}
	
	/** оповестить слушателей о наличии данных для чтения */
	protected void notifyAllListeners(byte[] data){
		for(int counter=0;counter<list.size();counter++){
			this.list.get(counter).notifyData(data);
		}
	}

	/** удалить объект, который нужно оповещать */
	public void removeDataForReadListener(IDataForRead listener) {
		this.list.remove(listener);
	}
	 
	/** закрыть транспортный объект */
	public abstract void close();
}
