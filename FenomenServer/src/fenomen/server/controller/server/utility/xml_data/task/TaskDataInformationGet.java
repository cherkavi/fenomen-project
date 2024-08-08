package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;

public class TaskDataInformationGet extends TaskData{

	/** получить настройки для ThreadInformation (от модуля)*/
	public TaskDataInformationGet() {
		super(true, "Получить настройки для ThreadInformation");

		Element heart_beat=this.getDocument().createElement("information");
		this.getTaskElement().appendChild(heart_beat);
		
			Element get=this.getDocument().createElement("get");
			heart_beat.appendChild(get);
	}

}
