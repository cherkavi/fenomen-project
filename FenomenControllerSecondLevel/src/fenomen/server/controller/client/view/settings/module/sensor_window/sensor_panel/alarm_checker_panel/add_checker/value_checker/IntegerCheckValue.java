package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.value_checker;

public class IntegerCheckValue extends CheckValueForType{

	public IntegerCheckValue() {
		super("Integer");
	}

	@Override
	public boolean isType(String value) {
		try{
			Integer.parseInt(value.trim());
			return true;
		}catch(Exception ex){
			return false;
		}
	}

}
