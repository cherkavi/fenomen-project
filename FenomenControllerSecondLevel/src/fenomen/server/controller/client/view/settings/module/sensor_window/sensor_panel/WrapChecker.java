package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WrapChecker implements IsSerializable{
	private Integer idEdit;
	private int idModule; 
    private int modbusAddress;
    private Integer registerAddress; 
    private String alarmDescription; 
    private String alarmMessage; 
    private String valuePresentation; 
    private String valueCondition; 
    private String controlValue; 
    private int timeDelay;
	
    public WrapChecker(){
    }
    
    public WrapChecker(Integer idEdit,
			  int idModule, 
		      int modbusAddress,
		      Integer registerAddress, 
		      String alarmDescription, 
		      String alarmMessage, 
		      String valuePresentation, 
		      String valueCondition, 
		      String controlValue, 
		      int timeDelay){
    	this.idEdit=idEdit;
    	this.idModule=idModule;
    	this.modbusAddress=modbusAddress;
    	this.registerAddress=registerAddress;
    	this.alarmDescription=alarmDescription;
    	this.alarmMessage=alarmMessage;
    	this.valuePresentation=valuePresentation;
    	this.valueCondition=valueCondition;
    	this.controlValue=controlValue;
    	this.timeDelay=timeDelay;
    }

	public Integer getIdEdit() {
		return idEdit;
	}

	public void setIdEdit(Integer idEdit) {
		this.idEdit = idEdit;
	}

	public int getIdModule() {
		return idModule;
	}

	public void setIdModule(int idModule) {
		this.idModule = idModule;
	}

	public int getModbusAddress() {
		return modbusAddress;
	}

	public void setModbusAddress(int modbusAddress) {
		this.modbusAddress = modbusAddress;
	}

	public Integer getRegisterAddress() {
		return registerAddress;
	}

	public void setRegisterAddress(Integer registerAddress) {
		this.registerAddress = registerAddress;
	}

	public String getAlarmDescription() {
		return alarmDescription;
	}

	public void setAlarmDescription(String alarmDescription) {
		this.alarmDescription = alarmDescription;
	}

	public String getAlarmMessage() {
		return alarmMessage;
	}

	public void setAlarmMessage(String alarmMessage) {
		this.alarmMessage = alarmMessage;
	}

	public String getValuePresentation() {
		return valuePresentation;
	}

	public void setValuePresentation(String valuePresentation) {
		this.valuePresentation = valuePresentation;
	}

	public String getValueCondition() {
		return valueCondition;
	}

	public void setValueCondition(String valueCondition) {
		this.valueCondition = valueCondition;
	}

	public String getControlValue() {
		return controlValue;
	}

	public void setControlValue(String controlValue) {
		this.controlValue = controlValue;
	}

	public int getTimeDelay() {
		return timeDelay;
	}

	public void setTimeDelay(int timeDelay) {
		this.timeDelay = timeDelay;
	}
    
    
}
