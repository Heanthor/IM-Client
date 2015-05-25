package main;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.CardLayout;

import javax.swing.JList;
import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.AbstractListModel;
import javax.swing.JScrollPane;
import javax.swing.DropMode;

import java.awt.Font;

public class FriendsList {

	private JFrame frmUserList;
	private JList list;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FriendsList window = new FriendsList();
					window.frmUserList.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FriendsList() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize() {
		frmUserList = new JFrame();
		frmUserList.setTitle("User List");
		frmUserList.setResizable(false);
		frmUserList.setBounds(100, 100, 201, 273);
		frmUserList.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmUserList.getContentPane().setLayout(new CardLayout(0, 0));

		list = new JList();
		list.setFont(new Font("Tahoma", Font.BOLD, 14));
		list.setForeground(Color.BLACK);
		list.setDropMode(DropMode.ON);
		list.setCellRenderer(new FriendsListRenderer());
		//Center text in list
		/*DefaultListCellRenderer renderer =  (DefaultListCellRenderer)list.getCellRenderer();  
		renderer.setHorizontalAlignment(JLabel.CENTER);
		renderer.setPreferredSize(new Dimension(175, 50));
		 */
		list.setModel(new AbstractListModel() {
			private static final long serialVersionUID = 1L;
			String[] values = new String[] {"Test1", "Test2"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setBorder(new LineBorder(new Color(0, 0, 0)));

		JScrollPane scrollPane = new JScrollPane(list);
		frmUserList.getContentPane().add(scrollPane, "name_12871148397846");
	}

}
