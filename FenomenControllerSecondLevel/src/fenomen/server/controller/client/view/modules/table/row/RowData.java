package fenomen.server.controller.client.view.modules.table.row;

import com.google.gwt.user.client.rpc.IsSerializable;

/** элемент, который представляет объект-обертку для модуля */
public class RowData implements IsSerializable{
	/** уникальный идентификатор */
	private int id;
	/** идентификатор */
	private String idModule;
	/** адрес */
	private String address;
	
	/** элемент, который представляет объект-обертку для модуля */
	public RowData(){
	}
	
	/** элемент, который представляет объект-обертку для модуля 
	 * @param id - уникальный идентификатор
	 * @param idModule - уникальный идентификатор модуля 
	 * @param address - адрес 
	 */
	public RowData(int id, String idModule, String address){
		this.id=id;
		this.idModule=idModule;
		this.address=address;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	

	@Override
	public String toString(){
		return "Id:"+id+"  Module: "+idModule+"   Address:"+address;
	}

	public String getIdModule() {
		return idModule;
	}

	public void setIdModule(String idModule) {
		this.idModule = idModule;
	}
	
	
}
