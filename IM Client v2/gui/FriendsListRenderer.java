package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Custom cell renderer for the JList used in FriendsList.
 * @author Reed
 */
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

		Image i = Toolkit.getDefaultToolkit()
				.getImage(FriendsListRenderer.class.getResource("logos/quill_50.png"));	
		label.setIcon(new ImageIcon(i));

		return label;
	}
}
