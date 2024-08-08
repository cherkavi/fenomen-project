package fenomen.monitor.notifier.worker;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import database.ConnectWrap;
import database.StaticConnector;
import fenomen.monitor.notifier.jabber.JabberSender;
import fenomen.monitor.notifier.jabber.message.MessageAlarm;
import fenomen.monitor.notifier.jabber.message.MessageHeartBeat;
import fenomen.monitor.notifier.jabber.message.MessageInformation;
import fenomen.monitor.notifier.jabber.message.MessageRestart;
import fenomen.monitor.notifier.jabber.wrap.IMessageListener;
import fenomen.monitor.notifier.jabber.wrap.IPresenceListener;

/** фабрика, которая генерирует и управляет Worker-ами(работой и жизненным циклом), по каждому из модулей, которые в данный момент присоединены */
public class MonitorWorkerFactory  extends Thread implements IMessageListener, IPresenceListener{
	
	private static MonitorWorkerFactory instance;
	static {
		instance=new MonitorWorkerFactory();
		instance.start();
		
		MonitorHeartBeatWatchDogFactory.getInstance();
	}
	
	/** получить единственный экземляр {@link MonitorWorkerFactory} */
	public static MonitorWorkerFactory getInstance(){
		return instance;
	}
	
	/** логгер для данного класса */
	private Logger logger=Logger.getLogger(this.getClass());
	
	/**  объект для информационного обмена с мониторами */
	private JabberSender jabberSender;
	
	
	/** фабрика, которая генерирует и управляет Worker-ами (работой и жизненным циклом), по каждому из модулей, которые в данный момент присоединены */
	public MonitorWorkerFactory(){
		try{
			jabberSender=new JabberSender(this,this);
			jabberSender.start();
		}catch(Exception ex){
			logger.error("JabberServer create Exception:"+ex.getMessage());
			System.exit(1);
		}
	}
	
	/** список событий, которые находятся в очереди для оповещения мониторов, которые находятся в OnLine списке Jabber */
	private ArrayList<EPulseType> listOfEvent=new ArrayList<EPulseType>();

	/** добавить в очередь очередное событие для оповещения */
	private void addPulseType(EPulseType pulseType){
		synchronized(listOfEvent){
			listOfEvent.add(pulseType);
			// оповестить о необходимости обработки очереди
			listOfEvent.notify();
		}
	}
	
	/** Alarm оповещение о наличии в таблице monitor_event_alarm записей для оповещения удаленных мониторов */
	public void pulseEventAlarm(){
		this.addPulseType(EPulseType.alarm);
	}
	/** Information оповещение о наличии в таблице monitor_event_information записей для оповещения удаленных мониторов */
	public void pulseEventInformation(){
		this.addPulseType(EPulseType.information);
	}
	
	/** Restart оповещение о наличии в таблице monitor_event_restart записей для оповещения удаленных мониторов */
	public void pulseEventRestart(){
		this.addPulseType(EPulseType.restart);
	}
	
	/** HeartBeat оповещение о наличии в таблице monitor_event_heart записей для оповещения удаленных мониторов */
	public void pulseEventHeartBeat(){
		this.addPulseType(EPulseType.heart_beat);
	}
	
	/* просмотреть таблицы с промежуточными состояниями для сообщений и перевести их в начальные ( State=1) 
	private void resetEvent(){
		Connection connection=this.getConnector().getConnection();
		// сервер.старт WorkerFactory - просмотреть таблицы с промежуточными состояниями для сообщений и перевести их в начальные ( State=1)
			// restart
		try{
			Statement statement=connection.createStatement();
			statement.executeUpdate("update monitor_event_restart set id_monitor_event_state=1 where id_monitor_event_state=1");
			connection.commit();
			try{
				statement.close();
			}catch(Exception ex){};
		}catch(Exception ex){
			logger.error("reset monitor_event_restart Exception:"+ex.getMessage(), ex);
		}
			// information
		try{
			Statement statement=connection.createStatement();
			statement.executeUpdate("update monitor_event_information set id_monitor_event_state=1 where id_monitor_event_state=1");
			connection.commit();
			try{
				statement.close();
			}catch(Exception ex){};
		}catch(Exception ex){
			logger.error("reset monitor_event_information Exception:"+ex.getMessage(), ex);
		}
			// heart_beat
		try{
			Statement statement=connection.createStatement();
			statement.executeUpdate("update monitor_event_heart_beat set id_monitor_event_state=1 where id_monitor_event_state=1");
			connection.commit();
			try{
				statement.close();
			}catch(Exception ex){};
		}catch(Exception ex){
			logger.error("reset monitor_event_heart_beat Exception:"+ex.getMessage(), ex);
		}
			// alarm
		try{
			Statement statement=connection.createStatement();
			statement.executeUpdate("update monitor_event_alarm set id_monitor_event_state=1 where id_monitor_event_state=1");
			connection.commit();
			try{
				statement.close();
			}catch(Exception ex){};
		}catch(Exception ex){
			logger.error("reset monitor_event_alarm Exception:"+ex.getMessage(), ex);
		}
	}
	*/
	
