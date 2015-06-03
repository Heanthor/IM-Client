package src;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JTextPane;
import javax.swing.border.LineBorder;

public class TextPaneFactory {

	private TextPaneFactory() {}

	/**
	 * Returns a JTextPane consistent with the layout of QuillChat
	 * @return
	 */
	public static JTextPane createTextPane() {
		JTextPane toReturn = new JTextPane();
		toReturn = new JTextPane();
		toReturn.setFont(new Font(toReturn.getFont().getName(), Font.PLAIN, 15));
		toReturn.setBackground(new Color(153, 153, 153));
		toReturn.setBorder(new LineBorder(new Color(0, 0, 0)));
		toReturn.setMargin(new Insets(2, 5, 5, 2));
		toReturn.setEditable(false); 

		return toReturn;
	}
}
