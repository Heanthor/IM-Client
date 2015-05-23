package tests;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import main.IMServer;

import org.junit.Test;

public class TestHelperMethods {

	@SuppressWarnings("resource")
	@Test
	public void test() {
		IMServer s = new IMServer(null);
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("users/identifiers.txt", false));
			
			bw.write("user1 192.168.1");
			
			bw.close();
			
			assertTrue(s.contains("user1"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
