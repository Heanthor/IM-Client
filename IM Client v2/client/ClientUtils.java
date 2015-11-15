package client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

/**
 * Utilities for an IMClient, mainly for handling image IO.
 * @author Reed
 *
 */
public class ClientUtils {
	//Returns a bufferedImage from image, from some github page
	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	/**
	 * Encodes an image into Base64.
	 * @param i The ImageIcon representation of the image.
	 * @param extension String containing the file's type, e.g. "jpg" for test.jpg
	 * @return Base64 encoding of the image in a byte[]
	 */
	public static byte[] encodeImage(ImageIcon i, String extension) {
		Encoder b = Base64.getEncoder();
		BufferedImage bi = ClientUtils.toBufferedImage(i.getImage());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			ImageIO.write(bi, extension, baos);
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] byteImage = baos.toByteArray();
		return b.encode(byteImage);
	}

	/**
	 * Decodes an encoded byte[] image into a viewable format
	 * @param encodedImage An image encoded by Base64.Encoder
	 * @return The BufferedImage stored in the given byte[]
	 */
	public static BufferedImage decodeImage(byte[] encodedImage) {
		Decoder d = Base64.getDecoder();
		try {

			return ImageIO.read(new ByteArrayInputStream(d.decode(encodedImage)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
