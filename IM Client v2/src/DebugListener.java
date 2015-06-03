package src;

import java.util.Scanner;

/**
 * Allows user hosting an IMServer to input commands while running.
 * @author Reed
 */
public class DebugListener implements Runnable {
	public boolean run = true;

	@Override
	public void run() {
		System.out.println("Debug thread started.\n");
		Scanner sc = new Scanner(System.in);

		while (run) {
			String line = sc.nextLine();

			//Command list
			if (line.equals("connected")) {
				IMServer.printConnections();
			} else if (line.equals("quit")) {
				System.exit(0);
			} else if (line.equals("users")) {
				IMServer.printUsers("users/users.ser");
			} else if (line.equals("help")) {
				help();
			} 
			else {
				System.out.println("Invalid command. Use \"help\" for command list.");
			}
		}

		sc.close();
	}

	/**
	 * Prints command list.
	 */
	private void help() {
		System.out.println("\nAvailable commands: " +
				"connected " + 
				"restart " +
				"quit " + 
				"users");
	}
}
