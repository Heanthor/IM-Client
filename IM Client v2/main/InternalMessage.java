package main;

import login.User;


/**
 * Messages sent internally between client and server, for alerts and authentication.
 * @author Reed
 *
 */
public class InternalMessage extends Message {
	private static final long serialVersionUID = 2907853835140463173L;
	private String message;
	private User u;
	
	public InternalMessage(String sender, User u, String recipient, String internalMessage) {
		super(sender, recipient);
		this.u = u;
		this.message = internalMessage;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return u;
	}
}
