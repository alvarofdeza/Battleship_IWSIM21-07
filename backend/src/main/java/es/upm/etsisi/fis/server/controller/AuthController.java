package es.upm.etsisi.fis.server.controller;

import es.upm.etsisi.fis.server.dto.auth.*;
import es.upm.etsisi.fis.server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para el registro, inicio de sesion y perfil del usuario autenticado.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication endpoints with JWT tokens, email verification, and account credentials")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new player account and returns an initial JWT authentication token.")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Sign in", description = "Validates credentials (username or email) and issues a Bearer JWT access token.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email with OTP code", description = "Validates the 6-digit military PIN and activates high score recording eligibility.")
    public ResponseEntity<AuthResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        AuthResponse response = authService.verifyEmail(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-code")
    @Operation(summary = "Resend verification OTP", description = "Generates and dispatches a new 6-digit PIN to the registered email.")
    public ResponseEntity<Void> resendCode(@Valid @RequestBody ResendOtpRequest request) {
        authService.resendVerificationCode(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset PIN", description = "Dispatches a 6-digit PIN to the user email if the account exists, returning a generic status message.")
    public ResponseEntity<java.util.Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(java.util.Map.of("message", "If an account exists with the provided identifier, a 6-digit reset PIN has been dispatched."));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with PIN", description = "Validates the 6-digit PIN and configures a new password for the officer account.")
    public ResponseEntity<java.util.Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(java.util.Map.of("message", "Password has been successfully reset. Please sign in with your new credentials."));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns authenticated officer profile details decoded from Bearer JWT.")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserProfileResponse profile = authService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/password")
    @Operation(summary = "Change password", description = "Updates authenticated user password after verifying current credentials.")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.noContent().build();
    }
}
