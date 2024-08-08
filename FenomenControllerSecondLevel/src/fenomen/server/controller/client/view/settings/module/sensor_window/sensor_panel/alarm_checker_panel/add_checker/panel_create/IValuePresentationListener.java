package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.panel_create;

/** слушатель для выделения одного из объектов на панели */
public interface IValuePresentationListener {
	/** оповещение о выделении объекта
	 * @param name - имя объекта 
	 */
	public void onValueSelected(String name);
}
