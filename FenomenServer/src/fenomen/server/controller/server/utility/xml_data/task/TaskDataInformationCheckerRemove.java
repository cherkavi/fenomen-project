package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;

/** удалить InformationChecker для указанного датчика  и указанного InformationChecker-а*/
public class TaskDataInformationCheckerRemove extends TaskData{

	/** получить все InformationChecker-ы по указанному датчику 
	 * @param addressModbus - уникальный адрес в сети Modbus
	 * @param register - номер регистра в сети Modbus
	 * @param idCheckerNumber - уникальный номер Checker-a в контексте датчика, который нужно удалить 
	 * */
	public TaskDataInformationCheckerRemove(int addressModbus,
											int register, 
											int idCheckerNumber) {
		super(true, "Удалить InformationChecker для датчика №"+addressModbus+"  Register:"+register+"   idChecker:"+idCheckerNumber);

		Element information_checker=this.getDocument().createElement("information_checker");
		this.getTaskElement().appendChild(information_checker);
		
			Element id_modbus=this.createElementWithText("id_modbus", addressModbus);
			information_checker.appendChild(id_modbus);
	
			Element id_register=this.createElementWithText("register", register);
			information_checker.appendChild(id_register);
			
			Element remove=this.getDocument().createElement("remove");
			information_checker.appendChild(remove);
			
				Element id_checker=this.createElementWithText("id_checker", idCheckerNumber);
				remove.appendChild(id_checker);

	}

}
