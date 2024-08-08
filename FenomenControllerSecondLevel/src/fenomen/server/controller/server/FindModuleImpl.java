package fenomen.server.controller.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import database.ConnectWrap;
import database.StaticConnector;

import fenomen.server.controller.client.view.modules.table.row.RowData;
import fenomen.server.controller.client.view.settings.FindModules;

/** поиск модулей в системе */
public class FindModuleImpl extends RemoteServiceServlet implements FindModules{
	private final static long serialVersionUID=1L;
	private Logger logger=Logger.getLogger(this.getClass());
	
	@Override
	public RowData[] findModules(RowData findData) {
		RowData[] returnValue=new RowData[]{};
		ConnectWrap connector=StaticConnector.getConnectWrap();
		try{
			Connection connection=connector.getConnection();
			StringBuffer where=new StringBuffer();
			if(findData.getIdModule()!=null){
				if(where.length()>0){
					where.append(" and ");
				}
				where.append("modules.id_module=%"+findData.getIdModule().replaceAll("'", "''")+"% ");
			}
			if(findData.getAddress()!=null){
				if(where.length()>0){
					where.append(" and ");
				}
				where.append("modules.id_module=%"+findData.getAddress().replaceAll("'", "''")+"% ");
			}
			
			String query=null;
			if(where.length()>0){
				query="select * from module where "+where.toString()+" order by id";
			}else{
				query="select * from module order by id";
			}
			//System.out.println("Query:"+query);
			ResultSet rs=connection.createStatement().executeQuery(query);
			ArrayList<RowData> list=new ArrayList<RowData>();
			while(rs.next()){
				list.add(this.getRowDataFromResultSet(rs));
			}
			returnValue=list.toArray(returnValue);
		}catch(Exception ex){
			System.err.println("findModules: "+ex.getMessage());
		}finally{
			connector.close();
		}
		return returnValue;
	}

	/** преобразовать запись из таблицы MODULE в RowData */
	private RowData getRowDataFromResultSet(ResultSet rs){
		RowData returnValue=new RowData();
		try{
			returnValue.setId(rs.getInt("id"));
			returnValue.setIdModule(rs.getString("id_module"));
			returnValue.setAddress(rs.getString("address"));
		}catch(Exception ex){
			logger.error(" getRowDataFromResultSet Exception: "+ex.getMessage());
		}
		return returnValue;
	}
}
