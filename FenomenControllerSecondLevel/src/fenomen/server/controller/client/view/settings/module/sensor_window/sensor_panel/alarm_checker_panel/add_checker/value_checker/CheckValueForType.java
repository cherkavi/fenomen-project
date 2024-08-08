package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.value_checker;

/** проверка введенных значений на валидность указанному типу ( возможно ли преобразовать из String в какое-либо значение )*/
public abstract class CheckValueForType {
	private String description;
	
	public CheckValueForType(String description){
		this.description=description;
	}
	
	/** является ли переданное значение соответствием */
	public abstract boolean isType(String value);
	
	public String getDescription(){
		return description;
	}
}
