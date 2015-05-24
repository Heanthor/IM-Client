package messages;

import main.Message;

public class External extends Message {
	private static final long serialVersionUID = -8030516592457220877L;
	private String message;
	
	public External(String message) {
		this.message = message;
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
		return "External [message=" + message + "]";
	}
}
