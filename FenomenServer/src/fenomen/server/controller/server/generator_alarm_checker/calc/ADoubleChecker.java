package fenomen.server.controller.server.generator_alarm_checker.calc;

import java.math.BigDecimal;

/** проверяющий для Double значений */
public abstract class ADoubleChecker<T> extends Checker<T>{
	private final static long serialVersionUID=1L;
	/** контрольное значение */
	private double controlValue;
	/** знак, до которого следует округлять */
	private int digitForRound;
	/** номер регистра, который подлежит проверке */
	private int registerAddress;
	
	/** проверяющий для Double значений 
	 * @param delayMs - время задержки для следующей итерации 
	 * @param message - сообщение, которое может быть выдано
	 * @param controlValue - контрольное значение для сравнения
	 * @param digit - разряд, до которого следует округлять значение (-1 - округление не требуется)
	 * <li><b>-1</b>округление не требуется </li>
	 * <li><b>>=0</b>округлить до указанного знака </li>
	 */
	public ADoubleChecker(int absoluteRegisterNumber, int delayMs, String message,double controlValue, int digit) {
		super(delayMs, message);
		this.registerAddress=absoluteRegisterNumber;
		this.controlValue=controlValue;
		this.digitForRound=digit;
	}

	/** получить контрольное значение, с которым нужно сравнивать величины */
	public double getControlValue(){
		return this.controlValue;
	}
	
	/** получить кол-во знаков, на которые нужно округлять значения */
	protected int getDigit(){
		return this.digitForRound;
	}
	
	/** округлить значение до указанного знака  */
	protected double round(double d, int decimalPlace){
		if(decimalPlace<0){
			return d;
		}else{
		    // see the Javadoc about why we use a String in the constructor
		    // http://java.sun.com/j2se/1.5.0/docs/api/java/math/BigDecimal.html#BigDecimal(double)
		    BigDecimal bd = new BigDecimal(Double.toString(d));
		    bd = bd.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
		    return bd.doubleValue();
		}
	  }	/**
	 * @param value - значение, которое вернул датчик
	 * @return 
	 * */
	protected abstract boolean isAlarm(double value); 
	
	/** сформировать сообщение по указанному значению */
	protected abstract T getMessage(String value);
	
	@Override
	protected T calculate(int absoluteNumber, int value) {
		if(this.registerAddress==absoluteNumber){
			try{
				double doubleValue=value;
				if(isAlarm(doubleValue)){
					this.setDateEvent();
					return this.getMessage(Integer.toString(value)); 
				}else{
					return null;
				}
			}catch(Exception ex){
				System.err.println("ADoubleChecker Exception: "+ex.getMessage());
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
