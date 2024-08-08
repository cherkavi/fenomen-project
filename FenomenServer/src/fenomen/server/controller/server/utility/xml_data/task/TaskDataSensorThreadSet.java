package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;

/** установить настройки ThreadSensor - потока, который общается с датчиками */
public class TaskDataSensorThreadSet extends TaskData{

	/** установить настройки ThreadSensor - потока, который общается с датчиками */
	public TaskDataSensorThreadSet(int timeWait) {
		super(true, "Установить настройки для ThreadSensor");

		Element sensor_thread=this.getDocument().createElement("sensor_thread");
		this.getTaskElement().appendChild(sensor_thread);
			
			Element set=this.getDocument().createElement("set");
			sensor_thread.appendChild(set);
			
				Element time_wait=this.createElementWithText("time_wait", timeWait);
				set.appendChild(time_wait);
	}

}
