package fenomen.module.web_service.common;

import java.io.Serializable;

/** контейнер для информационных сообщений от модуля на сервер */
public class ContainerModuleInformation implements Serializable{
	public final static long serialVersionUID=1L;
	private ModuleInformation[] content=new ModuleInformation[]{};
	
	public ContainerModuleInformation(){};
	
	public ContainerModuleInformation(ModuleInformation moduleInformation){
		content=new ModuleInformation[]{moduleInformation};
	}
	
	/** получить содержимое контейнера */
	public ModuleInformation[] getContent() {
		return content;
	}
	
	/** установить содержимое контейнера */
	public void setContent(ModuleInformation[] content) {
		this.content = content;
	}
	
}
