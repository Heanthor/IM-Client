package main;

import java.io.Serializable;

import login.*;

public abstract class Message implements Serializable {
	private static final long serialVersionUID = -7316250263820535794L;
	private User sender;
	private User recipient;
	
	public Message(User sender, User recipient) {
		this.sender = sender;
		this.recipient = recipient;
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


}
