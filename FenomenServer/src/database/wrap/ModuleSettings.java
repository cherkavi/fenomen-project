package database.wrap;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.*;

@Entity
@PersistenceCapable(detachable="false")
@Table(name="module_settings")
public class ModuleSettings {
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;
	@Column(name="id_module")
	private int idModule;
	@Column(name="id_section")
	private int idSection;
	@Column(name="id_parameter")
	private int idParameter;
	@Column(name="settings_value",length=255)
	private String settingsValue;
	@Column(name="module_recieve")
	private int moduleRecieve;
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
	public int getIdSection() {
		return idSection;
	}
	public void setIdSection(int idSection) {
		this.idSection = idSection;
	}
	public int getIdParameter() {
		return idParameter;
	}
	public void setIdParameter(int idParameter) {
		this.idParameter = idParameter;
	}
	public String getSettingsValue() {
		return settingsValue;
	}
	public void setSettingsValue(String settingsValue) {
		this.settingsValue = settingsValue;
	}
	public int getModuleRecieve() {
		return moduleRecieve;
	}
	public void setModuleRecieve(int moduleRecieve) {
		this.moduleRecieve = moduleRecieve;
	}
	public Date getTimeWrite() {
		return timeWrite;
	}
	public void setTimeWrite(Date timeWrite) {
		this.timeWrite = timeWrite;
	}
	
}
