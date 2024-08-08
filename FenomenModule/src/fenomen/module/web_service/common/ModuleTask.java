package fenomen.module.web_service.common;

import java.io.Serializable;

/** ������� ��� ������ */
public class ModuleTask implements Serializable{
	private final static long serialVersionUID=1L;
	/** ���������� ������������� ������ */
	private int id;
	/** ������, ������� ������������ �� ���� XML */
	private String xmlString;
	
	/** ������� ��� ������ */
	public ModuleTask(){
	}
	
	/** ������� ��� ������ 
	 * @param id - ���������� ������������� 
	 * @param xmlString - ������, ������� ������������ �� ���� XML ����
	 */
	public ModuleTask(int id, String xmlString){
		this.id=id;
		this.xmlString=xmlString;
	}

	/** �������� ���������� ����� ������ */
	public int getId() {
		return id;
	}

	/** ���������� ���������� ����� ������ */
	public void setId(int id) {
		this.id = id;
	}

	/** ���������� ������ ���� XML � ���� ������ */
	public String getXmlString() {
		return xmlString;
	}

	/** �������� ���� XML � ���� ������ */
	public void setXmlString(String xmlString) {
		this.xmlString = xmlString;
	}
	
	 
}
