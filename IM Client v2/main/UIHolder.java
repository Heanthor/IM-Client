package main;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.*;

public class UIHolder implements Runnable {

	static String messageOut; 
	static String messageIn;
	static JFrame frame = new JFrame();

	public UIHolder (){
		messageOut = "";
	}

	public static void main(String[] args) throws Exception {
		UIHolder i = new UIHolder();

		i.UI();
		
		System.out.println("Repainting");
		
		while (true)
		frame.repaint();
	}

	private void UI() {

		frame = new JFrame();
		frame.setBounds(150, 150, 350, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		//Input Field
		final JTextField text = new JTextField();
		text.setText("Enter message here");
		text.setBounds(20, 30, 250, 34);
		frame.getContentPane().add(text);
		text.setColumns(10);

		//Incoming Message

		JLabel incoming = new JLabel(messageIn);
		incoming.setBounds(20, 0, 350, 34);
		frame.getContentPane().add(incoming);

		//Buttons
		JButton buttonSend = new JButton("Send");
		JButton buttonClose = new JButton("Close");
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

	public void communicator(String message, String type, int port) throws Exception{
		String host = "162.203.101.47";
		String response;	//Store response
		PrintWriter writer;  //Send request to server
		BufferedReader reader;
		Socket clientSocket;
		InetAddress serverIP;

		//Get host name from DNS
		serverIP = InetAddress.getByName(host);

		//Create socket
		clientSocket = new Socket(serverIP, port);

		// open both streams to communicate with the server
		writer= new PrintWriter(new OutputStreamWriter(
				clientSocket.getOutputStream()));
		reader= new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));

		//If the thread running is sending
		if (type.equals("send")) {

			writer.println(message);
			writer.flush();
			//Refreshes messageOut
			messageOut = "";

		} else { //The thread is receiving
			while (true) {
				response = reader.readLine();
				messageIn = response;
			}
		}
		clientSocket.close();
	}

	@Override
	public void run() {
	}
}