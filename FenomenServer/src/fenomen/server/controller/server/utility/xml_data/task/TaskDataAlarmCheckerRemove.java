package fenomen.server.controller.server.utility.xml_data.task;

import org.w3c.dom.Element;

/** удалить AlarmChecker для указанного датчика  и указанного AlarmChecker-а*/
public class TaskDataAlarmCheckerRemove extends TaskData{

	/** получить все AlarmChecker-ы по указанному датчику
	 * @param modbusAddress - уникальный адрес в сети Modbus
	 * @param registerAddress  - адрес в сети Modbus
	 * @param idCheckerNumber - уникальный номер Checker-a в контексте датчика/модуля (modbusAddress) 
	 * */
	public TaskDataAlarmCheckerRemove(int modbusAddress, 
									  int registerAddress, 
									  int idCheckerNumber) {
		super(true, "Удалить AlarmChecker для датчика №"+modbusAddress);

		Element alarm_checker=this.getDocument().createElement("alarm_checker");
		this.getTaskElement().appendChild(alarm_checker);
		
			Element id_modbus=this.createElementWithText("id_modbus", modbusAddress);
			alarm_checker.appendChild(id_modbus);
			
			Element register=this.createElementWithText("register", registerAddress);
			alarm_checker.appendChild(register);

			Element remove=this.getDocument().createElement("remove");
			alarm_checker.appendChild(remove);
			
				Element id_checker=this.createElementWithText("id_checker", idCheckerNumber);
				remove.appendChild(id_checker);

	}

}
