package main;

import java.io.Serializable;

public abstract class Message implements Serializable {
	private static final long serialVersionUID = -7316250263820535794L;
	private String senderUsername;
	private String recipientUsername;
	
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
