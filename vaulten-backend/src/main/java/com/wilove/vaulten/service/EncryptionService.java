package com.wilove.vaulten.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service for server-side encryption/decryption using AES-256 GCM.
 */
@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int AES_KEY_BIT = 256;

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public EncryptionService(@Value("${vault.encryption.key}") String base64Key) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        if (decodedKey.length != AES_KEY_BIT / 8) {
            throw new IllegalArgumentException("Invalid encryption key length. Expected 256 bits.");
        }
        this.secretKey = new SecretKeySpec(decodedKey, "AES");
    }

    /**
     * Encrypts plain text using AES-256 GCM.
     * Returns Base64 encoded string containing (IV + Authentication Tag +
     * Ciphertext).
     */
    public String encrypt(String plainText) {
        if (plainText == null)
            return null;

        try {
            byte[] iv = new byte[IV_LENGTH_BYTE];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new RuntimeException("Error during encryption", e);
        }
    }

    /**
     * Decrypts a Base64 encoded string (IV + Authentication Tag + Ciphertext).
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null)
            return null;

        try {
            byte[] decode = Base64.getDecoder().decode(encryptedText);

            ByteBuffer byteBuffer = ByteBuffer.wrap(decode);
            byte[] iv = new byte[IV_LENGTH_BYTE];
            byteBuffer.get(iv);

            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error during decryption", e);
        }
    }
}
