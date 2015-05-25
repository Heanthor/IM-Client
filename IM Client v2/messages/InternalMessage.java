package messages;

import login.User;
import main.Message;


/**
 * Messages sent internally between client and server, for alerts and authentication.
 * @author Reed
 *
 */
//TODO why does this have sender and recipient, shouldn't it just send to u.getUsername()?
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InternalMessage [message=" + message + "]";
	}
}
