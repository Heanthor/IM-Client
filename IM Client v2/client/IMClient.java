package client;
// the client part of the example.  To run both programs, run the server
// first, then run the client, that will connect to it.  Using localhost
// means the client will connect to the server running on the same machine.

//

import crypt.KeyAlreadySetException;
import crypt.MessageCrypt;
import gui.LoginWindow;
import gui.MainWindow;
import login.Credentials;
import login.NameTooLongException;
import login.PermissionLevel;
import login.User;
import messages.*;

import javax.crypto.IllegalBlockSizeException;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * The IMClient is the client portion of the chat program.
 * An IMServer must be running at the correct IP, otherwise an error will occur
 * when the program is launched. This class contains a main method that launches
 * the UI elements, and then handles communications with the server.
 * @author Reed
 */
public class IMClient implements Runnable {
	//TODO obfuscate this IP
	//private String host = "52.10.127.193";  // refers to the server IP joseph AMAZON IP 
	private static String host = "server.quillchat.com"; //Reed home IP
	//private String host = "108.18.116.197"; //REED IP
	//private String host = "162.203.100.133"; //new Joseph IP
	private User identifier; //Your unique identifier
	private String myUsername; //Username of this client
	private String recipient; //Recipient of current message
	private int portNumber = 6969;	//Port the program runs on
	private ObjectInputStream reader;  // stream used to read the server's response
	private Socket serverSocket; // connection to the server
	private InetAddress serverIP; // get IP
	private static final Object o = new Object(); // synchronization
	private static final Object internal = new Object(); //Alert for internal messages
	private static final Object recipientChange = new Object(); //Recipient changes?
	/* 
	 * make sure all messages send before closing program
	 * This is only important with slow internet, when the logout message
	 * cannot be sent in time before the program closes.
	 */
	private static final Object sendLock = new Object();
	private InternalMessage currentInternalMessage; //Internal message to be evaluated
	private MainWindow mainWindow; // associated MainWindow, for printing
	private boolean register = false; //Registration request

	/**
	 * @param u - What user is using this IMClient. Used for printing
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

		if (args != null && args.length > 0) {
			host = args[0];
		}

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
			switch (currentInternalMessage.getMessage()) {
				case "$true$":
					System.out.println("Registration successful, welcome " +
							identifier.getCredentials().getUsername());
					break;
				case "$duplicate$":
					JOptionPane.showMessageDialog(new JFrame(), "User already has account",
							"Registration Error", JOptionPane.ERROR_MESSAGE);
					IMClient.main(null); // Restart program

					break;
				default:
					JOptionPane.showMessageDialog(new JFrame(), "Registration write error",
							"Registration Error", JOptionPane.ERROR_MESSAGE);
					IMClient.main(null); // Restart program

					break;
			}
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				synchronized(internal) {
					try {
						internal.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		mainWindow = new MainWindow(o, recipientChange, sendLock,
				identifier.getCredentials().getUsername());

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
						} else if (message instanceof External) {
							if (!Objects.equals(myUsername, recipient) && !recipient.equals("No users online")) {
								//Starts send message thread
								new Thread(new Sender(this, new ExternalMessage
										(myUsername, recipient, ((External) message).
												getMessage()))).start();
							} else if (recipient.equals("No users online")) {
								printToUI();
							}
						} else { //Image message
							new Thread(new Sender(this, new ImageMessage
									(myUsername, recipient, ((ImageMessage)message).getImage()))).start();
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
	 * Sets the displayed textPane to the parameter.
	 * @param doc The text pane to display.
	 */
	public void setDocument(StyledDocument doc) {
		mainWindow.setDocument(doc);
	}

