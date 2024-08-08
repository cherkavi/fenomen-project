package fenomen.monitor_manager.client.view.monitor_list;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

/** панель, которая содержит один монитор  */
public class MonitorPanel extends Composite{
	private MonitorList parent;
	private MonitorWrap monitor;
	/** панель, которая содержит один монитор  
	 * @param parent - родительский объект, которому следует передавать управление при нажатии на кнопки 
	 * @param monitor - объект, который будет передан родительскому как аргумент 
	 * @param buttonEditCaption - заголовок для кнопки "Редактировать"
	 * @param buttonRemoveCaption - заголовок для кнопки "Удалить"
	 * @param height - высота панели 
	 * @param width - ширина панели
	 */
	public MonitorPanel(MonitorList parent, 
						MonitorWrap monitor, 
						String buttonEditCaption, 
						String buttonRemoveCaption, 
						int height, 
						int width){
		this.parent=parent;
		this.monitor=monitor;
		Panel panel=new Panel();
		panel.setMargins(2);
		panel.setLayout(new BorderLayout());
		if(height>0){
			panel.setHeight(height);
		}
		if(width>0){
			panel.setWidth(width);
		}
		
		Button buttonEdit=new Button(buttonEditCaption);
		buttonEdit.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonEdit();
			}
		});
		Button buttonRemove=new Button(buttonRemoveCaption);
		buttonRemove.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				onButtonRemove();
			}
		});
		
		panel.add(buttonEdit, new BorderLayoutData(RegionPosition.WEST));
		panel.add(new Label(this.monitor.getDescription()),new BorderLayoutData(RegionPosition.CENTER));
		panel.add(buttonRemove,new BorderLayoutData(RegionPosition.EAST));
		this.initWidget(panel);
	}
	
	private void onButtonEdit(){
		this.parent.editMonitor(monitor);
	}
	
	private void onButtonRemove(){
		this.parent.removeMonitor(monitor);
	}
}
