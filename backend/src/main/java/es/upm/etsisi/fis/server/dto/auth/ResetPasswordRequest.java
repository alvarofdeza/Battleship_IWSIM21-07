package es.upm.etsisi.fis.server.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Peticion DTO para restablecer la contrasena utilizando el codigo PIN militar recibido por correo.
 */
public class ResetPasswordRequest {

    @NotBlank(message = "Username or email is required")
    private String identifier;

    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Verification code must be exactly 6 digits")
    private String code;

    @NotBlank(message = "New password cannot be blank")
    @Size(min = 6, max = 50, message = "New password must be between 6 and 50 characters")
    private String newPassword;

    public ResetPasswordRequest() {}

    public ResetPasswordRequest(String identifier, String code, String newPassword) {
        this.identifier = identifier;
        this.code = code;
        this.newPassword = newPassword;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
