package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;

import fenomen.server.controller.server.utility.XmlData;

/** обертка для всех Task в системе */
public class TaskData extends XmlData{
	/** описание данной задачи */
	private String description;
	/** корневой элемент данной задачи */
	private Element rootElement;
	private Element taskElement;
	/** является ли данный объект от сервера на модуль (true) или от модуля на сервер (false)*/
	private boolean isRequestTask;
	
	/** обертка для всех Task в системе 
	 * @param isRequestTask
	 * <table border=1>
	 * 	<tr>
	 * 		<th>Задача</th><th>Описание</th> 
	 * 	</tr>
	 * 	<tr>
	 * 		<td>task_request = true</td><td> - от сервера к модулю </td> 
	 * 	</tr>
	 * 	<tr>
	 * 		<td>task_response = false</td><td> - от модуля к серверу </td> 
	 * 	</tr>
	 * </table>
	 */
	public TaskData(boolean isRequestTask){
		this(isRequestTask, "");
	}
	
	/** обертка для всех Task в системе 
	 * @param isRequestTask
	 * <table border=1>
	 * 	<tr>
	 * 		<th>Задача</th><th>Описание</th> 
	 * 	</tr>
	 * 	<tr>
	 * 		<td>task_request = true</td><td> - от сервера к модулю </td> 
	 * 	</tr>
	 * 	<tr>
	 * 		<td>task_response = false</td><td> - от модуля к серверу </td> 
	 * 	</tr>
	 * </table>
	 * @param description - описание задачи, комментарий для базы данных 
	 */
	public TaskData(boolean isRequestTask, String description){
		super("t_",".txml");
		this.isRequestTask=isRequestTask;
		if(isRequestTask==true){
			this.rootElement=this.getDocument().createElement("task_request");
			this.getDocument().appendChild(this.rootElement);
		}else{
			this.rootElement=this.getDocument().createElement("task_response");
			this.getDocument().appendChild(this.rootElement);
		}
		taskElement=this.getDocument().createElement("task");
		this.rootElement.appendChild(taskElement);
		this.description=description;
	}
	
	/** получить описание данного Task */
	public String getDescription(){
		return this.description;
	}
	
	/**
	 * @return 
	 * <table border=1>
	 * 	<tr>
	 * 		<th>Задача</th><th>Описание</th> 
	 * 	</tr>
	 * 	<tr>
	 * 		<td>task_request = true</td><td> - от сервера к модулю </td> 
	 * 	</tr>
	 * 	<tr>
	 * 		<td>task_response = false</td><td> - от модуля к серверу </td> 
	 * 	</tr>
	 * </table>
	 */
	public boolean isRequestTask(){
		return this.isRequestTask;
	}
	
	/** получить task элемент */
	protected Element getTaskElement(){
		return this.taskElement;
	}

	/** создать элемент с названием тэга 
	 * @param elementName - тэг
	 * @return - элемент
	 */
	protected Element createElement(String elementName){
		return this.getDocument().createElement(elementName);
	}
	
	/** создать элемент с названием тэга и текстом  
	 * @param elementName - тэг
	 * @param text - текст, который должен быть внутри элемента 
	 * @return - элемент
	 */
	protected Element createElementWithText(String elementName,String text){
		Element element=this.getDocument().createElement(elementName);
		element.setTextContent(text);
		return element;
	}

	/** создать элемент с названием тэга и текстом  
	 * @param elementName - тэг
	 * @param value - цифровое значение  
	 * @return - элемент
	 */
	protected Element createElementWithText(String elementName,int value){
		Element element=this.getDocument().createElement(elementName);
		element.setTextContent(Integer.toString(value));
		return element;
	}
	
}

