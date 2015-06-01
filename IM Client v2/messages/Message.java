package messages;

import java.io.Serializable;

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
	
	public Message() {
		senderUsername = null;
		recipientUsername = null;
	}
	
	public Message(String sender, String recipient) {
		this.senderUsername = sender;
		this.recipientUsername = recipient;
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
}
