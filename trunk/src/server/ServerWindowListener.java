package server;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import utility.Opcode;
import utility.Trace;
/**
 * a window listener specifically to make it so that the server exits properly when closed with the close button
 * @author bmsmolin
 *
 */
public class ServerWindowListener implements WindowListener{

	private ServerSocketThread thread=null;
	public ServerWindowListener(ServerSocketThread t)
	{
		thread=t;
	}
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * the method that is called when the close button is hit, does the exact same thing as the exit command from terminal
	 */
	public void windowClosing(WindowEvent event) {
		try
		{
			Trace.dprint("Sending global '%s' packet...", Opcode.Quit.toString());
			thread.send(Opcode.Quit, "Server is shutting down.");

			Thread.sleep(EuchreServer.QUIT_SLEEP_MS);
		}
		catch (InterruptedException ex)
		{
			Trace.dprint("Main thread was interrupted while joining ServerSocketThread!");
		}
		finally
		{
			try
			{
				Trace.dprint("Stopping ServerSocketThread...");
				thread.stopThread();
				thread.join();
			}
			catch (InterruptedException ex)
			{
				Trace.dprint("Main thread was interrupted while joining ServerSocketThread!");
			}
			finally
			{
				try
				{
					// close the socket
					Trace.dprint("Closing server socket...");
					thread.getServerSocket().close();
				}
				catch (IOException e)
				{
					Trace.dprint("Could not close socket. Message: %s", e.getMessage());
				}
			}
		}

		Trace.dprint("Terminated.");
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}