	/* просмотреть таблицы с промежуточными, взятым для отправки (2), и подтвержденными (3) состояниями для сообщений и перевести их в новые ( State=2,3 -> State=1) 
	private void resetEventByMonitor(int monitorId){
		Connection connection=this.getConnector().getConnection();
		// сервер.start MonitorWorker - просмотреть таблицы с промежуточными состояниями (2,3) для сообщений и перевести их в начальные ( State=1)
			// restart
		try{
			Statement statement=connection.createStatement();
			statement.executeUpdate("update monitor_event_restart set id_monitor_event_state=1 where (id_monitor_event_state=2 or id_monitor_event_state=3) and id_monitor="+monitorId);
			connection.commit();
			try{
				statement.close();
			}catch(Exception ex){};
		}catch(Exception ex){
			logger.error("reset monitor_event_restart Exception:"+ex.getMessage(), ex);
		}
			// information
		try{
			Statement statement=connection.createStatement();
			statement.executeUpdate("update monitor_event_information set id_monitor_event_state=1 where (id_monitor_event_state=2 or id_monitor_event_state=3) and id_monitor="+monitorId);
			connection.commit();
			try{
				statement.close();
			}catch(Exception ex){};
		}catch(Exception ex){
			logger.error("reset monitor_event_information Exception:"+ex.getMessage(), ex);
		}
			// heart_beat
		try{
			Statement statement=connection.createStatement();
			statement.executeUpdate("update monitor_event_heart_beat set id_monitor_event_state=1 where (id_monitor_event_state=2 or id_monitor_event_state=3) and id_monitor="+monitorId);
			connection.commit();
			try{
				statement.close();
			}catch(Exception ex){};
		}catch(Exception ex){
			logger.error("reset monitor_event_heart_beat Exception:"+ex.getMessage(), ex);
		}
			// alarm
		try{
			Statement statement=connection.createStatement();
			statement.executeUpdate("update monitor_event_alarm set id_monitor_event_state=1 where (id_monitor_event_state=2 or id_monitor_event_state=3) and id_monitor="+monitorId);
			connection.commit();
			try{
				statement.close();
			}catch(Exception ex){};
		}catch(Exception ex){
			logger.error("reset monitor_event_alarm Exception:"+ex.getMessage(), ex);
		}
	}*/
	

