package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;

/** заменить AlarmChecker для указанного датчика  и указанного AlarmChecker-а*/
public class TaskDataAlarmCheckerReplace extends TaskData{

	/** получить все AlarmChecker-ы по указанному датчику 
	 * @param addressModbus - адрес в сети ModBus
	 * @param addressRegister - адрес регистра на модуле 
	 * @param idCheckerNumber - уникальный номер Checker-a в контексте датчика 
	 * @param idModuleAlarmChecker - уникальный номер записи в таблице module_alarm_checker  
	 * */
	public TaskDataAlarmCheckerReplace(int addressModbus, 
			   						   int addressRegister, 
			   						   int idCheckerNumber, 
			   						   int idModuleAlarmChecker) {
		super(true, "Заменить AlarmChecker для датчика №"+addressModbus);

		Element alarm_checker=this.getDocument().createElement("alarm_checker");
		this.getTaskElement().appendChild(alarm_checker);
		
			Element id_modbus=this.createElementWithText("id_modbus", addressModbus);
			alarm_checker.appendChild(id_modbus);
	
			Element register=this.createElementWithText("register", addressRegister);
			alarm_checker.appendChild(register);
			
			Element replace=this.getDocument().createElement("replace");
			alarm_checker.appendChild(replace);
			
				Element id_checker=this.createElementWithText("id_checker", idCheckerNumber);
				replace.appendChild(id_checker);
	
				Element id_file=this.createElementWithText("id_file", idModuleAlarmChecker);
				replace.appendChild(id_file);
	}

}
