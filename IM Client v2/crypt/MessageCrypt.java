package crypt;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;

/**
 * Class responsible for encrypting an decrypting all messages being sent between clients.
 * Key can only be set once per run of the program.
 * Uses 128 bit AES encryption for all messages.
 * @author Reed
 */
public class MessageCrypt {

    private static MessageCrypt instance = new MessageCrypt();

    private SecretKey key;

    private MessageCrypt() {}

    /**
     * Encrypts string using AES encryption, and the supplied secret key.
     * @param message The message to be encrypted
     * @return The encrypted message as a byte array.
     * @throws KeyException if the key has not been set yet.
     * @throws UnsupportedOperationException if an error occurs in encrypting.
     */
    public byte[] encrypt(final String message) throws KeyException {
        if (key == null) {
            throw new KeyException("Secret key not set");
        }

        try {
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return cipher.doFinal(message.getBytes());
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException |
                InvalidKeyException |
                BadPaddingException |
                IllegalBlockSizeException e) {
            e.printStackTrace();
           throw new UnsupportedOperationException();
        }
    }

    /**
     * Decrypts a hexadecimal string encoded message, using the provided secret key.
     * The secret key must match the one used in encrypting the message.
     * @param hexEncodedMessage The message to decode
     * @return The decrypted message.
     * @throws KeyException if the key has not been set yet.
     * @throws UnsupportedOperationException if an error occurs in decrypting.
     */
    public String decrypt(final byte[] hexEncodedMessage) throws KeyException, IllegalBlockSizeException {
        if (key == null) {
            throw new KeyException("Secret key not set");
        }

        try {
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);

            return new String(cipher.doFinal(hexEncodedMessage));
        } catch (NoSuchAlgorithmException |
                InvalidKeyException |
                BadPaddingException |
                NoSuchPaddingException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Get singleton instance of MessageCrypt. Provide the secret key to be used in encryption and decryption.
     * @return The singleton instance
     */
    public static MessageCrypt getInstance() {
        return instance;
    }

    /**
     * Sets the key to be used in encrypting and decrypting all messages.
     * @param keyIn - Key string to use
     * @throws KeyAlreadySetException - If the key has already been set this runtime.
     * @throws UnsupportedOperationException If a problem is encountered processing the key
     */
    public void init(String keyIn) throws KeyAlreadySetException {
        if (key != null) {
            throw new KeyAlreadySetException();
        } else if (keyIn == null) {
            throw new NullPointerException();
        } else {
            try {
                System.out.println("Key string: " + keyIn);
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                //Same salt for every client
                Random r = new Random(128);
                byte[] salt = new byte[32];
                r.nextBytes(salt);
                KeySpec spec = new PBEKeySpec(keyIn.toCharArray(), salt, 65536, 128);
                SecretKey tmp = factory.generateSecret(spec);

                key = new SecretKeySpec(tmp.getEncoded(), "AES");
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();

                throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * @return True if secret key has already been initialized, false otherwise.
     */
    public boolean isInitialized() {
        return key != null;
    }
}
