package tests;

import crypt.KeyAlreadySetException;
import crypt.MessageCrypt;

import javax.crypto.IllegalBlockSizeException;
import java.security.KeyException;
import java.util.Locale;
import java.util.Random;

/**
 * Test encrypting and decrypting library.
 */
public class TestCrypt {
    private static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String lower = upper.toLowerCase(Locale.ROOT);

    private static final String digits = "0123456789";

    private static final String alphanum = upper + lower + digits;

    private static final String key = "47336799122";

    private static final String badString = "OLJnGOJv0ZHTx045O8U3H8jsYy";


    public static void main(String[] args) {
        //testBasicFunctionality();
        findBadString();
//        testSpecificBadString(badString);
//        testSpecificBadString("hello?");
    }

    private static void testBasicFunctionality() {
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
            byte[] encryptedMessage = m.encrypt(message);

            String decryptedMessage = m.decrypt(encryptedMessage);

            System.out.println("Decrypted: " + decryptedMessage);
        } catch (KeyException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    private static void findBadString() {
        MessageCrypt m = MessageCrypt.getInstance();

        try {
            m.init(key);
        } catch (KeyAlreadySetException e) {
            e.printStackTrace();
        }

        int iterations = 10_000_000;
        int maxLength = 40;

        for (int i = 0; i < iterations; i++) {
            StringBuilder sb = new StringBuilder();
            Random r = new Random();
            int length = r.nextInt(maxLength - 1) + 1;

            for (int j = 0; j < length; j++) {
                int maxChar = alphanum.length();
                Random r2 = new Random();

                sb.append(alphanum.charAt(r2.nextInt(maxChar)));
            }

            String toTest = sb.toString();
            try {
                byte[] encryptedMessage = m.encrypt(toTest);
                System.out.println("Testing '" + toTest + "'");


                String decryptedMessage = m.decrypt(encryptedMessage);

                if (!toTest.equals(decryptedMessage)) {
                    System.out.println("String '" + decryptedMessage + "' not equal to '" + toTest + "'");
                    return;
                }

            } catch (javax.crypto.IllegalBlockSizeException e) {
                e.printStackTrace();
                System.out.println("Found bad string: " + toTest);
                return;
            } catch (KeyException e) {
                e.printStackTrace();
            }
        }

        System.out.println("No error found");
    }

    private static void testSpecificBadString(String s) {
        MessageCrypt m = MessageCrypt.getInstance();

        try {
            m.init(key);
        } catch (KeyAlreadySetException e) {
            e.printStackTrace();
        }

        try {
            byte[] encryptedMessage = m.encrypt(s);

            String decryptedMessage = m.decrypt(encryptedMessage);

        } catch (IllegalBlockSizeException e) {
            System.err.println("String triggered IllegalBlockSizeException");
        } catch (KeyException e) {
            e.printStackTrace();
        }
    }
}
