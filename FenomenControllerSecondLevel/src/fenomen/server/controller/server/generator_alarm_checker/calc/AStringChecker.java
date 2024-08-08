package fenomen.server.controller.server.generator_alarm_checker.calc;

/** проверяющий для String значений */
public abstract class AStringChecker<T> extends Checker<T>{
	private final static long serialVersionUID=1L;
	
	/** значение с которым нужно проводить сравнение */
	private String controlValue;
	/** флаг, который говорит о необходимости игнорирования Case в строках  */
	private boolean ignoreCase;
	/** номер регистра, который подлежит проверке */
	private int registerAddress;
	
	/** проверяющий для String значений 
	 * @param delayMs - время задержки для следующей итерации 
	 * @param message - сообщение, которое может быть выдано
	 * @param controlValue - контрольное значение, с которым необходимо проводить сравнение 
	 * @param ignoreCase - нужно ли игнорировать Case ("one"=="OnE")
	 */
	public AStringChecker(int absoluteRegisterNumber, int delayMs, String message, String controlValue, boolean ignoreCase) {
		super(delayMs, message);
		this.registerAddress=absoluteRegisterNumber;
		this.controlValue=controlValue;
		this.ignoreCase=ignoreCase;
	}

	/** получить контрольное значение */
	public String getControlValue(){
		return this.controlValue;
	}
	
	/** нужно ли игнорировать case в строках */
	protected boolean isIgnoreCase(){
		return this.ignoreCase;
	}
	
	/**
	 * @param value - значение, которое вернул датчик
	 * @return 
	 * */
	protected abstract boolean isAlarm(String value); 
	
	/** получить сообщение на основании значения */
	protected abstract T getMessage(String value);
	
	@Override
	protected T calculate(int registerNumber, int value) {
		if(this.registerAddress==registerNumber){
			try{
				if(isAlarm(Integer.toString(value))){
					this.setDateEvent();
					return this.getMessage(Integer.toString(value)); 
				}else{
					return null;
				}
			}catch(Exception ex){
				System.err.println("AStringChecker Exception: "+ex.getMessage());
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
