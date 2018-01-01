package messages;

import crypt.MessageCrypt;

import javax.crypto.IllegalBlockSizeException;
import java.security.KeyException;

/**
 * External messages are packets containing actual messages to be shown to users.
 * Messages have a 10,000 character limit.
 * @author Reed
 */
public class ExternalMessage extends Message {
	private static final long serialVersionUID = 1338880583237113936L;
	private String message;

	/**
	 * Create a new message to be sent to a recipient.
	 * Handles encryption of message with 128-bit AES encryption scheme.
	 * Message will not be set if a key error occurs.
	 * @param sender Sender of message
	 * @param recipient Recipient of message
	 * @param externalMessage Message to send
	 */
	public ExternalMessage(String sender, String recipient, String externalMessage) {
		super(sender, recipient);
		
		//Add length restriction
		if (externalMessage.length() > 10000) {
			externalMessage = externalMessage.substring(0, 10000) + "...";
		}
		try {
			this.message = encryptMessage(externalMessage);
		} catch (KeyException e) {
			this.message = null;
			e.printStackTrace();
		}
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Retrieves the stored message, decrypting it first using the already-initialized MessageCrypt class.
	 * @return The decrypted message
	 * @throws KeyException If they key is not initialized, or another problem occurs
	 */
	public String getDecryptedMessage() throws KeyException, IllegalBlockSizeException {
		MessageCrypt m = MessageCrypt.getInstance();

		return m.decrypt(message);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExternalMessage [message=" + message + "]";
	}

	private String encryptMessage(String message) throws KeyException {
		MessageCrypt m = MessageCrypt.getInstance();

		return m.encrypt(message);
	}
}
