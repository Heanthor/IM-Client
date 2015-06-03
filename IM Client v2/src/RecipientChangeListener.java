package src;

import gui.FriendsList;

/**
 * One thread running an instance of this class is created to listen for changes
 * in the user list, and when they are detected, push the changes to the IMClient
 * which will in turn change them in the MainWindow.
 * @author Reed
 *
 */
public class RecipientChangeListener implements Runnable {
	private IMClient client;
	private FriendsList f;
	private Object o;

	public RecipientChangeListener(IMClient client, FriendsList f, Object o) {
		this.client = client;
		this.f = f;
		this.o = o;
	}

	@Override
	public void run() {
		while (true) {

			synchronized(o) {
				try {
					o.wait(); //Wait for name to change
					
					client.setRecipient(f.getSelectedValue());
					client.setTextPane(client.getConversations().get(f.getSelectedValue()));
					System.out.println("Recipient set to " + f.getSelectedValue());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
