package com.cherkashin.vitaliy.modbus.session;

import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.cherkashin.vitaliy.modbus.transport.IDataForRead;

/** ������, ������� ������ ������ �� ��������������� ��� ��������, � ���������� ����� */
class TransportReader implements IDataForRead{
	/** */
	/** ������, ������� ������������� � ������ ������� */
	private byte[] arrayBegin;
	/** ������, ������� ������������� �� ��������� ������� */
	private byte[] arrayEnd;
	/** ����� �������� ������ ������� */
	private int timeForWaitBegin=200;
	/** ����� �������� ��������� ������� */
	private int timeForWaitEnd=400;
	/** �����, � ������� ������� ���������� ������ */
	private OutputStream outputStream;
	
	private Logger logger=Logger.getLogger(this.getClass());
	
	/**
	 * @param timeForWaitBegin - ����� � ������������ �������� ������ ������� (:)
	 * @param arrayBegin - ������, ������� ������������� � ������ ������� 
	 * @param timeForWaitEnd - ����� � ������������ �������� ����� ������� (0x0d, 0x0a)
	 * @param arrayEnd - ������, ������� ������������� �� ��������� ������� 
	 * 
	 */
	public TransportReader(OutputStream outputStream, int timeForWaitBegin, byte[] arrayBegin, int timeForWaitEnd, byte[] arrayEnd){
		this.outputStream=outputStream;
		this.timeForWaitBegin=timeForWaitBegin;
		this.timeForWaitEnd=timeForWaitEnd;
		this.arrayBegin=arrayBegin;
		this.arrayEnd=arrayEnd;
	}
	
	/** ������, ������� ����� ����� ��� ���� ������� */
	private Object sharedObject=new Object();
	
	/** ���� ������ ������ ������������������ */
	private boolean flagFindBeginMarker=false;
	
	/** ���� ������ ��������� ������������������ */
	private boolean flagFindEndMarker=false;
	
	/** �������� ������ � ����� � �������� �����
	 * @param array - ������, ������� ����� �������� � ���� 
	 * <li> <b>byte[]</b> - ����� �� ���������� ����������, ������� ��� ��������� �������� </li>
	 * <li> <b>null</b> - ��������� ������ ������ ������, �������� ������ ����� ������ ����� {@link #getLastError()} </li>
	 * */
	public byte[] write(byte[] array) throws Exception{
		byte[] returnValue=null;
		this.currentBuffer=new byte[]{};
		this.flagFindBeginMarker=false;
		this.flagFindEndMarker=false;
		
		logger.debug("�������� � OutputStream");
		this.flagFindBeginMarker=true;
		this.outputStream.write(array);
		this.outputStream.flush();
		int positionMarkerBegin=(-1);
		logger.debug("�������� ������ ����� ������");
		synchronized(this.sharedObject){
			if(this.currentBuffer.length>0){
				positionMarkerBegin=this.getIndexSourceInDestination(this.currentBuffer, this.arrayBegin);
				if(positionMarkerBegin>=0){
					logger.debug("������ ������ ������ � ����������� ������");
					this.flagFindBeginMarker=false;
				}else{
					logger.debug("��� ������� � ����������� ������, ��������");
					sharedObject.wait(this.timeForWaitBegin);
				}
			}else{
				logger.debug("������ �� ���������, ��������");
				// Thread.sleep(this.timeForWaitBegin);
				sharedObject.wait(this.timeForWaitBegin);
			}
		}
		this.flagFindBeginMarker=false;
		logger.debug("���������, �������� �� ������ ������ ������� ");
		if(positionMarkerBegin<0){
			positionMarkerBegin=this.getIndexSourceInDestination(this.currentBuffer, arrayBegin);
		}
		if(positionMarkerBegin>=0){
			logger.debug("������ ������ ������� ��������, ����� ������� ��������� ");
			this.flagFindEndMarker=true;
			int positionMarkerEnd=(-1);
			synchronized(this.sharedObject){
				positionMarkerEnd=this.getIndexSourceInDestination(this.currentBuffer, arrayEnd);
				if(positionMarkerEnd>=0){
					logger.debug("������ ��������� ������ � ����������� ������"); 
					this.flagFindEndMarker=false;
				}else{
					logger.debug("��� ������� � ����������� ������, ��������");
					sharedObject.wait(this.timeForWaitEnd);
				}
			}
			this.flagFindEndMarker=false;
			if(positionMarkerEnd<0){
				positionMarkerEnd=this.getIndexSourceInDestination(this.currentBuffer, arrayEnd);
			}
			if(positionMarkerEnd>=0){
				logger.debug("������ ����������");
				returnValue=this.getSubArray(this.currentBuffer, positionMarkerBegin, positionMarkerEnd+this.arrayEnd.length);
			}else{
				logger.debug("������ ��������� �� ������ ");
				returnValue=null;
			}
		}else{
			logger.debug("������ ������ ������������������ �� ������ � ����������� ������ ( ���� ��� ���� )");
			returnValue=null;
		}
		return returnValue;
	}
	
