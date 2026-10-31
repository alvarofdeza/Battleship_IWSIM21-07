package es.upm.etsisi.fis.server.service;

import es.upm.etsisi.fis.server.dto.auth.*;
import es.upm.etsisi.fis.server.entity.UserEntity;
import es.upm.etsisi.fis.server.entity.UserRole;
import es.upm.etsisi.fis.server.exception.ResourceNotFoundException;
import es.upm.etsisi.fis.server.repository.UserRepository;
import es.upm.etsisi.fis.server.security.JwtService;
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
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    /**
     * Inicializa los usuarios de prueba en la base de datos si esta vacia.
     */
    @PostConstruct
    public void seedInitialUsers() {
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
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered.");
        }

        String otp = String.format("%06d", secureRandom.nextInt(1000000));

        UserEntity user = new UserEntity(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                UserRole.ROLE_USER
        );
        user.setEmailVerified(false);
        user.setVerificationOtp(otp);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        // Enviar codigo OTP militar a traves de la API de Resend
        emailService.sendVerificationCode(user.getEmail(), user.getUsername(), otp);

        UserDetails userDetails = new User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );

        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name(), false);
    }

    @Transactional
    public AuthResponse verifyEmail(VerifyEmailRequest request) {
        UserEntity user = userRepository.findByUsername(request.getIdentifier())
                .or(() -> userRepository.findByEmail(request.getIdentifier()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with identifier: " + request.getIdentifier()));

        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified.");
        }

        if (user.getOtpExpiresAt() == null || user.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification code has expired. Please request a new code.");
        }

        if (user.getVerificationOtp() == null || !user.getVerificationOtp().equals(request.getCode().trim())) {
            throw new IllegalArgumentException("Invalid verification code.");
        }

        user.setEmailVerified(true);
        user.setVerificationOtp(null);
        user.setOtpExpiresAt(null);
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
        UserEntity user = userRepository.findByUsername(request.getIdentifier())
                .or(() -> userRepository.findByEmail(request.getIdentifier()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with identifier: " + request.getIdentifier()));

        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified.");
        }

        String otp = String.format("%06d", secureRandom.nextInt(1000000));
        user.setVerificationOtp(otp);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        emailService.sendVerificationCode(user.getEmail(), user.getUsername(), otp);
    }

    public AuthResponse login(LoginRequest request) {
        // Permitir login con username o email
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .or(() -> userRepository.findByEmail(request.getUsername()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

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
