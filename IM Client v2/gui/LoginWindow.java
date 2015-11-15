package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Window that appears first when running program, handles login information,
 * and the option to register users. Passes all processing to IMClient and MainWindow.
 * @author Reed
 *
 */
public class LoginWindow {
	private JTextField txtEnterUsername;
	private String username;
	private String password;
	private JDialog d = new JDialog(); // Main dialog window
	@SuppressWarnings("unused")
	private static Object o; //Synchronization
	private JPasswordField txtEnterPassword;
	private boolean register = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			LoginWindow dialog = new LoginWindow(new Object());
			dialog.d.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
			dialog.d.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 

	/**
	 * Create the dialog.
	 */
	public LoginWindow(final Object o) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		LoginWindow.o = o;
		d.setTitle("Quillchat v2.0 Login");
		d.setBounds(100, 100, 333, 155);
		d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		d.getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		d.getContentPane().add(contentPanel, BorderLayout.CENTER);

		/*
		 * Username field listeners
		 */

		txtEnterUsername = new JTextField();
		txtEnterUsername.addMouseListener(new MouseAdapter() {
			//Mouse entered
			@Override
			public void mouseClicked(MouseEvent arg0) {
				txtEnterUsername.setForeground(Color.BLACK);
				if (txtEnterUsername.getText().equals("Enter Username")) {
					txtEnterUsername.selectAll();
				}
			}
		});
		txtEnterUsername.setForeground(Color.LIGHT_GRAY);
		txtEnterUsername.addFocusListener(new FocusAdapter() {
			//On focus
			@Override
			public void focusGained(FocusEvent arg0) {
				if (txtEnterUsername.getText().equals("Enter Username")) {
					txtEnterUsername.setForeground(Color.BLACK);
					txtEnterUsername.selectAll();
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				txtEnterUsername.setForeground(Color.LIGHT_GRAY);
				if (txtEnterUsername.getText().equals("")) {
					txtEnterUsername.setText("Enter Username");
				}
			}
		});
		contentPanel.setLayout(new GridLayout(0, 1, 0, 5));
		txtEnterUsername.setText("Enter Username");
		txtEnterUsername.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(txtEnterUsername);
		txtEnterUsername.setColumns(10);

		/*
		 * Password field listeners
		 */

		txtEnterPassword = new JPasswordField();

		final char defaultEchoChar = txtEnterPassword.getEchoChar();
		txtEnterPassword.setEchoChar((char) 0);
		txtEnterPassword.addMouseListener(new MouseAdapter() {
			//Mouse entered
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (txtEnterPassword.getPassword().equals("Enter Password")) {
					txtEnterPassword.setEchoChar(defaultEchoChar);
					txtEnterPassword.setForeground(Color.BLACK);
					txtEnterPassword.setText("");
				}
			}
		});

		txtEnterPassword.setForeground(Color.LIGHT_GRAY);
		txtEnterPassword.addFocusListener(new FocusAdapter() {
			//On focus
			@Override
			public void focusGained(FocusEvent arg0) {
				txtEnterPassword.setForeground(Color.BLACK);
				txtEnterPassword.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				txtEnterPassword.setForeground(Color.LIGHT_GRAY);
				if (new String(txtEnterPassword.getPassword()).equals("")) {
					txtEnterPassword.setEchoChar((char) 0);
					txtEnterPassword.setText("Enter Password");
				}
			}
		});

		txtEnterPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				txtEnterPassword.setEchoChar(defaultEchoChar);
			}
		});

		txtEnterPassword.setText("Enter Password");
		txtEnterPassword.setHorizontalAlignment(SwingConstants.CENTER);
		txtEnterPassword.setForeground(Color.LIGHT_GRAY);
		txtEnterPassword.setColumns(10);
		contentPanel.add(txtEnterPassword);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			d.getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton loginButton = new JButton("Login");
				loginButton.addActionListener(new ActionListener() {
					//Button pressed
					public void actionPerformed(ActionEvent arg0) {
						if (!txtEnterUsername.getText().equals("Enter Username") && 
								!txtEnterUsername.getText().equals("") &&
								!new String(txtEnterPassword.getPassword()).equals("Enter Password") &&
								!new String(txtEnterPassword.getPassword()).equals("")) {

							username = txtEnterUsername.getText();
							password = new String(txtEnterPassword.getPassword());
							//Close the window
							close();

							//Creates the main window after completion of first dialog
							synchronized(o) {
								o.notifyAll();
							}
						}	
					}
				});
				loginButton.setActionCommand("OK");
				buttonPane.add(loginButton);
				d.getRootPane().setDefaultButton(loginButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					//Cancel program
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				{
					//Register button
					JButton registerButton = new JButton("Register");
					registerButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							if (!txtEnterUsername.getText().equals("Enter Username") && 
									!txtEnterUsername.getText().equals("") &&
									!new String(txtEnterPassword.getPassword()).equals("Enter Password") &&
									!new String(txtEnterPassword.getPassword()).equals("")) {

								username = txtEnterUsername.getText();
								password = new String(txtEnterPassword.getPassword());

								register = true;
								//Close the window
								close();

								//Creates the main window after completion of first dialog
								synchronized(o) {
									o.notifyAll();
								}
							}
						}
					});
					buttonPane.add(registerButton);
				}
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		{
			JPanel panel = new JPanel();
			d.getContentPane().add(panel, BorderLayout.NORTH);
		}

		d.setVisible(true);

		/*
		 * Blocks main thread
		 */

		synchronized(o) {
			try {
				o.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/** 
	 * @return the username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password.
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * @return the register
	 */
	public boolean isRegister() {
		return register;
	}

	//Closes the window
	private void close() {
		d.setVisible(false);
	}
}
