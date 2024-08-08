package com.cherkashin.vitaliy.modbus.core.schedule_read.test;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class TableOfRegisters extends JFrame {
	private final static long serialVersionUID=1L;
	
	public TableOfRegisters(int count){
		super("Modbus Test");
		initComponents(count);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100,50,50,800);
		this.setResizable(true);
		this.setVisible(true);
	}
	
	private ArrayList<LabelPanel> panels=new ArrayList<LabelPanel>();
	
	private void initComponents(int panelCount){
		this.getContentPane().setLayout(new GridLayout(panelCount,1));
		for(int counter=0;counter<panelCount;counter++){
			LabelPanel panel=new LabelPanel("Number:"+panelCount); 
			panels.add(panel);
			this.getContentPane().add(panel);
		}
	}
	
	public void setText(int counter, String value){
		final int index=counter;
		final String text=value;
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				
				TableOfRegisters.this.panels.get(index).setText(text);
			}
		});
	}
}

class LabelPanel extends JPanel{
	private final static long serialVersionUID=1l;
	private JLabel label=new JLabel();
	
	public LabelPanel(String title){
		super();
		this.setLayout(new GridLayout(1,1));
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(title));
		this.add(label);
	}
	
	public void setText(String text){
		this.label.setText(text);
	}
}