	/** проверить таблицы на недоставленные сообщения, о которых нужно оповестить удаленные мониторы 
	 * в случае, когда мониторы подключены, а сервер перезагрузился - мониторы ждут - очень узкое место, почти никогда не должно использоваться,
	 * т.к. если "гаснет" сервер - гаснут и мониторы */
	private void checkForPulse(Integer idMonitor){
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Connection connection=connector.getConnection();
			ResultSet rs=null;
			try{
				if(idMonitor!=null){
					rs=connection.createStatement().executeQuery("SELECT * FROM monitor_event_alarm where (id_monitor_event_state=0 or id_monitor_event_state=1)  and id_monitor="+idMonitor +"  order by id desc");
				}else{
					rs=connection.createStatement().executeQuery("SELECT * FROM monitor_event_alarm where (id_monitor_event_state=0 or id_monitor_event_state=1) order by id desc");
				}
				if(rs.next()){
					logger.debug("Найдены сообщения Alarm по монитору №"+idMonitor);
					this.addPulseType(EPulseType.alarm);
				}else{
					logger.debug("Event_Alarm does not found by Monitor.id="+idMonitor);
					/*if(idMonitor!=null){
						rs=connection.createStatement().executeQuery("SELECT * FROM monitor_event_alarm where id_monitor="+idMonitor);
						while(rs.next()){
							for(int counter=0;counter<rs.getMetaData().getColumnCount();counter++){
								System.out.print(rs.getMetaData().getColumnName(counter+1)+":"+rs.getString(counter+1)+"  | ");
							}
						}
						System.out.println("");
					}*/		
				}
			}catch(Exception ex){
				logger.error("checkForPulse table alarm Exception: "+ex.getMessage(), ex);
			}finally{
				try{rs.getStatement().close();}catch(Exception ex){};
			}

			try{
				if(idMonitor!=null){
					rs=connection.createStatement().executeQuery("SELECT * FROM monitor_event_information where (id_monitor_event_state=0 or id_monitor_event_state=1) and id_monitor="+idMonitor+" order by id desc");
				}else{
					rs=connection.createStatement().executeQuery("SELECT * FROM monitor_event_information where (id_monitor_event_state=0 or id_monitor_event_state=1) order by id desc");
				}
				
				if(rs.next()){
					logger.debug("Найдены сообщения Information по монитору №"+idMonitor);
					this.addPulseType(EPulseType.information);
				}else{
					logger.debug("Event_Information does not found by Monitor.id="+idMonitor);
				}
			}catch(Exception ex){
				logger.error("checkForPulse table information Exception: "+ex.getMessage(), ex);
			}finally{
				try{rs.getStatement().close();}catch(Exception ex){};
			}
			
			try{
				if(idMonitor!=null){
					rs=connection.createStatement().executeQuery("SELECT * FROM monitor_event_restart where (id_monitor_event_state=0 or id_monitor_event_state=1) and id_monitor="+idMonitor+" order by id desc");
				}else{
					rs=connection.createStatement().executeQuery("SELECT * FROM monitor_event_restart where (id_monitor_event_state=0 or id_monitor_event_state=1) order by id desc");
				}
				
				if(rs.next()){
					logger.debug("Найдены сообщения Restart по монитору №"+idMonitor);
					this.addPulseType(EPulseType.restart);
				}else{
					logger.debug("Restart_Information does not found by Monitor.id="+idMonitor);
				}
			}catch(Exception ex){
				logger.error("checkForPulse table restart Exception: "+ex.getMessage(), ex);
			}finally{
				try{rs.getStatement().close();}catch(Exception ex){};
			}
			
			try{
				if(idMonitor!=null){
					rs=connection.createStatement().executeQuery("SELECT * FROM monitor_event_heart_beat where (id_monitor_event_state=0 or id_monitor_event_state=1) and id_monitor="+idMonitor+" order by id desc");
				}else{
					rs=connection.createStatement().executeQuery("SELECT * FROM monitor_event_heart_beat where (id_monitor_event_state=0 or id_monitor_event_state=1) order by id desc");
				}
				
				if(rs.next()){
					logger.debug("Найдены сообщения Heart по монитору №"+idMonitor);
					this.addPulseType(EPulseType.heart_beat);
				}else{
					logger.debug("Event_HeartBeat does not found by Monitor.id="+idMonitor);
				}
			}catch(Exception ex){
				logger.error("checkForPulse table heart_beat Exception: "+ex.getMessage(), ex);
			}finally{
				try{rs.getStatement().close();}catch(Exception ex){};
			}
		}catch(Exception ex){
			logger.error("ошибка при выявлении событий "+ex.getMessage(), ex);
		}finally{
			connector.close();
		}
		
	}
	
	@Override
	public void run(){
		this.checkForPulse(null);
		/** текущий тип оповещения для потока */
		EPulseType currentPulseType=null;
		// запуск бесконечного цикла для данного объекта, работает вместе с жизненным циклом программы 
		while(true){
			try{
				currentPulseType=null;
				synchronized(this.listOfEvent){
					if(this.listOfEvent.size()>0){
						currentPulseType=this.listOfEvent.remove(0);
					}
				}
				if(currentPulseType!=null){
					// получено одно событие из списка 
					switch(currentPulseType){
						case alarm: {
							logger.debug("оповещение об Alarm событиях");
							this.processAlarmEvent();
							 };break;
						case information: {
							logger.debug("оповещение об Information событиях");
							this.processInformationEvent();
							 };break;
						case heart_beat: {
							logger.debug("оповещение об HeartBeat событиях");
							this.processHeartBeatEvent();
							 };break;
						case restart: {
							logger.debug("оповщение о restart событиях");
							this.processRestartEvent();
							 };break;
						default:{
							logger.error(" type of Event is not recognized: "+currentPulseType.toString());
						}
					}
				}else{
					// событий нет
					synchronized(this.listOfEvent){
						// проверка очереди на пустое значение
						if(this.listOfEvent.size()==0){
							try{
								// сообщений нет - уснуть 
								listOfEvent.wait();
							}catch(InterruptedException ie){};
						}
					}
				}
			}catch(Exception ex){
				logger.error("CRITICAL ERROR Worker Factory Exception:"+ex.getMessage());
			}
		}
	}
	
	/** изменить статус сообщения с 1-new на 2 - отправленное */
	private void saveAsSended(Connection connection, String table, int id) throws SQLException{
		connection.createStatement().executeUpdate("update "+table+" set id_monitor_event_state=2 where id_monitor_event_state=1 and id="+id);
		connection.commit();
	}
	
	String queryAlarmEvent="	select monitor_event_alarm.id,	 \n "+ // уникальный идентификатор сообщения
	"	       monitor.id monitor_id,	 \n "+ // уникальный идентификатор монитора
	"	       monitor.jabber_login jabber_login,	 \n "+ // jabber адрес или логин
	"	       module_alarm.id module_alarm_id,	 \n "+ // уникальный адрес удаленного модуля, по которому произошло событие
	"	       module_alarm.id_description description,	 \n "+ // описание события
	"	       module_alarm.time_write,	 \n "+ // время события
	"	       module.id_module module_name,	 \n "+ // имя модуля
	"	       module.address module_address	 \n "+ // адрес модуля
	"	from monitor_event_alarm	 \n "+
	"	inner join monitor on monitor.id=monitor_event_alarm.id_monitor	 \n "+
	"	inner join module_alarm on module_alarm.id=monitor_event_alarm.id_module_alarm	 \n "+
	"	inner join module on module.id=module_alarm.id_module	 \n "+
	"	where monitor_event_alarm.id_monitor_event_state<=1 \n ";

	/** обработать оповещение о появившихся Alarm сообщениях для мониторов  */
	private void processAlarmEvent(){
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Connection connection=connector.getConnection();
			logger.debug("прочесть таблицу, получить объекты для оповещений (Alarm), оповестить");
			String query=queryAlarmEvent+" and monitor.id in ("+this.monitorListDelimeterComma+") order by monitor.id";
			// logger.debug(query);
			ResultSet rs=connection.createStatement().executeQuery(query);
			ArrayList<MessageAlarm> listOfAlarm=new ArrayList<MessageAlarm>();
			while(rs.next()){
				logger.debug("получить очередное событие MessageAlarm для оповещения по определенному монитору"+rs.getInt("id"));
				MessageAlarm message=new MessageAlarm();
				message.setId(rs.getInt("id"));
				message.setJabberAddress(rs.getString("jabber_login"));
				message.setEventId(rs.getInt("module_alarm_id"));
				message.setDescription(rs.getString("description"));
				try{
					message.setTimeWrite(new Date(rs.getTimestamp("time_write").getTime()));
				}catch(Exception ex){};
				message.setModuleName(rs.getString("module_name"));
				message.setModuleAddress("module_address");
				message.setFlagNotify(true);
				listOfAlarm.add(message);
			}
			logger.debug("все события (Alarm) прочитаны, оповестить модули ");
			for(int counter=0;counter<listOfAlarm.size();counter++){
				try{
					logger.debug("получить MonitorWorker");
					MonitorWorker monitorWorker=this.mapOfWorker.get(listOfAlarm.get(counter).getJabberAddress());
					MessageAlarm messageAlarm=listOfAlarm.get(counter);
					logger.debug("оповестить удаленный монитор(Alarm) - поставить в очередь Jabber ");
					monitorWorker.sendMessageToMonitor(listOfAlarm.get(counter).convertToXml());
					logger.debug("записать как оповещенное MessageAlarm");
					this.saveAsSended(connection,"monitor_event_alarm",messageAlarm.getId());
				}catch(Exception ex){
					logger.warn("ошибка оповещения удаленного монитора (MessageAlarm):"+ex.getMessage(),ex);
				}
			}
		}catch(Exception ex){
			logger.error("попытка получения событий для оповещения мониторов закончилась неудачно: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}

	private String queryInformationEvent="	select monitor_event_information.id,	 \n "+
	"	       monitor.id monitor_id,	 \n "+
	"	       monitor.jabber_login jabber_login,	 \n "+
	"	       module_information.id module_information_id,	 \n "+
	"	       module_information.description,	 \n "+
	"	       module_information.time_write,	 \n "+
	"	       module.id_module module_name,	 \n "+
	"	       module.address module_address	 \n "+
	"	from monitor_event_information	 \n "+
	"	inner join monitor on monitor.id=monitor_event_information.id_monitor	 \n "+
	"	inner join module_information on module_information.id=monitor_event_information.id_module_information	 \n "+
	"	inner join module on module.id=module_information.id_module	 \n "+
	"	where monitor_event_information.id_monitor_event_state<=1	 \n ";

	/** обработать оповещение о появившихся Information сообщениях для мониторов  */
	private void processInformationEvent(){
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Connection connection=connector.getConnection();
			logger.debug("прочесть таблицу, получить объекты для оповещений (Information), оповестить");
			String query=queryInformationEvent+" and monitor.id in ("+this.monitorListDelimeterComma+") order by monitor.id";
			// logger.debug(query);
			ResultSet rs=connection.createStatement().executeQuery(query);
			ArrayList<MessageInformation> listOfInformation=new ArrayList<MessageInformation>();
			while(rs.next()){
				logger.debug("получить очередное событие (Information) для оповещения по определенному монитору"+rs.getInt("id"));
				MessageInformation message=new MessageInformation();
				message.setId(rs.getInt("id"));
				message.setJabberAddress(rs.getString("jabber_login"));
				message.setEventId(rs.getInt("module_information_id"));
				message.setDescription(rs.getString("description"));
				try{
					message.setTimeWrite(new Date(rs.getTimestamp("time_write").getTime()));
				}catch(Exception ex){};
				message.setModuleName(rs.getString("module_name"));
				message.setModuleAddress("module_address");
				message.setFlagNotify(true);
				listOfInformation.add(message);
			}
			logger.debug("все события (Information) прочитаны, оповестить модули ");
			for(int counter=0;counter<listOfInformation.size();counter++){
				try{
					logger.debug("получить MonitorWorker");
					MonitorWorker monitorWorker=this.mapOfWorker.get(listOfInformation.get(counter).getJabberAddress());
					MessageInformation messageInformation=listOfInformation.get(counter);
					logger.debug("оповестить удаленный монитор - поставить в очередь Jabber (MessageInformation)");
					monitorWorker.sendMessageToMonitor(listOfInformation.get(counter).convertToXml());
					logger.debug("записать (MessageInformation) как оповещенное");
					this.saveAsSended(connection,"monitor_event_information",messageInformation.getId());
				}catch(Exception ex){
					logger.warn("ошибка оповещения удаленного монитора (о событии MessageInformation):"+ex.getMessage(),ex);
				}
			}
		}catch(Exception ex){
			logger.error("попытка получения событий для оповещения мониторов (Information) закончилась неудачно: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}
	
	private String queryRestartEvent="	select monitor_event_restart.id,	 \n "+
	"	       monitor.id monitor_id,	 \n "+
	"	       monitor.jabber_login jabber_login,	 \n "+
	"	       module_restart.id module_restart_id,	 \n "+
	"	       module_restart.time_write,	 \n "+
	"	       module.id_module module_name,	 \n "+
	"	       module.address module_address	 \n "+
	"	from monitor_event_restart	 \n "+
	"	inner join monitor on monitor.id=monitor_event_restart.id_monitor	 \n "+
	"	inner join module_restart on module_restart.id=monitor_event_restart.id_module_restart	 \n "+
	"	inner join module on module.id=module_restart.id_module	 \n "+
	"	where monitor_event_restart.id_monitor_event_state<=1	 \n ";

	/** обработать оповещение о появившихся Restart сообщениях для мониторов  */
	private void processRestartEvent(){
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Connection connection=connector.getConnection();
			logger.debug("прочесть таблицу, получить объекты для оповещений (Restart), оповестить");
			String query=queryRestartEvent+" and monitor.id in ("+this.monitorListDelimeterComma+") order by monitor.id";
			//logger.debug(query);
			ResultSet rs=connection.createStatement().executeQuery(query);
			ArrayList<MessageRestart> listOfRestart=new ArrayList<MessageRestart>();
			while(rs.next()){
				logger.debug("получить очередное событие (Restart) для оповещения по определенному монитору"+rs.getInt("id"));
				MessageRestart message=new MessageRestart();
				message.setId(rs.getInt("id"));
				message.setJabberAddress(rs.getString("jabber_login"));
				message.setEventId(rs.getInt("module_restart_id"));
				message.setDescription("RESTART");
				try{
					message.setTimeWrite(new Date(rs.getTimestamp("time_write").getTime()));
				}catch(Exception ex){};
				message.setModuleName(rs.getString("module_name"));
				message.setModuleAddress("module_address");
				message.setFlagNotify(true);
				listOfRestart.add(message);
			}
			logger.debug("все события (Restart) прочитаны, оповестить модули ");
			for(int counter=0;counter<listOfRestart.size();counter++){
				try{
					logger.debug("получить MonitorWorker");
					MonitorWorker monitorWorker=this.mapOfWorker.get(listOfRestart.get(counter).getJabberAddress());
					MessageRestart messageRestart=listOfRestart.get(counter);
					logger.debug("оповестить удаленный монитор - поставить в очередь Jabber (MessageRestart)");
					monitorWorker.sendMessageToMonitor(listOfRestart.get(counter).convertToXml());
					logger.debug("записать (MessageRestart) как оповещенное");
					this.saveAsSended(connection,"monitor_event_restart",messageRestart.getId());
				}catch(Exception ex){
					logger.warn("ошибка оповещения удаленного монитора (о событии MessageRestart):"+ex.getMessage(),ex);
				}
			}
		}catch(Exception ex){
			logger.error("попытка получения событий для оповещения мониторов (Restart) закончилась неудачно: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}

	private String queryHeartBeatEvent="	select monitor_event_heart_beat.id,	 \n "+
	"	       monitor.id monitor_id,	 \n "+
	"	       monitor.jabber_login jabber_login,	 \n "+
	"	       module_heart_beat.id  module_heart_beat_id,	 \n "+
	"	       monitor_event_heart_beat.state_time_write time_write,	 \n "+
	"	       module.id_module module_name,	 \n "+
	"	       module.address module_address	 \n "+
	"	from monitor_event_heart_beat	 \n "+
	"	inner join monitor on monitor.id=monitor_event_heart_beat.id_monitor	 \n "+
	"	left join module_heart_beat on module_heart_beat.id=monitor_event_heart_beat.id_module_heart_beat	 \n "+
	"	inner join module on module.id=monitor_event_heart_beat.id_module	 \n "+
	"	where monitor_event_heart_beat.id_monitor_event_state<=1	 \n ";


	/** обработать оповещение о появившихся HeartBeat сообщениях для мониторов  */
	private void processHeartBeatEvent(){
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Connection connection=connector.getConnection();
			logger.debug("прочесть таблицу, получить объекты для оповещений (HeartBeat not recieved), оповестить");
			String query=queryHeartBeatEvent+" and monitor.id in ("+this.monitorListDelimeterComma+") order by monitor.id";
			// logger.debug(query);
			ResultSet rs=connection.createStatement().executeQuery(query);
			ArrayList<MessageHeartBeat> listOfHeartBeat=new ArrayList<MessageHeartBeat>();
			while(rs.next()){
				logger.debug("получить очередное событие (HeartBeat not recieved) для оповещения по определенному монитору"+rs.getInt("id"));
				MessageHeartBeat message=new MessageHeartBeat();
				message.setId(rs.getInt("id"));
				message.setJabberAddress(rs.getString("jabber_login"));
				message.setEventId(rs.getInt("module_heart_beat_id")); // код получения последнего сигнала ( не используется )   
				message.setDescription("HeartBeat not recieved");
				try{
					message.setTimeWrite(new Date(rs.getTimestamp("time_write").getTime())); // время не получения сигнала от удаленного модуля 
				}catch(Exception ex){};
				message.setModuleName(rs.getString("module_name"));
				message.setModuleAddress("module_address");
				message.setFlagNotify(true);
				listOfHeartBeat.add(message);
			}
			logger.debug("все события (HeartBeat not recieved) прочитаны, оповестить модули ");
			for(int counter=0;counter<listOfHeartBeat.size();counter++){
				try{
					// INFO сервер.монитор оповещение о событии HeartBeat - не вышел на связь
					logger.debug("получить MonitorWorker");
					MonitorWorker monitorWorker=this.mapOfWorker.get(listOfHeartBeat.get(counter).getJabberAddress());
					MessageHeartBeat messageHeartBeat=listOfHeartBeat.get(counter);
					logger.debug("оповестить удаленный монитор - поставить в очередь Jabber (HeartBeat not recieved)");
					monitorWorker.sendMessageToMonitor(listOfHeartBeat.get(counter).convertToXml());
					logger.debug("записать (HeartBeat not recieved) как оповещенное");
					this.saveAsSended(connection,"monitor_event_heart_beat",messageHeartBeat.getId());
				}catch(Exception ex){
					logger.warn("ошибка оповещения удаленного монитора (о событии HeartBeat not recieved):"+ex.getMessage(),ex);
				}
			}
		}catch(Exception ex){
			logger.error("попытка получения событий для оповещения мониторов (HeartBeat not recieved) закончилась неудачно: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}
	
	@Override
	public void messageNotify(String from, String text) {
		MonitorWorker monitorWorker=this.mapOfWorker.get(from);
		if(monitorWorker==null){
			logger.warn("Сообщение от несозданного WorkerMonitor-a: "+from);
		}else{
			logger.debug("сообщение от монитора : "+from);
			try{
				monitorWorker.getMessageFromMonitor(text);
			}catch(Exception ex){
				logger.error("ошибка обработки сообщения по отдельно взятому Worker-у: "+ex.getMessage());
			}
		}
	}

	/** список всех подключенных MonitorWorker-ов */
	private HashMap<String, MonitorWorker> mapOfWorker=new HashMap<String, MonitorWorker>();
	/** строка, которая перечисляет через запятую все доступные в данный момент мониторы  */
	private String monitorListDelimeterComma="0";

	/** создать строку с перечисленными кодами мониторов, которые находятся в сети */
	private void recalculateMonitorList(){
		StringBuffer returnValue=new StringBuffer();
		returnValue.append("0");
		Iterator<String> keyIterator=this.mapOfWorker.keySet().iterator();
		while(keyIterator.hasNext()){
			String currentValue=keyIterator.next();
			if(currentValue!=null){
				returnValue.append(", ");
				returnValue.append(this.mapOfWorker.get(currentValue).getMonitorId());
			}
		}
		this.monitorListDelimeterComma=returnValue.toString();
	}
	
	@Override
	public void userEnter(String user) {
		// INFO сервер.монитор вход монитора в сеть
		this.removeMonitorWorker(user);
		logger.debug("создать worker-a по заданному ключу: "+user);
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			MonitorWorker monitor=new MonitorWorker(user,this.jabberSender, connector);
			// logger.debug("взятые для отправки (2) и переданные на модуль (3) перевести в статус новые (1) ");
			// this.resetEventByMonitor(monitor.getMonitorId()); - сделано в конструкторе монитора
			this.mapOfWorker.put(user, monitor);
			recalculateMonitorList();
			logger.debug("проверить наличие сообщений по данному монитору "); 
			this.checkForPulse(monitor.getMonitorId());
		}catch(Exception ex){
			logger.error("user Enter Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}

	/** удалить пользователя из списка MonitorWorker по общению с мониторами */
	private void removeMonitorWorker(String user){
		MonitorWorker forRemove=this.mapOfWorker.get(user);
		if(forRemove!=null){
			this.mapOfWorker.remove(user);
		}
	}
	
	@Override
	public void userExit(String user) {
		// INFO сервер.монитор выход монитора из сети
		logger.debug("удалить worker-a по заданному ключу: "+user);
		this.removeMonitorWorker(user);
		recalculateMonitorList();
	}

	
	@Override
	public void userError(String user) {
		logger.error("нет реализации");
	}
	
}
