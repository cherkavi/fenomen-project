package fenomen.module.web_service.common;


/** контейнер для задач от модуля на клиента */
public class ModuleTaskContainer {
	private ModuleTask[] taskStorage=new ModuleTask[]{};
	
	public ModuleTask[] getContent(){
		return this.taskStorage;
	}
	
	public void setContent(ModuleTask[] content){
		this.taskStorage=content;
	}
	
	/** контейнер для задач от модуля на клиента */
	public ModuleTaskContainer(){
	}
	
	
}
