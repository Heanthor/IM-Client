package main;


/**
 * Messages sent internally between client and server, for alerts and authentication.
 * @author Reed
 *
 */
public class InternalMessage extends Message {
	private static final long serialVersionUID = 2907853835140463173L;
	private String message;
	
	public InternalMessage(String sender, String recipient, String internalMessage) {
		super(sender, recipient);
		this.message = internalMessage;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
}
