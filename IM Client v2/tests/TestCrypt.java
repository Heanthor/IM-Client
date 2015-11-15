package tests;

import crypt.KeyAlreadySetException;
import crypt.MessageCrypt;

import java.security.KeyException;
import java.util.Random;

/**
 * Test encrypting and decrypting library.
 */
public class TestCrypt {

    public static void main(String[] args) {
        MessageCrypt m = MessageCrypt.getInstance();

        Random r = new Random(12);
        byte[] salt = new byte[32];
        r.nextBytes(salt);

        try {
            m.init("Secret Key");
        } catch (KeyAlreadySetException e) {
            e.printStackTrace();
        }

        String message = "\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?\"";

        try {
            String encryptedMessage = m.encrypt(message);

            String decryptedMessage = m.decrypt(encryptedMessage);

            System.out.println("Decrypted: " + decryptedMessage);
        } catch (KeyException e) {
            e.printStackTrace();
        }
    }
}
