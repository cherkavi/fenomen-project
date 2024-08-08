package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;

/** получить все InformationChecker-ы по указанному датчику */
public class TaskDataInformationCheckerGetList extends TaskData{

	/** получить все InformationChecker-ы по указанному датчику 
	 * @param modbusAddress - адрес датчика/модуля в сети ModBus 
	 * */
	public TaskDataInformationCheckerGetList(int addressModbus) {
		super(true, "Получить InformationChecker-ы по датчику №"+addressModbus);

		Element information_checker=this.getDocument().createElement("information_checker");
		this.getTaskElement().appendChild(information_checker);
		
			Element modbus=this.createElementWithText("id_modbus", addressModbus);
			information_checker.appendChild(modbus);
			
			Element get_list=this.getDocument().createElement("get_list");
			information_checker.appendChild(get_list);
	}

}
