package com.DarkBlue.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.DarkBlue.Utilities.Utilities;

// Source for WindowListener: https://stackoverflow.com/questions/4265159/javas-do-nothing-on-close
public final class InstructionFrame extends JFrame implements WindowListener{

	private static final long serialVersionUID = Utilities.ONE_LONG;
	private JTextArea m_text;
	
	public InstructionFrame(){
		this.m_text = new JTextArea();
		
		this.m_text.setText(Utilities.INSTRUCTIONS);
		
		this.m_text.setEditable(false);
		
		this.add(new JScrollPane(this.m_text), BorderLayout.CENTER);
		
		this.setSize(new Dimension(1000, 500));
		
		this.setVisible(true);
	}
	
	@Override
	public final void windowOpened(final WindowEvent a_event){
		return;
	}
	
	@Override
	public final void windowIconified(final WindowEvent a_event){
		return;
	}
	
	@Override
	public final void windowActivated(final WindowEvent a_event){
		return;
	}
	
	@Override
	public final void windowClosing(final WindowEvent a_event){
		this.setVisible(false);
		this.dispose();
	}
	
	@Override
	public final void windowDeactivated(final WindowEvent a_event){
		return;
	}
	
	@Override
	public final void windowDeiconified(final WindowEvent a_event){
		return;
	}
	
	@Override
	public final void windowClosed(final WindowEvent a_event){
		return;
	}
}