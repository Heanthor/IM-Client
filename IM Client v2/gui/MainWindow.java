package gui;

import messages.External;
import messages.ImageMessage;
import messages.Internal;
import messages.Message;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

/**
 * The main UI window used to send and receive messages, and to display everything
 * else going on in the program. Originally generated with WindowBuilder, so is
 * a little messy.
 * @author Reed
 *
 */
public class MainWindow {
	private JFrame frmReedreadV;
	private JTextPane textPane; //TODO add html support
	//http://stackoverflow.com/questions/14038703/how-can-i-add-hyperlinks-to-a-jtextpane-without-html
	private JScrollPane scrollPane;
	private FriendsList list;
	private JTextField txtEnterMessage;
	private JButton btnNewButton;

	private Message message;
	private static Object o;
	private static Object listUpdate;
	private static Object sendLock; //Credit to joseph3114 for this idea
	private String username;

	public HashMap<String, StyledDocument> conversations = new HashMap<String, StyledDocument>(); //Store separate conversations

	public MainWindow(Object o, Object listUpdate, Object sendLock, String username) {
		MainWindow.o = o;
		MainWindow.listUpdate = listUpdate;
		MainWindow.sendLock = sendLock;
		this.username = username;
		initialize();
		//frmReedreadV.setVisible(true);
	}

