package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;

/** настройки для ThreadAlarm (установить) */
public class TaskDataAlarmSet extends TaskData{

	/** настройки для ThreadAlarm (установить)
	 * @param timeWait - время очередного выхода на связь
	 * @param timeError - время ожидания после неудачного соединения
	 * @param maxCount - максимальное кол-во сохраняемых событий Information (удаление первого при превышении )  
	 */
	public TaskDataAlarmSet(int timeWait, int timeError, int maxCount) {
		super(true, "Установить настройки для ThreadAlarm");

		Element alarm=this.getDocument().createElement("alarm");
		this.getTaskElement().appendChild(alarm);
		
		Element set=this.getDocument().createElement("set");
		alarm.appendChild(set);
		
		Element time_wait=this.getDocument().createElement("time_wait");
		time_wait.setTextContent(Integer.toString(timeWait));
		set.appendChild(time_wait);
		
		Element time_error=this.getDocument().createElement("time_error");
		time_error.setTextContent(Integer.toString(timeError));
		set.appendChild(time_error);

		Element max_count=this.getDocument().createElement("max_count");
		max_count.setTextContent(Integer.toString(maxCount));
		set.appendChild(max_count);
	}

}
