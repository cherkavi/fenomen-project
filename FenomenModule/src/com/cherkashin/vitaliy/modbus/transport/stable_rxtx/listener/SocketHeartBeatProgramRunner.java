package com.cherkashin.vitaliy.modbus.transport.stable_rxtx.listener;

import org.apache.log4j.Logger;

/** ������, ������� ������������ ����������, ������� �������� �� ����, � ���� ����� ���������� ������� �� ����� ������������ - ��������� ��������� ������ */
public class SocketHeartBeatProgramRunner extends Thread implements IWatchDogTimerNotify,ISocketPortListener{
	private Logger logger=Logger.getLogger(this.getClass());
	/** ����� ���������� ������� ���������, ���� �� ������� ������ ������������ */
	private int controlTime;
	/** ������, ������� ����� ������� �� ��������� ������� */
	private String runString;
	/** ����, ������� ������������� � ������������� ������� */
	private boolean flagRun=false;
	private Object sharedResource=new Object();
	private volatile boolean watchDogTimer=false;
	
	/** ������, ������� ������������ ����������, ������� �������� �� ����, � ���� ����� ���������� ������� �� ����� ������������ - ��������� ��������� ������
	 * @param portListener - ����, ������� ����� ������� �� ������� ������
	 * @param controlTime - �����, ����� ������� ������� ��������� ��������� 
	 * @param runString - ������, ������� ������ ���� �������� � �������� ����������� 
	 */
	public SocketHeartBeatProgramRunner(int portListener, int controlTime, String runString) throws Exception {
		this.controlTime=controlTime;
		this.runString=runString;
		
		socketPortListener=new SocketPortListener(portListener);
		socketPortListener.addDataListener(this);
		this.start();
	}
	private SocketPortListener socketPortListener;
	/** ���������� �����  */
	public void stopThread(){
		logger.debug("stop thread");
		this.flagRun=false;
		socketPortListener.stopThread();
		this.interrupt();
	}
	
	public void run(){
		logger.debug("run thread");
		this.flagRun=true;
		while(flagRun){
			try{
				synchronized(sharedResource){
					logger.debug("wait for heart beat signal ");
					watchDogTimer=false;
					sharedResource.wait(this.controlTime);
				}
				if(flagRun==false){
					break;
				}
				if(watchDogTimer==false){
					// run program
					this.runProgram();
				}
			}catch(Exception ex){
				logger.warn("run Exception:"+ex.getMessage());
			};
		}
	}

	private void runProgram(){
		try{
			logger.debug("need to run program, Execute Command:"+this.runString);
			Runtime.getRuntime().exec(this.runString);
		}catch(Exception ex){
			logger.error("ConPortReaderController#runProgram: "+ex.getMessage());
		}
	}

	@Override
	public void notifyWatchDog() {
		logger.debug("watch dog notify");
		synchronized(sharedResource){
			this.watchDogTimer=true;
			this.sharedResource.notify();
		}
	}

	@Override
	public void notifyDataFromPort(byte[] data) {
		logger.debug("data from port readed");
		this.notifyWatchDog();
	}
}
