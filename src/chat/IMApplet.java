package chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * IMApplet is a JApplet that implements a basic chat window interface. 
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
	private ChatManager manager;

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

		//initialize size of the applet <REMOVE LATER>
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

		//initialize the ChatManager object.
		manager = new ChatManager();
	}

	/**
	 * Submits the input text to the chat window when the submit button is pressed 
	 * or the enter key is pressed.
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == submit)
		{
			onSubmit();
			
		}
			
	}


	private void onSubmit() {
		//if the message is not empty
		if (inputText.getText() != "") 
		{
			//if the user has not signed in, then create a new user.
			if(currentUser == null) {
				//Create the new user.
				currentUser = new User(inputText.getText());
				//Update the user window.
				userWindow.append(currentUser.getUsername());
				//Set up the message text field.
				inputText.setText("Enter your messages here.");
				inputText.select(0, inputText.getText().length());
			}
			else {
				sendMessage(currentUser + ": " + inputText.getText());
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
	public void sendMessage(String text) {
		//add message to current users window.
		messageWindow.append(text + "\n");
		//send out message to other users.
		//WILL NEED TO MODIFY LATER FOR MULTIUSERS.
		//TODO
		ChatObject obj = new ChatObject(currentUser, new User("destination"), text);
		manager.send(obj);
	}

	/**
	 * Receives a message from another user and appends it to the chat window
	 * 
	 * @param message the message to be received
	 */
	public void receiveMessage(ChatObject message) {
		messageWindow.append(message.getSource() + ": " + message.getMessage() + "\n");
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyChar() == '\n')
		{
			onSubmit();
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
