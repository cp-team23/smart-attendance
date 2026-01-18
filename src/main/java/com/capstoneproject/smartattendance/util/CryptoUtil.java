package com.capstoneproject.smartattendance.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class CryptoUtil {

    private static final String IV = "1234567890123456";

    private static SecretKeySpec getKey(String key) throws Exception {
        byte[] keyBytes = MessageDigest.getInstance("SHA-256")
                .digest(key.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, 0, 16, "AES"); // 128-bit
    }

    public static String encrypt(String data, String key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    getKey(key),
                    new IvParameterSpec(IV.getBytes())
            );

            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));

        } catch (Exception e) {
            return null;
        }
    }
    
    public static String decrypt(String encryptedCode, String key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    getKey(key),
                    new IvParameterSpec(IV.getBytes())
            );

            return new String(
                    cipher.doFinal(Base64.getDecoder().decode(encryptedCode)),
                    StandardCharsets.UTF_8
            );

        } catch (Exception e) {
            return null;
        }
    }
}
