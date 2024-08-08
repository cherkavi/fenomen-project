package fenomen.monitor.notifier.worker;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.MonitorEventAlarm;
import database.wrap.MonitorEventHeartBeat;
import database.wrap.MonitorEventInformation;
import database.wrap.MonitorEventResolve;
import database.wrap.MonitorEventRestart;
import fenomen.monitor.notifier.jabber.JabberSender;
import fenomen.monitor.web_service.common.XmlMessage;

/** �������, ������� ����������� �������������� �������� */
public class MonitorWorker extends Thread{
	private Logger logger=Logger.getLogger(this.getClass());
	/** ���������� ����� �������� � ���� ������ �� ������� Monitor.id*/
	private int monitorId;
	/** ����� �������� � ���� Jabber */
	private String jabberAddress;
	/** ������, ����� ������� ����� �������� ��������� ��� �������� */
	private JabberSender jabberSender;
	
	/** �������, ������� ����������� �������������� �������� 
	 * @param jabberAddress - ����� � ���� Jabber
	 * @param jabberSender - ������, ����� ������� ����� ������������ �������
	 * @param connector - ���������� � ����� ������ ��� ��������� ����������� ����� �� �������� 
	 */
	public MonitorWorker(String jabberAddress, JabberSender jabberSender, ConnectWrap connector) throws SQLException{
		this.jabberSender=jabberSender;
		this.jabberAddress=jabberAddress;
		Connection connection=connector.getConnection();
		String query="select * from monitor where jabber_login=?";
		PreparedStatement ps=connection.prepareStatement(query);
		ps.setString(1, jabberAddress);
		ResultSet rs=ps.executeQuery();
		if(rs.next()){
			this.monitorId=rs.getInt("id");
		}else{
			throw new SQLException("monitor does not found: "+jabberAddress);
		}
		
		logger.debug("�������� �� ������� �������� #"+this.monitorId+" ��� ������, �� ��� ��� ������� �������� ���������� ��� �����, �� ��������");
		String[] tables=new String[]{"monitor_event_alarm", "monitor_event_information", "monitor_event_heart_beat", "monitor_event_restart"};
		for(int counter=0;counter<tables.length;counter++){
			connection.createStatement().executeUpdate("update "+tables[counter]+" set id_monitor_event_state=1 where (id_monitor_event_state=2 or id_monitor_event_state=3 or id_monitor_event_state=0) and id_monitor="+this.monitorId);
		}
		connection.commit();
		logger.debug("������� ������� ���������");
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj!=null){
			if(obj instanceof MonitorWorker){
				return ((MonitorWorker)obj).jabberAddress.equals(this.jabberAddress);
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

	/** �������� ���������� ����� �������� �� ���� ������ (monitor.id) */
	public int getMonitorId(){
		return this.monitorId;
	}
	
	/** �������� ��������� ��������� �� ���������� �������� - ��������� �������, ����� ����� ��� ���������*/
	public void getMessageFromMonitor(String text) {
		logger.debug("��������� ��������� �� ���������� ��������: "+text);
		XmlMessage message=XmlMessage.getInstanceByMessage(text);
		if(message!=null){
			ConnectWrap connector=StaticConnector.getConnectWrap();
			try{
				if(message.isFlagConfirm()){
					// confirm
					if(message.getType().equals(XmlMessage.typeAlarm)){
						// ���������� ��� ��������� � 3 - ������������ �������
						connector.getConnection().createStatement().executeUpdate("update monitor_event_alarm set id_monitor_event_state=3 where id_monitor_event_state=2 and id="+message.getId());
						connector.getConnection().commit();
					}
					if(message.getType().equals(XmlMessage.typeHeartBeat)){
						connector.getConnection().createStatement().executeUpdate("update monitor_event_heart_beat set id_monitor_event_state=3 where id_monitor_event_state=2 and id="+message.getId());
						connector.getConnection().commit();
					}
					if(message.getType().equals(XmlMessage.typeInformation)){
						connector.getConnection().createStatement().executeUpdate("update monitor_event_information set id_monitor_event_state=3 where id_monitor_event_state=2 and id="+message.getId());
						connector.getConnection().commit();
					}
					if(message.getType().equals(XmlMessage.typeRestart)){
						connector.getConnection().createStatement().executeUpdate("update monitor_event_restart set id_monitor_event_state=3 where id_monitor_event_state=2 and id="+message.getId());
						connector.getConnection().commit();
					}
				}
				if(message.isFlagNotify()){
					logger.error("�� ������ ������ ���������� - ������ ��������� ");
				}
				if(message.isFlagResolve()){
					// INFO ������.������� ������� ������� �������� 
					if(message.getType().equals(XmlMessage.typeAlarm)){
						Session session=connector.getSession();
						MonitorEventAlarm event=(MonitorEventAlarm)session.get(MonitorEventAlarm.class, message.getId());
						// ���������� ��� ������ 
						event.setIdMonitorEventResolve(this.getResolveByName(session, message.getResolveName()));
						// ���������� ���� ���������� ��������
						event.setResolveTimeWrite(new Date());
						// ���������� ��������� ��� "�������� ������"
						event.setIdMonitorEventState(4);
						session.beginTransaction();
						session.update(event);
						session.getTransaction().commit();
					}
					if(message.getType().equals(XmlMessage.typeHeartBeat)){
						Session session=connector.getSession();
						MonitorEventHeartBeat event=(MonitorEventHeartBeat)session.get(MonitorEventHeartBeat.class, message.getId());
						// ���������� ��� ������ 
						event.setIdMonitorEventResolve(this.getResolveByName(session, message.getResolveName()));
						// ���������� ���� ���������� ��������
						event.setResolveTimeWrite(new Date());
						// ���������� ��������� ��� "�������� ������"
						event.setIdMonitorEventState(4);
						session.beginTransaction();
						session.update(event);
						session.getTransaction().commit();
					}
					if(message.getType().equals(XmlMessage.typeInformation)){
						Session session=connector.getSession();
						MonitorEventInformation event=(MonitorEventInformation)session.get(MonitorEventInformation.class, message.getId());
						// ���������� ��� ������ 
						event.setIdMonitorEventResolve(this.getResolveByName(session, message.getResolveName()));
						// ���������� ���� ���������� ��������
						event.setResolveTimeWrite(new Date());
						// ���������� ��������� ��� "�������� ������"
						event.setIdMonitorEventState(4);
						session.beginTransaction();
						session.update(event);
						session.getTransaction().commit();
					}
					if(message.getType().equals(XmlMessage.typeRestart)){
						Session session=connector.getSession();
						MonitorEventRestart event=(MonitorEventRestart)session.get(MonitorEventRestart.class, message.getId());
						// ���������� ��� ������ 
						event.setIdMonitorEventResolve(this.getResolveByName(session, message.getResolveName()));
						// ���������� ���� ���������� ��������
						event.setResolveTimeWrite(new Date());
						// ���������� ��������� ��� "�������� ������"
						event.setIdMonitorEventState(4);
						session.beginTransaction();
						session.update(event);
						session.getTransaction().commit();
					}
					XmlMessage messageForSend=new XmlMessage(message.getType()){};
					messageForSend.setFlagConfirm(true);
					messageForSend.setId(message.getId());
					String messageForSendXml=messageForSend.convertToXml();
					logger.debug("������������� � ��������� � ��������� ���������: "+messageForSendXml);
					this.jabberSender.addMessageForSend(jabberAddress, messageForSendXml);
				}
			}catch(Exception ex){
				logger.error("getMessageFromMonitor: "+ex.getMessage());
			}finally{
				connector.close();
			}
		}else{
			logger.error("message does not recognized: "+text);
		}
	}
	
	
	/** �������� ��� ������� �� �������  */
	private int getResolveByName(Session session, String resolveName) {
		int returnValue=0;
		try{
			MonitorEventResolve event=(MonitorEventResolve)session.createCriteria(MonitorEventResolve.class)
																   .add(Restrictions.eq("name", resolveName.toUpperCase()))
																   .setMaxResults(1)
																   .uniqueResult();
			if(event!=null){
				returnValue=event.getId();
			}else{
				returnValue=0; 
			}
		}catch(NullPointerException ex){
			returnValue=0;
		}
		// ��������� �� ��������� �������� 
		if(returnValue==0){
			MonitorEventResolve event=new MonitorEventResolve();
			event.setName(resolveName);
			session.beginTransaction();
			session.save(event);
			session.getTransaction().commit();
			returnValue=event.getId();
		}
		return returnValue;
	}


	/** �������� ��������� ��� �������� �� ��������� ������� */
	public void sendMessageToMonitor(String text){
		logger.debug(this.jabberAddress+" : "+text);
		this.jabberSender.addMessageForSend(this.jabberAddress, text);
	}
}
