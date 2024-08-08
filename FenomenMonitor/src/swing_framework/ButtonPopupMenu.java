package swing_framework;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import swing_framework.messages.IMessageSender;
import swing_framework.messages.WindowMessage;
import swing_framework.messages.WindowMessageType;

/** контекстное меню для кнопок, которые изображают открытые окна */
public class ButtonPopupMenu extends JPopupMenu{
	private final static long serialVersionUID=1L;
	private IMessageSender messageSender;
	private AbstractInternalFrame selectedWindow;
	
	/** контекстное меню для кнопок, которые изображают открытые окна  */
	public ButtonPopupMenu(IMessageSender messageSender){
		super();
		this.messageSender=messageSender;
		initComponents();
	}
	
	private void initComponents(){
		JMenuItem itemIconify=new JMenuItem("Свернуть");
		itemIconify.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onButtonIconify();
			}
		});
		
		JMenuItem itemDeiconify=new JMenuItem("Развернуть");
		itemDeiconify.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onButtonDeiconify();
			}
		});
		
		JMenuItem itemClose=new JMenuItem("Закрыть");
		itemClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onButtonClose();
			}
		});
		
		this.add(itemDeiconify);
		this.add(itemIconify);
		this.addSeparator();
		this.add(itemClose);
	}
	
	public void setSelectedInternalFrame(AbstractInternalFrame frame){
		this.selectedWindow=frame;
	}
	
	public void onButtonClose(){
		this.messageSender.sendMessage(null, new WindowMessage(WindowMessageType.CLOSE_WINDOW,null,this.selectedWindow));
	}
	
	public void onButtonIconify(){
		this.messageSender.sendMessage(null, new WindowMessage(WindowMessageType.ICONIFY_WINDOW,null,this.selectedWindow));
	}
	public void onButtonDeiconify(){
		this.messageSender.sendMessage(null, new WindowMessage(WindowMessageType.DEICONIFY_WINDOW,null,this.selectedWindow));
	}
}
