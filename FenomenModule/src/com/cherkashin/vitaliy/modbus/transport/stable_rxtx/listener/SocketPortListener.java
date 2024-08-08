package com.cherkashin.vitaliy.modbus.transport.stable_rxtx.listener;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/** объект, который получает уведомления о принятых с порта данных */
public class SocketPortListener extends Thread{
	private Logger logger=Logger.getLogger(this.getClass());
	private ServerSocket serverSocket;
	private int port;
	private ArrayList<byte[]> queue=new ArrayList<byte[]>();
	private boolean flagRun=false;
	byte[] buffer=new byte[2048];
	private Notifyer notifyer;
	private int limitQueueByte=2048;
	
	public SocketPortListener(int port) throws Exception {
		this.port=port;
		serverSocket=new ServerSocket(port);
		notifyer=new Notifyer(this.queue);
		notifyer.start();
		this.start();
	}
	
	public void stopThread(){
		this.flagRun=false;
		try{
			this.serverSocket.close();
		}catch(Exception ex){};
	}
	
	
	@Override 
	public void run(){
		this.flagRun=true;
		Socket socket=null;
		InputStream is=null;
		while(flagRun){
			try{
				logger.debug("wait for signal on port:"+port);
				socket=serverSocket.accept();
				logger.debug("socket have been connected ");
				// однопоточная система - для многопоточной создать отдельный поток 
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				is=socket.getInputStream();
				int readCount=0;
				while( (readCount=is.read(buffer))>0){
					baos.write(buffer,0,readCount);
					baos.flush();
				}
				is.close();
				byte[] recievedData=baos.toByteArray();
				logger.debug("socket successful readed:"+recievedData.length);
				notifyData(recievedData);
			}catch(Exception ex){
				logger.error("SocketPortListener#run: "+ex.getMessage());
			}finally{
				try{
					is.close();
				}catch(Exception ex){};
				try{
					socket.close();
				}catch(Exception ex){};
			}
		}
	}
	
	public void addDataListener(ISocketPortListener dataListener){
		logger.debug("add data listener");
		this.notifyer.addDataListener(dataListener);
	}
	
	/**  удалить слушателя данных с порта */
	public void removeDataListener(ISocketPortListener dataListener){
		logger.debug("remove data listener");
		this.notifyer.removeDataListener(dataListener);
	}
	
	private void notifyData(byte[] data){
		logger.debug("notify data listener:");
		/*
		 for(ISocketPortListener dataListener:listOfListeners){
			dataListener.notifyDataFromPort(data);
		}*/
		// добавить в буффер, если буффер превышен - очистить
		synchronized(this.queue){
			this.queue.add(data);
			while(this.getQueueSize()>this.limitQueueByte){
				logger.warn("SocketPortListener out of limit");
				this.queue.remove(0);
			}
			this.queue.notify();
		}
	}

	private int getQueueSize(){
		int value=0;
		for(int counter=0;counter<this.queue.size();counter++)value+=this.queue.get(counter).length;
		return value;
	}
}
/** класс, который оповещает слушателей о появлении данных с порта */
class Notifyer extends Thread {
	private ArrayList<ISocketPortListener> listOfListeners=new ArrayList<ISocketPortListener>();
	
	private Logger logger=Logger.getLogger(this.getClass());
	private ArrayList<byte[]> queue;
	public Notifyer(ArrayList<byte[]> queue){
		this.queue=queue;
	}

	/** добавить слушателя данных с порта  */
	public void addDataListener(ISocketPortListener dataListener){
		logger.debug("add data listener");
		this.listOfListeners.add(dataListener);
	}
	
	/**  удалить слушателя данных с порта */
	public void removeDataListener(ISocketPortListener dataListener){
		logger.debug("remove data listener");
		this.listOfListeners.remove(dataListener);
	}
	
	@Override
	public void run(){
		byte[] forSend;
		while(true){
			forSend=null;
			synchronized(this.queue){
				logger.debug("Проверить очередь на наличие данных ");
				if(queue.size()>0){
					logger.debug("получить первый пакет для отправки");
					forSend=this.queue.remove(0);
				}else{
					logger.debug("нет данных - заснуть");
					try{
						queue.wait();
					}catch(Exception ex){};
				}
			}
			if(forSend!=null){
				logger.debug("есть данные для отправки - оповестить всех слушателей:"+forSend.length);
				 for(ISocketPortListener dataListener:listOfListeners){
					 dataListener.notifyDataFromPort(forSend);
				 }
			}
		}
	}
}