package fenomen.monitor.menu_settings;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import fenomen.monitor.menu_settings.alarm.AlarmSettings;
import fenomen.monitor.menu_settings.heart_beat.HeartBeatSettings;
import fenomen.monitor.menu_settings.information.InformationSettings;
import fenomen.monitor.menu_settings.restart.RestartSettings;

import swing_framework.AbstractInternalFrame;
import swing_framework.messages.IMessageSender;

public class MenuSettings extends AbstractInternalFrame{
	private static final long serialVersionUID=1L;
	private IMessageSender parent;
	public MenuSettings(IMessageSender messageSender) {
		super("Настройки оповещения", messageSender, 200, 200, false, true);
		this.parent=messageSender;
		initComponents();
	}
	 
	
	private void initComponents(){
		this.getContentPane().setLayout(new GridLayout(4,1));
		JButton buttonHeartBeat=new JButton("HeartBeat");
		this.getContentPane().add(buttonHeartBeat);
		buttonHeartBeat.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				onButtonHeartBeat();
			}
		});
		
		JButton buttonAlarm=new JButton("Alarm");
		this.getContentPane().add(buttonAlarm);
		buttonAlarm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				onButtonAlarm();
			}
		});

		JButton buttonInformation=new JButton("Information");
		this.getContentPane().add(buttonInformation);
		buttonInformation.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				onButtonInformation();
			}
		});

		JButton buttonRestart=new JButton("Restart");
		this.getContentPane().add(buttonRestart);
		buttonRestart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				onButtonRestart();
			}
		});
	}
	

	private void onButtonHeartBeat(){
		new HeartBeatSettings(this.parent);
	}
	
	private void onButtonAlarm(){
		new AlarmSettings(this.parent);
	}
	
	private void onButtonInformation(){
		new InformationSettings(this.parent); 
	}
	
	private void onButtonRestart(){
		new RestartSettings(this.parent);
	}
	
}
