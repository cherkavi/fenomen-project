package com.cherkashin.vitaliy.modbus.session;

import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.cherkashin.vitaliy.modbus.transport.IDataForRead;

/** объект, который читает данные из предоставленных ему ресурсов, и возвращает ответ */
class TransportReader implements IDataForRead{
	/** */
	/** массив, который сигнализирует о начале посылки */
	private byte[] arrayBegin;
	/** массив, который сигнализирует об окончании посылки */
	private byte[] arrayEnd;
	/** время ожидания начала посылки */
	private int timeForWaitBegin=200;
	/** время ожидания окончания посылки */
	private int timeForWaitEnd=400;
	/** поток, в который следует записывать данные */
	private OutputStream outputStream;
	
	private Logger logger=Logger.getLogger(this.getClass());
	
	/**
	 * @param timeForWaitBegin - время в милисекундах ожидания начала посылки (:)
	 * @param arrayBegin - массив, который сигнализирует о начале посылки 
	 * @param timeForWaitEnd - время в милисекундах ожидания конца посылки (0x0d, 0x0a)
	 * @param arrayEnd - массив, который сигнализирует об окончании посылки 
	 * 
	 */
	public TransportReader(OutputStream outputStream, int timeForWaitBegin, byte[] arrayBegin, int timeForWaitEnd, byte[] arrayEnd){
		this.outputStream=outputStream;
		this.timeForWaitBegin=timeForWaitBegin;
		this.timeForWaitEnd=timeForWaitEnd;
		this.arrayBegin=arrayBegin;
		this.arrayEnd=arrayEnd;
	}
	
	/** объект, который будет общим для всех потоков */
	private Object sharedObject=new Object();
	
	/** флаг поиска начала последовательности */
	private boolean flagFindBeginMarker=false;
	
	/** флаг поиска окончания последовательности */
	private boolean flagFindEndMarker=false;
	
	/** записать данные в буфер и получить ответ
	 * @param array - данные, которые нужно записать в порт 
	 * <li> <b>byte[]</b> - ответ от удаленного устройства, который был полностью прочитан </li>
	 * <li> <b>null</b> - произошла ошибка чтения данных, получить ошибку можно вызвав метод {@link #getLastError()} </li>
	 * */
	public byte[] write(byte[] array) throws Exception{
		byte[] returnValue=null;
		this.currentBuffer=new byte[]{};
		this.flagFindBeginMarker=false;
		this.flagFindEndMarker=false;
		
		logger.debug("записать в OutputStream");
		this.flagFindBeginMarker=true;
		this.outputStream.write(array);
		this.outputStream.flush();
		int positionMarkerBegin=(-1);
		logger.debug("ожидание начала цикла данных");
		synchronized(this.sharedObject){
			if(this.currentBuffer.length>0){
				positionMarkerBegin=this.getIndexSourceInDestination(this.currentBuffer, this.arrayBegin);
				if(positionMarkerBegin>=0){
					logger.debug("маркер начала найден в прочитанных данных");
					this.flagFindBeginMarker=false;
				}else{
					logger.debug("нет маркера в прочитанных данных, ожидание");
					sharedObject.wait(this.timeForWaitBegin);
				}
			}else{
				logger.debug("данные не прочитаны, ожидание");
				// Thread.sleep(this.timeForWaitBegin);
				sharedObject.wait(this.timeForWaitBegin);
			}
		}
		this.flagFindBeginMarker=false;
		logger.debug("проверить, прочитан ли маркер начала посылки ");
		if(positionMarkerBegin<0){
			positionMarkerBegin=this.getIndexSourceInDestination(this.currentBuffer, arrayBegin);
		}
		if(positionMarkerBegin>=0){
			logger.debug("маркер начала посылки прочитан, поиск маркера окончания ");
			this.flagFindEndMarker=true;
			int positionMarkerEnd=(-1);
			synchronized(this.sharedObject){
				positionMarkerEnd=this.getIndexSourceInDestination(this.currentBuffer, arrayEnd);
				if(positionMarkerEnd>=0){
					logger.debug("маркер окончания найден в прочитанных данных"); 
					this.flagFindEndMarker=false;
				}else{
					logger.debug("нет маркера в прочитанных данных, ожидание");
					sharedObject.wait(this.timeForWaitEnd);
				}
			}
			this.flagFindEndMarker=false;
			if(positionMarkerEnd<0){
				positionMarkerEnd=this.getIndexSourceInDestination(this.currentBuffer, arrayEnd);
			}
			if(positionMarkerEnd>=0){
				logger.debug("данные обнаружены");
				returnValue=this.getSubArray(this.currentBuffer, positionMarkerBegin, positionMarkerEnd+this.arrayEnd.length);
			}else{
				logger.debug("маркер окончания не найден ");
				returnValue=null;
			}
		}else{
			logger.debug("маркер начала последовательности не найден в прочитанных данных ( если они были )");
			returnValue=null;
		}
		return returnValue;
	}
	
	/** буфер для чтения данных из порта */
	private byte[] currentBuffer;
	
	/** очистить массив */
	@SuppressWarnings("unused")
	private void clearArray(byte[] array){
		for(int counter=0;counter<array.length;counter++)array[counter]=0;
	}
	
	/** получить индекс одного массива в другом 
	 * @param destination - объект, в котором следует производить поиск 
	 * @param source - объект, элементы которого нужно искать 
	 * */
	private int getIndexSourceInDestination(byte[] destination, byte[] source){
		if((destination==null)||(source==null)||(destination.length==0)||(source.length==0)||(source.length>destination.length)){
			return -1;
		}else{
			for(int counter=0;counter<(destination.length-source.length+1);counter++){
				if(destination[counter]==source[0]){
					// попытка найти последовательность
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
	
	/** получить индекс одного массива в другом 
	 * @param destination - объект, в котором следует производить поиск 
	 * @param source - объект, элементы которого нужно искать 
	 * */
	@SuppressWarnings("unused")
	private int getIndexSourceInDestination(byte[] destination, byte[] source, int destinationLimit){
		if((destination==null)||(source==null)||(destination.length==0)||(source.length==0)||(source.length>destination.length)){
			return -1;
		}else{
			for(int counter=0;counter<(destinationLimit-source.length+1);counter++){
				if(destination[counter]==source[0]){
					// попытка найти последовательность
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

	/** получить подмассив из массива 
	 * @param source - источник, исходный массив 
	 * @param indexBegin - индекс начала
	 * @param indexEnd - индекс окончания 
	 * @return - возвращает подмассив из массива 
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
	
	/** сложить массивы */
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

