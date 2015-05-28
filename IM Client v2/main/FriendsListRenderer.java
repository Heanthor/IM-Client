package main;

import java.awt.Color;
import java.awt.Component;

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

		if (isSelected) {
			label.setForeground(new Color(0, 0, 0));
			label.setBackground(new Color(247, 247, 247));
		}
		label.setHorizontalTextPosition(JLabel.RIGHT);
		//label.setPreferredSize(new Dimension(125, 50));
		label.setIcon(new ImageIcon("logos/icon.jpg"));
		return label;
	}
}
