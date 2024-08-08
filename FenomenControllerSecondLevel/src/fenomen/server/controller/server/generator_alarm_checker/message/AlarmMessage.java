package fenomen.server.controller.server.generator_alarm_checker.message;
import java.util.Date;


public class AlarmMessage {
	private Date eventDate;
	private String description;
	private String value;
	private int registerAddress;
	
	public AlarmMessage(){
	}
	
	public AlarmMessage(int registerAddress, Date eventDate, String description, String value){
		this.registerAddress=registerAddress;
		this.eventDate=eventDate;
		this.description=description;
		this.value=value;
	}

	/**
	 * @return the eventDate
	 */
	public Date getEventDate() {
		return eventDate;
	}

	/**
	 * @param eventDate the eventDate to set
	 */
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	public int getRegisterAddress() {
		return registerAddress;
	}

	public void setRegisterAddress(int registerAddress) {
		this.registerAddress = registerAddress;
	}
}
