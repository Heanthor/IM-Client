package main;
// the client part of the example.  To run both programs, run the server
// first, then run the client, that will connect to it.  Using localhost
// means the client will connect to the server running on the same machine.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import login.*;
import messages.External;
import messages.ExternalMessage;
import messages.Internal;
import messages.InternalMessage;

/**
 * The IMClient is the client portion of the chat program.
 * An IMServer must be running at the correct IP, otherwise an error will occur
 * when the program is launched. This class contains a main method that launches
 * the UI elements, and then handles communications with the server.
 * @author Reed
 */
public class IMClient implements Runnable {
	//private String host = "162.203.101.47";  // refers to the server IP
	private String host = "52.10.127.193";  // refers to the server IP AMAZON IP
	private User identifier; //Your unique identifier
	private int portNumber = 6969;	//Port the program runs on
	private ObjectInputStream reader;  // stream used to read the server's response
	private Socket serverSocket; // connection to the server
	private InetAddress serverIP; // get IP
	private static Object o = new Object(); // synchronization
	private MainWindow mainWindow; // associated MainWindow, for printing

	/**
	 * @param username - What user is using this IMClient. Used for printing 
	 * namestamp in chat, and for identification.
	 */
	public IMClient(User u) {
		identifier = u;

		//Opens connection to server
		try {
			serverIP = InetAddress.getByName(host);
			serverSocket = new Socket(serverIP, portNumber);
		} catch (IOException e) {
			e.printStackTrace();

			JOptionPane.showMessageDialog(new JFrame(), e.toString(),
					"IM Server not running", JOptionPane.ERROR_MESSAGE);

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
		User u = new User(PermissionLevel.USER,
				new Credentials(w.getUsername(), w.getPassword()));

		IMClient client = new IMClient(u);
		client.init();
	}

	/**
	 * Launches the main threads for the client.
	 */
	private void init() {
		//If the first part of the username contains the register code
		if (identifier.getCredentials().getUsername().length() > 10 && 
				identifier.getCredentials().getUsername().
				substring(0, 10).equals("$register$")) {
			
			System.out.println("Register new user.");
			//Register the user
			new Thread(new Sender(this, new InternalMessage("test", identifier, "test", "$register$"))).start();
		}
		
		mainWindow = new MainWindow(o, identifier.getCredentials().getUsername());

		//Starts incoming message scanner
		new Thread(this).start();

		//Lets server know client is "connected"
		new Thread(new Sender(this, new InternalMessage("test", identifier, "test", "$connected$"))).start();

		while (true) {
			try {
				synchronized(o) {
					o.wait(); // main thread waits
					Message message = mainWindow.getMessage();

					if (message instanceof Internal) {
						new Thread(new Sender(this, new InternalMessage
								("test", identifier, "test", ((Internal) message)
										.getCode()))).start();
					} else {
						//Starts send message thread
						new Thread(new Sender(this, new ExternalMessage
								("test", "test", ((External) message).
										getMessage()))).start();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
		//Don't close the ObjectOutputStream, it closes the socket in use!
	}

	/**
	 * Method responsible for handling incoming messages to the client.
	 * Reads the message, and prints it to the console and UI.
	 * @throws IOException if a problem in the InputStreamReader occurs.
	 */
	public void incoming() throws IOException {
		while (true) {
			reader = new ObjectInputStream(
					serverSocket.getInputStream());

			//Protects against null output, shows real messages only
			Object temp = null;
			try {
				temp = reader.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			if (temp != null) {
				if (temp instanceof InternalMessage) {
					//TODO add internal message support
					System.out.println("(temp) Internal message" + temp);
				} else { //External message
					ExternalMessage response = (ExternalMessage)temp;
					System.out.println("Received message: " + response.getMessage());
					mainWindow.getTextArea().
					append(response.getSender() + ": " + response.getMessage() + "\n");

					//Scroll to bottom
					mainWindow.getScrollPane().getVerticalScrollBar().
					setValue(mainWindow.getScrollPane().
							getVerticalScrollBar().getMaximum());
				}
			}
		}
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
