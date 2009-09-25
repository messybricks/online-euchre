package client;

import java.applet.*;

import javax.swing.JOptionPane;

public class EuchreApplet extends Applet
{
	private EuchreNetClient client = null;
	
	private static final long serialVersionUID = 1L;
	
	public void init()
	{
		client = new EuchreNetClient("127.0.0.1", 36212);
		if(!client.isValid())
		{
			JOptionPane.showMessageDialog(this, "Unable to establish connection with server. Cannot continue.");
			System.exit(-1);
		}
		
		if(!client.start())
		{
			JOptionPane.showMessageDialog(this, "Unable to start client core.");
			System.exit(-1);
		}
	}

	public void destroy()
	{
		client.dispose();
	}
}
