package fenomen.monitor.menu_notifier.jabber.message_table;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import fenomen.monitor.menu_notifier.MenuNotifier;
import fenomen.monitor.menu_notifier.jabber.message_table.resolve_window.ResolveWindow;
import fenomen.monitor.web_service.common.XmlMessage;

import swing_framework.messages.IMessageSender;

/** таблица, которая содержит все сообщения, которые были получены от удаленного сервера  */
public class TableOfMessage extends JTable {
	private final static long serialVersionUID=1L;
	
	private void debug(Object message){
		System.out.println("DEBUG TableOfMessage:"+message);
	}

	private void error(Object message){
		System.err.println("DEBUG TableOfMessage:"+message);
	}
	
	/** родительское окно, на основании которого следует строить все окна */
	private IMessageSender parentWindow;
	private TableModel tableModel=new TableModel();
	private MenuNotifier menuNotifier;
	/** таблица, которая содержит все сообщения, которые были получены от удаленного сервера  
	 * @param menuNotifier - родительский компонент 
	 * @param messageSender - родительское окно, от имени которого "поднимать" дочерние окна 
	 */
	public TableOfMessage(MenuNotifier menuNotifier, IMessageSender messageSender){
		this.menuNotifier=menuNotifier;
		this.parentWindow=messageSender;
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// this.setCellSelectionEnabled(true);
		this.tableModel=new TableModel();
		this.setModel(tableModel);
		this.setDefaultRenderer(Object.class, new TableRender(this.tableModel));
		this.getColumn(this.getColumnName(0)).setHeaderRenderer(new ColumnCell("Id"));
		this.getColumn(this.getColumnName(0)).setPreferredWidth(70);
		this.getColumn(this.getColumnName(1)).setHeaderRenderer(new ColumnCell("Тип"));
		this.getColumn(this.getColumnName(1)).setPreferredWidth(70);
		this.getColumn(this.getColumnName(2)).setHeaderRenderer(new ColumnCell("время"));
		this.getColumn(this.getColumnName(2)).setPreferredWidth(100);
		this.getColumn(this.getColumnName(3)).setHeaderRenderer(new ColumnCell("модуль.имя"));
		this.getColumn(this.getColumnName(3)).setPreferredWidth(100);
		this.getColumn(this.getColumnName(4)).setHeaderRenderer(new ColumnCell("модуль.адрес"));
		this.getColumn(this.getColumnName(4)).setPreferredWidth(200);
		this.getColumn(this.getColumnName(5)).setHeaderRenderer(new ColumnCell("описание"));
		this.getColumn(this.getColumnName(5)).setPreferredWidth(200);

		// убрать первую колонку в виде уникального идентификатора 
		this.removeColumn(this.getColumn(this.getColumnName(0)));
		
		this.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent event) {
				if((event.getButton()==MouseEvent.BUTTON1)&&(event.getClickCount()>1)){
					onDblClick();
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {}
		});
	}

	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	
	/** реакция на двойное нажатие кнопки мыши в выделенной области */
	private void onDblClick(){
		MessageElement selectedElement=this.tableModel.getValue(this.convertRowIndexToModel(this.getSelectedRow()));
		if(selectedElement!=null){
			String caption=selectedElement.getData().getType()+"  "+sdf.format(selectedElement.getData().getTimeWrite());
			new ResolveWindow(this, this.parentWindow, selectedElement, caption);
		}
	}

	/** дочернее окно передало решение одного из Event  
	 * @param value - значение, по которому принято решение  
	 * @param data решение, которое нужно передать как значение 
	 */
	public void resolveEvent(MessageElement data, String value){
		XmlMessage message=new XmlMessage(data.getData().getType()){};
		message.setId(data.getData().getId());
		
		message.setFlagResolve(true);
		message.setResolveId(0);
		message.setResolveName(value);
		try{
			String serverExchange=this.menuNotifier.sendToServer(message.convertToXml());
			if(serverExchange==null){
				debug("запись сообщения на сервер прошла успешно, изменить строку на 'ожидающее доставки' ");
				data.setProcess(true);
				this.tableModel.fireTableDataChanged();
				this.repaint();
				//this.tableModel.removeElement(data);
				//this.tableModel.fireTableDataChanged();
			}else{
				JOptionPane.showMessageDialog(this, serverExchange,"Ошибка передачи данных", JOptionPane.WARNING_MESSAGE);
			}
		}catch(Exception ex){
			JOptionPane.showMessageDialog(this, "ошибка алгоритма","Ошибка передачи данных", JOptionPane.WARNING_MESSAGE);			
		}
	}
	
	/** получить сообщение от сервера, обработать это сообщение и вернуть ответ, который следует передать  */
	public String inputMessageFromServer(String text){
		XmlMessage message=XmlMessage.getInstanceByMessage(text);
		// INFO монитор.input message
		if(message!=null){
			String returnValue=null;
			debug("получено входящее сообщение от сервера: "+text);
			if((message.isFlagNotify())&&(tableModel.checkForExists(message)==false)){
				debug("сообщение является уведомлением о новом событии ");
				// создать панель и поместить эту панель в таблицу
				MessageElement messageElement=new MessageElement(message);
				tableModel.addElement(messageElement);
				tableModel.fireTableDataChanged();
				XmlMessage messageConfirm=new XmlMessage(message.getType()){};
				messageConfirm.setId(message.getId());
				messageConfirm.setFlagConfirm(true);
				try{
					returnValue=messageConfirm.convertToXml();
				}catch(Exception ex){
					return null;
				}
			} else if(message.isFlagConfirm()){
				debug("событие является подтверждением доставки оповещения о решении проблемы ");
				if(tableModel.removeElement(tableModel.getMessageElementByXmlMessage(message))){
					this.tableModel.fireTableDataChanged();
					debug("элемент успешно удален:"+message.getId());
				}else{
					error("ошибка удаления данных ");
				}
			} else {
				error("неизвестный флаг доставки ");
			}
			return returnValue;
		}else{
			debug("не удалось расшифровать сообщение:"+text);
			return null;
		}
	}
}

