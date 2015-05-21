package main;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ServerRunner implements Runnable {
	int portNumber; 
	String recipientIP;
	String message;
	Socket clientSocket;
	Socket recipientSocket;
	public static TreeMap<String, Socket> openConnections= new TreeMap<String, Socket>();
	public static ArrayList<String> connectedIPs = new ArrayList<String>();
	
	public ServerRunner(int portNumber, Socket in) {
		this.portNumber = portNumber;
		this.clientSocket = in;
	}
	
	public void run() {
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


	/* Receives message from a client. Is the first method that runs, always
	 * before send().*/
	@SuppressWarnings("unchecked")
	public boolean receive() throws IOException {
		ObjectInputStream reader = null;
		List<String> rawInput = new ArrayList<String>();

		//Opens input stream to read message
		try {
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

		//Handle message
		if (rawInput.size() > 0) { //Handles broken messages being sent
			/* Temporarily commented out
			 BufferedReader fileReader = new BufferedReader
					(new FileReader("identifiers.txt"));

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

			fileReader.close();
			 */
			recipientIP = rawInput.get(0);

			if(true) {

				message = rawInput.get(2);
				message.substring(1);

				if (message.equals("$connected$")) {
					connectedIPs.add(clientSocket.getInetAddress().toString());
					System.out.println("Client " +
							clientSocket.getInetAddress().toString() + " connected.");
					return false; // Don't send message
				}

				if (message.equals("$logout$")) {
					connectedIPs.remove(clientSocket.getInetAddress().toString());

					System.out.println("Client " +
							clientSocket.getInetAddress().toString() + " disconnected.");
				}

				//Saves identifier and InetAddress to a file
				String identifier = rawInput.get(1);
				BufferedWriter fileWriter = new 
						BufferedWriter(new PrintWriter("identifiers.txt"));
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

	/* Sends message to the intended client, after it received by receive()*/
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

		try {
			clientSocket.close();
			recipientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (message.length() != 0) {
			return true;
		} else {
			return false;
		}
	}

}
