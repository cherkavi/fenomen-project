package database.wrap;
import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.*;

/** 
 * <table border=1>
 * 	<tr>
 * 		<th>Database Field</th> <th>POJO</th>
 * 	</tr>
 * 	<tr>
 * 		<td>id</td> <td>id</td>
 * 	</tr>
 * 	<tr>
 * 		<td>name</td> <td>name</td>
 * 	</tr>
 * 	<tr>
 * 		<td>xml_directory</td> <td>directory</td>
 * 	</tr>
 * </table>
 * 
 * */
@Entity
@PersistenceCapable(detachable="false")
@Table(name="module_storage")
public class ModuleStorage {
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="xml_directory")
	private String directory;
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
	public String getDirectory() {
		return directory;
	}
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	
}
