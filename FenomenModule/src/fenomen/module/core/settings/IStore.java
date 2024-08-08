package fenomen.module.core.settings;

import java.util.ArrayList;

public interface IStore {
	public boolean load(ArrayList<String> paramName, ArrayList<String> paramValue);
	public boolean save(ArrayList<String> paramName, ArrayList<String> paramValue);
}
