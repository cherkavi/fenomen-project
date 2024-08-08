package fenomen.server.controller.server.generator_alarm_checker.calc;

/** проверяющий для Integer значений */
public abstract class AIntegerChecker<T> extends Checker<T>{
	private final static long serialVersionUID=1L;
	protected int controlValue;
	/** номер регистра, который подлежит проверке */
	private int registerAddress;
	
	/** проверяющий для Integer значений
	 * @param  
	 * @param delayMs - время задержки для следующей итерации 
	 * @param message - сообщение, которое может быть выдано 
	 */
	public AIntegerChecker(int absoluteRegisterNumber, int delayMs, String message, int controlValue) {
		super(delayMs, message);
		this.registerAddress=absoluteRegisterNumber;
		this.controlValue=controlValue;
	}

	public int getControlValue(){
		return this.controlValue;
	}
	
	/**
	 * @param value - значение, которое вернул датчик
	 * @return 
	 * <li> <b>true</b> если по переданному значению объект возвращает зарегестрированное событие</li>
	 * */
	protected abstract boolean isAlarm(int value); 
	
	/** 
	 * @param value значение, которое вернул датчик
	 * */
	protected abstract T getMessage(String value);
	
	@Override
	protected T calculate(int registerNumber, int value) {
		if(this.registerAddress==registerNumber){
			try{
				if(isAlarm(value)){
					this.setDateEvent();
					return this.getMessage(Integer.toString(value)); 
				}else{
					return null;
				}
			}catch(Exception ex){
				System.err.println("AInteger Exception: "+ex.getMessage());
				return null;
			}
		}else{
			return null;
		}
	}

	@Override
	public int getRegisterAddress() {
		return registerAddress;
	}

	
}
