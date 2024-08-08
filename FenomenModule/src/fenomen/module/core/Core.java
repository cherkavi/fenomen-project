package fenomen.module.core;

import java.io.File;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;

import com.cherkashin.vitaliy.modbus.core.direct.ModBusNet;
import com.cherkashin.vitaliy.modbus.core.schedule_read.DeviceRegisterBlock;
import com.cherkashin.vitaliy.modbus.transport.Transport;
import com.cherkashin.vitaliy.modbus.transport.stable_rxtx.SerialPortProxy;

import fenomen.module.core.sensor.FileSystemCheckerStorage;
import fenomen.module.core.sensor.Sensor;
import fenomen.module.core.sensor.SensorContainer;
import fenomen.module.core.sensor.SensorProcessor;
import fenomen.module.core.sensor.ThreadSensor;
import fenomen.module.core.service_alarm.ThreadAlarm;
import fenomen.module.core.service_heart_beat.ThreadHeartBeat;
import fenomen.module.core.service_information.ThreadInformation;
import fenomen.module.core.service_task.TaskProcessor;
import fenomen.module.core.service_task.ThreadTask;
import fenomen.module.core.settings.FileStore;
import fenomen.module.core.settings.ModuleSettings;
import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.service.IAlarm;
import fenomen.module.web_service.service.IHeartBeat;
import fenomen.module.web_service.service.IInformation;
import fenomen.module.web_service.service.ITask;
import fenomen.server.controller.server.generator_alarm_checker.calc.Checker;
import fenomen.server.controller.server.generator_alarm_checker.message.AlarmMessage;
import fenomen.server.controller.server.generator_alarm_checker.message.InformationMessage;


/** главный файл модул€, который осуществл€ет сбор данных от датчиков и обмен с сервером  */
public class Core extends Thread implements IHeartBeatAware, IInformationAware, ITaskAware,IAlarmAware, IModuleSettingsAware, IModuleSettingsListener, ISensorContainerAware {
	
	public static void main(String[] args){
		if(args.length==0){
			try{
				(new Core("http://localhost:8080/FenomenServer")).start();
			}catch(Exception ex){
				System.err.println("Core Exception:"+ex.getMessage());
			}
		}else{
			try{
				(new Core(args[0])).start();
			}catch(Exception ex){
				System.err.println("Core Exception:"+ex.getMessage());
			}
		}
	}
	
	private Logger logger=Logger.getLogger(this.getClass());
	private XFireProxyFactory proxyFactory=null;
	private String serviceUrl = null;
	private Service serviceHeartBeat=null;
	private Service serviceTask=null;
	private Service serviceInformation=null;
	private Service serviceAlarm=null;
	/** текущие настройки модул€ */
	private ModuleSettings moduleSettings=null;
	private ArrayList<IUpdateSettings> listOfSettingsNotify=new ArrayList<IUpdateSettings>();
    /** контейнер с датчиками/сенсорами */
	private SensorContainer sensorContainer=null;

	private String getCurrentDir(){
		return System.getProperty("user.dir");
		//File dir=new File(".");
		// return dir.getAbsolutePath();
	}
	
	/** главный файл модул€, который осуществл€ет сбор данных от датчиков и обмен с сервером  */
	public Core(String pathToServer) throws Exception{
        Logger.getRootLogger().setLevel(Level.INFO);
		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} %t %5p %c{1}:%L - %m%n")));
		
		serviceUrl=pathToServer.trim();
		if(serviceUrl.endsWith("/")){
			serviceUrl=serviceUrl+"services/";
		}else{
			serviceUrl=serviceUrl+"/services/";
		}
        XFire xfire = XFireFactory.newInstance().getXFire();
        proxyFactory = new XFireProxyFactory(xfire);
        
        serviceHeartBeat = new ObjectServiceFactory().create(IHeartBeat.class);
        serviceTask = new ObjectServiceFactory().create(ITask.class);
        serviceInformation = new ObjectServiceFactory().create(IInformation.class);
        serviceAlarm=new ObjectServiceFactory().create(IAlarm.class);
        moduleSettings=new ModuleSettings(new FileStore("settings.properties"));
        
