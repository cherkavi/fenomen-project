package fenomen.module.web_service.common;


/** ��������� ��� ����� �� ������ �� ������� */
public class ModuleTaskContainer {
	private ModuleTask[] taskStorage=new ModuleTask[]{};
	
	public ModuleTask[] getContent(){
		return this.taskStorage;
	}
	
	public void setContent(ModuleTask[] content){
		this.taskStorage=content;
	}
	
	/** ��������� ��� ����� �� ������ �� ������� */
	public ModuleTaskContainer(){
	}
	
	
}
