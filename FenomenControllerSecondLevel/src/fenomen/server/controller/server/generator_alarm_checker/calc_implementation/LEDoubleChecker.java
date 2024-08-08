package fenomen.server.controller.server.generator_alarm_checker.calc_implementation;

import java.util.Date;

import fenomen.server.controller.server.generator_alarm_checker.calc.ADoubleChecker;
import fenomen.server.controller.server.generator_alarm_checker.message.AlarmMessage;

/** проверка на меньше или равно */
public class LEDoubleChecker extends ADoubleChecker<AlarmMessage>{
	private final static long serialVersionUID=1L;
	
	/**  проверка на меньше или равно
	 * @param registerNumber - номер регистра 
	 * @param delayMs - задержка перед следующей идентификацией события 
	 * @param message - сообщение, которое следует выдавать 
	 * @param controlValue - контрольное значение (значение для сравнения )
	 * @param digit - число знаков, на которые нужно делать округление  
	 */
	public LEDoubleChecker(int registerNumber, int delayMs, String message, double controlValue, int digit) {
		super(registerNumber, delayMs, message,controlValue, digit);
	}

	@Override
	protected boolean isAlarm(double value) {
		try{
			if( this.round(value, this.getDigit())<=this.round(this.getControlValue(),this.getDigit())){
				return true;
			}else {
				return false;
			}
		}catch(Exception ex){
			return false;
		}
	}

	@Override
	protected AlarmMessage getMessage(String value) {
		return new AlarmMessage(this.getRegisterAddress(), new Date(),this.getTextMessage(), value);
	}
	
}
