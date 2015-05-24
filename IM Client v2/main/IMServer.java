package main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import java.util.TreeMap;

import login.*;
import messages.ExternalMessage;
import messages.InternalMessage;

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
	private Message message;
	private Socket clientSocket;
	private Socket recipientSocket;
	private ArrayList<String> connectedIPs = new ArrayList<String>();
	private static Object o = new Object(); // Synchronizing
	private LoginServer loginServer = new LoginServer("users/users.ser"); //Authentication
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
		Message rawInput = null;

		//Opens input stream to read message
		try {
			//TODO this errors upon closing the client with logout
			reader = new ObjectInputStream(
					new ObjectInputStream(clientSocket.getInputStream()));
		} catch (IOException e) {
			System.err.println("ObjectInputStream exception");
			loopInput = false; //kills thread
			e.printStackTrace();
		}

		// read the client's request, interpret message

		try {
			rawInput = (Message) reader.readObject();
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
		if (rawInput != null) {
			BufferedReader fileReader = new BufferedReader
					(new FileReader("users/identifiers.txt"));

			String line;
			//Finds the first instance of the identifier in list, saves IP
			while ((line = fileReader.readLine()) != null) {
				if (line.substring(0, line.indexOf(" ")).
						equals(rawInput.getRecipient())) {
					recipientIP = line.substring
							(line.indexOf(" ") + 2); //Saves IP
					break;
				}
			}

			fileReader.close(); 

			//recipientIP = rawInput.get(0); //backup

			if(true) { //TODO if ip is in connectedIPs
				if (rawInput instanceof InternalMessage) {
					InternalMessage temp = (InternalMessage)rawInput;
					String str = temp.getMessage();

					if (str.equals("$connected$")) {
						connectedIPs.add(clientSocket.getInetAddress().toString());
						System.out.println("Client " +
								clientSocket.getInetAddress().toString() + " connected.");

						AuthenticateResponse r = loginServer.authenticate(temp.getUser().getCredentials());
						
						//Send the results of authentication back to client
						if (r.reponseCode == AuthenticateResponse.RESPONSE_AUTHENTICATED) {
							
							message = new InternalMessage(null, temp.getUser(), null, "$authenticated$");
							System.out.println("Authenticated " + clientSocket.getInetAddress().toString());
						} else if (r.reponseCode == AuthenticateResponse.RESPONSE_WRONG_PASSWORD) {
							
							message = new InternalMessage(null, temp.getUser(), null, "$wrong_password$");
							System.out.println("Wrong password on " + clientSocket.getInetAddress().toString());
						} else if (r.reponseCode == AuthenticateResponse.RESPONSE_USERNAME_NOT_FOUND) {
							
							message = new InternalMessage(null, temp.getUser(), null, "$username_not_found$");
							System.out.println("Username not found on " + clientSocket.getInetAddress().toString());
						}
						
						send();
						
						return false; // Don't send message
					}

					if (str.equals("$register$")) {
						//Register new user, returns the results to the client.
						if (loginServer.newUser(((InternalMessage) rawInput).getUser().getCredentials())) {
							message = new InternalMessage(null, temp.getUser(), null, "$true$");
						} else {
							message = new InternalMessage(null, temp.getUser(), null, "$false$");
						}
						
						//sends response message
						send();
						
						return false;
					}

					if (str.equals("$logout$")) {
						connectedIPs.remove(clientSocket.getInetAddress().toString());
						System.out.println("Client " +
								clientSocket.getInetAddress().toString() + " disconnected.");

						loopInput = false;
						return false;
					}
				} else {
					message = rawInput;
					ExternalMessage message = (ExternalMessage)rawInput;
					String str = message.getMessage();
					str.substring(1);

					/* Saves identifier and InetAddress to a file in form
				/* <identifier> /<ip address> */
					String identifier = rawInput.getSender();
					BufferedWriter fileWriter = new 
							BufferedWriter(new PrintWriter("users/identifiers.txt"));
					fileWriter.write(identifier + " " + 
							clientSocket.getInetAddress());

					fileWriter.flush();
					fileWriter.close();

					System.out.println("Sender IP: " + clientSocket.getInetAddress());
					System.out.println("Dest IP: " + recipientIP);
					System.out.println("Message: " + str); 
				}
			} else {
				System.out.println("Recipient not connected.");
				return false;
			}
		} else {
			System.out.println("Message not received properly");
		}
		//Successfully parsed a message, or not
		return true;
	}

	/**
	 *  Sends message to the intended client, after it received by receive().
	 *  @return true if message is sent, false otherwise.
	 */
	public boolean send() {
		ObjectOutputStream writer = null;

		try {
			System.out.println("Attempting to open connection 2");
			recipientSocket = openConnections.get(recipientIP);
			writer = new ObjectOutputStream(recipientSocket.getOutputStream());
			System.out.println("Opened conection to recipient");
		} catch (IOException e) {
			System.err.println("IP Exception");
			loopInput = false; //kills thread
			e.printStackTrace();
		}

		// send message to recipient
		try {
			writer.writeObject(message);
			// flush the stream
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (message != null) {
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
}
