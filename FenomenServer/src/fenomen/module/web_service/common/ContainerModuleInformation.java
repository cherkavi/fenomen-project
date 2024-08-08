package fenomen.module.web_service.common;

import java.io.Serializable;

/** ��������� ��� �������������� ��������� �� ������ �� ������ */
public class ContainerModuleInformation implements Serializable{
	public final static long serialVersionUID=1L;
	private ModuleInformation[] content=new ModuleInformation[]{};
	
	public ContainerModuleInformation(){};
	
	public ContainerModuleInformation(ModuleInformation moduleInformation){
		content=new ModuleInformation[]{moduleInformation};
	}
	
	/** �������� ���������� ���������� */
	public ModuleInformation[] getContent() {
		return content;
	}
	
	/** ���������� ���������� ���������� */
	public void setContent(ModuleInformation[] content) {
		this.content = content;
	}
	
}
