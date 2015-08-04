package messages;

import java.io.File;

import javax.swing.ImageIcon;

import client.ClientUtils;

public class ImageMessage extends Message {
	private static final long serialVersionUID = -5440144229626750431L;
	private byte[] image;
	
	public ImageMessage(String sender, String recipient, File image) {
		super(sender, recipient);
		String ext = image.getName().substring(image.getName().indexOf(".") + 1);

		this.image = ClientUtils.encodeImage(new ImageIcon(image.getAbsolutePath()), ext);
		System.out.println();
	}

	public ImageMessage(String sender, String recipient, byte[] image) {
		super(sender, recipient);
		this.image = image;
	}

	/**
	 * @return the image
	 */
	public byte[] getImage() {
		return image;
	}
}
