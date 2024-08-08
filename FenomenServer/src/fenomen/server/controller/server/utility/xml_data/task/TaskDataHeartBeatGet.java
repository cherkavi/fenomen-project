package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;

public class TaskDataHeartBeatGet extends TaskData{

	public TaskDataHeartBeatGet() {
		super(true, "Получить настройки для ThreadHeartBeat");

		Element heart_beat=this.getDocument().createElement("heart_beat");
		this.getTaskElement().appendChild(heart_beat);
		
			Element get=this.getDocument().createElement("get");
			heart_beat.appendChild(get);
	}

}