/** объект, который занимается отображением строк и столбцов  */
class TableRender implements TableCellRenderer{
	private TableModel model;
	private SimpleDateFormat sdf=new SimpleDateFormat("MM.dd HH:mm:ss");

	private Color colorAlarmSelected=new Color(255,100,100);
	private Color colorAlarmFocused=new Color(180,0,0);
	private Color colorAlarmNormal=new Color(255,0,0);
	
	private Color colorInformationSelected=new Color(100,255,255);
	private Color colorInformationFocused=new Color(0,150,150);
	private Color colorInformationNormal=new Color(0,255,255);
	
	private Color colorRestartSelected=new Color(100,255,100);
	private Color colorRestartFocused=new Color(0,120,0);
	private Color colorRestartNormal=new Color(0,255,0);
	
	private Color colorHeartBeatSelected=new Color(255,255,100);
	private Color colorHeartBeatFocused=new Color(180,180,0);
	private Color colorHeartBeatNormal=new Color(255,255,0);
	
	private Color colorUnknownSelected=new Color(230,230,230);
	private Color colorUnknownFocused=new Color(170,170,170);
	private Color colorUnknownNormal=new Color(190,190,190);

	private void debug(Object message){
		System.out.println("TableOfMessage.TableRender: "+message);
	}
	
