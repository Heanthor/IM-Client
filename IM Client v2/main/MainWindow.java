package main;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.*;

import messages.External;
import messages.Internal;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainWindow {
	private JFrame frmReedreadV;
	private JPanel stretchyPanel;
	private JPanel panel;
	private JTextPane textArea;
	private JScrollPane scrollPane;
	private FriendsList list;
	private JPanel panel_2;
	private JTextField txtEnterMessage;
	private JPanel panel_1;
	private JButton btnNewButton;
	private JButton btnNewButton_1;

	private Message message;
	private static Object o;
	private static Object listUpdate;
	private String username;


	public MainWindow(Object o, Object listUpdate, String username) {
		MainWindow.o = o;
		MainWindow.listUpdate = listUpdate;
		this.username = username;
		initialize();
		frmReedreadV.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				@SuppressWarnings("unused")
				MainWindow t = new MainWindow(new Object(), new Object(), "test");
			}
		});
	}

	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		frmReedreadV = new JFrame();
		frmReedreadV.setTitle("Quillchat v2");
		frmReedreadV.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				//Notifies the server that this client is logging out.
				message = new Internal("$logout$");
				synchronized(o) {
					o.notifyAll();
				}

				System.out.println("Window closing");
			}
		});
		frmReedreadV.getContentPane().setLayout((new BoxLayout(frmReedreadV.getContentPane(), BoxLayout.Y_AXIS)));
		frmReedreadV.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmReedreadV.setBounds(100, 100, 550, 600);

		stretchyPanel = new JPanel();
		frmReedreadV.getContentPane().add(stretchyPanel);
		stretchyPanel.setLayout(new CardLayout(0, 0));

		panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		textArea = new JTextPane();
		textArea.setMargin(new Insets(2, 5, 5, 2));
		//textArea.setLineWrap(true); /*******************************************/
		textArea.setEditable(false); 
		
		scrollPane = new JScrollPane(textArea);
		scrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		list = new FriendsList(listUpdate);

		c.fill = GridBagConstraints.BOTH;
		c.weightx = .8;
		c.weighty = 1;
		panel.add(scrollPane, c);
		c.weightx = .2;
		panel.add(list.frmUserList, c);

		stretchyPanel.add(panel);

		panel_2 = new JPanel();
		frmReedreadV.getContentPane().add(panel_2);
		panel_2.setLayout(new GridLayout(0, 1, 0, 0));

		txtEnterMessage = new JTextField();
		txtEnterMessage.addActionListener(new ActionListener() {
			@Override //Enter is pressed
			public void actionPerformed(ActionEvent arg0) {
				sendMessage();
				txtEnterMessage.setText(""); //Clear text
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
					setDefaultText();
				}
			}
		});
		
		panel_2.add(txtEnterMessage);
		txtEnterMessage.setColumns(10);

		panel_1 = new JPanel();
		frmReedreadV.getContentPane().add(panel_1);

		btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			//"Send" button press
			public void actionPerformed(ActionEvent e) {
				btnNewButton.requestFocus();
				if (sendMessage()) {
					setDefaultText();
				}
			}
		});
		panel_1.add(btnNewButton);

		btnNewButton_1 = new JButton("Logout");
		btnNewButton_1.addActionListener(new ActionListener() {
			//Logout
			public void actionPerformed(ActionEvent e) {
				//Notifies the server that this client is logging out.
				message = new Internal("$logout$");

				synchronized(o) {
					o.notifyAll();
				}

				System.exit(0);

			}
		});
		panel_1.add(btnNewButton_1);

		txtEnterMessage.requestFocus();
	}

	private boolean sendMessage() {
		if (!txtEnterMessage.getText().equals("") &&
				!txtEnterMessage.getText().equals("Enter Message...") &&
				!txtEnterMessage.getText().matches("^[\\s]*$")) { //Doesn't match all space characters

			//Sets message
			message = new External(txtEnterMessage.getText());
			//textArea.append(username + ": " + ((External)message).getMessage() + "\n");
			StyledDocument doc = textArea.getStyledDocument();
			SimpleAttributeSet keyWord = new SimpleAttributeSet();
			StyleConstants.setForeground(keyWord, Color.RED);
			StyleConstants.setBackground(keyWord, Color.YELLOW);
			StyleConstants.setBold(keyWord, true);
			try {
				doc.insertString(doc.getLength(), username + ": " + ((External)message).getMessage() + "\n", keyWord);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			//Scrolls to bottom
			JScrollBar vertical = scrollPane.getVerticalScrollBar();
			vertical.setValue(vertical.getMaximum());

			if (message != null) {
				synchronized(o) {
					o.notifyAll(); //Message is ready!
				}
			}

			return true; //Message sent
		} else {
			return false; //Message not sent
		}
	}

	private void setDefaultText() {
		txtEnterMessage.setForeground(Color.LIGHT_GRAY);
		txtEnterMessage.setText("Enter Message...");
	}

	/**
	 * @return the user list
	 */
	public FriendsList getList() {
		return list;
	}

	public void setVisible(boolean b) {
		frmReedreadV.setVisible(b);
	}
	
	/**
	 * Message getter.
	 * @return Current message
	 */
	public Message getMessage() {
		return message;
	}

	public JTextPane getTextPane() {
		return textArea;
	}
	public JScrollPane getScrollPane() {
		return scrollPane;
	}
}
