package fenomen.server.controller.client.view.settings.module.sensor_window.sensor_panel.alarm_checker_panel.add_checker.panel_create;

import java.util.ArrayList;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.HorizontalLayout;

/** панель, которая представляет в виде RadioButton различные варианты для представления строковой переменной как значения из датчика  */
public class PanelValuePresentation extends Panel{
	private ArrayList<RadioButton> listOfButton=new ArrayList<RadioButton>();
	/** панель, которая представляет в виде RadioButton различные варианты для представления строковой переменной как значения из датчика  
	 * @param radioButtonGroupName - наименование группы ( любая уникальная последовательность символов )
	 * @param values - массив Values  
	 * @param selectedIndex - индекс, который должен быть выделен (-1 - нет выделений )
	 */
	public PanelValuePresentation(String radioButtonGroupName, String[] values, int selectedIndex){
		VerticalPanel panelLabel=new VerticalPanel();
		Label labelValueAs=new Label("Value as:");
		panelLabel.add(labelValueAs);
		
		VerticalPanel panelChoice=new VerticalPanel();
		for(int counter=0;counter<values.length;counter++){
			final RadioButton button=new RadioButton(radioButtonGroupName,values[counter]);
			if(counter==selectedIndex){
				button.setValue(true);
			}
			button.ensureDebugId(radioButtonGroupName);
			button.addValueChangeHandler(new ValueChangeHandler<Boolean>(){
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if(event.getValue().equals(true)){
						buttonSelected(button);
						PanelValuePresentation.this.selectedValue=button;
					}
				}
			});
			panelChoice.add(button);
			this.listOfButton.add(button);
		}
		this.setLayout(new HorizontalLayout(10));
		this.setWidth("100%");
		this.add(panelLabel);
		this.add(panelChoice);
	}
	
	private ArrayList<IValuePresentationListener> listener=new ArrayList<IValuePresentationListener>();
	
	public void addListener(IValuePresentationListener listener){
		this.listener.add(listener);
	}
	
	/** выделенная в данный момент кнопка */
	private RadioButton selectedValue;
	
	/** оповещение о выделении указанного объекта */
	private void buttonSelected(RadioButton button){
		this.selectedValue=button;
		String value=button.getText();
		for(IValuePresentationListener listener: this.listener){
			listener.onValueSelected(value);
		}
	}
	
	/** получить уникальное имя выделенного объекта */
	public String getSelectedName(){
		try{
			return this.selectedValue.getText();
		}catch(Exception ex){
			return null;
		}
	}

	public void setSelectedName(String valuePresentation) {
		for(int counter=0;counter<this.listOfButton.size();counter++){
			this.listOfButton.get(counter).setValue(false);
			if(this.listOfButton.get(counter).getText().equals(valuePresentation)){
				this.listOfButton.get(counter).setValue(true);
			}
		}
	}
	
}
