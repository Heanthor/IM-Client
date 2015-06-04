package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

import messages.InternalMessage;
import messages.Message;

import org.junit.Test;

import src.*;

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
}
