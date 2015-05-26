package messages;

import main.Message;

/**
 * External messages are packets containing actual messages to be shown to users.
 * Messages have a 10,000 character limit.
 * @author Reed
 *
 */
public class ExternalMessage extends Message {
	private static final long serialVersionUID = 1338880583237113936L;
	private String message;
	
	public ExternalMessage(String sender, String recipient, String externalMessage) {
		super(sender, recipient);
		
		//Add length restriction
		if (externalMessage.length() > 10000) {
			externalMessage = externalMessage.substring(0, 10000);
		}
		this.message = externalMessage;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExternalMessage [message=" + message + "]";
	}
}
