package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;


/** создать запрос к модулю по получению всех данных о сенсоре: Получить список сенсоров  */
public class TaskDataGetSensorList extends TaskData{

	/** создать запрос к модулю по получению всех данных о сенсоре: Получить список сенсоров  
	 */
	public TaskDataGetSensorList(){
		super(true, "Получить список сенсоров ");

		Element sensorList=this.getDocument().createElement("sensor_list");
		this.getTaskElement().appendChild(sensorList);
		
		Element get=this.getDocument().createElement("get");
		sensorList.appendChild(get);
	}
	
}
