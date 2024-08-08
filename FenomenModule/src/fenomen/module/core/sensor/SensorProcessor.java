package fenomen.module.core.sensor;

import java.lang.reflect.Array;

import fenomen.module.core.service_alarm.IModuleAlarmListener;
import fenomen.module.core.service_information.IModuleInformationListener;
import fenomen.module.web_service.common.ContainerModuleAlarm;
import fenomen.module.web_service.common.ContainerModuleInformation;
import fenomen.module.web_service.common.ModuleAlarm;
import fenomen.module.web_service.common.ModuleInformation;

/** объект, который обрабатывает данные, полученные от датчиков, и оповещает о всевозможных событиях другие потоки */
public class SensorProcessor {
	/** объект, который нужно оповещать о новых Alarm событиях */
	private IModuleAlarmListener moduleAlarmListener;
	
	/** объект, который нужно оповещать о новых Information событиях */
	private IModuleInformationListener moduleInformationListener;
	
	/** объект, который обрабатывает данные, полученные от датчиков, и оповещает о всевозможных событиях другие потоки 
	 * @param moduleAlarmListener - слушатель для Alarm контейнеров 
	 * @param moduleInformationListener - слушатель для Information контейнеров
	 */
	public SensorProcessor(IModuleAlarmListener moduleAlarmListener, IModuleInformationListener moduleInformationListener){
		this.moduleAlarmListener=moduleAlarmListener;
		this.moduleInformationListener=moduleInformationListener;
	}
	
	/** обработать входящие от сенсоров параметры 
	 * @param sensorContainer - контейнер с сенсорами  
	 */
	public void processSensorValues(SensorContainer sensorContainer){
		ContainerModuleAlarm moduleAlarmContainer=new ContainerModuleAlarm();
		ContainerModuleInformation moduleInformationContainer=new ContainerModuleInformation();
		// пройтись по всем модулям ModBus и проверить их на наличие событий Alarm и/или Information - наполнить контейнеры 
		for(int counter=0;counter<sensorContainer.getSize();counter++){
			this.checkSensor(counter, sensorContainer.getSensor(counter), moduleAlarmContainer, moduleInformationContainer);
		}
		if(moduleAlarmContainer.getContent().length>0){
			// модуль не пустой - положить контейнер в поток для отправки
			this.moduleAlarmListener.notifyAlarm(moduleAlarmContainer);
		}
		if(moduleInformationContainer.getContent().length>0){
			// модуль не пустой - положить контейнер в поток для отправки
			this.moduleInformationListener.notifyInformation(moduleInformationContainer);
		}
	}
	
	/** обработать значение в сенсоре, на наличие либо же тревожных сообщений, либо же информационных сообщений
	 * @param sensorIndex - номер данного сенсора в масштабе модуля  
	 * @param sensor - сенсор/датчик, по которому проводится анализ
	 * @param moduleAlarmContainer - контейнер для Alarm
	 * @param moduleInformationContainer - контейнер для Information
	 */
	private void checkSensor(int sensorIndex, 
							 Sensor sensor, 
							 ContainerModuleAlarm moduleAlarmContainer, 
							 ContainerModuleInformation moduleInformationContainer){
		
		// обработать все сенсоры на наличие всех прикрепленных проверок для Alarm
		ModuleAlarm[] moduleAlarmArray=sensor.getModuleAlarm(sensorIndex);
		if((moduleAlarmArray!=null)&&(moduleAlarmArray.length>0)){
			moduleAlarmContainer.setContent(this.addArrays(moduleAlarmContainer.getContent(),moduleAlarmArray));
		}
		// обработать все сенсоры на наличие всех прикрепленных проверок для Information
		ModuleInformation[] moduleInformationArray=sensor.getModuleInformation(sensorIndex);
		if((moduleInformationArray!=null)&&(moduleInformationArray.length>0)){
			moduleInformationContainer.setContent(this.addArrays(moduleInformationContainer.getContent(), moduleInformationArray));
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T[] addArrays(T[] one, T[] two){
		if((one!=null)&&(two!=null)){
			one.getClass();
			T[] returnValue=null;
			if(one.length>0){
				returnValue=(T[])Array.newInstance((one[0]).getClass(), one.length+two.length);
			}else{
				if(two.length>0){
					returnValue=(T[])Array.newInstance((two[0]).getClass(), one.length+two.length);
				}else{
					return null; 
				}
			}
			for(int counter=0;counter<one.length;counter++){
				returnValue[counter]=one[counter];
			}
			for(int counter=0;counter<two.length;counter++){
				returnValue[counter+one.length]=two[counter];
			}
			return returnValue;
		}else{
			if((one==null)&&(two==null)){
				return null;
			}else{
				if(one==null){
					return two;
				}else{
					// only two has null
					return one;
				}
			}
		}
	}
	
	/* public static void main(String[] args){
		Integer[] one=new Integer[]{1,2,3,4};
		Integer[] two=new Integer[]{5,6,7,8};
		Integer[] three=addArrays(one, two);
		for(int counter=0;counter<three.length;counter++){
			System.out.print(counter+":"+three[counter]+"    ");
		}
		System.out.println();
	}*/
}
