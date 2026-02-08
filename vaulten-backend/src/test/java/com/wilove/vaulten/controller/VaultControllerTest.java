package com.wilove.vaulten.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilove.vaulten.model.User;
import com.wilove.vaulten.model.VaultEntry;
import com.wilove.vaulten.model.VaultEntryType;
import com.wilove.vaulten.security.JwtAuthenticationFilter;
import com.wilove.vaulten.service.AuthService;
import com.wilove.vaulten.service.JwtService;
import com.wilove.vaulten.service.VaultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VaultController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for controller testing
class VaultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VaultService vaultService;

    // Security dependencies required for context
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private JwtAuthenticationFilter jwtAuthFilter;
    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private VaultEntry testEntry;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testEntry = VaultEntry.builder()
                .id(1L)
                .name("Test Login")
                .username("user123")
                .password("plain_pass")
                .type(VaultEntryType.LOGIN)
                .build();
    }

    @Test
    void getAllEntries_ShouldReturnList() throws Exception {
        when(vaultService.getEntriesForUser(any())).thenReturn(List.of(testEntry));

        mockMvc.perform(get("/api/vault"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Login"));
    }

    @Test
    void createEntry_ShouldReturnCreated() throws Exception {
        when(vaultService.createEntry(any(VaultEntry.class), any())).thenReturn(testEntry);

        mockMvc.perform(post("/api/vault")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEntry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Login"));
    }

    @Test
    void getEntryById_ShouldReturnEntry() throws Exception {
        when(vaultService.getEntryById(eq(1L), any())).thenReturn(testEntry);

        mockMvc.perform(get("/api/vault/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Login"));
    }

    @Test
    void updateEntry_ShouldReturnUpdated() throws Exception {
        when(vaultService.updateEntry(eq(1L), any(VaultEntry.class), any())).thenReturn(testEntry);

        mockMvc.perform(put("/api/vault/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEntry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Login"));
    }

    @Test
    void deleteEntry_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/vault/1"))
                .andExpect(status().isNoContent());

        verify(vaultService).deleteEntry(eq(1L), any());
    }
}
