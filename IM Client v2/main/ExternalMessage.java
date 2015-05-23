package main;

import login.User;

/**
 * External messages are packets containing actual messages to be shown to users.
 * @author Reed
 *
 */
public class ExternalMessage extends Message {
	private static final long serialVersionUID = 1338880583237113936L;
	private String message;
	
	public ExternalMessage(User sender, User recipient, String externalMessage) {
		super(sender, recipient);
		this.message = externalMessage;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
