package main;

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
					System.out.println("Recipient set to " + f.getSelectedValue());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

}
