package fenomen.server.controller.client.utility;

import java.util.ArrayList;

/** объект который служит для добавления, удаления и оповещения объектов, реализующих класс {@link INotifyClose} */
public class NotifyCloseContainer {
	private ArrayList<INotifyClose> list=new ArrayList<INotifyClose>();
	
	/** добавить объект, который будет оповещаться о закрытии */
	public void addNotifyClose(INotifyClose notifyClose){
		this.list.add(notifyClose);
	}
	
	/** удалить объект из списка оповещения */
	public void removeNotifyClose(INotifyClose notifyClose){
		this.list.remove(notifyClose);
	}
	
	/** 
	 * <lo>оповестить все объекты из списка оповещения о закрытии родителя или данного объекта </lo> 
	 * <lo>удалить все объекты из контейнера </lo>*/
	@SuppressWarnings("unchecked")
	public void notifyAllObjectAndRemoveThey(){
		/** нужен клон объекта, потому что один из агрегированных объетов может вызвать удаление себя же из родительского контейнера  */
		ArrayList<INotifyClose> cloneList=(ArrayList<INotifyClose>)this.list.clone();
		for(INotifyClose object:cloneList){
			try{
				object.notifyClose();
			}catch(Exception ex){
				// System.err.println("Notify Close Exception:"+ex.getMessage());
			};
		}
		this.list.clear();
	}
}
