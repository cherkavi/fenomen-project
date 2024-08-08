package fenomen.monitor.login;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import fenomen.monitor.Monitor;
import fenomen.monitor.main_menu.MainMenu;
import fenomen.monitor.web_service.client_implementation.LoginImplementation;
import fenomen.monitor.web_service.common.MonitorIdentifier;

import swing_framework.AbstractInternalFrame;
import swing_framework.messages.IMessageSender;

public class Login extends AbstractInternalFrame{
	private final static long serialVersionUID=1L;
	private Monitor monitor;
	
	public Login(Monitor monitor, IMessageSender messageSender) {
		super("Идентификация", messageSender, 200, 100, true, true,false, false, true, false);
		this.monitor=monitor;
		initComponents();
		this.addInternalFrameListener(new InternalFrameAdapter(){
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				Login.this.onButtonExit();
			}
		});
		this.revalidate();
	}

	private JTextField textLogin;
	private JPasswordField textPassword;

	private void initComponents(){
		JPanel panelInput=new JPanel(new GridLayout(2,1));
		JPanel panelLogin=new JPanel(new BorderLayout());
		panelInput.add(panelLogin);
		JLabel labelLogin=new JLabel("Логин:",SwingConstants.RIGHT);
		panelLogin.add(labelLogin, BorderLayout.WEST);
		textLogin=new JTextField();
		panelLogin.add(textLogin, BorderLayout.CENTER);
		
		JPanel panelPassword=new JPanel(new BorderLayout());
		panelInput.add(panelPassword);
		JLabel labelPassword=new JLabel("Пароль:",SwingConstants.RIGHT);
		panelPassword.add(labelPassword, BorderLayout.WEST);
		textPassword=new JPasswordField();
		panelPassword.add(textPassword, BorderLayout.CENTER);
		
		JPanel panelManager=new JPanel(new GridLayout(1,2));
		JButton buttonEnter=new JButton("Вход");
		panelManager.add(buttonEnter);
		buttonEnter.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				onButtonEnter();
			}
		});
		JButton buttonExit=new JButton("Выход");
		panelManager.add(buttonExit);
		buttonExit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				onButtonExit();
			}
		});
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(panelInput,BorderLayout.CENTER);
		this.getContentPane().add(panelManager, BorderLayout.SOUTH);
	}
	
	private void onButtonEnter(){
		//  запрос на сервер идентификатора по логину и паролю
		final LoginImplementation loginImpl=new LoginImplementation(Monitor.url);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				try{
					MonitorIdentifier identifier=loginImpl.enter(Login.this.textLogin.getText(), new String(Login.this.textPassword.getPassword()));
					if(identifier==null){
						Login.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						JOptionPane.showInternalMessageDialog(Login.this, "Логин/пароль не опознаны");
					}else{
						Login.this.closeCurrentWindow();
						Monitor.monitorIdentifier=identifier;
						Login.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						new MainMenu(Login.this.monitor, Login.this.monitor);
					}
				}catch(Exception ex){
					Login.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showInternalMessageDialog(Login.this, "Server не ответил");
				}
			}
		});
		this.repaint();
	}
	
	
	
	private void onButtonExit(){
		System.exit(1);
	}
}
