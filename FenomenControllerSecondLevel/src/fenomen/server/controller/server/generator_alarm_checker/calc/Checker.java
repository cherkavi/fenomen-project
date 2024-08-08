package fenomen.server.controller.server.generator_alarm_checker.calc;

import java.io.Serializable;
import java.util.Date;
import fenomen.server.controller.server.generator_alarm_checker.message.AlarmMessage;

/** абстрактный класс для вычислений (для проверок текстовых значений)*/
public abstract class Checker<T> implements Serializable{
	private final static long serialVersionUID=1L;
	
	/** время, после наступления которого поток проснется и начнет реагировать на запросы */
	private Date nextEnabledTime=null;
	/** время задержки в милисекундах */
	private int delayMiliseconds=0;
	/** сообщение, которое стоит выдать */
	private String message;
	/** уникальный идентификатор файла  */
	private int idFile;
	/** описание */
	private String description;
	
	/** абстрактный класс для вычислений 
	 * @param delayMiliseconds - время задержки, после наступления события от датчика на которое "заснет" Checker и будет генерировать события только после окончания данного времени 
	 * @param message - строка, которая будет выдана в {@link AlarmMessage#getMessage()}
	 */
	public Checker(int delayMiliseconds, String message){
		this.delayMiliseconds=delayMiliseconds;
		this.message=message;
	}

	/**  получить на основании переданного значения сообщение (AlarmMessage или InormationMessage)
	 * @param number - уникальный номер регистра на устройстве
	 * @param value - значение регистра   
	 * @param currentDate дата считывания данного значения с датчика
	 * @return <li> <b>null</b> нет по данному регистру срабатывания события </li> <li> <b>value</b> есть по данному регистру событие  </li> 
	 */
	public T checkForMessage(int number, int value, Date currentDate){
		// проверка на установку таймера задержки для события 
		if(nextEnabledTime!=null){
			if(currentDate.after(this.nextEnabledTime)){
				// сбросить время ожидания
				this.clearDateEvent();
				// провести очередную проверку 
				return calculate(number, value);
			}else{
				// время ожидания еще не вышло  
				return null;
			}
		}else{
			return calculate(number, value);
		}
	}
	
	/** на основании переданного параметра вернуть AlarmMessage, либо же вернуть null 
	 * @param number - абсолютный номер регистра
	 * @param value - абсолютное значение регистра
	 * */
	protected abstract T calculate(int number, int value);
	
	
	/** очистить таймер, который указывает на совершенное событие */
	protected void clearDateEvent(){
		this.nextEnabledTime=null;
	}

	/** описание события */
	public String getTextMessage(){
		return this.message;
	}
	
	/** время в мс. по прошествии которого тревожное сообщение может быть объявлено */
	public int getTimeDelay(){
		return this.delayMiliseconds;
	}
	
	
	/** установить следующую реакцию на событие после наступления времени, указанного в {@link #delayMiliseconds}*/
	protected void setDateEvent(){
		this.nextEnabledTime=new Date((new Date()).getTime()+delayMiliseconds);
	}

	/** получить уникальный идентификатор записи module_alarm_checker.id или же module_information_checker.id, в зависимости от параметризированного параметра */
	public int getIdFile() {
		return idFile;
	}

	/** установить уникальный идентификатор записи module_alarm_checker.id или же module_information_checker.id, в зависимости от параметризированного параметра */
	public void setIdFile(int idFile) {
		this.idFile = idFile;
	}

	/** получить описание данного */
	public String getDescription() {
		return description;
	}

	/** установить описание */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		return this.idFile;
	} 

	/** получить абстрактный номер регистра, если таковой имеет смысл 
	 * (другими возвращает адрес регистра, если Checker может быть "привязан" к таковому адресу )*/
	public abstract int getRegisterAddress();
}
