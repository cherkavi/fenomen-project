package fenomen.monitor.menu_settings.heart_beat;

import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import fenomen.monitor.web_service.common.HeartBeatSettingsElement;

/** таблица TableHeartBeat, которая отображает все данные от сервера  */
public class TableHeartBeat extends JTable implements IRepaint{
	private final static long serialVersionUID=1L;
	private TableHeartBeatModel model;
	
	public TableHeartBeat(){
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setCellSelectionEnabled(true);
		this.model=new TableHeartBeatModel();
		this.setModel(this.model);

		// отображение колонок 
		this.getColumn(this.getColumnName(0)).setHeaderRenderer(new ColumnCell("Module"));
		this.getColumn(this.getColumnName(0)).setPreferredWidth(50);
		this.getColumn(this.getColumnName(1)).setHeaderRenderer(new ColumnCell("Module"));
		this.getColumn(this.getColumnName(1)).setPreferredWidth(70);
		this.getColumn(this.getColumnName(2)).setHeaderRenderer(new ColumnCell("Module Address"));
		this.getColumn(this.getColumnName(2)).setPreferredWidth(250);
		this.getColumn(this.getColumnName(3)).setHeaderRenderer(new ColumnCell("Enabled"));
		this.getColumn(this.getColumnName(3)).setPreferredWidth(50);
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
		this.getColumn(this.getColumnName(3)).setCellEditor(new BooleanEditor(this));
		this.getColumn(this.getColumnName(4)).setHeaderRenderer(new ColumnCell("TimeWait"));
		this.getColumn(this.getColumnName(4)).setPreferredWidth(75);
		this.getColumn(this.getColumnName(4)).setCellRenderer(new TableCellRenderer(){
			private JTextField textField=new JTextField();
			{
				textField.setHorizontalAlignment(SwingConstants.CENTER);
			}
			@Override
			public Component getTableCellRendererComponent(JTable table,
														   Object value, 
														   boolean isSelected, 
														   boolean hasFocus, 
														   int row, 
														   int column) {
				if(value instanceof Integer){
					this.textField.setText(((Integer)value).toString());
				}else if(value instanceof String){
					try{
						Integer.parseInt((String)value);
						this.textField.setText((String)value);
					}catch(Exception ex){
						this.textField.setText("0");
					}
				}else{
					System.err.println("value is not integer");
				}
				// проверка на Enabled
				textField.setEnabled((((TableHeartBeatModel)table.getModel()).getData()[row]).isEnabled());
				return textField;
			}
		});
		this.getColumn(this.getColumnName(4)).setCellEditor(new IntegerEditor());

		this.getColumn(this.getColumnName(5)).setHeaderRenderer(new ColumnCell("Settings"));
		this.getColumn(this.getColumnName(5)).setPreferredWidth(60);
		
		// убрать первую колонку в виде уникального идентификатора 
		this.removeColumn(this.getColumn(this.getColumnName(0)));
	}

	/** установить данные для модели  */
	public void setModelData(HeartBeatSettingsElement[] list) {
		this.model.setData(list);
	}

	public HeartBeatSettingsElement[] getModelData() {
		return this.model.getData();
	}

	@Override
	public void mustRepaint() {
		this.repaint();
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
				objectForRepaint.mustRepaint();
			}
		});
	}
	
	private IRepaint objectForRepaint;
	
	public BooleanEditor(IRepaint objectForRepaint){
		this.objectForRepaint=objectForRepaint;
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
/** объект, который представляет из себя редактор для поля JCheckBox */
class IntegerEditor extends AbstractCellEditor implements TableCellEditor{
	private static final long serialVersionUID=1L;
	private JTextField textField=new JTextField();
	{
		this.textField.setHorizontalAlignment(SwingConstants.LEFT);
		this.textField.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {
			}
			@Override
			public void focusLost(FocusEvent arg0) {
				onEndEdit();
			}
		});
	}
	/** была нажата кнопка выбора - остановить редактирование */
	private void onEndEdit(){
		this.stopCellEditing();
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, 
												 Object value,
												 boolean isSelected, 
												 int row, 
												 int column) {
		if(value instanceof Integer){
			this.textField.setText( ((Integer)value).toString());
			return this.textField;
		}else if(value instanceof String){
			this.textField.setText( (String)value);
			return this.textField;
		}else{
			System.err.println("String Editor is not for 'null' values");
			return null;
		}
	}
	
	@Override
	public Object getCellEditorValue() {
		try{
			return Integer.parseInt(this.textField.getText());
		}catch(Exception ex){
			this.textField.setText("0");
			return new Integer(0);
		}
	}
	
}

/** модель данных для таблицы  */
class TableHeartBeatModel extends AbstractTableModel{
	private final static long serialVersionUID=1L;
	private HeartBeatSettingsElement[] data=null;
	@Override
	public int getColumnCount() {
		return 6;
	}

	/** получить установленные данные */
	public HeartBeatSettingsElement[] getData() {
		return this.data;
	}

	/** установить данные для модели */
	public void setData(HeartBeatSettingsElement[] data) {
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
			}if(columnIndex==3){
				return this.data[rowIndex].isEnabled();
			}if(columnIndex==4){
				return this.data[rowIndex].getTimeWait();
			}if(columnIndex==5){
				return this.data[rowIndex].getSettingsValue();
			}else{
				return this.data[rowIndex].getSettingsValue();
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
		if(columnIndex==4){
			int tempValue=0;
			try{
				if(value instanceof Integer){
					tempValue=(Integer)value;
				}
				if(value instanceof String){
					tempValue=Integer.parseInt((String)value);
				}
			}catch(Exception ex){};
			this.data[rowIndex].setTimeWait(tempValue);
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if(columnIndex==3){
			return true;
		}else if(columnIndex==4){
			return this.data[rowIndex].isEnabled();
		}else{
			return false;
		}
	}
}

/** принудительная перерисовка экрана */
interface IRepaint{
	/** принудительная перерисовка экрана */
	public void mustRepaint();
}