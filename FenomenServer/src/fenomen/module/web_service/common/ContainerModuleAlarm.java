package fenomen.module.web_service.common;

import java.io.Serializable;

/** ��������� ��������� ��������� �� ������ �� ������ */
public class ContainerModuleAlarm implements Serializable {
	public static final long serialVersionUID=1L;
	private ModuleAlarm[] content=new ModuleAlarm[]{};
	
	/** ��������� ��������� ��������� �� ������ �� ������ */
	public ContainerModuleAlarm(){
		content=new ModuleAlarm[]{};
	}

	/** �������� ���������� �������  */
	public ModuleAlarm[] getContent(){
		return this.content;
	}
	
	/** ���������� ���������� ������� */
	public void setContent(ModuleAlarm[] content){
		this.content=content;
	}
	
}
