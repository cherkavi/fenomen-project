package fenomen.module.web_service.common;

import java.io.Serializable;

/** ������, ������� ������������� ������ �������������, ������� ������� ���������� ������ � �������� ���������� */
public class ModuleIdentifier implements Serializable{
	private final static long serialVersionUID=1L;
	//private IStore store=null;
	//private ArrayList<String> paramName=new ArrayList<String>();
	//private ArrayList<String> paramValue=new ArrayList<String>();
	
	/** ���������� ������������� ������ � �������*/
	private String id;
	
	/** ���������� ����� ������� � ������� */
	private int idSensor;
	
	/** ������, ������� ������������� ������ �������������, ������� ������� ���������� ������ � �������� ���������� */
	public ModuleIdentifier(){
	}
	
	/** ������, ������� ������������� ������ �������������, ������� ������� ���������� ������ � �������� ���������� 
	public ModuleIdentifier(IStore store){
		this.store=store;
		store.load(paramName, paramValue);
		boolean update=false;
		if(paramName.indexOf("id")<0){
			paramName.add("id");
			paramValue.add("00");
			update=true;
		}
		// �������� �� ��������� ���������� ����� ������ 
		int idPosition=this.paramName.indexOf("id");
		this.id=this.paramValue.get(idPosition);
		if(update){
			store.save(paramName, paramValue);
		}
		
	}*/
	
	/** ������, ������� ������������� ������ �������������, ������� ������� ���������� ������ � �������� ���������� 
	public ModuleIdentifier(String id){
		this.id=id;
		if(this.store!=null){
			this.store.save(paramName, paramValue);
		}
	}*/
	
	/** �������� ������������� */
	public String getId(){
		return this.id;
	}
	
	/** ���������� ������������� */
	public void setId(String id){
		this.id=id;
	}

	/** �������� ���������� ������������� �������*/
	public int getIdSensor() {
		return idSensor;
	}

	/** ���������� ���������� ������������� ������� */
	public void setIdSensor(int idSensor) {
		this.idSensor = idSensor;
	}

}
