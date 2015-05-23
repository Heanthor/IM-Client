package main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import login.*;

/**
 * The IMServer is the server portion of the chat program.
 * The port in use is defined in the main method, and must match the client's 
 * port. The server can handle multiple connections with multiple users, and
 * will keep a list of login information and associated IP addresses for easy
 * connection.
 * 
 * @author Reed
 */
public class IMServer implements Runnable {
	private String recipientIP;
	private String message;
	private Socket clientSocket;
	private Socket recipientSocket;
	private ArrayList<String> connectedIPs = new ArrayList<String>();
	private static Object o = new Object(); // Synchronizing
	private boolean loopInput = true; // Controls looping IO for one connection

	public static TreeMap<String, Socket> openConnections = new TreeMap<String, Socket>();

	/**
	 * @param clientSocket - The sender's socket connection.
	 */
	public IMServer(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public static void main(String args[]) throws Exception {
		int portNumber = 6969;    // the same arbitrary unused port the clients use
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(portNumber);
		System.out.println("IM server is running.");

		while (true) {  // loop forever
			System.out.println("...");
			//Waits for connection, saves the socket
			Socket mainClientSocket = serverSocket.accept();

			//Passes the clientSocket to the thread to begin response
			IMServer runner = new IMServer(mainClientSocket);

			//Save open connection in collection
			String connectedIP = mainClientSocket.getInetAddress().toString()
					.substring(mainClientSocket.getInetAddress().toString()
							.indexOf("/") + 1);

			synchronized(o) {
				openConnections.put
				(connectedIP, mainClientSocket);
			}

			new Thread(runner).start();

			System.out.println("Started runner on: " 
					+ mainClientSocket.getInetAddress());
		}
	}

	public void run() {
		while(loopInput) {
			try {
				//Receive message
				if (receive()) {
					//Send message
					if (send()) {
						System.out.println("Sent message");
					} else {
						System.out.println("Did not send message.");
					}
				} else {
					System.out.println("Not proceeding to send.");
				}
			} catch (Exception e) {
				System.out.println("Exception in run method");
				e.printStackTrace();
			}
		}
		//TODO when the connection is done, close the open socket?
	}

	/**
	 * Receives message from a client. Is the first method that runs, always
	 * before send().
	 * @return true if message is meant to be passed on, false otherwise.
	 * @throws IOException if the input is corrupted.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public boolean receive() throws IOException {
		ObjectInputStream reader = null;
		List<String> rawInput = new ArrayList<String>();

		//Opens input stream to read message
		try {
			//TODO this errors upon closing the client with logout
			reader = new ObjectInputStream(
					new ObjectInputStream(clientSocket.getInputStream()));
		} catch (IOException e) {
			System.err.println("ObjectInputStream exception");
			e.printStackTrace();
		}

		// read the client's request, interpret message

		try {
			rawInput = (List<String>) reader.readObject();
		} catch (Exception e) {
			System.out.println("Exception in receive() after reading rawInput");
			e.printStackTrace();
		}
		//Connection to the client is done

		System.out.println("Received raw input: " + rawInput);

		/*TODO the server stores list of logins, and their associated IPs,
		 * and updates them when a new user is created. This is what this commented
		 * out stuff is trying to do. */
		//Handle message
		if (rawInput.size() > 0) { //Handles broken messages being sent
			/*BufferedReader fileReader = new BufferedReader
					(new FileReader("users/identifiers.txt"));

			String line;
			//Finds the first instance of the identifier in list, saves IP
			while ((line = fileReader.readLine()) != null) {
				if (line.substring(0, line.indexOf(" ")).
						equals(rawInput.get(1))) {
					recipientIP = line.substring
							(line.indexOf(" ") + 1); //Saves IP
					break;
				}
			}

			fileReader.close(); */
			/*String line;
			if ((line = contains(rawInput.get(1))) != null) {
				recipientIP = line.substring
						(line.indexOf(" ") + 1); //Saves IP
			}
			*/
			recipientIP = rawInput.get(0); //backup

			if(true) { //TODO if ip is in connectedIPs
				message = rawInput.get(2);
				message.substring(1);

				if (message.equals("$connected$")) {
					connectedIPs.add(clientSocket.getInetAddress().toString());
					System.out.println("Client " +
							clientSocket.getInetAddress().toString() + " connected.");

					//AuthenticateResponse r = authenticate();
					return false; // Don't send message
				}

				if (message.equals("$logout$")) {
					connectedIPs.remove(clientSocket.getInetAddress().toString());
					System.out.println("Client " +
							clientSocket.getInetAddress().toString() + " disconnected.");

					loopInput = false;
					return false;
				}

				/* Saves identifier and InetAddress to a file in form
				/* <identifier> /<ip address> */
				String identifier = rawInput.get(1);
				BufferedWriter fileWriter = new 
						BufferedWriter(new PrintWriter("users/identifiers.txt"));
				fileWriter.write(identifier + " " + 
						clientSocket.getInetAddress());

				fileWriter.flush();
				fileWriter.close();

				System.out.println("Sender IP: " + clientSocket.getInetAddress());
				System.out.println("Dest IP: " + recipientIP);
				System.out.println("Message: " + message); 
			} else {
				System.out.println("Recipient not connected.");
				return false;
			}
		} else {
			System.out.println("Message not received properly");
		}
		//Successfully parsed a message, or not
		if (!(rawInput.equals(new ArrayList<String>()))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 *  Sends message to the intended client, after it received by receive().
	 *  @return true if message is sent, false otherwise.
	 */
	public boolean send() {
		PrintWriter writer = null;

		try {
			System.out.println("Attempting to open connection 2");
			recipientSocket = openConnections.get(recipientIP);
			writer= new PrintWriter(
					new OutputStreamWriter(recipientSocket.getOutputStream()));
			System.out.println("Opened conection to recipient");
		} catch (IOException e) {
			System.err.println("IP Exception");
			e.printStackTrace();
		}

		// send message to recipient
		writer.println(message);

		// flush the stream, and close the socket
		writer.flush();

		if (message.length() != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Helper method for reading identifiers.txt file.
	 * @param name The username being searched for
	 * @return the line containing the name if found, or null if not found.
	 * @throws IOException
	 */
	public String contains(String name) throws IOException {
		BufferedReader fileReader = null;
		
		try {
			fileReader = new BufferedReader
					(new FileReader("users/identifiers.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String line;
		//Finds the first instance of the identifier in list, saves IP
		while ((line = fileReader.readLine()) != null) {
			if (line.substring(0, line.indexOf(" ")).
					equals(name)) {
				fileReader.close();
				return line;
			}
		}

		fileReader.close();
		return null;
	}
	
	//TODO when authenticating, use the sender User object in the InternalMessage to get his Credentials
	private AuthenticateResponse authenticate(User u) {
		LoginServer s = new LoginServer("/users/users.ser");

		return s.authenticate(u.getCredentials());
	}
}
