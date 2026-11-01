package es.upm.etsisi.fis.server.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Peticion DTO para solicitar el restablecimiento de contrasena mediante PIN de 6 digitos.
 */
public class ForgotPasswordRequest {

    @NotBlank(message = "Email or username is required")
    private String identifier;

    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
