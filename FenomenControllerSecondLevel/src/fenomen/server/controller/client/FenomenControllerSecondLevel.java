package fenomen.server.controller.client;

import com.google.gwt.core.client.EntryPoint;

import fenomen.server.controller.client.view.RootComposite;
import fenomen.server.controller.client.view.main_menu.MainMenu;

// import com.google.gwt.core.client.GWT;
// import com.google.gwt.event.dom.client.ClickEvent;
// import com.google.gwt.user.client.rpc.AsyncCallback;
// import com.google.gwt.user.client.ui.Button;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FenomenControllerSecondLevel implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootComposite.setMain("main");
		RootComposite.showView(new MainMenu());
	}
}
