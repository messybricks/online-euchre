package client;

import javax.swing.*;

import utility.Trace;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;
import java.io.*;
import java.net.InetAddress;

import chat.ChatObject;
import chat.User;

public class EuchreApplet extends JApplet implements ActionListener, KeyListener, MouseListener
{
	private static final long serialVersionUID = 1L;

	private EuchreNetClient client = null;
	private User currentUser;

	private JTextArea messageWindow;
	private JTextArea userWindow;
	private JTextField inputText;
	private JButton submit;
	private Process server;
	private boolean madeserver=false;
	private ArrayList<User> users;

	/**
	 * Initializes the client and applet, calls helper methods setUpClient and setUpApplet
	 */
	public void init() {

		//set up the client
		setUpClient();

		//set up the applet
		setUpApplet();


	}
	/**
	 * uses JOptionPanes to ask user to join or host a game, then sets up a client and server (if applicable)
	 */
	private void setUpClient(){
		//set up the client
		//<<<<<<< .working
		client = new EuchreNetClient("127.0.0.1", 36212, this);
		//=======
		Object[] options = {"Host","Join" };
		int n = JOptionPane.showOptionDialog(this,
				"Would you like to host or join a game?",
				"",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[1]);
		if(n==0)
			madeserver=true;
		String myAddress,serverNums;
		String serverIP="",port="", username="";
		if(madeserver)
		{
			try
			{		
				String myName = InetAddress.getLocalHost().getHostName();
				myAddress= InetAddress.getLocalHost().getHostAddress();
				port =JOptionPane.showInputDialog("Your IP is " + myAddress +", your name is " + myName + "\n Choose Port:", "36212");
				
				//if the cancel button is pressed, exit the system
				if(port == null)
					System.exit(ABORT);
				
				//TODO deal with improper port values
				String [] args= {port};
				serverIP="127.0.0.1";
				server = Runtime.getRuntime().exec("java server/EuchreServer",args);
				
			}
			catch(IOException e)
			{
				Trace.dprint("server failed to start: %s", e.getMessage());
			}
			//sleeps to give server time to initialize
			try 
			{
				Thread.sleep(1000);
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				
			}

		}
		else
		{
			//TODO deal with improper input
			serverNums=JOptionPane.showInputDialog("Enter the Server IP:port","127.0.0.1:36212");
			
			//if the cancel button is pressed, exit the system
			if(serverNums == null)
				System.exit(ABORT);
			
			serverIP=serverNums.substring(0, serverNums.indexOf(':')).trim();
			port=serverNums.substring(serverNums.indexOf(':')+1).trim();
			Trace.dprint("the server ip is: %s and the port is %s", serverIP,port);
		}
		client = new EuchreNetClient(serverIP, new Integer(port).intValue(), this);
		//>>>>>>> .merge-right.r48
		//error messages
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

		//ask for username
		username =JOptionPane.showInputDialog("Enter username:");
		while((username.compareTo("") == 0) || (!isAlphaNumeric(username)))
			username =JOptionPane.showInputDialog("Enter username:");

		//initialize user
		initializeUser(username);
	}

	/**
	 * initializes the layout and fields of the Applet
	 */
	private void setUpApplet(){

		//initialize fields for applet.
		messageWindow = new JTextArea(10,10);
		userWindow = new JTextArea(10,10);         
		inputText = new JTextField(10);
		submit = new JButton("Submit");

		//set up the text areas				
		messageWindow.setText("Welcome to Online Euchre. \nType messages below.\n");
		userWindow.setText("Users currently in chat:\n");
		userWindow.append(currentUser.getUsername());
		userWindow.setBackground(Color.LIGHT_GRAY);
		//set up borders
		messageWindow.setBorder(BorderFactory.createEtchedBorder());
		userWindow.setBorder(BorderFactory.createEtchedBorder());
		//make both text areas not editable.
		messageWindow.setEditable(false);
		userWindow.setEditable(false);
		messageWindow.setLineWrap(true);


		//TODO: initialize size of the applet <REMOVE LATER>
		setSize(500, 500);

		//set the layout manager to BorderLayout
		setLayout(new BorderLayout());

		//initialize JPanels & ScrollPane
		JPanel centerArea = new JPanel (new GridLayout(1,2));
		JPanel messageArea = new JPanel(new GridLayout(1,1));
		JPanel userArea = new JPanel (new GridLayout(1,1));
		JPanel inputArea = new JPanel ();
		JScrollPane messageScroll = new JScrollPane(messageWindow);
		inputArea.setLayout(new BoxLayout (inputArea,BoxLayout.LINE_AXIS));

		//add the fields to the panels
		messageArea.add(messageScroll);
		userArea.add(userWindow);
		inputArea.add(submit);
		inputArea.add(inputText);

		//add JPanels to IMApplet
		centerArea.add(messageArea);
		centerArea.add(userArea);
		add(centerArea, BorderLayout.CENTER);
		add(inputArea, BorderLayout.SOUTH);

		//Attach a listener to the button.
		submit.addActionListener(this);
		inputText.addActionListener(this);
		inputText.addKeyListener(this);
		inputText.addMouseListener(this);

		//TODO: get this to work 
		inputText.setText("Enter your messages here");
		inputText.select(0, inputText.getText().length());

	}

