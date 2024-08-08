package database;

import org.apache.log4j.ConsoleAppender;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import fenomen.monitor.notifier.worker.MonitorWorkerFactory;

public class StaticConnector {
	/** ���������� � ����� ������ */
	static Connector connector;
	
	static{
		// INFO ������.������ ���������
		Logger.getLogger("fenomen").setLevel(Level.DEBUG);
		Logger.getLogger("fenomen").addAppender(new ConsoleAppender(new PatternLayout("%-5p %l %M %m%n")));
		try {
			connector=new Connector();
			// INFO ������.start 
			updateModuleTask();
		} catch (Exception e) {
			e.printStackTrace();
		}
		MonitorWorkerFactory.getInstance();
	}
	
	/**  
	 * @return �������� ���������� � ����� ������ ({@link ConnectWrap}) 
	 * */
	public static ConnectWrap getConnectWrap(){
		return connector.getConnector();
	}
	
	private static void updateModuleTask(){
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// ���������� ��� ������ �� ���� �������, ������� ����� State=1 - ����� �������, ���������� � 0 - ����� ������ �� ������
			connector.getConnection().createStatement().executeUpdate("update module_task set module_task.id_state=0,module_task.id_result=0 where module_task.id_state=1 ");
			connector.getConnection().commit();
		}catch(Exception ex){
			System.err.println("set module_task.id_state=0 Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}
}
