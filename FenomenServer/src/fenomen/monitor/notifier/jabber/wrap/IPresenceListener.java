package fenomen.monitor.notifier.jabber.wrap;

/** ��������� ��������� ���������, � ��������� ��������� ��� ������������ �� ���� ������ ��������� �������� */
public interface IPresenceListener {
	/** ������������ �������� � ����  */
	public void userEnter(String user);
	/** ������������ ������� ����  */
	public void userExit(String user); 
	/** ��������� ��������� �� ������������ - �� ��������� �� ����� ������*/
	public void userError(String user); 
}
