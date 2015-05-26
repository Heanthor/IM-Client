package main;

import java.awt.CardLayout;
import java.awt.EventQueue;

import javax.swing.JList;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Color;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.DropMode;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Font;

public class FriendsList {
	public JPanel frmUserList;
	private JList<String> list;
	private DefaultListModel<String> dlm;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					@SuppressWarnings("unused")
					FriendsList window = new FriendsList();
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
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		frmUserList = new JPanel();

		frmUserList.setLayout(new CardLayout());
		list = new JList();
		list.setFont(new Font("Tahoma", Font.BOLD, 14));
		list.setForeground(Color.BLACK);
		list.setDropMode(DropMode.ON);
		list.setCellRenderer(new FriendsListRenderer());

		//New item is selected
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (arg0.getValueIsAdjusting() == false) {
					System.out.println(list.getSelectedValue());
				}
			}
		});

		dlm = new DefaultListModel();
		dlm.add(0, "Test1");
		dlm.add(1, "Test2");
		list.setModel(dlm);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setBorder(new LineBorder(new Color(0, 0, 0)));

		JScrollPane scrollPane = new JScrollPane(list);
		frmUserList.add(scrollPane, "name_12871148397846");
	}

	public JList<String> getList() {
		return list;
	}
	
	public void addToList(String s) {
		dlm.addElement(s);
	}
	
	public void removeFromList(String s) {
		dlm.removeElement(s);
	}
}
