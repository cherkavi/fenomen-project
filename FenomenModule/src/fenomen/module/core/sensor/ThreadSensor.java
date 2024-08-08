package fenomen.module.core.sensor;

import org.apache.log4j.Logger;

import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.IUpdateSettings;
import fenomen.module.core.settings.ModuleSettings;

/** объект-поток, который "читает" состояние датчиков через указанные интервалы времени, и хранит в себе текущее состояние/значение всех датчиков в системе */
public class ThreadSensor extends Thread implements IUpdateSettings{
	private Logger logger=Logger.getLogger(this.getClass());
	/** флаг, который "говорит" о необходимости обновления параметров */
	private Boolean needUpdateSettings=false;
	/** объект, от которого можно получить текущие настройки модуля */
	private IModuleSettingsAware moduleSettingsAware;
	/** время задержки в мс. перед следующим чтением данных от датчиков */
	private long timeWait=250;
	/** объект-анализатор полученных от датчиков сообщений */
	private SensorProcessor sensorProcessor;
	/** объект, который содержит все датчики в системе */
	private SensorContainer sensorContainer;

	/** объект-поток, который "читает" состояние датчиков через указанные интервалы времени, и хранит в себе текущее состояние/значение всех датчиков в системе
	 * @param moduleSettingsAware - объект, который владеет текущими настройками модуля
	 * @param sensorProcessor - объект, который обрабатывает все 
	 * @param sensorContainer - все датчики в системе ( уже заполненный объект )
	 */
	public ThreadSensor(IModuleSettingsAware moduleSettingsAware, 
						SensorProcessor sensorProcessor, 
						SensorContainer sensorContainer){
		this.moduleSettingsAware=moduleSettingsAware;
		this.sensorProcessor=sensorProcessor;
		this.sensorContainer=sensorContainer;
	}
	
	@Override
	public void run(){
		this.updateSettings();
		try{
			while(true){
				// проверка на необходимость обновления настроек по модулю 
				if(this.needUpdateSettings==true){
					this.updateSettings();
				}
				// проанализировать пакет данных от сенсоров
				this.sensorProcessor.processSensorValues(sensorContainer);
				// заснуть на время перед следующей итерацией
				try{Thread.sleep(this.timeWait);}catch(Exception ex){};
			}
		}catch(Exception ex){
			logger.error("Thread Sensor EXCEPTION: "+ex.getMessage());
		}
	}

	
	/** необходимость в обновлении настроек модуля  */
	private void updateSettings(){
		synchronized(this.needUpdateSettings){
			this.needUpdateSettings=false;
		}
		// обновление параметров модуля 
		ModuleSettings moduleSettings=this.moduleSettingsAware.getModuleSettings();
		// запись настроек из ModuleSettings в текущий объект ThreadSensor
		this.timeWait=moduleSettings.getParameterAsLong(ModuleSettings.sensorTimeWait);
	}
	
	@Override
	public void notifyUpdateSettings() {
		synchronized(needUpdateSettings){
			this.needUpdateSettings=true;
		}
	}
}
