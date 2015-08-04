package client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.imgscalr.Scalr;

/**
 * Displays images sent by other users.
 * @author Reed
 *
 */
public class ImageLayer extends JComponent {
	private static final long serialVersionUID = -8170121070644585867L;
	private BufferedImage bi;

	public ImageLayer(BufferedImage bi) {
		//resize
		if (bi.getHeight() > 1440 || bi.getWidth() > 900) {
			bi = Scalr.resize(bi, Scalr.Mode.AUTOMATIC, 1440, 900);
		}

		this.bi = bi;
		this.setPreferredSize(new Dimension(bi.getWidth(), bi.getHeight()));
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.drawImage(bi, 0, 0, this);
	}
}
