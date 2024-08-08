package database.wrap;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.*;

@Entity
@PersistenceCapable(detachable="false")
@Table(name="module_settings_section")
public class ModuleSettingsSection {
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;
	@Column(name="name")
	private String name;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
