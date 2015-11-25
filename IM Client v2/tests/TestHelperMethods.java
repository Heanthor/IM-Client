package tests;

import client.ClientUtils;
import messages.External;
import messages.InternalMessage;
import messages.Message;
import org.junit.Test;
import server.ServerUtils;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class TestHelperMethods {

	/*@Test
	public void testFileIO() {
		IMServer s = new IMServer(null);

		try {
			File f = new File("users/identifiers.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, false));

			bw.write("user1 192.168.1");
			bw.flush();

			assertTrue(s.contains("user1") != null);

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	 */
	@Test
	public void testPolymorphism() {
		Message m = new InternalMessage(null, null);

		assertTrue(m instanceof InternalMessage);
	}

	/*
	@Test
	public void testReplace() {
		IMServer s = new IMServer(null);
		String oldStr = "test";
		String newStr = "replace";

		s.replace(oldStr, newStr);
	}
	 */
	@Test
	public void testSplit() {
		String in = "$list_update reed test ";
		String two = in.substring(in.indexOf(" ") + 1);

		String[] names = two.split(" ");

		System.out.println("[");
		for (String s: names) {
			System.out.println(s);
		}
		System.out.println("]");
	}

	@Test
	public void testRegex() {
		String in = "   ";
		String regex = "^[\\s]*$";

		assertTrue(in.matches(regex));

		in = "   d";
		assertFalse(in.matches(regex));

	}

	@Test
	public void testDocument() {
		DefaultStyledDocument d = new DefaultStyledDocument();
		JTextPane p = new JTextPane();
		p.setDocument(d);
		assertTrue(d instanceof StyledDocument);
	}

	@Test
	public void testURLValidation() {
		Pattern p = Pattern.compile(
				"(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
						+ "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
						+ "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

		String testString = "some text lelelel http://www.reddit.com/r/dogs/top/?sort=top&t=all#page=1"
				+ " lelelele "
				+ "http://www.reddit.com/r/dogs/top/?sort=top&t=all#page=2";
		Matcher m = p.matcher(testString); 

		while(m.find()) {
			System.out.println(testString.substring(m.start(1), m.end()));
		}

	}

//	@Test
//	public void testUrlIP() {
//		String name = ServerUtils.usernameIP("/52.10.127.193");
//		assertEquals(name, "sdfsd");
//	}

	@Test
	public void testEncoding() {
		String filename = "sadie.jpg";
		String extension = filename.substring(filename.indexOf(".") + 1);
		System.out.println(extension);
		URL imageURL = TestHelperMethods.class.getResource(filename);

		ImageIcon i = new ImageIcon(imageURL);

		ClientUtils.encodeImage(i, extension);
	}

	@Test
	public void testDecoding() {
		String filename = "sadie.jpg";
		String extension = filename.substring(filename.indexOf(".") + 1);
		System.out.println(extension);
		URL imageURL = TestHelperMethods.class.getResource(filename);

		ImageIcon i = new ImageIcon(imageURL);
		
		//encode
		byte[] encodedImage = ClientUtils.encodeImage(i, extension);
		//******************************************//
		//decode
		BufferedImage decodedImage = ClientUtils.decodeImage(encodedImage);
		
		assertEquals(591, decodedImage.getHeight());
		assertEquals(375, decodedImage.getWidth());
	}

	@Test
	public void testStaticInstantiaion() {
		String key1 = ServerUtils.createKey();

		assertEquals(key1, ServerUtils.createKey());
	}

	@Test
	public void testFields() {
		Message m = new External(null);
		External m2 = new External(null);
		Object m3 = m2;

		External casted = (External)m3;

		assertNotNull(m.getTimestamp());
		assertNotNull(m2.getTimestamp());
		assertNotNull(casted.getTimestamp());
	}
}
