package fenomen.module.core.settings;

import java.util.ArrayList;

/** настройки модуля, которые содержат необходимые переменные */
public class ModuleSettings {
	/** для потока отправки "сердцебиения" - временная задержка перед последующей отправкой */
	public static final String heartBeatTimeWait="heartBeatTimeWait";
	/** для потока отправки "сердцебиения" - время повторной попытки после сбоя передачи */
	public static final String heartBeatTimeError="heartBeatTimeError";
	
	/** для потока отправки информационных сообщений - временная задержка перед последующей отправкой */
	public static final String informationTimeWait = "informationTimeWait";
	/** для потока отправки информационных сообщений - время повторной попытки после сбоя передачи */
	public static final String informationTimeError="informationTimeError";
	/** максимальное кол-во событий, которые могут находится в очереди для передачи на сервер, все что больше - уничтожаются старые */
	public static final String informationMaxInformationCount = "informationMaxCount";
	
	/** для потока отправки информационных сообщений - временная задержка перед последующей отправкой */
	public static final String alarmTimeWait = "alarmTimeWait";
	/** для потока отправки информационных сообщений - время повторной попытки после сбоя передачи */
	public static final String alarmTimeError="alarmTimeError";
	/** максимальное кол-во событий, которые могут находится в очереди для передачи на сервер, все что больше - уничтожаются старые */
	public static final String alarmMaxInformationCount = "alarmMaxCount";

	/** для потока получения заданий от сервера - временная задержка перед следующим запросом */
	public static final String taskTimeWait = "taskTimeWait";
	/** для потока получения заданий от сервера - следующая попытка связи после ошибки связи с сервером*/
	public static final String taskTimeError="taskTimeError";

	/** для потока работы с датчиками - временная задержка для обновления очередных параметров от сенсоров */
	public static final String sensorTimeWait="sensorTimeWait";
	
	/** максимально-возможный адрес в сети Modbus для сканирования возможного модуля */
	public static final String maxModbusAddress="maxModbusAddress";
	
	/** имена параметров */
	private ArrayList<String> parameterName=new ArrayList<String>();
	/** соответствующие именам значения */
	private ArrayList<String> parameterValue=new ArrayList<String>();
	/** хранилище */
	private IStore store;
	
	/** настройки модуля, которые содержат необходимые переменные */
	public ModuleSettings(IStore store){
		// загрузить настройки модуля из внешней памяти ( диска/базы данных/сетевого соединения )
		this.store=store;
		this.store.load(parameterName, parameterValue);
		// проверить на наличие параметров, и назначить параметры по умолчанию, в случае не нахождения таковых
		boolean noNeedToSave=true;
		noNeedToSave&=this.checkParameter(heartBeatTimeWait, "30000"); 
		noNeedToSave&=this.checkParameter(heartBeatTimeError,"30000");
		noNeedToSave&=this.checkParameter(informationTimeWait,"5000");
		noNeedToSave&=this.checkParameter(informationTimeError,"30000");
		noNeedToSave&=this.checkParameter(informationMaxInformationCount,"5");
		noNeedToSave&=this.checkParameter(alarmTimeWait,"5000");
		noNeedToSave&=this.checkParameter(alarmTimeError,"30000");
		noNeedToSave&=this.checkParameter(alarmMaxInformationCount,"5");
		noNeedToSave&=this.checkParameter(taskTimeWait,"5000");
		noNeedToSave&=this.checkParameter(taskTimeError,"30000");
		noNeedToSave&=this.checkParameter(sensorTimeWait,"1000");
		noNeedToSave&=this.checkParameter(maxModbusAddress,"20");
		if(noNeedToSave==false){
			store.save(parameterName, parameterValue);
		}
	}
	
	/** проверить параметр по имени и установить его в случае не нахождения 
	 * @param parameterName - имя параметра 
	 * @param valueIfNotExists - значение параметра, если таковой не найден
	 * @return 
	 * <li><b>true</b> - параметр присутствует</li>
	 * <li><b>false</b> - параметр отсутствует </li> 
	 */
	private boolean checkParameter(String parameterName, String valueIfNotExists){
		boolean returnValue=true;
		if(this.parameterName.indexOf(parameterName)<0){
			this.parameterName.add(parameterName);
			this.parameterValue.add(valueIfNotExists);
			returnValue=false;
		}
		return returnValue;
	}
	
	/** установить параметр 
	 * @param paramName - имя параметра
	 * @param paramValue - значение параметра
	 */
	public void setParameter(String paramName, String paramValue){
		this.setParameter(paramName, paramValue, true);
	}

	/** установить параметр 
	 * @param paramName - имя параметра 
	 * @param paramValue - значение параметра
	 * @param storeSave - необходимость сохранения объекта польностью в хранилище 
	 */
	public void setParameter(String paramName, String paramValue, boolean storeSave){
		int index=this.parameterName.indexOf(paramName);
		if(index>=0){
			 // данный параметр уже задан - заменить
			this.parameterValue.set(index, paramValue);
		}else{
			// добавить параметр
			this.parameterName.add(paramName);
			this.parameterValue.add(paramValue);
		}
		if(storeSave==true){
			this.store.save(parameterName, parameterValue);
		}
	}
	
	public boolean saveSettingsInStore(){
		return this.store.save(parameterName, parameterValue);
	}
	
	
	/** получить параметр по имени */
	public String getParameter(String paramName){
		int index=this.parameterName.indexOf(paramName);
		if(index>=0){
			return this.parameterValue.get(index);
		}else{
			return null;
		}
	}
	
	/** получить параметр в виде целого числа 
	 * @param paramName -имя параметра 
	 * @param defaultValue - значение, в случае не нахождения параметра 
	 * @return
	 */
	public long getParameterAsLong(String paramName, long defaultValue){
		long returnValue=defaultValue;
		try{
			returnValue=Long.parseLong(this.getParameter(paramName));
		}catch(Exception ex){
			returnValue=defaultValue;
		}
		return returnValue;
	}

	/** получить параметр в виде целого числа 
	 * @param paramName - имя параметра 
	 * @return возвращаемое значение 
	 */
	public long getParameterAsLong(String paramName){
		long defaultValue=0;
		long returnValue=defaultValue;
		try{
			returnValue=Long.parseLong(this.getParameter(paramName));
		}catch(Exception ex){
			returnValue=defaultValue;
		}
		return returnValue;
	}

}
