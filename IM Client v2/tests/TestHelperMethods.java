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

	@Test
	public void testBWT() {
		String toTransform = "GCGTGCCTGGTCA";

		assertEquals("1A1C1T2G1C1T1\u00001T1G1C2G1C", ClientUtils.compressString(toTransform));

		assertEquals(toTransform, ClientUtils.decompressString("1A1C1T2G1C1T1\u00001T1G1C2G1C"));
	}

	@Test
    public void testCompression() {
        String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec pellentesque quam nunc, ut blandit odio sollicitudin non. Vivamus malesuada enim massa. Aliquam erat volutpat. Suspendisse vehicula pretium diam id facilisis. Maecenas metus ante, bibendum mattis metus a, varius elementum nisl. Ut in lobortis libero. Morbi egestas lectus vel sapien tincidunt, sed eleifend nunc ultricies. Integer eu orci dignissim, malesuada odio ut, scelerisque neque. In congue magna nec fermentum porttitor. Aliquam feugiat dapibus arcu id rhoncus. Sed vel mauris a lorem pharetra efficitur. Phasellus ut lobortis dui.\n" +
                "\n" +
                "Nulla eget consequat justo, eget luctus urna. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Sed nec massa ac augue convallis varius. Integer tincidunt ligula at tortor pharetra iaculis vel vitae tortor. Sed maximus enim eu arcu ultrices, ac pulvinar magna commodo. Cras condimentum vel dui sit amet porta. Etiam placerat, tellus a eleifend imperdiet, magna libero placerat orci, sit amet tincidunt metus sapien eu nisi. Pellentesque varius orci ut turpis fringilla, vitae vehicula risus pellentesque. Nam quis dui varius, semper quam id, ultricies odio.\n" +
                "\n" +
                "Duis imperdiet mollis metus, in lobortis est molestie ut. Mauris ullamcorper nec nibh eget finibus. Nunc eleifend interdum metus. Quisque facilisis vel velit at vestibulum. Integer dignissim tincidunt tempor. Nullam pulvinar gravida neque, et luctus quam iaculis id. Mauris eget nisi imperdiet, mollis dolor eu, ornare augue. Nullam sollicitudin dui mi, in ullamcorper ipsum sollicitudin a. Nullam ut dui lorem. Sed lobortis massa vel tellus suscipit tempor. Vestibulum nec imperdiet nunc. Morbi et orci eget dolor cursus dignissim. Donec et ex nunc.\n" +
                "\n" +
                "Integer auctor dolor in diam placerat ultricies. Nulla maximus magna eu elit semper, ac accumsan metus laoreet. Vestibulum at malesuada arcu. Nam in nulla consequat, ornare tellus eget, consectetur odio. Duis vulputate egestas diam placerat interdum. Quisque id sem pulvinar mi eleifend hendrerit. Aliquam et maximus lectus. Aenean eget dapibus lorem. Suspendisse tempus metus in dolor interdum, in gravida lorem tempor. Quisque vel arcu massa. Duis id congue nunc.\n" +
                "\n" +
                "Nullam metus urna, porttitor vitae mauris a, pulvinar dapibus dui. Vivamus enim dolor, commodo non ligula eu, tempor iaculis purus. Vestibulum ut ultricies diam, ac consequat dui. In vestibulum, nunc sed imperdiet ornare, quam est tristique metus, non imperdiet velit ligula et velit. Maecenas rhoncus nec libero eu facilisis. Nullam aliquam fermentum felis, et dignissim nunc pharetra eu. Morbi non leo rhoncus, mollis nunc non, semper nibh. In facilisis, metus sed mollis mattis, sem lacus posuere neque, fringilla vulputate metus urna egestas ante. Aliquam a nisl auctor, accumsan tortor eu, malesuada augue. Donec interdum purus ullamcorper, tristique lacus et, auctor nibh. Etiam non vulputate quam.";

        String compressedLoremIpsum = ClientUtils.compressString(loremIpsum);
        System.out.println("Original length: " + loremIpsum.length());
        System.out.println("Compressed length: " + compressedLoremIpsum.length());
//        assertTrue(compressedLoremIpsum.length() < loremIpsum.length());

        String decodedLorenIpsum = ClientUtils.decompressString(compressedLoremIpsum);
        assertEquals(loremIpsum, decodedLorenIpsum);
    }
}
