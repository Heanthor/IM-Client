package crypt;

/**
 * Exception to be thrown if the secret key of the MessageCrypt instance is set more than once.
 * @author Reed
 */
public class KeyAlreadySetException extends Exception {
    public KeyAlreadySetException() {}

    public KeyAlreadySetException(String arg0) {
        super(arg0);
    }

    public KeyAlreadySetException(Throwable arg0) {
        super(arg0);
    }

    public KeyAlreadySetException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public KeyAlreadySetException(String arg0, Throwable arg1, boolean arg2,
                                  boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }
}
