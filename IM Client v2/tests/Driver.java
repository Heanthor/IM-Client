package tests;

import gui.LoginWindow;

import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import client.IMClient;

@Deprecated
public class Driver {
	/**
	 * Message starts in MainWindow, on the send button action event.
	 * Then passed to Driver.start static method.
	 * Then is passed into static instance variable message
	 * And finally is accessed by IMClient class in the run() method to be passed to outoging()
	 */
	String message;
	IMClient client;
	
	//Stores contacts on ReedRead network in form ("First Name", "IP Address")
	HashMap<String, String> contacts;

	public Driver() {
		//Populating contact list
		contacts = new HashMap<String, String>();
		contacts.put("Joseph", "162.203.101.47");
	}

	public static void main(String[] args) {
		//Starts UI
		Driver d = new Driver();
		d.execute();
		
	}
	
	/*Why didn't I make this yet.. */
	private void execute() {
		LoginWindow w = new LoginWindow(new Object());
		//If the IM server is not running, client will not be initialized
		
		client = new IMClient(null);
		
		System.out.println("Client successfully launched.\n"
				+ "Enter message:");
		System.out.println();
	}
	/**
	 * Necessary to avoid problems caused by removing scanner. Is called to "send" message
	 */
	public void start(String messageFromMainWindow, IMClient client) {
		message = messageFromMainWindow;
		if (message != null) {
			//Starts send message thread
			new Thread(client).start();
		}
		
		//Starts incoming message scanner
		try {
			client.incoming();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), e.toString(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	//deprecated
	/*	private void UI() {
		JFrame frame = new JFrame();
		frame = new JFrame();
		frame.setBounds(150, 150, 350, 225);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); //Centers frame
		frame.setLocation(dim.width/2-frame.getSize().width/2,
				dim.height/2-frame.getSize().height/2);
		//Title for the window
		frame.setTitle("ReedRead v1.0");

		//Input Field
		final JTextField text = new JTextField();
		//Clears text on click
		text.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				text.setText("");
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}

		});
		text.setText("Enter message here");
		text.setBounds(20, 15, 250, 34);
		frame.getContentPane().add(text);
		text.setColumns(10);

		//Incoming Message
		JLabel incoming = new JLabel(messageIn);
		incoming.setBounds(20, 50, 350, 34);
		frame.getContentPane().add(incoming);

		//Buttons
		JButton buttonSend = new JButton("Send");
		JButton buttonClose = new JButton("Close");
		//Makes "enter" press send message
		frame.getRootPane().setDefaultButton(buttonSend);
		buttonSend.setBounds(20, 150, 89, 23);
		buttonClose.setBounds(120, 150, 89, 23);
		frame.getContentPane().add(buttonSend);
		frame.getContentPane().add(buttonClose);

		//Action for send button
		buttonSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				messageOut = text.getText();               
				text.setText(messageOut);
				System.out.println(messageOut);
				text.setText("");
			}
		});

		//Action for close button
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Kills the program
				System.exit(0);
			}
		});

		frame.revalidate();
		frame.setVisible(true);
	}
	 */
	

}
