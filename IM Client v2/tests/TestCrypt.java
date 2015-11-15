package tests;

import crypt.KeyAlreadySetException;
import crypt.MessageCrypt;

import java.security.KeyException;

/**
 * Test encrypting and decrypting library.
 */
public class TestCrypt {

    public static void main(String[] args) {
        MessageCrypt m = MessageCrypt.getInstance();
        try {
            m.init("12345");
        } catch (KeyAlreadySetException e) {
            e.printStackTrace();
        }

        String message = "afsdasdfasdfdsfa!";

        try {
            String encryptedMessage = m.encrypt(message);

            String decryptedMessage = m.decrypt(encryptedMessage);

            System.out.println("Decrypted: " + decryptedMessage);
        } catch (KeyException e) {
            e.printStackTrace();
        }
    }
}