        // создать ModBus сеть и загрузить данные
		Properties properties=new Properties();
		File fileProperties=new File("com_port.settings");
		properties.load(new FileInputStream(fileProperties));
		/** данный объект должен быть в отдельном потоке, т.к. может ожидать Socket-ного соединени€, которое будет "тормозить" данный поток */
		Transport transport=new SerialPortProxy(Integer.parseInt(properties.getProperty("tcp_port_input_data")),
											    Integer.parseInt(properties.getProperty("tcp_port_output_data")),
											    Integer.parseInt(properties.getProperty("tcp_port_heart_beat")),
											    Integer.parseInt(properties.getProperty("time_heart_beat")),
											    properties.getProperty("run_command"));
		
		//Transport transport=new ComTransport("COM4",9600,ComTransport.DATABITS_7,ComTransport.STOPBITS_2,ComTransport.PARITY_NONE);
		try{Thread.sleep(Integer.parseInt(properties.getProperty("time_heart_beat")));}catch(InterruptedException innerEx){};
		
		int maxModbusAddressForScan=(int)moduleSettings.getParameterAsLong(ModuleSettings.maxModbusAddress);
		String fileSeparator=System.getProperty("file.separator");
		String rootPath=this.getCurrentDir();
        String infromationRelativePath=fileSeparator+"InformationChecker"+fileSeparator;
        String alarmRelativePath=fileSeparator+"AlarmChecker"+fileSeparator;
    	

