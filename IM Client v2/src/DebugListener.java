package src;

import java.util.Scanner;

public class DebugListener implements Runnable {
	public boolean run = true;

	@Override
	public void run() {
		System.out.println("Debug thread started.\n");
		Scanner sc = new Scanner(System.in);

		while (run) {
			String line = sc.nextLine();

			if (line.equals("connected")) {
				IMServer.printConnections();
			} else if (line.equals("quit")) {
				System.exit(0);
			} else if (line.equals("users")) {
				IMServer.printUsers();
			} else if (line.equals("help")) {
				help();
			}
			else {
				System.out.println("Invalid command. Use \"help\" for command list.");
			}
		}

		sc.close();
	}

	private void help() {
		System.out.println("Available commands: \n" +
				"connected\n" + 
				"quit\n" + 
				"users\n" 
				);
	}
}
