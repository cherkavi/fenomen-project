package fenomen.module.web_service.common;

import java.io.Serializable;

/** ������-��������� ��� �������� ������ {@link Checker} c ������������������� ���������� {@link AlarmMessage} */
public class TransportChecker implements Serializable{
	private final static long serialVersionUID=1L;
	/** ��������������� ������ Checker � ���� ������� ���� */
	private byte[] objectAsByteArray;
	
	/** ���������� ������ ����, ������� ������������ �� ���� ��������������� ������ */
	public void setObjectAsByteArray(byte[] array){
		this.objectAsByteArray=array;
	}

	/** �������� ������ ����, ������� ������������ �� ���� ��������������� ������ */
	public byte[] getObjectAsByteArray(){
		return this.objectAsByteArray;
	}
	
	/** ������-��������� ��� �������� ������ {@link Checker} c ������������������� ���������� ({@link AlarmMessage} | {@link InformationMessage}) */
	public TransportChecker(){
	}

}