	/**
	 * initialize the user
	 * @param username the name inputed by user
	 */
	private void initializeUser(String username){
		//Create the new user.
		currentUser = new User(username);
		//authenticate the client
		client.authenticate(currentUser);
	}
	
	/**
	 * adds the username of a new user to the user window
	 * @param users the user whose name is to be added
	 */
	public void addUserToWindow(ArrayList<User> newUsers)
	{
		users = newUsers;
		userWindow.setText("Users currently in chat:");
		for(User x : users)
			userWindow.append("\n" + x.getUsername());
	}

	/**
	 * sets the user name or calls the sendMessage function to send a message, using the input text
	 */
	private void onSubmit(){
		String text = inputText.getText().trim();		
		//if the message is not empty
		if (!text.equals("")) {
			sendMessage(inputText.getText(), currentUser);
			//clear the text.
			inputText.setText("");
			//set focus back to the text back for easy message passing.
			inputText.requestFocus();
		}	
	}


	/**
	 * Adds a message to the chat window, followed by a newline character.
	 * 
	 * @param text the message to be added to the window
	 */
	public void sendMessage(String text, User user) {
		//add message to current users window.
		//	messageWindow.append(user.getUsername() + ": " + text + "\n");
		//send out message to other users.
		client.sendGlobalChatMessage(text);
	}

	/**
	 * Receives a message from another user and appends it to the chat window
	 * 
	 * @param message the message to be received
	 */
	public void receiveMessage(ChatObject message) {
		messageWindow.append(message.getSource() + ": " + message.getMessage() + "\n");
	}

	/**
	 * updates the user window with the users in the given vector of users
	 * 
	 * @param users the vector containing the users to be added
	 */
	public void setUserWindow(Vector<User> users) {
		//reset the user window
		userWindow.setText("Users currently in chat:\n");
		//add each user to the user window.
		for(User user : users) {
			userWindow.append(user.getUsername());
		}
	}

	/**
	 * closes client threads prior to the applet closing and sends a quit message to the server.
	 */
	public void destroy()
	{
		client.dispose(currentUser);
		if(madeserver){
			try 
			{
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
				writer.write("exit\r\n");
				writer.flush();
				writer.close();
			} 
			catch (IOException e) 
			{
				Trace.dprint("unable to tell server to exit: %s", e.getMessage());
			
			}
		}
	}

	/**
	 * calls the onSubmit method when the submit button is pressed (submits input text) 
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == submit)
		{
			onSubmit();
		}
	}

	/**
	 * calls the onSubmit method when the enter key is released (submits input text) 
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyChar() == '\n')
		{
			onSubmit();
		}

	}

	/**
	 * clears the text when the user first clicks on the input text area
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if(inputText.getText().equals("Enter your messages here"))
			inputText.setText("");
		
	}

	/**
	 * not implemented
	 */
	@Override
	public void keyPressed(KeyEvent e) {}

	/**
	 * not implemented
	 */
	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	private boolean isAlphaNumeric(final String s) 
	{
		  final char[] chars = s.toCharArray();
		  for (int x = 0; x < chars.length; x++) {      
		    final char c = chars[x];
		    if ((c >= 'a') && (c <= 'z')) continue; // lowercase
		    if ((c >= 'A') && (c <= 'Z')) continue; // uppercase
		    if ((c >= '0') && (c <= '9')) continue; // numeric
		    return false;
		  }  
		  return true;
		}


	
}
