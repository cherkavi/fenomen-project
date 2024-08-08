package fenomen.module.web_service.common;

import java.io.Serializable;

/** объект, который предоставляет модуль идентификации, другими словами уникальный модуль в масштабе приложения */
public class ModuleIdentifier implements Serializable{
	private final static long serialVersionUID=1L;
	//private IStore store=null;
	//private ArrayList<String> paramName=new ArrayList<String>();
	//private ArrayList<String> paramValue=new ArrayList<String>();
	
	/** уникальный идентификатор модуля в системе*/
	private String id;
	
	/** уникальный адрес датчика в системе */
	private int idSensor;
	
	/** объект, который предоставляет модуль идентификации, другими словами уникальный модуль в масштабе приложения */
	public ModuleIdentifier(){
	}
	
	/** объект, который предоставляет модуль идентификации, другими словами уникальный модуль в масштабе приложения 
	public ModuleIdentifier(IStore store){
		this.store=store;
		store.load(paramName, paramValue);
		boolean update=false;
		if(paramName.indexOf("id")<0){
			paramName.add("id");
			paramValue.add("00");
			update=true;
		}
		// прочесть из хранилища уникальный номер модуля 
		int idPosition=this.paramName.indexOf("id");
		this.id=this.paramValue.get(idPosition);
		if(update){
			store.save(paramName, paramValue);
		}
		
	}*/
	
	/** объект, который предоставляет модуль идентификации, другими словами уникальный модуль в масштабе приложения 
	public ModuleIdentifier(String id){
		this.id=id;
		if(this.store!=null){
			this.store.save(paramName, paramValue);
		}
	}*/
	
	/** получить идентификатор */
	public String getId(){
		return this.id;
	}
	
	/** установить идентификатор */
	public void setId(String id){
		this.id=id;
	}

	/** получить уникальный идентификатор сенсора*/
	public int getIdSensor() {
		return idSensor;
	}

	/** установить уникальный идентификатор сенсора */
	public void setIdSensor(int idSensor) {
		this.idSensor = idSensor;
	}

}
