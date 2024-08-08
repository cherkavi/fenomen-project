package fenomen.monitor.web_service.common;

/** уникальный идентификатор монитора, по которому был осуществлен вход  */
public class MonitorIdentifier {
	private int id;
	public MonitorIdentifier(){
	}
	
	public MonitorIdentifier(int id){
		this.id=id;
	}

	/** получить код из базы данных */
	public int getId() {
		return id;
	}

	/** установить код (из базы данных )*/
	public void setId(int id) {
		this.id = id;
	}
	
}
