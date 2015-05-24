package tests;

import filter.BloomFilter;
import login.Credentials;
import login.LoginServer;

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
		r.s.authenticate(new Credentials("_list_users", new BloomFilter()));
	}
}