		// INFO MODBUS - прочесть все доступные ModbusModules и добавить эти модули в SensorContainer
		sensorContainer=new SensorContainer(new ModBusNet(transport,
														  600, // врем€ ожидани€ стартового пакета
														  600, // врем€ ожидани€ концевика в пакете
														  null,
														  null));
		for(int counter=0;counter<maxModbusAddressForScan;counter++){
			int[] type=sensorContainer.discoverDeviceByModbusNumber(counter);
			if(type!=null){
				this.logger.info("устройство найдено:"+counter);
				/** блоки данных, которые должны обновл€тьс€ в сети */ 
				DeviceRegisterBlock[] blocks=SensorContainer.getRegisterBlock(type);
		        Sensor sensor=new Sensor(counter, 
						 				 Integer.toString(type[0])+Integer.toString(type[1]), 
						 				 new FileSystemCheckerStorage<Checker<InformationMessage>>(rootPath+infromationRelativePath+Integer.toString(counter)+"I"+fileSeparator),
						 				 new FileSystemCheckerStorage<Checker<AlarmMessage>>(rootPath+alarmRelativePath+Integer.toString(counter)+"A"+fileSeparator),
						 				 blocks
						 				);
		        this.sensorContainer.addSensor(sensor);
			}
		}
		logger.info("¬сего найдено устройств: "+this.sensorContainer.getSize());
    }

	/** получить HeartBeat */
	@Override
	public IHeartBeat getHeartBeat(){
		IHeartBeat client = null;
        try {
            client = (IHeartBeat) proxyFactory.create(serviceHeartBeat, serviceUrl+"HeartBeat");
        } catch(Exception ex){
        	client=null;
            System.err.println("Core#getHeartBeat: EXCEPTION: " + ex.toString());
        }
        return client;
	}
	
	/** получить Task */
	@Override
	public ITask getTask(){
		ITask client = null;
        try {
            client = (ITask) proxyFactory.create(serviceTask, serviceUrl+"Task");
        } catch(Exception ex){
        	client=null;
            System.err.println("Core#getTask: EXCEPTION: " + ex.toString());
        }
        return client;
	}
	
	/** получить Information*/
	@Override
	public  IInformation getInformation(){
		IInformation client = null;
        try {
            client = (IInformation) proxyFactory.create(serviceInformation, serviceUrl+"Information");
        } catch(Exception ex){
        	client=null;
            System.err.println("Core#getInformation: EXCEPTION: " + ex.toString());
        }
        return client;
	}

	@Override
	public IAlarm getAlarm() {
		IAlarm client=null;
        try {
            client = (IAlarm) proxyFactory.create(serviceAlarm, serviceUrl+"Alarm");
        } catch(Exception ex){
        	client=null;
            System.err.println("Core#getAlarm: EXCEPTION: " + ex.toString());
        }
		return client;
	}

	
	@Override
	public ModuleSettings getModuleSettings() {
		return this.moduleSettings;
	}
	
	
	
	/** рабочий цикл программы */
	public void run(){
		// Logger.getLogger("fenomen").setLevel(Level.DEBUG);
		// Logger.getLogger("fenomen").addAppender(new ConsoleAppender(new PatternLayout()));
		/** уникальный идентификатор данного модул€ */
		// уникальный идентификатор модул€
		ModuleIdentifier moduleIdentifier=new ModuleIdentifier(new FileStore("module.id"));

		/** сервис, который служит дл€ отправки сигнала Information */
		ThreadInformation threadInformation=new ThreadInformation(this,this,moduleIdentifier);
		/** сервис, который служит дл€ отправки сигнала Alarm */
		ThreadAlarm threadAlarm=new ThreadAlarm(this,this,moduleIdentifier);
		
		/** сервис, который общаетс€ с датчиками */
		SensorProcessor sensorProcessor=new SensorProcessor(threadAlarm,threadInformation);

		ThreadSensor threadSensor=new ThreadSensor(this,sensorProcessor, sensorContainer);

		/** обработчик вход€щих Task*/
		TaskProcessor taskProcessor=new TaskProcessor(moduleIdentifier, 	
													  10000,
													  threadInformation,// IModuleInformationListener
													  this, // IModuleSettingsAware
													  this, // IModuleSettingsListener 
													  this, // ISensorContainerAware
													  this, // AlarmServiceAware
													  this.getAlarm(), // serviceAlarm
													  this, // InformationServiceAware
													  this.getInformation() // informationService
													  );
		/** сервис, который получает вход€щие Task */
		ThreadTask threadTask=new ThreadTask(this, 
											 this,
											 moduleIdentifier, 
											 taskProcessor);

		/**  сервис, который служит дл€ отправки сигнала —ердцебиение */ 
		ThreadHeartBeat threadHeartBeat=new ThreadHeartBeat(this,
															this,
															moduleIdentifier,
															threadTask
															);
		
		// добавить элементы дл€ оповещени€ об изменени€х в настройках
		this.addUpdateSettingsNotifier(threadHeartBeat);
		this.addUpdateSettingsNotifier(threadTask);
		this.addUpdateSettingsNotifier(threadSensor);
		this.addUpdateSettingsNotifier(threadAlarm);
		this.addUpdateSettingsNotifier(threadInformation);
		
		// запуск прослушивани€ сети ModBus и автоматического считывани€ значени€ датчиков 
		sensorContainer.startService();
		// запуск потока HeartBeat
		threadHeartBeat.start();
		// запуск потока Task
		threadTask.start();
		// запуск потока Information
		threadInformation.start();
		// запуск потока Alarm
		threadAlarm.start();
		// запуск потока общени€ с ƒатчиками
		threadSensor.start();
	}

	public void addUpdateSettingsNotifier(IUpdateSettings updateSettings){
		this.listOfSettingsNotify.add(updateSettings);
	}
	
	@Override
	public void setModuleSettings(String paramName, String paramValue) {
		this.moduleSettings.setParameter(paramName, paramValue);
		// оповестить об изменени€х в параметре
		for(IUpdateSettings element : this.listOfSettingsNotify){
			element.notifyUpdateSettings();
		}
	}

	@Override
	public void setModuleSettings(ModuleSettings moduleSettings) {
		this.moduleSettings=moduleSettings;
		// оповестить об изменени€х в параметре
		for(IUpdateSettings element : this.listOfSettingsNotify){
			element.notifyUpdateSettings();
		}
	}

	@Override
	public SensorContainer getSensorContainer() {
		return this.sensorContainer;
	}
}
