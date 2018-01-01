package tests;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;

public class ReproCase {
    private static final String seed = "47336799122";
    private static final String badString = "OLJnGOJv0ZHTx045O8U3H8jsYy";

    public static void main(String[] args) {
        SecretKey key = getSecretKey();

        try {
            // encrypt
            final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedMessage = cipher.doFinal(badString.getBytes());

            String encrypted = new BigInteger(encryptedMessage).toString(16);

            System.out.println(encrypted); // -1fc1ae4eee2b4801f021c8a2c573d2d145a89700bd2555b4d05c9248d72078
            System.out.println(encrypted.length()); // 63

            // decrypt
            cipher.init(Cipher.DECRYPT_MODE, key);

            // throws IllegalBlockSizeException
            String decrypted = new String(cipher.doFinal(new BigInteger(encrypted, 16).toByteArray()));
        } catch (NoSuchAlgorithmException |
                InvalidKeyException |
                BadPaddingException |
                NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            System.out.println("Block size exception");
        }
    }

    private static SecretKey getSecretKey() {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            Random r = new Random(128);
            byte[] salt = new byte[32];
            r.nextBytes(salt);
            KeySpec spec = new PBEKeySpec(seed.toCharArray(), salt, 65536, 128);
            SecretKey tmp = factory.generateSecret(spec);

            return new SecretKeySpec(tmp.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();

            return null;
        }
    }
}
