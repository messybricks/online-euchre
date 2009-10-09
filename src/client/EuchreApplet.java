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

/**
 * The Graphical User Interface of the Online Euchre Game
 */
public class EuchreApplet extends JApplet implements ActionListener, KeyListener, MouseListener
{
	private static final long serialVersionUID = 1L;

	private EuchreNetClient client = null;
	private User currentUser;

	private JTextArea messageWindow;
	private JTextArea userWindow;
	private ArrayList<JTextArea> userNames;
	private JTextField inputText;
	private JButton submit;
	private Process server;
	private boolean madeserver=false;
	private ArrayList<User> users;
	private ArrayList<String> ignoreList;
	private JPanel userArea;
	private JTextArea clicked;
	private JCheckBox ignore;
	private boolean inputTextDeleted;

	/**
	 * Initializes the client and applet, calls helper methods setUpClient and setUpApplet
	 */
	public void init() 
	{
		super.init();

		//set up the client
		setUpClient();

		//set up the applet
		setUpApplet();

	}

	/**
	 * uses JOptionPanes to prompt the user for information, then sets up a client and server (if applicable)
	 */
	private void setUpClient()
	{
		//prompt user to choose between hosting and joining a game
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
				boolean validport=false;
				//keep asking for port until it is valid
				while (!validport)
				{
					try
					{
						port =JOptionPane.showInputDialog("Your IP is " + myAddress +", your name is " + myName + "\n Choose Port:", "36212");
						if(port == null)
							System.exit(ABORT);
						int portTest=new Integer(port).intValue();
						if (portTest >= 1024 && portTest <= 65534)
							validport=true;
					}
					catch(NumberFormatException e)
					{
						//TODO message saying the port is wrong
					}
				}
				//if the cancel button is pressed, exit the system
				if(port == null)
					System.exit(ABORT);
				
				//TODO deal with improper port values
				//String [] args= {port};
				serverIP="127.0.0.1";
				server = Runtime.getRuntime().exec("java server/EuchreServer " + port);
				
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
			
			boolean validport=false;
			//keep asking for IP and port until input is valid
			while(!validport)
			{
				try
				{
					serverNums=JOptionPane.showInputDialog("Enter the Server IP:port","127.0.0.1:36212");
					//if the cancel button is pressed, exit the system
					if(serverNums == null)
						System.exit(ABORT);
					serverIP=serverNums.substring(0, serverNums.indexOf(':')).trim();
					port=serverNums.substring(serverNums.indexOf(':')+1).trim();
					int portTest=new Integer(port).intValue();
					if (portTest >= 1024 && portTest <= 65534)
						validport=true;
				}
				catch(NumberFormatException e)
				{
					//TODO message saying port is wrong
				}
				catch(StringIndexOutOfBoundsException e)
				{
					//TODO message saying input format is wrong
				}
			}
			
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
		username ="";
		while((username.compareTo("") == 0) || (!isAlphaNumeric(username)))
		{
			username =JOptionPane.showInputDialog("Enter username:");
			if(username == null)
				System.exit(ABORT);
		}

		//initialize user
		initializeUser(username);
	}

	/**
	 * initializes the layout and fields of the Applet
	 */
	private void setUpApplet()
	{

		userNames = new ArrayList<JTextArea>();
		ignoreList = new ArrayList<String>();

		//initialize fields for applet.
		messageWindow = new JTextArea(10,10);
		userWindow = new JTextArea(0,1);         
		inputText = new JTextField(10);
		submit = new JButton("Submit");

		//set up the text areas				
		messageWindow.setText("Welcome to Online Euchre. \nType messages below.\n");
		userWindow.setText("Users currently in chat:\n");
		userNames.add(new JTextArea(1,10));
		userNames.get(0).setText(currentUser.getUsername());
		//userWindow.append(currentUser.getUsername());
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
		userArea = new JPanel ();
		userArea.setLayout(new BoxLayout(userArea,BoxLayout.Y_AXIS));
		JPanel inputArea = new JPanel ();
		JScrollPane messageScroll = new JScrollPane(messageWindow);
		inputArea.setLayout(new BoxLayout (inputArea,BoxLayout.LINE_AXIS));

		//add the fields to the panels
		messageArea.add(messageScroll);
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

		inputText.setText("Enter your messages here");
		inputTextDeleted = false;
		//inputText.select(0, inputText.getText().length());

	}

	/**
	 * initialize the user
	 * 
	 * @param username the name inputed by user
	 */
	private void initializeUser(String username)
	{
		currentUser = new User(username);
		client.authenticate(currentUser);
	}

