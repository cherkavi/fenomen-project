package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.value_checker;

public class DoubleCheckValue extends CheckValueForType{

	public DoubleCheckValue() {
		super("Double");
	}

	@Override
	public boolean isType(String value) {
		try{
			Double.parseDouble(value.trim());
			return true;
		}catch(Exception ex){
			return false;
		}
	}

}
