package com.wilove.vaulten.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VaultEntryTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidVaultEntry() {
        User user = new User();
        VaultEntry entry = VaultEntry.builder()
                .name("My Bank")
                .type(VaultEntryType.LOGIN)
                .user(user)
                .build();

        Set<ConstraintViolation<VaultEntry>> violations = validator.validate(entry);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidVaultEntry_BlankName() {
        User user = new User();
        VaultEntry entry = VaultEntry.builder()
                .name("")
                .type(VaultEntryType.LOGIN)
                .user(user)
                .build();

        Set<ConstraintViolation<VaultEntry>> violations = validator.validate(entry);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testInvalidVaultEntry_NullType() {
        User user = new User();
        VaultEntry entry = VaultEntry.builder()
                .name("My Bank")
                .type(null)
                .user(user)
                .build();

        Set<ConstraintViolation<VaultEntry>> violations = validator.validate(entry);
        assertFalse(violations.isEmpty());
    }
}
