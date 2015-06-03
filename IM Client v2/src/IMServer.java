package src;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;

import filter.BloomFilter;
import login.*;
import messages.InternalMessage;
import messages.Message;

/**
 * The IMServer is the server portion of the chat program.
 * The port in use is defined in the main method, and must match the client's 
 * port. The server can handle multiple connections with multiple users, and
 * will keep a list of login information and associated IP addresses for easy
 * connection.
 * 
 * @author Reed
 */
//TODO two people cannot connect from the same IP
public class IMServer implements Runnable {
	private String recipientIP;
	private Message message;
	private Socket clientSocket;
	private static ArrayList<String> userList = new ArrayList<String>(); //To be sent to client's userlists
	private static ArrayList<String> connectedIPs = new ArrayList<String>();
	private static Object o = new Object(); // Synchronizing
	private LoginServer loginServer = new LoginServer("users/users.ser"); //Authentication
	private boolean loopInput = true; // Controls looping IO for one connection
	private static DebugListener debug = new DebugListener();

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
		new Thread(debug).start();

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
			String rIP;
			try {
				//Receive message
				if ((rIP = receive()) != null) {
					//Send message
					if (send(rIP)) {
						System.out.println("Sent message");
					} else {
						System.out.println("Did not send message.");
					}
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
	 * @return the recipient IP of message if meant to be passed on, or null if not.
	 * @throws IOException if the input is corrupted.
	 */
	public String receive() throws IOException {
		ObjectInputStream reader = null;
		Message rawInput = null;
		String rIP = null;

		//Opens input stream to read message
		try {
			reader = new ObjectInputStream(
					new ObjectInputStream(clientSocket.getInputStream()));
		} catch (IOException e) {
			System.err.println("ObjectInputStream exception");
			//TODO alt-f4 still causes this error
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

		if (rawInput instanceof InternalMessage) {
			InternalMessage temp = (InternalMessage)rawInput;
			String str = temp.getMessage();

			/*
			 * Process the three types of InternalMessages,
			 * connected notification, registration notification,
			 * logout notification. These all return false to break the loop.
			 */
			if (str.equals("$connected$")) {
				connected(rawInput, temp);
				return null;
			} else if (str.equals("$register$")) {
				register(rawInput, temp);
				return null;
			} else if (str.equals("$logout$")) {
				logout(rawInput);
				return null;
			}
		} else {
			message = rawInput;
			rIP = identifiers(rawInput);
		}

		return rIP;
	}

	/**
	 * Removes this IP from the list of connected IPs, and updates userlist
	 * to notify all clients of disconnected client.
	 * @param rawInput
	 * @return false
	 */
	private boolean logout(Message rawInput) {
		connectedIPs.remove(clientSocket.getInetAddress().toString());

		String username = ((InternalMessage) rawInput).getUser().
				getCredentials().getUsername();

		userList.remove(username);
		updateUserList();

		System.out.println("Client " +
				clientSocket.getInetAddress().toString() + " disconnected.");

		loopInput = false;

		//Close the associated socket
		try {
			openConnections.get(clientSocket.getInetAddress().toString().substring(1)).close();
			openConnections.remove(clientSocket.getInetAddress().toString().substring(1));
		} catch (IOException e) {
			System.err.println("Error closing socket in logout");
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Registers new user returning appropriate response for the request
	 * @param rawInput
	 * @param temp casted message
	 * @return false
	 */
	private boolean register(Message rawInput, InternalMessage temp) {
		if (!connectedIPs.contains(clientSocket.getInetAddress().toString())) {
			connectedIPs.add(clientSocket.getInetAddress().toString());
		}

		try {
			String rip = clientSocket.getInetAddress().toString().substring(1);
			//Register new user, returns the results to the client.
			if (loginServer.newUser(((InternalMessage) rawInput).getUser().getCredentials())) {
				message = new InternalMessage(temp.getUser(), "$true$");
				System.out.println("Registration successful");
			} else {
				message = new InternalMessage(temp.getUser(), "$duplicate$");
				System.err.println("Registration failed - duplicate user");
				loopInput = false;
			}

			//sends response message
			send(rip);
		} catch (IOException e) { //Serialize failed
			message = new InternalMessage(temp.getUser(), "$false$");
			System.err.println("Registration failed - write error");
			loopInput = false;
		}

		return false;
	}

	/**
	 * New client has connected. Authenticate the client, and send back
	 * an appropriate response. Additionally, update all clients' list of users,
	 * if this client has connected successfully.
	 * @param rawInput 
	 * @param temp Casted message
	 * @return false
	 * @throws IOException
	 */
	private boolean connected(Message rawInput, InternalMessage temp)
			throws IOException {
		if (!connectedIPs.contains(clientSocket.getInetAddress().toString())) {
			connectedIPs.add(clientSocket.getInetAddress().toString());
		}

		//Handle printing to identifiers.txt
		if (rawInput != null) {
			identifiers(rawInput);
		}

		System.out.println("Client " +
				clientSocket.getInetAddress().toString() + " connected.");

		//Authenticate user
		AuthenticateResponse r = loginServer.authenticate(temp.getUser().getCredentials());

		//Send the results of authentication back to client
		if (r.reponseCode == AuthenticateResponse.RESPONSE_AUTHENTICATED) {
			String username = ((InternalMessage) rawInput).getUser().
					getCredentials().getUsername();
			userList.add(username);
			updateUserList();

			message = new InternalMessage(temp.getUser(), "$authenticated$");
			System.out.println("Authenticated " + clientSocket.getInetAddress().toString());
		} else if (r.reponseCode == AuthenticateResponse.RESPONSE_WRONG_PASSWORD) {

			message = new InternalMessage(temp.getUser(), "$wrong_password$");
			System.out.println("Wrong password on " + clientSocket.getInetAddress().toString());
			loopInput = false;
		} else if (r.reponseCode == AuthenticateResponse.RESPONSE_USERNAME_NOT_FOUND) {

			message = new InternalMessage(temp.getUser(), "$username_not_found$");
			System.out.println("Username not found on " + clientSocket.getInetAddress().toString());
			loopInput = false;
		}

		send(clientSocket.getInetAddress().toString().substring(1)); //Send to self

		return false; // Don't send message
	}

	/**
	 * Stores and reads data for users' IP addresses, and updates the list if 
	 * necessary.
	 * @param rawInput
	 * @throws IOException
	 */
	private String identifiers(Message rawInput) throws IOException {
		//Handle message
		String toReturn = null;

		BufferedReader fileReader = new BufferedReader
				(new FileReader("users/identifiers.txt"));

		//IP not found
		/* Saves identifier and InetAddress to a file in form
				/* <identifier> /<ip address> */
		//TODO read these files to memory on start of server, move this to before anything is processed
		String identifier = rawInput.getSender();
		if (identifier == null) { //InternalMessage
			identifier = ((InternalMessage)rawInput).getUser().getCredentials().getUsername();
		}

		BufferedWriter fileWriter = new 
				BufferedWriter(new PrintWriter(new FileWriter("users/identifiers.txt", true)));

		String checker;
		if ((checker = contains(identifier)) != null &&
				!checker.equals(identifier + " " + 
						clientSocket.getInetAddress())) { //IP for a user changed
			System.out.println("IP for \"" + identifier + "\" has changed.");
			System.out.println("Difference: " + contains(identifier) + " : " + identifier + " " + clientSocket.getInetAddress() + "\n");
			replace(identifier, identifier + " " + 
					clientSocket.getInetAddress());

		} else if (checker != null) {
			toReturn = clientSocket.getInetAddress().toString().substring(1); //Trims /
		} else {
			fileWriter.write("\n" + identifier + " " + 
					clientSocket.getInetAddress());
			toReturn = clientSocket.getInetAddress().toString().substring(1); //Trims /

			fileWriter.flush();
			fileWriter.close();
		}

		String line;
		//Finds the first instance of the identifier in list, saves IP
		while ((line = fileReader.readLine()) != null) {
			line = line.replace("\n", ""); //trim away newlines
			if (line.contains(" ") && line.substring(0, line.indexOf(" ")).
					equals(rawInput.getRecipient())) {
				toReturn = line.substring
						(line.indexOf(" ") + 2); //Saves IP
				break;
			}
		}

		fileReader.close();

		return toReturn;
	}

	/**
	 *  Sends message to the intended client, after it received by receive().
	 *  @param recipientIP - The IP to send this message to.
	 *  @return true if message is sent, false otherwise.
	 */
	public boolean send(String recipientIP) {
		ObjectOutputStream writer = null;

		try {
			System.out.println("Attempting to open connection 2");
			Socket recipientSocket = openConnections.get(recipientIP);

			if (!recipientSocket.isClosed()) { //Hopefully reduce errors
				writer = new ObjectOutputStream(recipientSocket.getOutputStream());
				System.out.println("Opened conection to recipient\n");
			} else {
				System.err.println("Socket closed too early!");
			}
		} catch (IOException e) {
			System.err.println("Error in send(): ");
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
			if (line.contains(" ") && line.substring(0, line.indexOf(" ")).
					equals(name)) {
				fileReader.close();
				return line;
			}
		}

		fileReader.close();
		return null;
	}

	/**
	 * Sends new list of connected clients to every connected client.
	 */
	private void updateUserList() {
		System.out.println("Updating user list, sending to... ");
		String usrListMessage = "$list_update ";

		for (String s: userList) {
			usrListMessage += s + " ";
		}

		for (String s: connectedIPs) {
			String rip = s.substring(s.indexOf("/") + 1);
			System.out.println(recipientIP);
			message = new InternalMessage(null, usrListMessage);

			send(rip);
		}
	}

	/**
	 * Helper method used to write to the identifiers.txt method
	 * Will replace oldStr located in identifiers.txt with newStr.
	 * @param - oldStr String to replace
	 * @param - newStr String to replace with
	 * @return True if operation succeeds, false otherwise.
	 */
	public boolean replace(String oldStr, String newStr) {
		try {
			BufferedReader rd = new BufferedReader(new FileReader("users/identifiers.txt"));
			String line;
			String input = "";

			while ((line = rd.readLine()) != null) {
				line = line.replaceAll(oldStr + " .*", newStr);
				input += line + "\n";
			}

			BufferedWriter wr = new BufferedWriter(new PrintWriter("users/identifiers.txt"));

			wr.write(input);
			wr.flush();
			rd.close();
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	/**
	 * Prints contents of connectedIPs object
	 */
	public static void printConnections() {
		System.out.println("Connected IP list: ");

		System.out.print("[");
		for (String s: connectedIPs) {
			System.out.println(s + ", ");
		}

		System.out.println("]");
	}

	/**
	 * Prints contents of users.ser
	 * @param dir The location of users.ser
	 */
	public static void printUsers(String dir) {
		try {
			new LoginServer(dir).authenticate(new Credentials("_list_users", new BloomFilter()));
		} catch (NameTooLongException e) {
			e.printStackTrace();
		}
	}
}