	/** объект, который занимается отображением строк и столбцов  */
	public TableRender(TableModel model){
		this.model=model;
	}
	@Override
	public Component getTableCellRendererComponent(JTable table, 
												   Object value,
												   boolean isSelected, 
												   boolean hasFocus, 
												   int row, 
												   int column) {
		JLabel label=null;
		if(value instanceof String){
			label=new JLabel((String)value);
		}else if (value instanceof Date){
			label=new JLabel(sdf.format((Date)value));
		}else if (value instanceof Integer){
			label=new JLabel(((Integer)value).toString());
		}else{
			if(value!=null){
				debug("неизвестный тип для преобразований ");
				label=new JLabel(value.toString());
			}else{
				label=new JLabel("");
			}
		}
		JPanel panel=new JPanel(new GridLayout(1,1));
		panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 3, 0, 0));
		panel.add(label);
		MessageElement currentElement=model.getValue(row);
		if(currentElement.isProcess()){
			// отобразить элемент, который находится в процессе ожидания ответа от сервера
			if(hasFocus){
				// элемент имеет фокус
				panel.setBackground(Color.darkGray);
			}else if(isSelected){
				// элемент выделен
				panel.setBackground(Color.gray);
			}else{
				// обычное состояние
				panel.setBackground(Color.gray);
			}
		}else{
			if(currentElement.getData().getType().equals(XmlMessage.typeAlarm)){
				if(hasFocus){
					// элемент имеет фокус
					panel.setBackground(colorAlarmFocused);
				}else if(isSelected){
					// элемент выделен
					panel.setBackground(colorAlarmSelected);
				}else{
					// обычное состояние
					panel.setBackground(colorAlarmNormal);
				}
				
			}else if (currentElement.getData().getType().equals(XmlMessage.typeInformation)){
				if(hasFocus){
					// элемент имеет фокус
					panel.setBackground(colorInformationFocused);
				}else if(isSelected){
					// элемент выделен
					panel.setBackground(colorInformationSelected);
				}else{
					// обычное состояние
					panel.setBackground(colorInformationNormal);
				}
				
			}else if (currentElement.getData().getType().equals(XmlMessage.typeRestart)){
				if(hasFocus){
					// элемент имеет фокус
					panel.setBackground(colorRestartFocused);
				}else if(isSelected){
					// элемент выделен
					panel.setBackground(colorRestartSelected);
				}else{
					// обычное состояние
					panel.setBackground(colorRestartNormal);
				}
				
			}else if (currentElement.getData().getType().equals(XmlMessage.typeHeartBeat)){
				if(hasFocus){
					// элемент имеет фокус
					panel.setBackground(colorHeartBeatFocused);
				}else if(isSelected){
					// элемент выделен
					panel.setBackground(colorHeartBeatSelected);
				}else{
					// обычное состояние
					panel.setBackground(colorHeartBeatNormal);
				}
				
			}else{
				// неизвестный тип элемента 
				if(hasFocus){
					// элемент имеет фокус
					panel.setBackground(colorUnknownFocused);
				}else if(isSelected){
					// элемент выделен
					panel.setBackground(colorUnknownSelected);
				}else{
					// обычное состояние
					panel.setBackground(colorUnknownNormal);
				}
			}
			
		}
		return panel;
	}
	
}

/** модель данных для таблицы  */
class TableModel extends AbstractTableModel{
	private final static long serialVersionUID=1L;
	private ArrayList<MessageElement> list=new ArrayList<MessageElement>();
	
	/** добавить элемент в таблицу */
	public void addElement(MessageElement messageElement){
		this.list.add(0,messageElement);
	}
	
	public MessageElement getMessageElementByXmlMessage(XmlMessage message){
		MessageElement returnValue=null;
		for(int counter=0;counter<list.size();counter++){
			MessageElement currentElement=list.get(counter);
			if ((currentElement.getData().getId()==message.getId())
			  &&(currentElement.getData().getType().equals(message.getType()))
			  ){
				returnValue=list.get(counter);
				break;
			}
		}
		return returnValue;
	}
	
	/** проверить, есть ли сообщение с данным кодом уже в таблице */
	public boolean checkForExists(XmlMessage message) {
		return this.getMessageElementByXmlMessage(message)!=null;
	}

	/** удалить элемент из таблицы */
	public boolean removeElement(MessageElement messageElement){
		return this.list.remove(messageElement);
	}
	
	/** удалить элемент из таблицы */
	public void removeElement(int index){
		this.list.remove(index);
	}
	
	@Override
	public int getColumnCount() {
		return 6;
	}

	@Override
	public int getRowCount() {
		if(list!=null){
			return list.size();
		}else{
			return 0;
		}
	}

	/** получить значение элемента на основании индекса */
	public MessageElement getValue(int rowIndex){
		return this.list.get(rowIndex);
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex){
		case 0: {return this.list.get(rowIndex).getData().getId();}
		case 1: {return this.list.get(rowIndex).getData().getType();}
		case 2: {return this.list.get(rowIndex).getData().getTimeWrite();}
		case 3: {return this.list.get(rowIndex).getData().getModuleName();}
		case 4: {return this.list.get(rowIndex).getData().getModuleAddress();}
		case 5: {return this.list.get(rowIndex).getData().getDescription();}
		default:{return this.list.get(rowIndex).getData().getDescription();}
		}
	}
	
}
/** отображает TableCellRender в виде объекта-константы */
class ColumnCell implements TableCellRenderer{
	private JLabel label=null;
	/** отображает TableCellRender в виде объекта-константы */
	public ColumnCell(String title){
		label=new JLabel(title, SwingConstants.CENTER);
		label.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 1, Color.black));
	}
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		return label;
	}
}
