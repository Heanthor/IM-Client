package messages;

import java.io.Serializable;
import java.util.Date;

/**
 * Parent class of data sent between client and server.
 * Contains at least a sender and recipient, and is extended to include a message
 * where needed.
 * @author Reed
 */
public abstract class Message implements Serializable {
	private static final long serialVersionUID = -7316250263820535794L;
	private String senderUsername;
	private String recipientUsername;
	private final Date timestamp;
	
	public Message() {
		senderUsername = null;
		recipientUsername = null;
		//On creation of a message, save the timestamp
		timestamp = new Date();
	}
	
	public Message(String sender, String recipient) {
		this.senderUsername = sender;
		this.recipientUsername = recipient;
		timestamp = new Date();
	}

	/**
	 * @return the sender
	 */
	public String getSender() {
		return senderUsername;
	}
	
	/**
	 * @return the recipient
	 */
	public String getRecipient() {
		return recipientUsername;
	}

	/**
	 * @return the timestamp of when this message was created
	 */
	public Date getTimestamp() {
		return timestamp;
	}
}
