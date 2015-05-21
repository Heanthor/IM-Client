package main;
// the client part of the example.  To run both programs, run the server
// first, then run the client, that will connect to it.  Using localhost
// means the client will connect to the server running on the same machine.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class IMClient extends Thread {
	String host = "162.203.101.47";  // refers to the server IP
	String identifier; //Your unique identifier
	int portNumber = 6969;	//Port the program runs on
	List<String> message = new ArrayList<String>();  //Data to be sent
	String response; // reads the server's response
	ObjectOutputStream writer;  // stream used to send request to the server
	BufferedReader reader;  // stream used to read the server's response
	Socket serverSocket; 
	InetAddress serverIP;

	public String messageFromWindow;

	public IMClient(String username) {
		identifier = username;

		//Opens connection to server
		try {
			serverIP= InetAddress.getByName(host);
			serverSocket = new Socket(serverIP, portNumber);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JFrame(), e.toString(),
					"IM Server not running", JOptionPane.ERROR_MESSAGE);
			//Quit program since it is useless if server is not running
			System.exit(1);
		}
	}

	public static void main(String args[])  {
		//LoginWindow blocks until the "OK" button is pressed with a correct username
		Object o = new Object();
		LoginWindow w = new LoginWindow(o);
		MainWindow m = new MainWindow(o);
		IMClient client = new IMClient(w.getUsername());

		//Starts incoming message scanner
		client.start();

		while (true) {
			try {
				synchronized(o) {
					o.wait();
					String message = m.getMessage();

					//Starts send message thread
					new Thread(new Sender(client, message)).start();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//Client sending messages
	public void outgoing(String messageOut) throws Exception {
		writer= new ObjectOutputStream(new ObjectOutputStream(
				serverSocket.getOutputStream()));

		message.add("72.45.15.42"); //Recipient IP -- outdated
		message.add(identifier); //Recipient unique identifier
		message.add(messageOut); //Message

		writer.writeObject(message);
		writer.flush();
	}

	//Incoming messages to the client
	public void incoming() throws Exception {
		while (true) {
			System.out.println("Incoming...");
			reader = new BufferedReader(new InputStreamReader(
					serverSocket.getInputStream()));

			//Protects against null output, shows real messages only
			String temp = reader.readLine();

			if (temp != null) {
				if (temp.equals("$ping")) {
					//handled
				} else {
					response = temp;
					System.out.println("Received message: " + response);
				}
			}
		}
	}

	public void run() {
		try {
			incoming();
		} catch (Exception e) {
			System.out.println("%%%%%%%%%%%%Server not running.%%%%%%%%%%%");
			e.printStackTrace();
		}
	}
	/*
	//Accepts message from user, calls outgoing().
	//Driver.start is called in LoginWindow
	public void run() {
		String messageOut = messageFromWindow;

		try {
			//Send to outgoing thread
			System.out.println("Message sending in run(): " + messageOut);
			outgoing(messageOut);
		} catch (Exception e) {

			System.out.println("Exception in run()");
			e.printStackTrace();

			System.out.println("Exception " + e + " caught in run()");
			e.printStackTrace();
		}
	}
	 */
}
