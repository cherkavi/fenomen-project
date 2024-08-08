package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;

/** заменить InformationChecker для указанного датчика  и указанного InformationChecker-а*/
public class TaskDataInformationCheckerReplace extends TaskData{

	/** получить все InformationChecker-ы по указанному датчику 
	 * @param addressModbus - уникальный адрес датчика/модуля в сети Modbus
	 * @param register - номер регистра на модуле/датчике
	 * @param idCheckerNumber - уникальный номер Checker-a в контексте датчика 
	 * @param idModuleInformationChecker - уникальный номер записи в таблице module_information_checker  
	 * */
	public TaskDataInformationCheckerReplace(int addressModbus,
											 int register,
											 int idCheckerNumber, 
											 int idModuleInformationChecker) {
		super(true, "Заменить InformationChecker для датчика №"+addressModbus+"  регистр: "+ register+" checkerNumber:"+idCheckerNumber);

		Element information_checker=this.getDocument().createElement("information_checker");
		this.getTaskElement().appendChild(information_checker);
		
			Element id_modbus=this.createElementWithText("id_modbus", addressModbus);
			information_checker.appendChild(id_modbus);
			
			Element id_register=this.createElementWithText("register", register);
			information_checker.appendChild(id_register);

			Element replace=this.getDocument().createElement("replace");
			information_checker.appendChild(replace);
			
				Element id_checker=this.createElementWithText("id_checker", idCheckerNumber);
				replace.appendChild(id_checker);
		
					Element id_file=this.createElementWithText("id_file", idModuleInformationChecker);
					replace.appendChild(id_file);
	}

}
