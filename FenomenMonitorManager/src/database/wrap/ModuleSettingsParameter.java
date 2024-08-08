package database.wrap;
import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.*;

@Entity
@PersistenceCapable(detachable="false")
@Table(name="module_settings_parameter")
public class ModuleSettingsParameter {
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;
	@Column(name="id_section")
	private int idSection;
	@Column(name="parameter_name",length=45)
	private String parameterName;
	@Column(name="parameter_description",length=255)
	private String parameterDescription;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdSection() {
		return idSection;
	}
	public void setIdSection(int idSection) {
		this.idSection = idSection;
	}
	public String getParameterName() {
		return parameterName;
	}
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	public String getParameterDescription() {
		return parameterDescription;
	}
	public void setParameterDescription(String parameterDescription) {
		this.parameterDescription = parameterDescription;
	}
}
