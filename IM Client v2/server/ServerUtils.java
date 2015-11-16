package server;

import filter.BloomFilter;
import login.Credentials;
import login.LoginServer;
import login.NameTooLongException;

import java.io.*;
import java.util.Date;

/**
 * Utilities for an IMServer, such as reading and scanning files.
 * @author Reed
 *
 */
public class ServerUtils {

	/**
	 * The time at which the server was started. Used to generate keys for all clients.
	 */
	private static final Date timeAtInitialization = new Date();

	private ServerUtils() {}

	/**
	 * Returns the username associated with the given IP address, in the server's
	 * username store.
	 * @param ip The IP to match
	 * @return The username associated with the IP, or null if not found.
	 * @throws IOException
	 */
	public static String usernameIP(String ip) {
		BufferedReader fileReader;
		String line;

		try {
			fileReader = new BufferedReader
					(new FileReader("users/identifiers.txt"));

			while ((line = fileReader.readLine()) != null) {
				if (line.contains(" ")) {
					String temp = line.substring(line.indexOf("/"), line.length());
					String user = line.substring(0, line.indexOf(" "));

					if (temp.equals(ip)) {
						fileReader.close();
						return user;
					}
				}
			}

			fileReader.close();
		} catch (IOException | NullPointerException e) {
			System.out.println("Username IP Exception");
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Helper method used to write to the identifiers.txt method
	 * Will replace oldStr located in identifiers.txt with newStr.
	 * @param - oldStr String to replace
	 * @param - newStr String to replace with
	 * @return True if operation succeeds, false otherwise.
	 */
	public static boolean replace(String oldStr, String newStr) {
		try {
			BufferedReader rd = new BufferedReader(new FileReader("users/identifiers.txt"));
			String line;
			String input = "";

			while ((line = rd.readLine()) != null) {
				line = line.replaceAll(oldStr + " .*", newStr);
				input += line + "\n";
			}

			BufferedWriter wr = new BufferedWriter(new PrintWriter("users/identifiers.txt"));

			wr.write(input);
			wr.flush();
			rd.close();
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	/**
	 * Helper method for reading identifiers.txt file.
	 * @param name The username being searched for
	 * @return the line containing the name if found, or null if not found.
	 * @throws IOException
	 */
	public static String contains(String name) throws IOException {
		BufferedReader fileReader = null;

		try {
			fileReader = new BufferedReader
					(new FileReader("users/identifiers.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String line;
		//Finds the first instance of the identifier in list, saves IP
		while ((line = fileReader.readLine()) != null) {
			if (line.contains(" ") && line.substring(0, line.indexOf(" ")).
					equals(name)) {
				fileReader.close();
				return line;
			}
		}

		fileReader.close();
		return null;
	}

	/**
	 * Prints contents of connectedIPs parameter
	 * @param connectedIPs an iterable list of strings
	 */
	public static void printConnections(Iterable<String> connectedIPs) {
		System.out.println("Connected IP list: ");

		System.out.print("[");
		for (String s: connectedIPs) {
			System.out.println(s + ", ");
		}

		System.out.println("]");
	}

	/**
	 * Prints contents of users.ser
	 * @param dir The location of users.ser
	 */
	public static void printUsers(String dir) {
		try {
			new LoginServer(dir).authenticate(new Credentials("_list_users", new BloomFilter()));
		} catch (NameTooLongException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a key to be given to a client, for encryption.
	 * Uses the server's start time as a seed.
	 * @return The standard key.
	 */
	public static String createKey() {
		return String.valueOf(timeAtInitialization.getTime() >> 5);
	}
}