	//TODO make this flash by opening 0x0 window on message
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainWindow t = new MainWindow(new Object(), new Object(),
						new Object(), "test");
				t.setVisible(true);
			}
		});
	}

	private void initialize() {
		//Set native LnF
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		frmReedreadV = new JFrame();
		frmReedreadV.setTitle("Quillchat v2");
		Image i = Toolkit.getDefaultToolkit()
				.getImage(FriendsListRenderer.class.getResource("logos/Quillchat-30.png"));	
		frmReedreadV.setIconImage(new ImageIcon(i).getImage());
		frmReedreadV.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				//Notifies the server that this client is logging out.
				message = new Internal("$logout$");
				synchronized(o) {
					o.notifyAll();
				}

				//Make sure message is done sending before closing the program
				synchronized(sendLock) {
					try {
						sendLock.wait(10000);
						System.out.println("Message is done sending. Closing.");
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				System.out.println("Window closing");
			}
		});
		frmReedreadV.getContentPane().setLayout((new BoxLayout(frmReedreadV.getContentPane(), BoxLayout.Y_AXIS)));
		frmReedreadV.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frmReedreadV.setBounds(100, 100, 550, 600);
		frmReedreadV.getContentPane().setBackground(new Color(173, 173, 173));

		JPanel stretchyPanel = new JPanel();
		stretchyPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		frmReedreadV.getContentPane().add(stretchyPanel);
		stretchyPanel.setLayout(new CardLayout(0, 0));

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//Default text pane -- can change
		textPane = new JTextPane();
		textPane.setFont(new Font(textPane.getFont().getName(), Font.PLAIN, 15));
		textPane.setBackground(new Color(153, 153, 153));
		textPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		textPane.setMargin(new Insets(2, 5, 5, 2));
		//textArea.setLineWrap(true); /******************************************* broken/
		textPane.setEditable(false); 

		scrollPane = new JScrollPane(textPane);
		scrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		list = new FriendsList(listUpdate);
		list.addToList("");

		c.fill = GridBagConstraints.BOTH;
		c.weightx = .8;
		c.weighty = 1;
		panel.add(scrollPane, c);
		c.weightx = .2;
		panel.add(list.frmUserList, c);

		stretchyPanel.add(panel);

		JPanel panel_2 = new JPanel();
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

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(173, 173, 173));
		frmReedreadV.getContentPane().add(panel_1);

		btnNewButton = new JButton("Send");
		btnNewButton.setBackground(new Color(173, 173, 173));
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

		JButton btnNewButton_1 = new JButton("Logout");
		btnNewButton_1.setBackground(new Color(173, 173, 173));
		btnNewButton_1.addActionListener(new ActionListener() {
			//Logout
			public void actionPerformed(ActionEvent e) {
				//Notifies the server that this client is logging out.
				message = new Internal("$logout$");

				synchronized (o) {
					o.notifyAll();
				}

				//Make sure message is done sending before closing the program
				synchronized (sendLock) {
					try {
						sendLock.wait(10000);
						System.out.println("Message is done sending. Closing.");
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}

				System.exit(0);
			}
		});
		panel_1.add(btnNewButton_1);

		JButton btnSendImage = new JButton("Send Image");
		btnSendImage.setBackground(new Color(173, 173, 173));
		btnSendImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Send image
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Images", "jpg", "jpeg", "gif", "png");
				chooser.setFileFilter(filter);

				int returnVal = chooser.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: " +
							chooser.getSelectedFile().getName());

					message = new ImageMessage(null, null, chooser.getSelectedFile());

					synchronized (o) {
                        o.notifyAll(); //Message is ready!
                    }
				}
			}
		});

		panel_1.add(btnSendImage);

		txtEnterMessage.requestFocus();
	}

	private boolean sendMessage() {
		if (!txtEnterMessage.getText().equals("") &&
				!txtEnterMessage.getText().equals("Enter Message...") &&
				!txtEnterMessage.getText().matches("^[\\s]*$")) { //Doesn't match all space characters

			//Sets message
			message = new External(txtEnterMessage.getText());
			//textArea.append(username + ": " + ((External)message).getMessage() + "\n");
			StyledDocument doc = textPane.getStyledDocument();

			//Set colors
			SimpleAttributeSet usernameStyle = new SimpleAttributeSet();
			StyleConstants.setForeground(usernameStyle, new Color(76, 76, 76));
			StyleConstants.setBold(usernameStyle, true);

			SimpleAttributeSet messageStyle = new SimpleAttributeSet();
			StyleConstants.setForeground(messageStyle, new Color(255, 255, 255));

			try {
				doc.insertString(doc.getLength(), username + ":", usernameStyle);
				doc.insertString(doc.getLength(), " " + ((External)message).
						getMessage() + "\n", messageStyle);

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

	/**
	 * Sets message pane's default text.
	 */
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

	/**
	 * Sets this window visible according to condition.
	 * @param b
	 */
	public void setVisible(boolean b) {
		frmReedreadV.setVisible(b);
	}

	/**
	 * Revalidates this window.
	 */
	public void revalidate() {
		frmReedreadV.revalidate();
	}

	/**
	 * Message getter.
	 * @return Current message
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * Returns the textPane used for displaying all messages.
	 * @return
	 */
	public JTextPane getTextPane() {
		return textPane;
	}

	/**
	 * Returns the scrollPane used to hold the textPane.
	 * @return
	 */
	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	/**
	 * Sets the displayed textPane to the parameter.
	 * @param doc The text pane to display.
	 */
	public void setDocument(StyledDocument doc) {
		textPane.setDocument(doc);
	}

	//work in progress
	private void makeClickableLink(String link, JTextPane textPane) {
		StyledDocument doc = textPane.getStyledDocument();

		Style linkBlue = doc.addStyle("linkBlue",  StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE));
		StyleConstants.setForeground(linkBlue, Color.BLUE);
		StyleConstants.setUnderline(linkBlue, true);

	}

	//Not currently using this
	public void rerenderCell(String cellName) {
		FriendsListRenderer r = (FriendsListRenderer)list.getRenderer();
		int index = list.getIndexOfValue(cellName);

		r.setFlag(false); // Add alert icon
		r.getListCellRendererComponent(list.list, cellName, index, false, false);
		r.setFlag(true); //Reset icon adding
	}

	public static void pingTaskbar(MainWindow focusCallback) {
		JFrame ping = new JFrame();
		ping.setUndecorated(true);
		ping.setBounds(0, 0, 1, 1);
		ping.setVisible(true);
		ping.toFront();
		ping.setVisible(false);
	}
}
