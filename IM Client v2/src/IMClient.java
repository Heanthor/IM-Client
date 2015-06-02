package src;
// the client part of the example.  To run both programs, run the server
// first, then run the client, that will connect to it.  Using localhost
// means the client will connect to the server running on the same machine.

import gui.FriendsList;
import gui.LoginWindow;
import gui.MainWindow;

import java.awt.Color;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.*;

import login.*;
import messages.External;
import messages.ExternalMessage;
import messages.Internal;
import messages.InternalMessage;
import messages.Message;

/**
 * The IMClient is the client portion of the chat program.
 * An IMServer must be running at the correct IP, otherwise an error will occur
 * when the program is launched. This class contains a main method that launches
 * the UI elements, and then handles communications with the server.
 * @author Reed
 */
public class IMClient implements Runnable {
	//private String host = "162.203.101.47";  // refers to the server IP JOSEPH IP
	//TODO obfuscate this IP
	//private String host = "52.10.127.193";  // refers to the server IP AMAZON IP 
	private String host = "52.11.220.192"; //Reed amazon IP
	//private String host = "72.45.15.42"; //REED IP
	private User identifier; //Your unique identifier
	private String myUsername; //Username of this client
	private String recipient; //Recipient of current message
	private int portNumber = 6969;	//Port the program runs on
	private ObjectInputStream reader;  // stream used to read the server's response
	private Socket serverSocket; // connection to the server
	private InetAddress serverIP; // get IP
	private static Object o = new Object(); // synchronization
	private static Object internal = new Object(); //Alert for internal messages
	private static Object recipientChange = new Object();
	private InternalMessage currentInternalMessage; //Internal message to be evaluated
	private MainWindow mainWindow; // associated MainWindow, for printing
	@SuppressWarnings("unused")
	private FriendsList userList; //User list
	private boolean register = false; //Registration request

	/**
	 * @param username - What user is using this IMClient. Used for printing 
	 * namestamp in chat, and for identification.
	 */
	public IMClient(User u) {
		identifier = u;
		myUsername = u.getCredentials().getUsername();

		//Opens connection to server
		try {
			serverIP = InetAddress.getByName(host);

			if (serverSocket != null) { //If program is being relaunched
				serverSocket.close();
			}

			serverSocket = new Socket(serverIP, portNumber);
		} catch (IOException e) {
			e.printStackTrace();

			JOptionPane.showMessageDialog(new JFrame(), "The IM server is down. "
					+ "Please try again later.",
					"IM Server Not Running", JOptionPane.ERROR_MESSAGE);

			//Quit program since it is useless if server is not running
			System.exit(1);
		}
	}

	/**
	 * Launches the login window, which saves the username given, and then calls
	 * init().
	 * @param args
	 */
	public static void main(String args[])  {
		//LoginWindow blocks until the "OK" button is pressed with a correct username
		LoginWindow w = new LoginWindow(o);

		//Create authentication bit
		User u = null;
		try {
			u = new User(PermissionLevel.USER,
					new Credentials(w.getUsername(), w.getPassword()));
		} catch (NameTooLongException e) {
			JOptionPane.showMessageDialog(new JFrame(), e.getMessage(),
					"Registration Error", JOptionPane.ERROR_MESSAGE);
			IMClient.main(null);
		}

		IMClient client = new IMClient(u);
		client.register = w.isRegister(); //Register or not?
		client.init();
	}

