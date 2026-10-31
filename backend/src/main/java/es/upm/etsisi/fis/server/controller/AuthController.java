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
@Tag(name = "Authentication", description = "Operaciones de autenticacion con JWT y registro de cuentas de usuario")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea una nueva cuenta de jugador y devuelve el token JWT inicial.")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion", description = "Valida las credenciales (username o email) y emite un token JWT de acceso.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verificar correo con codigo OTP", description = "Valida el PIN militar de 6 digitos y activa el registro de records en el escalafon.")
    public ResponseEntity<AuthResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        AuthResponse response = authService.verifyEmail(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-code")
    @Operation(summary = "Reenviar codigo OTP", description = "Genera y envia un nuevo PIN de 6 digitos al correo del usuario.")
    public ResponseEntity<Void> resendCode(@Valid @RequestBody ResendOtpRequest request) {
        authService.resendVerificationCode(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Consultar perfil actual", description = "Devuelve los datos del usuario autenticado a partir del token JWT Bearer.")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserProfileResponse profile = authService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/password")
    @Operation(summary = "Cambiar contrasena", description = "Actualiza la contrasena del usuario autenticado tras validar la actual.")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.noContent().build();
    }
}
