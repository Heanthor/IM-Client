package src;

import messages.Message;

/**
 * Runnable class that calls the associated IMClient's outgoing method, to send
 * the given message.
 * @author Reed
 */
public class Sender implements Runnable {
	private IMClient client;
	private Message messageOut;

	public Sender(IMClient client, Message message) {
		this.client = client;
		messageOut = message;
	}

	@Override
	public void run() {
		try {
			//Send to outgoing thread
			System.out.println("Message sending in Sender run(): " + messageOut);
			client.outgoing(messageOut);
		} catch (Exception e) {

			System.out.println("Exception in run()");
			e.printStackTrace();
		}
	}
}