package tests;

import filter.BloomFilter;
import login.Credentials;
import login.LoginServer;
import login.NameTooLongException;

/**
 * Utility for reading the user list contained in users.ser. Just runs
 * the debug command buried in LoginServer.
 * @author Reed
 *
 */
public class UserListReader {
	private LoginServer s;
	
	public UserListReader() {
		s = new LoginServer();
	}
	
	public UserListReader(String filePath) {
		s = new LoginServer(filePath);
	}

	public static void main(String[] args) {
		UserListReader r = new UserListReader("users/users.ser");
		try {
			r.s.authenticate(new Credentials("_list_users", new BloomFilter()));
		} catch (NameTooLongException e) {
			e.printStackTrace();
		}
	}
}
