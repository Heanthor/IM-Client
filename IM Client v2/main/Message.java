package main;

import java.io.Serializable;

import login.*;

public class Message implements Serializable {
	private static final long serialVersionUID = -7316250263820535794L;
	private User sender;
	private User recipient;
	private String message;
	
	public Message(User sender, User recipient, String message) {
		this.sender = sender;
		this.recipient = recipient;
		this.message = message;
	}

	/**
	 * @return the sender
	 */
	public User getSender() {
		return sender;
	}
	
	/**
	 * @return the recipient
	 */
	public User getRecipient() {
		return recipient;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
}
