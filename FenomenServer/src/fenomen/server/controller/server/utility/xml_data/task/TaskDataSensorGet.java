package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;


/** создать запрос к модулю по получению одного датчика */
public class TaskDataSensorGet extends TaskData{

	/** создать запрос к модулю по получению одного датчика
	 * @param number - порядковый номер сенсора в модуле   
	 */
	public TaskDataSensorGet(int number ){
		super(true, "Получить датчик №"+number);

		Element sensor=this.getDocument().createElement("sensor_get");
		this.getTaskElement().appendChild(sensor);
		
			Element id_modbus=this.createElementWithText("id_modbus", number);
			sensor.appendChild(id_modbus);
	}
	
}
