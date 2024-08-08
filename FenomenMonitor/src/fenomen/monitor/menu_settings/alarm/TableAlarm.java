package fenomen.monitor.menu_settings.alarm;

import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import fenomen.monitor.web_service.common.AlarmSettingsElement;

/** таблица TableAlarm, которая отображает все данные от сервера  */
public class TableAlarm extends JTable{
	private final static long serialVersionUID=1L;
	private TableAlarmModel model;
	
	public TableAlarm(){
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setCellSelectionEnabled(true);
		this.model=new TableAlarmModel();
		this.setModel(this.model);

		// отображение колонок 
		this.getColumn(this.getColumnName(0)).setHeaderRenderer(new ColumnCell("Module"));
		this.getColumn(this.getColumnName(0)).setPreferredWidth(50);
		this.getColumn(this.getColumnName(1)).setHeaderRenderer(new ColumnCell("Module"));
		this.getColumn(this.getColumnName(1)).setPreferredWidth(70);
		this.getColumn(this.getColumnName(2)).setHeaderRenderer(new ColumnCell("Module Address"));
		this.getColumn(this.getColumnName(2)).setPreferredWidth(300);
		this.getColumn(this.getColumnName(3)).setHeaderRenderer(new ColumnCell("Enabled"));
		this.getColumn(this.getColumnName(3)).setPreferredWidth(70);
		this.getColumn(this.getColumnName(3)).setCellRenderer(new TableCellRenderer(){
			private JCheckBox checkBox=new JCheckBox();
			{
				checkBox.setHorizontalAlignment(SwingConstants.CENTER);
			}
			@Override
			public Component getTableCellRendererComponent(JTable table,
														   Object value, 
														   boolean isSelected, 
														   boolean hasFocus, 
														   int row, 
														   int column) {
				if(value instanceof Boolean){
					checkBox.setSelected((Boolean)value);
				}else{
					System.err.println("value is not boolean");
				}
				return checkBox;
			}
		});
		this.getColumn(this.getColumnName(3)).setCellEditor(new BooleanEditor());
		
		// убрать первую колонку в виде уникального идентификатора 
		this.removeColumn(this.getColumn(this.getColumnName(0)));
	}

	/** установить данные для модели  */
	public void setModelData(AlarmSettingsElement[] list) {
		this.model.setData(list);
	}

	public AlarmSettingsElement[] getModelData() {
		return this.model.getData();
	}
	 
}

/** отображает TableCellRender в виде объекта-константы */
class ColumnCell implements TableCellRenderer{
	private JLabel label=null;
	/** отображает TableCellRender в виде объекта-константы */
	public ColumnCell(String title){
		label=new JLabel(title, SwingConstants.CENTER);
	}
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		return label;
	}
}

/** объект, который представляет из себя редактор для поля JCheckBox */
class BooleanEditor extends AbstractCellEditor implements TableCellEditor{
	private static final long serialVersionUID=1L;
	private JCheckBox checkBox=new JCheckBox();
	{
		this.checkBox.setHorizontalAlignment(SwingConstants.CENTER);
		this.checkBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onButtonClicked();
			}
		});
	}
	/** была нажата кнопка выбора - остановить редактирование */
	private void onButtonClicked(){
		this.stopCellEditing();
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, 
												 Object value,
												 boolean isSelected, 
												 int row, 
												 int column) {
		if(value instanceof Boolean){
			this.checkBox.setSelected((Boolean)value);
			return this.checkBox;
		}else{
			System.err.println("Boolean Editor is not for 'null' values");
			return null;
		}
	}
	
	@Override
	public Object getCellEditorValue() {
		return this.checkBox.isSelected();
	}
	
}

/** модель данных для таблицы  */
class TableAlarmModel extends AbstractTableModel{
	private final static long serialVersionUID=1L;
	private AlarmSettingsElement[] data=null;
	@Override
	public int getColumnCount() {
		return 4;
	}

	/** получить установленные данные */
	public AlarmSettingsElement[] getData() {
		return this.data;
	}

	/** установить данные для модели */
	public void setData(AlarmSettingsElement[] data) {
		this.data=data;
		this.fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		if(data!=null){
			return data.length;
		}else{
			return 0;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		try{
			if(columnIndex==0){
				return this.data[rowIndex].getId();
			}if(columnIndex==1){
				return this.data[rowIndex].getModuleId();
			}if(columnIndex==2){
				return this.data[rowIndex].getModuleAddress();
			}else{
				return this.data[rowIndex].isEnabled();
			}
		}catch(Exception ex){
			return null;
		}
	}
	
	@Override
	public void setValueAt(Object value, 
						   int rowIndex, 
						   int columnIndex) {
		super.setValueAt(value, rowIndex, columnIndex);
		if(columnIndex==3){
			this.data[rowIndex].setEnabled((Boolean)value);
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if(columnIndex==3){
			return true;
		}else{
			return false;
		}
	}
}
