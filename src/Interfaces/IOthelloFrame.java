package Interfaces;

import java.awt.event.WindowEvent;

public interface IOthelloFrame {
	
	/**
	 * Updates the GUI
	 */
	public abstract void update();
	
	/**
	 * Updates the message to the user
	 */
	public abstract void updateMsg();
	
	// ----------------- window listener methods ----------------------------
	public abstract void windowActivated(WindowEvent e);
	
	public abstract void windowDeactivated(WindowEvent e);
	
	public abstract void windowDeiconified(WindowEvent e);
	
	public abstract void windowIconified(WindowEvent e);
	
	public abstract void windowClosing(WindowEvent e);
	
	public abstract void windowClosed(WindowEvent e);
	
	public abstract void windowOpened(WindowEvent e);
	
}