package fenomen.server.controller.server.generator_alarm_checker.calc_implementation;

import java.util.Date;

import fenomen.server.controller.server.generator_alarm_checker.calc.AIntegerChecker;
import fenomen.server.controller.server.generator_alarm_checker.message.AlarmMessage;

/**  проверка на НЕ равенство величин */
public class NEIntegerChecker extends AIntegerChecker<AlarmMessage>{
	private final static long serialVersionUID=1L;

	/**  проверка на НЕ равенство величин
	 * @param registerNumber - номер регистра    
	 * @param delayMs - задержка перед следующей идентификацией события 
	 * @param message - сообщение, которое следует выдавать 
	 * @param controlValue - контрольное значение (значение для сравнения )
	 */
	public NEIntegerChecker(int registerNumber, int delayMs, String message, int controlValue) {
		super(registerNumber, delayMs, message,controlValue);
	}

	@Override
	protected boolean isAlarm(int value) {
		if(controlValue!=value){
			return true;
		}else return false;
	}

	@Override
	protected AlarmMessage getMessage(String value) {
		return new AlarmMessage(this.getRegisterAddress(), new Date(),this.getTextMessage(), value);
	}
	
}
