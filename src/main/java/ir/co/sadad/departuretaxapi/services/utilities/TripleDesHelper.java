package ir.co.sadad.departuretaxapi.services.utilities;

import javax.crypto.Cipher;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Security;

public class TripleDesHelper {
    public static int MAX_KEY_LENGTH = DESedeKeySpec.DES_EDE_KEY_LEN;
    private static String ENCRYPTION_KEY_TYPE = "DESede";
    private static String ENCRYPTION_ALGORITHM = "DESede/ECB/PKCS5Padding";
    private final SecretKeySpec keySpec;

    public TripleDesHelper(byte[] keyBytes) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        keySpec = new SecretKeySpec(keyBytes, "DESede");

    }

    public byte[] GetKeyAsBytes(String key) {
        byte[] keyBytes = new byte[24]; // a Triple DES key is a byte[24] array

        for (int i = 0; i < key.length() && i < keyBytes.length; i++)
            keyBytes[i] = (byte) key.charAt(i);

        return keyBytes;
    }

    private byte[] padKeyToLength(byte[] key, int len) {
        byte[] newKey = new byte[len];
        System.arraycopy(key, 0, newKey, 0, Math.min(key.length, len));
        return newKey;
    }

    public byte[] encrypt(String message, Charset charset) throws GeneralSecurityException, UnsupportedEncodingException {
        byte[] unencrypted = message.getBytes(charset);
        byte[] result = doCipher(unencrypted, Cipher.ENCRYPT_MODE);
        return result;
    }

    public byte[] decrypt(byte[] encrypted) throws GeneralSecurityException {
        return doCipher(encrypted, Cipher.DECRYPT_MODE);
    }

    private byte[] doCipher(byte[] original, int mode)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(mode, keySpec); //, iv);
        return cipher.doFinal(original);
    }

    // Takes a 7-byte quantity and returns a valid 8-byte DES key.
// The input and output bytes are big-endian, where the most significant
// byte is in element 0.
    public static byte[] addParity(byte[] in) {
        byte[] result = new byte[8];

        // Keeps track of the bit position in the result
        int resultIx = 1;

        // Used to keep track of the number of 1 bits in each 7-bit chunk
        int bitCount = 0;

        // Process each of the 56 bits
        for (int i = 0; i < 56; i++) {
            // Get the bit at bit position i
            boolean bit = (in[6 - i / 8] & (1 << (i % 8))) > 0;

            // If set, set the corresponding bit in the result
            if (bit) {
                result[7 - resultIx / 8] |= (1 << (resultIx % 8)) & 0xFF;
                bitCount++;
            }

            // Set the parity bit after every 7 bits
            if ((i + 1) % 7 == 0) {
                if (bitCount % 2 == 0) {
                    // Set low-order bit (parity bit) if bit count is even
                    result[7 - resultIx / 8] |= 1;
                }
                resultIx++;
                bitCount = 0;
            }
            resultIx++;
        }
        return result;
    }

}

