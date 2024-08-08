package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.value_checker;

public class ZeroOrOneCheckValue extends CheckValueForType{

	public ZeroOrOneCheckValue() {
		super("0 or 1");
	}

	@Override
	public boolean isType(String value) {
		String trimValue=value.trim();
		if(trimValue.equals("0")||trimValue.equals("1")){
			return true;
		}else{
			return false;
		}
	}

}
