package database.wrap;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.*;

/**
 * <table border=1>
 * 	<tr>
 * 		<th>monitor_settings_information</th>  <th>POJO</th>  <th>Description</th>
 * 	</tr>
 * 	<tr>
 * 		<td>id</td>  <td>id</td>  <td> уникальный идентификатор в базе </td>
 * 	</tr>
 * 	<tr>
 * 		<td>id_monitor</td>  <td>idMonitor</td>  <td> код Monitor.id</td>
 * 	</tr>
 * 	<tr>
 * 		<td>id_module</td>  <td>idModule</td>  <td> код Module.id</td>
 * 	</tr>
 * 	<tr>
 * 		<td>is_enabled</td>  <td>isEnabled</td>  <td> активна ли данная услуга </td>
 * 	</tr>
 * </table>
 */
@Entity
@PersistenceCapable(detachable="false")
@Table(name="monitor_settings_information")
public class MonitorSettingsInformation {
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;
	@Column(name="id_monitor")
	private int idMonitor;
	@Column(name="id_module")
	private int idModule;
	@Column(name="is_enabled")
	private int isEnabled;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdMonitor() {
		return idMonitor;
	}

	public void setIdMonitor(int idMonitor) {
		this.idMonitor = idMonitor;
	}

	public int getIdModule() {
		return idModule;
	}

	public void setIdModule(int idModule) {
		this.idModule = idModule;
	}

	public int getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(int isEnabled) {
		this.isEnabled = isEnabled;
	}
}
