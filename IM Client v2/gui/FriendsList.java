package gui;

import java.awt.CardLayout;
import java.awt.Dimension;
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

/**
 * The user list located to the right of the chat text area.
 * @author Reed
 *
 */
public class FriendsList {
	public JPanel frmUserList;
	private JList<String> list;
	private Object listUpdate;
	private DefaultListModel<String> dlm;

	/**
	 * Launch the application.
	 * @deprecated
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					@SuppressWarnings("unused")
					FriendsList window = new FriendsList(new Object());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FriendsList(Object listUpdate) {
		this.listUpdate = listUpdate;
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
		list.setBackground(new Color(173, 173, 173));
		list.setForeground(Color.BLACK);
		list.setDropMode(DropMode.ON);
		list.setCellRenderer(new FriendsListRenderer());
		list.setMinimumSize(new Dimension(125, 50));

		//New item is selected
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (arg0.getValueIsAdjusting() == false) {
					
					synchronized(listUpdate) { //Alert thread that it has changed
						listUpdate.notifyAll();
					}
				}
			}
		});

		dlm = new DefaultListModel();
		//dlm.add(0, "Test1");
		//dlm.add(1, "Test2");
		list.setModel(dlm);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setBorder(new LineBorder(new Color(0, 0, 0)));

		JScrollPane scrollPane = new JScrollPane(list);
		frmUserList.add(scrollPane, "name_12871148397846");
	}

	/*
	 * Self-explanatory getters and helpers
	 */
	
	public String getSelectedValue() {
		return  list.getSelectedValue();
	}
	
	public void addToList(String s) {
		dlm.addElement(s);
	}
	
	public void removeFromList(String s) {
		dlm.removeElement(s);
	}
	
	public void clearList() {
		dlm.clear();
	}
	
	/**
	 * @return number of elements in list
	 */
	public int getLength() {
		return list.getModel().getSize();
	}
	
	public void setSelectedIndex(int index) {
		list.setSelectedIndex(index);
	}
}
