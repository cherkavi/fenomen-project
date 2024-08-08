package fenomen.monitor.menu_settings.information;

import java.awt.BorderLayout;

import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fenomen.monitor.Monitor;
import fenomen.monitor.web_service.client_implementation.InformationSettingsImplementation;
import fenomen.monitor.web_service.common.InformationSettingsElement;
import swing_framework.AbstractInternalFrame;
import swing_framework.messages.IMessageSender;

public class InformationSettings extends AbstractInternalFrame{
	private static final long serialVersionUID=1L;
	private TableInformation table;
	private InformationSettingsImplementation manager=null;
	/** отображение настроек по оповщению события Information 
	 * @param messageSender - родительское окно
	 * */
	public InformationSettings( IMessageSender messageSender) {
		super("Информационные сообщения", messageSender, 500, 500, false, true);
		this.manager=new InformationSettingsImplementation(Monitor.url);
		initComponents();
		loadData();
	}
	
	/**  обновить данные в таблице */
	private boolean loadData(){
		boolean returnValue=false;
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try{
			this.table.setModelData(this.manager.getList(Monitor.monitorIdentifier));
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			returnValue=true;
		}catch(Exception ex){
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			JOptionPane.showMessageDialog(this, "Ошибка обмена с сервером","Error",JOptionPane.ERROR_MESSAGE);
			returnValue=false;
		}
		return returnValue;
	}
	/** послать данные на сервер */
	private boolean updateData(InformationSettingsElement[] list){
		boolean returnValue=false;
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try{
			this.manager.updateList(Monitor.monitorIdentifier, list);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			returnValue=true;
		}catch(Exception ex){
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			JOptionPane.showMessageDialog(this, "Ошибка обмена с сервером","Error",JOptionPane.ERROR_MESSAGE);
			returnValue=false;
		}
		return returnValue;
	}

	
	
	/** инициализация компонентов */
	private void initComponents(){
		table=new TableInformation();
		
		JPanel panelManager=new JPanel(new GridLayout(1,2,20,10));
		JButton buttonSave=new JButton("Save");
		buttonSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onButtonSave();
			}
		});
		JButton buttonCancel=new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onButtonCancel();
			}
		});
		panelManager.add(buttonSave);
		panelManager.add(buttonCancel);
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(new JScrollPane(table),BorderLayout.CENTER);
		this.getContentPane().add(panelManager, BorderLayout.SOUTH);
	}
	
	private void onButtonSave(){
		// System.out.println("Сохранить");
		InformationSettingsElement[] list=this.table.getModelData();
		// отправить данные на сервер
		if(this.updateData(list)){
			// прочесть данные от сервера
			if(this.loadData()){
				JOptionPane.showMessageDialog(this, "Save OK");
			}
		}
	}
	
	private void onButtonCancel(){
		this.closeCurrentWindow();
	}
}
