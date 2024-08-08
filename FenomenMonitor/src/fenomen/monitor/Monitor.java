package fenomen.monitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import fenomen.monitor.login.Login;
import fenomen.monitor.menu_notifier.MenuNotifier;
import fenomen.monitor.menu_settings.alarm.AlarmSettings;
import fenomen.monitor.menu_settings.heart_beat.HeartBeatSettings;
import fenomen.monitor.menu_settings.information.InformationSettings;
import fenomen.monitor.menu_settings.restart.RestartSettings;
import fenomen.monitor.web_service.common.MonitorIdentifier;

import swing_framework.FrameMain;
import swing_framework.Position;

public class Monitor extends FrameMain{
	private final static long serialVersionUID=1L;
	/** идентификатор данного логина и пароля */
	public static MonitorIdentifier monitorIdentifier;
	public static String url="http://127.0.0.1:8080/FenomenServer/";
	
	public static void main(String[] args){
		new Monitor();
	}
	
	public Monitor() {
		super("монитор событий");
		Position.set_frame_by_dimension(this, 800,600);
		this.setVisible(true);
		new Login(this,this);
	}
	
	@Override
	protected JMenuBar getUserMenuBar() {
		JMenuBar menu=new JMenuBar();
		JMenu menuMain=new JMenu("Главное меню");
		menu.add(menuMain);
		
		JMenu menuSettings=new JMenu("Настройки оповещения"){
			private final static long serialVersionUID=1L;
			
			@Override
			public boolean isEnabled() {
				return Monitor.monitorIdentifier!=null;
			};
			
			@Override
			public boolean isVisible() {
				return Monitor.monitorIdentifier!=null;
			} 
		};
		menuMain.add(menuSettings);
		
		JMenuItem menuSettingsHeartBeat=new JMenuItem("HeartBeat");
		menuSettings.add(menuSettingsHeartBeat);
		menuSettingsHeartBeat.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				onSettingsHeartBeat();
			}
		});
		
		JMenuItem menuSettingsAlarm=new JMenuItem("Alarm");
		menuSettings.add(menuSettingsAlarm);
		menuSettingsAlarm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				onSettingsAlarm();
			}
		});
		
		JMenuItem menuSettingsInformation=new JMenuItem("Information");
		menuSettings.add(menuSettingsInformation);
		menuSettingsInformation.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				onSettingsInformation();
			}
		});
		
		JMenuItem menuSettingsRestart=new JMenuItem("Restart");
		menuSettings.add(menuSettingsRestart);
		menuSettingsRestart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				onSettingsRestart();
			}
		});

		JMenuItem menuNotify=new JMenuItem("Монитор оповщения"){
			private final static long serialVersionUID=1L;

			@Override
			public boolean isEnabled() {
				return Monitor.monitorIdentifier!=null;
			};
			
			@Override
			public boolean isVisible() {
				return Monitor.monitorIdentifier!=null;
			} 
		};
		menuMain.add(menuNotify);
		menuNotify.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onButtonNotifier();
			}
		});
		
		return menu;
	}

	private void onSettingsHeartBeat(){
		new HeartBeatSettings(this);
	}
	private void onSettingsAlarm(){
		new AlarmSettings(this);
	}
	private void onSettingsInformation(){
		new InformationSettings(this);
	}
	private void onSettingsRestart(){
		new RestartSettings(this);
	}
	
	private void onButtonNotifier(){
		new MenuNotifier(this); 
	}
}
