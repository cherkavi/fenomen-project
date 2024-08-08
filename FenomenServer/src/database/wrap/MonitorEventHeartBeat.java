package database.wrap;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.*;

/**
 * <table border=1>
 * 	<tr>
 * 		<th>monitor_event_heart_beat</th>  <th>POJO</th>  <th>Description</th>
 * 	</tr>
 * 	<tr>
 * 		<td>id</td>  <td>id</td>  <td> ���������� ������������� � ���� </td>
 * 	</tr>
 * 	<tr>
 * 		<td>id_module_heart_beat</td>  <td>idModuleHeartBeat</td>  <td> ��� �� module_heart_beat</td>
 * 	</tr>
 * 	<tr>
 * 		<td>id_monitor</td>  <td>idMonitor</td>  <td> ��� Monitor.id</td>
 * 	</tr>
 * 	<tr>
 * 		<td>id_monitor_event_state</td>  
 * 		<td>idMonitorEventState</td>  
 * 		<td> ��� ���������:
 * 			<ul>
 * 				<li><b>1</b> - ����� �������</li>
 * 				<li><b>2</b> - ����� ��� �������� ����� Jabber </li>
 * 				<li><b>3</b> - ������� �������� </li>
 * 			</ul>
 * 		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>state_time_write</td>  <td>stateTimeWrite</td>  <td> ����� ������ ���� ��������� </td>
 * 	</tr>
 * 	<tr>
 * 		<td>id_monitor_event_resolve</td>  
 * 		<td>idMonitorEventState</td>  
 * 		<td> ��� �������� ����� �� ������� �� ��������:
 * 			<ul>
 * 				<li><b>0</b> - ��� �������� ����� �� ������� ������� </li>
 * 				<li><b> x > 0</b> - ��� �� ������� monitor_event_resolve</li>
 * 			</ul>
 * 		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>resolve_time_write</td>  <td>resolveTimeWrite</td>  <td> ����� ������ ��������� ���������� </td>
 * 	</tr>
 *  <tr>
 *  	<td>id_module</td> <td>idModule</td> <td>������, �� �������� ������ ��� ������ ������, �� �� ������ </td>
 *  </tr>
 * </table>
 */
@Entity
@PersistenceCapable(detachable="false")
@Table(name="monitor_event_heart_beat")
public class MonitorEventHeartBeat {
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;
	@Column(name="id_module_heart_beat")
	private int idModuleHeartBeat;
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
	@Column(name="id_module")
	private int idModule;
	
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
	public int getIdModuleHeartBeat() {
		return idModuleHeartBeat;
	}
	public void setIdModuleHeartBeat(int idModuleHeartBeat) {
		this.idModuleHeartBeat = idModuleHeartBeat;
	}
	public int getIdModule() {
		return idModule;
	}
	public void setIdModule(int idModule) {
		this.idModule = idModule;
	}
}
