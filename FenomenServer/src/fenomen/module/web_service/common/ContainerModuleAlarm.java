package fenomen.module.web_service.common;

import java.io.Serializable;

/** контейнер тревожных сообщений от модуля на сервер */
public class ContainerModuleAlarm implements Serializable {
	public static final long serialVersionUID=1L;
	private ModuleAlarm[] content=new ModuleAlarm[]{};
	
	/** контейнер тревожных сообщений от модуля на сервер */
	public ContainerModuleAlarm(){
		content=new ModuleAlarm[]{};
	}

	/** получить содержимое объекта  */
	public ModuleAlarm[] getContent(){
		return this.content;
	}
	
	/** установить содержимое объекта */
	public void setContent(ModuleAlarm[] content){
		this.content=content;
	}
	
}
