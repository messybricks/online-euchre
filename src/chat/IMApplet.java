package chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * IMApplet is a JApplet that implements a basic chat window interface. * 
 * 
 * @author Ryan Hurtubise and Mike Mesenbring *
 */

public class IMApplet extends JApplet implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	private String username = "Your Username";
	
	private JTextArea messageWindow;
	private JLabel userWindow;
	private JTextField inputText;
	private JButton submit;

	/**
	 * Initializes the chat window interface
	 */
	public void init() {
		//initialize fields for applet.
		messageWindow = new JTextArea(10,10);
		userWindow = new JLabel();         
		inputText = new JTextField(10);
		submit = new JButton("Submit");
		
		//set up the text areas
		userWindow.setVerticalAlignment(JLabel.TOP);					
		userWindow.setText("Users currently in chat:");						//cannot get new lines!!!!
		messageWindow.setBorder(BorderFactory.createEtchedBorder());
		userWindow.setBorder(BorderFactory.createEtchedBorder());
		
		//initialize size of the applet <REMOVE LATER>
		this.setSize(500, 500);
			    
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
		
		
	}

	/**
	 * Submits the input text to the chat window when the submit button is pressed 
	 * or the enter key is pressed.
	 */
	public void actionPerformed(ActionEvent e) {
		if (inputText.getText() != "") {
			submitMessage(username + ": " + inputText.getText());
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
	public void submitMessage(String text) {
		messageWindow.append(text + "\n");
	}

}
