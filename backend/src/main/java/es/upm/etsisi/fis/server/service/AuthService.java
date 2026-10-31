package es.upm.etsisi.fis.server.service;

import es.upm.etsisi.fis.server.dto.auth.*;
import es.upm.etsisi.fis.server.entity.UserEntity;
import es.upm.etsisi.fis.server.entity.UserRole;
import es.upm.etsisi.fis.server.exception.ResourceNotFoundException;
import es.upm.etsisi.fis.server.repository.UserRepository;
import es.upm.etsisi.fis.server.security.JwtService;
import es.upm.etsisi.fis.server.util.UsernamePolicyValidator;
import jakarta.annotation.PostConstruct;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * Servicio de negocio para la gestion de registro, verificacion OTP por correo, inicio de sesion y perfiles de usuario.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final org.springframework.core.env.Environment environment;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager,
                       EmailService emailService, org.springframework.core.env.Environment environment) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.environment = environment;
    }

    /**
     * Inicializa los usuarios de prueba en la base de datos si esta vacia.
     * En el perfil de produccion (Neon) se desactiva para mantener las tablas completamente vacias.
     */
    @PostConstruct
    public void seedInitialUsers() {
        if (java.util.Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            return;
        }
        if (userRepository.count() == 0) {
            registerUserInternal("admin", "admin@upm.es", "admin123", UserRole.ROLE_ADMIN);
            registerUserInternal("alvaro", "alvaro@alumnos.upm.es", "password123", UserRole.ROLE_USER);
            registerUserInternal("adrian", "adrian@alumnos.upm.es", "password123", UserRole.ROLE_USER);
        }
    }

    private void registerUserInternal(String username, String email, String rawPassword, UserRole role) {
        UserEntity user = new UserEntity(
                username,
                email,
                passwordEncoder.encode(rawPassword),
                role
        );
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String cleanUsername = request.getUsername().trim();
        String cleanEmail = request.getEmail().trim().toLowerCase();

        // Validar politica de nombres de usuario reservados y prohibidos
        UsernamePolicyValidator.validate(cleanUsername);

        if (userRepository.existsByUsername(cleanUsername)) {
            throw new IllegalArgumentException("Username '" + cleanUsername + "' is already taken.");
        }
        if (userRepository.existsByEmail(cleanEmail)) {
            throw new IllegalArgumentException("Email '" + cleanEmail + "' is already registered.");
        }

        String otp = String.format("%06d", secureRandom.nextInt(1000000));

        UserEntity user = new UserEntity(
                cleanUsername,
                cleanEmail,
                passwordEncoder.encode(request.getPassword()),
                UserRole.ROLE_USER
        );
        user.setEmailVerified(false);
        user.setVerificationOtp(otp);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(10));
        user.setOtpAttempts(0);
        userRepository.save(user);

        // Enviar codigo OTP militar a traves del servicio de correo
        emailService.sendVerificationCode(user.getEmail(), user.getUsername(), otp);

        UserDetails userDetails = new User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );

        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name(), false);
    }

    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public AuthResponse verifyEmail(VerifyEmailRequest request) {
        String identifier = request.getIdentifier().trim();
        String identifierLower = identifier.toLowerCase();

        UserEntity user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifierLower))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with identifier: " + identifier));

        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified.");
        }

        if (user.getOtpExpiresAt() == null || user.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification code has expired. Please request a new code.");
        }

        if (user.getVerificationOtp() == null || !user.getVerificationOtp().equals(request.getCode().trim())) {
            int newAttempts = user.getOtpAttempts() + 1;
            user.setOtpAttempts(newAttempts);
            if (newAttempts >= 5) {
                user.setVerificationOtp(null);
                user.setOtpExpiresAt(null);
                userRepository.save(user);
                throw new IllegalArgumentException("Maximum verification attempts exceeded. Please request a new code.");
            }
            userRepository.save(user);
            throw new IllegalArgumentException("Invalid verification code. Attempts remaining: " + (5 - newAttempts));
        }

        user.setEmailVerified(true);
        user.setVerificationOtp(null);
        user.setOtpExpiresAt(null);
        user.setOtpAttempts(0);
        userRepository.save(user);

        UserDetails userDetails = new User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );

        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name(), true);
    }

    @Transactional
    public void resendVerificationCode(ResendOtpRequest request) {
        String identifier = request.getIdentifier().trim();
        String identifierLower = identifier.toLowerCase();

        UserEntity user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifierLower))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with identifier: " + identifier));

        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified.");
        }

        String otp = String.format("%06d", secureRandom.nextInt(1000000));
        user.setVerificationOtp(otp);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(10));
        user.setOtpAttempts(0);
        userRepository.save(user);

        emailService.sendVerificationCode(user.getEmail(), user.getUsername(), otp);
    }

    public AuthResponse login(LoginRequest request) {
        // Autenticacion exclusiva mediante correo electronico normalizado
        String cleanEmail = request.getEmail().trim().toLowerCase();
        UserEntity user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
        );

        UserDetails userDetails = new User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );

        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name(), user.isEmailVerified());
    }

    public UserProfileResponse getProfile(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.isEmailVerified(),
                user.getCreatedAt()
        );
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password does not match.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