	/**
	 * Launches the main threads for the client.
	 */
	private void init() {
		//Starts incoming message scanner
		new Thread(this).start();

		//If the first part of the username contains the register code
		if (register) {
			System.out.println("Registering new user.");
			//Register the user
			new Thread(new Sender(this, new InternalMessage
					(identifier, "$register$"))).start();

			//Wait for response
			synchronized(internal) {
				try {
					internal.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			//Check registration response
			if (currentInternalMessage.getMessage().equals("$true$")) {
				System.out.println("Registration successful, welcome " + 
						identifier.getCredentials().getUsername());
			} else if (currentInternalMessage.getMessage().equals("$duplicate$")) {
				JOptionPane.showMessageDialog(new JFrame(), "User already has account",
						"Registration Error", JOptionPane.ERROR_MESSAGE);
				IMClient.main(null); // Restart program
			} else {
				JOptionPane.showMessageDialog(new JFrame(), "Registration write error",
						"Registration Error", JOptionPane.ERROR_MESSAGE);
				IMClient.main(null); // Restart program
			}
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainWindow = new MainWindow(o, recipientChange, identifier.getCredentials().getUsername());

				synchronized(internal) {
					try {
						internal.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		//Lets server know client is "connected"
		new Thread(new Sender(this, new InternalMessage
				(identifier, "$connected$"))).start();

		//Wait for results of authentication
		synchronized(internal) {
			try {
				internal.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//Check authentication response
		if (currentInternalMessage.getMessage().equals("$wrong_password$")) {
			JOptionPane.showMessageDialog(new JFrame(), "Wrong password",
					"Login Error", JOptionPane.ERROR_MESSAGE);
			IMClient.main(null); //Restart program
		} else if (currentInternalMessage.getMessage().equals("$username_not_found$")) {
			JOptionPane.showMessageDialog(new JFrame(), "Username not found",
					"Login Error", JOptionPane.ERROR_MESSAGE);
			IMClient.main(null); //Restart program
		} else {
			//Helps keep the users list the right size
			mainWindow.setVisible(true);
			mainWindow.getList().removeFromList("");

			//Start listener for changes
			new Thread(new RecipientChangeListener(this, mainWindow.getList(), recipientChange)).start();

			//Message loop
			while (true) {
				try {
					synchronized(o) {
						o.wait(); // main thread waits
						Message message = mainWindow.getMessage();

						if (message instanceof Internal) { //Getting message
							new Thread(new Sender(this, new InternalMessage
									(identifier, ((Internal) message)
											.getCode()))).start();
						} else {
							if (myUsername != recipient && !recipient.equals("No users online")) {
								//Starts send message thread
								new Thread(new Sender(this, new ExternalMessage
										(myUsername, recipient, ((External) message).
												getMessage()))).start();
							} else if (recipient.equals("No users online")) {
								printToUI();
							}
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Prints text to the UI, for use in init().
	 */
	private void printToUI() {
		//Mark up string to insert
		Document doc = mainWindow.getTextPane().getDocument();

		//Set colors and styles
		SimpleAttributeSet errorStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(errorStyle, Color.RED);
		StyleConstants.setBold(errorStyle, true);

		try {
			doc.insertString(doc.getLength(), "Error: No user to send to.", errorStyle);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method responsible for sending messages. Connects to the server and sends
	 * the message, including destination IP information.
	 * @param messageOut - The message to be sent
	 * @throws IOException If a problem in the ObjectOutputStream occurs.
	 */
	public void outgoing(Message messageOut) throws IOException {
		if (recipient != null) {
		ObjectOutputStream writer = new ObjectOutputStream(new ObjectOutputStream(
				serverSocket.getOutputStream()));

		writer.writeObject(messageOut);
		writer.flush();
		//Don't close the ObjectOutputStream, it closes the socket in use!
		} else {
			System.err.println("No recipient selected");
		}
	}

	/**
	 * Method responsible for handling incoming messages to the client.
	 * Reads the message, and prints it to the console and UI.
	 * @throws IOException if a problem in the InputStreamReader occurs.
	 */
	public void incoming() throws IOException {
		while (true) {
			try {
				reader = new ObjectInputStream(
						serverSocket.getInputStream());
			} catch (EOFException e) {
				System.err.println("Server quit unexpectedly.");
				System.exit(1);
			}
			//Protects against null output, shows real messages only
			Object temp = null;
			try {
				temp = reader.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			if (temp != null) {
				if (temp instanceof InternalMessage) {
					System.out.println("Internal message in: " + temp);

					InternalMessage tempIM = (InternalMessage)temp;
					if (mainWindow != null) {
						if (tempIM.getMessage().contains("$list_update ")) {
							mainWindow.getList().clearList(); //Empty list
							//mainWindow.revalidate();

							String tempx = tempIM.getMessage().substring(tempIM.getMessage().indexOf(" ") + 1);
							String[] names = tempx.split(" ");
							Arrays.sort(names); //Correct order

							//Populate user list
							if (names.length == 1 && names[0].equals(myUsername)) {
								mainWindow.getList().addToList("No users online");
							} else {
								for(String s: names) {
									if (!s.equals(myUsername)) {
										mainWindow.getList().addToList(s);
									}
								}
							}

							//Make sure you always have a selection
							if (mainWindow.getList().getLength() == 1) {
								mainWindow.getList().setSelectedIndex(0);
								setRecipient(mainWindow.getList().getSelectedValue());
							} 
						}
					}
					currentInternalMessage = tempIM;

					//Notify that an internal message is received
					synchronized(internal) {
						internal.notifyAll();
					}
				} else { //External message
					ExternalMessage response = (ExternalMessage)temp;
					System.out.println("Received message: " + response.getMessage());

					//Mark up string to insert
					Document doc = mainWindow.getTextPane().getDocument();

					//Set colors and styles
					SimpleAttributeSet usernameStyle = new SimpleAttributeSet();
					StyleConstants.setForeground(usernameStyle, new Color(52, 52, 52));
					StyleConstants.setBold(usernameStyle, true);

					SimpleAttributeSet messageStyle = new SimpleAttributeSet();
					StyleConstants.setForeground(messageStyle, new Color(255, 255, 255));

					try {
						doc.insertString(doc.getLength(), response.getSender() +
								":", usernameStyle);

						doc.insertString(doc.getLength(), " " + response.getMessage() + 
								"\n", messageStyle);

					} catch (Exception e) {
						e.printStackTrace();
					}

					//Scroll to bottom
					//TODO this doesn't scroll properly if the message is very long
					mainWindow.getScrollPane().getVerticalScrollBar().
					setValue(mainWindow.getScrollPane().
							getVerticalScrollBar().getMaximum());
				}
			}
		}
	}

	/**
	 * @param recipient the recipient to set
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	//this thread is the incoming message scanner.
	public void run() {
		try {
			incoming();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
