package fenomen.module.web_service.common;

import java.io.Serializable;

/** задание для модуля */
public class ModuleTask implements Serializable{
	private final static long serialVersionUID=1L;
	/** уникальный идентификатор задачи */
	private int id;
	/** строка, которая представляет из себя XML */
	private String xmlString;
	
	/** задание для модуля */
	public ModuleTask(){
	}
	
	/** задание для модуля 
	 * @param id - уникальный идентификатор 
	 * @param xmlString - строка, которая представляет из себя XML файл
	 */
	public ModuleTask(int id, String xmlString){
		this.id=id;
		this.xmlString=xmlString;
	}

	/** получить уникальный номер задачи */
	public int getId() {
		return id;
	}

	/** установить уникальный номер задачи */
	public void setId(int id) {
		this.id = id;
	}

	/** установить строку файл XML в виде строки */
	public String getXmlString() {
		return xmlString;
	}

	/** получить файл XML в виде строки */
	public void setXmlString(String xmlString) {
		this.xmlString = xmlString;
	}
	
	 
}
