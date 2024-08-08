package fenomen.server.controller.client.view.settings.module.thread_settings_window;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ModuleSettingsManagerAsync {

	void getListOfParameters(int idModule, int idSection,
			AsyncCallback<ModuleSettingsWrap[]> callback);

	void saveOrUpdateParameter(int idModule, int idSection, int idParameter,
			String value, AsyncCallback<String> callback);

	void saveOrUpdateParameter(int idModule, int idSection,  ModuleSettingsWrap[] parameters,
			AsyncCallback<String> callback);

}
