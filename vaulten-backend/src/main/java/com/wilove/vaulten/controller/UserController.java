package com.wilove.vaulten.controller;

import com.wilove.vaulten.model.ChangePasswordRequest;
import com.wilove.vaulten.model.User;
import com.wilove.vaulten.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User account management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final AuthService authService;

    @PostMapping("/change-password")
    @Operation(summary = "Change master password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        authService.changePassword(request.getCurrentPassword(), request.getNewPassword(), currentUser);
        return ResponseEntity.noContent().build();
    }
}
