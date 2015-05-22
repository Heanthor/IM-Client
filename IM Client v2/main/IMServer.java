package main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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

public class IMServer extends Thread {
	int portNumber= 6969; 
	String recipientIP;
	String message;
	Socket clientSocket;
	Socket recipientSocket;
	public static TreeMap<String, Socket> openConnections= new TreeMap<String, Socket>();
	public static ArrayList<String> connectedIPs = new ArrayList<String>();
	static Object o = new Object(); // Synchronizing
	private boolean loopInput = true;

	public IMServer (int portNumber, Socket clientSocket) {
		this.portNumber = portNumber; 
		this.clientSocket = clientSocket;
	}

	public static void main(String args[]) throws Exception {
		int portNumber= 6969;    // the same arbitrary unused port the clients use
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(portNumber);
		System.out.println("IM server is running.");
		/* //What a joke
		IMServer s = new IMServer();
		Ping p = s.new Ping();
		Thread t = new Thread(p);

		t.start(); //Starts infinite ping
		 */

		while (true) {  // loop forever
			System.out.println("... ");
			//Waits for connection, saves the socket
			Socket MainClientSocket = serverSocket.accept();

			//Passes the clientSocket to the thread to begin response
			IMServer runner = new IMServer(portNumber, MainClientSocket);

			//Checks if client has connected, cleans up list
			//runner.ping(MainClientSocket);

			//Save open connection in collection
			String connectedIP = MainClientSocket.getInetAddress().toString()
					.substring(MainClientSocket.getInetAddress().toString()
							.indexOf("/") + 1);

			synchronized(o) {
				openConnections.put
				(connectedIP, MainClientSocket);
			}

			runner.start();

			System.out.println("Started runner on: " 
					+ MainClientSocket.getInetAddress());
		}
	}

	public boolean ping(Socket in) {
		try {
			PrintWriter writer = new PrintWriter(in.getOutputStream());
			writer.write("$ping");
		} catch (IOException e) {
			System.out.println("Failed ping on " +
					in.getInetAddress().toString());
			openConnections.remove(in.getInetAddress().toString().substring
					(in.getInetAddress().toString().indexOf("/") + 1));
		}

		return false;
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
	}

	/* Receives message from a client. Is the first method that runs, always
	 * before send().*/
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
			recipientIP = rawInput.get(0); //backup

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
					String toRemove = clientSocket.getInetAddress().toString().
							substring(clientSocket.getInetAddress().toString().
									indexOf("/") + 1);

					int index = -1;
					for (int i = 0; i < connectedIPs.size(); i++) {
						if (connectedIPs.get(i).equals(toRemove)) {
							index = i;
							break;
						}
					}

					connectedIPs.remove(index); //If not found, will throw array out of bounds exception
					System.out.println("Client " +
							clientSocket.getInetAddress().toString() + " disconnected.");

					loopInput = false;
					return false;
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

		if (message.length() != 0) {
			return true;
		} else {
			return false;
		}
	}


	/* class Ping implements Runnable {

		public void ping(Socket in) {
			try {
				PrintWriter writer = new PrintWriter(in.getOutputStream());
				writer.write("$ping");
			} catch (IOException e) {
				System.out.println("Failed ping on " +
						in.getInetAddress().toString());
				e.printStackTrace();

				//Removes connection from tree if it is inactive
				synchronized(o) {
					openConnections.remove(in.getInetAddress().toString().substring
							(in.getInetAddress().toString().indexOf("/") + 1));
				}
			}
			//Ping is successful, still connected
			System.out.println("Successful ping");
		}

		@Override
		public void run() {
			while (true) {
				//Delay 5 seconds
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Collection<Socket> val = openConnections.values();
				Iterator<Socket> iter = val.iterator();
				while (iter.hasNext()) {
					Socket temp = iter.next();
					ping(temp);
				}
			}
		}
	} */
}
