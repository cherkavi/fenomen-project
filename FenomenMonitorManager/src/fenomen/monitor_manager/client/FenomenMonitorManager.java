package fenomen.monitor_manager.client;

import com.google.gwt.core.client.EntryPoint;

import fenomen.monitor_manager.client.utility.RootComposite;
import fenomen.monitor_manager.client.view.MainMenu;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FenomenMonitorManager implements EntryPoint {
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootComposite.setMain("main");
		RootComposite.showView(new MainMenu());
	}
}