	/**
	 * adds the username of a new user to the user window
	 * 
	 * @param users the user whose name is to be added
	 */
	public void addUserToWindow(ArrayList<User> newUsers)
	{
		users = newUsers;
		userWindow.setText("Users currently in chat:");
		userArea.removeAll();
		userArea.add(userWindow);
		userNames = new ArrayList<JTextArea>();
//		for(int x = 0; x < userNames.size(); x++)
//		{
//			//userArea.remove(userNames.get(x));
//			userNames.remove(0);
//		}
		for(User x : users)
		{
			JTextArea temp = new JTextArea(1,1);
			temp.setText("  " + x.getUsername());
			temp.setEditable(false);
			temp.addMouseListener(this);
			temp.setBackground(Color.LIGHT_GRAY);
			temp.setFocusable(false);
			if(x.getUsername().compareTo(currentUser.getUsername()) == 0)
			{
				temp.setToolTipText("This is me!");
			}
			userNames.add(temp);
			userArea.add(userNames.get(userNames.lastIndexOf(temp)));
			//userWindow.append("\n" + x.getUsername());
		}
		userArea.add(Box.createRigidArea(new Dimension(5,500)));
		//userArea.setSize(0, 0);
		userArea.doLayout();
		
	}

	/**
	 * calls the sendMessage function with the text from inputText
	 */
	private void onSubmit()
	{
		String text = inputText.getText().trim();		
		//if the message is not empty
		if (!text.equals("")) {
			//sendMessage(inputText.getText(), currentUser);
			client.sendGlobalChatMessage(inputText.getText());
			//clear the text.
			inputText.setText("");
			//set focus back to the text back for easy message passing.
			inputText.requestFocus();
		}	
	}

	/*
	/**
	 * THIS FUNCTION APPEARS TO BE UNNECCESSARY.  
	 * TODO: REMOVE?
	 * 
	 * @param text the message to be added to the window
	 *
	public void sendMessage(String text, User user) 
	{
		//add message to current users window.
		//	messageWindow.append(user.getUsername() + ": " + text + "\n");
		//send out message to other users.
		client.sendGlobalChatMessage(text);
	}
	 */

	/**
	 * Receives a message from another user and appends it to the chat window
	 * 
	 * @param message the message to be received
	 */
	public void receiveMessage(ChatObject message) 
	{
		String incomingName = message.getSource().getUsername();

		//check to see if the message is from a blocked user
		boolean ignore = false;
		for(String blockName:ignoreList)
		{
			if(incomingName.compareTo(blockName) == 0)
				ignore = true;
		}

		//if user is allowed, append the message
		if(!ignore)
		{
			messageWindow.append(message.getSource() + ": " + message.getMessage() + "\n");
			messageWindow.setCaretPosition(messageWindow.getDocument().getLength());
		}
	}

	/* 
	 * THIS FUNCTION APPEARS TO BE UNNECCESSARY.
	 * TODO: REMOVE?
	/**
	 * updates the user window with the users in the given vector of users
	 * 
	 * @param users the vector containing the users to be added
	 *
	public void setUserWindow(Vector<User> users) 
	{
		//reset the user window
		userWindow.setText("Users currently in chat:\n");
		//add each user to the user window.
		for(User user : users) {
			userWindow.append(user.getUsername());
		}
	}
	 */

