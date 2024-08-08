package fenomen.monitor.menu_settings.heart_beat;

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
import fenomen.monitor.web_service.client_implementation.HeartBeatSettingsImplementation;
import fenomen.monitor.web_service.common.HeartBeatSettingsElement;
import swing_framework.AbstractInternalFrame;
import swing_framework.messages.IMessageSender;

public class HeartBeatSettings extends AbstractInternalFrame{
	private static final long serialVersionUID=1L;
	private TableHeartBeat table;
	private HeartBeatSettingsImplementation manager=null;
	/** отображение настроек по оповщению события HeartBeat 
	 * @param messageSender - родительское окно
	 * */
	public HeartBeatSettings( IMessageSender messageSender) {
		super("Информационные сообщения", messageSender, 550, 500, false, true);
		this.manager=new HeartBeatSettingsImplementation(Monitor.url);
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
	private boolean updateData(HeartBeatSettingsElement[] list){
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
		table=new TableHeartBeat();
		
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
		HeartBeatSettingsElement[] list=this.table.getModelData();
		// просмотреть список элементов на наличие в списке таких исправленных time_wait, которые меньше чем settings_value
		if(list!=null){
			for(int counter=0;counter<list.length;counter++){
				if(list[counter].getSettingsValue()>=list[counter].getTimeWait()){
					if(JOptionPane.showConfirmDialog(this, "Модуль: "+list[counter].getModuleId()+"\n Значение на модуле: "+list[counter].getSettingsValue()+"\n Значение события: "+list[counter].getTimeWait()+"\n изменить ?", "ошибка установки значения TimeWait", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
						// yes option
						list[counter].setSettingsValue(list[counter].getTimeWait()*2);
					}else{
						// no option
					}
				}
			}
			// отправить данные на сервер
			if(this.updateData(list)){
				// прочесть данные от сервера
				if(this.loadData()){
					JOptionPane.showMessageDialog(this, "Save OK");
				}
			}
		}else{
			JOptionPane.showMessageDialog(this, "Нет данных для отправки");
		}
	}
	
	private void onButtonCancel(){
		this.closeCurrentWindow();
	}
}
