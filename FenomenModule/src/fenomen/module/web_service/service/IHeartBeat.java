package fenomen.module.web_service.service;

import fenomen.module.web_service.common.ModuleIdentifier;

/** ���������, ������� ������������� ������� ��� ������ ������������ */
public interface IHeartBeat {
	public final String sendOk="send_ok";
	public final String sendError="send_error";
	public final String taskExists="task_exists";
	
	/** ������� ������ "������������" �� ������ */	
	public String hearBeat(ModuleIdentifier moduleIdentifier);
}
