package database.wrap;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.*;

@Entity
@PersistenceCapable(detachable="false")
@Table(name="module_heart_beat")
public class ModuleHeartBeat {
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;
	
	@Column(name="id_module")
	private int idModule;
	
	@Column(name="time_write")
	private Date timeWrite;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdModule() {
		return idModule;
	}

	public void setIdModule(int idModule) {
		this.idModule = idModule;
	}

	public Date getTimeWrite() {
		return timeWrite;
	}

	public void setTimeWrite(Date timeWrite) {
		this.timeWrite = timeWrite;
	}
	
	
}
