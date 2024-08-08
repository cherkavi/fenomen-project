package fenomen.module.web_service.service_implementation.storage;

public interface IStorage<T> {
	/** ��������� ������ 
	 * @param object - ������, ������� ������ ���� �������� � ��������� ( Serializable ) 
	 * @return - ���������� ������������� ������������ ��������
	 * @throws  ����������� ����������, ���� �� ������� ��������� 
	 */
	public String save(T object) throws Exception ;
	
	/**
	 * �������� ������ �� ���������  
	 * @param identifier ���������� ������������� �������
	 * @return ������, ������� �������� �� ��������� ( instanceof Serializable ) 
	 * @throws ���� �� ������� �������� �������� 
	 */
	public T read(String identifier) throws Exception ;
}
