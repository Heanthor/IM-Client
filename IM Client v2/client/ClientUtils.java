package client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

/**
 * Utilities for an IMClient, mainly for handling image IO.
 * @author Reed
 *
 */
public class ClientUtils {

	private ClientUtils() {}

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

	/**
	 * Compress a string using Burrows-Wheeler Transform and run-length encoding.
	 * Should only be used on sufficiently large strings.
	 * Uses null character for internal termination.
	 * @param in The String to compress
	 * @return the compressed string
	 */
	public static String compressString(String in) {
		// Add terminating character
		in += '\0';

		ArrayList<String> rotations = new ArrayList<>();

		// Calculate cyclic rotations
		for (int i = 0; i < in.length(); i++) {
			rotations.add(in.substring(in.length() - i) + in.substring(0, in.length() - i));
		}

		// Sort rotations
		String[] rotations_sorted = new String[rotations.size()];
		rotations.toArray(rotations_sorted);
		Arrays.sort(rotations_sorted);

		// Get the BWT
		StringBuilder bwt = new StringBuilder();
		for (String s: rotations_sorted) {
			bwt.append(s.charAt(s.length() - 1));
		}

		/* Run-length encoding
		   Format: <number><character> */
		String bwtString = bwt.toString();
		StringBuilder rle = new StringBuilder();

		int counter = 1;
		char nextChar;
		for (int i = 0; i < bwtString.length(); i++) {
			char chAt = bwtString.charAt(i);
			nextChar = i + 1 < bwtString.length() ? bwtString.charAt(i + 1) : '\0';

			if (chAt == nextChar) {
				counter++;
			} else {
				rle.append(counter);
				rle.append(bwtString.charAt(i));
				counter = 1;
			}
		}

		return rle.toString();
	}

	/**
	 * Decompresses and decodes a string compressed with Burrows-Wheeler Transform
	 * and run-length encoding.
	 * @param encodedMessage The encoded string
	 * @return The decompressed string
	 */
	public static String decompressString(String encodedMessage) {
		// Reverse run-length encoding
		StringBuilder rev = new StringBuilder();

		for (int i = 0; i < encodedMessage.length() - 1; i += 2) {
			for (int j = Character.getNumericValue(encodedMessage.charAt(i)); j > 0; j--) {
				rev.append(encodedMessage.charAt(i + 1));
			}
		}

		// encodedMessage is the last column of the BWT
		encodedMessage = rev.toString();

		// Initialize table
		String[] building = new String[encodedMessage.length()];
		for (int i = 0; i < building.length; i++) {
			building[i] = "";
		}

		char[] lastCol = encodedMessage.toCharArray();

		// Sort and add to build table
		for (int x = 0; x < encodedMessage.length(); x++) {
			for (int i = 0; i < lastCol.length; i++) {
				building[i] = lastCol[i] + building[i];
			}
			Arrays.sort(building);
		}

		// Find string that ends with terminating character, this is the original string
		for (String s: building) {
			if (s.charAt(s.length() - 1) == '\0') {
				// Remove temporary terminating character
				s = s.replace("\0", "");
				return s;
			}
		}

		// Invalid message type
		return null;
	}
}
