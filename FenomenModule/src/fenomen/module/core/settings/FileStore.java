package fenomen.module.core.settings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

/** хранилище для параметров в виде файла на диске */
public class FileStore implements IStore{
	private Logger logger=Logger.getLogger(this.getClass());
	private String pathToFile;
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd HH:mm:ssss");
	
	/** хранилище для параметров в виде файла на диске 
	 * @param pathToFile - полный путь к файлу на диске
	 */
	public FileStore(String pathToFile){
		this.pathToFile=pathToFile;
	}
	
	@Override
	public boolean load(ArrayList<String> paramName,
						ArrayList<String> paramValue) {
		boolean returnValue=false;
		try{
			paramName.clear();
			paramValue.clear();
			Properties properties=new Properties();
			properties.load(new FileInputStream(this.pathToFile));
			Enumeration<Object> keys=properties.keys();
			while(keys.hasMoreElements()){
				try{
					String currentKey=(String)keys.nextElement();
					String currentValue=(String)properties.get(currentKey);
					paramName.add(currentKey);
					paramValue.add(currentValue);
				}catch(Exception ex){
					logger.error("load Exception:"+ex.getMessage());
				}
			}
			returnValue=true;
		}catch(Exception ex){
			logger.error("parameters was not load: "+ex.getMessage());
			returnValue=false;
		}
		return returnValue;
	}

	@Override
	public boolean save(ArrayList<String> paramName,
						ArrayList<String> paramValue) {
		boolean returnValue=false;
		try{
			Properties properties=new Properties();
			for(int counter=0;counter<paramName.size();counter++){
				properties.put(paramName.get(counter), paramValue.get(counter));
			}
			properties.store(new FileOutputStream(this.pathToFile), sdf.format(new Date()));
			returnValue=true;
		}catch(Exception ex){
			logger.error("parameters was not load: "+ex.getMessage());
			returnValue=false;
		}
		return returnValue;
	}

}
