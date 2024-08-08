package database.wrap;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.*;

/**
 * <table border=1>
 * 	<tr>
 * 		<th>monitor_event_information</th>  <th>POJO</th>  <th>Description</th>
 * 	</tr>
 * 	<tr>
 * 		<td>id</td>  <td>id</td>  <td> уникальный идентификатор в базе </td>
 * 	</tr>
 * 	<tr>
 * 		<td>id_module_information</td>  <td>idModuleInformation</td>  <td> код из module_information</td>
 * 	</tr>
 * 	<tr>
 * 		<td>id_monitor</td>  <td>idMonitor</td>  <td> код Monitor.id</td>
 * 	</tr>
 * 	<tr>
 * 		<td>id_monitor_event_state</td>  
 * 		<td>idMonitorEventState</td>  
 * 		<td> код состояния:
 * 			<ul>
 * 				<li><b>1</b> - новое событие</li>
 * 				<li><b>2</b> - взято для отправки через Jabber </li>
 * 				<li><b>3</b> - монитор оповещен </li>
 * 			</ul>
 * 		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>state_time_write</td>  <td>stateTimeWrite</td>  <td> время записи кода состояния </td>
 * 	</tr>
 * 	<tr>
 * 		<td>id_monitor_event_resolve</td>  
 * 		<td>idMonitorEventState</td>  
 * 		<td> код обратной связи по событию от монитора:
 * 			<ul>
 * 				<li><b>0</b> - нет обратной связи по данному событию </li>
 * 				<li><b> x > 0</b> - код из таблицы monitor_event_resolve</li>
 * 			</ul>
 * 		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>resolve_time_write</td>  <td>resolveTimeWrite</td>  <td> время записи состояния разрешения </td>
 * 	</tr>
 * </table>
 */
@Entity
@PersistenceCapable(detachable="false")
@Table(name="monitor_event_information")
public class MonitorEventInformation {
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;
	@Column(name="id_module_information")
	private int idModuleInformation;
	@Column(name="id_monitor")
	private int idMonitor;
	@Column(name="id_monitor_event_state")
	private int idMonitorEventState;
	@Column(name="state_time_write")
	private Date stateTimeWrite;
	@Column(name="id_monitor_event_resolve")
	private int idMonitorEventResolve;
	@Column(name="resolve_time_write")
	private Date resolveTimeWrite;
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
	public int getIdMonitorEventState() {
		return idMonitorEventState;
	}
	public void setIdMonitorEventState(int idMonitorEventState) {
		this.idMonitorEventState = idMonitorEventState;
	}
	public Date getStateTimeWrite() {
		return stateTimeWrite;
	}
	public void setStateTimeWrite(Date stateTimeWrite) {
		this.stateTimeWrite = stateTimeWrite;
	}
	public int getIdMonitorEventResolve() {
		return idMonitorEventResolve;
	}
	public void setIdMonitorEventResolve(int idMonitorEventResolve) {
		this.idMonitorEventResolve = idMonitorEventResolve;
	}
	public Date getResolveTimeWrite() {
		return resolveTimeWrite;
	}
	public void setResolveTimeWrite(Date resolveTimeWrite) {
		this.resolveTimeWrite = resolveTimeWrite;
	}
	public int getIdModuleInformation() {
		return idModuleInformation;
	}
	public void setIdModuleInformation(int idModuleInformation) {
		this.idModuleInformation = idModuleInformation;
	}
	
	
}
