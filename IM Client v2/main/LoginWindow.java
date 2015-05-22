package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginWindow {
	private final JPanel contentPanel = new JPanel();
	private JTextField txtEnterUsername;
	private String username;
	private JDialog d = new JDialog(); // Main dialog window
	@SuppressWarnings("unused")
	private static Object o; //Synchronization
	private JPasswordField txtEnterPassword;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			LoginWindow dialog = new LoginWindow(new Object());
			dialog.d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
		d.setResizable(false);
		d.setBounds(100, 100, 333, 155);
		d.getContentPane().setLayout(new BorderLayout());
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
				System.out.println(txtEnterPassword.getPassword());
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
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					//Button pressed
					public void actionPerformed(ActionEvent arg0) {
						if (!txtEnterUsername.getText().equals("Enter Username") && 
								!txtEnterUsername.getText().equals("")) {
							username = txtEnterUsername.getText();
							//Close the window
							close();

							//Creates the main window after completion of first dialog
							synchronized(o) {
								o.notifyAll();
							}
						}	
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				d.getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					//Cancel program
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		{
			JPanel panel = new JPanel();
			d.getContentPane().add(panel, BorderLayout.NORTH);
		}

		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.setVisible(true);

		synchronized(o) {
			try {
				o.wait(); //Blocks main thread
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/** 
	 * Returns the username
	 */
	public String getUsername() {
		return username;
	}

	//Closes the window
	private void close() {
		d.setVisible(false);
	}
}
