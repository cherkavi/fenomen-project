package fenomen.module.core.service_information;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import fenomen.module.core.IInformationAware;
import fenomen.module.core.IModuleSettingsAware;
import fenomen.module.core.IUpdateSettings;
import fenomen.module.core.settings.ModuleSettings;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ContainerModuleInformation;
import fenomen.module.web_service.service.IInformation;

/** поток, который посылает на сервер информационные сообщения, полученные от датчиков */
public class ThreadInformation extends Thread implements IUpdateSettings, IModuleInformationListener{
	private Logger logger=Logger.getLogger(this.getClass());
	/** уникальный идентификатор данного модуля */
	private ModuleIdentifier moduleIdentifier;
	/** объект, который генерирует сервис по передаче информации на сервер */
	private IInformationAware informationAware;
	private IInformation serviceInformation;
	/** объект, который генерирует сервис по получению информационного объекта */
	private IModuleSettingsAware moduleSettingsAware;
	/** объект, который содержит необходимые информационные объекты для отправки на севрер */
	private ArrayList<ContainerModuleInformation> listOfInformation=new ArrayList<ContainerModuleInformation>();
	
	/** флаг,который сигнализирует о необходимости обновления настроек для модуля */
	private Boolean needUpdateSettings=false;
	/** объект, который будет сигнализировать о появлении событий для обработки */
	private Object signal=new Object();
	
	/** время задержки перед следующей посылкой*/
	private long timeWait;
	/** время задержки перед очередной попыткой отправки */
	private long timeError;
	/** максимальное кол-во информационных сообщений в очереди, все что больше - уничтожается первый */
	private int maxInformationCount=0;

	/** поток, который посылает на сервер информационные сообщения, полученные от датчиков 
	 * @param informationAware объект, который генерирует сервисы по связи с сервером
	 * @param moduleSettingsAware объект, который "ведает" о настройках модуля 
	 * @param moduleIdentifier объект-идентификатор для модуля
	 * IMPORTANT: требуется вызов метода start()   
	 */
	public ThreadInformation(IInformationAware informationAware,
							 IModuleSettingsAware moduleSettingsAware,
							 ModuleIdentifier moduleIdentifier){
		this.informationAware=informationAware;
		this.informationAware.getInformation();
		this.moduleSettingsAware=moduleSettingsAware;
		this.moduleIdentifier=moduleIdentifier;
	}
	
	@Override
	public void run(){
		this.updateSettings();
		ContainerModuleInformation currentForSend=null;
		while(true){
			if((this.needUpdateSettings.booleanValue()==true)||(this.listOfInformation.size()>0)){
				// проверка на изменение в настройках модуля
				if(this.needUpdateSettings.booleanValue()==true){
					this.updateSettings();
				}
				// проверка на наличие объекта для отправки 
				if(this.listOfInformation.size()>0){
					// получение очередного объекта
					synchronized(this.listOfInformation){
						currentForSend=this.listOfInformation.remove(0);
					}
					logger.debug("run: sendInformation");
					this.sendInformation(currentForSend);
					// wait for next send
					try{Thread.sleep(this.timeWait);}catch(Exception ex){};
				}
			}else{
				synchronized(this.signal){
					if((this.needUpdateSettings.booleanValue()==true)||(this.listOfInformation.size()>0)){
						logger.debug("for work");
						continue;
					}else{
						try{
							logger.debug("no information data for send - wait for work");
							this.signal.wait();
						}catch(InterruptedException ex){};
					}
				}
			}
		}
	}

	/** обновить возможные настройки модуля, которые касаются именно временных интервалов */
	private void updateSettings(){
		synchronized(this.needUpdateSettings){
			ModuleSettings moduleSettings=this.moduleSettingsAware.getModuleSettings();
			this.timeWait=moduleSettings.getParameterAsLong(ModuleSettings.informationTimeWait, 5*1000);
			this.timeError=moduleSettings.getParameterAsLong(ModuleSettings.informationTimeError, 60*1000);
			this.maxInformationCount=(int)moduleSettings.getParameterAsLong(ModuleSettings.informationMaxInformationCount,100);
			if(this.maxInformationCount<0){this.maxInformationCount=0;};
			this.needUpdateSettings=false;
		}
	}
	
	/** отправить информационное сообщение на сервер
	 * @param moduleInformation - объект, который должен быть отправлен на сервер 
	 */
	private void sendInformation(ContainerModuleInformation moduleInformation){
		while(true){
			try{
				// проверка на разрушение/очистку сервисного объекта 
				if(this.serviceInformation==null){
					this.serviceInformation=this.informationAware.getInformation();
				}
				// передача данных на сервер
				String returnValue=this.serviceInformation.sendInformation(this.moduleIdentifier, moduleInformation);
				if((returnValue==null)||(returnValue.equals(IInformation.returnError))){
					logger.debug("sendInformation: Data sended with error - repeat, after wait");
					try{Thread.sleep(this.timeError);}catch(Exception ex){};
					continue;
				}else if(returnValue.equals(IInformation.returnOk)){
					logger.debug("sendInformation: Data was sended ");
					break;
				}else {
					logger.error("sendInformation: Unknown server response:"+returnValue);
					break;
				}
			}catch(Exception ex){
				logger.warn("sendInformation Exception:"+ex.getMessage());
			}
		}
	}
	
	@Override
	public void notifyUpdateSettings() {
		synchronized(needUpdateSettings){
			this.needUpdateSettings=true;
		}
		// оповещение потока о необходимости инициализации рабочего цикла 
		synchronized(this.signal){
			this.signal.notify();
		}
	}

	@Override
	public void notifyInformation(ContainerModuleInformation moduleInformation) {
		synchronized(this.listOfInformation){
			this.listOfInformation.add(moduleInformation);
			while(this.maxInformationCount<this.listOfInformation.size()){
				this.listOfInformation.remove(0);
			}
		}
		// оповещение потока о необходимости инициализации рабочего цикла 
		synchronized(this.signal){
			this.signal.notify();
		}
	}
	

	/** передать на обработку существующую очередь из информационных сообщений 
	 * @param objectForProcess - объект, который будет обрабатывать очередь событий (в синхронизированном режиме )
	 */
	public void processInformation(IProcessInformation objectForProcess){
		synchronized(this.listOfInformation){
			objectForProcess.processInformation(this.listOfInformation);
		}
		// оповещение потока о необходимости инициализации рабочего цикла 
		synchronized(this.signal){
			this.signal.notify();
		}
	}
}
