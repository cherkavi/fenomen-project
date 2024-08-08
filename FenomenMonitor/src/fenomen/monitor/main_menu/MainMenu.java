package fenomen.monitor.main_menu;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import fenomen.monitor.Monitor;
import fenomen.monitor.menu_notifier.MenuNotifier;
import fenomen.monitor.menu_settings.MenuSettings;

import swing_framework.AbstractInternalFrame;
import swing_framework.messages.IMessageSender;

public class MainMenu extends AbstractInternalFrame{
	private final static long serialVersionUID=1L;
	private Monitor monitor;
	
	public MainMenu(Monitor monitor, IMessageSender messageSender) {
		super("Главное меню", messageSender, 350, 80, false, true);
		this.monitor=monitor;
		initComponents();
	}
	
	private void initComponents(){
		this.getContentPane().setLayout(new GridLayout(2,1));
		
		JButton buttonSettings=new JButton("Настройки оповещения");
		this.getContentPane().add(buttonSettings);
		buttonSettings.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				onButtonSettings();
			}
		});
		
		JButton buttonNotifier=new JButton("Монитор оповещения");
		this.getContentPane().add(buttonNotifier);
		buttonNotifier.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				onButtonNotifier();
			}
		});
	}

	private void onButtonSettings(){
		this.closeCurrentWindow();
		new MenuSettings(this.monitor);
	}
	
	private void onButtonNotifier(){
		this.closeCurrentWindow();
		new MenuNotifier(this.monitor);
	}
}
