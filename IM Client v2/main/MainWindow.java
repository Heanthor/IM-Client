package main;

import javax.swing.JFrame;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JTextArea;

import java.awt.CardLayout;

import javax.swing.JTextField;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainWindow {

	private JFrame frmReedreadV;
	private JTextField txtEnterMessage;
	private String message;
	private static Object o;
	private JTextArea textArea;
	private String username;
	/*
	 *//**
	 * Launch the application.
	 *//*
		public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmReedreadV.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	  */

	public MainWindow(Object o, String username) {
		MainWindow.o = o;
		this.username = username;
		initialize();
	}

	private void initialize() {
		frmReedreadV = new JFrame();
		frmReedreadV.setTitle("ReedRead v2");
		frmReedreadV.setBounds(100, 100, 454, 558);
		frmReedreadV.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmReedreadV.getContentPane().setLayout(new BoxLayout(frmReedreadV.getContentPane(), BoxLayout.Y_AXIS));
		frmReedreadV.setVisible(true);

		JPanel panel = new JPanel();
		frmReedreadV.getContentPane().add(panel);
		panel.setLayout(new CardLayout(0, 0));

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		panel.add(textArea, "name_49774492601421");

		JPanel panel_2 = new JPanel();
		frmReedreadV.getContentPane().add(panel_2);
		panel_2.setLayout(new GridLayout(0, 1, 0, 0));

		frmReedreadV.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {

			}

			@Override
			public void windowClosed(WindowEvent arg0) {

			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				//Notifies the server that this client is logging out.
				message = "$logout$";
				synchronized(o) {
					o.notifyAll();
				}

				System.out.println("Window closing");
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {

			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {

			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowOpened(WindowEvent arg0) {

			}

		});

		txtEnterMessage = new JTextField();
		txtEnterMessage.addFocusListener(new FocusAdapter() {
			//Focus gained in text
			@Override
			public void focusGained(FocusEvent e) {
				if (txtEnterMessage.getText().equals("Enter Message...")) {
					txtEnterMessage.setForeground(Color.BLACK);
					txtEnterMessage.setText("");
				}
			}
			//Resets placeholder text
			@Override
			public void focusLost(FocusEvent e) {
				if (txtEnterMessage.getText().equals("")) {
					txtEnterMessage.setForeground(Color.LIGHT_GRAY);
					txtEnterMessage.setText("Enter Message...");
				}
			}
		});
		txtEnterMessage.addMouseListener(new MouseAdapter() {
			//Mouse enters frame
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (txtEnterMessage.getText().equals("Enter Message...")) {
					txtEnterMessage.setForeground(Color.BLACK);
					txtEnterMessage.setText("");
				}
			}
		});
		txtEnterMessage.setForeground(Color.LIGHT_GRAY);
		txtEnterMessage.setText("Enter Message...");
		panel_2.add(txtEnterMessage);
		txtEnterMessage.setColumns(10);


		JPanel panel_1 = new JPanel();
		frmReedreadV.getContentPane().add(panel_1);

		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			//"Send" button press
			public void actionPerformed(ActionEvent e) {
				if (!txtEnterMessage.getText().equals("") &&
						!txtEnterMessage.getText().equals("Enter Message...")) {
					//Sets message
					message = txtEnterMessage.getText();
					textArea.append(username + ": " + message + "\n");

					if (message != null) {
						synchronized(o) {
							o.notifyAll(); //Message is ready!
						}
					}
				}
			}
		});
		panel_1.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Logout");
		btnNewButton_1.addActionListener(new ActionListener() {
			//Logout
			public void actionPerformed(ActionEvent e) {
				//Notifies the server that this client is logging out.
				message = "$logout$";
				synchronized(o) {
					o.notifyAll();
				}

				System.exit(0);
			}
		});
		panel_1.add(btnNewButton_1);
	}

	/**
	 * Message getter.
	 * @return Current message
	 */
	public String getMessage() {
		return message;
	}

	public JTextArea getTextArea() {
		return textArea;
	}
}
