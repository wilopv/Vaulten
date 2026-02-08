package com.wilove.vaulten.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EncryptionServiceTest {

    @Autowired
    private EncryptionService encryptionService;

    @Test
    void testEncryptDecryptSuccess() {
        String originalText = "Sensitive Data 123";

        String encryptedText = encryptionService.encrypt(originalText);
        assertNotNull(encryptedText);
        assertNotEquals(originalText, encryptedText);

        String decryptedText = encryptionService.decrypt(encryptedText);
        assertEquals(originalText, decryptedText);
    }

    @Test
    void testDifferentIVsForSameText() {
        String text = "Same Text";

        String encrypted1 = encryptionService.encrypt(text);
        String encrypted2 = encryptionService.encrypt(text);

        assertNotEquals(encrypted1, encrypted2, "Encrypted values should be different due to random IV");

        assertEquals(text, encryptionService.decrypt(encrypted1));
        assertEquals(text, encryptionService.decrypt(encrypted2));
    }

    @Test
    void testDecryptWithInvalidData() {
        assertThrows(RuntimeException.class, () -> {
            encryptionService.decrypt("InvalidBase64Data");
        });
    }

    @Test
    void testEncryptNull() {
        assertNull(encryptionService.encrypt(null));
    }

    @Test
    void testDecryptNull() {
        assertNull(encryptionService.decrypt(null));
    }
}