	/**
	 * closes client threads prior to the applet closing and sends a quit message to the server.
	 */
	public void destroy()
	{
		super.destroy();
		
		if(client != null)
			client.dispose(currentUser);
		
		if(madeserver){
			try 
			{
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
				writer.write("exit ");
				writer.newLine();
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
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == submit)
		{
			onSubmit();
		}
	}

	/**
	 * calls the onSubmit method when the enter key is released (submits input text) 
	 */
	@Override
	public void keyReleased(KeyEvent e) 
	{
		if(e.getKeyChar() == '\n')
		{
			onSubmit();
		}

	}

	/**
	 * performs actions on various fields when the mouse is clicked
	 * 
	 * TODO: Document this method more thoroughly
	 */
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		if(e.getSource() != inputText)
		{
			try
			{
				if(clicked == null)
				{
					JTextArea temp = (JTextArea) e.getComponent();
					temp.setForeground(Color.LIGHT_GRAY);
					temp.setBackground(Color.BLACK);
					clicked = temp;


					int index = userNames.indexOf(clicked);
					userWindow.setText("Users currently in chat:");
					userArea.removeAll();
					userArea.add(userWindow);
					for(int x = 0; x < index + 1; x++)
					{
						userArea.add(userNames.get(x));
					}

					JPanel userInfo = new JPanel();
					userInfo.setBackground(Color.WHITE);
					//userInfo.add(Box.createRigidArea(new Dimension(5,100)));
					ignore = new JCheckBox("Ignore?");

					boolean inList = false;
					for(String name: ignoreList)
					{
						if(temp.getText(2, temp.getText().length()-2).compareTo(name) == 0)
						{
							inList = true;
							ignore = new JCheckBox("Ignore?", true);
						}	
					}
					if(!inList)
						ignore = new JCheckBox("Ignore?", false);

					ignore.setBackground(Color.WHITE);
					if(temp.getText(2, temp.getText().length()-2).compareTo(currentUser.getUsername()) == 0)
					{
						JTextArea tempText = new JTextArea("This is you!");
						tempText.setEditable(false);
						tempText.setFocusable(false);
						userInfo.add(tempText);
						//ignore.setEnabled(false);
					}
					else
						userInfo.add(ignore);
					userArea.add(userInfo);


					for(int x = index + 1; x < userNames.size(); x++)
					{
						userArea.add(userNames.get(x));
					}
					userArea.add(Box.createRigidArea(new Dimension(5,500)));
					userArea.doLayout();
				}
				else
				{
					boolean alreadyIgnored = false;
					for(String name:ignoreList)
					{
						if(clicked.getText(2, clicked.getText().length()-2).compareTo(name) == 0)
							alreadyIgnored = true;
					}
					if(ignore.isSelected())
					{
						if(!alreadyIgnored)
							ignoreList.add(clicked.getText(2, clicked.getText().length()-2));
					}
					else if(alreadyIgnored)
					{
						int removeIndex = -1;
						for(int x = 0; x < ignoreList.size(); x++)
						{
							if(clicked.getText(2, clicked.getText().length()-2).compareTo(ignoreList.get(x)) == 0)
								removeIndex = x;
						}
						if(removeIndex > -1)
							ignoreList.remove(removeIndex);
					}


					clicked.setForeground(Color.BLACK);
					clicked.setBackground(Color.LIGHT_GRAY);
					clicked = null;


					userWindow.setText("Users currently in chat:");
					userArea.removeAll();
					userArea.add(userWindow);
					for(int x = 0; x < userNames.size(); x++)
					{
						userArea.add(userNames.get(x));
					}
					userArea.add(Box.createRigidArea(new Dimension(5,500)));
					userArea.doLayout();
					userArea.setSize(0, 0);
				}
			}
			catch(Exception e1)
			{

			}
		}
		/*	if(e.getSource() != inputText)
		{
			try
			{
			}
			catch(Exception e1)
			{

			}
		}
		if(e.getSource() != inputText)
			clicked = (JTextArea) e.getComponent();*/
	}

	/**
	 * deletes the initial text of the inputText field when the mouse is pressed on it
	 */
	@Override
	public void mousePressed(MouseEvent e) 
	{
		if(!inputTextDeleted && (e.getSource() == inputText))
		{
			inputText.setText("");
			inputTextDeleted = true;
		}
	}

	/**
	 * returns true if the given string is alphanumeric.  Alphanumeric strings
	 * contain only upper and lower case letters (a-z, A-Z), and digits (0-9)
	 * 
	 * @param s the given string to be tested
	 * @return true if the given string is alphanumeric
	 */
	public static boolean isAlphaNumeric(final String s) 
	{
		final char[] chars = s.toCharArray();
		for (int x = 0; x < chars.length; x++) 
		{      
			final char c = chars[x];
			if ((c >= 'a') && (c <= 'z')) continue; // lowercase
			if ((c >= 'A') && (c <= 'Z')) continue; // uppercase
			if ((c >= '0') && (c <= '9')) continue; // numeric
			return false;
		}  
		return true;
	}


	/**
	 * not implemented
	 */
	@Override
	public void mouseReleased(MouseEvent e) 
	{

	}

	/**
	 * not implemented
	 */
	@Override
	public void keyPressed(KeyEvent e) 
	{

	}

	/**
	 * not implemented
	 */
	@Override
	public void keyTyped(KeyEvent e) 
	{

	}

	/**
	 * not implemented
	 */
	@Override
	public void mouseEntered(MouseEvent e) 
	{

	}

	/**
	 * not implemented
	 */
	@Override
	public void mouseExited(MouseEvent e) 
	{

	}
	
	/**
	 * This method gets called to close the applet gracefully when the server crashes or shuts down.
	 * 
	 * @param message A brief explanation of why the applet is closing
	 */
	public void onServerExit(String message)
	{
		JOptionPane.showMessageDialog(this, message, "Server Has Closed", 0);
		System.exit(0);
	}

}