	/**
	 * Method responsible for sending messages. Connects to the server and sends
	 * the message, including destination IP information.
	 * @param messageOut - The message to be sent
	 * @throws IOException If a problem in the ObjectOutputStream occurs.
	 */
	public void outgoing(Message messageOut) throws IOException {
		ObjectOutputStream writer = new ObjectOutputStream(new ObjectOutputStream(
				serverSocket.getOutputStream()));

		writer.writeObject(messageOut);
		writer.flush();

		//Notify that the client is done sending a message
		synchronized(sendLock) {
			sendLock.notifyAll();
		}
		//Don't close the ObjectOutputStream, it closes the socket in use!
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
					handleInternalMessage(tempIM);
				} else if (temp instanceof ExternalMessage) { 
					ExternalMessage response = (ExternalMessage)temp;
					handleExternalMessage(response);
				} else { //ImageMessage
					ImageMessage response = (ImageMessage)temp;
					handleImageMessage(response);
				}
			}
		}
	}

	private void handleImageMessage(ImageMessage response) {
		//TODO drag images into frame to upload
		BufferedImage bi = ClientUtils.decodeImage(response.getImage());
		//Creates a quick frame to display received image
		printMessage(response.getSender(), "sent an image.");
		JFrame f = new JFrame();
		f.setLocation(200, 200);
		f.setBackground(new Color(173, 173, 173));
		f.setTitle("Image from " + response.getSender());
		f.getContentPane().add(new ImageLayer(bi));
		f.pack();
		f.setVisible(true);
	}

	private void handleExternalMessage(ExternalMessage response) {
		try {
			String decryptedMessage = response.getDecryptedMessage();

			System.out.println("Received message: " + decryptedMessage);
			//MainWindow.pingTaskbar(mainWindow);
			//Mark up string to insert
			printMessage(response.getSender(), decryptedMessage, response.getTimestamp());
		} catch (KeyException |IllegalBlockSizeException e) {
			e.printStackTrace();
		}

		//Alert new message in UI if tab is not currently selected
		/*if (!mainWindow.getList().getSelectedValue().equals(conversationPartner)) {
			mainWindow.rerenderCell(conversationPartner);
		} */
	}

	private void printMessage(String sender, String message, Date timestamp) {
		//Document doc = mainWindow.getTextPane().getDocument();
		Document doc = mainWindow.conversations.get(sender);

		//Set colors and styles
		SimpleAttributeSet usernameStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(usernameStyle, new Color(52, 52, 52));
		StyleConstants.setBold(usernameStyle, true);

		SimpleAttributeSet messageStyle = new SimpleAttributeSet();
		StyleConstants.setForeground(messageStyle, new Color(255, 255, 255));

		try {
			//Handle timestamp
			if (timestamp != null) {
				SimpleAttributeSet timestampStyle = new SimpleAttributeSet();
				StyleConstants.setForeground(timestampStyle, new Color(0, 0, 0));

				SimpleDateFormat fmt = new SimpleDateFormat("h:mm:ss a");

				doc.insertString(doc.getLength(), "[" + fmt.format(timestamp) + "]", timestampStyle);

			}

			doc.insertString(doc.getLength(), sender +
					":", usernameStyle);

			doc.insertString(doc.getLength(), " " + message +
					"\n", messageStyle);

		} catch (Exception e) {
			e.printStackTrace();
		}

		//Scroll to bottom
		//TODO this doesn't scroll properly if the message is very long
		mainWindow.getScrollPane().getVerticalScrollBar().
				setValue(mainWindow.getScrollPane().
						getVerticalScrollBar().getMaximum() + 1);
	}

	private void printMessage(String sender, String message) {
		printMessage(sender, message, null);
	}

	private void handleInternalMessage(InternalMessage tempIM) {
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
					//No user text box
					if (mainWindow.conversations.get("No users online") == null) {
						mainWindow.conversations.put("No users online", (StyledDocument)new DefaultStyledDocument());
						System.out.println("New document for No users online created.");
						mainWindow.setDocument(mainWindow.conversations.get("No users online"));
					}
				} else {
					for(String s: names) {
						if (!s.equals(myUsername)) {
							mainWindow.getList().addToList(s);

							//Add text boxes for clients
							if (mainWindow.conversations.get(s) == null) {
								mainWindow.conversations.put(s, (StyledDocument)new DefaultStyledDocument());
								System.out.println("New document for " + s + " created.");
							}
						}
					}
				}

				//Make sure you always have a selection
				if (mainWindow.getList().getLength() == 1) {
					mainWindow.getList().setSelectedIndex(0);
					setRecipient(mainWindow.getList().getSelectedValue());
				} else if (mainWindow.getList().getSelectedValue() == null) {
					mainWindow.getList().setSelectedIndex(0);
					setRecipient(mainWindow.getList().getSelectedValue());
				}	

				//One person online document selection
				if (mainWindow.getList().getLength() == 1 && mainWindow.getList().getSelectedValue() != null &&
						!mainWindow.getList().getSelectedValue().equals("No users online")) {
					mainWindow.setDocument(mainWindow.conversations.get(mainWindow.getList().getSelectedValue()));
				}
			} else if (tempIM.getMessage().contains("key:")) {
				String messageText = tempIM.getMessage();
				//Get secret key from part of authentication message
				if (messageText.contains("$authenticated$")) {
					String secretKey = messageText.substring(messageText.indexOf(":") + 1);

					//Initialize encryption
					MessageCrypt m = MessageCrypt.getInstance();

					if (!m.isInitialized()) {
						try {
							m.init(secretKey);
						} catch (KeyAlreadySetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		currentInternalMessage = tempIM;

		//Notify that an internal message is received
		synchronized(internal) {
			internal.notifyAll();
		}
	}

	/**
	 * @param recipient the recipient to set
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * @return The conversation list associated with this client.
	 */
	public HashMap<String, StyledDocument> getConversations() {
		return mainWindow.conversations;
	}

	public void revalidate() {
		mainWindow.revalidate();
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
