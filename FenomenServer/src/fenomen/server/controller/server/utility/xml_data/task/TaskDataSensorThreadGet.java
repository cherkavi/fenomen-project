package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;

/** получить настройки ThreadSensor - потока, который общается с датчиками */
public class TaskDataSensorThreadGet extends TaskData{

	/** получить настройки ThreadSensor - потока, который общается с датчиками */
	public TaskDataSensorThreadGet() {
		super(true, "Получить настройки для ThreadSensor");

		Element sensor_thread=this.getDocument().createElement("sensor_thread");
		this.getTaskElement().appendChild(sensor_thread);
		
			Element get=this.getDocument().createElement("get");
			sensor_thread.appendChild(get);
	}

}
