package chat;

import javax.swing.*;

import utility.Trace;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;


/**
 * 
 * 
 * 
 * THIS CLASS IS NOT USED.  
 * 
 *  
 * We integrated it with EuchreApplet. 
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 * 
 * @author Ryan Hurtubise and Mike Mesenbring
 */
public class IMApplet extends JApplet implements ActionListener, KeyListener{

	private static final long serialVersionUID = 1L;

	private User currentUser;

	private JTextArea messageWindow;
	private JTextArea userWindow;
	private JTextField inputText;
	private JButton submit;

	/**
	 * Initializes the chat window interface
	 */
	public void init() {
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
		messageWindow.append(user.getUsername() + ": " + text + "\n");
		//send out message to other users.
		//WILL NEED TO MODIFY LATER FOR MULTIUSERS.
		//TODO
		ChatObject obj = new ChatObject(currentUser, new User("destination"), text);

		//Have client send obj to chatmanager
		//client.send(obj);
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
