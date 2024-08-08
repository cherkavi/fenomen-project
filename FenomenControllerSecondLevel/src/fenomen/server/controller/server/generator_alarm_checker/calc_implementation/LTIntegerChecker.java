package fenomen.server.controller.server.generator_alarm_checker.calc_implementation;

import java.util.Date;

import fenomen.server.controller.server.generator_alarm_checker.calc.AIntegerChecker;
import fenomen.server.controller.server.generator_alarm_checker.message.AlarmMessage;

/** проверка на меньше */
public class LTIntegerChecker extends AIntegerChecker<AlarmMessage>{
	private final static long serialVersionUID=1L;
	
	/**  проверка на меньше
	 * @param registerNumber - номер регистра     
	 * @param delayMs - задержка перед следующей идентификацией события 
	 * @param message - сообщение, которое следует выдавать 
	 * @param controlValue - контрольное значение (значение для сравнения )
	 */
	public LTIntegerChecker(int registerNumber, int delayMs, String message, int controlValue) {
		super(registerNumber, delayMs, message,controlValue);
	}

	@Override
	protected boolean isAlarm(int value) {
		if(value<controlValue){
			return true;
		}else return false;
	}

	@Override
	protected AlarmMessage getMessage(String value) {
		return new AlarmMessage(this.getRegisterAddress(), new Date(),this.getTextMessage(), value);
	}
	
}
