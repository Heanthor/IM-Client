package tests;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import main.*;
import messages.InternalMessage;

import org.junit.Test;

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
		Message m = new InternalMessage(null, null, null, null);
		
		assertTrue(m instanceof InternalMessage);
	}
	
	@Test
	public void testReplace() {
		IMServer s = new IMServer(null);
		String oldStr = "test";
		String newStr = "replace";
		
		s.replace(oldStr, newStr);
	}
}
