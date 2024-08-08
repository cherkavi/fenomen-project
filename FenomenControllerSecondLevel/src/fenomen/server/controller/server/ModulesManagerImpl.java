package fenomen.server.controller.server;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import database.ConnectWrap;
import database.StaticConnector;
import database.wrap.Module;

import fenomen.server.controller.client.view.modules.table.ModulesManager;
import fenomen.server.controller.client.view.modules.table.row.RowData;

public class ModulesManagerImpl extends RemoteServiceServlet implements ModulesManager{
	private final static long serialVersionUID=1L;
	private ArrayList<RowData> listOfData=new ArrayList<RowData>();
	private Logger logger=Logger.getLogger(this.getClass());

	public ModulesManagerImpl(){
		this.fillList();
	}
	
	/** заполнить List данными из базы данных */
	@SuppressWarnings("unchecked")
	private void fillList(){
		this.listOfData.clear();
		ConnectWrap connectWrap=StaticConnector.getConnectWrap();
		try{
			Session session=connectWrap.getSession();
			List<Module> list=(List<Module>)session.createCriteria(Module.class).addOrder(Order.asc("id")).list();
			Iterator<Module> iterator=list.iterator();
			while(iterator.hasNext()){
				Module module=iterator.next();
				listOfData.add(new RowData(module.getId(), module.getIdModule(), module.getAddress()));
			}
		}catch(Exception ex){
			
		}finally{
			connectWrap.close();
		}
	}
	
	@Override
	public RowData[] getAllRowElement() {
		return listOfData.toArray(new RowData[]{});
	}

	@Override
	public String updateRowElement(int id, RowData rowData) {
		String returnValue="element does not found";
		// пройтись по всем элементам, и найти тот, у кого будет искомый id
		for(int counter=0;counter<this.listOfData.size();counter++){
			if(this.listOfData.get(counter).getId()==id){
				// объект найден
				RowData destination=this.listOfData.get(counter);
				destination.setId(rowData.getId());
				destination.setIdModule(rowData.getIdModule());
				destination.setAddress(rowData.getAddress());
				returnValue=this.saveOrUpdateRowData(destination);
				break;
			}
		}
		this.fillList();
		return returnValue;
	}
	
	private Module convertRowDataToModule(RowData rowData){
		Module returnValue=new Module();
		returnValue.setId(rowData.getId());
		returnValue.setIdModule(rowData.getIdModule());
		returnValue.setAddress(rowData.getAddress());
		return returnValue;
	}

	/** обновить запись в базе данных */
	private String saveOrUpdateRowData(RowData rowData){
		String returnValue=null;
		ConnectWrap connectWrap=StaticConnector.getConnectWrap();
		try{
			Session session=connectWrap.getSession();
			session.beginTransaction();
			session.saveOrUpdate(convertRowDataToModule(rowData));
			session.getTransaction().commit();
		}catch(Exception ex){
			returnValue=ex.getMessage();
			logger.error("updateRowData Exception:"+returnValue);
		}finally{
			connectWrap.close();
		}
		return returnValue;
	}
	
	@Override
	public boolean removeRowElement(int id) {
		boolean returnValue=false;
		ConnectWrap connectWrap=StaticConnector.getConnectWrap();
		try{
			Connection connection=connectWrap.getConnection();
			connection.createStatement().executeUpdate("delete from module where id="+id);
			connection.commit();
			returnValue=true;
		}catch(Exception ex){
			returnValue=false;
			logger.error("updateRowData Exception:"+returnValue);
		}finally{
			connectWrap.close();
		}
		this.fillList();
		return returnValue;
	}

	@Override
	public String createRowElement(RowData rowData) {
		rowData.setId(0);
		String returnValue=this.saveOrUpdateRowData(rowData);
		this.fillList();
		return returnValue;
	}

}
