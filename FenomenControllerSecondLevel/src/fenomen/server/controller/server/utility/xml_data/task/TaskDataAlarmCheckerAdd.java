package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;

/** добавить AlarmChecker для указанного датчика  */
public class TaskDataAlarmCheckerAdd extends TaskData{

	/** получить все AlarmChecker-ы по указанному датчику 
	 * @param addressModbus - адрес в сети ModBus
	 * @param addressRegister - адрес регистра на модуле 
	 * @param idModuleAlarmChecker - уникальный номер записи в таблице module_alarm_checker  
	 * */
	public TaskDataAlarmCheckerAdd(int addressModbus, 
								   int addressRegister,
								   int idModuleAlarmChecker) {
		super(true, "Добавить AlarmChecker для датчика №"+addressModbus);

		Element alarm_checker=this.getDocument().createElement("alarm_checker");
		this.getTaskElement().appendChild(alarm_checker);
		
			Element id_sensor=this.createElementWithText("id_modbus", addressModbus);
			alarm_checker.appendChild(id_sensor);
	
			Element register=this.createElementWithText("register", addressModbus);
			alarm_checker.appendChild(register);
			
			Element add=this.getDocument().createElement("add");
			alarm_checker.appendChild(add);
			
				Element id_file=this.createElementWithText("id_file", idModuleAlarmChecker);
				add.appendChild(id_file);
	}

}
