package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;

public class TaskDataAlarmGet extends TaskData{

	/** получить настройки для ThreadAlarm (от модуля)*/
	public TaskDataAlarmGet() {
		super(true, "Получить настройки для ThreadAlarm");

		Element alarm=this.getDocument().createElement("alarm");
		this.getTaskElement().appendChild(alarm);
		
		Element get=this.getDocument().createElement("get");
		alarm.appendChild(get);
	}

}
