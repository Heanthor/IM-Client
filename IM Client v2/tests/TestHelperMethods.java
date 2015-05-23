package tests;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import main.IMServer;

import org.junit.Test;

public class TestHelperMethods {

	@Test
	public void test() {
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

}
