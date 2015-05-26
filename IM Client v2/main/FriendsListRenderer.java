package main;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

@SuppressWarnings("serial")
public class FriendsListRenderer extends DefaultListCellRenderer {
	
	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list,
			Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		label.setHorizontalTextPosition(JLabel.RIGHT);
		label.setPreferredSize(new Dimension(125, 50));
		label.setIcon(new ImageIcon("icon.jpg"));
		
		return label;
	}
}
