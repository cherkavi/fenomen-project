package fenomen.module.core.sensor;

import java.io.Serializable;

/** ��������� �� ����������/��������������/�������� ����������� � ������� ������ */
public interface ICheckerStorage<T extends Serializable> {
	/** �������� �� ��������� �� ��������� ������� ������ 
	 * @param index - ������ ������� 
	 * @return ������, ���� null
	 */
	public T readFromStorage(int index);
	
	/** ��������� � ��������� ������ � ������� ��� ������ 
	 * @param value - ������, ������� ������ ���� �������� � ���������
	 * @return 
	 * <ul>
	 * 	<li><b>null</b> - ���� ������ �� ��������, </li> 
	 * 	<li><b>not null </b> - ���� �� ���������� ������������� � ��������� �� ������� ������� </li>
	 * </ul>
	 */
	public String saveToStorage(T value);
	
	/** �������� ���-�� �������� � ��������� */
	public int getStorageSize();
	
	/** ������� �� ��������� �� ���������� ������� 
	 * @param index - ������ � ���������
	 * @return true - ������� ������, false - ������ ��������
	 */
	public boolean removeFromStorage(int index);
}
