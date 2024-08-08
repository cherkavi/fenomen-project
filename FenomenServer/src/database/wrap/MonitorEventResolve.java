package database.wrap;

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.*;

/**
 * <table border=1>
 * 	<tr>
 * 		<th>monitor_event_resolve</th>  <th>POJO</th>  <th>Description</th>
 * 	</tr>
 * 	<tr>
 * 		<td>id</td>  <td>id</td>  <td> уникальный идентификатор в базе </td>
 * 	</tr>
 * 	<tr>
 * 		<td>name</td>  <td>name</td>  <td> наименование решения проблемы </td>
 * 	</tr>
 * </table>
 */
@Entity
@PersistenceCapable(detachable="false")
@Table(name="monitor_event_resolve")
public class MonitorEventResolve {
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
		if(name!=null){
			if(name.length()>255){
				this.name=name.substring(0, 254).toUpperCase();
			}else{
				this.name=name.toUpperCase();
			}
		}else{
			this.name=null;
		}
	}
	
	
}
