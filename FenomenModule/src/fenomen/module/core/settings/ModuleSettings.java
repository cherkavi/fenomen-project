package fenomen.module.core.settings;

import java.util.ArrayList;

/** ��������� ������, ������� �������� ����������� ���������� */
public class ModuleSettings {
	/** ��� ������ �������� "������������" - ��������� �������� ����� ����������� ��������� */
	public static final String heartBeatTimeWait="heartBeatTimeWait";
	/** ��� ������ �������� "������������" - ����� ��������� ������� ����� ���� �������� */
	public static final String heartBeatTimeError="heartBeatTimeError";
	
	/** ��� ������ �������� �������������� ��������� - ��������� �������� ����� ����������� ��������� */
	public static final String informationTimeWait = "informationTimeWait";
	/** ��� ������ �������� �������������� ��������� - ����� ��������� ������� ����� ���� �������� */
	public static final String informationTimeError="informationTimeError";
	/** ������������ ���-�� �������, ������� ����� ��������� � ������� ��� �������� �� ������, ��� ��� ������ - ������������ ������ */
	public static final String informationMaxInformationCount = "informationMaxCount";
	
	/** ��� ������ �������� �������������� ��������� - ��������� �������� ����� ����������� ��������� */
	public static final String alarmTimeWait = "alarmTimeWait";
	/** ��� ������ �������� �������������� ��������� - ����� ��������� ������� ����� ���� �������� */
	public static final String alarmTimeError="alarmTimeError";
	/** ������������ ���-�� �������, ������� ����� ��������� � ������� ��� �������� �� ������, ��� ��� ������ - ������������ ������ */
	public static final String alarmMaxInformationCount = "alarmMaxCount";

	/** ��� ������ ��������� ������� �� ������� - ��������� �������� ����� ��������� �������� */
	public static final String taskTimeWait = "taskTimeWait";
	/** ��� ������ ��������� ������� �� ������� - ��������� ������� ����� ����� ������ ����� � ��������*/
	public static final String taskTimeError="taskTimeError";

	/** ��� ������ ������ � ��������� - ��������� �������� ��� ���������� ��������� ���������� �� �������� */
	public static final String sensorTimeWait="sensorTimeWait";
	
	/** �����������-��������� ����� � ���� Modbus ��� ������������ ���������� ������ */
	public static final String maxModbusAddress="maxModbusAddress";
	
	/** ����� ���������� */
	private ArrayList<String> parameterName=new ArrayList<String>();
	/** ��������������� ������ �������� */
	private ArrayList<String> parameterValue=new ArrayList<String>();
	/** ��������� */
	private IStore store;
	
	/** ��������� ������, ������� �������� ����������� ���������� */
	public ModuleSettings(IStore store){
		// ��������� ��������� ������ �� ������� ������ ( �����/���� ������/�������� ���������� )
		this.store=store;
		this.store.load(parameterName, parameterValue);
		// ��������� �� ������� ����������, � ��������� ��������� �� ���������, � ������ �� ���������� �������
		boolean noNeedToSave=true;
		noNeedToSave&=this.checkParameter(heartBeatTimeWait, "30000"); 
		noNeedToSave&=this.checkParameter(heartBeatTimeError,"30000");
		noNeedToSave&=this.checkParameter(informationTimeWait,"5000");
		noNeedToSave&=this.checkParameter(informationTimeError,"30000");
		noNeedToSave&=this.checkParameter(informationMaxInformationCount,"5");
		noNeedToSave&=this.checkParameter(alarmTimeWait,"5000");
		noNeedToSave&=this.checkParameter(alarmTimeError,"30000");
		noNeedToSave&=this.checkParameter(alarmMaxInformationCount,"5");
		noNeedToSave&=this.checkParameter(taskTimeWait,"5000");
		noNeedToSave&=this.checkParameter(taskTimeError,"30000");
		noNeedToSave&=this.checkParameter(sensorTimeWait,"1000");
		noNeedToSave&=this.checkParameter(maxModbusAddress,"20");
		if(noNeedToSave==false){
			store.save(parameterName, parameterValue);
		}
	}
	
	/** ��������� �������� �� ����� � ���������� ��� � ������ �� ���������� 
	 * @param parameterName - ��� ��������� 
	 * @param valueIfNotExists - �������� ���������, ���� ������� �� ������
	 * @return 
	 * <li><b>true</b> - �������� ������������</li>
	 * <li><b>false</b> - �������� ����������� </li> 
	 */
	private boolean checkParameter(String parameterName, String valueIfNotExists){
		boolean returnValue=true;
		if(this.parameterName.indexOf(parameterName)<0){
			this.parameterName.add(parameterName);
			this.parameterValue.add(valueIfNotExists);
			returnValue=false;
		}
		return returnValue;
	}
	
	/** ���������� �������� 
	 * @param paramName - ��� ���������
	 * @param paramValue - �������� ���������
	 */
	public void setParameter(String paramName, String paramValue){
		this.setParameter(paramName, paramValue, true);
	}

	/** ���������� �������� 
	 * @param paramName - ��� ��������� 
	 * @param paramValue - �������� ���������
	 * @param storeSave - ������������� ���������� ������� ���������� � ��������� 
	 */
	public void setParameter(String paramName, String paramValue, boolean storeSave){
		int index=this.parameterName.indexOf(paramName);
		if(index>=0){
			 // ������ �������� ��� ����� - ��������
			this.parameterValue.set(index, paramValue);
		}else{
			// �������� ��������
			this.parameterName.add(paramName);
			this.parameterValue.add(paramValue);
		}
		if(storeSave==true){
			this.store.save(parameterName, parameterValue);
		}
	}
	
	public boolean saveSettingsInStore(){
		return this.store.save(parameterName, parameterValue);
	}
	
	
	/** �������� �������� �� ����� */
	public String getParameter(String paramName){
		int index=this.parameterName.indexOf(paramName);
		if(index>=0){
			return this.parameterValue.get(index);
		}else{
			return null;
		}
	}
	
	/** �������� �������� � ���� ������ ����� 
	 * @param paramName -��� ��������� 
	 * @param defaultValue - ��������, � ������ �� ���������� ��������� 
	 * @return
	 */
	public long getParameterAsLong(String paramName, long defaultValue){
		long returnValue=defaultValue;
		try{
			returnValue=Long.parseLong(this.getParameter(paramName));
		}catch(Exception ex){
			returnValue=defaultValue;
		}
		return returnValue;
	}

	/** �������� �������� � ���� ������ ����� 
	 * @param paramName - ��� ��������� 
	 * @return ������������ �������� 
	 */
	public long getParameterAsLong(String paramName){
		long defaultValue=0;
		long returnValue=defaultValue;
		try{
			returnValue=Long.parseLong(this.getParameter(paramName));
		}catch(Exception ex){
			returnValue=defaultValue;
		}
		return returnValue;
	}

}