	/** ����� ��� ������ ������ �� ����� */
	private byte[] currentBuffer;
	
	/** �������� ������ */
	@SuppressWarnings("unused")
	private void clearArray(byte[] array){
		for(int counter=0;counter<array.length;counter++)array[counter]=0;
	}
	
	/** �������� ������ ������ ������� � ������ 
	 * @param destination - ������, � ������� ������� ����������� ����� 
	 * @param source - ������, �������� �������� ����� ������ 
	 * */
	private int getIndexSourceInDestination(byte[] destination, byte[] source){
		if((destination==null)||(source==null)||(destination.length==0)||(source.length==0)||(source.length>destination.length)){
			return -1;
		}else{
			for(int counter=0;counter<(destination.length-source.length+1);counter++){
				if(destination[counter]==source[0]){
					// ������� ����� ������������������
					boolean returnValue=true;
					for(int index=0;index<source.length;index++){
						if(destination[counter+index]!=source[index]){
							returnValue=false;
							break;
						}
					}
					if(returnValue==true){
						return counter;
					}
				}
			}
			return -1;
		}
	}
	
	/** �������� ������ ������ ������� � ������ 
	 * @param destination - ������, � ������� ������� ����������� ����� 
	 * @param source - ������, �������� �������� ����� ������ 
	 * */
	@SuppressWarnings("unused")
	private int getIndexSourceInDestination(byte[] destination, byte[] source, int destinationLimit){
		if((destination==null)||(source==null)||(destination.length==0)||(source.length==0)||(source.length>destination.length)){
			return -1;
		}else{
			for(int counter=0;counter<(destinationLimit-source.length+1);counter++){
				if(destination[counter]==source[0]){
					// ������� ����� ������������������
					boolean returnValue=true;
					for(int index=0;index<source.length;index++){
						if(destination[counter+index]!=source[index]){
							returnValue=false;
							break;
						}
					}
					if(returnValue==true){
						return counter;
					}
				}
			}
			return -1;
		}
	}

	/** �������� ��������� �� ������� 
	 * @param source - ��������, �������� ������ 
	 * @param indexBegin - ������ ������
	 * @param indexEnd - ������ ��������� 
	 * @return - ���������� ��������� �� ������� 
	 */
	private byte[] getSubArray(byte[] source, int indexBegin, int indexEnd){
		byte[] returnValue=new byte[indexEnd-indexBegin];
		for(int counter=indexBegin;counter<indexEnd;counter++){
			returnValue[counter-indexBegin]=source[counter];
		}
		return returnValue;
	}

	@Override
	public void notifyData(byte[] data) {
		logger.debug("TransportReader notify Data:"+data.length);
		synchronized(this.sharedObject){
			this.currentBuffer=addArray(this.currentBuffer,data);
			if(this.flagFindBeginMarker==true){
				if(this.getIndexSourceInDestination(this.currentBuffer, arrayBegin)>=0){
					logger.debug("marker Begin recieved");					
					this.sharedObject.notify();
				}
			}
			if(this.flagFindEndMarker==true){
				if(this.getIndexSourceInDestination(this.currentBuffer, arrayEnd)>=0){
					logger.debug("marker End recieved");
					this.sharedObject.notify();
				}
			}
		}
	}
	
	/** ������� ������� */
	private byte[] addArray(byte[] ... values){
		int size=0;
		for(int counter=0;counter<values.length;counter++){
			if(values!=null){
				size+=values[counter].length;
			}
		}
		byte[] result=new byte[size];
		
		int index=0;
		for(int counter=0;counter<values.length;counter++){
			if(values[counter]!=null){
				for(int counterInner=0;counterInner<values[counter].length;counterInner++){
					result[index]=values[counter][counterInner];
					index++;
				}
			}
		}
		return result;
	}
}

