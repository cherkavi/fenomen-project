package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;

/** настройки для ThreadHeartBeat (установить) */
public class TaskDataHeartBeatSet extends TaskData{

	/** настройки для ThreadHeartBeat (установить)
	 * @param timeWait - время очередного выхода на связь <b>/set/time_wait</b>
	 * @param timeError - время ожидания после неудачного соединения <b>/set/time_error</b>
	 */
	public TaskDataHeartBeatSet(int timeWait, int timeError) {
		super(true, "Установить настройки для ThreadHeartBeat");

		Element heart_beat=this.getDocument().createElement("heart_beat");
		this.getTaskElement().appendChild(heart_beat);
		
			Element set=this.getDocument().createElement("set");
			heart_beat.appendChild(set);
		
				Element time_wait=this.getDocument().createElement("time_wait");
				time_wait.setTextContent(Integer.toString(timeWait));
				set.appendChild(time_wait);
				
				Element time_error=this.getDocument().createElement("time_error");
				time_error.setTextContent(Integer.toString(timeError));
				set.appendChild(time_error);
	}

}
