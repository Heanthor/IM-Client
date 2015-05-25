package messages;

import main.Message;

/**
 * Message class used to communicate between the LoginWindow and IMClient.
 * Used for internal messages.
 * @author Reed
 *
 */
public class Internal extends Message {
	private static final long serialVersionUID = 2586132525686378231L;
	private String code;
	
	public Internal(String code) {
		this.code = code;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Internal [code=" + code + "]";
	}
}
