package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Custom cell renderer for the JList used in FriendsList.
 * @author Reed
 */
@SuppressWarnings("serial")
public class FriendsListRenderer extends DefaultListCellRenderer {
	private boolean flag;

	/**
	 * The flag used to determine whether to paint the idle icon, or
	 * new message icon.
	 * @param flag True to render idle icon, false for new message icon.
	 */
	public FriendsListRenderer(boolean flag) {
		super();
		this.flag = flag;
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list,
			Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (isSelected) {
			label.setForeground(new Color(0, 0, 0));
			label.setBackground(new Color(247, 247, 247));
		}
		label.setHorizontalTextPosition(JLabel.RIGHT);

		Image i;
		if (flag) {
			i = Toolkit.getDefaultToolkit()
					.getImage(FriendsListRenderer.class.getResource("logos/quill_50.png"));	
		} else {
			i = Toolkit.getDefaultToolkit()
					.getImage(FriendsListRenderer.class.getResource("logos/quill_alert_50.png"));	
		}

		label.setIcon(new ImageIcon(i));

		return label;
	}
	
	/**
	 * @param newFlag - Set a new flag.
	 */
	public void setFlag(boolean newFlag) {
		flag = newFlag;
	}
}
