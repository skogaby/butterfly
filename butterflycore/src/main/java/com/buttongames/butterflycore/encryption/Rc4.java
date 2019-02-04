package com.buttongames.butterflycore.encryption;

import com.buttongames.butterflycore.util.CollectionUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class to handle RC4 encryption for network packets.
 * Ported from deamuse (https://buck.ludd.ltu.se/yugge/deamuse) and easerver_standalone.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class Rc4 {

    /**
     * The "secret" portion of the RC4 key, 26 bytes that get appended to the 6 bytes we're
     * given in the X-Eamuse-Info header.
     */
    private static final String SECRET_KEY = "69D74627D985EE2187161570D08D93B12455035B6DF0D8205DF5";

    /**
     * Decrypts the given data using RC4.
     * @param data The data to decrypt
     * @param key The key to use for decryption
     * @return The decrypted data
     * @throws GeneralSecurityException
     */
    public static byte[] decrypt(final byte[] data, final String key) {
        try {
            final byte[] keyBytes = getKeyFromEamuseHeader(key);
            final SecretKey sk = new SecretKeySpec(keyBytes, 0, keyBytes.length, "RC4");
            final Cipher cipher = Cipher.getInstance("RC4");
            cipher.init(Cipher.DECRYPT_MODE, sk);
            return cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encrypts the given data using RC4.
     * @param data The data to encrypt
     * @param key The key to use for encryption
     * @return The encrypted data
     * @throws GeneralSecurityException
     */
    public static byte[] encrypt(final byte[] data, final String key) {
        return decrypt(data, key);
    }

    /**
     * Returns the RC4 key to use for crypto operations, given the string
     * that is passed into the request via the X-Eamuse-Info header.
     * @param headerValue The value in the X-Eamuse-Info header
     * @return The RC4 key to use for encryption or decryption
     * @throws NoSuchAlgorithmException
     */
    public static byte[] getKeyFromEamuseHeader(final String headerValue)
            throws NoSuchAlgorithmException {
        final String[] keyList = headerValue.split("-");
        final byte[] hashKey = CollectionUtils.hexStringToByteArray(
                keyList[1] + keyList[2] + SECRET_KEY);
        final MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(hashKey);
        return md.digest();
    }
}
