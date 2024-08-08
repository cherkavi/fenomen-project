package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.value_checker;

public class StringCheckValue extends CheckValueForType {

	public StringCheckValue() {
		super("String");
	}

	@Override
	public boolean isType(String value) {
		return true;
	}

}
