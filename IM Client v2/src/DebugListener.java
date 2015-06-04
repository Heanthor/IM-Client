package src;

import java.text.NumberFormat;
import java.util.Scanner;

/**
 * Allows user hosting an IMServer to input commands while running.
 * @author Reed
 */
public class DebugListener implements Runnable {
	public boolean run = true;
	private long startTime = System.currentTimeMillis();
	
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
			} else if (line.equals("stats")) {
				stats();
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
				"quit " + 
				"stats " +
				"users");
	}

	/**
	 * Print memory and uptime statistics about the server.
	 */
	private void stats() {
		Runtime runtime = Runtime.getRuntime();

		long maxMemory = runtime.maxMemory() / 1024;
		long freeMemory = runtime.freeMemory() / 1024;
		long allocatedMemory = runtime.totalMemory() / 1024;

		NumberFormat f = NumberFormat.getInstance();
		System.out.println("Free memory: " + f.format(freeMemory) + " kb\n"
				+ "Allocated memory: " + f.format(allocatedMemory) + " kb\n" 
				+ "Maximum memory: " + f.format(maxMemory) + " kb\n" 
				+ "Total free memory: " + f.format(freeMemory + (maxMemory - allocatedMemory)) + " kb"
				);
		
		System.out.println("\nUsage Graph (Allocated / total)");
		System.out.println(bar(allocatedMemory, freeMemory + (maxMemory - allocatedMemory)) + "\n");
		long uptime = (System.currentTimeMillis() - startTime) / 1000; //In seconds
		System.out.println("Uptime: " + uptime(uptime) + "\n");
	}

	/**
	 * Prints the proportion of filled / total as a text progress bar.
	 * @param filled - Amount of bar to fill, as a raw number
	 * @param total - The total the bar should represent, as a raw number.
	 * @return The string representation of the bar
	 */
	private String bar(long filled, long total) {
		int fill = (int) (filled * 100 / total);

		StringBuilder sb = new StringBuilder("[");

		double fillProportion = (double)fill / 2;
		
		for (int i = 0; i < 50; i++) {
			double currentProportion = (double)i;
			
			if (currentProportion < fillProportion) {
				sb.append("|");
			} else {
				sb.append(".");
			}
		}
		
		sb.append("]");
		sb.append(" " + fill + "%");

		return sb.toString();
	}
	
	/**
	 * Prints server uptime in a friendly format.
	 * @param seconds - Total number of seconds of uptime
	 * @return The formatted string in HH hours MM minutes SS seconds format.
	 */
	private String uptime(long seconds) {
		int secondsRemainder = (int)seconds % 60;
		int minutes = (int)(seconds / 60);
		int actualMinutes = minutes % 60;
		int hours = minutes / 60;
		
		String h = hours == 1 ? " hour " : " hours ";
		String s = ((secondsRemainder == 1) ? " second." : " seconds.");
		return h + " hours " + actualMinutes + " minutes " + secondsRemainder + s;
	}
}
