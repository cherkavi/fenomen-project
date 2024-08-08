package fenomen.module.web_service.common;

import java.io.Serializable;

/** объект-контейнер для передачи одного {@link Checker} c параметризированным параметром {@link AlarmMessage} */
public class TransportChecker implements Serializable{
	private final static long serialVersionUID=1L;
	/** сериализованный объект Checker в виде массива байт */
	private byte[] objectAsByteArray;
	
	/** установить массив байт, который представляет из себя сериализованный объект */
	public void setObjectAsByteArray(byte[] array){
		this.objectAsByteArray=array;
	}

	/** получить массив байт, который представляет из себя сериализованный объект */
	public byte[] getObjectAsByteArray(){
		return this.objectAsByteArray;
	}
	
	/** объект-контейнер для передачи одного {@link Checker} c параметризированным параметром ({@link AlarmMessage} | {@link InformationMessage}) */
	public TransportChecker(){
	}

}
