package client;

import javax.swing.*;

import utility.Trace;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.io.*;
import java.net.InetAddress;

import chat.ChatObject;
import chat.User;

public class EuchreApplet extends JApplet implements ActionListener, KeyListener
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

	/**
	 * Initializes the chat window interface
	 */
	public void init() {
		//set up the client
		//TODO: ask for server IP address
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
		String myAddress,serverNums,serverIP="",port="";
		if(madeserver)
		{
			try
			{		
				myAddress= InetAddress.getLocalHost().getHostAddress();
				port =JOptionPane.showInputDialog("your IP is " + myAddress +"\n port:", "36212");
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
				e.printStackTrace();
			}
			
		}
		else
		{
			//TODO deal with improper input
			serverNums=JOptionPane.showInputDialog("Enter the Server IP:port","127.0.0.1:36212");
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
		
		
		//initialize fields for applet.
		messageWindow = new JTextArea(10,10);
		userWindow = new JTextArea(10,10);         
		inputText = new JTextField(10);
		submit = new JButton("Submit");

		//set up the text areas				
		messageWindow.setText("Welcome to Online Euchre. \nPlease submit your username below.\n");
		userWindow.setText("Users currently in chat:\n");
		userWindow.setBackground(Color.LIGHT_GRAY);
		//set up borders
		messageWindow.setBorder(BorderFactory.createEtchedBorder());
		userWindow.setBorder(BorderFactory.createEtchedBorder());
		//make both text areas not editable.
		messageWindow.setEditable(false);
		userWindow.setEditable(false);
		messageWindow.setLineWrap(true);
		//TODO: get this to work like the "Enter your messages here" thing
		inputText.setText("Enter your username here.");
		inputText.select(0, inputText.getText().length());

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

	}

	/**
	 * sets the user name or calls the sendMessage function to send a message, using the input text
	 */
	private void onSubmit(){
		String text = inputText.getText().trim();		
		//if the message is not empty
		if (!text.equals("")) {
			//if the user has not signed in, then create a new user.
			if(currentUser == null) {
				//Create the new user.
				currentUser = new User(inputText.getText());
				//Update the user window.
				userWindow.append(currentUser.getUsername());
				//Set up the message text field.
				//TODO Might need to remove if all screens only share one text area, rather then sharing the data in the area.
				inputText.setText("Enter your messages here.");
				inputText.select(0, inputText.getText().length());
				client.authenticate(currentUser);
			}
			else {
				sendMessage(inputText.getText(), currentUser);
				//clear the text.
				inputText.setText("");
				//set focus back to the text back for easy message passing.
				inputText.requestFocus();
			}
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
		client.dispose();
		if(madeserver){
			try 
			{
				new BufferedWriter(new OutputStreamWriter(server.getOutputStream())).write("exit");
				server.destroy(); //currently called because the above command is not working
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
	 * not implemented
	 */
	@Override
	public void keyPressed(KeyEvent e) {}
	
	/**
	 * not implemented
	 */
	@Override
	public void keyTyped(KeyEvent e) {}
	


}