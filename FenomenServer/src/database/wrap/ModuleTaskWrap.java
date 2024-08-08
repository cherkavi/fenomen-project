package database.wrap;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.*;

/** 
 * <table border=1>
 * 	<tr>
 * 		<th> Database</th>  <th> POJO </th> <th> Description </th>d
 * 	</tr>
 * 	<tr>
 * 		<td> id </td> <td>id </td> <td> Таблица для выдачи Task каждому модулю </td>
 * 	</tr>
 * 	<tr>
 * 		<td>id_module </td> <td> idModule</td> <td>уникальный идентификатор модуля  </td>
 * 	</tr>
 * 	<tr>
 * 		<td> id_storage </td> <td> idStorage</td> <td>уникальный идентификатор в хранилище задач для модуля </td>
 * 	</tr>
 * 	<tr>
 * 		<td> id_state </td> <td> idState</td> <td> состояние задачи:
											<li>0 - новая задача </li>
											<li>1 - задача была выдана модулю </li>	
											<li>2 - задача была отработана модулем</li> </td>	
 * 	</tr>
 * 	<tr>
 * 		<td> time_write </td> <td> timeWrite</td> <td> время записи/создания</td>
 * 	</tr>
 * 	<tr>
 * 		<td> id_result </td> <td> idResult</td> <td> результат выполнения задачи:
											<li>0 - задача в неопределенном состоянии</li>
											<li>1 - задача выполнена</li>
											<li>2 - задача не выполнена - задача не найдена</li>
											<li>3 - задача не выполнена - ошибка выполнения</li></td>
 * 	</tr>
 * </table>
 * 
 */
@Entity
@PersistenceCapable(detachable="false")
@Table(name="module_task")
public class ModuleTaskWrap {
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;
	@Column(name="id_module")
	private int idModule;
	@Column(name="id_storage",length=255)
	private String idStorage;
	@Column(name="id_state")
	private int idState;
	@Column(name="id_result")
	private int idResult;
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
	public String getIdStorage() {
		return idStorage;
	}
	public void setIdStorage(String idStorage) {
		this.idStorage = idStorage;
	}
	public int getIdState() {
		return idState;
	}
	public void setIdState(int idState) {
		this.idState = idState;
	}
	public int getIdResult() {
		return idResult;
	}
	public void setIdResult(int idResult) {
		this.idResult = idResult;
	}
	public Date getTimeWrite() {
		return timeWrite;
	}
	public void setTimeWrite(Date timeWrite) {
		this.timeWrite = timeWrite;
	}
}
