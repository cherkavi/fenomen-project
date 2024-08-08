package fenomen.module.web_service.service_implementation.information_handler;

import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import database.ConnectWrap;
import database.StaticConnector;

import fenomen.module.web_service.common.ModuleIdentifier;
import fenomen.module.web_service.common.ModuleInformation;

/** ���������� ��� ���������� �� ������ �������� ThreadHeartBeat */
public class InformationHandlerSettingsHeartBeat extends InformationHandler{
	/** ��� ��������� � ���� ������, � ������� module_settings_parameter */
	private final String idTimeWait="time_wait";
	/** ��� ��������� � ���� ������, � ������� module_settings_parameter */
	private final String idTimeError="time_error";
	
	/** ��� ������ */
	private Integer kodSection=null;
	/** ��� ��������� ����� �������� */
	private int kodTimeWait=0;
	/** ��� ��������� ����� ��������� �������� ����� ������ */
	private int kodTimeError=0;
	
	/** ���������� ��� ���������� �� ������ �������� ThreadHeartBeat */
	public InformationHandlerSettingsHeartBeat(){
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			// �������� ��� ������
			kodSection=this.getSectionKod(connector.getSession(), this.idInformation);
			// �������� ��� ��������� TimeWait
			kodTimeWait=this.getParameterKod(connector.getSession(), kodSection, idTimeWait);
			// �������� ��� ��������� TimeError
			kodTimeError=this.getParameterKod(connector.getSession(), kodSection, idTimeError);
		}catch(Exception ex){
			logger.error(" constructor Exception: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}

	@Override
	protected boolean isValid(ModuleInformation moduleInformation) {
		try{
			return (this.getNode(this.getXmlFromString(moduleInformation.getContent()), this.getXPathToTask(false)+"/heart_beat")!=null);
		}catch(Exception ex){
			return false;
		}
	}

	@Override
	protected void process(ModuleIdentifier moduleIdentifier, ModuleInformation moduleInformation) {
		ConnectWrap connector=StaticConnector.getConnectWrap();
		Document document=this.getXmlFromString(moduleInformation.getContent());
		try{
			String timeWait=((Element)this.getNode(document, this.getXPathToTask(false)+"/heart_beat/time_wait")).getTextContent().trim();
			// ��������� ���������� �������� 
			String timeError=((Element)this.getNode(document, this.getXPathToTask(false)+"/heart_beat/time_error")).getTextContent().trim();
			// ����� ������ ���������� 
			Date dateWrite=new Date();
			Integer idModule=this.getModuleIdFromIdentifier(connector.getSession(), moduleIdentifier);
			// ��������� ���������� ��������
			this.saveParameter(connector.getSession(), idModule, this.kodSection, this.kodTimeWait, timeWait, dateWrite);
			// ��������� ���������� ��������
			this.saveParameter(connector.getSession(), idModule, this.kodSection, this.kodTimeError, timeError, dateWrite);
		}catch(Exception ex){
			logger.error("process Error: "+ex.getMessage());
		}finally{
			connector.close();
		}
	}

}
